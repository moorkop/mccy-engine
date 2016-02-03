package me.itzg.mccy.model;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class FailedRequest {
    private Class<? extends Throwable> cause;

    private String message;

    public FailedRequest(Class<? extends Throwable> cause, String message) {
        this.cause = cause;
        this.message = message;
    }

    public Class<? extends Throwable> getCause() {
        return cause;
    }

    public void setCause(Class<? extends Throwable> cause) {
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
