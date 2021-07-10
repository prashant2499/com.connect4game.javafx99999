package com.connect4game.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader= new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);//got the reference to our menupane which is basically the top pane within the rootGridPane

        controller= loader.getController();
        controller.createPlayground();
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());// extends the width of the menubar to cover the whole area of menu bar
        menuPane.getChildren().add(menuBar);// to add menubar at the menupane(topmost pane)
        Scene scene = new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ConnectFOUR");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    private MenuBar createMenu(){
        Menu Filemenu= new Menu("File");
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> {
            controller.resetGame();
        });
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> {
            controller.resetGame();
        });
        SeparatorMenuItem separatorMenuItem =new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> {
            ExitGame();
        });

        Filemenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);

        Menu helpMenu= new Menu("About");
        MenuItem aboutgame= new MenuItem("About the game");
        aboutgame.setOnAction(event -> {
            Aboutconnect4();
        });
        SeparatorMenuItem separatorMenuItem1 =new SeparatorMenuItem();
        MenuItem aboutdeveloper = new MenuItem("About Developer");
        aboutdeveloper.setOnAction(event -> {
            aboutme();
        });
        helpMenu.getItems().addAll(aboutgame,separatorMenuItem1,aboutdeveloper);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(Filemenu,helpMenu);
        return menuBar;



    }

    private void aboutme() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("PRASHANT KUMAR MISHRA");
        alert.setTitle("About The Developer");
        alert.setContentText("I am a engineering student and i have interests in making games from scratch,ConnectFOUR is one of them.I hope you like it!!\n" +
                "CONTACT me at: pkmishra.848@gmail.com");

        alert.show();
    }

    private void Aboutconnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("How To Play?");
        alert.setTitle("connectFOUR RULES");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and \n" +
                "then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. \n" +
                "The pieces fall straight down, occupying the next available space within the column.\n " +
                "The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs.\n Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.");
        alert.show();
    }

    private void ExitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetgame() {

    }


    public static void main(String[] args) {
        launch(args);
    }
}
