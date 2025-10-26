package capers;

import java.io.File;
import java.io.IOException;
public class Test {
    public static void main(String[] args) throws IOException {
        File b = new File(System.getProperty("user.dir"));
        System.out.println(b.toString());
        File a = new File(b, "ggbond");
        System.out.println(a.isFile());
    }
}
