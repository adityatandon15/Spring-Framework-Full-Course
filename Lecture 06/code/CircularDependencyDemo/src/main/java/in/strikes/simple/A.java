package in.strikes.simple;

/**
 * THEORY (§2, §3): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * PROBLEM — do NOT do this:
 *   public A() { this.b = new B(); }   // and B does new A()
 *   → StackOverflowError
 *
 * SOLUTION when A needs B and B needs A:
 *   1) Create both with empty constructors
 *   2) Wire with setters afterward
 *   See Main.demoPlainJavaCircularFix() for the snippet.
 */
public class A {
    private B b;

    // Empty constructor — does NOT create B here
    public A() {
        System.out.println("A created");
    }

    // Wire B AFTER both A and B already exist
    public void setB(B b) {
        this.b = b;
    }

    public void show() {
        System.out.println("A is using B: " + (b != null ? "yes" : "no"));
    }
}
