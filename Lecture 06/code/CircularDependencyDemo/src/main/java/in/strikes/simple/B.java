package in.strikes.simple;

/**
 * THEORY (§2, §3): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * Pair with A.java — create both first, then setA / setB (see Main).
 */
public class B {
    private A a;

    // Empty constructor — does NOT create A here
    public B() {
        System.out.println("B created");
    }

    // Wire A AFTER both A and B already exist
    public void setA(A a) {
        this.a = a;
    }

    public void show() {
        System.out.println("B is using A: " + (a != null ? "yes" : "no"));
    }
}
