package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author ChenJinzhao
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The dictionary contains different blobs refer to files.
     *  Each file's name is sha1 and content is byte array. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /** The staging area dictionary. */
    public static final File STAGED_AREA = join(GITLET_DIR, "stagedArea");
    /** The area storing the blobs from command "add". */
    public static final File ADDITION = join(STAGED_AREA, "addition");
    /** The area storing the blobs from command "rm". */
    public static final File REMOVAL = join(STAGED_AREA, "removal");
    /** The dictionary contains commits.
     *  Each file's name is sha1 and content is byte array. */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** The HEAD pointer points to the current branch, here, it's a file
     *  with its content the name of branch.
     */
    public static final File HEAD = join(GITLET_DIR, "head");
    /** The branches dictory, a branch always points to the front of the commit. */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    /** Shortened length of sha-1 to be the dictionary of files of commits. */
    public static final int SHORTENED_LENGTH = 2;
    /** Default branch name. */
    public static final String DEFAULT_BRANCH = "master";
    /** Tell if the .gitlet has been built. */
    public static boolean isGitletSetUp() {
        return GITLET_DIR.exists();
    }
    /** Init a new .gitlet dictionary in pwd.
     *  If .gitlet exists, it will do nothing.
     */
    static void setUpPersistence() {
        if (isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_ALREADY_EXISTS.getMessage());
        }
        GITLET_DIR.mkdir();
        STAGED_AREA.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        ADDITION.mkdir();
        REMOVAL.mkdir();
        Commit origin = new Commit();
        String originId = origin.getId();
        saveObject(COMMITS_DIR, originId, origin);
        moveHead(DEFAULT_BRANCH);
        SwitchAddBranch(DEFAULT_BRANCH, originId);
    }
    /** Get the current commit or other words the HEAD pointer. */
    static Commit getCurrCommit() {
        String currString = readContentsAsString(HEAD);
        File target = join(BRANCHES_DIR, currString);
        if (!target.exists()) {
            return getObjectByID(COMMITS_DIR, currString, Commit.class);
        } else {
            return getObjectByID(COMMITS_DIR, readContentsAsString(join(BRANCHES_DIR, currString)), Commit.class);
        }
    }
    /** Get a commit or blob by ID, which can be a shortened one but larger than SHORTENED_LENGTH. */
    static <T extends Serializable> T getObjectByID(File dic, String ID, Class<T> cls) {
        if (ID == null || ID.length() < SHORTENED_LENGTH || ID.length() > UID_LENGTH) {
            return null;
        }
        String dirString = ID.substring(0, SHORTENED_LENGTH);
        String restString = ID.substring(SHORTENED_LENGTH);
        File firstDir = join(dic, dirString);
        if (!firstDir.exists() || !firstDir.isDirectory()) {
            return null;
        }
        for (File file: firstDir.listFiles()) {
            if (file.getName().startsWith(restString)) {
                return readObject(file, cls);
            }
        }
        return null;
    }
    /** Save a commit or blob to its dictionary. */
    static <T extends Serializable> void saveObject(File dic, String ID, T object) {
        String dicString = ID.substring(0, SHORTENED_LENGTH);
        String restString = ID.substring(SHORTENED_LENGTH);
        File firstDic = join(dic, dicString);
        if (!firstDic.exists()) {firstDic.mkdir();}
        writeObject(join(firstDic, restString), object);
    }
    /** Move the HEAD pointer. */
    static void moveHead(String newHead) {
        writeContents(HEAD, newHead);
    }
    /** Remove everything from the stagedArea. */
    static void clearStagedArea() {
        for (File file: Repository.ADDITION.listFiles()) {
            file.delete();
        }
        for (File file: Repository.REMOVAL.listFiles()) {
            file.delete();
        }
    }
    /** Get current branch.
     *  And return null if head is detched, which will not happen normally. */
    static String getCurrBranch() {
        if (!join(BRANCHES_DIR, readContentsAsString(HEAD)).exists()) {
            return null;
        }
        return readContentsAsString(HEAD);
    }
    /** Add a new branch or switch the current branch. */
    static void SwitchAddBranch(String newBranch, String ID) {
        writeContents(join(BRANCHES_DIR, newBranch), ID);
    }
    /** Check if a file is not in currCommit's list of filenames.
     *  Return true if it's not in it. */
    static boolean isFileUntrackedInCommit(String filename) {
        Commit currCommit = Repository.getCurrCommit();
        return !currCommit.containFilename(filename);
    }
    /** Check if a file is neither in currCommit's list of filenames
     *  nor in ADDITION area, which is considered "Untracked File".
     *  Return true if it's "Untracked File". */
    static boolean isFileUntracked(String filename){
        return join(Repository.CWD, filename).exists() &&
                isFileUntrackedInCommit(filename) &&
                !join(ADDITION, filename).exists();
    }
}
