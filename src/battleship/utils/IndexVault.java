package battleship.utils;

public class IndexVault {
    private int i;
    private int j;

    public static IndexVault getLowerVault(IndexVault indexVault1, IndexVault indexVault2) {
        if (indexVault1.getI() > indexVault2.getI()) {
            return indexVault1;
        } else return indexVault2;
    }

    public static IndexVault getHigherVault(IndexVault indexVault1, IndexVault indexVault2) {
        if (indexVault1.getI() < indexVault2.getI()) {
            return indexVault1;
        } else return indexVault2;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public IndexVault(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
