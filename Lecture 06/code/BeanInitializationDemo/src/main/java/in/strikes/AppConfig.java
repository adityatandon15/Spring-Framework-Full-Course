package in.strikes;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * THEORY (§1): Lecture 06/How-AppConfig-Gets-Registered.md
 *
 * Registered manually from Main — @ComponentScan then finds other @Component classes.
 */
@Configuration
@ComponentScan
public class AppConfig {
}
