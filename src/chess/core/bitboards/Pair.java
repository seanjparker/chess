package chess.core.bitboards;

public class Pair {

  private long piece;
  private PlayerColour colour;

  public Pair(long piece, PlayerColour colour) {
    this.piece = piece;
    this.colour = colour;
  }

  public long getPiece() {
    return piece;
  }

  public void xorWith(long piece) {
    this.piece ^= piece;
  }

  public void andWith(long piece) {
    this.piece &= piece;
  }

  public PlayerColour getColour() {
    return colour;
  }
}
