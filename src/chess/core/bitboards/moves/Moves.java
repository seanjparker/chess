package chess.core.bitboards.moves;

import javax.swing.JOptionPane;
import chess.core.bitboards.Board;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.MoveType;
import chess.core.bitboards.moves.pieces.PieceType;
import chess.core.utils.Utils;

public class Moves {

  public static boolean applyMove(Move m, int p, MoveType moveType, String move, boolean add) {
    Move playerMove = null;

    if (m == null) {
      PieceType captureIndex = Board.getBBCapIndex(move, p); // Gets the piece being captured
      PieceType pieceIndex = Board.getBBIndex(move, p); // Gets the piece being moved
      if (pieceIndex != null) { // Only create a move if a valid move
        playerMove = new Move(move, moveType, p, pieceIndex, captureIndex);
      }
    } else { // If an AI move, dont create a move object
      playerMove = m;
    }

    switch (moveType) {
      case CAPTURE: // capture
        return capture(playerMove, add);
      case MOVE: // move
        return move(playerMove, add);
      case CASTLE: // castle
        return castle(playerMove, add);
      case ENPASSANT: // en passant
        return enPassant(playerMove, add);
      case PROMOTION: // promotion
        return promotion(playerMove, add);
    }
    return false;
  }

  private static boolean move(Move m, boolean add) {
    if (Board.validMove(m)) { // Check to see if move is valid
      Moves.applyPieces(m.getPiece(), null, m.getPlayer(), getFrom(m.getMoveReg()), getTo(m.getMoveReg()), false);
      if (add)
        MoveHistory.addItem(m);
    } else {
      System.err.println("Invalid Move");
      return false;
    }
    return true;
  }

  private static boolean capture(Move m, boolean add) {
    if (Board.validCapture(m)) { // Check to see if capture is valid
      applyPieces(m.getPiece(), m.getPieceC(), m.getPlayer(), getFrom(m.getMoveReg()), getTo(m.getMoveReg()), true);
      if (add)
        MoveHistory.addItem(m);
    } else {
      System.err.println("Invalid Capture");
      return false;
    }
    return true;
  }

  private static boolean castle(Move m, boolean add) {
    // longShort --> long (Queen Side Castle) = 0, short (King Side Castle) = 1 [WHITE]
    // longShort --> long (Queen Side Castle) = 0, short (King Side Castle) = 1 [BLACK]

    int longShort = -1;
    int player = m.getPlayer();
    String move = m.getMoveReg();
    int shiftTo = Utils.getShiftTo(move);

    // Determine if the move is queenside or king side
    if (player == 0) {
      if ((Board.getCWL() == false) && (shiftTo == BoardConstants.INITIAL_ROOK_POSITIONS[0])) {
        longShort = 0; // If white castle to the left
      }
      if ((Board.getCWS() == false) && (shiftTo == BoardConstants.INITIAL_ROOK_POSITIONS[1])) {
        longShort = 1; // If white castle to the right
      }
    } else {
      if ((Board.getCBL() == false) && (shiftTo == BoardConstants.INITIAL_ROOK_POSITIONS[2])) {
        longShort = 0; // If black castle to the left
      }
      if ((Board.getCBS() == false) && (shiftTo == BoardConstants.INITIAL_ROOK_POSITIONS[3])) {
        longShort = 1; // If black castle to the right
      }
    }

    if (longShort >= 0) {
      if (Board.validCastle(move, player, longShort)) { // Check to see if castle if valid
        long fromBBR, toBBR, fromToBBR, fromBBK, toBBK, fromToBBK;
        int k = longShort + (player * 2); // Get index based on colour

        fromBBR = (1L << BoardConstants.INITIAL_ROOK_POSITIONS[k]);
        toBBR = (1L << BoardConstants.FINAL_ROOK_POSITIONS[k]);
        fromToBBR = fromBBR ^ toBBR; // Gets the from and to bitboard for the rook

        fromBBK = (1L << BoardConstants.INITIAL_KING_POSITIONS[k]);
        toBBK = (1L << BoardConstants.FINAL_KING_POSITIONS[k]);
        fromToBBK = fromBBK ^ toBBK; // Gets the from and to bitboard for the king

        Board.pieces[PieceType.ROOK.ordinal() * (player + 1)].xorWith(fromToBBR); // Applies the bitboard to rook by XOR
        Board.pieces[PieceType.KING.ordinal() * (player + 1)].xorWith(fromToBBK); // Applies the bitboard to king by XOR

        setGeneral((fromToBBK | fromToBBR), player ^ 1); // Sets occupied and empty bitboards
        if (add)
          MoveHistory.addItem(new Move(move, MoveType.CASTLE, player, PieceType.KING, null, longShort));

        setCastleStatus(longShort, player, true);
      } else {
        System.err.println("Invalid Castle");
        return false;
      }
    }
    return true;
  }

