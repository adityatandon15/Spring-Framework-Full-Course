package in.strikes;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * THEORY map — Lecture 06/How-AppConfig-Gets-Registered.md  →  §1, §6
 *
 * Run this class and READ the console order:
 *   1) Eager singletons print "created" during context startup
 *   2) Prototype / @Lazy print "created" only when first requested
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(">>> Creating ApplicationContext (eager singletons start here)");
        // §1: AppConfig registered here (not found by scan)
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println(">>> Context ready\n");

        demoSingletonSameObject(context);
        demoPrototypeNewObjectEachTime(context);
        demoLazySingleton(context);
        demoSingletonSharedAcrossBeans(context);
        demoTwoBeanDefinitions(context);
    }

    /**
     * SINGLETON: one definition → one object
     *
     *   OrderService o1 = context.getBean(OrderService.class);
     *   OrderService o2 = context.getBean(OrderService.class);
     *   o1 == o2  →  true
     */
    private static void demoSingletonSameObject(ApplicationContext context) {
        System.out.println("--- §6 Singleton: same object ---");
        OrderService o1 = context.getBean(OrderService.class);
        OrderService o2 = context.getBean(OrderService.class);

        System.out.println("o1 == o2 ? " + (o1 == o2)); // true
        o1.placeOrder();
        o2.placeOrder(); // same instance
        System.out.println();
    }

    /**
     * PROTOTYPE: one definition → new object every getBean()
     *
     *   User u1 = context.getBean(User.class);
     *   User u2 = context.getBean(User.class);
     *   u1 == u2  →  false
     *
     * Also LAZY: User constructor did NOT run at context startup.
     */
    private static void demoPrototypeNewObjectEachTime(ApplicationContext context) {
        System.out.println("--- §6 Prototype: new object each getBean (lazy) ---");
        User u1 = context.getBean(User.class);
        User u2 = context.getBean(User.class);

        u1.setName("Ram");
        u2.setName("Shyam");

        System.out.println("u1 == u2 ? " + (u1 == u2)); // false
        System.out.println("u1.name=" + u1.getName() + ", u2.name=" + u2.getName());
        System.out.println();
    }

    /**
     * SINGLETON + @Lazy: still one shared object, but created on first use.
     */
    private static void demoLazySingleton(ApplicationContext context) {
        System.out.println("--- §6 Lazy singleton: created on first getBean ---");
        System.out.println("(constructor prints on the next line, not at startup)");
        LazySingletonService s1 = context.getBean(LazySingletonService.class);
        LazySingletonService s2 = context.getBean(LazySingletonService.class);

        System.out.println("s1 == s2 ? " + (s1 == s2)); // true — still singleton
        s1.work();
        System.out.println();
    }

    /**
     * A and B both depend on OrderService → both get the SAME singleton.
     */
    private static void demoSingletonSharedAcrossBeans(ApplicationContext context) {
        System.out.println("--- §6 Same singleton injected into A and B ---");
        A a = context.getBean(A.class);
        B b = context.getBean(B.class);

        System.out.println("A.order == B.order ? " + (a.getOrderService() == b.getOrderService())); // true
        System.out.println();
    }

    /**
     * TWO bean DEFINITIONS of CartService (getCart + getCart2 in AppConfig)
     * → TWO objects, even though both are singleton scope.
     *
     * "Singleton" means: one object PER DEFINITION, not one object per Java class.
     */
    private static void demoTwoBeanDefinitions(ApplicationContext context) {
        System.out.println("--- §6 Two definitions of same class → two objects ---");
        CartService c1 = context.getBean("getCart", CartService.class);
        CartService c2 = context.getBean("getCart2", CartService.class);

        System.out.println("c1.id=" + c1.getId() + ", c2.id=" + c2.getId());
        System.out.println("c1 == c2 ? " + (c1 == c2)); // false — different definitions
        System.out.println();
    }
}
