package me.itzg.mccy.types;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
public class MccyException extends Throwable {
    public MccyException() {
    }

    public MccyException(String message) {
        super(message);
    }

    public MccyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MccyException(Throwable cause) {
        super(cause);
    }
}
