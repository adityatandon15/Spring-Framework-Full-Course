package in.coderarmy;

import in.strikes.CartService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {

        // =========================================================
        // WHAT IS "Class" IN JAVA? (java.lang.Class)
        // =========================================================
        // "Class" is itself a real class in Java (java.lang.Class).
        //
        // Every class YOU write (AppConfig, OrderService, User, etc.)
        // automatically gets a matching Class OBJECT at runtime.
        // That Class object does NOT hold your business data
        // (like a user's name/age). Instead it holds METADATA —
        // information ABOUT the class itself.
        //
        // Think of it like this:
        //   OrderService          → the blueprint (your class)
        //   new OrderService(...) → one object created FROM that blueprint
        //   OrderService.class    → a Class object that DESCRIBES the blueprint
        //
        // What does that Class object "describe"? For example, for OrderService:
        //   - name          → "in.coderarmy.OrderService"
        //   - methods       → placeOrder(), setPaymentService(...), etc.
        //   - constructors  → OrderService(PaymentService paymentService)
        //   - annotations   → (e.g. @Component if it were uncommented)
        //   - fields        → paymentService
        //
        // Same idea for AppConfig.class:
        //   - name          → "in.coderarmy.AppConfig"
        //   - methods       → createUser(), createOrderService(...), etc.
        //   - annotations   → @Configuration, @ComponentScan("in.coderarmy")
        //   - constructors  → the default constructor
        //
        // Writing  Something.class  means:
        //   "Give me the Class object that describes Something"
        // It does NOT create an instance of Something.
        //
        // This is part of Java Reflection — inspecting types at runtime.
        // Spring heavily uses this: it reads Class objects to find
        // annotations (@Bean, @Configuration, ...) and create beans.
        // =========================================================

        // AppConfig.class → pass Spring the Class OBJECT of AppConfig
        // (not a new AppConfig()). Spring reads AppConfig's metadata:
        // annotations like @Configuration / @ComponentScan, and @Bean methods.
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // what is ApplicationContext?
        // ApplicationContext is the root interface for Spring's IoC container.
        // It manages bean creation, dependency injection, and bean lifecycle
        // (create → use → destroy), and gives beans to the client (your code).

        // what is AnnotationConfigApplicationContext?
        // A type of ApplicationContext that builds the container from
        // Java config classes (classes annotated with @Configuration),
        // instead of XML. You pass it AppConfig.class so it knows
        // which config Class object to read.

        // what is AppConfig?
        // AppConfig is OUR configuration class (@Configuration).
        // AppConfig.class is the Class object Spring uses to discover
        // @Bean methods and component-scan settings inside AppConfig.

        // OrderService.class → again a Class object (metadata about OrderService).
        // Spring uses it to find: "which bean in the container matches this TYPE?"
        // It looks at the Class object's type info (name / type), not a new instance.
        OrderService order = context.getBean(OrderService.class);
        order.placeOrder();

//        CartService cart = context.getBean(CartService.class);
//        cart.addToCart();

    }
}
