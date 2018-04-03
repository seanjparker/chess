package chess.core.bitboards.moves.pieces;

abstract class Piece { //abstract class inherited by all pieces - provides basic functions
  public abstract long wGetPossiblePieces(long pieceBB, long empty, long bOcc);
  public abstract long bGetPossiblePieces(long pieceBB, long empty, long wOcc);

  public abstract long wGetPossibleTargets(long pieceBB, long empty);
  public abstract long bGetPossibleTargets(long pieceBB, long empty);

  public abstract long wGetPossibleCaptures(long pieceBB, long bOccupied, long empty);
  public abstract long bGetPossibleCaptures(long pieceBB, long wOccupied, long empty);
}
