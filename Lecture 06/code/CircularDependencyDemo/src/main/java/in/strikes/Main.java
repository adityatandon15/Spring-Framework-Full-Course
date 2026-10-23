package in.strikes;

import in.strikes.simple.A;
import in.strikes.simple.B;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * THEORY map — Lecture 06/How-AppConfig-Gets-Registered.md
 *
 *   demoPlainJavaCircularFix()  →  §3 Case 1 (create both, then setters)
 *   Spring context below         →  §1 (AppConfig), §3 Case 2 (@Lazy), §4 (field vs ctor)
 */
public class Main {
    public static void main(String[] args) {

        // ----------------------------------------------------------
        // §3 Case 1 — PLAIN JAVA: A needs B and B needs A
        // Wrong: constructors call new on each other → StackOverflowError
        // Right: create both, then setB / setA
        // Code: simple/A.java , simple/B.java
        // ----------------------------------------------------------
        demoPlainJavaCircularFix();

        // ----------------------------------------------------------
        // §1 — AppConfig registered HERE (not by @ComponentScan)
        // §3 Case 2 — Spring cycle OrderService ↔ PaymentService
        //             fixed with @Lazy in PaymentService
        // §4 — field injection = create bean first, fill fields later
        // ----------------------------------------------------------
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        OrderService order = context.getBean(OrderService.class);
        order.placeOrder();
    }

    /**
     * Snippet for revision (§3 Case 1):
     *
     *   A a = new A();
     *   B b = new B();
     *   a.setB(b);
     *   b.setA(a);
     *
     * Same idea as Spring field injection timing: objects exist first, links filled later.
     * Difference: in Spring we prefer constructor injection for normal (non-cycle) deps.
     */
    private static void demoPlainJavaCircularFix() {
        System.out.println("--- Plain Java circular fix (§3) ---");

        A a = new A();
        B b = new B();

        // Wire AFTER both objects already exist
        a.setB(b);
        b.setA(a);

        a.show();
        b.show();
        System.out.println("--- Spring beans next (§1, §3, §4) ---");
    }
}
