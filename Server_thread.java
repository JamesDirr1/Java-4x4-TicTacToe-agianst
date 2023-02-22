//James Dirr CSC-460-001
//Server thread that plays a person in 4x4 ticTacToe and has strategy

import java.net.*;
import java.util.*;
import java.io.*;

public class Server_thread extends Thread{

    private  Socket toclientsocket;
    private  DataInputStream   instream;
    private  DataOutputStream  outstream;
    private  PrintWriter out;
    private  BufferedReader in;
    private Random gen;
    private char [ ] [ ] board;
    private int row,col;

    public Server_thread(Socket s) throws IOException {
        this.toclientsocket = s;
        gen = new Random();
        instream = new DataInputStream(toclientsocket.getInputStream());
        outstream = new DataOutputStream(toclientsocket.getOutputStream());
        out = new PrintWriter(outstream, true);
        in = new BufferedReader(new InputStreamReader(instream));
        board = new char[4][4];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' ';
            }
        }
        row = 1;
        col = 1;
    }


    public void run(){

        int counter = 0;
        String response = "";
        boolean gameover = false;
        boolean turn = false;
        int coin = gen.nextInt(2);
        if (coin == 0){
            //System.out.println("CLIENT");
            out.println("CLIENT");
            turn = true;
        }
        while(gameover != true){
            if(turn){
               // System.out.println("Waiting for client");
                try{
                    response =  in.readLine();
                } catch (IOException e) {
                    System.out.println("Some sort of read error on socket in server thread ");
                }
                String [ ] data = response.split( "\\s+" );
                row = Integer.parseInt(data[1]);
                col = Integer.parseInt(data[2]);
                board[row][col] = 'O';
                printboard();
                counter++;

                if(checkwin() || counter == 16){
                    gameover = true;
                    if(checkwin()){
                        out.println("MOVE -1 -1 WIN");
                    }
                    else {
                        out.println("MOVE -1 -1 TIE");
                    }
                }
            }
            else{
                //System.out.println("Server making move");
                makemove(counter);
                counter++;
                board[row][col]= 'X';
                printboard();
                if(checkwin() || counter == 16){
                    gameover = true;
                    if(checkwin()) {
                        out.println("MOVE " + row + " " + col + " LOSS");
                    }
                    else{
                        out.println("MOVE " + row + " " + col + " TIE");
                    }
                }
                else{
                    //System.out.println("MOVE " + row + " " + col);
                    out.println("MOVE " + row + " " + col);
                }
            }
            turn = !turn;
        }
  }
  void makemove(int counter) {
      boolean strat = true; /** set to false to disable the computers strategy **/
      if (strat) {
          if (counter == 0 || counter == 1) {/**goes for one of the corners on first move */
              if (board[0][0] == ' ') {
                  row = 0;
                  col = 0;
              } else if (board[3][0] == ' ') {
                  row = 3;
                  col = 0;
              } else if (board[0][3] == ' ') {
                  row = 0;
                  col = 3;
              } else {
                  row = 3;
                  col = 3;
              }
          } else if (closecol()) {/** try to block a row win */
              int i = 0;
              if (board[i][col] == ' ') {
                  row = i;
              } else {
                  while (board[i][col] != ' ') {
                      i++;
                      row = i;
                  }
              }
          } else if (closerow()) {/** try to block a col win */
              int i = 0;
              if (board[row][i] == ' ') {
                  col = i;
              } else {
                  while (board[row][i] != ' ') {
                      i++;
                      col = i;
                  }
              }
          } else if (closedia1()) {/**try to block 1st diagonal */
              int i = 0;
              if (board[i][i] == ' ') {
                  col = i;
                  row = i;
              } else {
                  while (board[i][i] != ' ') {
                      i++;
                      col = i;
                      row = i;
                  }
              }
          } else if (closedia2()) {/**try to block 2st diagonal */
              int i = 0;
              int j = 3;
              if (board[i][j] == ' ') {
                  col = j;
                  row = i;
              } else {
                  while (board[i][j] != ' ') {
                      i++;
                      j--;
                      col = j;
                      row = i;
                  }
              }
          } else {/** random move if no close wins */
              row = gen.nextInt(4);
              col = gen.nextInt(4);
              while (board[row][col] != ' ') {
                  row = gen.nextInt(4);
                  col = gen.nextInt(4);
              }
          }
      }
      else {/**random move if strat set to false*/
          row = gen.nextInt(4);
          col = gen.nextInt(4);
          while (board[row][col] != ' ') {
              row = gen.nextInt(4);
              col = gen.nextInt(4);
          }
      }
  }

  boolean checkwin(){
      for (int x = 0; x <= 3; x++) {/**check for a row-win*/
          if (board[x][0] == board[x][1] && board[x][1] == board[x][2] &&
                  board[x][2] == board[x][3] && board[x][0] != ' ') {
              return true;
          }
      }

      for (int x = 0; x <= 3; x++) {/**check for a col-win*/
          if (board[0][x] == board[1][x] && board[1][x] == board[2][x] &&
                  board[2][x] == board[3][x] && board[0][x] != ' ') {
              return true;
          }
      }
      /** check diagonal-win*/
      if(board[0][0] == board[1][1] && board[1][1] == board[2][2] &&
                  board[2][2] == board[3][3] && board[0][0] != ' '){
              return true;
      }
      else if(board[3][0] == board[2][1] && board[2][1] == board[1][2] &&
              board[1][2] == board[0][3] && board[3][0] != ' '){
          return true;
      }
      else{
          return false;
      }
  }
  boolean closedia1(){/**checks for a close diagonal*/
        int temp = 0;
        boolean T = false;
        if(board[0][0] == 'O'){
            temp++;
        }
         if(board[1][1] == 'O'){
          temp++;
         }
         if(board[2][2] == 'O'){
          temp++;
         }
         if(board[3][3] == 'O'){
          temp++;
         }
         if(board[0][0] == 'X' || board[1][1] == 'X' || board[2][2] == 'X' || board[3][3] == 'X'){
             temp = 4;
         }
         if (temp == 3){
             T = true;
         }
         return T;
  }
    boolean closedia2(){/** checks for a close diagonal */
        int temp = 0;
        boolean T = false;
        if(board[0][3] == 'O'){
            temp++;
        }
        if(board[1][2] == 'O'){
            temp++;
        }
        if(board[2][1] == 'O'){
            temp++;
        }
        if(board[3][0] == 'O'){
            temp++;
        }
        if(board[0][3] == 'X' || board[1][2] == 'X' || board[2][1] == 'X' || board[3][0] == 'X'){
            temp = 4;
        }
        if (temp == 3){
            T = true;
        }
        return T;
    }

  boolean closecol(){/**checks for a close col win */
      boolean T = false;
      //System.out.println("Checking close col");
      for (int i = 0; i <= 3; i++) {
          int temp = 0;
          int tempCol = 0;
          for(int j = 0;  j <= 3; j++) {
             // System.out.println("checking row : " + i + " col : " + j + "temp is: " + temp);
              if (board[j][i] == 'O') {
                  tempCol = i;
                  temp++;
                 // System.out.println("Checking  and O found at row: " + i + " and col: " + j + " O count is: " + temp);
              }
              if (board[j][i] == 'X') {
                 // System.out.println("x found temp = 4");
                  temp = 4;
              }
          }
          if (temp == 3){
             // System.out.println("found close row at Col: " + i);
              T = true;
              row = tempCol;
          }
      }
      return T;
  }
    boolean closerow(){/** check for a row-close*/
        boolean T = false;
           // System.out.println("Checking close row");
        for (int i = 0; i <= 3; i++) {
            int temp = 0;
            int tempRow = 0;
            for(int j = 0;  j <= 3; j++) {
              //  System.out.println("checking row : " + i + " col : " + j + "temp is: " + temp);
                if (board[i][j] == 'O') {
                    tempRow = i;
                    temp++;
                   // System.out.println("Checking  and O found at row: " + i + " and col: " + j + " O count is: " + temp);
                }
                if (board[i][j] == 'X') {
                   // System.out.println("x found temp = 4");
                    temp = 4;
                }
            }
                    if (temp == 3){
                      //  System.out.println("found close col at row: " + i);
                        T = true;
                        row = tempRow;
                    }
                }

        return T;
    }

  void printboard(){
        System.out.println("_________SERVER MOVE_______");
        for(int i = 0; i <= 3; i++) {
         System.out.println(board[i][0] + " | " + board[i][1] + " | " + board[i][2] + " | " +board[i][3] );
         if( i < 3){
         System.out.println("-------------");
        }
        }
  }
}
