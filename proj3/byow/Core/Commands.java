package byow.Core;


import java.io.File;
import static byow.Core.Utils.*;

class Commands implements InputThing{

    //文件路径
    static final File CWD = new File(System.getProperty("user.dir"));
    static final File SAVE_FILE = join(CWD, "save_file.txt");

    //实例变量
    private String commands;
    private int curr;
    Commands(String commands) {
        this.commands = commands;
        this.curr = 0;
    }
    @Override
    public char getCurrKey() {
        return Character.toUpperCase(commands.charAt(curr));
    }
    @Override
    public boolean haveNextKey() {
        return curr < size();
    }
    @Override
    public char getNextKey() {
        if (!haveNextKey()) {
            return '\0';
        }
        return Character.toUpperCase(commands.charAt(curr++));
    }
    private int size() {
        return commands.length();
    }

    long getSeed() {
        while (haveNextKey()) {
            char c = getNextKey();
            if (c == Engine.NEW_WORLD) {
                StringBuilder stringBuilder = new StringBuilder();
                while (haveNextKey()) {
                    c = getNextKey();
                    if (c == Engine.SEED) {
                        if (stringBuilder.length() == 0) {
                            return -1;
                        }
                        return Long.parseLong(stringBuilder.toString());
                    }
                    stringBuilder.append(c);
                }
            }
        }
        return -1;
    }

    void load() {
        if (!SAVE_FILE.exists()) {
            return;
        }
        String loaded = readContentsAsString(SAVE_FILE);
        commands = loaded + commands.substring(1);
        curr = 0;
    }

    void quitSave() {
        String saved = commands.substring(0, curr - 2);
        writeContents(SAVE_FILE, saved);
        while (haveNextKey()) {
            getNextKey();
        }
    }
}
