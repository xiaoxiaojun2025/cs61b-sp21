package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/** 
 * Represent a remote repository.
 * 表示一个远程仓库类，由于此项目的远程仓库是用本地文件夹模拟的
 * 因此该类拥有对远程仓库与Repository相同的目录结构
 * 也有类似的方法，以便于对远程仓库的文件读写
 * @author ChenJinzhao
 */
public class RemoteRepository {
    /**
     * The path of the remote repository.
     */
    private final String path;
    /**
     * The .gitlet dictionary.
     */
    File GITLET_DIR;
    /**
     * The commits dictionary.
     */
    File COMMITS_DIR;
    /**
     * The Blobs dictionary.
     */
    File BLOBS_DIR;
    /**
     * The HEAD pointer.
     */
    File HEAD;
    /**
     * The branches dictionary.
     */
    File BRANCHES_DIR;

    /**
     * Constructor.
     */
    RemoteRepository(String path) {
        this.path = path;
        GITLET_DIR = join(path);
        if (!GITLET_DIR.exists()) {
            Main.printError(ErrorMessage.REMOTE_DICTIONARY_NOT_FOUND.getMessage());
        }
        COMMITS_DIR = join(GITLET_DIR, "commits");
        BLOBS_DIR = join(GITLET_DIR, "blobs");
        HEAD = join(GITLET_DIR, "head");
        BRANCHES_DIR = join(GITLET_DIR, "branches");
    }

    /**
     * Get the current branch.
     */
    public String getCurrBranch() {
        return readContentsAsString(HEAD);
    }

    /**
     * Get the current commit.
     */
    public Commit getCurrCommit() {
        String currCommitID = readContentsAsString(join(BRANCHES_DIR, getCurrBranch()));
        return Repository.getObjectByID(COMMITS_DIR, currCommitID, Commit.class);
    }

    /**
     * Get the branch head commit from the given branch.
     */
    Commit getCommitByBranch(String branchName) {
        return Repository.getObjectByID(COMMITS_DIR, readContentsAsString(join(BRANCHES_DIR,
                branchName)), Commit.class);
    }

    /**
     * Move the branch to given commit or add a new branch.
     */
    void switchAddBranch(String branchName, String commitID) {
        writeContents(join(BRANCHES_DIR, branchName), commitID);
    }
}