  private static boolean enPassant(Move m, boolean add) {
    long toBB = 0L, fromBB = 0L, fromToBB = 0L, fBoard = 0L;
    fromBB = 1L << Utils.getShiftFrom(m.getMoveReg());
    toBB = 1L << Utils.getShiftTo(m.getMoveReg());
    fromToBB = fromBB ^ toBB;

    Board.pieces[PieceType.PAWN.ordinal() * (m.getPlayer() + 1)].xorWith(fromToBB);

    fBoard = m.getPlayer() == 0 ? northOne(toBB) : southOne(toBB);
    Board.pieces[PieceType.PAWN.ordinal() * ((m.getPlayer() ^ 1) + 1)].xorWith(fBoard);

    setGeneral((fromToBB | fBoard), m.getPlayer() ^ 1); // Sets the occupied and empty bitboards
    if (add)
      MoveHistory.addItem(m);
    return true;
  }

  private static boolean promotion(Move m, boolean add) {
    if (Board.validPromotion(m)) {
      PieceType r = m.getPiece() == PieceType.PAWN ? promotionDialog() : m.getPiece();

      long toBB = 0L, fromBB = 0L, fromToBB = 0L;
      fromBB = (1L << Utils.getShiftFrom(m.getMoveReg()));
      toBB = (1L << Utils.getShiftTo(m.getMoveReg()));
      fromToBB = fromBB ^ toBB;

      Board.pieces[r.ordinal() * (m.getPlayer() + 1)].xorWith(toBB); // Updates the promotion bitboard
      Board.pieces[PieceType.PAWN.ordinal() * (m.getPlayer() + 1)].xorWith(fromBB);

      if (m.getPieceC() != null) // Capture and promotion -- diagonal move + promotion
        Board.pieces[m.getPieceC().ordinal() * ((m.getPlayer() ^ 1) + 1)].xorWith(toBB);

      setGeneral(fromToBB, m.getPlayer() ^ 1);
      if (add)
        MoveHistory.addItem(new Move(m.getMoveReg(), MoveType.PROMOTION, m.getPlayer(), r, m.getPieceC()));

    } else {
      System.err.println("Invalid Promotion");
      return false;
    }
    return true;
  }

  private static PieceType promotionDialog() {
    Object[] poss = {"Queen", "Knight", "Rook", "Bishop"}; // Object for choices in drop-down
    String s = (String) JOptionPane.showInputDialog(null, "Choose the piece to promote to:",
        "Promotion Menu", JOptionPane.INFORMATION_MESSAGE, null, poss, "Queen");
    if ((s != null) && (s.length() > 0)) { // Shows the pieces in a drop down
      switch (s) {
        case "Queen":
          return PieceType.QUEEN;
        case "Knight":
          return PieceType.KNIGHT;
        case "Rook":
          return PieceType.ROOK;
        case "Bishop":
          return PieceType.BISHOP;
      }
    }
    return PieceType.QUEEN; // Default is queen
  }

