package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author ChenJinZhao
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printError(ErrorMessage.NON_ARGUMENT.getMessage());
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.setUpPersistence();
                break;
            case "add":
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                Commands.add(args[1]);
                break;
            case "commit":
                if (args.length < 2) {
                    printError(ErrorMessage.NON_MESSAGE_COMMIT.getMessage());
                }
                Commands.commit(args[1]);
                break;
            case "rm":
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                Commands.remove(args[1]);
                break;
            case "log":
                Commands.log();
                break;
            case "global-log":
                Commands.global_log();
                break;
            case "find":
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                Commands.find(args[1]);
                break;
            case "status":
                Commands.status();
                break;
            case "checkout":
                if (args.length < 2 ) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                if (args[1].equals("--")) {
                    if (args.length < 3) {
                        printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                    }
                    /* Case 1 */
                    Commands.checkout(args[2]);
                } else {
                    if (args.length < 3 || (args.length >= 3 && args[2].equals("--"))) {
                        /* Case 3 */
                        Commands.checkoutToBranch(args[1]);
                    } else {
                        if (args.length < 4) {
                            printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                        }
                        /* Case 2 */
                        Commands.checkout(args[1], args[3]);
                    }
                }
                break;
            case "branch":
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                Commands.createBranch(args[1]);
                break;
            case "rm-branch":
                if (args.length < 2) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                Commands.removeBranch(args[1]);
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
