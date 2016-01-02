package me.itzg.mccy.types;

/**
 * Class of exceptions that are induced by an incorrect client request.
 *
 * @author Geoff Bourne
 * @since 1/1/2016
 */
public abstract class MccyClientException extends MccyException {
    public MccyClientException() {
    }

    public MccyClientException(String message) {
        super(message);
    }

    public MccyClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public MccyClientException(Throwable cause) {
        super(cause);
    }
}
