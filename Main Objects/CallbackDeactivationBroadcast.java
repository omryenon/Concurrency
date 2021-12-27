package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
/**
 * a CallbackDeactivationBroadcast is class which all the microServices
 * who register DeactivationBroadcast{@link DeactivationBroadcast} to it unregister themselves.
 */
public class CallbackDeactivationBroadcast implements Callback<DeactivationBroadcast> {

    private MicroService microService;

    public CallbackDeactivationBroadcast(MicroService microService){
        this.microService = microService;
    }

    /**
     * unregister the microService and update the time in the diary.
     * @param c the DeactivationBroadcast.
     */
    public void call(DeactivationBroadcast c){
        MessageBusImpl bus = MessageBusImpl.getInstance();
        Diary diary = Diary.getInstance();

        bus.unregister(getMicroService());
        diary.setR2D2Deactivate(System.currentTimeMillis());
    }

    public MicroService getMicroService() {
        return microService;
    }
}
