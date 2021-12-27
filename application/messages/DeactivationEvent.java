package bgu.spl.mics.application.messages;
import bgu.spl.mics.*;

/**
 * DeactivationEvent is the class which holds the information of the deactivation
 */
public class DeactivationEvent implements Event<Boolean> {
    private long duration;

    public DeactivationEvent(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

}