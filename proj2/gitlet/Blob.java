package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    /** The filename of this blob refers to. */
    private String filename;
    /** The contents of the file. */
    private byte[] content;
    /** Create a new blob by given filename and content. */
    public Blob(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
    }
    /** Create a new blob by a given file. */
    public Blob(File file) {
        filename = file.getName();
        content = readContents(file);
    }
    /** Get the filename of the blob. */
    public String getFilename() {
        return filename;
    }
    /** Get the id of the blob. */
    public String getId() {
        return sha1(serialize(this));
    }
    /** Get the content of the blob. */
    public byte[] getContent() {
        return content;
    }
    /** Write the blob to CWD's file. */
    void backIntoFile() {
        writeContents(join(Repository.CWD, filename), content);
    }

}