  public static void redoMove(int player) {
    if (MoveHistory.getSizeOld() > 0) {
      Move p = MoveHistory.getNextOld().flipMove();
      if (p.getType() == MoveType.CASTLE) { // Castle
        // This is the same as making a move, except the player is flipped
        int k = p.getLS() + ((player ^ 1) * 2);
        long fromToBBK = (1L << BoardConstants.INITIAL_KING_POSITIONS[k])
            ^ (1L << BoardConstants.FINAL_KING_POSITIONS[k]);
        long fromToBBR = (1L << BoardConstants.INITIAL_ROOK_POSITIONS[k]
            ^ (1L << BoardConstants.FINAL_ROOK_POSITIONS[k]));

        Board.pieces[PieceType.ROOK.ordinal() * ((player ^ 1) + 1)].xorWith(fromToBBR);
        Board.pieces[PieceType.KING.ordinal() * ((player ^ 1) + 1)].xorWith(fromToBBK);

        setGeneral((fromToBBR | fromToBBK), player);
        setCastleStatus(p.getLS(), player, true); // Sets the castle status to prevent castle
      } else if (p.getType() == MoveType.PROMOTION) { // Promotion
        long toBB = 0L, fromBB = 0L, fromToBB = 0L;
        fromBB = (1L << p.getShiftFrom());
        toBB = (1L << p.getShiftTo());
        fromToBB = fromBB ^ toBB;

        Board.pieces[p.getPiece().ordinal() * ((player ^ 1) + 1)].xorWith(toBB);
        Board.pieces[PieceType.PAWN.ordinal() * ((player ^ 1) + 1)].xorWith(fromBB);

        if (p.getPieceC() != null) //If contained a capture, place back captured piece
          Board.pieces[p.getPieceC().ordinal() * (player + 1)].xorWith(toBB);

        setGeneral(fromToBB, player);
      } else if (p.getType() == MoveType.ENPASSANT) { //En Passant
        long toBB = 0L, fromBB = 0L, fromToBB = 0L;
        fromBB = 1L << p.getShiftFrom();
        toBB = 1L << p.getShiftTo();
        fromToBB = fromBB ^ toBB;

        // Place captured piece back in correct location
        Board.pieces[PieceType.PAWN.ordinal() * (p.getPlayer() + 1)].xorWith(fromToBB);
        if (p.getPlayer() == 0) {
          long toRBB = northOne(fromBB);
          Board.pieces[PieceType.PAWN.ordinal() * ((p.getPlayer() ^ 1) + 1)].xorWith(toRBB);
          setGeneral(toRBB | fromToBB, player); // Sets the occupied and empty bitboards
        } else {
          long toRBB = southOne(fromBB);
          Board.pieces[PieceType.PAWN.ordinal() * ((p.getPlayer() ^ 1) + 1)].xorWith(toRBB);
          setGeneral(toRBB | fromToBB, player);
        }
      } else { // Move or capture
        long fromBB = (1L << p.getShiftFrom());
        long toBB = (1L << p.getShiftTo());
        long fromToBB = fromBB ^ toBB;
        // Standard move, place the piece back as normal move
        Board.pieces[p.getPiece().ordinal() * ((player ^ 1) + 1)].xorWith(fromToBB);

        if (p.getType() == MoveType.CAPTURE) {
          Board.pieces[p.getPieceC().ordinal() * (player + 1)].xorWith(toBB);
        } // Capured piece gets removed

        setGeneral(fromToBB, player); // Sets occupied and empty + change current player
      }
    } else {
      System.err.println("Cannot Redo Move");
    }
  }

  public static void undoMove(int player, Move m) {
    if (MoveHistory.getSize() > 0) {
      Move p;
      if (m == null) {
        p = MoveHistory.getNext().flipMove(); // Flips the move, makes it easier to undo the move
      } else {
        p = m.flipMove(); // If move has been provided, dont get from move history
      }

      if (p.getType() == MoveType.CASTLE) { // Castle
        int k = p.getLS() + (player * 2);
        long fromToBBK = (1L << BoardConstants.INITIAL_KING_POSITIONS[k]) ^ (1L << BoardConstants.FINAL_KING_POSITIONS[k]);
        long fromToBBR = (1L << BoardConstants.INITIAL_ROOK_POSITIONS[k] ^ (1L << BoardConstants.FINAL_ROOK_POSITIONS[k]));

        Board.pieces[PieceType.ROOK.ordinal() * (player + 1)].xorWith(fromToBBR); // Sets piece to previous location
        Board.pieces[PieceType.KING.ordinal() * (player + 1)].xorWith(fromToBBK);

        setGeneral((fromToBBR | fromToBBK), player);
        setCastleStatus(p.getLS(), player, false); // Updates castle ability

      } else if (p.getType() == MoveType.PROMOTION) { // Promotion
        long toBB = 0L, fromBB = 0L, fromToBB = 0L;
        fromBB = (1L << p.getShiftFrom());
        toBB = (1L << p.getShiftTo());
        fromToBB = fromBB ^ toBB;

        Board.pieces[p.getPiece().ordinal() * (player + 1)].xorWith(fromBB); // Removes the promoted piece
        Board.pieces[PieceType.PAWN.ordinal() * (player + 1)].xorWith(toBB); // Adds a pawn to the board

        if (p.getPieceC() != null)
          Board.pieces[p.getPieceC().ordinal() * ((player ^ 1) + 1)].xorWith(fromBB);
        setGeneral(fromToBB, player); // Updates occupied and empty bitboards

      } else if (p.getType() == MoveType.ENPASSANT) { // En Passant
        long toBB = 0L, fromBB = 0L, fromToBB = 0L;
        fromBB = 1L << p.getShiftFrom();
        toBB = 1L << p.getShiftTo();
        fromToBB = fromBB ^ toBB;

        // Place captured piece back in correct location
        Board.pieces[PieceType.PAWN.ordinal() * (p.getPlayer() + 1)].xorWith(fromToBB);
        if (p.getPlayer() == 0) {
          Board.pieces[PieceType.PAWN.ordinal() * ((p.getPlayer() ^ 1) + 1)].xorWith(northOne(fromBB));
          setGeneral(northOne(fromBB) | fromToBB, player);
        } else {
          Board.pieces[PieceType.PAWN.ordinal() * ((p.getPlayer() ^ 1) + 1)].xorWith(southOne(fromBB));
          setGeneral(southOne(fromBB) | fromToBB, player);
        }
      } else { // Move or capture
        long toBB = 0L, fromBB = 0L, fromToBB = 0L;
        int piece = p.getPiece().ordinal(); // Get the correct piece from the previous move
        fromBB = (1L << p.getShiftFrom());
        toBB = (1L << p.getShiftTo());
        fromToBB = fromBB ^ toBB;

        Board.pieces[piece * (player + 1)].xorWith(fromToBB);

        if (p.getType() == MoveType.CAPTURE)
          Board.pieces[p.getPieceC().ordinal() * ((player ^ 1) + 1)].xorWith(fromBB);

        setGeneral(fromToBB, player);
      }
    } else {
      System.err.println("Cannot Undo Move"); // Tells the user the undo action cannot be performed
    }
  }

