package chess.core.bitboards.moves;

import java.util.Stack;

public class MoveHistory {
  private static Stack<Move> pMoves = new Stack<Move>(); //Holds all moves that can be undone
  private static Stack<Move> oPMoves = new Stack<Move>(); //Holds all moves that can be redone

  public static void addItem(Move m) { pMoves.push(m); } //Adds move that can be undone
  public static void remove(int from, int to) { pMoves.subList(from, to).clear(); } //Clears a portion from undo moves
  public static void removeNextFromOld() { oPMoves.pop(); } //Gets the move to be redone

  public static Move peekNext() { return pMoves.peek(); } //Looks at next move
  public static Move getItemAt(int i) { return pMoves.elementAt(i); } //Gets specific item

  public static boolean isEmpty() { return pMoves.isEmpty(); } //Is the possible moves to undo empty?
  public static int getSize() { return pMoves.size(); } //Returns size of possible moves to undo
  public static int getSizeOld() { return oPMoves.size(); } //Returns the size of possible moves to redo

  public static Move getNext() {
    oPMoves.add(pMoves.peek()); //Gets the next move to undo and places it in redo
    return pMoves.pop();
  }
  public static Move getNextOld() {
    pMoves.add(oPMoves.peek()); //Gets the next move to redo and places it back in undo
    return oPMoves.pop();
  }	
  public static void clear() {
    pMoves.clear(); //Clears the undo moves
    oPMoves.clear(); //Clears the redo moves
  }
}
