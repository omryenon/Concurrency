package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
/**
 * a CallbackBombDestroyer is class where all the BombDestroyerEvent{@link BombDestroyerEvent} process is taking place.
 */
public class CallbackBombDestroyer implements Callback<BombDestroyerEvent> {
    /**
     * sleep for a given time in the  BombDestroyerEvent{@link BombDestroyerEvent}
     * when finish sleeping complete {@link #complete(bgu.spl.mics.application.messages.BombDestroyerEvent)}
     * and finally update the dairy {@link Diary} and update the flag{@link Flags} setBombed so leia can continue.
     *
     * @param c the DeactivationEvent {@link BombDestroyerEvent} of the current callback.
     */
    public void call(BombDestroyerEvent c){
        try {
            Thread.sleep(c.getDuration());
        }catch (InterruptedException ignored){}
        complete(c);

        Flags flags = Flags.getInstance();
        flags.setBombed(true);
    }

    private void complete(BombDestroyerEvent c) {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        bus.complete(c , true);
    }
}
