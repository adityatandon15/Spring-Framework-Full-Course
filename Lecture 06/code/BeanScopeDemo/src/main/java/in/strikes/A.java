package in.strikes;

import org.springframework.stereotype.Component;

/**
 * THEORY (§6): When A and B both inject singleton OrderService,
 * they receive the SAME shared object (one definition → one instance).
 *
 * See Main.demoSingletonSharedAcrossBeans()
 */
@Component
public class A {
    private final OrderService orderService;

    public A(OrderService orderService) {
        this.orderService = orderService;
        System.out.println("A got OrderService: " + orderService);
    }

    public OrderService getOrderService() {
        return orderService;
    }
}
