package chess.core.bitboards;

import java.util.ArrayList;
import java.util.List;
import chess.core.BitboardInit;
import chess.core.bitboards.moves.Move;
import chess.core.bitboards.moves.MoveHistory;
import chess.core.bitboards.moves.Moves;
import chess.core.bitboards.moves.pieces.PieceType;
import chess.core.utils.Utils;

public class Board {
  public final static short PIECES = 6;

  public static Pair[] pieces;

  private static boolean isCreating = false;

  private static int human = 0; // 0 = White, 1 = Black
  private static int whichPlayer = human; // 0 = White, 1 = Black

  private static boolean CWL = false, // Queen Side Castle (<-- Left) -- White
      CWS = false, // King Side Castle (--> Right) -- White
      CBL = false, // Queen Side Castle (<-- Left) -- Black
      CBS = false; // King Side Castle (--> Right) -- Black

  private static long wOccupied = 0L; // White's occupied bitboards
  private static long bOccupied = 0L; // Blacks occupied
  private static long aOccupied = 0L; // Every PieceType
  private static long empty = 0L; // The empty pieces = ~aOccupied

  public static void initChess() {
    pieces = new Pair[PIECES << 1];

    BitboardInit.initBitboards();
    setOccupied();

    CWL = CWS = CBL = CBS = false;

    human = 0; // White moves first
    whichPlayer = human;

    MoveHistory.clear(); // Clears all previous moves
  }

  public static long getPieceBoard(PieceType p, int c) {
    return pieces[c * PIECES + p.ordinal()].getPiece();
  }

  public static long getPieceBoard(int i, int c) {
    return pieces[c * PIECES + i].getPiece();
  }

  public static MoveType whichMoveType(String move, int colour) { // Determine which move type
    int from = Utils.getShiftFrom(move);
    int to = Utils.getShiftTo(move);

    // Castle - Promotion - En Passant
    if (!CWL || !CWS || !CBL || !CBS) { // If moving a king to a rook
      if (isPositionOcc(getPieceBoard(PieceType.KING, colour), from)
          && isPositionOcc(getPieceBoard(PieceType.ROOK, colour), to)) {
        return MoveType.CASTLE; // Castle may be possible
      }
    }

    if (isPositionOcc(getPieceBoard(PieceType.PAWN, colour), from))
      if ((Utils.getShiftTo(move) < 8) || (Utils.getShiftTo(move) > 55)) 
        return MoveType.PROMOTION; // Promotion may be possible

    if (!MoveHistory.isEmpty() && isPositionOcc(getPieceBoard(PieceType.PAWN, colour), from)) {
      if ((MoveHistory.peekNext().getPiece() == PieceType.PAWN)) {
        Move prevMove = MoveHistory.peekNext();
        if (Math.abs(prevMove.getFromY() - prevMove.getToY()) == 2) {
          // If the last move was a double pawn push - enpassant possible
          // if moving for a capture on opposite colour & with the same x as opposing players
          // pawn, en passant
          int shift = Utils.bitPosition(colour == 0 ? Moves.northOne(1L << to) : Moves.southOne(1L << to));
          if (isPositionOcc(getPieceBoard(PieceType.PAWN, colour ^ 1), shift))
            return MoveType.ENPASSANT; // En passant may be possible
        }
      }
    }

    int pieceIndex1 = -1, pieceIndex2 = -1;
    // Move - Capture
    for (int i = 0; i < PIECES; i++) {
      if (isPositionOcc(getPieceBoard(i, colour), from))
        pieceIndex1 = i;
      if (isPositionOcc(getPieceBoard(i, colour ^ 1), to))
        pieceIndex2 = i;
    }

    return pieceIndex1 != -1 && pieceIndex2 != -1 ? MoveType.CAPTURE : MoveType.MOVE;
  }

  public static boolean validMove(Move m) {
    if (m != null) {
      int f = m.getShiftFrom();
      int t = m.getShiftTo();
      int pl = m.getPlayer();
      PieceType piece = m.getPiece();

      long bb = getBB(m.getMoveReg(), pl) & (1L << f); // Single Bit isolation at click
      if (isPositionOcc(getPieceBoard(piece, pl), f)) {
        boolean isValid = Type.getMoves(bb, piece, f, t, empty, pl);
        if (isValid) {
          if (piece == PieceType.ROOK) { // If rook moves, prevent castle accordingly
            if (BoardConstants.INITIAL_ROOK_POSITIONS[0 + (pl * 2)] == f) {
              if (pl == 0)
                CWL = true;
              else
                CBL = true;
            } else if (BoardConstants.INITIAL_ROOK_POSITIONS[1 + (pl * 2)] == f) {
              if (pl == 0)
                CWS = true;
              else
                CBS = true;
            }
          } else if (piece == PieceType.KING) {
            if (pl == 0) // If the king is moved, prevent all of castles for player
              CWS = CWL = true;
            else
              CBS = CBL = true;
          }
        }
        return isValid; // Return the valid flag
      }
    }
    return false; // Not a valid move
  }

