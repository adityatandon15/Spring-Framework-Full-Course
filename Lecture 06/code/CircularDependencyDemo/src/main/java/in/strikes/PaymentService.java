package in.strikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * THEORY — revise with:
 *   Lecture 06/How-AppConfig-Gets-Registered.md  →  §2, §3, §4
 *
 * Other side of the cycle: PaymentService → OrderService
 *
 * QUICK FIX (§3): @Lazy on this injection point
 *   - Spring injects a proxy first
 *   - Real OrderService is resolved when first used (e.g. in pay())
 *
 * To SEE the error again (§2):
 *   1) Remove @Lazy
 *   2) Use constructor injection on BOTH OrderService and PaymentService
 *   → BeanCurrentlyInCreationException
 *
 * Remember (§4): field injection "working" with a cycle only HIDES the design problem.
 */
@Component
public class PaymentService {

    // @Lazy breaks the creation-time cycle (quick fix — prefer redesign long-term)
    @Lazy
    @Autowired
    private OrderService orderService;

    // ---------- BAD (constructor cycle with OrderService) — keep commented ----------
    // public PaymentService(OrderService orderService) {
    //     this.orderService = orderService;
    // }
    // --------------------------------------------------------------------------------

    public void pay() {
        System.out.println("Payment done");

        // Proxy resolves to the real OrderService bean here
        orderService.getOrderDetails();
    }
}
