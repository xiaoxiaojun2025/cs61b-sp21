package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * 存储对所有可能使用到的子目录及文件的引用，实现很多对文件的IO方法
 * 便于其他类直接对文件操作。实现对对象（COMMIT,BLOB）的管理，可以
 * 根据需求读写对象。实现对分支指针，头指针的管理
 *
 * @author ChenJinzhao
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The dictionary contains different blobs refer to files.
     * Each file's name is sha1 and content is byte array.
     */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /**
     * The staging area dictionary.
     */
    public static final File STAGED_AREA = join(GITLET_DIR, "stagedArea");
    /**
     * The area storing the blobs from command "add".
     */
    public static final File ADDITION = join(STAGED_AREA, "addition");
    /**
     * The area storing the blobs from command "rm".
     */
    public static final File REMOVAL = join(STAGED_AREA, "removal");
    /**
     * The dictionary contains commits.
     * Each file's name is sha1 and content is byte array.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /**
     * The HEAD pointer points to the current branch, here, it's a file
     * with its content the name of branch.
     */
    public static final File HEAD = join(GITLET_DIR, "head");
    /**
     * The branches dictionary, a branch always points to the front of the commit.
     */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    /**
     * The dictionary saves the paths of other remote dictionaries.
     */
    public static final File REMOTE_DIR = join(GITLET_DIR, "remotes");
    /**
     * Shortened length of sha-1 to be the dictionary of files of commits.
     */
    public static final int SHORTENED_LENGTH = 2;
    /**
     * Default branch name.
     */
    public static final String DEFAULT_BRANCH = "master";

    /**
     * Tell if the .gitlet has been built.
     */
    public static boolean isGitletSetUp() {
        return GITLET_DIR.exists();
    }

    /**
     * Init a new .gitlet dictionary in pwd.
     * If .gitlet exists, it will do nothing.
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
        REMOTE_DIR.mkdir();
        Commit origin = new Commit();
        String originId = origin.getId();
        saveObject(COMMITS_DIR, originId, origin);
        moveHead(DEFAULT_BRANCH);
        switchAddBranch(DEFAULT_BRANCH, originId);
    }

    /**
     * Get the current commit or other words the HEAD pointer.
     */
    static Commit getCurrCommit() {
        String currString = readContentsAsString(HEAD);
        File target = join(BRANCHES_DIR, currString);
        if (!target.exists()) {
            return getObjectByID(COMMITS_DIR, currString, Commit.class);
        } else {
            return getObjectByID(COMMITS_DIR,
                    readContentsAsString(join(BRANCHES_DIR, currString)), Commit.class);
        }
    }

    /**
     * Check if a dictionary is empty.
     */
    static boolean isDictionaryEmpty(File target) {
        if (target == null || !target.exists() || !target.isDirectory()) {
            return true;
        }
        String[] files = target.list();
        return files == null || files.length == 0;
    }

    /**
     * Get a commit or blob by ID, which can be a shortened one but larger than SHORTENED_LENGTH.
     */
    static <T extends Serializable> T getObjectByID(File dic, String objectID, Class<T> cls) {
        if (objectID == null || objectID.length() < SHORTENED_LENGTH
                || objectID.length() > UID_LENGTH) {
            return null;
        }
        String dirString = objectID.substring(0, SHORTENED_LENGTH);
        String restString = objectID.substring(SHORTENED_LENGTH);
        File firstDir = join(dic, dirString);
        if (!firstDir.exists() || !firstDir.isDirectory()) {
            return null;
        }
        for (File file : firstDir.listFiles()) {
            if (file.getName().startsWith(restString)) {
                return readObject(file, cls);
            }
        }
        return null;
    }

    /**
     * Get a commit by branch name.
     */
    static Commit getCommitByBranch(String branch) {
        String commitID = readContentsAsString(join(Repository.BRANCHES_DIR, branch));
        return getObjectByID(Repository.COMMITS_DIR, commitID, Commit.class);
    }

    /**
     * Save a commit or blob to its dictionary.
     */
    static <T extends Serializable> void saveObject(File dic, String objectID, T object) {
        String dicString = objectID.substring(0, SHORTENED_LENGTH);
        String restString = objectID.substring(SHORTENED_LENGTH);
        File firstDic = join(dic, dicString);
        if (!firstDic.exists()) {
            firstDic.mkdir();
        }
        writeObject(join(firstDic, restString), object);
    }

    /**
     * Move the HEAD pointer.
     */
    static void moveHead(String newHead) {
        writeContents(HEAD, newHead);
    }

    /**
     * Remove everything from the stagedArea.
     */
    static void clearStagedArea() {
        for (File file : Repository.ADDITION.listFiles()) {
            file.delete();
        }
        for (File file : Repository.REMOVAL.listFiles()) {
            file.delete();
        }
    }

    /**
     * Get current branch.
     * And return null if head is detched, which will not happen normally.
     */
    static String getCurrBranch() {
        if (!join(BRANCHES_DIR, readContentsAsString(HEAD)).exists()) {
            return null;
        }
        return readContentsAsString(HEAD);
    }

    /**
     * Add a new branch or switch the current branch.
     */
    static void switchAddBranch(String newBranch, String commitID) {
        writeContents(join(BRANCHES_DIR, newBranch), commitID);
    }

    /**
     * Check if a file is not in currCommit's list of filenames.
     * Return true if it's not in it.
     */
    static boolean isFileUntrackedInCommit(String filename) {
        Commit currCommit = Repository.getCurrCommit();
        return !currCommit.containFilename(filename);
    }

    /**
     * Check if a file is neither in currCommit's list of filenames
     * nor in ADDITION area, which is considered "Untracked File".
     * Return true if it's "Untracked File".
     */
    static boolean isFileUntracked(String filename) {
        return join(Repository.CWD, filename).exists()
                && isFileUntrackedInCommit(filename) && !join(ADDITION, filename).exists();
    }

    /**
     * Find the latest common ancestor of current branch and the given branch.
     */
    static Commit findLatestCommonAncestor(Commit currCommit, Commit otherBranch) {
        Map<String, Integer> map = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        String currID = currCommit.getId();
        String otherID = otherBranch.getId();
        queue.add(currID);
        queue.add(otherID);
        map.put(currID, 1);
        map.put(otherID, 2);
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            int currentSource = map.get(currentId);
            Commit current = getObjectByID(Repository.COMMITS_DIR, currentId, Commit.class);
            for (String parentID : current.getParents()) {
                if (parentID == null) {
                    continue;
                }
                if (!map.containsKey(parentID)) {
                    map.put(parentID, currentSource);
                    queue.add(parentID);
                } else if (map.get(parentID) != currentSource) {
                    return getObjectByID(Repository.COMMITS_DIR, parentID, Commit.class);
                }
            }
        }
        return null;
    }

}
