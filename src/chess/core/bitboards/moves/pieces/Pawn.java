package chess.core.bitboards.moves.pieces;

import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.moves.Moves;

public class Pawn extends Piece {

  private long wSinglePushTargets(long pieceBB, long empty) {
    return Moves.southOne(pieceBB) & empty; // Single move up
  }

  private long wDblPushTargets(long pieceBB, long empty) {
    long singlePush = wSinglePushTargets(pieceBB, empty); // Double move up
    return Moves.southOne(singlePush) & empty & BoardConstants.RANK_5; // Ensures no wrapping
  }

  private long bSinglePushTargets(long pieceBB, long empty) {
    return (Moves.northOne(pieceBB) & empty); // Black single move
  }

  private long bDblPushTargets(long pieceBB, long empty) {
    long singlePush = bSinglePushTargets(pieceBB, empty); // Black double move
    return Moves.northOne(singlePush) & empty & BoardConstants.RANK_4; // Prevent wrapping
  }

  private long wCaptureTargets(long pieceBB) {
    long cpDiag1 = Moves.soEaOne(pieceBB); // SE capture
    long cpDiag2 = Moves.soWeOne(pieceBB); // SW capture
    return (cpDiag1 | cpDiag2);
  }

  private long bCaptureTargets(long pieceBB) {
    long cpDiag1 = Moves.noEaOne(pieceBB); // NE capture
    long cpDiag2 = Moves.noWeOne(pieceBB); // NW capture
    return (cpDiag1 | cpDiag2);
  }

  @Override
  public long wGetPossibleTargets(long pieceBB, long empty) {
    return wSinglePushTargets(pieceBB, empty) | wDblPushTargets(pieceBB, empty); // White moves
  }

  @Override
  public long bGetPossibleTargets(long pieceBB, long empty) {
    return bSinglePushTargets(pieceBB, empty) | bDblPushTargets(pieceBB, empty); // Black moves
  }

  @Override
  public long wGetPossibleCaptures(long pieceBB, long bOccupied, long empty) {
    return wCaptureTargets(pieceBB) & bOccupied & ~empty; // White captures
  }

  @Override
  public long bGetPossibleCaptures(long pieceBB, long wOccupied, long empty) {
    return bCaptureTargets(pieceBB) & wOccupied & ~empty; // Blaack captures
  }

  @Override
  public long wGetPossiblePieces(long pieceBB, long empty, long bOcc) { // White moves and captures
    return wGetPossibleTargets(pieceBB, empty) | (wGetPossibleCaptures(pieceBB, bOcc, empty));
  }

  @Override
  public long bGetPossiblePieces(long pieceBB, long empty, long wOcc) { // Black moves and captures
    return bGetPossibleTargets(pieceBB, empty) | (bGetPossibleCaptures(pieceBB, wOcc, empty));
  }

}
