package chess.core.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import chess.core.ai.AB;
import chess.core.bitboards.Board;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.MoveType;
import chess.core.bitboards.BoardConstants.Mode;
import chess.core.bitboards.Pair;
import chess.core.bitboards.moves.Move;
import chess.core.bitboards.moves.MoveHistory;
import chess.core.bitboards.moves.Moves;
import chess.core.bitboards.moves.pieces.PieceType;
import chess.core.display.input.MouseHandler;
import chess.core.display.window.CreateBoard;
import chess.core.utils.Utils;

public class Chessboard extends JPanel {
  public static final String FILE_PATH = "./saves";
  private static final String GFX_PATH = "/gfx/chessPieces.png";

  public static int gameOver = -1;

  public String possMoves = "";

  private Image chessPieceImage;

  private int squareSize = 0, draggedIndex = -1;

  public Chessboard(int width, int height) {
    final MouseHandler m = new MouseHandler();

    setPreferredSize(new Dimension(width, height));

    addMouseListener(m);
    addMouseMotionListener(m);
  }

  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    drawBoard(g2);
    //drawBorders(g2);
    drawPieces(g2, Board.pieces);
    drawCaptured(g2);
    drawPossible(g2);
    drawAlgebraic(g2);
    drawPieceAnimation(g2);
  }

  public void initChessboard(int width, int height) {
    URL url = Chessboard.class.getResource(GFX_PATH);
    chessPieceImage = new ImageIcon(url).getImage();

    BoardConstants.squareSize = Math.min(width, height) / 8;
    BoardConstants.WIDTH_F = width;
    BoardConstants.HEIGHT_F = height;

    squareSize = BoardConstants.squareSize;

    Board.initChess();
  }

  private void drawBoard(Graphics2D g) {
    for (int i = 0; i < 64; i += 2) {
      g.setColor(new Color(240, 240, 240)); // White square
      g.fillRect((i % 8 + (i / 8) % 2) * squareSize, (i / 8) * squareSize, squareSize, squareSize);

      g.setColor(new Color(135, 135, 135)); // Black square
      g.fillRect(((i + 1) % 8 - ((i + 1) / 8) % 2) * squareSize, ((i + 1) / 8) * squareSize,
          squareSize, squareSize);
    }
  }

  public void drawBorders(Graphics g) {
    int border = 10;
    int board = 8 * squareSize;
    int boardPlusBorder = board + 2 * border;

    //Color of the game board
    g.setColor(new Color(15, 70, 90));

    // Lines to show bevel on edge
    g.fill3DRect(border, 0, board, border, true);
    g.fill3DRect(0, border, border, board, true);
    g.fill3DRect(border, border + board, board, border, true);
    g.fill3DRect(board + border, border, border, board, true);

    // Surrounds the piece capture area
    g.fillRect(boardPlusBorder, 0, WIDTH - boardPlusBorder, border);
    g.fillRect(boardPlusBorder, board + border, WIDTH - boardPlusBorder, border);
    g.fillRect(WIDTH - border, 0, border, boardPlusBorder);

    // Single squares to show raised bevel
    g.fill3DRect(0, 0, border, border, true);
    g.fill3DRect(0, border + board, border, border, true);
    g.fill3DRect(board + border, 0, border, border, true);
    g.fill3DRect(board + border, board + border, border, border, true);
}

  private void drawPieces(Graphics2D g, Pair[] pieces) {
    for (int j = 0; j < pieces.length; j++) {
      int player = j < Board.PIECES ? 0 : 1;
      int piece = j % Board.PIECES;
      long bb = pieces[j].getPiece();
      while (bb != 0) {
        int i = Utils.bitPosition(bb);
        bb &= bb - 1;
        g.drawImage(chessPieceImage, (i % 8) * squareSize, (i / 8) * squareSize,
            ((i % 8) + 1) * squareSize, ((i / 8) + 1) * squareSize, piece * 64, player * 64,
            (piece + 1) * 64, (player + 1) * 64, null);
      }
    }
  }

  private void drawPossible(Graphics2D g) {
    if (possMoves.length() > 0) {
      g.setColor(new Color(100, 180, 0));
      for (int j = 0; j < possMoves.length(); j += 2) {
        String tMove = possMoves.substring(j, j + 2);

        int x = Character.getNumericValue(tMove.charAt(0)) * squareSize;
        int y = Character.getNumericValue(tMove.charAt(1)) * squareSize;
        int r = squareSize / 2;

        g.fillOval(x + (r / 2), y + (r / 2), r, r);
      }
    }
  }

  private void drawCaptured(Graphics2D g) {
    if (!MoveHistory.isEmpty()) { // Have moves been played
      int tCounterW = -1, tCounterB = -1;
      int x = 8 * squareSize; // Gets the x offset

      for (int i = 0; i < MoveHistory.getSize(); i++) { // Goes through all past moves
        if (MoveHistory.getItemAt(i).getType() == MoveType.CAPTURE) { // Did the move in include a capture?
          int itemIndex = MoveHistory.getItemAt(i).getPieceC().ordinal();
          int colIndex = MoveHistory.getItemAt(i).getPlayer(); // Gets the player colour
          int y = (64 * colIndex);

          if (colIndex == 0) {
            tCounterW++;
            g.drawImage(chessPieceImage, (x + (tCounterW * 64)) - (32 * tCounterW), y,
                (x + ((tCounterW + 1) * 64)) - (32 * tCounterW), (64 * (colIndex + 1)),
                itemIndex * 64, (colIndex ^ 1) * 64, (itemIndex + 1) * 64,
                ((colIndex ^ 1) + 1) * 64, null);
          } else if (colIndex == 1) {
            tCounterB++;
            g.drawImage(chessPieceImage, (x + (tCounterB * 64)) - (32 * tCounterB), y,
                (x + ((tCounterB + 1) * 64)) - (32 * tCounterB), (64 * (colIndex + 1)),
                itemIndex * 64, (colIndex ^ 1) * 64, (itemIndex + 1) * 64,
                ((colIndex ^ 1) + 1) * 64, null);
          }
        }
      }
    }
  }

  private void drawAlgebraic(Graphics2D g) {
    if (!MoveHistory.isEmpty()) {
      setText(g, Color.BLUE);
      FontMetrics c = g.getFontMetrics();
      int x = (8 * squareSize) + 10;
      int y = getHeight() / 3;

      int j = 0, end = (int) Math.floor((2 * (getHeight() - y)) / c.getHeight());
      int mhs = MoveHistory.getSize();
      if (((mhs * c.getHeight() / 2) + y) >= getHeight())
        j += (j + (mhs - end)) % 2 != 0 ? (mhs - end) + 1 : mhs - end;

      for (int i = j; i < MoveHistory.getSize(); i++) { // At which move should we start drawing
        String alg = Utils.convertToAlgebraic(MoveHistory.getItemAt(i)); // Gets the text to draw
        int offX = c.stringWidth(Integer.toString((i / 2) + 1) + ". ");
        int offY = ((i - j) / 2) * c.getHeight(); // Gets the height offset

        if (i % 2 == 0) {
          g.drawString(Integer.toString((i / 2) + 1) + ".", x, y + offY);
          g.drawString(alg, x + offX, y + offY); // Draws the algebraic move notation
        } else { // Draws a black move
          g.drawString(alg,
              x + offX + 10 + c.stringWidth(Utils.convertToAlgebraic(MoveHistory.getItemAt(i - 1))),
              y + offY);
        }
      }
    }
  }

  public void drawPieceAnimation(Graphics2D g) {
    if (MouseHandler.draggedMouse) { // Is the mouse being dragged
      int mX = MouseHandler.dMX; // Gets the current x on the screen
      int mY = MouseHandler.dMY; // Gets the current y on the screen
      if (draggedIndex != -1) { // Is a piece clicked
        int p = Board.getPlayer(); // Gets the current player
        g.drawImage(chessPieceImage, mX - squareSize / 2, mY - squareSize / 2, mX + squareSize,
            mY + squareSize, draggedIndex * 64, p * 64, (draggedIndex + 1) * 64, (p + 1) * 64,
            null);
      }
    }
  }

  private void setText(Graphics2D g, Color c) {
    g.setFont(new Font("TimesRoman", Font.BOLD, 18));
    g.setColor(c); // Sets the colour to the one specified
  }

  public void pressedEvent() {
    if ((MouseHandler.button == MouseEvent.BUTTON1) || (MouseHandler.button == MouseEvent.BUTTON3)) {
      int mX = MouseHandler.mX; // Gets the current x and y where the mouse was clicked
      int mY = MouseHandler.mY;
      possMoves = Board.getPossibleMoves(mX, mY, squareSize);
    }
  }

  public void releasedEvent() {
    int mX = MouseHandler.mX; // Gets the start location of the click
    int mY = MouseHandler.mY;
    int nMX = MouseHandler.nMX; // Gets the end location of the click
    int nMY = MouseHandler.nMY; // These are combined to form a from->to move

    if (getGM() == Mode.ONE_PLAYER && !Board.isCreating()) { // One Player
      if (MouseHandler.button == MouseEvent.BUTTON1) {
        int p = Board.getPlayer();
        String move = Utils.calculateDragMove(mX, mY, nMX, nMY, squareSize);
        MoveType moveType = Board.whichMoveType(move, p);
        if (Moves.applyMove(null, p, moveType, move, true) && !checkingForChecksSinglePlayer(p ^ 1))
          performAIMove();
      }
    } else if ((getGM() == Mode.TWO_PLAYER) && !Board.isCreating()) { // Two player
      if (MouseHandler.button == MouseEvent.BUTTON1) {
        String move = Utils.calculateDragMove(mX, mY, nMX, nMY, squareSize);
        standardMove(move, Board.getPlayer()); // Just a normal move if the king is not in check
      }
    }
    if (Board.isCreating()) { // Editing the board
      int shift = Utils.getShiftFrom("" + (nMY / squareSize) + (nMX / squareSize));
      PieceType pieceIndex = CreateBoard.pieceSelected;
      int playerSelected = CreateBoard.whichSelected;
      if (((Board.getPieceBoard(pieceIndex, playerSelected) >> shift) & 1) == 0) {
        Board.pieces[(playerSelected * Board.PIECES) + pieceIndex.ordinal()].xorWith(1L << shift);
      } else {
        Board.pieces[(playerSelected * Board.PIECES) + pieceIndex.ordinal()].andWith(~(1L << shift));
      }
    }

    this.draggedIndex = -1; // Reset the piece currently selected
  }

  private void performAIMove() {
    int prevMoves = MoveHistory.getSize();
    Move nextMove = new AB().alphaBetaMax(null, Integer.MIN_VALUE, Integer.MAX_VALUE, BoardConstants.PVS_DEPTH, 1); // Begin the search function
    if (nextMove != null) {
      nextMove.flipMove();
      MoveHistory.remove(prevMoves, MoveHistory.getSize()); // Clear the MoveHistory
      Moves.applyMove(nextMove, nextMove.getPlayer(), nextMove.getType(), nextMove.getMoveReg(), true);
      Board.setPlayer(0); // Now white's move
    }
    checkingForChecksSinglePlayer(Board.getPlayer()); // Did the AI put the player in check/checkmate
  }

  public void draggedEvent() {
    if ((MouseHandler.draggedMouse) && (draggedIndex == -1)) {
      int rank = MouseHandler.mX / squareSize;
      int file = MouseHandler.mY / squareSize;
      PieceType piece = Board.getBBIndex(rank, file, Board.getPlayer());
      if (piece != null)
        draggedIndex = piece.ordinal();
    }
  }

  private boolean checkingForChecksSinglePlayer(int p) {
    return Board.kingInCheck(p) && Board.kingInCheckmate(p);
  }

  private void checkingForChecksTwoPlayer(int p) {
    if (Board.kingInCheck(p)) {
      Moves.undoMove(p, MoveHistory.getNext());
    } else {
      p = Board.getPlayer();
      if (Board.kingInCheck(p)) {
        if (Board.kingInCheckmate(p)) {
          checkmateDialog(p);
        } else {
          checkDialog(p);
        }
      }
    }
  }

  private void standardMove(String move, int p) {
    MoveType moveType = Board.whichMoveType(move, p);
    if (moveType != null && Moves.applyMove(null, p, moveType, move, true))
      checkingForChecksTwoPlayer(p);
  }

  private void checkmateDialog(int p) {
    boolean shouldRestart = (Utils.showDialog(
        "Checkmate!", (p == 0 ? "Black" : "White") + " has won! \nGame Over")) == 0;
    if (shouldRestart) { // Restart
      Board.reset();
    } else { // Dont Restart
      MoveHistory.peekNext().setCheckmate(true);
      MoveHistory.peekNext().setPlayerWon(p);
    }
  }

  private void checkDialog(int p) {
    Utils.simpleDialog("Check!", "Check"); // Shows the user that a check occured
    MoveHistory.peekNext().setCheck(true); // Sets the check status for the current move
  }

  // Gets the current game mode
  public Mode getGM() {
    return BoardConstants.gamemode;
  }
}
