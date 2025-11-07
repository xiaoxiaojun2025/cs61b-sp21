package gitlet;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author ChenJinzhao
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit. */
    private Date timestamp;
    /** The first parent commit of this commit. */
    private String parent1;
    /** The second parent commit of this commit. */
    private String parent2;
    /** The references to this commit's blobs,
     *  which contain only plain files without subdictionary.
     *  This key is filename and value is sha-1 of the blob.
     */
    private Map<String, String> blobs;
    /** Constructor of the class, which doesn't contain timestamp,
     *  as it will calculate the current time.
     */
    public Commit (String message, String parent1, String parent2, Map<String, String> blobs) {
        this.message = message;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.blobs = blobs;
        timestamp = new Date();
    }
    /** Create a new Commit from its parent commit.
     *  The new one should have new message, timestamp and parentID. */
    public Commit (String message, Commit parent1) {
        this.message = message;
        this.parent1 = parent1.getId();
        parent2 = null;
        blobs = parent1.getBlobs();
        timestamp = new Date();
    }
    public Commit (String message, Commit parent1, String parent2) {
        this.message = message;
        this.parent1 = parent1.getId();
        this.parent2 = parent2;
        blobs = parent1.getBlobs();
        timestamp = new Date();
    }
    /** Constructor which create the very original commit.
     * 创建初始提交， 时间戳固定 */
    public Commit() {
        message = "initial commit";
        timestamp = new Date(0L);
        parent1 = parent2 = null;
        blobs = null;
    }
    /** Get the sha-1 id of the commit. */
    public String getId() {return sha1(serialize(this));}
    /** Get the message. */
    public String getMessage() {return message;}
    /** Get the blobs. */
    public Map<String, String> getBlobs() {return blobs == null ? new HashMap<>(): blobs;}
    /** Check if the commit contains a given blob. */
    public boolean containBlob(Blob other) {
        if (blobs == null || !blobs.containsKey(other.getFilename())) {return false;}
        return blobs.get(other.getFilename()).equals(other.getId());
    }
    public String getBlobByFileName(String filename) {
        if (blobs == null) {return null;}
        return blobs.get(filename);
    }
    /** Check if the commit contains a given filename. */
    public boolean containFilename(String other) {
        return blobs != null && blobs.containsKey(other);
    }
    /** Overwrite or add a blob. */
    void overwriteAdd(Blob newBlob) {
        if (blobs == null) {
            blobs = new TreeMap<>();
        }
        blobs.put(newBlob.getFilename(), newBlob.getId());
    }
    /** Remove an existing blob. */
    void removeFile(String filename) {blobs.remove(filename);}
    /** Get the parent1 of the commit. */
    public String getParent1() {return parent1;}
    /** Get the parent2 of the commit. */
    public String getParent2() {return parent2;}
    public List<String> getParents() {
        List<String> res = new LinkedList<>();
        res.add(parent1);
        res.add(parent2);
        return res;
    }
    /** Get formatted timestamp. */
    public String getFormattedTimestamp() {
        Instant instant = timestamp.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return zdt.format(formatter);
    }
    /** Print all message of one commit. */
    static void printCommit(Commit commit) {
        if (commit == null) {return;}
       System.out.println("===");
       String parent1 = commit.getParent1();
       String parent2 = commit.getParent2();
       System.out.println("commit " + commit.getId());
        if (parent2 != null && parent1 != null) {
            System.out.println("Merge: " + parent1.substring(0, 7) + " " + parent2.substring(0, 7));
        }
       System.out.println("Date: " + commit.getFormattedTimestamp());
       System.out.println(commit.getMessage());
       System.out.println();
    }
}
