package battleship;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    private Button startButton;

private void addRectangle(GridPane gridPane) {
    gridPane.setGridLinesVisible(true);
    gridPane.setHgap(1);
    gridPane.setVgap(1);
    for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
            gridPane.add(new Rectangle(30,30,Color.LIGHTGREY), i, j);
        }
    }
}

private void setActionsOnEnemyBoard(GridPane gridPane) {
    for (Node element : gridPane.getChildren()) {
        element.setOnMouseClicked(e -> {
            controller.makeShot(e);
        });
    }
}

    private void setActionsOnYourBoard(GridPane gridPane) {
        for (Node element : gridPane.getChildren()) {
            element.setOnMouseClicked(e -> {
                controller.placeShip(e);
            });
        }
    }

    private void setActionsOnYourButton(Button button) {
        button.setOnAction(event -> {
            try {
                controller.runToServer(event);
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
        enemyBoard.setPrefSize(392, 314);
        addRectangle(enemyBoard);
        setActionsOnEnemyBoard(enemyBoard);
        startButton.setPrefSize(50, 30);
        setActionsOnYourButton(startButton);
        startButton.relocate(460, 400);
        yourBoard.relocate(100,100);
        youLabel.relocate(100,60);
        enemyBoard.relocate(600, 100);
        enemyLabel.relocate(600,60);
        youLabel.setFont(Font.font(18));
        enemyLabel.setFont(Font.font(18));
        parentPane.getChildren().addAll(yourBoard, enemyBoard, youLabel, enemyLabel, startButton);
        return parentPane;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        parentPane = new Pane();
        yourBoard = new GridPane();
        enemyBoard = new GridPane();
        youLabel = new Label();
        enemyLabel = new Label();
        startButton = new Button();

        enemyLabel.setText("Enemy");
        startButton.setText("Start");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your nickname");
        youLabel.setText("Stas"/*scanner.nextLine()*/);
        System.out.println("Enter addres of server");
        String address = "127.0.0.1"/*scanner.nextLine()*/;
        System.out.println("Enter port of server");
        int port = 6667/*Integer.parseInt(scanner.nextLine())*/;
        controller = new Controller(address, port, youLabel.getText());

        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Battleship Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
