package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.*;
import bgu.spl.mics.application.services.*;

import java.util.concurrent.atomic.*;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * holds the information for the output file and get update during the microServices run.
 */
public class Diary {
    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    /**
     * a singleton implementation
     */
    private static Diary instance = null;
    public static Diary getInstance(){
        if (instance == null)
            instance = new Diary();
        return instance;
    }

    /**
     * a private Constructor
     */
    private Diary() {
        this.totalAttacks = new AtomicInteger(0);

    }

    public void setTotalAttacks() {
        totalAttacks.getAndIncrement();
    }

    public int getTotalAttacks() {
        return totalAttacks.intValue();
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    /**
     * Set the correct's Microservice temination time
     * @param m
     */
    public void setTerminiate(MicroService m){
        if (m instanceof HanSoloMicroservice)
            setHanSoloTerminate(System.currentTimeMillis());
        else if (m instanceof C3POMicroservice)
            setC3POTerminate(System.currentTimeMillis());
        else if (m instanceof R2D2Microservice)
            setR2D2Terminate(System.currentTimeMillis());
        else if (m instanceof LandoMicroservice)
            setLandoTerminate(System.currentTimeMillis());
        else
            setLeiaTerminate(System.currentTimeMillis());

    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public AtomicInteger getNumberOfAttacks() {
        return totalAttacks;
    }

    public void resetNumberAttacks() {
        totalAttacks.set(0);
    }

    /**
     * reset all the fields to 0.
     */
    public void resetDairy(){
        totalAttacks.set(0);
        setHanSoloFinish(0);
        setC3POFinish(0);
        setR2D2Deactivate(0);
        setHanSoloTerminate(0);
        setC3POTerminate(0);
        setR2D2Terminate(0);
        setLandoTerminate(0);
        setLeiaTerminate(0);

    }
}
