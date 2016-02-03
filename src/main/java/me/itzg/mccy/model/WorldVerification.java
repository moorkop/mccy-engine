package me.itzg.mccy.model;

import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class WorldVerification {
    private boolean valid;

    private URI resolvedUri;

    private String levelName;
    private Exception exception;
    private String failureMessage;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public URI getResolvedUri() {
        return resolvedUri;
    }

    public void setResolvedUri(URI resolvedUri) {
        this.resolvedUri = resolvedUri;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
