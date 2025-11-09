package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static gitlet.Utils.*;

/**
 * The main class processing command line commands.
 * 该类实现gitlet中所有本地命令（不包括远程），只有方法
 * 当调用方法时，会涉及对.gitlet目录中文件IO操作，诸如添加新COMMIT,BLOB对象，
 * 对暂存区的增删等以实现可持续化目的。
 *
 * @author ChenJinzhao
 */
class Commands {

    /**
     * Add exactly one file to staged area of ADDITION.
     * the ADDITION stores the filenames.
     * Add the file to Blob, remove it from REMOVAL if it exists there.
     * 将文件加入暂存添加区，如果文件不存在于工作区则报错，移除暂存删除区该文件
     * 如果当前提交有该文件相同内容放弃加入暂存区，否则加入暂存区并保存该BLOB（如果它不存在）
     */
    static void add(String filename) {
        File fileContent = join(Repository.CWD, filename);
        if (!fileContent.exists()) {
            Main.printError(ErrorMessage.NON_EXISTING_FILE.getMessage());
        }
        /* Create a new Blob. */
        Blob newBlob = new Blob(filename, readContents(fileContent));
        String newId = newBlob.getId();
        /* Remove it from REMOVAL */
        join(Repository.REMOVAL, filename).delete();
        /* To be added in stagedArea. */
        File newFile = join(Repository.ADDITION, filename);
        if (Repository.getCurrCommit().containBlob(newBlob)) {
            newFile.delete();
            return;
        }
        writeContents(newFile, newId);
        /* To be added in BLOBS_DIR. */
        Repository.saveObject(Repository.BLOBS_DIR, newId, newBlob);
    }

    /**
     * Create a new commit from the copy of HEAD, then check StagedArea
     * to overwrite, add or remove files to it ,then set it as the new commit.
     * 非合并提交，因此只有一个父提交，提交时先复制父提交（时间戳和日志除外），
     * 然后根据暂存区增删改文件，父ID是其父提交的ID。
     * 提交后移动分支指针，清空暂存区。
     */
    static void commit(String message) {
        commit(message, null);
    }

