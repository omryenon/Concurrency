package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;

/**
 * a CallbackAttack is class where all the attackEvent{@link AttackEvent} process is taking place.
 */

public class CallbackAttack implements Callback<AttackEvent> {

    /**
     * aquire the ewoks {@link Ewoks} which needed to the attack (from a blocking method)
     * then sleep for a given time in the attack {@link Attack}
     * when finish sleeping release the ewoks and complete {@link #complete(bgu.spl.mics.application.messages.AttackEvent)}
     * and finally update the dairy {@link Diary}
     *
     * @param c the attackEvent {@link AttackEvent} of the current callback.
     */
    @Override
    public void call(AttackEvent c)  {
        Ewoks ewoks = Ewoks.getInstance(0);
        Attack attack = c.getAttack();
        List<Integer> serials = attack.getSerials();
        ewoks.aquireEwoks(serials);

        try {
            Thread.sleep(attack.getDuration());
        } catch (InterruptedException ignored) {
        }
        ewoks.releaseEwoks(serials);
        complete(c);

        Diary diary = Diary.getInstance();
        diary.setTotalAttacks();
        }

    private void complete(AttackEvent c) {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        bus.complete(c , true);
    }

}

