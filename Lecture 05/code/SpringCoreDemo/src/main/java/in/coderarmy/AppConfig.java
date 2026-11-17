package in.coderarmy;

import in.coderarmy.payment.CardPayment;
import in.coderarmy.payment.PaymentService;
import in.coderarmy.payment.UpiPayment;
import in.strikes.CartService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

// When Main does: new AnnotationConfigApplicationContext(AppConfig.class)
// Spring receives AppConfig.class — the Class OBJECT that DESCRIBES this class.
// From that Class object Spring can read:
//   - annotations → @Configuration, @ComponentScan (seen below)
//   - methods     → createUser(), createCardPayment(), createOrderService(...), etc.
//   - constructors / name / fields
// That is why "every class has a matching Class object": AppConfig (this file)
// and AppConfig.class (the metadata object Spring inspects) are related but different.
@Configuration
// =========================================================
// @ComponentScan — HOW PACKAGE SCANNING BEHAVES
// =========================================================
// @ComponentScan tells Spring: "look for classes annotated with
// @Component, @Service, @Repository, @Controller, etc. and register them as beans."
//
// 1) If you write ONLY @ComponentScan  (no package mentioned):
//    Spring defaults to the PACKAGE WHERE THIS AppConfig class lives
//    + all of its SUB-PACKAGES.
//    Example: AppConfig is in package "in.coderarmy"
//             → scans "in.coderarmy" and "in.coderarmy.payment", etc.
//             → does NOT scan sibling packages like "in.strikes"
//
// 2) If you write @ComponentScan("in.coderarmy")  (package mentioned — current code):
//    Spring scans ONLY that base package + its sub-packages
//    (same coverage as the default here, but you made the package EXPLICIT).
//    So classes like OrderService / payment classes under in.coderarmy
//    can be picked up IF they have @Component (or similar).
//    Still does NOT scan "in.strikes" (where CartService lives).
//
// 3) If you want to scan ANOTHER package that is NOT under AppConfig's package:
//    AppConfig is in "in.coderarmy". CartService is in "in.strikes" —
//    that is a DIFFERENT / sibling package, so default scan misses it.
//    Pass multiple base packages, e.g.:
//      @ComponentScan(basePackages = {"in.coderarmy", "in.strikes"})
//    or the shorter form:
//      @ComponentScan({"in.coderarmy", "in.strikes"})
//    Then Spring will also look in "in.strikes" for @Component classes.
//    (Right now CartService is registered via @Bean createCartService() below
//     instead of relying on component scan.)
// =========================================================
@ComponentScan("in.coderarmy")
public class AppConfig {

    @Bean
    public User createUser() {
        return new User("Aditya", 28);
    }

    @Bean
    public CartService createCartService() {
        return new CartService();
    }

    // Two beans of the SAME type (PaymentService) → Spring cannot guess which
    // one to inject unless you distinguish them (@Qualifier / @Primary).
    // This is exactly the ambiguity problem @Autowired also faces.
    @Bean
    @Qualifier("cp")
    public PaymentService createCardPayment() {
        return new CardPayment();
    }

    @Bean
    @Qualifier("upi")
    public PaymentService createUpiPayment() {
        return new UpiPayment();
    }

    // =========================================================
    // @Bean method parameters ≈ same idea as @Autowired
    // =========================================================
    // Spring sees: createOrderService needs a PaymentService argument.
    // It AUTO-WIRES that argument from the container (find bean by type).
    // Because TWO PaymentService beans exist (cp + upi), we add
    // @Qualifier("cp") so Spring injects CardPayment, not UpiPayment.
    //
    // Flow:
    //   1) Spring creates PaymentService beans (createCardPayment / createUpiPayment)
    //   2) Spring calls createOrderService(...)
    //   3) It looks up the bean named/qualified "cp" and passes it in
    //   4) We return new OrderService(paymentService) — DI is done
    //
    // If OrderService were a @Component instead of this @Bean method,
    // you would put @Autowired (+ @Qualifier) on OrderService's
    // constructor / field / setter. See notes in OrderService.java.
    // =========================================================
    @Bean
    public OrderService createOrderService(@Qualifier("cp") PaymentService paymentService) {
        return new OrderService(paymentService);
    }
}