    /**
     * Commit for merge.
     * 合并提交，有两个父提交。
     */
    static void commit(String message, String secondParent) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        File addition = Repository.ADDITION;
        File removal = Repository.REMOVAL;
        if (Repository.isDictionaryEmpty(addition) && Repository.isDictionaryEmpty(removal)) {
            Main.printError(ErrorMessage.NON_FILES_STAGED.getMessage());
        }
        Commit parent = Repository.getCurrCommit();
        Commit newCommit = new Commit(message, parent, secondParent);
        /* Overwrite or add files from stagedArea to commit. */
        for (File toBeAdded : Repository.ADDITION.listFiles()) {
            Blob newBlob = Repository.getObjectByID(Repository.BLOBS_DIR,
                    readContentsAsString(toBeAdded), Blob.class);
            newCommit.overwriteAdd(newBlob);
        }
        for (File toBeRemoved : Repository.REMOVAL.listFiles()) {
            newCommit.removeFile(toBeRemoved.getName());
        }
        String newId = newCommit.getId();
        /* Add it to the COMMIT_DIR. */
        Repository.saveObject(Repository.COMMITS_DIR, newId, newCommit);
        /* Deal with the branch and head. */
        String currBranch = Repository.getCurrBranch();
        /* 正常状态 */
        if (currBranch != null) {
            Repository.switchAddBranch(currBranch, newId);
            /* 分离头状态（该项目不会出现）*/
        } else {
            Repository.moveHead(newId);
        }
        Repository.clearStagedArea();


    }

    /**
     * Remove the file from the working dictionary and
     * stage it to the REMOVAL.
     * 如果暂存添加区存在该文件，将其移除,如果文件被当前提交跟踪，
     * 将其加入暂存删除区并从工作目录删除（如果用户没有删除）
     * 如果是未跟踪文件（既不在暂存区也不被提交跟踪）会报错。
     */
    static void remove(String filename) {
        boolean untrackedOrStaged = false;
        for (File file : Repository.ADDITION.listFiles()) {
            if (file.getName().equals(filename)) {
                file.delete();
                untrackedOrStaged = true;
            }
        }
        /* Add to the REMOVAL if the file is tracked. */
        if (!Repository.isFileUntrackedInCommit(filename)) {
            writeContents(join(Repository.REMOVAL, filename));
            /* Delete the file. */
            restrictedDelete(join(Repository.CWD, filename));
            untrackedOrStaged = true;
        }
        if (!untrackedOrStaged) {
            Main.printError(ErrorMessage.NO_NEED_TO_RM.getMessage());
        }
    }

    /**
     * Print all commits from the HEAD follows parent1 to original commit.
     * 从当前头提交开始，沿着父提交（第一父提交）依次打印该提交的信息知道最初的提交。
     */
    static void log() {
        Commit currCommit = Repository.getCurrCommit();
        while (currCommit != null) {
            Commit.printCommit(currCommit);
            currCommit = Repository.getObjectByID(Repository.COMMITS_DIR,
                    currCommit.getParent1(), Commit.class);
        }
    }

    /**
     * Print all commits ever made with no specific order.
     * 打印目前为止拥有的所有提交，无确定顺序。
     */
    static void globalLog() {
        for (File firstDic : Repository.COMMITS_DIR.listFiles()) {
            for (File file : firstDic.listFiles()) {
                Commit currCommit = readObject(file, Commit.class);
                Commit.printCommit(currCommit);
            }
        }
    }

    /**
     * Print commits with specific commit message, maybe contain multiple commits.
     * 依据提交的日志消息查找提交ID，可能有多个提交拥有相同日志消息并查找出多个提交。
     */
    static void find(String commitMessage) {
        boolean found = false;
        for (File firstDic : Repository.COMMITS_DIR.listFiles()) {
            for (File target : firstDic.listFiles()) {
                Commit currCommit = readObject(target, Commit.class);
                if (commitMessage.equals(currCommit.getMessage())) {
                    System.out.println(currCommit.getId());
                    found = true;
                }
            }
        }
        if (!found) {
            Main.printError(ErrorMessage.NON_EXISTING_COMMIT.getMessage());
        }
    }

    /**
     * Print all branches, the current branch, stagedArea files,
     * modifications but not staged for commit files, untracked files.
     * 获取当前状态，这包括现有分支和当前分支；暂存区状态，修改但未暂存文件以及未跟踪文件。
     */
    static void status() {
        /* Print branches. */
        System.out.println("=== Branches ===");
        String currBranch = Repository.getCurrBranch();
        for (String branch : plainFilenamesIn(Repository.BRANCHES_DIR)) {
            if (currBranch.equals(branch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        /* Print stagedArea. */
        System.out.println("=== Staged Files ===");
        for (String stagedFile : plainFilenamesIn(Repository.ADDITION)) {
            System.out.println(stagedFile);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String removeFile : plainFilenamesIn(Repository.REMOVAL)) {
            System.out.println(removeFile);
        }
        System.out.println();
        /* Print others. */
        statusEC();
    }

    /**
     * Help deal with untracked files and modified but not staged files.
     */
    private static void statusEC() {
        Set<String> untrackedFiles = new TreeSet<>();
        Set<String> modifiedButNotStagedFiles = new TreeSet<>();
        Commit currCommit = Repository.getCurrCommit();
        /* modified */
        for (String filename : plainFilenamesIn(Repository.CWD)) {
            if (Repository.isFileUntracked(filename)) {
                untrackedFiles.add(filename);
            }
            File cwdFile = join(Repository.CWD, filename);
            String cwdFileID = new Blob(cwdFile).getId();
            File addFile = join(Repository.ADDITION, filename);
            if (addFile.exists()) {
                if (!cwdFileID.equals(readContentsAsString(addFile))) {
                    modifiedButNotStagedFiles.add(filename + " (modified)");
                }
            } else {
                if (currCommit.containFilename(filename)
                        && !currCommit.getBlobByFileName(filename).equals(cwdFileID)) {
                    modifiedButNotStagedFiles.add(filename + " (modified)");
                }
            }
        }
        /* deleted */
        for (String filename : plainFilenamesIn(Repository.ADDITION)) {
            if (!join(Repository.CWD, filename).exists()) {
                modifiedButNotStagedFiles.add(filename + " (deleted)");
            }
        }
        if (currCommit.getBlobs() != null) {
            for (String filename : currCommit.getBlobs().keySet()) {
                if (!join(Repository.REMOVAL, filename).exists() && !join(Repository.CWD,
                        filename).exists()) {
                    modifiedButNotStagedFiles.add(filename + " (deleted)");
                }
            }
        }
        /* Print */
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String string : modifiedButNotStagedFiles) {
            System.out.println(string);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String string : untrackedFiles) {
            System.out.println(string);
        }
        System.out.println();
    }

    /**
     * Change one file to the given commit.
     * No influence to the stagedArea.
     * 将单个文件改成指定提交时的版本，若工作目录不存在会创建该文件，文件在指定提交不存在时报错。
     */
    static void checkout(String commitID, String filename) {
        Commit targetCommit = Repository.getObjectByID(Repository.COMMITS_DIR, commitID,
                Commit.class);
        if (targetCommit == null) {
            Main.printError(ErrorMessage.NON_EXISTING_COMMIT_WITH_ID.getMessage());
        }
        /* Get file ID. */
        String fileID = targetCommit.getBlobByFileName(filename);
        if (fileID == null) {
            Main.printError(ErrorMessage.FILE_NOT_IN_COMMIT.getMessage());
        }
        /* Get target blob. */
        Blob newBlob = Repository.getObjectByID(Repository.BLOBS_DIR, fileID, Blob.class);
        /* Change the file. */
        writeContents(join(Repository.CWD, newBlob.getFilename()), newBlob.getContent());
    }

    /**
     * Default checkout, or to say the head commit.
     * 将单个文件改成当前提交的版本。
     */
    static void checkout(String filename) {
        checkout(Repository.getCurrCommit().getId(), filename);
    }

    /**
     * Change all files in the CWD to the branch version.
     * 将工作区所有文件改成指定分支头的版本，不会被覆写的未跟踪文件不会受影响，若会被覆写则报错。
     * 完成后当前分支切换为指定分支，并清空暂存区。
     */
    static void checkoutToBranch(String checkoutBranch) {
        if (!join(Repository.BRANCHES_DIR, checkoutBranch).exists()) {
            Main.printError(ErrorMessage.NON_EXISTING_BRANCH.getMessage());
        }
        String currBranch = Repository.getCurrBranch();
        if (currBranch.equals(checkoutBranch)) {
            Main.printError(ErrorMessage.ALREADY_CURRENT_BRANCH.getMessage());
        }
        String checkoutID = readContentsAsString(join(Repository.BRANCHES_DIR, checkoutBranch));
        checkoutToCommit(checkoutID);
        /* Change HEAD. */
        Repository.moveHead(checkoutBranch);
    }

    /**
     * Change one file to the given branch.
     */
    private static void checkoutOneFileToBranch(String checkoutBranch, String filename) {
        String targetID = readContentsAsString(join(Repository.BRANCHES_DIR, checkoutBranch));
        checkout(targetID, filename);
    }

    /**
     * Checkout to specific commit.
     * Remember there will never be in a detached head state,
     * but this method won't move pointer directly, should be used with other methods.
     * 将工作区所有文件切换至指定提交的版本。
     */
    private static void checkoutToCommit(String commitID) {
        /* Get commits. */
        Commit currCommit = Repository.getObjectByID(Repository.COMMITS_DIR,
                Repository.getCurrCommit().getId(), Commit.class);
        Commit checkoutCommit = Repository.getObjectByID(Repository.COMMITS_DIR, commitID,
                Commit.class);
        if (checkoutCommit == null) {
            Main.printError(ErrorMessage.NON_EXISTING_COMMIT_WITH_ID.getMessage());
        }
        /* Check if there are untracked files. */
        for (String filename : plainFilenamesIn(Repository.CWD)) {
            if (Repository.isFileUntracked(filename) && checkoutCommit.containFilename(filename)) {
                Main.printError(ErrorMessage.UNTRACKED_FILE_EXISTS.getMessage());
            }
        }
        /* Delete files tracked in curr branch, not in checkout branch. */
        for (String filename : plainFilenamesIn(Repository.CWD)) {
            if (!checkoutCommit.containFilename(filename) && currCommit.containFilename(filename)) {
                restrictedDelete(join(Repository.CWD, filename));
            }
        }
        /* Overwrite or add files in checkout branch. */
        for (String blobID : checkoutCommit.getBlobs().values()) {
            Blob newBlob = Repository.getObjectByID(Repository.BLOBS_DIR, blobID, Blob.class);
            newBlob.backIntoFile();
        }
        /* Clear the stagedArea. */
        Repository.clearStagedArea();
    }

    /**
     * Create a new branch and do not move head.
     * 创建一个新的分支，不会切换到该分支。
     */
    static void createBranch(String newBranch) {
        if (join(Repository.BRANCHES_DIR, newBranch).exists()) {
            Main.printError(ErrorMessage.ALREADY_EXISTING_BRANCH.getMessage());
        }
        writeContents(join(Repository.BRANCHES_DIR, newBranch),
                readContentsAsString(join(Repository.BRANCHES_DIR, Repository.getCurrBranch())));
    }

    /**
     * Remove a branch.
     * 删除一个已有分支如果它存在的话，不能删除当前分支。
     */
    static void removeBranch(String branchName) {
        if (!join(Repository.BRANCHES_DIR, branchName).exists()) {
            Main.printError(ErrorMessage.NON_EXISTING_BRANCH_WITH_NAME.getMessage());
        }
        if (branchName.equals(Repository.getCurrBranch())) {
            Main.printError(ErrorMessage.CURRENT_BRANCH_REMOVE_ABORTED.getMessage());
        }
        join(Repository.BRANCHES_DIR, branchName).delete();
    }

    /**
     * Change the version to a specific commit. What's important is that
     * this method will move the branch and head together.
     * 将工作区所有文件改成指定提交的版本，并将当前分支头移至该提交，
     * 这也意味着中间的提交被放弃（仍可以通过ID查找）。
     */
    static void reset(String commitID) {
        checkoutToCommit(commitID);
        Repository.switchAddBranch(Repository.getCurrBranch(), commitID);
    }

    /**
     * Merge given branch with current branch.
     * 将给定分支与当前分支合并，以下简称当前分支为当前，给定分支为给定，所有文件遵循以下规则：
     * 1.根据当前和给定分支获取最近共同祖先（或分裂点、拆分点等）
     * 2.未跟踪文件若在给定分支存在且与分裂点内容不同将被覆写，这是不被允许的，会报错，
     * 否则未跟踪文件可以保留。如果暂存区不为空（即可提交而未提交），也会报错
     * 3.对其余文件，若给定分支存在且发生修改而当前未修改（都是相对于分裂点），切换到给定分支，并加入暂存。
     * 4.如果当前未修改文件，在给定分支不存在，会执行rm操作
     * 5.仅存在于给定分支的，会在工作区创建该文件并加入暂存
     * 6.当前与给定分支以不同方式修改文件，这包括在拆分点不存在，当前与给定皆存在而内容不同；
     * 在拆分点存在而当前和给定有一个不存在而另一个修改，或都存在内容不一样。
     * 这被称为冲突，会将当前和给定以及冲突标识都写入文件并在终端打印冲突提示
     * 7.其余情况都应什么都不做，保持当前状态。
     *
     */
    static void merge(String checkoutBranch) {
        if (!Repository.isDictionaryEmpty(Repository.ADDITION)
                || !Repository.isDictionaryEmpty(Repository.REMOVAL)) {
            Main.printError(ErrorMessage.CHANGES_UNCOMMITED.getMessage());
        }
        if (!join(Repository.BRANCHES_DIR, checkoutBranch).exists()) {
            Main.printError(ErrorMessage.BRANCH_WITH_NAME_NOT_EXISTING.getMessage());
        }
        String currBranch = Repository.getCurrBranch();
        if (checkoutBranch.equals(currBranch)) {
            Main.printError(ErrorMessage.CANNOT_MERGE_ITSELF.getMessage());
        }
        Commit checkoutCommit = Repository.getCommitByBranch(checkoutBranch);
        Commit currCommit = Repository.getCurrCommit();
        Commit splitPoint = Repository.findLatestCommonAncestor(currCommit, checkoutCommit);
        for (String file : plainFilenamesIn(Repository.CWD)) {
            if (Repository.isFileUntracked(file)
                    && checkoutCommit.containFilename(file)
                    && !checkoutCommit.getBlobByFileName(file).
                            equals(splitPoint.getBlobByFileName(file))) {
                Main.printError(ErrorMessage.UNTRACKED_FILE_EXISTS.getMessage());
            }
        }
        String currID = currCommit.getId();
        String checkoutID = checkoutCommit.getId();
        String splitID = splitPoint.getId();
        if (splitID.equals(checkoutID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (splitID.equals(currID)) {
            checkoutToCommit(checkoutID);
            writeContents(join(Repository.BRANCHES_DIR, currBranch), checkoutID);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        Set<String> allfiles = new HashSet<>(currCommit.getBlobs().keySet());
        allfiles.addAll(checkoutCommit.getBlobs().keySet());
        allfiles.addAll(splitPoint.getBlobs().keySet());
        boolean ifConflictHappens = false;
        for (String file : allfiles) {
            String currFileID = currCommit.getBlobByFileName(file);
            String checkoutFileID = checkoutCommit.getBlobByFileName(file);
            String splitFileID = splitPoint.getBlobByFileName(file);
            if (splitFileID != null) {
                /* 给定分支存在且修改，当前分支未修改 → 检出给定分支版本并stage*/
                if (checkoutFileID != null && !splitFileID.equals(checkoutFileID)
                        && splitFileID.equals(currFileID)) {
                    checkoutOneFileToBranch(checkoutBranch, file);
                    add(file);
                    /* 检出点存在，当前未改，给定不存在， 应删除并暂存 */
                } else if (checkoutFileID == null && splitFileID.equals(currFileID)) {
                    remove(file);
                    /* 以不同方式修改 */
                } else if ((!splitFileID.equals(currFileID)
                        && !splitFileID.equals(checkoutFileID))
                        && ((currFileID != null && !currFileID.equals(checkoutFileID))
                        || (checkoutFileID != null && !checkoutFileID.equals(currFileID)))) {
                    dealWithConflict(currFileID, checkoutFileID, file);
                    ifConflictHappens = true;
                }
                /* 以相同方式修改，都删除，当前修改给定未修改, 不会有任何改变。*/
            } else {
                /* 任何不存在于拆分点且仅存在于给定分支中的文件都应检出并暂存。*/
                if (currFileID == null && checkoutFileID != null) {
                    checkout(checkoutID, file);
                    add(file);
                    /* 冲突, 以不同方式修改。 */
                } else if (currFileID != null && checkoutFileID != null
                        && !currFileID.equals(checkoutFileID)) {
                    dealWithConflict(currFileID, checkoutFileID, file);
                    ifConflictHappens = true;
                }
                /* 拆分点不存在且仅存在于当前分支中的任何文件都应保持原样。
                 *  或者二者都不存在或以相同方式修改, 不做出更改。*/
            }
        }
        if (ifConflictHappens) {
            System.out.println(ErrorMessage.MERGE_CONFLICT.getMessage());
        }
        commit(String.format("Merged %s into %s.", checkoutBranch, currBranch), checkoutID);
    }

    /**
     * Help deal with merge conflicts.
     * */
    private static void dealWithConflict(String currFileID, String checkoutFileID, String file) {
        byte[] currentContent = currFileID == null ? new byte[0]
                : Repository.getObjectByID(Repository.BLOBS_DIR, currFileID,
                Blob.class).getContent();
        byte[] checkoutContent = checkoutFileID == null ? new byte[0]
                : Repository.getObjectByID(Repository.BLOBS_DIR, checkoutFileID,
                Blob.class).getContent();
        writeContents(join(Repository.CWD, file),
                "<<<<<<< HEAD\n", currentContent,
                "=======\n", checkoutContent, ">>>>>>>\n");
        add(file);
    }
}
