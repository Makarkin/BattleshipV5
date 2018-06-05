package battleship.utils;

public class Board {
    private Cell[][] cells = new Cell[10][10];

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell getIndexCell(int i, int j) {
        return cells[i][j];
    }

    public void setIndexCell(Cell cell, int i, int j) {
        cells[i][j] = cell;
    }

    public Board() {
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {
                cells[i][j] = new Cell();
            }
        }

    }
}
