package in.strikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * THEORY — revise with:
 *   Lecture 06/How-AppConfig-Gets-Registered.md  →  §2, §3, §4, §5
 *
 * CYCLE:
 *   OrderService → PaymentService
 *   PaymentService → OrderService   (see PaymentService)
 *
 * FIELD INJECTION FLOW (§4):
 *   1) Spring creates this bean with no-arg constructor (fields still null)
 *   2) Later Spring fills @Autowired fields
 *   That is why field injection can sometimes HIDE a cycle (create first, fill later).
 *   It does NOT mean field injection is better than constructor injection.
 *
 * WHY STILL PREFER CONSTRUCTOR (§4, §5):
 *   - explicit required deps, testable, final fields
 *   - constructor does NOT create PaymentService; Spring passes it in (not an SRP violation)
 *   - constructor cycles fail fast → you fix the design
 *
 * FIX IN THIS DEMO (§3):
 *   @Lazy on PaymentService's OrderService field (see PaymentService.java)
 */
@Component
public class OrderService {

    // Field injection demo: bean created first, field filled later (§4)
    @Autowired
    private PaymentService paymentService;

    // ---------- BAD if BOTH sides use this (constructor cycle) ----------
    // public OrderService(PaymentService paymentService) {
    //     this.paymentService = paymentService;
    // }
    // Error: BeanCurrentlyInCreationException  (see notes §2)
    // Preferred style in real apps is still constructor — just don't leave a cycle.
    // See BeanInitializationDemo for constructor + @Lazy example.
    // --------------------------------------------------------------------

    public void placeOrder() {
        paymentService.pay();
        getOrderDetails();
        System.out.println("Order placed");
    }

    public void getOrderDetails() {
        System.out.println("Order Details");
    }
}
