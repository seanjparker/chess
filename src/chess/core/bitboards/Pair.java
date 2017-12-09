package chess.core.bitboards;

public class Pair {
  private long piece;
  private int colour;

  public Pair(long piece, int colour) {
    this.piece = piece;
    this.colour = colour;
  }

  public long getPiece() {
    return piece;
  }

  public void setPiece(long piece) {
    this.piece = piece;
  }

  public int getColour() {
    return colour;
  }

  public void setColour(int colour) {
    this.colour = colour;
  }
}
