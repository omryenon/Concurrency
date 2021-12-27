package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;

/**
 * a CallbackAttackBroadcast is class which all the microServices
 * who register AttackBroadcast{@link AttackBroadcast} to it unregister themselves.
 */
public class CallbackAttackBroadcast implements Callback<AttackBroadcast> {
    private MicroService microService;

    public CallbackAttackBroadcast(MicroService microService){
        this.microService = microService;
    }

    /**
     * unregister the microService and update the time in the diary.
     * @param c the AttackBroadcast.
     */
    public void call(AttackBroadcast c){
        MessageBusImpl bus = MessageBusImpl.getInstance();
        Diary diary = Diary.getInstance();
        bus.unregister(getMicroService());
        if (microService instanceof HanSoloMicroservice)
            diary.setHanSoloFinish(System.currentTimeMillis());
        else
            diary.setC3POFinish(System.currentTimeMillis());
    }

    public MicroService getMicroService() {
        return microService;
    }
}
