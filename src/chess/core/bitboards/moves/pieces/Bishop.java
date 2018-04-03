package chess.core.bitboards.moves.pieces;

import chess.core.bitboards.BoardConstants;
import chess.core.utils.Utils;

public class Bishop extends Piece {
  private long osBoardManipulation(long pieceBB, long empty) {
    long total = 0L;
    if (pieceBB != 0) {
      while (pieceBB != 0) {
        int sliderBit = Utils.bitPosition(pieceBB); // Get bit position
        int diagPos = (sliderBit / 8) + (sliderBit % 8); // Get y + x
        int aDiagPos = ((sliderBit / 8) + 7) - (sliderBit % 8); // Get (y + 7) - x
        long occ = ~empty, bb1 = (1L << sliderBit) * 2, bb2 = Long.reverse(1L << sliderBit) * 2;
        long diagMask = BoardConstants.DIAGONAL_MASKS[diagPos]; // Gets a mask for the diagonal
        long aDiagMask = BoardConstants.ANTIDIAGONAL_MASKS[aDiagPos]; // Get a mask for
                                                                      // anti-diagonal
        long diag = ((occ & diagMask) - bb1) ^ Long.reverse(Long.reverse(occ & diagMask) - bb2); // diagonal
        long aDiag = ((occ & aDiagMask) - bb1) ^ Long.reverse(Long.reverse(occ & aDiagMask) - bb2); // anti-diagonal
        total |= ((diag & diagMask) | (aDiag & aDiagMask));
        pieceBB &= pieceBB - 1;
      }
    }
    return total;
  }

  @Override
  public long wGetPossibleTargets(long pieceBB, long empty) {
    return osBoardManipulation(pieceBB, empty) & empty; // Gets white moves
  }

  @Override
  public long bGetPossibleTargets(long pieceBB, long empty) {
    return osBoardManipulation(pieceBB, empty) & empty; // Gets black moves
  }

  @Override
  public long wGetPossibleCaptures(long pieceBB, long bOcc, long empty) {
    return osBoardManipulation(pieceBB, empty) & bOcc; // Gets white captures
  }

  @Override
  public long bGetPossibleCaptures(long pieceBB, long wOcc, long empty) {
    return osBoardManipulation(pieceBB, empty) & wOcc; // Gets black captures
  }

  @Override
  public long wGetPossiblePieces(long pieceBB, long empty, long bOcc) {
    return wGetPossibleTargets(pieceBB, empty) | wGetPossibleCaptures(pieceBB, bOcc, empty);
  }

  @Override
  public long bGetPossiblePieces(long pieceBB, long empty, long wOcc) {
    return bGetPossibleTargets(pieceBB, empty) | bGetPossibleCaptures(pieceBB, wOcc, empty);
  }
}
