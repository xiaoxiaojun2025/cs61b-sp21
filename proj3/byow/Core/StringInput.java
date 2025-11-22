package byow.Core;

public class StringInput implements InputThing{
    private String string;
    private int index;
    StringInput(String in) {
        string = in;
        index = 0;
    }
    @Override
    public boolean haveNextKey() {
        return index < size();
    }
    @Override
    public char getNextKey() {
        if (!haveNextKey()) {
            return '\0';
        }
        return Character.toUpperCase(string.charAt(index++));
    }
    private int size() {
        return string.length();
    }
}
