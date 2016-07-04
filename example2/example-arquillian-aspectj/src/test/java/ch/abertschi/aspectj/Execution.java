package ch.abertschi.aspectj;

/**
 * Created by abertschi on 04/07/16.
 */
public enum Execution
{
    GET;

    public enum ExecutionStatus
    {
        YES,
        NO;
    }

    private ExecutionStatus status = ExecutionStatus.NO;

    public ExecutionStatus getExectionStatus()
    {
        return status;
    }

    public void setExecutionStatus(ExecutionStatus status)
    {
        this.status = status;
    }
}


