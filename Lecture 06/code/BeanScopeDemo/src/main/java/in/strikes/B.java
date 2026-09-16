package in.strikes;

import org.springframework.stereotype.Component;

/**
 * THEORY (§6): B also gets the same singleton OrderService as A.
 */
@Component
public class B {
    private final OrderService orderService;

    public B(OrderService orderService) {
        this.orderService = orderService;
        System.out.println("B got OrderService: " + orderService);
    }

    public OrderService getOrderService() {
        return orderService;
    }
}
