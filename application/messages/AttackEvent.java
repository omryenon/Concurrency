package bgu.spl.mics.application.messages;
import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * AttackEvent is the class which holds the information of the attack {@link Attack}
 */
public class AttackEvent implements Event<Boolean> {
    private Attack attack;

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public Attack getAttack() {
        return attack;
    }
}
