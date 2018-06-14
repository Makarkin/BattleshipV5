package battleship.utils;

import java.io.Serializable;

public class Cell implements Serializable {
    private boolean withShip = false;
    private boolean wasShoot = false;
    private boolean isModifable = false;
    private boolean isFrozen = false;

    public boolean isFrozen() { return isFrozen; }

    public void setFrozen(boolean frozen) { isFrozen = frozen; }

    public boolean isModifable() {
        return isModifable;
    }

    public void setModifable(boolean modifable) {
        this.isModifable = modifable;
    }

    public boolean isWithShip() {
        return withShip;
    }

    public void setWithShip(boolean withShip) {
        this.withShip = withShip;
    }
}
