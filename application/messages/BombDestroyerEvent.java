package bgu.spl.mics.application.messages;
import bgu.spl.mics.*;

/**
 * BombDestroyerEvent is the class which holds the information of the BombDestroyer
 */
public class BombDestroyerEvent implements Event<Boolean> {

    private long duration;

    public BombDestroyerEvent(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }
}