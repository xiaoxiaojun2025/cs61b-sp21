package tester;
class Person {
    void speakTo(Person other) { System.out.println("kudos"); }
    void watch(SoccerPlayer other) { System.out.println("wow"); }
}
class Athlete extends Person {
    void speakTo(Athlete other) { System.out.println("take notes"); }
    void watch(Athlete other) { System.out.println("game on"); }
}
class SoccerPlayer extends Athlete {
    void speakTo(Athlete other) {
        System.out.println("respect");
    }

    void speakTo(Person other) {
        System.out.println("hmph");
    }
}
public class Test {

    public static void main(String[] args) {
        Person itai = new Person();
        Athlete sohum = new SoccerPlayer();
        Person jack = new Athlete();
        Athlete anjali = new Athlete();
        SoccerPlayer chirasree = new SoccerPlayer();

    }
}
