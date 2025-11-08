package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

/**
 *
 * @author ChenJinzhao */
public class RemoteRepository {
    /** The path of the remote repository. */
    private String path;
    /** The .gitlet dictionary. */
    public File GITLET_DIR;
    /** The commits dictionary. */
    public File COMMITS_DIR;
    /** The Blobs dictionary. */
    public File BLOBS_DIR;
    /** The HEAD pointer. */
    public File HEAD;
    /** The branches dictionary. */
    public File BRANCHES_DIR;
    /** Constructor. */
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
    /** Get the current branch. */
    public String getCurrBranch() {
        return readContentsAsString(HEAD);
    }
    /** Get the current commit. */
    public Commit getCurrCommit() {
        String currCommitID = readContentsAsString(join(BRANCHES_DIR, getCurrBranch()));
        return Repository.getObjectByID(COMMITS_DIR, currCommitID, Commit.class);
    }
    /** Get the branch head commit from the given branch. */
    Commit getCommitByBranch(String branchName) {
        return Repository.getObjectByID(COMMITS_DIR, readContentsAsString(join(BRANCHES_DIR, branchName)), Commit.class);
    }
    /** Move the branch to given commit or add a new branch. */
    void SwitchAddBranch(String branchName, String commitID) {
        writeContents(join(BRANCHES_DIR, branchName), commitID);
    }
}
