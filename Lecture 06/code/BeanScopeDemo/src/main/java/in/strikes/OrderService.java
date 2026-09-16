package in.strikes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * THEORY (§6): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * SINGLETON SCOPE (default in Spring):
 *   - ONE bean DEFINITION  →  ONE shared OBJECT in the container
 *   - Every getBean(OrderService.class) returns the SAME instance
 *   - Default with ApplicationContext = EAGER
 *       → object is created when the context starts
 *       → you will see "OrderService created" even before getBean()
 *
 * NOTE: @Scope("singleton") is optional — singleton is already the default.
 * Do NOT confuse singleton with lazy:
 *   singleton  = how many objects (one shared)
 *   eager/lazy = WHEN that object is created
 */
@Component
@Scope("singleton") // default; written explicitly for learning
public class OrderService {

    public OrderService() {
        // Printed once at context startup (EAGER singleton)
        System.out.println("OrderService created (singleton, usually eagerly)");
    }

    public void placeOrder() {
        System.out.println("Order placed by: " + this);
    }
}
