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
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.BoardConstants.Mode;
import chess.core.bitboards.CBoard;
import chess.core.bitboards.moves.Move;
import chess.core.bitboards.moves.MoveHistory;
import chess.core.bitboards.moves.Moves;
import chess.core.display.input.MouseHandler;
import chess.core.display.window.CreateBoard;
import chess.core.utils.Utils;

public class Chessboard extends JPanel {
  public static final String FILE_PATH = "./saves";
  private static final String GFX_PATH = "/gfx/chessPieces.png";
  public static int gameOver = -1;

  public String possMoves = "";
  private Image chessPieceImage;
  private Moves m = new Moves();

  private double squareSize = 0D;

  private int draggedIndex = -1;

  public Chessboard(int width, int height) {
    setPreferredSize(new Dimension(width, height));
  }

  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    //drawBorders(g2);
    drawBoard(g2);
    drawPieces(g2);
    drawCaptured(g2);
    drawPossible(g2);
    drawAlgebraic(g2);
    drawPieceAnimation(g2);
  }

  public void initChessboard(int width, int height) {
    URL url = Chessboard.class.getResource(GFX_PATH);
    chessPieceImage = new ImageIcon(url).getImage();

    CBoard.initChess();

    BoardConstants.squareSize = Math.min(width, height) / 8;
    BoardConstants.WIDTH_F = width;
    BoardConstants.HEIGHT_F = height;

    squareSize = BoardConstants.squareSize;
  }

  private void drawBoard(Graphics2D g) {
    for (int i = 0; i < 64; i += 2) {
      g.setColor(new Color(240, 240, 240)); // White square
      g.fillRect((int) ((i % 8 + (i / 8) % 2) * squareSize),
          (int) ((i / 8) * squareSize), (int) squareSize, (int) squareSize);
      g.setColor(new Color(135, 135, 135)); // Black square
      g.fillRect((int) (((i + 1) % 8 - ((i + 1) / 8) % 2) * squareSize),
          (int) (((i + 1) / 8) * squareSize), (int) squareSize, (int) squareSize);
    }
  }

  private void drawPieces(Graphics2D g) {
    for (int l = 0; l < CBoard.pieces.length; l++) {
      for (int k = 0; k < CBoard.pieces[l].length; k++) {
        long bb = CBoard.pieces[l][k];
        while (bb != 0) {
          int i = Utils.bitPosition(bb); // Gets the current bit location
          bb &= bb - 1; // Gets the next bit location
          g.drawImage(chessPieceImage, (int) ((i % 8) * squareSize),
              (int) ((i / 8) * squareSize), (int) (((i % 8) + 1) * squareSize),
              (int) (((i / 8) + 1) * squareSize), l * 64, k * 64, (l + 1) * 64,
              (k + 1) * 64, null);
        } // Above line draws a specific portion of the image to the screen
      }
    }
  }

  private void drawPossible(Graphics2D g) {
    if (possMoves.length() > 0) { // Once there are moves to display
      for (int j = 0; j < possMoves.length(); j += 2) {
        String tMove = possMoves.substring(j, j + 2); // Parse the string
        // Gets the x and y location
        int x = ((int) (Character.getNumericValue(tMove.charAt(0)) * squareSize));
        int y = ((int) (Character.getNumericValue(tMove.charAt(1)) * squareSize));
        int r = (int) squareSize / 2;

        g.setColor(new Color(100, 180, 0));
        g.fillOval(x + (r / 2), y + (r / 2), r, r); // Draws at specific location, an oval
      }
    }
  }

  private void drawCaptured(Graphics2D g) {
    if (!MoveHistory.isEmpty()) { // Have moves been played
      int tCounterW = -1, tCounterB = -1;
      int x = (int) (8 * squareSize); // Gets the x offset

      for (int i = 0; i < MoveHistory.getSize(); i++) { // Goes through all past moves
        if (MoveHistory.getItemAt(i).getType() == 0) { // Did the move in include a capture?
          int itemIndex = MoveHistory.getItemAt(i).getPieceCI(); // Gets the captured piece index
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
    if (!MoveHistory.isEmpty()) { // Have moves been played?
      setText(g, Color.BLUE); // Sets the text colour
      FontMetrics c = g.getFontMetrics();
      int x = (int) (8 * squareSize);
      int y = 128;

      int j = 0, end = (int) Math.floor((2 * (getHeight() - y)) / c.getHeight());
      // Should text scroll?
      if ((((MoveHistory.getSize() / 2) * c.getHeight()) + y) >= getHeight()) {
        j += ((MoveHistory.getSize() - end));
        if ((j % 2) != 0) {
          j++;
        } // Is is a white or black move
      }

      for (int i = j; i < MoveHistory.getSize(); i++) { // AT which move should we start drawing
        String alg = Utils.convertToAlgebraic(MoveHistory.getItemAt(i)); // Gets the text to draw
        int offX = c.stringWidth(Integer.toString((i / 2) + 1) + ". ");
        int offY = ((i - j) / 2) * c.getHeight(); // Gets the height offset

        if (i % 2 == 0) {
          g.drawString(Integer.toString((i / 2) + 1) + ".", x, y + offY);
          g.drawString(alg, x + offX, y + offY); // Draws the algebraic move notation
        } else { // Draws a black move
          g.drawString(alg,
              x + offX
                  + c.stringWidth(Utils.convertToAlgebraic(MoveHistory.getItemAt(i - 1))),
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
        int p = CBoard.getPlayer(); // Gets the current player
        g.drawImage(chessPieceImage, mX - (int) (squareSize / 2), mY - (int) (squareSize / 2),
            (int) (mX + squareSize), (int) (mY + squareSize), draggedIndex * 64, p * 64,
            (draggedIndex + 1) * 64, (p + 1) * 64, null);
      }
    }
  }

  private void setText(Graphics2D g, Color c) {
    g.setFont(new Font("TimesRoman", Font.BOLD, 18));
    g.setColor(c); // Sets the colour to the one specified
  }

  public void pressedEvent() {
    if ((MouseHandler.button == MouseEvent.BUTTON1)
        || (MouseHandler.button == MouseEvent.BUTTON3)) {
      int mX = MouseHandler.mX; // Gets the current x and y where the mouse was clicked
      int mY = MouseHandler.mY;
      possMoves = CBoard.getPossibleMoves(mX, mY, (int) squareSize);
    }
  }

  public void releasedEvent() {
    int mX = MouseHandler.mX; // Gets the start location of the click
    int mY = MouseHandler.mY;
    int nMX = MouseHandler.nMX; // Gets the end location of the click
    int nMY = MouseHandler.nMY; // These are combined to form a from->to move

    if (getGM() == Mode.ONE_PLAYER && !CBoard.isCreating()) { // One Player
      if (MouseHandler.button == MouseEvent.BUTTON1) {
        int p = CBoard.getPlayer();
        String move = Utils.calculateDragMove(mX, mY, nMX, nMY, squareSize);
        int moveType = CBoard.whichMoveType(move, p);
        if (m.applyMove(null, p, moveType, move, true)) {
          if (!checkingForChecksSinglePlayer(p ^ 1)) {
            performAIMove();
          }
        }
      }
    } else if ((getGM() == Mode.TWO_PLAYER) && !CBoard.isCreating()) { // Two Player or timed game
      if (MouseHandler.button == MouseEvent.BUTTON1) {
        int p = CBoard.getPlayer();
        String move = Utils.calculateDragMove(mX, mY, nMX, nMY, squareSize);
        standardMove(move, p); // Just a normal move if the king is not in check
      }
    }
    if (CBoard.isCreating()) { // Editing the board
      int shift = Utils.getShiftFrom("" + (int) (nMY / squareSize) + "" + (int) (nMX / squareSize));
      int pieceIndex = CreateBoard.pieceSelected;
      int playerSelected = CreateBoard.whichSelected;
      if (((CBoard.pieces[pieceIndex][playerSelected] >> shift) & 1) == 0) {
        CBoard.pieces[pieceIndex][playerSelected] ^= (1L << shift);
      } else {
        CBoard.pieces[pieceIndex][playerSelected] &= ~(1L << shift);
      }
    }

    this.draggedIndex = -1; // Reset the piece currently selected
  }

  private void performAIMove() {
    int prevMoves = MoveHistory.getSize();
    AB search = new AB(null, Integer.MIN_VALUE, Integer.MAX_VALUE, BoardConstants.PVS_DEPTH, 1);
    Move nextMove = search.startSearch(); // Begin the search function
    if (nextMove != null) {
      nextMove.flipMove();
      MoveHistory.remove(prevMoves, MoveHistory.getSize()); // Clear the MoveHistory
      m.applyMove(nextMove, nextMove.getPlayer(), nextMove.getType(), nextMove.getMoveReg(), true);
      CBoard.setPlayer(0); // Now white's move
    }
    int p = CBoard.getPlayer();
    checkingForChecksSinglePlayer(p); // Did the AI put the player in check/checkmate
  }

  public void draggedEvent() {
    if ((MouseHandler.draggedMouse) && (draggedIndex == -1)) {
      int rank = (int) (MouseHandler.mX / squareSize);
      int file = (int) (MouseHandler.mY / squareSize);
      int pieceI = CBoard.getBBIndex(rank, file, CBoard.getPlayer());
      if (pieceI != -1) {
        draggedIndex = pieceI;
      } // Sets the current piece clicked
    }
  }

  private boolean checkingForChecksSinglePlayer(int p) {
    if (CBoard.kingInCheck(p)) { // Is white in check
      if (CBoard.kingInCheckmate(p)) { // Is white in checkmate
        checkmateDialog(p);
        return true;
      }
    }
    return false;
  }

  private void checkingForChecksTwoPlayer(int p) {
    if (CBoard.kingInCheck(p)) {
      m.undoMove(p, MoveHistory.getNext());
    } else {
      p = CBoard.getPlayer(); // Get the updated player
      if (CBoard.kingInCheck(p)) { // Did the last move put the other player in check
        if (CBoard.kingInCheckmate(p)) { // If the king is in check, is the king in checkmate
          checkmateDialog(p); // If true, game over
        } else {
          checkDialog(p); // Is king in check, inform user
        }
      }
    }
  }

  private void standardMove(String move, int p) {
    int moveType = CBoard.whichMoveType(move, p); // Gets the move type of the current move
    if (moveType >= 0) { // Do a preliminary test to ensure move was valid
      if (m.applyMove(null, p, moveType, move, true)) {
        checkingForChecksTwoPlayer(p);
      }
    }
  }

  private void checkmateDialog(int p) {
    int v;
    if (p == 0) {
      v = Utils.showDialog("Checkmate!!", "Black has won! \nGame Over");
    } else {
      v = Utils.showDialog("Checkmate!!", "White has won! \nGame Over");
    }
    if (v == 0) { // Restart
      CBoard.reset();
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
