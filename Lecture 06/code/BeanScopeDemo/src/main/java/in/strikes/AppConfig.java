package in.strikes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * THEORY (§1 + §6): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * §1: AppConfig is registered from Main, then @ComponentScan finds @Component classes.
 *
 * §6 — "bean DEFINITION" vs "bean OBJECT":
 *   DEFINITION = the recipe Spring stores (how to create + scope)
 *   OBJECT     = the actual instance in memory
 *
 * Example below (optional teaching beans — method names become bean names):
 *   @Bean getCart() and @Bean getCart2() are TWO definitions of CartService type.
 *   Even though both are singleton scope, you get TWO objects
 *   because there are TWO definitions — not "one class = one object".
 *
 * Rule of thumb:
 *   singleton  → 1 definition → 1 shared object
 *   prototype  → 1 definition → new object every request
 *   2 @Bean methods of same class → 2 definitions → 2 objects (if singleton)
 */
@Configuration
@ComponentScan
public class AppConfig {

    /**
     * Bean DEFINITION #1 named "getCart" (method name).
     * Scope singleton (default) → one object for THIS definition.
     */
    @Bean
    @Scope("singleton")
    public CartService getCart() {
        return new CartService("cart-1");
    }

    /**
     * Bean DEFINITION #2 named "getCart2".
     * Same class type, but DIFFERENT definition → DIFFERENT object.
     */
    @Bean
    @Scope("singleton")
    public CartService getCart2() {
        return new CartService("cart-2");
    }
}
