package chess.core.bitboards.eval;

import chess.core.bitboards.Board;
import chess.core.bitboards.Type;
import chess.core.bitboards.moves.pieces.PieceType;
import chess.core.utils.Utils;

public class Evaluation {
  // Piece score tables are used to evaluate the current position of all pieces, higher score =
  // better position
  private static final int PAWN[] = {0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50, 50, 10, 10,
      20, 30, 30, 20, 10, 10, 5, 5, 10, 25, 25, 10, 5, 5, 0, 0, 0, 20, 20, 0, 0, 0, 5, -5, -10, 0,
      0, -10, -5, 5, 5, 10, 10, -20, -20, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0};
  private static final int KNIGHT[] = {-50, -40, -30, -30, -30, -30, -40, -50, -40, -20, 0, 0, 0, 0,
      -20, -40, -30, 0, 10, 15, 15, 10, 0, -30, -30, 5, 15, 20, 20, 15, 5, -30, -30, 0, 15, 20, 20,
      15, 0, -30, -30, 5, 10, 15, 15, 10, 5, -30, -40, -20, 0, 5, 5, 0, -20, -40, -50, -40, -30,
      -30, -30, -30, -40, -50};

  private static final int BISHOP[] =
      {-20, -10, -10, -10, -10, -10, -10, -20, -10, 0, 0, 0, 0, 0, 0, -10, -10, 0, 5, 10, 10, 5, 0,
          -10, -10, 5, 5, 10, 10, 5, 5, -10, -10, 0, 10, 10, 10, 10, 0, -10, -10, 10, 10, 10, 10,
          10, 10, -10, -10, 5, 0, 0, 0, 0, 5, -10, -20, -10, -10, -10, -10, -10, -10, -20};

  private static final int ROOK[] = {0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, 10, 10, 10, 10, 5, -5, 0, 0,
      0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5,
      -5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 5, 5, 0, 0, 0};

  private static final int QUEEN[] = {-20, -10, -10, -5, -5, -10, -10, -20, -10, 0, 0, 0, 0, 0, 0,
      -10, -10, 0, 5, 5, 5, 5, 0, -10, -5, 0, 5, 5, 5, 5, 0, -5, 0, 0, 5, 5, 5, 5, 0, -5, -10, 5, 5,
      5, 5, 5, 0, -10, -10, 0, 5, 0, 0, 0, 0, -10, -20, -10, -10, -5, -5, -10, -10, -20};

  private static final int KING_MID[] = {-30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50,
      -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40,
      -30, -20, -30, -30, -40, -40, -30, -30, -20, -10, -20, -20, -20, -20, -20, -20, -10, 20, 20,
      0, 0, 0, 0, 20, 20, 20, 30, 10, 0, 0, 10, 30, 20};

  private static final int KING_END[] = {-50, -40, -30, -20, -20, -30, -40, -50, -30, -20, -10, 0,
      0, -10, -20, -30, -30, -10, 20, 30, 30, 20, -10, -30, -30, -10, 30, 40, 40, 30, -10, -30, -30,
      -10, 30, 40, 40, 30, -10, -30, -30, -10, 20, 30, 30, 20, -10, -30, -30, -30, 0, 0, 0, 0, -30,
      -30, -50, -30, -30, -30, -30, -30, -30, -50};

  public static int eval(int player) {
    return staticPosition(player);
  }

  private static int staticPosition(int player) {
    int score = 0, material = material(player), oMaterial = material(player ^ 1);
    score += material + pieceSquareTables(player, material);
    score -= oMaterial + pieceSquareTables(player ^ 1, oMaterial);
    score += kSaftey(player); // Checks if the king is in check
    return score; // Returns the score for the node
  }

  private static int material(int player) {
    int s = 0;
    for (PieceType pieceType : PieceType.values())
      s += Type.SCORE[pieceType.ordinal()] * Utils.popCount(Board.getPieceBoard(pieceType.ordinal(), player));
    return s;
  }

  private static int kSaftey(int p) {
    //If the king is in checkmate or check, assign low score to prevent move chosen
    if (Board.kingInCheck(p))
      return Board.kingInCheckmate(p) ? -50000000 : -500000;
    
    return 0; // Otherwise, this should not affect final score
  }

  private static int pieceSquareTables(int player, int material) {
    int s = 0;
    // Piece Placement
    for (PieceType pieceType : PieceType.values()) {
      long bb = Board.getPieceBoard(pieceType, player);
      while (bb != 0) {
        int bitPos = Utils.bitPosition(bb);
        if (player == 1) bitPos ^= 56;
        
        switch(pieceType) {
          case PAWN: s += PAWN[bitPos]; break;
          case BISHOP: s += BISHOP[bitPos]; break;
          case KNIGHT: s += KNIGHT[bitPos]; break;
          case QUEEN: s += QUEEN[bitPos]; break;
          case ROOK: s += ROOK[bitPos]; break;
          case KING: 
            s += Math.floor(material / 100) <= 13 ? 
                KING_END[bitPos] : KING_MID[bitPos];
                break;
        }
        bb &= bb - 1; // Get the next bit in the bitboard
      }
    }
    return s; // Return sum of the final score from all pieces
  }
}
