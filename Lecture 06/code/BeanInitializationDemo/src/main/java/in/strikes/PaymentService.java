package in.strikes;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * THEORY (§3, §4): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * Constructor injection + @Lazy on the class (or on the injection point)
 * is one Spring way to deal with OrderService ↔ PaymentService.
 *
 * Without a cycle-break (@Lazy / redesign), constructor injection on BOTH
 * sides fails with BeanCurrentlyInCreationException — that fail-fast is good.
 */
@Component
@Lazy
public class PaymentService {

    private final OrderService orderService;

    public PaymentService(OrderService orderService) {
        // Dependency is injected by Spring — not created here (supports DIP / SRP)
        this.orderService = orderService;
    }

    public void pay() {
        System.out.println("Payment successful");

        orderService.getOrderDetails();
    }
}
