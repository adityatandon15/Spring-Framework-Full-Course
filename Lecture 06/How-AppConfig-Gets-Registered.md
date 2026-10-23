# Lecture 06 — Revision Notes (Theory + Code Map)

Use this file while revising. Each topic points to the demo code where the same idea is commented.

| Topic | Theory (this file) | Code |
|-------|--------------------|------|
| How `AppConfig` gets registered | §1 | `BeanScopeDemo` / `BeanInitializationDemo` / `CircularDependencyDemo` → `AppConfig.java`, `Main.java` |
| Circular dependency error & check | §2 | `CircularDependencyDemo` |
| How to solve circular dependency | §3 | `CircularDependencyDemo` (`simple/A`, `simple/B`, `OrderService`, `PaymentService`, `Main`) |
| Field vs Constructor injection | §4 | `CircularDependencyDemo` + `BeanInitializationDemo` |
| Does constructor injection break SRP? | §5 | Comments in `OrderService` / `PaymentService` |
| Singleton vs Prototype, Eager vs Lazy | §6 | `BeanScopeDemo` (`OrderService`, `User`, `LazySingletonService`, `CartService`, `Main`) |

---

# §1 How Does `AppConfig` Get Registered?

## The doubt

```java
@Configuration
@ComponentScan
public class AppConfig {
}
```

- `@Configuration` is meta-annotated with `@Component`
- `@ComponentScan` is written **on** `AppConfig` itself

> If `@Configuration` is a kind of `@Component`, and components are found by `@ComponentScan`, then how does Spring scan `AppConfig`?

## Short answer

**`AppConfig` is NOT found by `@ComponentScan`.**

It is registered **manually** in `Main`:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

## Step-by-step flow

```
Main
  → new AnnotationConfigApplicationContext(AppConfig.class)
      → Spring registers AppConfig as a bean (because of @Configuration)
          → Spring reads @ComponentScan on AppConfig
              → Spring scans the package for OTHER @Component classes
```

| Class | How it enters the container |
|--------|-----------------------------|
| `AppConfig` | Explicit registration via `AnnotationConfigApplicationContext(AppConfig.class)` |
| Other beans | Found by `@ComponentScan` |

## Why `@Configuration` still uses `@Component`

`@Component` has two roles:

1. **Marker** — “this class is a Spring bean candidate”
2. **Scan target** — `@ComponentScan` looks for `@Component` / `@Service` / `@Configuration` etc.

`AppConfig` uses role 1 via **manual registration**, not role 2 via scan.

**Code refs:** comments on `AppConfig.java` + `Main.java` in all three Lecture 06 demos.

---

# §2 Circular Dependency — Error & How to Check

## What is it?

```
OrderService  →  PaymentService
PaymentService  →  OrderService   // loop
```

Plain Java (`simple.A` / `simple.B`):

```
A creates B, B creates A, ... → StackOverflowError
```

## Errors

### Constructor injection cycle (Spring)

```text
BeanCurrentlyInCreationException:
Requested bean is currently in creation: Is there an unresolvable circular reference?
```

Often wrapped as `UnsatisfiedDependencyException`. Cycle in log:

```text
orderService → paymentService → orderService
```

### Field injection cycle

May start in classic Spring Core (early incomplete bean), but **hides** bad design.  
Spring Boot 2.6+ often still fails and prints:

```text
┌─────┐
|  orderService
↑     ↓
|  paymentService
└─────┘
```

### Plain Java recursive `new`

```text
StackOverflowError
```

## How to check

1. **Run the app** — cycle often fails at context startup.
2. **Draw arrows** from every `@Autowired` / constructor param.
3. **Force constructor injection temporarily** — cycles show up loudly.
4. **Read the ASCII cycle** in the stack trace.

**Code refs:** `CircularDependencyDemo/OrderService.java`, `PaymentService.java`

---

# §3 How to SOLVE circular dependency

**Demo folder:** `Lecture 06/code/CircularDependencyDemo/`

## Case 1 — Plain Java (`A` needs `B`, `B` needs `A`)

Wrong: create the other inside the constructor → `StackOverflowError`

Right: create both first, then wire with setters:

```java
A a = new A();
B b = new B();
a.setB(b);
b.setA(a);
```

**Code:** `simple/A.java`, `simple/B.java`, `Main.demoPlainJavaCircularFix()`

## Case 2 — Spring (`OrderService` ↔ `PaymentService`)

Quick fix — `@Lazy` on **one** side:

```java
@Lazy
@Autowired
private OrderService orderService;
```

Preferred long-term: redesign (remove one direction, or extract a 3rd class).

**Code:** `OrderService.java`, `PaymentService.java`

---

# §4 Field injection vs Constructor injection

## Field injection — create bean first, fill fields later

```text
1) Spring calls no-arg constructor  → bean object exists, fields are null
2) Spring later sets @Autowired fields
```

That is why a cycle can sometimes still start with field injection: both beans can be **created first**, then fields filled **later**.  
So field injection does **not** truly solve a cycle — it can **hide** it.

## Constructor injection — deps required during construction

```text
1) Spring must already have the dependency beans
2) Then it calls: new OrderService(paymentService)
```

If A needs B and B needs A through constructors → nothing can finish → `BeanCurrentlyInCreationException`.  
Failing fast is good: it forces a design fix (or intentional `@Lazy`).

## Why we still prefer constructor injection

