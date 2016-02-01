package me.itzg.mccy.types;

/**
 * @author Geoff Bourne
 * @since 1/31/2016
 */
public class MccyUnexpectedServerException extends MccyException {
    public MccyUnexpectedServerException() {
    }

    public MccyUnexpectedServerException(String message) {
        super(message);
    }

    public MccyUnexpectedServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MccyUnexpectedServerException(Throwable cause) {
        super(cause);
    }
}
