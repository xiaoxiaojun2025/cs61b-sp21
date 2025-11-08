package gitlet;

import static gitlet.Commands.*;
import static gitlet.RemoteCommands.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author ChenJinZhao
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            printError(ErrorMessage.NON_ARGUMENT.getMessage());
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.setUpPersistence();
                break;
            case "add":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                add(args[1]);
                break;
            case "commit":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                if (args[1].isEmpty()) {
                    printError(ErrorMessage.NON_MESSAGE_COMMIT.getMessage());
                }
                commit(args[1]);
                break;
            case "rm":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                remove(args[1]);
                break;
            case "log":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                log();
                break;
            case "global-log":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                global_log();
                break;
            case "find":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                find(args[1]);
                break;
            case "status":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                status();
                break;
            case "checkout":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2 ) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                /* Checkout to branch */
                if (args.length == 2) {
                    checkoutToBranch(args[1]);
                /* Checkout one file to curr */
                } else if (args[1].equals("--")) {
                    checkout(args[2]);
                } else if (args.length >= 4) {
                    if (!args[2].equals("--")) {
                        printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                    } else {
                /* Checkout one file to given commit */
                        checkout(args[1], args[3]);
                    }
                }
                break;
            case "branch":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                createBranch(args[1]);
                break;
            case "rm-branch":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                removeBranch(args[1]);
                break;
            case "reset":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                reset(args[1]);
                break;
            case "merge":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                merge(args[1]);
                break;
            case "add-remote":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 3) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                addRemote(args[1], args[2]);
                break;
            case "rm-remote":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                removeRemote(args[1]);
                break;
            case "push":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 3) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                push(args[1], args[2]);
                break;
            case "fetch":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 3) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                fetch(args[1], args[2]);
                break;
            case "pull":
                if (!Repository.isGitletSetUp()) {
                    printError(ErrorMessage.GITLET_NOT_INITIALIZED.getMessage());
                }
                if (args.length < 3) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                pull(args[1], args[2]);
                break;
            default:
                printError(ErrorMessage.NON_EXISTING_COMMAND.getMessage());
        }
    }
    /** Print the error message and exit the program. */
    static void printError(String message) {
        System.out.println(message);
        System.exit(0);
    }
}
