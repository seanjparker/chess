package chess.core.bitboards.moves.pieces;

import chess.core.bitboards.BoardConstants;
import chess.core.utils.Utils;

public class Rook extends Piece {
  private long osBoardManipulation(long pieceBB, long empty) {
    long total = 0L;
    if (pieceBB != 0) {
      while (pieceBB != 0) {
        int sliderBit = Utils.bitPosition(pieceBB); //Location of the bit in the bitboard
        long occ = ~empty, rOcc = Long.reverse(occ); //Gets occupied and reversed of occupied
        long bb1 = (1L << sliderBit) * 2, bb2 = Long.reverse(1L << sliderBit) * 2; //Gets 2 * current bit
        long yMask = BoardConstants.FILE_MASKS[sliderBit % 8]; //Rook move generation same as bishop, except different masks
        long xMask = BoardConstants.RANK_MASKS[sliderBit / 8]; //Rook used vertial and horizonal rather than diagonal
        long horizontal = (occ - bb1) ^ Long.reverse(rOcc - bb2); //Gets the possible horizontal moves
        long vertical = ((occ & yMask) - bb1) ^ Long.reverse(Long.reverse(occ & yMask) - bb2);	//Gets the possible vertical moves	
        total |= ((horizontal & xMask) | (vertical & yMask)); //Prevents wrapping and combines bitboard moves
        pieceBB &= pieceBB - 1;
      }
    }
    return total;
  }

  @Override
  public long wGetPossibleTargets(long pieceBB, long empty) {
    return osBoardManipulation(pieceBB, empty) & empty; //Gets moves only
  }
  @Override
  public long bGetPossibleTargets(long pieceBB, long empty) {
    return osBoardManipulation(pieceBB, empty) & empty; //Gets moves only
  }
  @Override
  public long wGetPossibleCaptures(long pieceBB, long bOcc, long empty) {
    return osBoardManipulation(pieceBB, empty) & bOcc; //Gets captures only
  }
  @Override
  public long bGetPossibleCaptures(long pieceBB, long wOcc, long empty) {
    return osBoardManipulation(pieceBB, empty) & wOcc; //Gets captures only
  }
  @Override
  public long wGetPossiblePieces(long pieceBB, long empty, long bOcc) {
    return wGetPossibleTargets(pieceBB, empty) | wGetPossibleCaptures(pieceBB, bOcc, empty); //Gets moves and captures
  }
  @Override
  public long bGetPossiblePieces(long pieceBB, long empty, long wOcc) {
    return bGetPossibleTargets(pieceBB, empty) | bGetPossibleCaptures(pieceBB, wOcc, empty); //Gets moves and captures
  }
}
