package chess.core.bitboards.moves.pieces;

public class Queen extends Piece {
  private Rook r = new Rook(); // Queen is a combination of bishop and rooks moves
  private Bishop b = new Bishop();

  @Override
  public long wGetPossibleTargets(long pieceBB, long empty) {
    return r.wGetPossibleTargets(pieceBB, empty) | b.wGetPossibleTargets(pieceBB, empty);
  }

  @Override
  public long bGetPossibleTargets(long pieceBB, long empty) {
    return r.bGetPossibleTargets(pieceBB, empty) | b.bGetPossibleTargets(pieceBB, empty);
  }

  @Override
  public long wGetPossibleCaptures(long pieceBB, long bOcc, long empty) {
    return r.wGetPossibleCaptures(pieceBB, bOcc, empty)
        | b.wGetPossibleCaptures(pieceBB, bOcc, empty);
  }

  @Override
  public long bGetPossibleCaptures(long pieceBB, long wOcc, long empty) {
    return r.bGetPossibleCaptures(pieceBB, wOcc, empty)
        | b.bGetPossibleCaptures(pieceBB, wOcc, empty);
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
