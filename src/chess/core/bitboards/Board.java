package chess.core.bitboards;

import java.util.ArrayList;
import java.util.List;
import chess.core.bitboards.Type.Piece;
import chess.core.bitboards.moves.Move;
import chess.core.bitboards.moves.MoveHistory;
import chess.core.bitboards.moves.Moves;
import chess.core.initialize.BitboardInit;
import chess.core.utils.Utils;

public class Board {
  public static Pair[] pieces = new Pair[12];

  private static boolean isCreating = false;

  private static int human = 0; // 0 = White, 1 = Black
  private static int whichPlayer = human; // 0 = White, 1 = Black

  private static boolean CWL = false, // Queen Side Castle (<-- Left) -- White
      CWS = false, // King Side Castle (--> Right) -- White
      CBL = false, // Queen Side Castle (<-- Left) -- Black
      CBS = false; // King Side Castle (--> Right) -- Black

  private static long wOccupied = 0L; // White's occupied bitboards
  private static long bOccupied = 0L; // Blacks occupied
  private static long aOccupied = 0L; // Every piece
  private static long empty = 0L; // The empty pieces = ~aOccupied

  public static void initChess() { // Initalize the bitboards
    BitboardInit.initBitboards(); // Set bitboards
    setOccupied(); // Sets the occupied pieces

    CWL = false;
    CWS = false; // Sets the castling flags
    CBL = false;
    CBS = false;

    human = 0; // White moves first
    whichPlayer = human;

    MoveHistory.clear(); // Clears all previous moves
  }
  
  public static Pair getPiecePair(int index) {
    return pieces[index];
  }
  public static long getPieceBoard(int index) {
    return pieces[index].getPiece();
  }
  public static long getPieceBoard(Piece p, int c) {
    return pieces[c == 0 ? p.id : (pieces.length / 2) + p.id].getPiece();
  }
  public static long getPieceBoard(int i, int c) {
    return pieces[c == 0 ? i : (pieces.length / 2) + i].getPiece();
  }
  public static int getPieceColour(int index) {
    return pieces[index].getColour();
  }

  public static int whichMoveType(String move, int colour) { // Determine which move type
    int from = Utils.getShiftFrom(move);
    int to = Utils.getShiftTo(move);
    int pieceIndex1 = -1, pieceIndex2 = -1;

    // Castle - Promotion - En Passant
    if (!CWL || !CWS || !CBL || !CBS) { // If moving a king to a rook
      if (isPositionOcc(getPieceBoard(Piece.KING, colour), from)
          && isPositionOcc(getPieceBoard(Piece.ROOK, colour), to)) {
        return 2; // Castle may be possible
      }
    }

    if (isPositionOcc(getPieceBoard(Piece.PAWN, colour), from)) { // Is moving a pawn to top/bottom rank
      if ((Utils.getShiftTo(move) < 8) || (Utils.getShiftTo(move) > 55)) {
        return 4; // Promotion may be possible
      }
    }

    if (!MoveHistory.isEmpty()) { // Check to see if previous move
      if (isPositionOcc(getPieceBoard(Piece.PAWN, colour), from)) {
        if ((MoveHistory.peekNext().getPieceI() == Piece.PAWN.id)) {
          Move prevMove = MoveHistory.peekNext();
          if (Math.abs(prevMove.getFromY() - prevMove.getToY()) == 2) {
            // If the last move was a double pawn push - enpassant possible
            // if moving for a capture on opposite colour & with the same x as opposing players
            // pawn, en passant
            int shift;

            if (colour == 0) {
              shift = Utils.bitPosition(Moves.northOne(1L << to));
            } else {
              shift = Utils.bitPosition(Moves.southOne(1L << to));
            }

            if (isPositionOcc(getPieceBoard(Piece.PAWN, colour ^ 1), shift)) {
              return 3; // En passant may be possible
            }
          }
        }
      }
    }

    // Move - Capture
    for (int i = 0; i < pieces.length / 2; i++) {
      if (isPositionOcc(getPieceBoard(i, colour), from)) {
        pieceIndex1 = i; // Gets the piece index at current location
      }
      if (isPositionOcc(getPieceBoard(i, colour ^ 1), to)) {
        pieceIndex2 = i; // Gets the piece index where moving to
      }
    }

    if ((pieceIndex1 != -1) && (pieceIndex2 != -1)) {
      return 0; // Capture
    } else {
      return 1; // Normal move
    }
  }

