package gitlet;

import java.io.File;
import java.io.IOException;

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
    /** The HEAD pointer points to the current commit, here, it's a file
     *  with its content the sha-1 of the commit.
     */
    public static final File HEAD = join(COMMITS_DIR, "head");
    /** The branches dictory, a branch always points to the front of the commit. */
    public static final File BRANCHES_DIR = join(COMMITS_DIR, "branches");
    /** Tell if the .gitlet has been built. */
    public static boolean isGitletSetUp() {
        return GITLET_DIR.exists();
    }
    /** Init a new .gitlet dictionary in pwd.
     *  If .gitlet exists, it will do nothing.
     */
    public static void setUpPersistence() throws IOException {
        if (isGitletSetUp()) {
            Main.printError(ErrorMessage.GITLET_ALREADY_EXISTS.getMessage());
        }
        GITLET_DIR.mkdir();
        STAGED_AREA.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        HEAD.createNewFile();
        BRANCHES_DIR.mkdir();
        ADDITION.mkdir();
        REMOVAL.mkdir();
        Commit origin = new Commit();
        writeObject(join(COMMITS_DIR, origin.getId()), origin);
        writeContents(HEAD, origin.getId());
        File master = join(BRANCHES_DIR, "master");
        master.createNewFile();
        writeContents(master, origin.getId());
    }
    /** Get the current commit or other words the HEAD pointer. */
    public static Commit getCurrCommit() {
        String currString = readContentsAsString(Repository.HEAD);
        File target = join(Repository.COMMITS_DIR, currString);
        return readObject(target, Commit.class);
    }
    /** Move the HEAD pointer. */
    static void moveHead(String newId) {
        writeContents(HEAD, newId);
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
}
