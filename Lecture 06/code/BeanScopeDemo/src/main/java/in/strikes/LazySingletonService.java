package in.strikes;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * THEORY (§6): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * Still a SINGLETON (one shared object), but marked @Lazy:
 *   - scope = singleton  → only ONE object will ever exist
 *   - @Lazy             → that object is created on FIRST getBean(), not at startup
 *
 * So:
 *   OrderService          → singleton + EAGER (default with ApplicationContext)
 *   LazySingletonService  → singleton + LAZY
 *   User                  → prototype + LAZY (new object each request)
 */
@Component
@Lazy
public class LazySingletonService {

    public LazySingletonService() {
        System.out.println("LazySingletonService created (singleton, but LAZY — first use only)");
    }

    public void work() {
        System.out.println("LazySingletonService working: " + this);
    }
}
