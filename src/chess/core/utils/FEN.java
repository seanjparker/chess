package chess.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import chess.core.BitboardInit;
import chess.core.bitboards.Board;
import chess.core.bitboards.Type;
import chess.core.bitboards.moves.MoveHistory;

public class FEN { 	
  private String fileName, path;	
  public FEN(String fileName, String path) {
    this.fileName = fileName;
    this.path = path;
  }

  public String generateFEN() {
    char[][] board = new char[8][8]; //new board to store all pieces from the bitboards
    String FENRecord = ""; //fen record that is written to the file
    int emptyCount = 0; //counter for a string of empty spaces



    for (int i = 0; i < Board.pieces.length; i++) {
      int p = i < 6 ? 1 : 2;
      long bb = Board.getPieceBoard(i, p);	//Gets the current bitboard			
      while (bb != 0) {
        int bitPos = Utils.bitPosition(bb); //Gets the new bit from the bitboard
        if (p == 1) { //If white, upper case character, otherwise, black and lowercase
          board[bitPos/8][bitPos%8] = Type.tPieceChar[i].toUpperCase().charAt(0);							
        } else {
          board[bitPos/8][bitPos%8] = Type.tPieceChar[i].toLowerCase().charAt(0);
        }
        bb &= bb - 1; //Set bitboard for the next bit
      }
    }
    for (int y = 0; y < board.length; y++) { //Converts the 2D array to a single String record
      for (int x = 0; x < board[y].length; x++) {
        if (board[y][x] == 0) {
          emptyCount = 0;
          for (int i = x; i < board.length; i++) {
            if (board[y][i] == 0) { //Empty space is found, count empty
              emptyCount++;
            } else {
              break;
            }
            x = i;
          }
          FENRecord += emptyCount; //Add the number of empty spaces to the record
        } else {
          FENRecord += board[y][x]; //If not empty, add character to the record
        }
      }
      if (y < board.length - 1) {
        FENRecord += "/"; //At the end of the row, set a breakpoint as '/'
      }
    }

    //Side to move
    FENRecord += Board.getPlayer() == 0 ? " w " : " b ";

    //Castling ability
    if (Board.getCWL() && Board.getCWS() && Board.getCBL() && Board.getCBS()) {
      FENRecord += " - ";
    } else {
      if (!Board.getCWS()) { FENRecord += "K"; }
      if (!Board.getCWL()) { FENRecord += "Q"; }
      if (!Board.getCBS()) { FENRecord += "k"; }
      if (!Board.getCBL()) { FENRecord += "q"; }
    }

    //Fullmove counter
    FENRecord += " " + (int) ((MoveHistory.getSize() / 2) + 1);

    return FENRecord; //Retuns the fen record
  }

  public String loadFEN() {
    String returnString = "";
    try {
      FileReader fr = new FileReader(path + "/" + fileName + ".txt"); //Gets the file from file path
      int temp;
      while ((temp = fr.read()) != -1) {
        returnString += (char) temp; //Reads of all the data from the file
      }
      fr.close(); //Closes the file
    } catch (FileNotFoundException e) {
      e.printStackTrace(); //If file not found, display error message
      JOptionPane.showMessageDialog(null, "File not found");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return returnString;
  }

  public void saveFEN() {
    File f = new File(this.path + "/" + this.fileName + ".txt");
    if (!f.exists() && !f.isDirectory()) { //Ensures the directory is valid
      try {
        f.createNewFile(); //Creates a new file with generated record
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    write(); //Writes to the file
  }

  private void write() {
    FileWriter fw;
    try {
      fw = new FileWriter(this.path + "/" + this.fileName + ".txt");
      fw.write(generateFEN()); //Generates the fen record
      fw.close();

      JOptionPane.showMessageDialog(null, "Saved successfully"); //Tells the user file saved
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void parseFEN(String fen) { //When loading fen from a file, parse by string -> 2d array -> bitboards
    char[][] board = new char[8][8];
    int boardIndex = 0, totalEmpty = 0, rowCount = 0, sideToMove = -1;
    boolean CWS = true, CWL = true, CBS = true, CBL = true; //Castling ability

    for (int i = 0; i < fen.length(); i++) {
      char currentCharacter = fen.charAt(i);
      if (boardIndex == board.length * board.length) { break; } //If reached the end of the chess board, break the loop

      if (currentCharacter != '/') { //When not at the end of a row
        if (Character.isDigit(currentCharacter)) {
          int empty = Character.getNumericValue(currentCharacter);
          boardIndex += empty; //If the character is a number, leave the correct number of spaces
          totalEmpty += empty;
        } else {
          board[boardIndex / 8][boardIndex % 8] = currentCharacter; //Place the character in the correct location
          boardIndex++;
        }
      } else {
        rowCount++; //Keeping track of the number of rows passed
      }
    }

    int cIndex = boardIndex - totalEmpty + (totalEmpty % 7) + rowCount; //Gets the index where the chess board ended
    //cIndex is always different depending on number of empty spaces are where they are located
    //Get the current player (side to move)
    switch(fen.charAt(cIndex + 1)) {
      case 'w':
        sideToMove = 0; //If the white is to move, set accordingly
      case 'b':
        sideToMove = 1;
    }
    cIndex += 2; //Move forward by two in the string

    //Set the possible castling ability
    while (!Character.isWhitespace(fen.charAt(cIndex))) {
      char cAbility = fen.charAt(cIndex);
      if (Character.isUpperCase(cAbility)) {
        if (cAbility == 'K') {
          CWS = false;
        } else {
          CWL = false;
        }
      } else {
        if (cAbility == 'k') {
          CBS = false;
        } else {
          CBL = false;
        }
      }
      cIndex++;
    }
    //Apply all information to bitboards
    String[][] chessBoard = new String[8][8];
    for (int j = 0; j < chessBoard.length; j++) {
      for (int k = 0; k < chessBoard[j].length; k++) {
        chessBoard[j][k] = Character.toString(board[j][k]);
      }
    }
    BitboardInit.arrayToBitboard(chessBoard); //Use the 2D array to apply data to bitboards
    Board.setCWS(CWS); Board.setCWL(CWL); //Sets the castleing ability
    Board.setCBS(CBS); Board.setCBL(CBL);		
    Board.setPlayer(sideToMove); //Sets which player is to move
  }
}
