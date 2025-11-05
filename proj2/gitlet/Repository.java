package gitlet;

import java.io.File;

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
    /** Tell if the .gitlet has been built. */
    public static boolean isGitletSetUp() {
        return GITLET_DIR.exists();
    }
    /** Init a new .gitlet dictionary in pwd.
     *  If .gitlet exists, it will do nothing.
     */
    public static void setUpPersistence() {
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
        writeObject(join(COMMITS_DIR, origin.getId()), origin);
        writeContents(HEAD, "master");
        File master = join(BRANCHES_DIR, "master");
        writeContents(master, origin.getId());
    }
    /** Get the current commit or other words the HEAD pointer. */
    public static Commit getCurrCommit() {
        String currString = readContentsAsString(HEAD);
        File target = join(BRANCHES_DIR, currString);
        if (!target.exists()) {
            return readObject(join(COMMITS_DIR, currString), Commit.class);
        } else {
            return readObject(join(COMMITS_DIR, readContentsAsString(target)), Commit.class);
        }
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
    public static String getCurrBranch() {
        if (!join(BRANCHES_DIR, readContentsAsString(HEAD)).exists()) {
            return null;
        }
        return readContentsAsString(HEAD);
    }

}
