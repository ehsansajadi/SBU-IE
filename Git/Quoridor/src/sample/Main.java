package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main extends Application {
    Coordinates[] playerStartCoordinates = new Coordinates[4];
    Stage primaryStage;
    Button button2P, button4P, buttonReturnMenu, buttonPlayAgain, button2PAI, button4PAI, buttonP2AIs, buttonCreateNewAI, buttonReadAIFile, buttonWriteAIFile;
    Player p0, p1, p2, p3;
    boolean twoP, registerCell = false, movePlayer = false, gameFinished = false, aiVSai = false;
    int defaultDistanceFactor = -75, defaultNowFactor = -17, defaultMoveFactor = 10, defaultRepeatFactor = 120;
    int aiOneNumber, aiTwoNumber, scheduleI, scheduleJ, turnCounter;
    int windowWidth = 1100, windowHeight = 900, initialX = 10, initialY = 10, playerRadius = 40, randomFactor = 150,
            cellWidth = 80, cellHeight = 80, wallWidth = 180, wallHeight = 20, gap = 20, maxWalls, turn, populationSize = 8;
    int[][] aiList, gameSchedule = null;
    int[][] aiScores = new int[populationSize][2];
    private Brick clickedBrick = null;
    private Cell clickedCell = null;
    private Label turnLabel, nowp0Label, nowp1Label, nowp2Label, nowp3Label, wallsLeftLabel;
    File FileName = new File("List.txt");
    Move aiMove;
    Group gameBoard;
    Move aiLastMove = null;
    Scanner scanner = new Scanner(System.in);

    public boolean[][] emptyJoints = new boolean[8][8];
    private boolean[][] searchedCells = new boolean[9][9];
    public Cell[][] cells = new Cell[9][9];
    public Brick[][] horizontalBricks = new Brick[8][9];
    public Brick[][] verticalBricks = new Brick[9][8];


    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        defaultReadAIFile();
        initiateValues(horizontalBricks, verticalBricks);
        primaryStage.setTitle("Quoridor");
        this.primaryStage = primaryStage;
        primaryStage.getIcons().add(new Image("Resource/icon.png"));
        makeMenu(primaryStage);

    }

    private void initiateValues(Brick[][] hB, Brick[][] vB) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 9; j++) {
                hB[i][j] = new Brick();
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 8; j++) {
                vB[i][j] = new Brick();
            }
        }
    }

    private void makeMenu(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome to Quoridor");

        button2P = new Button("Play 2 player");
        button4P = new Button("Play 4 player");
        button2PAI = new Button("Play 2 player AI");
        button4PAI = new Button("Play 4 player with AI.");
        buttonP2AIs = new Button("AI vs AI");
        buttonCreateNewAI = new Button("Create new generation of AIs.");
        buttonReadAIFile = new Button("Read AI list.");
        buttonWriteAIFile = new Button("Write current AI List to file");

        button2P.setOnAction(e -> start2Player(primaryStage));
        button4P.setOnAction(e -> start4Player(primaryStage));
        button2PAI.setOnAction(e -> start2PAI(primaryStage));
        button4PAI.setOnAction(e -> start4PAI(primaryStage));
        buttonP2AIs.setOnAction(e -> start2PAIs(primaryStage));
        buttonCreateNewAI.setOnAction(e -> createNewAI());
        buttonReadAIFile.setOnAction(e -> readAIFile());
        buttonWriteAIFile.setOnAction(e -> writeAIFile());

        VBox menuLayout = new VBox(50);
        menuLayout.setAlignment(Pos.TOP_CENTER);
        menuLayout.getChildren().addAll(welcomeLabel, button2P, button4P, button2PAI, button4PAI, buttonP2AIs,
                buttonCreateNewAI, buttonReadAIFile, buttonWriteAIFile);
        Scene menu = new Scene(menuLayout, windowWidth, windowHeight);
        primaryStage.setScene(menu);
        primaryStage.show();
    }

    void makeGameSchedule() {
        gameSchedule = new int[2][12];
        for (int i = 0; i < 2; i++) {
            gameSchedule[i][0] = i * 4;
            gameSchedule[i][1] = i * 4 + 1;
            gameSchedule[i][2] = i * 4;
            gameSchedule[i][3] = i * 4 + 2;
            gameSchedule[i][4] = i * 4;
            gameSchedule[i][5] = i * 4 + 3;
            gameSchedule[i][6] = i * 4 + 1;
            gameSchedule[i][7] = i * 4 + 2;
            gameSchedule[i][8] = i * 4 + 1;
            gameSchedule[i][9] = i * 4 + 3;
            gameSchedule[i][10] = i * 4 + 2;
            gameSchedule[i][11] = i * 4 + 3;
        }
        for (int i = 0; i < aiScores.length; i++) {
            aiScores[i][0] = 0;
            aiScores[i][1] = 0;
        }
        scheduleI = 0;
        scheduleJ = 0;
    }

    private void makeEndGameMenu() {
        Label label;
        if (turn == 0)
            label = new Label("Blue won.");
        else if (turn == 1)
            label = new Label("Red won.");
        else if (turn == 2)
            label = new Label("Green won.");
        else
            label = new Label("Yellow won.");
        buttonPlayAgain = new Button();
        buttonPlayAgain.setText("Play Again");
        buttonReturnMenu = new Button();
        buttonReturnMenu.setText("Return to main menu");

        VBox menuLayout = new VBox(200);
        menuLayout.setAlignment(Pos.TOP_CENTER);

        menuLayout.getChildren().addAll(label, buttonPlayAgain, buttonReturnMenu);
        Scene endGameMenu = new Scene(menuLayout, windowWidth, windowHeight);
        primaryStage.setScene(endGameMenu);

        buttonPlayAgain.setOnMouseClicked(e -> {
            if (twoP)
                start2Player(primaryStage);
            else
                start4Player(primaryStage);
        });

        buttonReturnMenu.setOnMouseClicked(e -> makeMenu(primaryStage));
    }

    private void playerClickedHandler(Player player) {
        player.setOnMouseClicked(e -> {
            if (turn == player.playerNumber)
                registerCell = true;
        });
    }

    private Group makeGameBoard() {
        if (twoP)
            turn = (int) (Math.random() * 2);
        else
            turn = (int) (Math.random() * 4);
        gameBoard = new Group();
        for (int i = 0; i < 81; i++) {
            int coloumn = i % 9;
            int row = i / 9;
            Coordinates c = new Coordinates(initialX + (coloumn * (cellWidth + gap)), initialY + (row * (cellWidth + gap)));
            cells[row][coloumn] = new Cell(c, cellWidth, Color.color(0.82, 0.41, 0.12), row, coloumn);
            gameBoard.getChildren().add(cells[row][coloumn]);
            if (i == 76)
                playerStartCoordinates[0] = new Coordinates(c.x + cellWidth / 2, c.y + cellWidth / 2);
            else if (i == 4)
                playerStartCoordinates[1] = new Coordinates(c.x + cellWidth / 2, c.y + cellWidth / 2);
            else if (i == 36)
                playerStartCoordinates[2] = new Coordinates(c.x + cellWidth / 2, c.y + cellWidth / 2);
            else if (i == 44)
                playerStartCoordinates[3] = new Coordinates(c.x + cellWidth / 2, c.y + cellWidth / 2);
            if (!twoP && (i == 76 || i == 4 || i == 36 || i == 44))
                cells[row][coloumn].isFilled = true;
            else if (twoP && (i == 76 || i == 4))
                cells[row][coloumn].isFilled = true;
        }
        for (int i = 0; i < 72; i++) {
            int column = i % 9;
            int row = i / 9;
            Coordinates c = new Coordinates(initialX + (column * (cellWidth + gap)), initialY + ((row + 1) * cellWidth) + (row * gap));
            horizontalBricks[row][column] = new Brick(c, cellWidth, gap, true, row, column);
            gameBoard.getChildren().add(horizontalBricks[row][column]);
        }
        for (int i = 0; i < 72; i++) {
            int column = i % 8;
            int row = i / 8;
            Coordinates c = new Coordinates(initialX + ((column + 1) * cellWidth) + (column * gap), initialY + (row * (cellWidth + gap)));
            verticalBricks[row][column] = new Brick(c, gap, cellHeight, false, row, column);
            gameBoard.getChildren().add(verticalBricks[row][column]);
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                emptyJoints[i][j] = true;
            }
        }
        for (int i = 0; i < 72; i++) {
            int column = i % 9;
            int row = i / 9;
            horizontalBricks[row][column].setOnMouseClicked(e -> {
                if (clickedBrick == null) {
                    clickedBrick = horizontalBricks[row][column];
                } else {
                    boolean makeWall = clickedBrick.isValidWall(horizontalBricks[row][column])
                            && clickedBrick.isValidJoint(horizontalBricks[row][column], emptyJoints);
                    if (makeWall) {
                        if (clickedBrick.column > horizontalBricks[row][column].column)
                            CreateWall(horizontalBricks[row][column], clickedBrick);
                        else
                            CreateWall(clickedBrick, horizontalBricks[row][column]);
                    } else {
                        clickedBrick = horizontalBricks[row][column];
                    }
                }
            });
        }
        for (int i = 0; i < 72; i++) {
            int column = i % 8;
            int row = i / 8;
            verticalBricks[row][column].setOnMouseClicked(e -> {
                if (clickedBrick == null) {
                    clickedBrick = verticalBricks[row][column];
                } else {
                    boolean makeWall = clickedBrick.isValidWall(verticalBricks[row][column])
                            && clickedBrick.isValidJoint(verticalBricks[row][column], emptyJoints);
                    if (makeWall) {
                        if (clickedBrick.row > verticalBricks[row][column].row)
                            CreateWall(verticalBricks[row][column], clickedBrick);
                        else
                            CreateWall(clickedBrick, verticalBricks[row][column]);
                    } else {
                        clickedBrick = verticalBricks[row][column];
                    }
                }
            });
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int row = i, column = j;
                cells[row][column].setOnMouseClicked(e -> {
                    if (registerCell) {
                        clickedCell = cells[row][column];
                        if (turn == 0) {
                            //System.out.println("Player is trying to move");
                            movePlayer = clickedCell.isValidCell(cells, p0.currentCell);
                            if (movePlayer)
                                playerMove(p0, clickedCell);
                        } else if (turn == 1) {
                            //System.out.println("Player is trying to move");
                            movePlayer = clickedCell.isValidCell(cells, p1.currentCell);
                            if (movePlayer)
                                playerMove(p1, clickedCell);
                        } else if (turn == 2) {
                            //System.out.println("Player is trying to move");
                            movePlayer = clickedCell.isValidCell(cells, p2.currentCell);
                            if (movePlayer)
                                playerMove(p2, clickedCell);
                        } else {
                            //System.out.println("Player is trying to move");
                            movePlayer = clickedCell.isValidCell(cells, p3.currentCell);
                            if (movePlayer)
                                playerMove(p3, clickedCell);
                        }
                        registerCell = false;
                    }
                });
            }
        }
        return gameBoard;
    }

    private void playerMove(Player player, Cell cell) {
        //System.out.println("Player move is called");
        player.currentCell.isFilled = false;
        player.currentCell = cell;
        player.currentCell.isFilled = true;
        player.coordinates.x = cell.coordinates.x + playerRadius;
        player.coordinates.y = cell.coordinates.y + playerRadius;
        player.setTranslateX(player.coordinates.x);
        player.setTranslateY(player.coordinates.y);
        gameFinished = playerWin(player);
        if (gameFinished) {
            makeEndGameMenu();
            gameFinished = false;
        } else
            nextTurn();
    }

    private boolean playerWin(Player player) {
        if (turn == 0)
            return player.currentCell.row == 0;
        else if (turn == 1)
            return player.currentCell.row == 8;
        else if (turn == 2)
            return player.currentCell.column == 8;
        else //turn == 3
            return player.currentCell.column == 0;
    }

    private void start2PAIs(Stage primaryStage) {
        if (gameSchedule == null)
            makeGameSchedule();
        turnCounter = 0;
        twoP = true;
        aiVSai = true;
        Group gameBoard = makeGameBoard();
        p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], true,
                0, true);
        p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], true,
                8, true);
        p0.now = 0;
        p1.now = 0;
        maxWalls = 10;
        aiOneNumber = gameSchedule[scheduleI][scheduleJ];
        aiTwoNumber = gameSchedule[scheduleI][scheduleJ + 1];
        System.out.println("Starting a game with ai "+aiOneNumber+" and "+aiTwoNumber);
        scheduleJ += 2;
        if (scheduleJ == 12) {
            scheduleI++;
            scheduleJ -= 12;
            System.out.println("One group complete.");
        }
        if (turn == 0) {
            AI2P ai2P0 = new AI2P(new Node2P(p0, p1, null), cells, horizontalBricks,
                    verticalBricks, emptyJoints, aiLastMove,
                    aiList[aiOneNumber][0], aiList[aiOneNumber][1], aiList[aiOneNumber][2], aiList[aiOneNumber][3]);
            aiMove = ai2P0.play();
            aiLastMove = aiMove;
            handleAIMove(p0, aiMove);
        } else if (turn == 1 && p1.isAI) {
            AI2P ai2P1 = new AI2P(new Node2P(p1, p0, null), cells, horizontalBricks, verticalBricks,
                    emptyJoints, aiLastMove,
                    aiList[aiTwoNumber][0], aiList[aiTwoNumber][1], aiList[aiTwoNumber][2], aiList[aiTwoNumber][3]);
            aiMove = ai2P1.play();
            aiLastMove = aiMove;
            handleAIMove(p1, aiMove);
        }
    }

    private void start2PAI(Stage primaryStage) {
        twoP = true;
        aiVSai = false;
        Group gameBoard = makeGameBoard();
        int random = (int) (Math.random() * 2);

        if (random == 0) {
            p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], true,
                    0, true);
            p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], false,
                    8, true);
            playerClickedHandler(p1);
        } else {
            p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], false,
                    0, true);
            p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], true,
                    8, true);
            playerClickedHandler(p0);
        }
        p0.now = 0;
        p1.now = 0;
        maxWalls = 10;
        gameBoard.getChildren().addAll(p0, p1);
        if (turn == 0)
            turnLabel = new Label("Blue's turn.");
        else
            turnLabel = new Label("Red's turn.");
        wallsLeftLabel = new Label("walls left: ");
        nowp0Label = new Label("Blue: " + (maxWalls - p0.now));
        nowp1Label = new Label("Red: " + (maxWalls - p1.now));
        VBox vbox = new VBox();
        vbox.getChildren().addAll(turnLabel, wallsLeftLabel, nowp0Label, nowp1Label);
        vbox.setTranslateX(920);
        vbox.setTranslateY(60);
        vbox.setSpacing(60);
        gameBoard.getChildren().add(vbox);
        Scene gameScene = new Scene(gameBoard, windowWidth, windowHeight);
        primaryStage.setScene(gameScene);
        if (turn == 0 && p0.isAI) {
            AI2P ai2P0 = new AI2P(new Node2P(p0, p1, null), cells, horizontalBricks,
                    verticalBricks, emptyJoints, aiLastMove,
                    defaultDistanceFactor, defaultNowFactor, defaultMoveFactor, defaultRepeatFactor);
            System.out.println("AI is Blue");
            aiMove = ai2P0.play();
            aiLastMove = aiMove;
            handleAIMove(p0, aiMove);
        } else if (turn == 1 && p1.isAI) {
            AI2P ai2P1 = new AI2P(new Node2P(p1, p0, null), cells, horizontalBricks, verticalBricks,
                    emptyJoints, aiLastMove,
                    defaultDistanceFactor, defaultNowFactor, defaultMoveFactor, defaultRepeatFactor);
            System.out.println("AI is Red");
            aiMove = ai2P1.play();
            aiLastMove = aiMove;
            handleAIMove(p1, aiMove);
        }
    }

    private void start2Player(Stage primaryStage) {
        twoP = true;
        aiVSai = false;
        Group gameBoard = makeGameBoard();
        p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], false, 8, true);
        p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], false, 8, true);
        playerClickedHandler(p0);
        playerClickedHandler(p1);
        gameBoard.getChildren().addAll(p0, p1);
        maxWalls = 10;
        if (turn == 0)
            turnLabel = new Label("Blue's turn.");
        else
            turnLabel = new Label("Red's turn.");
        wallsLeftLabel = new Label("walls left: ");
        nowp0Label = new Label("Blue: " + (maxWalls - p0.now));
        nowp1Label = new Label("Red: " + (maxWalls - p1.now));
        VBox vbox = new VBox();
        vbox.getChildren().addAll(turnLabel, wallsLeftLabel, nowp0Label, nowp1Label);
        vbox.setTranslateX(920);
        vbox.setTranslateY(60);
        vbox.setSpacing(60);
        gameBoard.getChildren().add(vbox);
        Scene gameScene = new Scene(gameBoard, windowWidth, windowHeight);
        primaryStage.setScene(gameScene);
    }

    private void start4PAI(Stage primaryStage) {
        twoP = false;
        aiVSai = false;
        Group gameBoard = makeGameBoard();
        int random = (int) (Math.random() * 4);

        if (random == 0) {
            p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], true, 0, true);
            p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], false, 8, true);
            p2 = new Player(playerStartCoordinates[2], playerRadius, Color.GREEN, 2, cells[0][4], false, 8, false);
            p3 = new Player(playerStartCoordinates[3], playerRadius, Color.YELLOW, 3, cells[0][4], false, 0, false);
            playerClickedHandler(p1);
            playerClickedHandler(p2);
            playerClickedHandler(p3);
        } else if (random == 1) {
            p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], false, 0, true);
            p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], true, 8, true);
            p2 = new Player(playerStartCoordinates[2], playerRadius, Color.GREEN, 2, cells[0][4], false, 8, false);
            p3 = new Player(playerStartCoordinates[3], playerRadius, Color.YELLOW, 3, cells[0][4], false, 0, false);
            playerClickedHandler(p0);
            playerClickedHandler(p2);
            playerClickedHandler(p3);
        } else if (random == 2) {
            p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], false, 0, true);
            p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], false, 8, true);
            p2 = new Player(playerStartCoordinates[2], playerRadius, Color.GREEN, 2, cells[0][4], true, 8, false);
            p3 = new Player(playerStartCoordinates[3], playerRadius, Color.YELLOW, 3, cells[0][4], false, 0, false);
            playerClickedHandler(p0);
            playerClickedHandler(p1);
            playerClickedHandler(p3);
        } else {
            p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], false, 0, true);
            p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], false, 8, true);
            p2 = new Player(playerStartCoordinates[2], playerRadius, Color.GREEN, 2, cells[0][4], false, 8, false);
            p3 = new Player(playerStartCoordinates[3], playerRadius, Color.YELLOW, 3, cells[0][4], true, 0, false);
            playerClickedHandler(p0);
            playerClickedHandler(p1);
            playerClickedHandler(p2);
        }
        p0.now = 0;
        p1.now = 0;
        p2.now = 0;
        p3.now = 0;
        maxWalls = 5;
        gameBoard.getChildren().addAll(p0, p1, p2, p3);
        if (turn == 0)
            turnLabel = new Label("Blue's turn.");
        else if (turn == 1)
            turnLabel = new Label("Red's turn.");
        else if (turn == 2)
            turnLabel = new Label("Green's turn.");
        else
            turnLabel = new Label("Yellow's turn.");
        wallsLeftLabel = new Label("walls left: ");
        nowp0Label = new Label("Blue: " + (maxWalls - p0.now));
        nowp1Label = new Label("Red: " + (maxWalls - p1.now));
        nowp2Label = new Label("Green: " + (maxWalls - p2.now));
        nowp3Label = new Label("Yellow: " + (maxWalls - p3.now));
        VBox vbox = new VBox();
        vbox.getChildren().addAll(turnLabel, wallsLeftLabel, nowp0Label, nowp1Label, nowp2Label, nowp3Label);
        vbox.setTranslateX(920);
        vbox.setTranslateY(60);
        vbox.setSpacing(60);
        gameBoard.getChildren().add(vbox);
        Scene gameScene = new Scene(gameBoard, windowWidth, windowHeight);
        primaryStage.setScene(gameScene);
        if (turn == 0 && p0.isAI) {
            AI4P ai4P0 = new AI4P(new Node4P(p0, p1, p2, p3, null), cells, horizontalBricks,
                    verticalBricks, emptyJoints, aiLastMove);
            System.out.println("AI is Blue");
            aiMove = ai4P0.play();
            aiLastMove = aiMove;
            handleAIMove(p0, aiMove);
        } else if (turn == 1 && p1.isAI) {
            AI4P ai4P1 = new AI4P(new Node4P(p1, p2, p3, p0, null), cells, horizontalBricks,
                    verticalBricks, emptyJoints, aiLastMove);
            System.out.println("AI is Red");
            aiMove = ai4P1.play();
            aiLastMove = aiMove;
            handleAIMove(p1, aiMove);
        } else if (turn == 2 && p2.isAI) {
            AI4P ai4P2 = new AI4P(new Node4P(p2, p3, p0, p1, null), cells, horizontalBricks,
                    verticalBricks, emptyJoints, aiLastMove);
            System.out.println("AI is Green");
            aiMove = ai4P2.play();
            aiLastMove = aiMove;
            handleAIMove(p2, aiMove);
        } else if (turn == 3 && p3.isAI) {
            AI4P ai4P3 = new AI4P(new Node4P(p3, p0, p1, p2, null), cells, horizontalBricks,
                    verticalBricks, emptyJoints, aiLastMove);
            System.out.println("AI is Yellow");
            aiMove = ai4P3.play();
            aiLastMove = aiMove;
            handleAIMove(p3, aiMove);
        }
    }

    private void start4Player(Stage primaryStage) {
        twoP = false;
        aiVSai = false;
        Group gameBoard = makeGameBoard();
        p0 = new Player(playerStartCoordinates[0], playerRadius, Color.BLUE, 0, cells[8][4], false, 0, true);
        p1 = new Player(playerStartCoordinates[1], playerRadius, Color.RED, 1, cells[0][4], false, 8, true);
        p2 = new Player(playerStartCoordinates[2], playerRadius, Color.GREEN, 2, cells[4][0], false, 8, false);
        p3 = new Player(playerStartCoordinates[3], playerRadius, Color.YELLOW, 3, cells[4][8], false, 0, false);
        playerClickedHandler(p0);
        playerClickedHandler(p1);
        playerClickedHandler(p2);
        playerClickedHandler(p3);
        gameBoard.getChildren().addAll(p0, p1, p2, p3);
        maxWalls = 5;
        if (turn == 0)
            turnLabel = new Label("Blue's turn.");
        else if (turn == 1)
            turnLabel = new Label("Red's turn.");
        else if (turn == 2)
            turnLabel = new Label("Green's turn.");
        else if (turn == 3)
            turnLabel = new Label("Yellow's turn.");
        wallsLeftLabel = new Label("walls left: ");
        nowp0Label = new Label("Blue: " + (maxWalls - p0.now));
        nowp1Label = new Label("Red: " + (maxWalls - p1.now));
        nowp2Label = new Label("Green: " + (maxWalls - p2.now));
        nowp3Label = new Label("Yellow: " + (maxWalls - p3.now));
        VBox vbox = new VBox();
        vbox.getChildren().addAll(turnLabel, wallsLeftLabel, nowp0Label, nowp1Label, nowp2Label, nowp3Label);
        vbox.setTranslateX(920);
        vbox.setTranslateY(60);
        vbox.setSpacing(60);
        gameBoard.getChildren().add(vbox);
        Scene gameScene = new Scene(gameBoard, windowWidth, windowHeight);
        primaryStage.setScene(gameScene);
    }


    private boolean CreateWall(Brick firstBrick, Brick secondBrick) {
        //System.out.println("Create wall CALLED");
        if (turn == 0) {
            if (p0.now == maxWalls)
                return false;
            else
                p0.now++;
        }
        if (turn == 1) {
            if (p1.now == maxWalls)
                return false;
            else
                p1.now++;
        }
        if (turn == 2) {
            if (p2.now == maxWalls)
                return false;
            else
                p2.now++;
        }
        if (turn == 3) {
            if (p3.now == maxWalls)
                return false;
            else
                p3.now++;
        }
        if (firstBrick.isHorizontal) {
            emptyJoints[firstBrick.row][firstBrick.column] = false;
            cells[firstBrick.row][firstBrick.column].bottomBlocked = true;
            cells[firstBrick.row][firstBrick.column + 1].bottomBlocked = true;
            cells[firstBrick.row + 1][firstBrick.column].topBlocked = true;
            cells[firstBrick.row + 1][firstBrick.column + 1].topBlocked = true;
            firstBrick.isSolid = true;
            secondBrick.isSolid = true;
            if (twoP) {
                Cell.resetSeachedCells(searchedCells);
                if (Cell.canReachDestination2P(cells, searchedCells, p0, p1)) {
                    //System.out.println("can be reached player");
                    Wall temp = new Wall(firstBrick.coordinates, wallWidth, wallHeight, Color.BLACK);
                    gameBoard.getChildren().add(temp);
                    if (aiVSai)
                        AINextTurn();
                    else {
                        nextTurn();
                        updateWallLabel();
                    }
                    return true;
                } else {
                    emptyJoints[firstBrick.row][firstBrick.column] = true;
                    cells[firstBrick.row][firstBrick.column].bottomBlocked = false;
                    cells[firstBrick.row][firstBrick.column + 1].bottomBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column].topBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column + 1].topBlocked = false;
                    firstBrick.isSolid = false;
                    secondBrick.isSolid = false;
                    if (aiVSai)
                        AINextTurn();
                    return false;
                }
            } else {
                Cell.resetSeachedCells(searchedCells);
                if (Cell.canReachDestination4P(cells, searchedCells, p0, p1, p2, p3)) {
                    Wall temp = new Wall(firstBrick.coordinates, wallWidth, wallHeight, Color.BLACK);
                    gameBoard.getChildren().add(temp);
                    if (aiVSai)
                        AINextTurn();
                    else {
                        nextTurn();
                        updateWallLabel();
                    }
                    return true;
                } else {
                    emptyJoints[firstBrick.row][firstBrick.column] = true;
                    cells[firstBrick.row][firstBrick.column].bottomBlocked = false;
                    cells[firstBrick.row][firstBrick.column + 1].bottomBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column].topBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column + 1].topBlocked = false;
                    firstBrick.isSolid = false;
                    secondBrick.isSolid = false;
                    return false;
                }
            }

        } else {

            emptyJoints[firstBrick.row][firstBrick.column] = false;
            cells[firstBrick.row][firstBrick.column].rightBlocked = true;
            cells[firstBrick.row][firstBrick.column + 1].leftBlocked = true;
            cells[firstBrick.row + 1][firstBrick.column].rightBlocked = true;
            cells[firstBrick.row + 1][firstBrick.column + 1].leftBlocked = true;
            firstBrick.isSolid = true;
            secondBrick.isSolid = true;
            if (twoP) {
                Cell.resetSeachedCells(searchedCells);
                if (Cell.canReachDestination2P(cells, searchedCells, p0, p1)) {
                    //System.out.println("can be reached player");
                    Wall temp = new Wall(firstBrick.coordinates, wallHeight, wallWidth, Color.BLACK);
                    gameBoard.getChildren().add(temp);
                    if (aiVSai)
                        AINextTurn();
                    else {
                        nextTurn();
                        updateWallLabel();
                    }
                    return true;
                } else {
                    emptyJoints[firstBrick.row][firstBrick.column] = true;
                    cells[firstBrick.row][firstBrick.column].rightBlocked = false;
                    cells[firstBrick.row][firstBrick.column + 1].leftBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column].rightBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column + 1].leftBlocked = false;
                    firstBrick.isSolid = false;
                    secondBrick.isSolid = false;
                    if (aiVSai)
                        AINextTurn();
                    return false;
                }
            } else {
                Cell.resetSeachedCells(searchedCells);
                if (Cell.canReachDestination4P(cells, searchedCells, p0, p1, p2, p3)) {
                    Wall temp = new Wall(firstBrick.coordinates, wallHeight, wallWidth, Color.BLACK);
                    gameBoard.getChildren().add(temp);
                    if (aiVSai)
                        AINextTurn();
                    else {
                        nextTurn();
                        updateWallLabel();
                    }
                    return true;
                } else {
                    emptyJoints[firstBrick.row][firstBrick.column] = true;
                    cells[firstBrick.row][firstBrick.column].rightBlocked = false;
                    cells[firstBrick.row][firstBrick.column + 1].leftBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column].rightBlocked = false;
                    cells[firstBrick.row + 1][firstBrick.column + 1].leftBlocked = false;
                    firstBrick.isSolid = false;
                    secondBrick.isSolid = false;
                    if (aiVSai)
                        AINextTurn();
                    return false;
                }
            }

        }
    }

    private void updateWallLabel() {
        if (turn == 0)
            nowp0Label.setText("Blue: " + (maxWalls - p0.now));
        else if (turn == 1)
            nowp1Label.setText("Red: " + (maxWalls - p1.now));
        else if (turn == 2)
            nowp2Label.setText("Green: " + (maxWalls - p2.now));
        else
            nowp3Label.setText("Yellow: " + (maxWalls - p3.now));
    }

    private void nextTurn() {
        if (twoP) {
            if (turn == 0) {
                turn = 1;

                turnLabel.setText("Red's turn.");
                if (p1.isAI) {
                    AI2P ai2P1 = new AI2P(new Node2P(p1, p0, null), cells, horizontalBricks, verticalBricks,
                            emptyJoints, aiLastMove,
                            defaultDistanceFactor, defaultNowFactor, defaultMoveFactor, defaultRepeatFactor);
                    aiMove = ai2P1.play();
                    aiLastMove = aiMove;
                    handleAIMove(p1, aiMove);
                }
            } else {
                turn = 0;

                turnLabel.setText("Blue's turn.");
                if (p0.isAI) {
                    AI2P ai2P0 = new AI2P(new Node2P(p0, p1, null), cells, horizontalBricks, verticalBricks,
                            emptyJoints, aiLastMove,
                            defaultDistanceFactor, defaultNowFactor, defaultMoveFactor, defaultRepeatFactor);
                    aiMove = ai2P0.play();
                    aiLastMove = aiMove;
                    handleAIMove(p0, aiMove);
                }
            }
        } else {
            if (turn == 0) {
                turn = 1;
                turnLabel.setText("Red's turn.");
                /*if (p1.isAI) {
                    AI4P ai4P1 = new AI4P(new Node4P(p1, p2, p3, p0, null), cells, horizontalBricks, verticalBricks,
                            emptyJoints, aiLastMove);
                    aiMove = ai4P1.play();
                    aiLastMove = aiMove;
                    handleAIMove(p1, aiMove);
                }*/
            } else if (turn == 1) {
                turn = 2;
                turnLabel.setText("Green's turn.");
                /*if (p2.isAI) {
                    AI4P ai4P2 = new AI4P(new Node4P(p2, p3, p0, p1, null), cells, horizontalBricks, verticalBricks,
                            emptyJoints, aiLastMove);
                    aiMove = ai4P2.play();
                    aiLastMove = aiMove;
                    handleAIMove(p2, aiMove);
                }*/
            } else if (turn == 2) {
                turn = 3;
                turnLabel.setText("Yellow's turn.");
                /*if (p3.isAI) {
                    AI4P ai4P3 = new AI4P(new Node4P(p3, p0, p1, p2, null), cells, horizontalBricks, verticalBricks,
                            emptyJoints, aiLastMove);
                    aiMove = ai4P3.play();
                    aiLastMove = aiMove;
                    handleAIMove(p3, aiMove);
                }*/
            } else {
                turn = 0;
                turnLabel.setText("Blue's turn.");
                /*if (p0.isAI) {
                    AI4P ai4P0 = new AI4P(new Node4P(p0, p1, p2, p3, null), cells, horizontalBricks, verticalBricks,
                            emptyJoints, aiLastMove);
                    aiMove = ai4P0.play();
                    aiLastMove = aiMove;
                    handleAIMove(p0, aiMove);
                }*/
            }
        }
    }

    void handleAIMove(Player ai, Move move) {
        if (aiVSai) {
            if (aiMove.changePos) {
                if (aiMove.direction == Direction.up) {
                    AIMove(ai, cells[ai.currentCell.row - 1][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.upUp) {
                    AIMove(ai, cells[ai.currentCell.row - 2][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.upRight) {
                    AIMove(ai, cells[ai.currentCell.row - 1][ai.currentCell.column + 1]);
                } else if (aiMove.direction == Direction.upLeft) {
                    AIMove(ai, cells[ai.currentCell.row - 1][ai.currentCell.column - 1]);
                } else if (aiMove.direction == Direction.down) {
                    AIMove(ai, cells[ai.currentCell.row + 1][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.downDown) {
                    AIMove(ai, cells[ai.currentCell.row + 2][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.downRight) {
                    AIMove(ai, cells[ai.currentCell.row + 1][ai.currentCell.column + 1]);
                } else if (aiMove.direction == Direction.downLeft) {
                    AIMove(ai, cells[ai.currentCell.row + 1][ai.currentCell.column - 1]);
                } else if (aiMove.direction == Direction.left) {
                    AIMove(ai, cells[ai.currentCell.row][ai.currentCell.column - 1]);
                } else if (aiMove.direction == Direction.leftLeft) {
                    AIMove(ai, cells[ai.currentCell.row][ai.currentCell.column - 2]);
                } else if (aiMove.direction == Direction.right) {
                    AIMove(ai, cells[ai.currentCell.row][ai.currentCell.column + 1]);
                } else if (aiMove.direction == Direction.rightRight) {
                    AIMove(ai, cells[ai.currentCell.row][ai.currentCell.column + 2]);
                }
            } else {

                if (move.firstBrick.isHorizontal) {
                    CreateWall(horizontalBricks[move.firstBrick.row][move.firstBrick.column],
                            horizontalBricks[move.secondBrick.row][move.secondBrick.column]);
                } else {
                    CreateWall(verticalBricks[move.firstBrick.row][move.firstBrick.column],
                            verticalBricks[move.secondBrick.row][move.secondBrick.column]);
                }
            }
        } else {
            if (aiMove.changePos) {
                if (aiMove.direction == Direction.up) {
                    playerMove(ai, cells[ai.currentCell.row - 1][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.upUp) {
                    playerMove(ai, cells[ai.currentCell.row - 2][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.upRight) {
                    playerMove(ai, cells[ai.currentCell.row - 1][ai.currentCell.column + 1]);
                } else if (aiMove.direction == Direction.upLeft) {
                    playerMove(ai, cells[ai.currentCell.row - 1][ai.currentCell.column - 1]);
                } else if (aiMove.direction == Direction.down) {
                    playerMove(ai, cells[ai.currentCell.row + 1][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.downDown) {
                    playerMove(ai, cells[ai.currentCell.row + 2][ai.currentCell.column]);
                } else if (aiMove.direction == Direction.downRight) {
                    playerMove(ai, cells[ai.currentCell.row + 1][ai.currentCell.column + 1]);
                } else if (aiMove.direction == Direction.downLeft) {
                    playerMove(ai, cells[ai.currentCell.row + 1][ai.currentCell.column - 1]);
                } else if (aiMove.direction == Direction.left) {
                    playerMove(ai, cells[ai.currentCell.row][ai.currentCell.column - 1]);
                } else if (aiMove.direction == Direction.leftLeft) {
                    playerMove(ai, cells[ai.currentCell.row][ai.currentCell.column - 2]);
                } else if (aiMove.direction == Direction.right) {
                    playerMove(ai, cells[ai.currentCell.row][ai.currentCell.column + 1]);
                } else if (aiMove.direction == Direction.rightRight) {
                    playerMove(ai, cells[ai.currentCell.row][ai.currentCell.column + 2]);
                }
            } else {
                if (move.firstBrick.isHorizontal) {
                    CreateWall(horizontalBricks[move.firstBrick.row][move.firstBrick.column],
                            horizontalBricks[move.secondBrick.row][move.secondBrick.column]);
                } else {
                    CreateWall(verticalBricks[move.firstBrick.row][move.firstBrick.column],
                            verticalBricks[move.secondBrick.row][move.secondBrick.column]);
                }
            }
        }

    }

    private void AIMove(Player player, Cell cell) {
        //System.out.println("Player move is called");
        player.currentCell.isFilled = false;
        player.currentCell = cell;
        player.currentCell.isFilled = true;
        gameFinished = playerWin(player);
        if (gameFinished) {
            if (turn == 0) {
                aiScores[aiOneNumber][0]++;
                aiScores[aiOneNumber][1] += turnCounter;
                System.out.println("AI " + aiOneNumber + " won. AI " + aiTwoNumber + " lost.");
            } else {
                aiScores[aiTwoNumber][0]++;
                aiScores[aiTwoNumber][1] += turnCounter;
                System.out.println("AI " + aiTwoNumber + " won. AI " + aiOneNumber + " lost.");
            }
            gameFinished = false;
            if (scheduleI == 2) {
                System.out.println("One league complete.");
                System.out.println("Scores:");
                for (int i = 0; i < aiScores.length; i++) {
                    System.out.println("AI "+i+" : "+aiScores[i][0]+" points in "+aiScores[i][1]+" turns");
                }
                System.out.println("Generating new AIs...");
                generateNewAI();
            } else {
                System.out.println("trying to start a new game.");
                start2PAIs(primaryStage);
            }
        } else
            AINextTurn();
    }

    private void AINextTurn() {
        if (turn == 0) {
            turn = 1;
            turnCounter++;
            AI2P ai2P1 = new AI2P(new Node2P(p1, p0, null), cells, horizontalBricks, verticalBricks,
                    emptyJoints, aiLastMove,
                    aiList[aiOneNumber][0], aiList[aiOneNumber][1], aiList[aiOneNumber][2], aiList[aiOneNumber][3]);
            aiMove = ai2P1.play();
            aiLastMove = aiMove;
            handleAIMove(p1, aiMove);

        } else {
            turn = 0;
            turnCounter++;
            AI2P ai2P0 = new AI2P(new Node2P(p0, p1, null), cells, horizontalBricks, verticalBricks,
                    emptyJoints, aiLastMove,
                    aiList[aiTwoNumber][0], aiList[aiTwoNumber][1], aiList[aiTwoNumber][2], aiList[aiTwoNumber][3]);
            aiMove = ai2P0.play();
            aiLastMove = aiMove;
            handleAIMove(p0, aiMove);

        }

    }

    void generateNewAI(){
        int[] bestAis = new int[4];
        for (int i = 0; i < 2; i++) {
            int aiScore0 = aiScores[i * 4][0] * 1000 - aiScores[i * 4][1];
            int aiScore1 = aiScores[i * 4 + 1][0] * 1000 - aiScores[i * 4 + 1][1];
            int aiScore2 = aiScores[i * 4 + 2][0] * 1000 - aiScores[i * 4 + 2][1];
            int aiScore3 = aiScores[i * 4 + 3][0] * 1000 - aiScores[i * 4 + 3][1];
            int temp = Math.max(aiScore0, aiScore1);
            int temp2 = Math.max(aiScore2, aiScore3);
            if(Math.max(temp, temp2) == temp) {
                if (temp == aiScore0) {
                    bestAis[i] = i * 4;
                    if(Math.max(temp2, aiScore1) == aiScore1)
                        bestAis[i + 2] = i * 4 + 1;
                    else if(temp2 == aiScore2){
                        bestAis[i + 2] = i * 4 + 2;
                    }else
                        bestAis[i + 2] = i * 4 + 3;
                } else {
                    bestAis[i] = i * 4 + 1;
                    if(Math.max(temp2, aiScore0) == aiScore0)
                        bestAis[i + 2] = i * 4;
                    else if(temp2 == aiScore2){
                        bestAis[i + 2] = i * 4 + 2;
                    }else
                        bestAis[i + 2] = i * 4 + 3;
                }
            } else{
                if (temp2 == aiScore2) {
                    bestAis[i] = i * 4 + 2;
                    if(Math.max(temp, aiScore3) == aiScore3)
                        bestAis[i + 2] = i * 4 + 3;
                    else if(temp == aiScore0){
                        bestAis[i + 2] = i * 4;
                    }else
                        bestAis[i + 2] = i * 4 + 1;
                } else {
                    bestAis[i] = i * 4 + 3;
                    if(Math.max(temp, aiScore2) == aiScore2)
                        bestAis[i + 2] = i * 4 + 2;
                    else if(temp == aiScore0){
                        bestAis[i + 2] = i * 4;
                    }else
                        bestAis[i + 2] = i * 4 + 1;
                }
            }
        }
        System.out.println("Chosen AIs :");
        for (int i = 0; i < 4; i++) {
            System.out.println(i+"- AI "+ bestAis[i]);
        }
        int[][] parentAIs = new int[4][4];
        int[][] childAIs = new int[6][4];
        for (int i = 0; i < 4; i++) {
            parentAIs[i] = aiList[bestAis[i]];
        }
        for (int i = 0; i < 4; i++) {
            childAIs[0][i] = (parentAIs[0][i] + parentAIs[1][i]) / 2;
        }
        for (int i = 0; i < 4; i++) {
            childAIs[1][i] = (parentAIs[0][i] + parentAIs[2][i]) / 2;
        }
        for (int i = 0; i < 4; i++) {
            childAIs[2][i] = (parentAIs[0][i] + parentAIs[3][i]) / 2;
        }
        for (int i = 0; i < 4; i++) {
            childAIs[3][i] = (parentAIs[1][i] + parentAIs[2][i]) / 2;
        }
        for (int i = 0; i < 4; i++) {
            childAIs[4][i] = (parentAIs[1][i] + parentAIs[3][i]) / 2;
        }
        for (int i = 0; i < 4; i++) {
            childAIs[5][i] = (parentAIs[2][i] + parentAIs[3][i]) / 2;
        }
        aiList[0] = parentAIs[0];
        aiList[1] = parentAIs[1];
        aiList[2] = childAIs[5];
        aiList[3] = childAIs[4];
        aiList[4] = childAIs[0];
        aiList[5] = childAIs[1];
        aiList[6] = childAIs[2];
        aiList[7] = childAIs[3];
        for (int i = 0; i < populationSize; i++) {
            if(Math.random() < 0.15){ //complete mutation
                int t = (int)(Math.random() * 4);
                if(t == 0)
                    aiList[i][0] = (int) (-40 + Math.random() * -randomFactor);
                else if(t == 1)
                    aiList[i][1] = (int) (-10 + Math.random() * (-randomFactor / 2));
                else if(t == 2)
                    aiList[i][2] = (int) (Math.random() * (randomFactor / 5));
                else
                    aiList[i][3] = (int) (60 + Math.random() * randomFactor);
                System.out.println("Complete mutation just happened!!!");
            }else if(Math.random() < 0.4){ // partial mutation
                aiList[i][(int)(Math.random() * 4)] *= ((Math.random() / 2) +0.75);
                System.out.println("Partial mutation happened!");
            }
        }
        writeAIFile();
    }

    void createNewAI() {
        aiList = new int[populationSize][4];
        for (int i = 0; i < populationSize; i++) {
            aiList[i][0] = (int) (-40 + Math.random() * -randomFactor);
            aiList[i][1] = (int) (-10 + Math.random() * (-randomFactor / 2));
            aiList[i][2] = (int) (Math.random() * (randomFactor / 5));
            aiList[i][3] = (int) (60 + Math.random() * randomFactor);
        }
        try {
            Writer writer = new BufferedWriter(new FileWriter(FileName));
            for (int i = 0; i < populationSize; i++) {
                writer.write(aiList[i][0] + "\n" + aiList[i][1] + "\n" + aiList[i][2] + "\n" + aiList[i][3] + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Something went wrong while creating AIs.");
        }
        System.out.println("New AIs created.");
    }

    void readAIFile() {
        System.out.println("Would you like to read from default text?");
        String line = scanner.next();
        aiList = new int[populationSize][4];
        if (!line.equalsIgnoreCase("yes")) {
            System.out.println("Enter Path");
            FileName = new File(scanner.next());
        }
        try {
            BufferedReader input = new BufferedReader(new FileReader(FileName));
            for (int i = 0; i < populationSize * 4; i++) {
                line = input.readLine();
                aiList[i / 4][i % 4] = Integer.parseInt(line);
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Something went wrong in reading a file.");
        }
        System.out.println("File read successful.");
    }

    void writeAIFile() {
        System.out.println("Would you like to write AI list in default text?");
        String line = scanner.next();
        if (!line.equalsIgnoreCase("yes")) {
            System.out.println("Enter Path");
            FileName = new File(scanner.next());
        }
        try {
            Writer writer = new BufferedWriter(new FileWriter(FileName));
            for (int i = 0; i < populationSize; i++) {
                writer.write(aiList[i][0] + "\n" + aiList[i][1] + "\n" + aiList[i][2] + "\n" + aiList[i][3] + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Something went wrong while writing AI file.");
        }
        System.out.println("Writing AI file successful.");
    }

    void defaultReadAIFile() {
        String line;
        aiList = new int[populationSize][4];
        try {
            BufferedReader input = new BufferedReader(new FileReader(FileName));
            for (int i = 0; i < populationSize * 4; i++) {
                line = input.readLine();
                aiList[i / 4][i % 4] = Integer.parseInt(line);
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Something went wrong in reading a file.");
        }
        System.out.println("Initial file read successful.");
    }
}




