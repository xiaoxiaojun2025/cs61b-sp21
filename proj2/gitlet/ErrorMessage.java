package gitlet;

public enum ErrorMessage {

    /* Common error. */

    /**
     * If a user doesn’t input any arguments.
     */
    NON_ARGUMENT("Please enter a command."),
    /**
     * If a user inputs a command that doesn’t exist.
     */
    NON_EXISTING_COMMAND("No command with that name exists."),
    /**
     * If a user inputs a command with the wrong number or format of operands.
     */
    INCORRECT_OPERANDS("Incorrect operands."),
    /**
     * If a user inputs a command that requires being in an initialized Gitlet working directory
     * (i.e., one containing a .gitlet subdirectory), but is not in such a directory.
     */
    GITLET_NOT_INITIALIZED("Not in an initialized Gitlet directory."),

    /* Error for INIT command. */

    /**
     * If there is already a Gitlet version-control system in the current directory, it should abort.
     * It should NOT overwrite the existing system with a new one.
     */
    GITLET_ALREADY_EXISTS("A Gitlet version-control system already exists in the current directory."),

    /* Error for ADD command. */

    /**
     * If the file does not exist.
     */
    NON_EXISTING_FILE("File does not exist."),

    /* Error for COMMIT command. */

    /**
     * If no files have been staged, abort.
     */
    NON_FILES_STAGED("No changes added to the commit."),
    /**
     * Every commit must have a non-blank message. If it doesn’t, it errors.
     */
    NON_MESSAGE_COMMIT("Please enter a commit message."),

    /* Error for RM command. */
    /**
     * If the file is neither staged nor tracked by the head commit.
     */
    NO_NEED_TO_RM("No reason to remove the file."),

    /* Error for FIND command. */
    /**
     * If no such commit exists.
     */
    NON_EXISTING_COMMIT("Found no commit with that message."),

    /* Error for CHECKOUT command. */
    /**
     * If the file does not exist in the previous commit.
     */
    FILE_NOT_IN_COMMIT("File does not exist in that commit."),
    /**
     * If no commit with the given id exists.
     */
    NON_EXISTING_COMMIT_WITH_ID("No commit with that id exists."),
    /**
     * If no branch with that name exists.
     */
    NON_EXISTING_BRANCH("No such branch exists."),
    /**
     * If that branch is the current branch.
     */
    ALREADY_CURRENT_BRANCH("No need to checkout the current branch."),
    /**
     * If a working file is untracked in the current branch and would be overwritten by the checkout.
     */
    UNTRACKED_FILE_EXISTS("There is an untracked file in the way; delete it, or add and commit it first."),

    /* Error for BRANCH command. */
    /**
     * If a branch with the given name already exists.
     */
    ALREADY_EXISTING_BRANCH("A branch with that name already exists."),

    /* Error for RM-BRANCH command. */
    /**
     * If a branch with the given name does not exist.
     */
    NON_EXISTING_BRANCH_WITH_NAME("A branch with that name does not exist."),
    /**
     * If you try to remove the branch you’re currently on.
     */
    CURRENT_BRANCH_REMOVE_ABORTED("Cannot remove the current branch."),

    /* Error for MERGE command. */
    /**
     * If there are staged additions or removals present.
     */
    CHANGES_UNCOMMITED("You have uncommitted changes."),
    /**
     * If a branch with the given name does not exist.
     */
    BRANCH_WITH_NAME_NOT_EXISTING("A branch with that name does not exist."),
    /**
     * If attempting to merge a branch with itself.
     */
    CANNOT_MERGE_ITSELF("Cannot merge a branch with itself."),
    /**
     * if the merge encountered a conflict.
     */
    MERGE_CONFLICT("Encountered a merge conflict."),

    /* Error for ADD-REMOTE command. */
    /**
     * If a remote with the given name already exists.
     */
    REMOTE_ALREADY_EXISTING("A remote with that name already exists."),

    /* Error for RM-REMOTE command. */
    /**
     * If a remote with the given name does not exist.
     */
    REMOTE_NON_EXISTING("A remote with that name does not exist."),

    /* Error for PUSH command. */
    /**
     * If the remote .gitlet directory does not exist.
     */
    REMOTE_DICTIONARY_NOT_FOUND("Remote directory not found."),
    /**
     * If the remote branch’s head is not in the history of the current local head.
     */
    REMOTE_HEAD_NOT_IN_CURR("Please pull down remote changes before pushing."),

    /* Error for FETCH command. */
    /**
     * If the remote Gitlet repository does not have the given branch name.
     */
    REMOTE_BRANCH_NOT_EXISTING("That remote does not have that branch.");


    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    /**
     * Get the errorMessage.
     */
    public String getMessage() {
        return message;
    }


}
