package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
/**
 * a CallBackDeactivation is class where all the DeactivationEvent{@link DeactivationEvent} process is taking place.
 */
public class CallBackDeactivation implements Callback<DeactivationEvent> {

    /**
     * sleep for a given time in the  DeactivationEvent{@link DeactivationEvent}
     * when finish sleeping complete {@link #complete(bgu.spl.mics.application.messages.DeactivationEvent)}
     * and finally update the dairy {@link Diary} and update the flag{@link Flags} setDeactivated so leia can continue.
     *
     * @param c the DeactivationEvent {@link DeactivationEvent} of the current callback.
     */
    public void call(DeactivationEvent c){
        try {
            Thread.sleep(c.getDuration());
        }catch (InterruptedException ignored){}
        complete(c);

        Flags flags = Flags.getInstance();
        flags.setDeactivated(true);
    }

    private void complete(DeactivationEvent c) {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        bus.complete(c , true);
    }
}