  public boolean validMove(Move m) {
    if (m != null) {
      int f = m.getShiftFrom();
      int t = m.getShiftTo();
      int pl = m.getPlayer();
      int piece = m.getPieceI();

      long bb = getBB(m.getMoveReg(), pl) & (1L << f); // Single Bit isolation at click
      if (isPositionOcc(getPieceBoard(piece, pl), f)) {
        boolean isValid = Type.getMoves(bb, piece, f, t, empty, pl);
        if (isValid) {
          if (piece == Piece.ROOK.id) { // If rook moves, prevent castle accordingly
            if (BoardConstants.INITIAL_ROOK_POSITIONS[0 + (pl * 2)] == f) {
              if (pl == 0) {
                CWL = true;
              } else {
                CBL = true;
              }
            } else if (BoardConstants.INITIAL_ROOK_POSITIONS[1 + (pl * 2)] == f) {
              if (pl == 0) {
                CWS = true;
              } else {
                CBS = true;
              }
            }
          } else if (piece == Piece.KING.id) {
            if (pl == 0) { // If the king is moved, prevent all of castles for player
              CWS = true;
              CWL = true;
            } else {
              CBS = true;
              CBL = true;
            }
          }
        }
        return isValid; // Return the valid flag
      }
    }
    return false; // Not a valid move
  }

  public boolean validCapture(Move m) {
    if (m != null) {
      int f = m.getShiftFrom();
      int t = m.getShiftTo();
      int pl = m.getPlayer();
      int piece = m.getPieceI();

      long bb = getBB(m.getMoveReg(), pl) & (1L << f); // isolate the piece in the bitboard
      if (isPositionOcc(getPieceBoard(piece, pl), f)) { // Is the current position occupied
        return Type.getCaptures(bb, piece, f, t, empty, wOccupied, bOccupied, pl);
      }
    }
    return false;
  }

