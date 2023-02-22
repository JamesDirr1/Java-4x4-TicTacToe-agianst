//James Dirr CSC-460-001
//JavaFx version of client

package sample;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class main extends Application {

    private static String hostName = "localhost";
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Socket toSeverSocket;
    private static char[][] board;

    //Javafx
    static boolean turn = false;
    static boolean gameover = false;
    static ArrayList<Button> buttons;
    static GridPane pane;
    static Text text;
    private static int[][] loc;
    private static int row;
    private static int col;


    @Override
    public void start(Stage primaryStage) throws InterruptedException, IOException {
        pane = new GridPane(); // creates pane
        text = new Text();  //creates text
        // creates the buttons
        Button button1 = new Button("1");
        Button button2 = new Button("2");
        Button button3 = new Button("3");
        Button button4 = new Button("4");
        Button button5 = new Button("5");
        Button button6 = new Button("6");
        Button button7 = new Button("7");
        Button button8 = new Button("8");
        Button button9 = new Button("9");
        Button button10 = new Button("10");
        Button button11 = new Button("11");
        Button button12 = new Button("12");
        Button button13 = new Button("13");
        Button button14 = new Button("14");
        Button button15 = new Button("15");
        Button button16 = new Button("16");
        // adds button to list
        buttons = new ArrayList<>(Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16));
        //sets style for each button
        buttons.forEach(button -> {
            Font O = Font.font(20);
            button.setFont(O);
            button.setOpacity(1);
            button.setFocusTraversable(false);
            button.setPrefSize(100, 100);
            button.setBorder(new Border(new BorderStroke(Color.BLACK,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        });
        //creates scene and style
        BorderPane borderPane = new BorderPane();
        text.setText("Welcome to TicTacToe");
        text.setTextAlignment(TextAlignment.CENTER);
        Font T = Font.font(30);
        text.setFont(T);
        borderPane.setTop(text);
        borderPane.setCenter(pane);
        //adds buttons to pane
        pane.add(button1, 0, 0, 1, 1);
        pane.add(button2, 1, 0, 1, 1);
        pane.add(button3, 2, 0, 1, 1);
        pane.add(button4, 3, 0, 1, 1);
        pane.add(button5, 0, 1, 1, 1);
        pane.add(button6, 1, 1, 1, 1);
        pane.add(button7, 2, 1, 1, 1);
        pane.add(button8, 3, 1, 1, 1);
        pane.add(button9, 0, 2, 1, 1);
        pane.add(button10, 1, 2, 1, 1);
        pane.add(button11, 2, 2, 1, 1);
        pane.add(button12, 3, 2, 1, 1);
        pane.add(button13, 0, 3, 1, 1);
        pane.add(button14, 1, 3, 1, 1);
        pane.add(button15, 2, 3, 1, 1);
        pane.add(button16, 3, 3, 1, 1);

        Scene scene = new Scene(borderPane, 400, 440);
        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(scene);
        primaryStage.show();

        //sets action for button press
        buttons.forEach(button -> {
            buttonPress(button);
        });

        //non javafx
        toSeverSocket = new Socket(hostName, 8787);
        board = new char[4][4];
        inputStream = new DataInputStream(toSeverSocket.getInputStream());
        outputStream = new DataOutputStream(toSeverSocket.getOutputStream());
        out = new PrintWriter(outputStream, true);
        in = new BufferedReader(new InputStreamReader(inputStream));
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' ';
            }
        }
        //matching a matrix with the buttons in the pane
        loc = new int[4][4];
        int inc = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                loc[i][j] = inc;
                inc++;
            }

        //starts game
        serverMove(in);
    }

    private static void buttonPress(Button button) {
        button.setOnMouseClicked(mouseEvent -> {
                    if (turn) {// if players turn disables and adds O to button
                        button.setDisable(true);
                        Font O = Font.font(45);
                        button.setFont(O);
                        button.setOpacity(1);
                        button.setBorder(new Border(new BorderStroke(Color.BLACK,
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                        button.setStyle("-fx-text-fill: #0000ff");
                        button.setText("O");
                        row = pane.getRowIndex(button);
                        col = pane.getColumnIndex(button);
                        board[row][col] = 'O';
                        out.println("MOVE " + row + " " + col);
                        text.setText("Server turn");// visually does not update usually  as server to fast in its response
                        try {
                            serverMove(in); //has server make its move
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{// if not players turn blocks them from making a move
                        System.out.println("NOT YOUR TURN");
                    }
                }
        );
    }

    public static void playgame(BufferedReader in, PrintWriter out) throws IOException {
        Scanner inp = new Scanner(System.in);
        String response = "";
        boolean turn = false;
        boolean gameover = false;

        while (!gameover) {
            if (turn) {
                buttons.forEach(button -> {
                    button.setDisable(false);
                });
            } else {

                response = in.readLine();
                if (response != "CLIENT") {
                    String[] args = response.split("\\s+");  // this statement splits the message into tokens
                    if (args.length > 3) {
                        row = Integer.parseInt(args[1]);
                        col = Integer.parseInt(args[2]);
                        if (args[3] != "Win" && row != -1) {
                            board[row][col] = 'X';
                        }
                        switch (args[3]) {
                            case "WIN":
                                System.out.println("\n\nCongratulations!!! You WON the game!");
                                break;
                            case "TIE":
                                System.out.println("\nThe game was a TIE!");
                                break;
                            case "LOSS":
                                System.out.println("\nSORRY! You LOST the game!");
                                break;
                        }   // end switch
                        gameover = true;
                    } else {
                        row = Integer.parseInt(args[1]);
                        col = Integer.parseInt(args[2]);
                        board[row][col] = 'X';
                    }
                } else {
                    System.out.println("\nYOU MOVE FIRST");
                }
            }
            printboard();
            turn = !turn;
        }
        System.out.println("\n\nHere is the final game board");
        printboard();
    }

    static void printboard() {
        System.out.println("_________client MOVE_______");
        for (int i = 0; i <= 3; i++) {
            System.out.println(board[i][0] + " | " + board[i][1] + " | " + board[i][2] + " | " + board[i][3] + " | ");
        }
    }

    static void serverMove(BufferedReader in) throws IOException {//takes in the server move
        String response = "";
        if (!gameover){
            response = in.readLine();
            if (!response.equals("CLIENT")) {
                String[] args = response.split("\\s+");  // this statement splits the message into tokens
                if (args.length > 3) {
                    row = Integer.parseInt(args[1]);
                    col = Integer.parseInt(args[2]);
                    if (args[3] != "Win" && row != -1) {
                        board[row][col] = 'X';
                    }
                    switch (args[3]) {
                        case "WIN":
                            System.out.println("\n\nCongratulations!!! You WON the game!");
                            text.setText("You WON the game!"); // updates game board
                            text.setStyle("-fx-fill: #49fc03");
                            break;
                        case "TIE":
                            System.out.println("\nThe game was a TIE!");
                            text.setText("The game was a TIE!");// updates game board
                            text.setStyle("-fx-fill: #030bfc");
                            break;
                        case "LOSS":
                            System.out.println("\nSORRY! You LOST the game!");
                            text.setStyle("-fx-fill: #ff0000"); // updates game board
                            text.setText("SORRY! You LOST the game!");
                            break;
                    }   // end switch
                    gameover = true;
                    buttons.forEach(button -> { //opens buttons after turn for user to move
                        button.setDisable(true);
                    });
                } else {//sets row and col
                    row = Integer.parseInt(args[1]);
                    col = Integer.parseInt(args[2]);
                    board[row][col] = 'X';
                    text.setText("Your turn");

                }
                if(row != -1) {// if game not over update game board to add server move
                    Font O = Font.font(45);
                    buttons.get(loc[row][col]).setDisable(true);
                    buttons.get(loc[row][col]).setFont(O);
                    buttons.get(loc[row][col]).setOpacity(1);
                    buttons.get(loc[row][col]).setBorder(new Border(new BorderStroke(Color.BLACK,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                    buttons.get(loc[row][col]).setStyle("-fx-text-fill: #ff0000");
                    buttons.get(loc[row][col]).setText("X");
                    printboard();
                    turn = true;
                }
            }
            else{
                text.setText("You move first");
                turn = true;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

