package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
/**
 * a CallbackBombDestroyerBroadcast is class which all the microServices
 * who register BombDestroyerBroadcast{@link BombDestroyerBroadcast} to it unregister themselves.
 */
public class CallbackBombDestroyerBroadcast implements Callback<BombDestroyerBroadcast> {

    private MicroService microService;

    public CallbackBombDestroyerBroadcast(MicroService microService){
        this.microService = microService;
    }

    /**
     * unregister the microService and update the time in the diary.
     * update the flag{@link Flags} setAllUnregistered and notify it, so leia can continue.
     * @param c the DeactivationBroadcast.
     */
    public void call(BombDestroyerBroadcast c){
        MessageBusImpl bus = MessageBusImpl.getInstance();

        bus.unregister(getMicroService());

        Flags flags = Flags.getInstance();
        flags.setAllUnregistered(true);

        synchronized (CallbackBombDestroyerBroadcast.class){
            CallbackBombDestroyerBroadcast.class.notifyAll();
        }
    }

    public MicroService getMicroService() {
        return microService;
    }
}
