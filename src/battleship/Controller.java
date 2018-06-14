package battleship;

import battleship.utils.Board;
import battleship.utils.Cell;
import battleship.utils.IndexVault;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class Controller {

    private MainView mainView;
    private final int port;
    private final String intrServerAddress;
    private ClientModel clientModel;
    private List<Integer> counterList = Arrays.asList(0, 4, 7, 10, 12, 14, 16, 17, 18, 19, 20);
    private List<Integer> numberOfDeckList = Arrays.asList(4, 3, 3, 2, 2, 2, 1, 1, 1, 1);
    private ArrayList<IndexVault> itShouldFrozen = new ArrayList<>();
    private int yourSumOfDecks = 20;
    private int enemySumOfDecks = 20;
    private Integer predI = null;
    private Integer predJ = null;
    private int counter = 0;
    private int counterOfDeck = 0;
    private int numberOfShipsOnBoard = 0;
    private Board yourBoard = new Board();
    static boolean yourTurn = false;

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public int getPort() {
        return port;
    }

    public String getIntrServerAddress() {
        return intrServerAddress;
    }

    public MainView getMainView() {
        return mainView;
    }

    public Board getYourBoard() {
        return yourBoard;
    }

    Controller(String intrServerAddres, int port, MainView mainView) {
        this.port = port;
        this.intrServerAddress = intrServerAddres;
        this.mainView = mainView;
    }

    void start(ActionEvent actionEvent) throws IOException {
        if (numberOfShipsOnBoard == 1) {
            enemySumOfDecks = 4;
            yourSumOfDecks = 4;
        System.out.println("Start");
        this.clientModel = new ClientModel(this);
        this.clientModel.start();
        }
    }

    void makeShot(MouseEvent mouseEvent) throws IOException {
        if (yourTurn) {
            yourTurn = false;
            Rectangle rectangle = (Rectangle) mouseEvent.getSource();
            Integer i = GridPane.getRowIndex(rectangle);
            Integer j = GridPane.getColumnIndex(rectangle);
            rectangle.setFill(BLACK);
            String fireCoordinates = "fire" + " " + i + " " + j;
            clientModel.transferFire(fireCoordinates);
        }
    }

    void acceptResult(String[] result) {
        int i = Integer.valueOf(result[0]);
        int j = Integer.valueOf(result[1]);
        Rectangle rectangle = (Rectangle) mainView.getEnemyBoard().getChildren().get(i+j*10+1);
        rectangle.setFill(RED);
        enemySumOfDecks--;
        if (enemySumOfDecks == 0) {
            Platform.runLater(() -> mainView.getGameMessage().setText("You win!"));
            clientModel.transferVictoryMessage();
        }
    }

    public void acceptFalseResult(String[] result) {
        int i = Integer.valueOf(result[0]);
        int j = Integer.valueOf(result[1]);
        Rectangle rectangle = (Rectangle) mainView.getEnemyBoard().getChildren().get(i+j*10+1);
        rectangle.setFill(BLACK);
    }

     void getShot(String[] result) {
         yourTurn = true;
         int i = Integer.valueOf(result[0]);
         int j = Integer.valueOf(result[1]);
         Rectangle rectangle = (Rectangle) mainView.getYourBoard().getChildren().get(i+j*10+1);
         rectangle.setFill(RED);
         yourSumOfDecks--;
         if (yourSumOfDecks == 0) {
             Platform.runLater(()-> mainView.getGameMessage().setText("You lose!"));
             clientModel.transferLoseMessage();
         }
     }

    void getShotPast(String[] result) {
        yourTurn = true;
        int i = Integer.valueOf(result[0]);
        int j = Integer.valueOf(result[1]);
        Rectangle rectangle = (Rectangle) mainView.getYourBoard().getChildren().get(i+j*10+1);
        rectangle.setFill(BLACK);
    }

    void placeShip(MouseEvent mouseEvent) {
        MouseButton mouseButton = mouseEvent.getButton();
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        Integer i = GridPane.getRowIndex(rectangle);
        Integer j = GridPane.getColumnIndex(rectangle);
        if (i == null) {
            i = 0;
        }

        if (j == null) {
            j = 0;
        }

        Cell probeCell = yourBoard.getIndexCell(i, j);

        if (mouseButton == PRIMARY) {

            if (counterList.contains(counter) && !hasShipsNear(i, j, yourBoard)) {
                probeCell.setModifable(true);
            }

            if (!probeCell.isWithShip() && probeCell.isModifable() && counter < 20
                    && checkVerticalShipPlaceConstraint(i, j, predI, predJ)
                    && !probeCell.isFrozen()) {
                addCellWithShip(rectangle, i, j);
                if (counterOfDeck < numberOfDeckList.get(numberOfShipsOnBoard)) {
                    if (i > 0 && i < 9) {
                        if (!yourBoard.getIndexCell(i - 1, j).isFrozen()) yourBoard.getIndexCell(i - 1, j).setModifable(true);
                        if (!yourBoard.getIndexCell(i + 1, j).isFrozen()) yourBoard.getIndexCell(i + 1, j).setModifable(true);
                    }
                    if (i == 0) {
                        if (!yourBoard.getIndexCell(i + 1, j).isFrozen()) yourBoard.getIndexCell(i + 1, j).setModifable(true);
                    }
                    if (i == 9) {
                        if (!yourBoard.getIndexCell(i - 1, j).isFrozen()) yourBoard.getIndexCell(i - 1, j).setModifable(true);
                    }
                } else {
                    afterOneShipPlace();
                }
            }
        } else if (mouseButton == SECONDARY) {

            if (counterList.contains(counter) && !hasShipsNear(i, j, yourBoard)) {
                probeCell.setModifable(true);
            }

            if (!probeCell.isWithShip() && probeCell.isModifable() && counter < 20
                    && checkHorizontalShipPlaceConstraint(i,j, predI, predJ)
                    && !probeCell.isFrozen()) {
                addCellWithShip(rectangle, i, j);
                if (counterOfDeck < numberOfDeckList.get(numberOfShipsOnBoard)) {
                    if (j > 0 && j < 9) {
                        if (!yourBoard.getIndexCell(i, j - 1).isFrozen()) yourBoard.getIndexCell(i, j - 1).setModifable(true);
                        if (!yourBoard.getIndexCell(i, j + 1).isFrozen()) yourBoard.getIndexCell(i, j + 1).setModifable(true);
                    }
                    if (j == 0) {
                        if (!yourBoard.getIndexCell(i, j + 1).isFrozen()) yourBoard.getIndexCell(i, j + 1).setModifable(true);
                    }
                    if (j == 9) {
                        if (!yourBoard.getIndexCell(i, j - 1).isFrozen()) yourBoard.getIndexCell(i, j - 1).setModifable(true);
                    }
                } else {
                    afterOneShipPlace();
                }
            }
        }
    }

    private void addCellWithShip(Rectangle rectangle, int i, int j) {
        rectangle.setFill(GREEN);
        counterOfDeck++;
        counter++;
        Cell cell = new Cell();
        cell.setWithShip(true);
        cell.setModifable(false);
        yourBoard.setIndexCell(cell, i, j);
        itShouldFrozen.add(new IndexVault(i, j));
    }

    private void afterOneShipPlace() {
        numberOfShipsOnBoard++;
        counterOfDeck = 0;
        freezeCell(itShouldFrozen, yourBoard);
        itShouldFrozen.clear();
        if (10 - numberOfShipsOnBoard > 0) {
        mainView.getGameMessage().setText("You have left to place " + (10 - numberOfShipsOnBoard) + " ships");
        } else { mainView.getGameMessage().setText("Press start");
        }

    }

    private static boolean checkVerticalShipPlaceConstraint(int rowCounter, int columnCounter, Integer predI, Integer predJ) {
        if (predI == null) {
            predI = rowCounter;
        }

        if (predJ == null) {
            predJ = columnCounter;
        }

        if (rowCounter == predI - 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI - 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI && columnCounter == predJ + 1) {
            return false;
        }

        predI = rowCounter;
        predJ = columnCounter;
        return true;
    }

    private static boolean checkHorizontalShipPlaceConstraint(int rowCounter, int columnCounter, Integer predI, Integer predJ) {
        if (predI == null) {
            predI = rowCounter;
        }

        if (predJ == null) {
            predJ = columnCounter;
        }

        if (rowCounter == predI - 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI - 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI - 1 && columnCounter == predJ) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ) {
            return false;
        }

        predI = rowCounter;
        predJ = columnCounter;
        return true;
    }

    private static boolean hasShipsNear(int rowIndex, int columnIndex, Board yourBoard) {
        int counter = 0;
        Cell[][] cells = yourBoard.getCells();
        int imax,imin,jmax,jmin;

        if (rowIndex == 0) {
            imin = rowIndex;
        } else {
            imin = rowIndex - 1;
        }
        if (rowIndex == 9) {
            imax = rowIndex;
        } else {
            imax = rowIndex + 1;
        }
        if (columnIndex == 0) {
            jmin = columnIndex;
        } else {
            jmin = columnIndex - 1;
        }
        if (columnIndex == 9) {
            jmax = columnIndex;
        } else {
            jmax = columnIndex + 1;
        }

        for (int i = imin; i <= imax; i++) {
            for (int j = jmin; j <= jmax; j++) {
                if (cells[i][j].isWithShip()) {
                    counter++;
                }
            }
        }

        return counter > 0;
    }

    private static void freezeCell(ArrayList<IndexVault> itShouldFrozen, Board yourBoard) {
        IndexVault vaultFirst = itShouldFrozen.get(0);
        IndexVault vaultLast = itShouldFrozen.get(itShouldFrozen.size() - 1);
        IndexVault first = IndexVault.getHigherVault(vaultFirst, vaultLast);
        IndexVault last = IndexVault.getLowerVault(vaultFirst, vaultLast);

        int imin = first.getI();
        int imax = last.getI();
        int jmin = first.getJ();
        int jmax = last.getJ();

        if (imin != 0) {
            imin -= 1;
        }

        if (imax != 9) {
            imax += 1;
        }

        if (jmin != 0) {
            jmin -= 1;
        }

        if (jmax != 9) {
            jmax += 1;
        }

        for (int i = imin; i <= imax; i++) {
            for (int j = jmin; j <= jmax; j++) {
                yourBoard.getIndexCell(i, j).setFrozen(true);
            }
        }
    }
}
