package chess.core.bitboards.moves;

import chess.core.bitboards.MoveType;
import chess.core.bitboards.moves.pieces.PieceType;
import chess.core.utils.Utils;

public class Move {
  private String move;
  private MoveType moveType; 
  private int player, longShort, playerWon, score;
  private boolean check, checkmate;
  private PieceType piece, pieceCap;


  public Move(String move, MoveType moveType, int player, PieceType piece, PieceType pieceCap) {
    this.move = move;
    this.moveType = moveType;
    this.player = player;
    this.piece = piece;
    this.pieceCap = pieceCap;
  }
  public Move(String move, MoveType moveType, int player, PieceType piece, PieceType pieceCap, int longShort) {
    this.move = move;
    this.moveType = moveType;
    this.player = player;
    this.piece = piece;
    this.pieceCap = pieceCap;
    this.longShort = longShort;
  }

  public int getFromX() { return Character.getNumericValue(move.charAt(1)); } //Gets integer value from string (x)
  public int getFromY() { return Character.getNumericValue(move.charAt(0)); } //gets y of current position
  public int getToX() { return Character.getNumericValue(move.charAt(3)); } //gets x of next position
  public int getToY() { return Character.getNumericValue(move.charAt(2)); } //gets y of next position

  public Move flipMove() {
    this.move = this.move.substring(2, 4) + this.move.substring(0, 2); //Flips the move coordinates
    return this;
  }

  public int getShiftFrom() { return Utils.getShiftFrom(move); } //Gets bitboard of current position
  public int getShiftTo() { return Utils.getShiftTo(move); } //Gets bitboard of next position
  public MoveType getType() { return moveType; } //Gets which move type was performed
  public int getPlayer() { return player; } //Gets the player that performed the move
  public PieceType getPiece() { return piece; } //Gets the piece that was moves
  public PieceType getPieceC() { return pieceCap; } //Gets the piece that was captured
  public int getLS() { return longShort; } //Gets if castle was queen side or king side

  public String getMoveReg() { return move; } //Gets the move, from Y, from X, to Y, to X

  public boolean getCheck() { return check; } //Was the king in check
  public boolean getCheckmate() { return checkmate; } //Was the king in checkmate

  public int getPlayerWon() { return playerWon; } //Which player won the game

  public void setCheck(boolean check) { this.check = check; } //Sets the check state
  public void setCheckmate(boolean checkmate) { this.checkmate = checkmate; } //Sets the checkmate state
  public void setPlayerWon(int player) { this.playerWon = player; } //Sets which player won
  public void setScore(int score) { this.score = score; } //Gets the score for the current node
  public int getScore() { return this.score; } //Gets the score for the current node
}