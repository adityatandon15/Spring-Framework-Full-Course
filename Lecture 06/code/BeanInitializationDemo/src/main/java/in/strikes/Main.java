package in.strikes;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        // THEORY (§1): AppConfig entry point — not found by scan.
        // See: Lecture 06/How-AppConfig-Gets-Registered.md
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        OrderService order = context.getBean(OrderService.class);

        //System.out.println("Payment Service not started yet");

        order.placeOrder();
//        PaymentService paymentService = context.getBean(PaymentService.class)

    }
}
