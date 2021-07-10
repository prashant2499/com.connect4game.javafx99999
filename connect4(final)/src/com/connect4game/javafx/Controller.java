package com.connect4game.javafx;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	public static final int rows= 7;
	public static final int columns=6;
	public static final int CircleDiameter= 80;
	public static final String Disc1Color= "#24303E";
	public static final String Disc2color= "#4CAA88";

	public static String PLAYER_ONE= "PlayerONE";
	public static String PLAYER_TWO= "PlayerTWO";

	private boolean isPlayerOneTurn=true;

	 private Disc[][] insertedDiscArray= new Disc[rows][columns];//for structural changes for developer

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Label PlayerNameLabel;
	@FXML
	public Button SetNamebtn;
	@FXML
	public TextField PlayerOneTxT;
	@FXML
	public TextField PlayerTwoTxT;

     private boolean isAllowedtoInsert= true;//flag to avoid same color disk being added
	public void createPlayground(){
		Shape rectangleWithHoles= CreateGameStructuralGrid();
         rootGridPane.add(rectangleWithHoles,0,1);
         List<Rectangle> rectangleList= clickablecoloumn();
		for (Rectangle rectangle :rectangleList
		     ) {
			rootGridPane.add(rectangle,0,1);
		}

	}


	public Shape CreateGameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((columns + 1) * CircleDiameter, (rows + 1) * CircleDiameter);

		for (int row = 0; row < rows; row++) {

			for (int col = 0; col < columns; col++) {

				Circle circle = new Circle();

				circle.setRadius(CircleDiameter / 2);
				circle.setCenterX(CircleDiameter / 2);
				circle.setCenterY(CircleDiameter / 2);
				circle.setSmooth(true);
				circle.setTranslateX(col * (CircleDiameter + 5) + CircleDiameter / 4);
				circle.setTranslateY(row * (CircleDiameter + 5) + CircleDiameter / 4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);


			}

		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;


	}
	private List<Rectangle> clickablecoloumn(){
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < columns; col++) {


		Rectangle rectangle= new Rectangle( CircleDiameter,(rows + 1) * CircleDiameter);
		rectangle.setFill(Color.TRANSPARENT);
		rectangle.setTranslateX( col *(CircleDiameter + 5) + CircleDiameter/4);
		rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
		rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
		final int column =col;
		rectangle.setOnMouseClicked(event -> {
			if(isAllowedtoInsert){
			    isAllowedtoInsert= false;//when disc is being dropped then no more disc of same color will be inserted
			insertDisc(new Disc(isPlayerOneTurn),column);}
		});
		rectangleList.add(rectangle);
		}
		return rectangleList;

	}
	private void insertDisc(Disc disc, int column){
		int row= rows-1;
		while (row>=0){
			if (getDiscIfPresent(row , column)==null)//check wheather the rows and columns are empty or not
				break;
		row--;
		}
		if (row<0){
			return;//colums are full no more disc will be inserted.
		}


		insertedDiscArray[row][column]=disc;//For Structural changes. For developers
		insertedDiscPane.getChildren().add(disc);// For visual changes for the players
		disc.setTranslateX(column * (CircleDiameter+5) + CircleDiameter/4);
		final int currentRow=row;
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5),disc);
		transition.setToY(row * (CircleDiameter + 5) + CircleDiameter / 4);
		transition.setOnFinished(event -> {
			isAllowedtoInsert = true;//Finally when the disc is dropped then allows next player to insert disc
			if (gameEnded(currentRow , column)){
				gameOver();
				return;

			}
			isPlayerOneTurn = ! isPlayerOneTurn;
			PlayerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE:PLAYER_TWO);
		});
		transition.play();

	}



	private boolean gameEnded(int Row, int Column) {

		List<Point2D> verticalPoints = IntStream.rangeClosed(Row - 3, Row + 3)  // If, row = 3, column = 3, then row = 0,1,2,3,4,5,6
				.mapToObj(r -> new Point2D(r, Column))  // 0,3  1,3  2,3  3,3  4,3  5,3  6,3 [ Just an example for better understanding ]
				.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(Column - 3, Column + 3)
				.mapToObj(col -> new Point2D(Row, col))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(Row - 3, Column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint1.add(i, -i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(Row - 3, Column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

		return isEnded;
	}


	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;

		for (Point2D point: points) {

			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {  // if the last inserted Disc belongs to the current player

				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}

		return false;
	}

	private Disc getDiscIfPresent(int row, int column) {
		if(row>=rows || row<0 || column>=columns || column<0)
			return null;
		return insertedDiscArray[row][column];
	}
	private void gameOver() {
		String winner=  isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		System.out.println("winner is: "+ winner);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText("The Winner: "+ winner);
		alert.setTitle("ConnectFOUR");
		alert.setContentText("Want To Play Again??");
		ButtonType yesbtn= new ButtonType("Yes");
		ButtonType nobtn = new ButtonType("No, Quit the game");
		alert.getButtonTypes().setAll(yesbtn,nobtn);
		 Platform.runLater(()->{


		Optional<ButtonType> clickedBtn = alert.showAndWait();
		if (clickedBtn.isPresent()&& clickedBtn.get()== yesbtn){
			// user chooses yes btn reset game
             resetGame();

		}
		else{
			//user chooses no btn ,exit the game
			Platform.exit();
			System.exit(0);
		}

	});
	}

	public void resetGame() {

		insertedDiscPane.getChildren().clear();
		for (int row = 0; row < insertedDiscArray.length; row++) {
			for (int col = 0; col < insertedDiscArray[row].length; col++) {
				insertedDiscArray[row][col]= null;

			}

		}
		isPlayerOneTurn= true;// let player one start the game
		PlayerNameLabel.setText(PLAYER_ONE);

		createPlayground();// prepares a fresh playground
	}

	private static class Disc extends Circle{
		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove= isPlayerOneMove;
		     setRadius(CircleDiameter/2);
			setFill(isPlayerOneMove? Color.valueOf(Disc1Color):Color.valueOf(Disc2color));
			setCenterX(CircleDiameter/2);
			setCenterY(CircleDiameter/2);
		}

}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SetNamebtn.setOnAction(event -> {

			String input1 = PlayerOneTxT.getText();
			String input2 = PlayerTwoTxT.getText();

			PLAYER_ONE = input1 + "`s";
			PLAYER_TWO = input2 + "`s";

			if (input1.isEmpty())
				PLAYER_ONE = "Player One`s";

			if (input2.isEmpty())
				PLAYER_TWO = "Player Two`s";

			//  isPlayerOneTurn = !isPlayerOneTurn;
			PlayerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);

		});

	}
}
