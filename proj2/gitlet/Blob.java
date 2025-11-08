package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

/** Represents a file object.
 *  该类定义单个文件对象，包括文件名和文件内容（字节数组方式），只有相同的文件名
 *  和完全相同的文件内容才算是相同的BLOB，实现getID方法使其成为内容可寻址对象
 *
 *  @author ChenJinzhao
 */

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
    /** Write the blob to CWD's file.
     *  把BLOB转化为工作目录的文件 */
    void backIntoFile() {
        writeContents(join(Repository.CWD, filename), content);
    }

}
