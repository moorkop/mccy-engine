package me.itzg.mccy.types;

/**
 * @author Geoff Bourne
 * @since 1/1/2016
 */
public class MccyNotFoundException extends MccyClientException {
    public MccyNotFoundException() {
    }

    public MccyNotFoundException(String message) {
        super(message);
    }

    public MccyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MccyNotFoundException(Throwable cause) {
        super(cause);
    }
}
