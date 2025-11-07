package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;
/** The main class processing command line commands. */
class Commands {

    /** Add exactly one file to staged area of ADDITION.
     *  the ADDITION stores the filenames.
     *  Add the file to Blob, remove it from REMOVAL if it exists there.*/
    static void add(String filename) {
        if (!Repository.isGitletSetUp()) {Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());}
        File FileContent = join(Repository.CWD, filename);
        if (!FileContent.exists()) {Main.printError(ErrorMessage.NON_EXISTING_FILE.getMessage());}
        /* Create a new Blob. */
        Blob newBlob = new Blob(filename, readContents(FileContent));
        String newId = newBlob.getId();
        /* To be added in BLOBS_DIR. */
        Repository.saveObject(Repository.BLOBS_DIR, newId, newBlob);
        /* Remove it from REMOVAL */
        join(Repository.REMOVAL, filename).delete();
        /* To be added in stagedArea. */
        File newFile = join(Repository.ADDITION, filename);
        if (Repository.getCurrCommit().containBlob(newBlob)) {
            newFile.delete();
            return;
        }
        writeContents(newFile, newId);
    }

    /** Create a new commit from the copy of HEAD, then check StagedArea
     *  to overwrite, add or remove files to it ,then set it as the new commit.
     */
    static void commit(String message) {
        commit(message, null);
    }
    /** Commit for merge. */
    static void commit(String message, String secondParent) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        if (!Repository.isGitletSetUp()) {Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());}
        File addition = Repository.ADDITION;
        File removal = Repository.REMOVAL;
        if (Repository.isDictionaryEmpty(addition) && Repository.isDictionaryEmpty(removal)) {
            Main.printError(ErrorMessage.NON_FILES_STAGED.getMessage());
        }
        Commit parent = Repository.getCurrCommit();
        Commit newCommit = new Commit(message, parent, secondParent);
        /* Overwrite or add files from stagedArea to commit. */
        for (File toBeAdded: Repository.ADDITION.listFiles()) {
            Blob newBlob = Repository.getObjectByID(Repository.BLOBS_DIR, readContentsAsString(toBeAdded), Blob.class);
            newCommit.overwriteAdd(newBlob);
        }
        for (File toBeRemoved: Repository.REMOVAL.listFiles()) {
            newCommit.removeFile(toBeRemoved.getName());
        }
        String newId = newCommit.getId();
        /* Add it to the COMMIT_DIR. */
        Repository.saveObject(Repository.COMMITS_DIR, newId, newCommit);
        /* Deal with the branch and head. */
        String currBranch = Repository.getCurrBranch();
        /* 正常状态 */
        if (currBranch != null) {
            Repository.SwitchAddBranch(currBranch, newId);
            /* 分离头状态（一般不会出现）*/
        } else{
            Repository.moveHead(newId);
        }
        Repository.clearStagedArea();


    }
    /** Remove the file from the working dictionary and
     *  stage it to the REMOVAL.
     */
    static void remove(String filename) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        for (File file: Repository.ADDITION.listFiles()) {
            if (file.getName().equals(filename)) {
                file.delete();
                return;
            }
        }
        /* Add to the REMOVAL if the file is tracked. */
        if (!Repository.isFileUntrackedInCommit(filename)) {
            writeContents(join(Repository.REMOVAL, filename));
            /* Delete the file. */
            restrictedDelete(join(Repository.CWD, filename));
            return;
        }
        Main.printError(ErrorMessage.NO_NEED_TO_RM.getMessage());
    }
    /** Print all commits from the HEAD follows parent1 to original commit. */
    static void log() {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        Commit currCommit = Repository.getCurrCommit();
        while (currCommit != null) {
            Commit.printCommit(currCommit);
            currCommit = Repository.getObjectByID(Repository.COMMITS_DIR, currCommit.getParent1(), Commit.class);
        }
    }
    /** Print all commits ever made with no specific order. */
    static void global_log() {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        for (File firstDic: Repository.COMMITS_DIR.listFiles()) {
            for (File file: firstDic.listFiles()) {
                Commit currCommit = readObject(file, Commit.class);
                Commit.printCommit(currCommit);
            }
        }
    }
    /** Print commits with specific commit message, maybe contain multiple commits. */
    static void find(String commitMessage) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        boolean found = false;
        for (File firstDic: Repository.COMMITS_DIR.listFiles()) {
            for (File target: firstDic.listFiles()) {
                Commit currCommit = readObject(target, Commit.class);
                if (commitMessage.equals(currCommit.getMessage())) {
                    System.out.println(currCommit.getId());
                    found = true;
                }
            }
        }
        if (!found) {Main.printError(ErrorMessage.NON_EXISTING_COMMIT.getMessage());}
    }
    /** Print all branches, the current branch, stagedArea files,
     * modifications but not staged for commit files, untracked files.  */
    static void status() {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        /* Print branches. */
        System.out.println("=== Branches ===");
        String currBranch = Repository.getCurrBranch();
        for (String branch: plainFilenamesIn(Repository.BRANCHES_DIR)) {
            if (currBranch.equals(branch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        /* Print stagedArea. */
        System.out.println("=== Staged Files ===");
        for (String stagedFile: plainFilenamesIn(Repository.ADDITION)) {
            System.out.println(stagedFile);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String removeFile: plainFilenamesIn(Repository.REMOVAL)) {
            System.out.println(removeFile);
        }
        System.out.println();
        /* Print others. */
        statusEC();
    }
    /** Help deal with untracked files and modified but not staged files. */
    private static void statusEC() {
        Set<String> untrackedFiles = new TreeSet<>();
        Set<String> modifiedButNotStagedFiles = new TreeSet<>();
        Commit currCommit = Repository.getCurrCommit();
        /* modified */
        for (String filename: plainFilenamesIn(Repository.CWD)) {
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
                if (currCommit.containFilename(filename) && !currCommit.getBlobByFileName(filename).equals(cwdFileID)) {
                    modifiedButNotStagedFiles.add(filename + " (modified)");
                }
            }
        }
        /* deleted */
        for (String filename: plainFilenamesIn(Repository.ADDITION)) {
            if (!join(Repository.CWD, filename).exists()) {
                modifiedButNotStagedFiles.add(filename + " (deleted)");
            }
        }
        if (currCommit.getBlobs() != null) {
            for (String filename: currCommit.getBlobs().keySet()) {
                if (!join(Repository.REMOVAL, filename).exists() && !join(Repository.CWD, filename).exists()) {
                    modifiedButNotStagedFiles.add(filename + " (deleted)");
                }
            }
        }
        /* Print */
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String string: modifiedButNotStagedFiles) {
            System.out.println(string);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String string: untrackedFiles) {
            System.out.println(string);
        }
        System.out.println();
    }
    /** Change one file to the given commit.
     *  No influence to the stagedArea. */
    static void checkout(String ID, String filename) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        Commit targetCommit = Repository.getObjectByID(Repository.COMMITS_DIR, ID, Commit.class);
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
    /** Default checkout, or to say the head commit. */
    static void checkout(String filename) {
        checkout(Repository.getCurrCommit().getId(), filename);
    }
    /** Change all files in the CWD to the branch version. */
    static void checkoutToBranch(String checkoutBranch) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
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
    /** Change one file to the given branch. */
    private static void checkoutOneFileToBranch(String checkoutBranch, String filename) {
        String targetID = readContentsAsString(join(Repository.BRANCHES_DIR, checkoutBranch));
        checkout(targetID, filename);
    }
    /** Checkout to specific commit.
     *  Remember there will never be in a detached head state,
     *  but this method won't move pointer directly, should be used with other methods.  */
    private static void checkoutToCommit(String ID) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        /* Get commits. */
        Commit currCommit = Repository.getObjectByID(Repository.COMMITS_DIR,
                Repository.getCurrCommit().getId(), Commit.class);
        Commit checkoutCommit = Repository.getObjectByID(Repository.COMMITS_DIR,
                ID, Commit.class);
        if (checkoutCommit == null) {
            Main.printError(ErrorMessage.NON_EXISTING_COMMIT_WITH_ID.getMessage());
        }
        /* Check if there are untracked files. */
        for (String filename: plainFilenamesIn(Repository.CWD)) {
            if (Repository.isFileUntracked(filename) && checkoutCommit.containFilename(filename)) {
                Main.printError(ErrorMessage.UNTRACKED_FILE_EXISTS.getMessage());
            }
        }
        /* Delete files tracked in curr branch, not in checkout branch. */
        for (String filename: plainFilenamesIn(Repository.CWD)) {
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
    /** Create a new branch and do not move head. */
    static void createBranch(String newBranch) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        if (join(Repository.BRANCHES_DIR, newBranch).exists()) {
            Main.printError(ErrorMessage.ALREADY_EXISTING_BRANCH.getMessage());
        }
        writeContents(join(Repository.BRANCHES_DIR, newBranch),
                readContentsAsString(join(Repository.BRANCHES_DIR, Repository.getCurrBranch())));
    }
    /** Remove a branch. */
    static void removeBranch(String branchName) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        if (!join(Repository.BRANCHES_DIR, branchName).exists()) {
            Main.printError(ErrorMessage.NON_EXISTING_BRANCH_WITH_NAME.getMessage());
        }
        if (branchName.equals(Repository.getCurrBranch())) {
            Main.printError(ErrorMessage.CURRENT_BRANCH_REMOVE_ABORTED.getMessage());
        }
        join(Repository.BRANCHES_DIR, branchName).delete();
    }
    /** Change the version to a specific commit. What's important is that
     *  this method will move the branch and head together. */
    static void reset(String ID) {
        checkoutToCommit(ID);
        Repository.SwitchAddBranch(Repository.getCurrBranch(), ID);
    }
    /** Merge not done yet. */
    static void merge(String checkoutBranch) {
        if (!Repository.isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
        }
        if (!Repository.isDictionaryEmpty(Repository.ADDITION) || !Repository.isDictionaryEmpty(Repository.REMOVAL)) {
            Main.printError(ErrorMessage.CHANGES_UNCOMMITED.getMessage());
        }
        if (!join(Repository.BRANCHES_DIR, checkoutBranch).exists()) {
            Main.printError(ErrorMessage.BRANCH_WITH_NAME_NOT_EXISTING.getMessage());
        }
        String currBranch = Repository.getCurrBranch();
        if (checkoutBranch.equals(currBranch)) {
            Main.printError(ErrorMessage.CANNOT_MERGE_ITSELF.getMessage());
        }
        for (String file: plainFilenamesIn(Repository.CWD)) {
            if (Repository.isFileUntracked(file)) {
                Main.printError(ErrorMessage.UNTRACKED_FILE_EXISTS.getMessage());
            }
        }
        Commit checkoutCommit = Repository.getCommitByBranch(checkoutBranch);
        Commit currCommit = Repository.getCurrCommit();
        Commit splitPoint = Repository.findLatestCommonAncestor(currCommit, checkoutCommit);
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
        for (String file: allfiles) {
            String currFileID = currCommit.getBlobByFileName(file);
            String checkoutFileID = checkoutCommit.getBlobByFileName(file);
            String splitFileID = splitPoint.getBlobByFileName(file);
            if (splitFileID != null) {
                /* 给定分支存在且修改，当前分支未修改 → 检出给定分支版本并stage*/
                if (checkoutFileID != null && !splitFileID.equals(checkoutFileID) && splitFileID.equals(currFileID)) {
                    checkoutOneFileToBranch(checkoutBranch, file);
                    add(file);
                /* 检出点存在，当前未改，给定不存在， 应删除并暂存 */
                } else if(checkoutFileID == null && splitFileID.equals(currFileID)) {
                    remove(file);
                /* 以不同方式修改 */
                } else if ((!splitFileID.equals(currFileID) && !splitFileID.equals(checkoutFileID)) &&
                        ((currFileID != null && !currFileID.equals(checkoutFileID)) ||
                        (checkoutFileID != null && !checkoutFileID.equals(currFileID)))){
                    byte[] currentContent = currFileID == null ? new byte[0]: Repository.getObjectByID(Repository.BLOBS_DIR, currFileID, Blob.class).getContent();
                    byte[] checkoutContent = checkoutFileID == null ? new byte[0]: Repository.getObjectByID(Repository.BLOBS_DIR, checkoutFileID, Blob.class).getContent();
                    writeContents(join(Repository.CWD, file), "<<<<<<< HEAD\n", currentContent, "=======\n", checkoutContent, ">>>>>>>\n");
                    add(file);
                    ifConflictHappens = true;
                }
                /* 以相同方式修改，都删除，当前修改给定未修改, 不会有任何改变。*/
            } else {
                /* 任何不存在于拆分点且仅存在于给定分支中的文件都应检出并暂存。*/
                if (currFileID == null && checkoutFileID != null) {
                    checkout(checkoutID, file);
                    add(file);
                /* 冲突, 以不同方式修改。 */
                } else if (currFileID != null && checkoutFileID != null && !currFileID.equals(checkoutFileID)){
                    byte[] currentContent = Repository.getObjectByID(Repository.BLOBS_DIR, currFileID, Blob.class).getContent();
                    byte[] checkoutContent = Repository.getObjectByID(Repository.BLOBS_DIR, checkoutFileID, Blob.class).getContent();
                    writeContents(join(Repository.CWD, file), "<<<<<<< HEAD\n", currentContent, "=======\n", checkoutContent, ">>>>>>>\n");
                    add(file);
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
}
