package in.strikes;

/**
 * THEORY (§6): Used only via @Bean methods in AppConfig (NOT @Component).
 *
 * Purpose: show that "same class" ≠ "one object".
 * Two @Bean methods ⇒ two bean definitions ⇒ two singleton objects.
 *
 * See Main.demoTwoBeanDefinitions()
 */
public class CartService {

    private final String id;

    public CartService(String id) {
        this.id = id;
        System.out.println("CartService created, id=" + id + " (eager singleton for its @Bean definition)");
    }

    public String getId() {
        return id;
    }
}
