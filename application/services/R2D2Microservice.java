package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    public R2D2Microservice(long duration) {
        super("R2D2");
    }

    /**
     * create an {@link CallBackDeactivation} and {@link CallbackDeactivationBroadcast}
     * the subscribe to that types of messages.
     */
    @Override
    protected void initialize() {
        CallBackDeactivation callbackE = new CallBackDeactivation();
        CallbackDeactivationBroadcast callbackB = new CallbackDeactivationBroadcast(this);

        this.subscribeEvent(DeactivationEvent.class, callbackE);
        this.subscribeBroadcast(DeactivationBroadcast.class, callbackB);

    }
}
