package bgu.spl.mics.application.passiveObjects;

import java.util.List;


/**
 * Passive data-object representing an attack object.
 * You must not alter any of the given public methods of this class.
 * <p>
 * YDo not add any additional members/method to this class (except for getters).
 */
public class Attack {
    final List<Integer> serials;
    final int duration;

    /**
     * Constructor
     * sorting the serial numbers, for synchronazation.
     * @param serialNumbers represent the required ewoks for the attack.
     * @param duration represent the required sleep time for the attack.
     */
    public Attack(List<Integer> serialNumbers, int duration) {
        serialNumbers.sort(Integer::compareTo);
        this.serials = serialNumbers;
        this.duration = duration;
    }

    public List<Integer> getSerials(){
        return serials;
    }

    public int getDuration(){
        return duration;
    }

}
