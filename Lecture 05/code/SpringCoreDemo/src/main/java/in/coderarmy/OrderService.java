package in.coderarmy;

import in.coderarmy.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Component
// When Main does: context.getBean(OrderService.class)
// OrderService.class is the Class OBJECT that DESCRIBES this class.
// Example of what that Class object knows about OrderService:
//   - name         → "in.coderarmy.OrderService"
//   - fields       → paymentService
//   - constructors → OrderService(PaymentService paymentService)
//   - methods      → setPaymentService(...), placeOrder()
//   - annotations  → @Component (if uncommented above)
// Spring uses that type metadata to locate the matching bean in the container.
//
// =========================================================
// @Autowired — HOW DEPENDENCY INJECTION WORKS
// =========================================================
// @Autowired tells Spring: "please INJECT (wire) a matching bean from the
// ApplicationContext into this place."
// You do NOT write: paymentService = new CardPayment() yourself.
// Spring finds a bean of type PaymentService in the container and plugs it in.
//
// HOW IT FINDS THE BEAN (matching rules):
//   1) By TYPE first  → look for a bean whose type is PaymentService
//                       (or implements PaymentService, e.g. CardPayment / UpiPayment)
//   2) If MANY beans of that type exist → ambiguity error
//      Fix with @Qualifier("beanName") or mark one bean @Primary
//   3) If NO bean of that type exists → startup fails (by default)
//
// In THIS project there can be multiple PaymentService beans
// (CardPayment + UpiPayment / createCardPayment + createUpiPayment),
// so you often need @Qualifier — see AppConfig createOrderService(...).
//
// THREE PLACES YOU CAN PUT @Autowired:
//
// A) Constructor injection  (PREFERRED / modern Spring style)
//    @Autowired   // optional on a SINGLE constructor since Spring 4.3+
//    public OrderService(@Qualifier("cp") PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }
//    Spring calls the constructor and passes the matching bean as the argument.
//    Works well with final fields and makes required deps obvious.
//
// B) Field injection  — WORKS, but NOT RECOMMENDED
//    @Autowired
//    @Qualifier("cp")
//    private PaymentService paymentService;
//    Spring sets the field AFTER creating the object (via reflection).
//    Short to write, but Spring / industry prefer constructor injection.
//
//    WHY field injection is NOT recommended:
//    1) Harder to UNIT TEST
//       Dependencies are private fields. In a plain unit test you cannot easily
//       pass a fake PaymentService via constructor — you must use reflection
//       or a Spring test context. With constructor injection you just do:
//         new OrderService(fakePaymentService)
//    2) Hides dependencies
//       Looking at the class, required deps are not obvious in the constructor.
//       Constructor injection makes "what does this class need?" crystal clear.
//    3) Cannot use "final" fields
//       Field-injected fields are set AFTER the object is created, so they
//       cannot be final. Constructor injection allows:
//         private final PaymentService paymentService;
//       which makes the dependency immutable / safer.
//    4) Breaks encapsulation / uses reflection
//       Spring reaches into private fields. That couples your class tightly
//       to the Spring container — outside Spring, the object is incomplete
//       until someone sets those fields manually.
//    5) Null / partial-object risk
//       Object exists before fields are injected. If something uses the bean
//       too early, paymentService can still be null.
//
//    Prefer: constructor injection (A). Use setter (C) only for optional deps.
//
// C) Setter injection
//    @Autowired
//    @Qualifier("cp")
//    public void setPaymentService(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }
//    Spring creates OrderService first, then calls the setter with the bean.
//    Useful for OPTIONAL dependencies.
//
// IMPORTANT LINK TO THIS LECTURE:
// Right now OrderService is created in AppConfig via @Bean createOrderService(...),
// and the PaymentService argument is wired THERE (with @Qualifier("cp")).
// That is Spring doing the SAME "find bean by type (+ qualifier)" work —
// similar idea to @Autowired, but on a @Bean method parameter.
//
// If you uncomment @Component on OrderService (and stop creating it with @Bean),
// then you would put @Autowired on the constructor / field / setter BELOW
// so Spring injects PaymentService automatically when creating OrderService.
// =========================================================
public class OrderService {

    // Example of FIELD injection (currently NOT active — for learning only):
    // @Autowired
    // @Qualifier("cp")
    private PaymentService paymentService;

    // Example of CONSTRUCTOR injection (preferred).
    // @Autowired is optional here if this is the only constructor.
    // @Autowired
    // public OrderService(@Qualifier("cp") PaymentService paymentService) { ... }
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Example of SETTER injection (currently NOT active — for learning only):
    // @Autowired
    // @Qualifier("cp")
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder() {

        paymentService.pay();

        System.out.println("Order placed");
    }
}
