package in.strikes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * THEORY (§6): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * PROTOTYPE SCOPE:
 *   - ONE bean DEFINITION  →  NEW OBJECT every time you ask Spring for it
 *   - getBean(User.class) twice → two different objects (user1 != user2)
 *   - Always LAZY in practice:
 *       → Spring does NOT create a User at context startup
 *       → constructor runs only when getBean() / injection needs a User
 *
 * Why User is a good prototype candidate:
 *   User is STATEFUL (name, age differ per person).
 *   You usually want a new User object per use, not one shared user for everyone.
 *
 * Contrast with OrderService (singleton / often STATELESS service):
 *   One shared service object is fine for the whole app.
 */
@Component
@Scope("prototype")
public class User {

    private String name;
    private int age;

    public User() {
        // Printed every time Spring creates a User (each getBean)
        System.out.println("User created (prototype, lazy — only when requested)");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }
}
