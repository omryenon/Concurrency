package bgu.spl.mics.application.passiveObjects;

/**
 * Flags class hold boolean flags which give information to the microservice
 * which actions occur and which yet.
 * isDeactivated turn true only after R2D2 finish {@link bgu.spl.mics.application.messages.DeactivationEvent}.
 * isBombed turn true only after Lando finish {@link bgu.spl.mics.application.messages.BombDestroyerEvent}.
 * allUnregistered turn true only all microService unregistered.
 * canTerminate turn true only after Leia use the function terminate.
 */
public class Flags {
    private boolean isDeactivated;
    private boolean isBombed;
    private boolean allUnregistered;
    private boolean canTerminate;

    /**
     * a singleton implementation
     */
    private static Flags instance = null;
    public static Flags getInstance(){
        if (instance == null)
            instance = new Flags();
        return instance;
    }

    /**
     * a private Constructor
     */
    private Flags() {
        this.isDeactivated = false;
        this.isBombed = false;
        this.canTerminate = false;
        this.allUnregistered = false;
    }

    public boolean isDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(boolean deactivated) {
        isDeactivated = deactivated;
    }

    public boolean isBombed() {
        return isBombed;
    }

    public void setBombed(boolean bombed) {
        isBombed = bombed;
    }

    public boolean isCanTerminate() {
        return canTerminate;
    }

    public void setCanTerminate(boolean canTerminate) {
        this.canTerminate = canTerminate;
    }

    public boolean isAllUnregistered() {
        return allUnregistered;
    }

    public void setAllUnregistered(boolean allUnregistered) {
        this.allUnregistered = allUnregistered;
    }

    /**
     * reset all the fields to false
     */
    public void resetAll(){
        setAllUnregistered(false);
        setBombed(false);
        setCanTerminate(false);
        setDeactivated(false);
    }
}