  public boolean validCastle(String move, int colour, int longShort) {
    long unsafe = getUnsafe(colour); // Get all unsafe positions for the current player

    if ((unsafe & getPieceBoard(Piece.KING, colour)) == 0) {
      if (getPlayer() == 0) { // The following determines if the castleing positions are not being
                              // attacked
        if ((CWL == false) && (longShort == 0) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[0])
            & getPieceBoard(Piece.ROOK, colour)) != 0)) {
          if (((~empty | (unsafe & (1L << 58))) & ((1L << 57) | (1L << 58) | (1L << 59))) == 0) {
            return true;
          }
        }
        if ((CWS == false) && (longShort == 1) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[1])
            & getPieceBoard(Piece.ROOK, colour)) != 0)) {
          if ((((~empty) | unsafe) & ((1L << 62) | (1L << 61))) == 0) {
            return true;
          }
        }
      } else {
        if ((CBL == false) && (longShort == 0) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[2])
            & getPieceBoard(Piece.ROOK, colour)) != 0)) {
          if (((~empty | (unsafe & (1L << 2))) & ((1L << 1) | (1L << 2) | (1L << 3))) == 0) {
            return true;
          }
        }
        if ((CBS == false) && (longShort == 1) && (((1L << BoardConstants.INITIAL_ROOK_POSITIONS[3])
            & getPieceBoard(Piece.ROOK, colour)) != 0)) {
          if (((~empty | unsafe) & ((1L << 5) | (1L << 6))) == 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean validEnPassant(Move m) {
    return true; // The enpassant is already validated, therefore always return true
  }

  public boolean validPromotion(Move m) {
    if ((Type.getPieceCapAndMove(m.getPlayer(), empty, wOccupied, bOccupied,
        (1L << Utils.getShiftFrom(m.getMoveReg())), Piece.PAWN.id) >> Utils.getShiftTo(m.getMoveReg())
        & 1) == 1) {
      return true; // Is the promotion a valid move based on moves and captures
    }
    return false;
  }

  public static int getBBIndex(int rank, int file, int colour) {
    int squareIndex = Utils.convert2D1D(rank, file);
    return getBB(squareIndex, colour); // Gets the bitboard index based on co-ordinate conversion
  }

  public static int getBBIndex(String move, int colour) {
    int squareIndex = Utils.getShiftFrom(move);
    return getBB(squareIndex, colour); // Gets the bitboard index square index
  }

  public static int getBBCapIndex(String move, int colour) {
    int squareIndex = Utils.getShiftTo(move);
    return getBB(squareIndex, colour ^ 1); // Gets the captured piece index
  }

  private static long getBB(int rank, int file, int colour) {
    int squareIndex = Utils.convert2D1D(rank, file); // Convert co-ordinates to square index
    int bb = getBB(squareIndex, colour);
    if (bb != -1) {
      return getPieceBoard(bb, colour);
    } // Gets the entire bitboard based on index and player
    return 0L;
  }

  private static long getBB(String move, int colour) {
    int squareIndex = Utils.getShiftFrom(move); // Based on move, get square index
    int bb = getBB(squareIndex, colour);
    if (bb != -1) {
      return getPieceBoard(bb, colour);
    } // Get the bitboard based on bitboard index
    return 0L;
  }

  private static int getBB(int squareIndex, int colour) {
    for (int i = 0; i < pieces.length / 2; i++) {
      if (isPositionOcc(getPieceBoard(i, colour), squareIndex)) {
        return i;
      }
    }
    return -1; // Return -1 to validate
  }

  private static long getUnsafe(int player) {
    return Type.getUnsafe(player, empty); // Gets all unsafe positions
  }

  public static void clear() {
    pieces = new Pair[6 * 2]; // Clear the whole bitboard
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
    for (int i = 0; i < pieces.length / 2; i++)
      wOccupied |= getPieceBoard(i);
    wOccupied |= getPieceBoard(Piece.KING, 1);
    
    for (int i = pieces.length / 2; i < pieces.length; i++)
      bOccupied |= getPieceBoard(i);
    bOccupied |= getPieceBoard(Piece.KING, 0);
    
    aOccupied = (wOccupied | bOccupied);
    setEmpty(~aOccupied); // Sets the empty bitboard
  }

  public static long getEmpty() {
    return empty;
  } // Returns the empty bitboard

  public static void setEmpty(long empty) {
    Board.empty = empty;
  }

  public static boolean getCWL() {
    return CWL;
  } // Get the castle ability

  public static boolean getCWS() {
    return CWS;
  } // Get the castle ability

  public static boolean getCBL() {
    return CBL;
  } // Get the castle ability

  public static boolean getCBS() {
    return CBS;
  } // Get the castle ability

  public static void setCWL(boolean CWL) {
    Board.CWL = CWL;
  } // Set the castle ability

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
    return whichPlayer;
  } // Gets the current player

  public static void setPlayer(int whichPlayer1) {
    whichPlayer = whichPlayer1;
  } // Sets the current player

  public static boolean isCreating() {
    return isCreating;
  } // Get the flag for user editing the board

  public static void setCreating(boolean isC) {
    Board.isCreating = isC;
  }

  public static int getHumanIsWhite() {
    return human;
  } // Sets is the human player is white (used for 1 player)

  public static void setHumanIsWhite(int humanWhite) {
    human = humanWhite;
  }

  public static String getPossibleMoves(int mX, int mY, int sSize) {
    setOccupied();

    String possMoves = "";
    int rank = (int) (mX / sSize);
    int file = (int) (mY / sSize);

    int pieceI = getBBIndex(rank, file, whichPlayer);

    long bb = getBB(rank, file, whichPlayer) & (1L << Utils.convert2D1D(rank, file));

    // Set the bb with the possible moves for the selected piece
    bb = Type.getPieceCapAndMove(whichPlayer, empty, wOccupied, bOccupied, bb, pieceI);

    bb &= ~Board.getPieceBoard(Piece.KING, whichPlayer); // Remove the king from the selected pieces

    while (bb != 0) { // Construct a list of moves from the resultant bitboard
      int bitPos = Utils.bitPosition(bb);
      possMoves += "" + (int) (bitPos % 8) + (int) (bitPos / 8);
      bb &= bb - 1; // Get the next bit
    }

    return possMoves; // Retun string of all possible moves
  }

  public static List<Move> getAIMoves(int player) { // Gets all possible AI moves for all pieces
    long bb = 0L;
    String possibleMove = "";
    List<Move> resultMoves = new ArrayList<Move>();
    for (int i = 0; i < pieces.length; i++) {
      long pieceBB = Board.getPieceBoard(i, player);
      while (pieceBB != 0) { // Loop through all bits in the bitboard
        int bitPosIsolated = Utils.bitPosition(pieceBB); // get the bit position
        long pieceBBIsolated = pieceBB & (1L << bitPosIsolated);

        bb = Type.getPieceCapAndMove(player, empty, wOccupied, bOccupied, pieceBBIsolated, i);
        bb &= ~Board.getPieceBoard(Piece.KING, player); // Remove the king from the selected pieces

        while (bb != 0) {
          int bitPos = Utils.bitPosition(bb); // For all possible moves, generate the moves in
                                              // correct format
          possibleMove = "" + (int) (bitPosIsolated / 8) + (int) (bitPosIsolated % 8)
              + (int) (bitPos / 8) + (int) (bitPos % 8);
          int moveType = whichMoveType(possibleMove, player);
          int pieceI = -1, pieceCapI = -1;
          // Based on the move type, assign the variable accordingly
          if ((moveType == 0) || (moveType == 1)) {
            pieceI = Board.getBBIndex(possibleMove, player);
          }
          if ((moveType == 0) || (moveType == 4)) {
            pieceCapI = Board.getBBCapIndex(possibleMove, player);
          }
          if (moveType == 4) {
            pieceI = Piece.QUEEN.id;
          } // Force the Ai to promote to queen (almost always the best move)

          resultMoves.add(new Move(possibleMove, moveType, player, pieceI, pieceCapI));
          bb &= bb - 1; // Get the next possible position of the current piece in the bitboard
        }
        pieceBB &= pieceBB - 1; // Get the next piece from the biboard
      }
    }
    return resultMoves; // Return the list of move objects generated
  }

  public static boolean kingInCheck(int player) {
    long k = getPieceBoard(Piece.KING, player);
    long atk = Type.getUnsafe(player, empty); // Gets all unsafe locations
    return (atk & k) != 0; // return true if king is being attacked by a piece
  }

  public static boolean kingInCheckmate(int player) {
    if (quickCheckmate(player)) {
      Moves moves = new Moves();
      boolean checkmate = true;
      List<Move> oneply = getAIMoves(player);
      for (Move m : oneply) {
        if (checkmate) {
          moves.applyMove(m, player, m.getType(), m.getMoveReg(), false);
          if (!kingInCheck(player)) {
            checkmate = false;
          }
          moves.undoMove(player, m);
        }
      }
      return checkmate;
    }
    return false;
  }

  private static boolean quickCheckmate(int player) {
    long atk = Type.getUnsafe(player, empty);
    long k = Type.getPieceCapAndMove(player, empty, wOccupied, bOccupied,
        getPieceBoard(Piece.KING, player), Piece.KING.id);
    long r = k & atk;
    return r != 0;
  }

  private static boolean isPositionOcc(long bb, int shift) {
    return ((bb >> shift) & 1) == 1;
  }
}