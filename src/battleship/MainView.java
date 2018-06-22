package battleship;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class MainView extends Application {

    private Controller controller;

    private Pane parentPane;
    private GridPane yourBoard;
    private GridPane enemyBoard;
    private Label youLabel;
    private Label enemyLabel;
    private Label gameMessage;
    private Label help;
    private Button startButton;

    public Label getHelp() {
        return help;
    }

    public Button getStartButton() {

        return startButton;
    }

    GridPane getYourBoard() {
        return yourBoard;
    }

    GridPane getEnemyBoard() {
        return enemyBoard;
    }

    Label getYouLabel() {
        return youLabel;
    }

    Label getEnemyLabel() {
        return enemyLabel;
    }

    Label getGameMessage() {
        return gameMessage;
    }

    private void addRectangle(GridPane gridPane) {
        gridPane.setGridLinesVisible(true);
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {
                gridPane.add(new Rectangle(30, 30, Color.LIGHTGREY), i, j);
            }
        }
    }

    private void setActionsOnEnemyBoard(GridPane gridPane) {
        for (Node element : gridPane.getChildren()) {
            if (element.getClass() == Rectangle.class) {
                element.setOnMouseClicked(e -> {
                    try {
                        controller.makeShot(e);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
            }
        }
    }

    private void setActionsOnYourBoard(GridPane gridPane) {
        for (Node element : gridPane.getChildren()) {
            if (element.getClass() == Rectangle.class) {
                element.setOnMouseClicked(e -> controller.placeShip(e));
            }
        }
    }

    private void setActionsOnYourButton(Button button) {
        button.setOnAction(event -> {
            try {
                controller.start(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Parent createContent() {
        parentPane.setPrefSize(1000, 600);

        yourBoard.setPrefSize(392, 314);
        addRectangle(yourBoard);
        setActionsOnYourBoard(yourBoard);
        yourBoard.relocate(20,150);

        enemyBoard.setPrefSize(392, 314);
        addRectangle(enemyBoard);
        setActionsOnEnemyBoard(enemyBoard);
        enemyBoard.relocate(680, 150);

        startButton.setPrefSize(200, 30);
        setActionsOnYourButton(startButton);
        startButton.relocate(400, 400);
        startButton.setDisable(true);


        youLabel.relocate(100,100);
        youLabel.setFont(Font.font(18));

        enemyLabel.relocate(700,100);
        enemyLabel.setFont(Font.font(18));

        gameMessage.relocate(420, 200);
        gameMessage.setFont(Font.font(18));

        help.relocate(20,480);
        help.setFont(Font.font(18));

        parentPane.getChildren().addAll(yourBoard, enemyBoard, youLabel, enemyLabel, gameMessage, startButton, help);
        return parentPane;
    }

    @Override
    public void start(Stage primaryStage) {
        parentPane = new Pane();
        yourBoard = new GridPane();
        enemyBoard = new GridPane();
        youLabel = new Label();
        enemyLabel = new Label();
        gameMessage = new Label();
        help = new Label();
        startButton = new Button();

        enemyLabel.setText("Enemy");
        startButton.setText("Connect with server");
        gameMessage.setText("Place your 4-deck ship");
        help.setText("Right mouse click for vertical ship\nLeft mouse click for horizontal ship");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your nickname");
        youLabel.setText(scanner.nextLine());
        System.out.println("Enter addres of server");
        String address = "127.0.0.1"/*scanner.nextLine()*/;
        System.out.println("Enter port of server");
        int port = 6667/*Integer.parseInt(scanner.nextLine())*/;
        controller = new Controller(address, port,this);

        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Battleships Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
