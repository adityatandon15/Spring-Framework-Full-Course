package in.strikes;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * THEORY (§1): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * AppConfig is registered from Main, then @ComponentScan finds OrderService / PaymentService.
 */
@Configuration
@ComponentScan
public class AppConfig {

}
