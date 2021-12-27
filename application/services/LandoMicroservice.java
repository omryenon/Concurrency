package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    public LandoMicroservice(long duration) {
        super("Lando");
    }

    /**
     * create an {@link CallbackBombDestroyer} and {@link CallbackBombDestroyerBroadcast}
     * the subscribe to that types of messages.
     */
    @Override
    protected void initialize() {
        CallbackBombDestroyer callbackE = new CallbackBombDestroyer();
        CallbackBombDestroyerBroadcast callbackB = new CallbackBombDestroyerBroadcast(this);

        this.subscribeEvent(BombDestroyerEvent.class, callbackE);
        this.subscribeBroadcast(BombDestroyerBroadcast.class, callbackB);
    }
}
