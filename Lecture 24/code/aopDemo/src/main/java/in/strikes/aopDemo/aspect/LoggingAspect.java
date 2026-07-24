package in.strikes.aopDemo.aspect;

import in.strikes.aopDemo.dto.Student;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

//    @Pointcut("within(in.strikes.aopDemo.service..*) " +
//            "&&" +
//            "execution(public * * (..))")
//    public void logPublicServiceMethod() {
//        // empty body
//    }

//    @Before("@within(org.springframework.stereotype.Service)")
//    public void logBeforeMethod() {
//        System.out.println("Method Intercepted");
//    }

//    @Before("args(in.strikes.aopDemo.dto.Student) " +
//            "&&" +
//            "within(in.strikes.aopDemo.service..*)")
//    public void logBeforeMethod2() {
//        System.out.println("Method Intercepted");
//    }

//    @Before("@args(jdk.jfr.Timestamp) " +
//            "&&" +
//            "within(in.strikes.aopDemo.service..*)")
//    public void logBeforeMethod3() {
//        System.out.println("Method Intercepted");
//    }

    @Before("this(in.strikes.aopDemo.service.StudentService)")
    public void logBeforeMethod4() {
        System.out.println("Method Intercepted");
    }

//    @Before("execution(in.strikes.aopDemo.dto.Student " +
//            "in.strikes.aopDemo.service.StudentService.createStudent(" +
//            "in.strikes.aopDemo.dto.Student))")
//    public void logBeforeMethod2() {
//        System.out.println("Method Intercepted");
//    }
}









