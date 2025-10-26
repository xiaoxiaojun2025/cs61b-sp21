package capers;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author xiaoxiaojun
*/
public class Dog {

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = new File(CapersRepository.CAPERS_FOLDER, "dogs");

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        File target_dog = new File(DOG_FOLDER, name);
        if (!target_dog.exists()) {
            return null;
        }
        String[] s = divideString(Utils.readContentsAsString(target_dog));
        return new Dog(s[0], s[1], Integer.parseInt(s[2]));
    }

    /** Help divide a string to parts by space and get the first three strings. */
    private static String[] divideString(String s) {
        StringBuilder[] tres = new StringBuilder[3];
        for (int i = 0; i < tres.length; i += 1) {
            tres[i] = new StringBuilder();
        }
        int count = 0;
        for (int i = 0; i < s.length(); i += 1) {
            if (count == 3) {
                break;
            }
            if (s.charAt(i) != '\n') {
                tres[count].append(s.charAt(i));
            } else {
                count += 1;
            }
        }
        String[] res = new String[3];
        for (int i = 0; i < tres.length; i += 1) {
            res[i] = tres[i].toString();
        }
        return res;
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() throws IOException {
        File newDog = new File(DOG_FOLDER, name);
        Utils.writeContents(newDog, name, "\n", breed, "\n", Integer.toString(age), "\n");
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
