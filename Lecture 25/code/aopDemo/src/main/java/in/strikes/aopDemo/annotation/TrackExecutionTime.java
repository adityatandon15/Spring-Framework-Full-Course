package in.strikes.aopDemo.annotation;

// Configured annotation

import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrackExecutionTime {

    long warnAfter() default 2000;

    String operation() default "";

}