  public static boolean validCapture(Move m) {
    if (m != null) {
      int f = m.getShiftFrom();
      int t = m.getShiftTo();
      int pl = m.getPlayer();
      PieceType piece = m.getPiece();

      long bb = getBB(m.getMoveReg(), pl) & (1L << f); // isolate the PieceType in the bitboard
      if (isPositionOcc(getPieceBoard(piece, pl), f)) { // Is the current position occupied
        return Type.getCaptures(bb, piece, f, t, empty, wOccupied, bOccupied, pl);
      }
    }
    return false;
  }

  public static boolean validCastle(String move, int colour, int longShort) {
    long unsafe = getUnsafe(colour); // Get all unsafe positions for the current player

    if ((unsafe & getPieceBoard(PieceType.KING, colour)) == 0) {
      if (getPlayer() == 0) { // The following determines if the castleing positions are not being attacked
        if ((CWL == false) && (longShort == 0) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[0])
            & getPieceBoard(PieceType.ROOK, colour)) != 0)) {
          if (((~empty | (unsafe & (1L << 58))) & ((1L << 57) | (1L << 58) | (1L << 59))) == 0) {
            return true;
          }
        }
        if ((CWS == false) && (longShort == 1) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[1])
            & getPieceBoard(PieceType.ROOK, colour)) != 0)) {
          if ((((~empty) | unsafe) & ((1L << 62) | (1L << 61))) == 0) {
            return true;
          }
        }
      } else {
        if ((CBL == false) && (longShort == 0) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[2])
            & getPieceBoard(PieceType.ROOK, colour)) != 0)) {
          if (((~empty | (unsafe & (1L << 2))) & ((1L << 1) | (1L << 2) | (1L << 3))) == 0) {
            return true;
          }
        }
        if ((CBS == false) && (longShort == 1) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[3])
            & getPieceBoard(PieceType.ROOK, colour)) != 0)) {
          if (((~empty | unsafe) & ((1L << 5) | (1L << 6))) == 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static boolean validPromotion(Move m) {
    return Type.getPieceCapAndMove(m.getPlayer(), empty, wOccupied, bOccupied, (1L << Utils.getShiftFrom(m.getMoveReg())), PieceType.PAWN) >> (Utils.getShiftTo(m.getMoveReg()) & 1) == 1;
  }

  public static PieceType getBBIndex(String move, int colour) {
    int squareIndex = Utils.getShiftFrom(move);
    int i = getBBPiece(squareIndex, colour);
    return i != -1 ? PieceType.values()[i] : null; // Gets the captured PieceType
  }

  public static PieceType getBBCapIndex(String move, int colour) {
    int squareIndex = Utils.getShiftTo(move);
    int i = getBBPiece(squareIndex, colour ^ 1); // Gets the captured PieceType
    return i != -1 ? PieceType.values()[i] : null;
  }

  public static PieceType getBBIndex(int rank, int file, int colour) {
    int squareIndex = Utils.convert2D1D(rank, file);
    int i = getBBPiece(squareIndex, colour); // Gets the captured PieceType
    return i != -1 ? PieceType.values()[i] : null;
  }

  private static long getBB(int rank, int file, int colour) {
    int squareIndex = Utils.convert2D1D(rank, file); // Convert co-ordinates to square index
    int i = getBBPiece(squareIndex, colour);
    return i != -1 ? getPieceBoard(i, colour) : 0L;
  }

  private static long getBB(String move, int colour) {
    int squareIndex = Utils.getShiftFrom(move); // Based on move, get square index
    int bb = getBBPiece(squareIndex, colour);
    return getPieceBoard(bb, colour);
  }

  private static int getBBPiece(int squareIndex, int colour) {
    for (int i = 0; i < PIECES; i++) {
      if (isPositionOcc(getPieceBoard(i, colour), squareIndex))
        return i;
    }
    return -1; // Return -1 to validate
  }

  private static long getUnsafe(int player) {
    return Type.getUnsafe(player, empty); // Gets all unsafe positions
  }

  public static void clear() {
    pieces = new Pair[PIECES << 1];
    MoveHistory.clear(); // Removes all past moves that have been played
  }

  public static void reset() {
    clear(); // Resets the bitboard
    initChess(); // Reset bitboards with initial positions
  }

  public static long getWOcc() {
    return wOccupied;
  } // Gets whites pieces bitboard

  public static long getBOcc() {
    return bOccupied;
  } // Gets blacks populated bitboard

  public static void setOccupied() { // Sets occupied bitboard of all pieces
    wOccupied = bOccupied = 0L;
    for (int i = 0; i < PIECES; i++)
      wOccupied |= getPieceBoard(i, 0);
    wOccupied |= getPieceBoard(PieceType.KING, 1);

    for (int i = 0; i < PIECES; i++)
      bOccupied |= getPieceBoard(i, 1);
    bOccupied |= getPieceBoard(PieceType.KING, 0);

    aOccupied = wOccupied | bOccupied;
    setEmpty(~aOccupied); // Sets the empty bitboard
  }

  public static long getEmpty() {
    return empty;
  }

  public static void setEmpty(long empty) {
    Board.empty = empty;
  }

  public static boolean getCWL() {
    return CWL;
  }

  public static boolean getCWS() {
    return CWS;
  }

  public static boolean getCBL() {
    return CBL;
  }

  public static boolean getCBS() {
    return CBS;
  }

  public static void setCWL(boolean CWL) {
    Board.CWL = CWL;
  }

  public static void setCWS(boolean CWS) {
    Board.CWS = CWS;
  }

  public static void setCBL(boolean CBL) {
    Board.CBL = CBL;
  }

  public static void setCBS(boolean CBS) {
    Board.CBS = CBS;
  }

  public static int getPlayer() {
    return Board.whichPlayer;
  }

  public static void setPlayer(int whichPlayer) {
    Board.whichPlayer = whichPlayer;
  } 

  public static boolean isCreating() {
    return Board.isCreating;
  }

  public static void setCreating(boolean isCreating) {
    Board.isCreating = isCreating;
  }

  public int getHumanIsWhite() {
    return human;
  }

  public void setHumanIsWhite(int human) {
    Board.human = human;
  }

  public static String getPossibleMoves(int mX, int mY, int sSize) {
    setOccupied();

    String possMoves = "";
    int rank = mX / sSize;
    int file = mY / sSize;
 
    PieceType piece = getBBIndex(rank, file, whichPlayer);

    long bb = getBB(rank, file, whichPlayer) & (1L << Utils.convert2D1D(rank, file));
    // Set the bb with the possible moves for the selected PieceType
    if (bb != 0L) {
      bb = Type.getPieceCapAndMove(whichPlayer, empty, wOccupied, bOccupied, bb, piece);
      bb &= ~getPieceBoard(PieceType.KING, whichPlayer); // Remove the king from the selected pieces
      while (bb != 0) { // Construct a list of moves from the resultant bitboard
        int bitPos = Utils.bitPosition(bb);
        possMoves += "" + (bitPos % 8) + (bitPos / 8);
        bb &= bb - 1; // Get the next bit
      }

    }
    System.out.println(piece + "  " + possMoves);
    return possMoves; // Retun string of all possible moves
  }

  public static List<Move> getAIMoves(int player) { // Gets all possible AI moves for all pieces
    long bb = 0L;
    String possibleMove = "";
    List<Move> resultMoves = new ArrayList<Move>();
    for (int i = 0; i < PIECES; i++) {
      long pieceBB = getPieceBoard(i, player);
      while (pieceBB != 0) { // Loop through all bits in the bitboard
        int bitPosIsolated = Utils.bitPosition(pieceBB); // get the bit position
        long pieceBBIsolated = pieceBB & (1L << bitPosIsolated);

        bb = Type.getPieceCapAndMove(player, empty, wOccupied, bOccupied, pieceBBIsolated, PieceType.values()[i]);
        bb &= ~getPieceBoard(PieceType.KING, player); // Remove the king from the selected pieces

        while (bb != 0) {
          int bitPos = Utils.bitPosition(bb); // For all possible moves, generate the moves in correct format
          possibleMove = "" + bitPosIsolated / 8 + bitPosIsolated % 8 + bitPos / 8 + bitPos % 8;
          MoveType moveType = whichMoveType(possibleMove, player);
          PieceType piece = null;
          PieceType pieceCap = null;
          // Based on the move type, assign the variable accordingly
          if (moveType == MoveType.CAPTURE || moveType == MoveType.MOVE)
            piece = getBBIndex(possibleMove, player);
          if (moveType == MoveType.CAPTURE || moveType == MoveType.PROMOTION)
            pieceCap = getBBCapIndex(possibleMove, player);
          if (moveType == MoveType.PROMOTION) // Force the Ai to promote to queen (almost always the best move)
            piece = PieceType.QUEEN;


          resultMoves.add(new Move(possibleMove, moveType, player, piece, pieceCap));
          bb &= bb - 1; // Get the next possible position of the current PieceType in the bitboard
        }
        pieceBB &= pieceBB - 1; // Get the next piece  from the biboard
      }
    }
    return resultMoves; // Return the list of move objects generated
  }

  public static boolean kingInCheck(int player) {
    long k = getPieceBoard(PieceType.KING, player);
    long atk = Type.getUnsafe(player, empty); // Gets all unsafe locations
     return (atk & k) != 0; // return true if king is being attacked by a PieceType
  }

  public static boolean kingInCheckmate(int player) {
    if (quickCheckmate(player)) {
      boolean checkmate = true;
      List<Move> oneply = getAIMoves(player);
      for (Move m : oneply) {
        if (checkmate) {
          Moves.applyMove(m, player, m.getType(), m.getMoveReg(), false);
          if (!kingInCheck(player))
            checkmate = false;
          Moves.undoMove(player, m);
        }
      }
      return checkmate;
    }
    return false;
  }

  private static boolean quickCheckmate(int player) {
    long atk = Type.getUnsafe(player, empty);
    long k = Type.getPieceCapAndMove(player, empty, wOccupied, bOccupied,
        getPieceBoard(PieceType.KING, player), PieceType.KING);
    long r = k & atk;
    return r != 0;
  }

  private static boolean isPositionOcc(long bb, int shift) {
    return ((bb >> shift) & 1) == 1;
  }
}