  public static void forfitCurrent() {
    int v = Utils.showDialog("Forfeit", "Are you sure you want to forfit?", new Object[] {"Yes", "No"});
    if ((v != -1) && (v == 0)) { // Allows the player to forfeit at any point in the game
      if (Board.getPlayer() == 0) {
        v = Utils.showDialog("Forefit", "White has forfeit the match! \nBlack has won.\nGame Over");
      } else {
        v = Utils.showDialog("Forefit", "Black has forfeit the match! \nWhite has won.\nGame Over");
      }
      if ((v != -1) && (v == 0)) //Reset bitboards
        Board.reset();
    }
  }

  private static long getFrom(String move) {
    return 1L << Utils.getShiftFrom(move);
  } // Forms bitboard based on shift

  private static long getTo(String move) {
    return 1L << Utils.getShiftTo(move);
  }

  private static void applyPieces(PieceType piece, PieceType pieceC, int player, long fBB, long tBB, boolean capture) {
    long fTBB = fBB ^ tBB;
    Board.pieces[piece.ordinal() * (player + 1)].xorWith(fTBB); // Applies the data to board based on bitboards
    if (capture)
      Board.pieces[pieceC.ordinal() * ((player ^ 1) + 1)].xorWith(tBB);
    setGeneral(fTBB, player ^ 1); // Sets the current player, empty and occupied bitboards
  }

  private static void setGeneral(long ftBB, int player) {
    Board.setEmpty(Board.getEmpty() ^ ftBB); // Sets current empty bitboard
    Board.setOccupied(); // Sets the empty bitboard
    Board.setPlayer(player); // Sets the current player
  }

  private static void setCastleStatus(int ls, int p, boolean s) {
    if (ls == 0) { // Sets long and short ability based on player colour
      if (p == 0) {
        Board.setCWL(s); // White queen side
      } else {
        Board.setCBL(s); // Black queen side
      }
    } else {
      if (p == 0) {
        Board.setCWS(s); // White king side
      } else {
        Board.setCBS(s); // Black king side
      }
    }
  }

  public static long southOne(long b) {
    return b >> 8;
  } // -8

  public static long northOne(long b) {
    return b << 8;
  } // +8

  public static long eastOne(long b) {
    return (b << 1) & BoardConstants.FILE_A;
  } // +1 and avoid wrap on H

  public static long noEaOne(long b) {
    return (b << 9) & BoardConstants.FILE_A;
  } // +9 and avoid wrap on H

  public static long soEaOne(long b) {
    return (b >> 7) & BoardConstants.FILE_A;
  } // -7 and avoid wrap on H

  public static long westOne(long b) {
    return (b >> 1) & BoardConstants.FILE_H;
  } // -1 and avoid wrap on A

  public static long soWeOne(long b) {
    return (b >>> 9) & BoardConstants.FILE_H;
  } // -9 and avoid wrap on A

  public static long noWeOne(long b) {
    return (b << 7) & BoardConstants.FILE_H;
  } // +7 and avoid wrap on A

  public static long noNoEa(long b) {
    return (b << 17) & BoardConstants.FILE_A;
  } // north north east knight

  public static long noEaEa(long b) {
    return (b << 10) & BoardConstants.FILE_AB;
  } // north east east knight

  public static long soEaEa(long b) {
    return (b >> 6) & BoardConstants.FILE_AB;
  } // south east east knight

  public static long soSoEa(long b) {
    return (b >> 15) & BoardConstants.FILE_A;
  } // south south east knight

  public static long soSoWe(long b) {
    return (b >> 17) & BoardConstants.FILE_H;
  } // south south west knight

  public static long soWeWe(long b) {
    return (b >> 10) & BoardConstants.FILE_GH;
  } // south west west knight

  public static long noWeWe(long b) {
    return (b << 6) & BoardConstants.FILE_GH;
  } // north west west knight

  public static long noNoWe(long b) {
    return (b << 15) & BoardConstants.FILE_H;
  } // north north west knight

}
