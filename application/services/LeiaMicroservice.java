package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * in addition Initialized with the duration needed to {@link DeactivationEvent} and {@link BombDestroyerEvent}
 * create the events and sends them.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private long dTime;
    private long bTime;

    public LeiaMicroservice(Attack[] attacks, long dTime, long bTime) {
        super("Leia");
        this.attacks = attacks;
        this.dTime = dTime;
        this.bTime = bTime;
    }

    /**
     * sleep for a short time to allow the other microServices to register before sending events
     */
    @Override
    protected void initialize() {
        try {
            Thread.sleep(150);
        }catch (InterruptedException e){}
    }

    public Attack[] getAttacks() {
        return attacks;
    }

    public long getbTime() {
        return bTime;
    }

    public long getdTime() {
        return dTime;
    }
}
