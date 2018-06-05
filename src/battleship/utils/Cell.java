package battleship.utils;

public class Cell {
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

    public boolean isWasShoot() {
        return wasShoot;
    }

    public void setWasShoot(boolean wasShoot) {
        this.wasShoot = wasShoot;
    }

    public boolean isWithShip() {
        return withShip;
    }

    public void setWithShip(boolean withShip) {
        this.withShip = withShip;
    }
}
