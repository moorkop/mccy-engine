package me.itzg.mccy.types;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class MccyInvalidFormatException extends MccyClientException {
    public MccyInvalidFormatException() {
    }

    public MccyInvalidFormatException(String message) {
        super(message);
    }

    public MccyInvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MccyInvalidFormatException(Throwable cause) {
        super(cause);
    }
}
