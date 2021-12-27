package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;

import java.util.concurrent.*;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {


    public C3POMicroservice( ) {
        super("C3PO");
    }

    /**
     * create an {@link CallbackAttack} and {@link CallbackAttackBroadcast}
     * the subscribe to that types of messages.
     */
    @Override
    protected void initialize() {
        CallbackAttack callbackE = new CallbackAttack();
        CallbackAttackBroadcast callbackB = new CallbackAttackBroadcast(this);

        this.subscribeEvent(AttackEvent.class, callbackE);
        this.subscribeBroadcast(AttackBroadcast.class, callbackB);
    }
}
