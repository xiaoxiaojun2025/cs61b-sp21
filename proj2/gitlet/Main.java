package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author ChenJinZhao
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printError(ErrorMessage.NON_ARGUMENT.getMessage());
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (args.length > 1) {
                    printError(ErrorMessage.INCORRECT_OPERANDS.getMessage());
                }
                Repository.setUpPersistence();
                break;
            case "add":
                if (args.length != 2) {
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
                if (args.length != 2) {
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
