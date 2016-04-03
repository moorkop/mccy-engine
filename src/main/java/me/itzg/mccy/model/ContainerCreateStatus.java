package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Conveys the ongoing status of a container's creation.
 *
 * @author Geoff Bourne
 * @since 0.2
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerCreateStatus {
    private State state;

    private String details;

    private PullDetails pullDetails;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public PullDetails getPullDetails() {
        return pullDetails;
    }

    public void setPullDetails(PullDetails pullDetails) {
        this.pullDetails = pullDetails;
    }

    public enum State {
        INIT,
        PULL,
        CREATE,
        START,
        READY,
        ERROR
    }

    public static class PullDetails {
        private String imageId;

        private long start;
        private long current;
        private long total;

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getCurrent() {
            return current;
        }

        public void setCurrent(long current) {
            this.current = current;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }
}
