package ch.abertschi.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by abertschi on 03/07/16.
 */
@Aspect
public class SetExecutionStatusToExecuted
{

    @Around("@annotation(ch.abertschi.aspectj.Interceptor)")
    public Object aroundInvoke()
    {
        System.out.println("within around aspect");
        Execution.GET.setExecutionStatus(Execution.ExecutionStatus.YES);
        return "intercepted";
    }

}
