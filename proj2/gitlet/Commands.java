package gitlet;

import java.io.File;
import java.util.Map;

import static gitlet.Utils.*;
/** The main class processing command line commands. */
class Commands {

    /** Add exactly one file to staged area of ADDITION.
     *  the ADDITION stores the filenames. */
    static void add(String filename) {
        if (!Repository.isGitletSetUp()) {Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());}
        File FileContent = join(Repository.CWD, filename);
        if (!FileContent.exists()) {Main.printError(ErrorMessage.NON_EXISTING_FILE.getMessage());}
        /* Create a new Blob. */
        Blob newBlob = new Blob(filename, readContents(FileContent));
        String newId = newBlob.getId();
        /* To be added in BLOBS_DIR. */
        Repository.saveObject(Repository.BLOBS_DIR, newId, newBlob);
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
        if (!Repository.isGitletSetUp()) {Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());}
        File addition = Repository.ADDITION;
        File removal = Repository.REMOVAL;
        if (isDictionaryEmpty(addition) && isDictionaryEmpty(removal)) {
            Main.printError(ErrorMessage.NON_FILES_STAGED.getMessage());
        }
        Commit parent = Repository.getCurrCommit();
        Commit newCommit = new Commit(message, parent);
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
    /** Check if a dictionary is empty. */
    private static boolean isDictionaryEmpty(File target) {
        if (target == null || !target.exists() || !target.isDirectory()) {
            return true;
        }
        String[] files = target.list();
        return files == null || files.length == 0;
    }
    /** Remove the file from the working dictionary and
     *  stage it to the REMOVAL.
     */
    static void remove(String filename) {
        for (File file: Repository.ADDITION.listFiles()) {
            if (file.getName().equals(filename)) {
                file.delete();
                return;
            }
        }
        /* Add to the REMOVAL if the file is tracked. */
        Commit currCommit = Repository.getCurrCommit();
        if (currCommit.containFilename(filename)) {
            writeContents(join(Repository.REMOVAL, filename));
            /* Delete the file. */
            restrictedDelete(join(Repository.CWD, filename));
            return;
        }
        Main.printError(ErrorMessage.NO_NEED_TO_RM.getMessage());
    }
    /** Print all commits from the HEAD follows parent1 to original commit. */
    static void log() {
        Commit currCommit = Repository.getCurrCommit();
        while (currCommit != null) {
            Commit.printCommit(currCommit);
            currCommit = Repository.getObjectByID(Repository.COMMITS_DIR, currCommit.getParent1(), Commit.class);
        }
    }
    /** Print all commits ever made with no specific order. */
    static void global_log() {
        for (File firstDic: Repository.COMMITS_DIR.listFiles()) {
            for (File file: firstDic.listFiles()) {
                Commit currCommit = readObject(file, Commit.class);
                Commit.printCommit(currCommit);
            }
        }
    }
    /** Print commits with specific commit message, maybe contain multiple commits. */
    static void find(String commitMessage) {
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
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();

    }
    /** Change one file to the given commit.
     *  No influence to the stagedArea. */
    static void checkout(String ID, String filename) {
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
        if (!join(Repository.BRANCHES_DIR, checkoutBranch).exists()) {
            Main.printError(ErrorMessage.NON_EXISTING_BRANCH.getMessage());
        }
        String currBranch = Repository.getCurrBranch();
        if (currBranch.equals(checkoutBranch)) {
            Main.printError(ErrorMessage.ALREADY_CURRENT_BRANCH.getMessage());
        }
        /* Get commits. */
        Commit currCommit = Repository.getObjectByID(Repository.COMMITS_DIR,
                readContentsAsString(join(Repository.BRANCHES_DIR, currBranch)), Commit.class);
        Commit checkoutCommit = Repository.getObjectByID(Repository.COMMITS_DIR,
                readContentsAsString(join(Repository.BRANCHES_DIR, checkoutBranch)), Commit.class);
        /* Check if there are untracked files. */
        for (File file: Repository.CWD.listFiles()) {
            if (checkoutCommit.containFile(file) && !currCommit.containFile(file)) {
                Main.printError(ErrorMessage.UNTRACKED_FILE_EXISTS.getMessage());
            }
        }
        /* Delete files tracked in curr branch, not in checkout branch. */
        for (File file: Repository.CWD.listFiles()) {
            if (!checkoutCommit.containFile(file) && currCommit.containFile(file)) {
                restrictedDelete(file);
            }
        }
        /* Overwrite or add files in checkout branch. */
        for (String ID: checkoutCommit.getBlobs().values()) {
            Blob newBlob = Repository.getObjectByID(Repository.BLOBS_DIR, ID, Blob.class);
            newBlob.backIntoFile();
        }
        /* Clear the stagedArea. */
        Repository.clearStagedArea();
        /* Change HEAD. */
        Repository.moveHead(checkoutBranch);
    }
    /** Checkout to specific commit.
     *  Remember there will never be in a detached head state,
     *  so move the branch with head together. */
    static void checkoutToCommit(String ID) {

    }
    /** Create a new branch and do not move head. */
    static void createBranch(String newBranch) {
        if (join(Repository.BRANCHES_DIR, newBranch).exists()) {
            Main.printError(ErrorMessage.ALREADY_EXISTING_BRANCH.getMessage());
        }
        writeContents(join(Repository.BRANCHES_DIR, newBranch),
                readContentsAsString(join(Repository.BRANCHES_DIR, Repository.getCurrBranch())));
    }
    /** Remove a branch. */
    static void removeBranch(String branchName) {
        if (!join(Repository.BRANCHES_DIR, branchName).exists()) {
            Main.printError(ErrorMessage.NON_EXISTING_BRANCH_WITH_NAME.getMessage());
        }
        if (branchName.equals(Repository.getCurrBranch())) {
            Main.printError(ErrorMessage.CURRENT_BRANCH_REMOVE_ABORTED.getMessage());
        }
        join(Repository.BRANCHES_DIR, branchName).delete();
    }
}