| Point | Constructor | Field `@Autowired` |
|--------|-------------|---------------------|
| Required deps | Explicit & mandatory | Easy to leave null |
| Immutability | Can use `final` | Cannot |
| Unit tests | `new OrderService(mock)` | Needs Spring / reflection |
| Fail fast | Missing bean fails startup | Bugs can appear later |
| Cycles | Exposed early | Often hidden |

**Code refs:**

- Field style + comments: `CircularDependencyDemo/OrderService.java`
- Constructor style + `@Lazy`: `BeanInitializationDemo/OrderService.java`, `PaymentService.java`

---

# §5 Does constructor injection break SRP / SOLID?

**No.**

```java
public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService; // only stores the reference
}
```

- The constructor does **not create** `PaymentService`.
- Spring (or your test) creates it and **passes it in**.
- Receiving collaborators is normal object construction, not a second business responsibility.

| Approach | SOLID impact |
|----------|----------------|
| `new PaymentService()` inside `OrderService` | Bad — class creates its own deps (hurts DIP / coupling) |
| Constructor receives `PaymentService` | Good — DI; class focuses on its job (supports SRP + DIP) |

SRP = reasons to change / business job — **not** “constructor must have zero parameters.”

**Code refs:** class-level comments in `BeanInitializationDemo/OrderService.java` and `CircularDependencyDemo/OrderService.java`

---

# §6 Bean Scope — Singleton, Prototype, Eager, Lazy

**Demo folder:** `Lecture 06/code/BeanScopeDemo/`  
**Run:** `Main.java` and watch the console order of `"created"` messages.

## Bean DEFINITION vs bean OBJECT (very important)

| Term | Meaning |
|------|---------|
| **Bean definition** | The *recipe* Spring stores: class + scope + how to create it (`@Component` or `@Bean` method) |
| **Bean object / instance** | The actual object in memory created from that recipe |

So:

- **Singleton** = **one definition → one shared object**
- **Prototype** = **one definition → new object every time you ask**
- **Two `@Bean` methods** of the same class = **two definitions → two objects** (even if both are singleton)

Singleton does **NOT** mean “only one object of that Java class in the whole JVM.”  
It means “one shared object **per bean definition** inside that Spring container.”

Example in code (`AppConfig` + `CartService`):

```java
@Bean
public CartService getCart() { return new CartService("cart-1"); }

@Bean
public CartService getCart2() { return new CartService("cart-2"); }
```

Same class, two definitions → `c1 == c2` is **false**.

## Singleton scope

```java
@Component
@Scope("singleton") // default — can omit
public class OrderService { ... }
```

- Default scope in Spring.
- Every `getBean(OrderService.class)` returns the **same** instance → `o1 == o2` is **true**.
- If `A` and `B` both inject `OrderService`, they share the **same** object.

```java
OrderService o1 = context.getBean(OrderService.class);
OrderService o2 = context.getBean(OrderService.class);
System.out.println(o1 == o2); // true
```

**Good for:** mostly stateless services (`OrderService`, repositories, etc.)

**Code:** `OrderService.java`, `A.java`, `B.java`, `Main.demoSingletonSameObject()`

## Prototype scope

```java
@Component
@Scope("prototype")
public class User { ... }
```

- Every `getBean(User.class)` creates a **new** object → `u1 == u2` is **false**.
- Spring does **not** create it at startup; it creates it when requested.

```java
User u1 = context.getBean(User.class);
User u2 = context.getBean(User.class);
System.out.println(u1 == u2); // false
```

**Good for:** stateful objects (e.g. `User` with different name/age per use)

**Code:** `User.java`, `Main.demoPrototypeNewObjectEachTime()`

## Eager vs Lazy — WHEN the object is created

Scope (singleton/prototype) answers **how many**.  
Eager/lazy answers **when**.

| Bean | Scope | When created | Eager / Lazy |
|------|--------|--------------|--------------|
| `OrderService` | singleton | Context startup | **Eager** (default with `ApplicationContext`) |
| `LazySingletonService` | singleton + `@Lazy` | First `getBean()` | **Lazy** singleton |
| `User` | prototype | Every `getBean()` | **Lazy** (not at startup; new each time) |
| `CartService` `@Bean`s | singleton | Context startup | **Eager** |

Common mistake (fixed in your notes/code):

- `@Scope("singleton")` is **not** “lazy initialization”
- Singleton is usually **eager**
- Use `@Lazy` if you want a lazy singleton

```java
@Component
@Lazy
public class LazySingletonService { ... } // still ONE object, created on first use
```

**Code:** `LazySingletonService.java`, `Main.demoLazySingleton()`

## Quick revision table

| Question | Singleton | Prototype |
|----------|-----------|-----------|
| How many objects per definition? | 1 shared | New each request |
| `getBean` twice → `==` ? | `true` | `false` |
| Created at context startup? | Yes (unless `@Lazy`) | No |
| Typical use | Services | Stateful per-use objects |

## Snippets to remember

```java
// Singleton — one shared object
OrderService o1 = context.getBean(OrderService.class);
OrderService o2 = context.getBean(OrderService.class);
// o1 == o2 → true  (and usually created at startup)

// Prototype — new object each time
User u1 = context.getBean(User.class);
User u2 = context.getBean(User.class);
// u1 == u2 → false (created only when requested)

// Lazy singleton — still one object, delayed creation
LazySingletonService s1 = context.getBean(LazySingletonService.class);
LazySingletonService s2 = context.getBean(LazySingletonService.class);
// s1 == s2 → true
```
