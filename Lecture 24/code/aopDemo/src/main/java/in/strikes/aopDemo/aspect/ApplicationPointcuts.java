package in.strikes.aopDemo.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class ApplicationPointcuts {

    @Pointcut("within(in.strikes.aopDemo.controller..*)")
    public void controllerLayer() {
        // emoty body
    }

    @Pointcut("within(in.strikes.aopDemo.service..*)")
    public void serviceLayer() {
        // empty body
    }

    @Pointcut("execution(public * * (..))")
    public void publicMethod() {
        // empty body
    }

    @Pointcut("serviceLayer() && publicMethod()")
    public void publicServiceMethod() {
        // empty body
    }

    @Pointcut("execution(* *.get* (..))")
    public void getterMethod() {
        // empty body
    }
}
