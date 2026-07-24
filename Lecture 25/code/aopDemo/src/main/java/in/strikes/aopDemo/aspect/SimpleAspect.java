package in.strikes.aopDemo.aspect;

import in.strikes.aopDemo.annotation.TrackExecutionTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SimpleAspect {

//    @Before("@annotation(jdk.jfr.Timestamp)")
//    public void logBeforeMethod() {
//        System.out.println("Method Intercepted");
//    }

    @Around("@annotation(trackExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint,
                                       TrackExecutionTime trackExecutionTime)
            throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        }
        finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            String operation = trackExecutionTime.operation();

            if(operation.isBlank()) {
                operation = joinPoint.getSignature().getName();
            }

            long warningThreshold = trackExecutionTime.warnAfter();

            if(duration >= warningThreshold) {
                System.out.println("SLOW OPERATION ALERT : " +
                        "Time Taken by " +
                        operation  + ": " + duration);
            }
            else {
                System.out.println(
                        "Time Taken by " + operation  + ": " + duration);
            }
        }
    }
}









