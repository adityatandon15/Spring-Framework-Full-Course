package in.strikes;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * THEORY:
 *   §4 Field vs Constructor  → Lecture 06/How-AppConfig-Gets-Registered.md
 *   §5 SRP / SOLID           → same notes file
 *
 * PREFERRED STYLE: constructor injection
 *   - dependencies are required and explicit
 *   - easy to unit test: new OrderService(mockPayment)
 *   - can use final fields
 *
 * Does this break SRP? NO.
 *   Constructor does NOT create PaymentService.
 *   Spring creates it and PASSES it in — we only store the reference.
 *   Creating deps with "new" inside this class would be the bad design.
 *
 * @Lazy here: if PaymentService also needs OrderService, delay resolving
 * PaymentService until first use (helps break a constructor cycle).
 * See also CircularDependencyDemo for the field-injection + @Lazy version.
 */
@Component
public class OrderService {

    // Prefer constructor injection over field @Autowired (see §4 in notes)
    private final PaymentService paymentService;

    public OrderService(@Lazy PaymentService paymentService) {
        // Only assigning — Spring already created paymentService
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();

        System.out.println("Order placed");
    }

    public void getOrderDetails() {
        System.out.println("Order Details");
    }
}
