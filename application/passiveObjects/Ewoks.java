package bgu.spl.mics.application.passiveObjects;
import java.util.stream.*;

import java.util.*;

/**
 * Passive object representing the resource manager.
 * This class is a thread-safe singleton.
 * each ewok from the ewokList hold ID number which needed to the {@link bgu.spl.mics.application.messages.AttackEvent}
 */
public class Ewoks {
    private Ewok[] ewoksList;

    /**
     * a singleton implementation
     */
    private static Ewoks instance = null;
    public static Ewoks getInstance(int num){
        if (instance == null)
            instance = new Ewoks(num);
        return instance;
    }

    /**
     * a private Constructor
     * creating a list of {@link Ewok} and giving each number from 1 to "num".
     * @param num holds the number of ewoks required
     */
    private Ewoks(int num){
        ewoksList = new Ewok[num];
        for (int i = 0; i < num; i++) {
            Ewok ewok = new Ewok(i+1);
            ewoksList[i] = ewok;
        }
    }

    /**
     * recreate ewoks and manage them (in a case of creation ewoks more than once)
     */
    public void setEwoksList(int num){
        if (ewoksList.length < num){
            ewoksList = new Ewok[num];
            for (int i = 0 ; i < num; i++){
                Ewok ewok = new Ewok(i+1);
                ewoksList[i] = ewok;
            }
        }
    }

    /**
     * blocking method.
     * acquire ewoks {@link Ewoks} for an AttackEvent{@link bgu.spl.mics.application.messages.AttackEvent}.
     * if one of them is not available , waiting for him to be release.
     * @param serial hold the ID numbers of the required ewoks.
     */
    public synchronized void aquireEwoks(List<Integer> serial){
        for (Integer i: serial) {
            while(!ewoksList[i-1].isAvailable())
                try {
                    wait();
                }catch (InterruptedException ignored){}
            ewoksList[i-1].acquire();
        }
    }

    /**
     * Release ewoks {@link Ewoks} from an AttackEvent{@link bgu.spl.mics.application.messages.AttackEvent}
     * after AttackEvent is completed.
     * @param serial
     */
    public  void releaseEwoks(List<Integer> serial) {
        for (Integer i : serial) {
            ewoksList[i-1].release();
        }
        synchronized (this){notifyAll();}
    }
}


