package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;
/** The main class processing command line commands. */
public class Commands {

    /** Add exactly one file to staged area of ADDITION.
     *  the ADDITION stores the filenames. */
    public static void add(String filename) {
        if (!Repository.isGitletSetUp()) {Main.printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());}
        File target = Repository.ADDITION;
        File FileContent = join(Repository.CWD, filename);
        if (!FileContent.exists()) {Main.printError(ErrorMessage.NON_EXISTING_FILE.getMessage());}
        /* Create a new Blob. */
        Blob newBlob = new Blob(filename, readContents(FileContent));
        String newId = newBlob.getId();
        /* To be added in BLOBS_DIR. */
        File blobFile = join(Repository.BLOBS_DIR, newId);
        writeObject(blobFile, newBlob);
        /* To be added in stagedArea. */
        File newFile = join(target, filename);
        if (Repository.getCurrCommit().containBlob(newBlob)) {
            if (newFile.exists()) {
                newFile.delete();
            }
            return;
        }
        writeContents(newFile, newId);
    }

    /** Create a new commit from the copy of HEAD, then check StagedArea
     *  to overwrite, add or remove files to it ,then set it as the new commit.
     */
    public static void commit(String message) throws IOException {
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
            Blob newBlob = readObject(join(Repository.BLOBS_DIR, readContentsAsString(toBeAdded)), Blob.class);
            newCommit.overwriteAdd(newBlob);
        }
        for (File toBeRemoved: Repository.REMOVAL.listFiles()) {
            newCommit.removeFile(toBeRemoved.getName());
        }
        String newId = newCommit.getId();
        /* Add it to the COMMITS_DIR, and update the pointers. */
        File newFile = new File(Repository.COMMITS_DIR, newId);
        writeObject(newFile, newCommit);
        Repository.moveHead(newId);
        Repository.clearStagedArea();
        /* Branch have not been delt yet. */
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
    public static void remove(String filename) throws IOException {
        for (File file: Repository.ADDITION.listFiles()) {
            if (file.getName().equals(filename)) {
                file.delete();
                return;
            }
        }
        /* Add to the REMOVAL if the file is tracked. */
        Commit currCommit = Repository.getCurrCommit();
        if (currCommit.containFilename(filename)) {
            File toBeRemoved = join(Repository.REMOVAL, filename);
            toBeRemoved.createNewFile();
            join(Repository.CWD, filename).delete();
            return;
        }
        Main.printError(ErrorMessage.NO_NEED_TO_RM.getMessage());
    }
    /** Print all commits from the HEAD follows parent1 to original commit. */
    public static void log() {
        Commit currCommit = Repository.getCurrCommit();
        while (true) {
            Commit.printCommit(currCommit);
            if (currCommit.getParent1() == null) {break;}
            File next = join(Repository.COMMITS_DIR, currCommit.getParent1());
            if (!next.exists()) {break;}
            currCommit = readObject(next, Commit.class);
        }
    }
    /** Print all commits ever made with no specific order. */
    public static void global_log() {
        for (String id:plainFilenamesIn(Repository.COMMITS_DIR)) {
            File file = join(Repository.COMMITS_DIR, id);
            Commit currCommit = readObject(file, Commit.class);
            Commit.printCommit(currCommit);
        }
    }

}
