package tester;

public class A {
    public static void main(String[] args) {
        A y = new B();
        B z = new B();
        System.out.println(y.fish(y));
    }
    int fish(A other) {
        return 1;
    }
    int fish(B other) {
        return 2;
    }
}
class B extends A {
    @Override
    int fish(A other) {
        return 4;
    }
    @Override
    int fish(B other) {
        return 3;
    }
}
