package org;

class Person {
    public String name;
    public int age;
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public void greet(Person other) {System.out.println("Hello, " + other.name);}
}
class Grandma extends Person {
    public Grandma(String name, int age) {
        super(name, age);
    }
    @Override
    public void greet(Person other) {System.out.println("Hello, young whippersnapper");}
    public void greet(Grandma other) {System.out.println("How was bingo, " + other.name + "?");}
}
public class testPeople {
    public static void main(String[] args) {
        Person n = new Person("Neil", 12);
        Person a = new Grandma("Ada", 60);
        Grandma v = new Grandma("Vidya", 80);
//        Grandma al = new Person("Alex", 70);
        n.greet(a);
        n.greet(v);
        v.greet(a);
        v.greet((Grandma) a);
        a.greet(n);
        a.greet(v);
        ((Grandma) a).greet(v);
        ((Grandma) n).greet(v);
    }
}
