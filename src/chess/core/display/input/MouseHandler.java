package chess.core.display.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import chess.core.bitboards.BoardConstants;

public class MouseHandler implements MouseListener, MouseMotionListener {
  public static int mX, mY, nMX, nMY, dMX, dMY;
  public static int button;
  public static boolean pressedMouse = false, releasedMouse = false, draggedMouse = false;

  public void mousePressed(MouseEvent e) { //Mouse pressed event
    int squareSize = BoardConstants.squareSize << 3;
    if ((e.getX() < squareSize) && (e.getY()  < squareSize)) {
      button = e.getButton(); //Above if statement ensures the click is within the bounds
      mX = e.getX();
      mY = e.getY();
      pressedMouse = true; //Set the flag - mouse is pressed
    }
  }

  public void mouseReleased(MouseEvent e) {
    int squareSize = BoardConstants.squareSize << 3;
    if ((e.getX() < squareSize) && (e.getY() < squareSize)) {
      button = e.getButton(); //Above if statement ensures the click is within the bounds
      nMX = e.getX();
      nMY = e.getY();
      releasedMouse = true; //Set the flag for a mouse released event
    }
  }

  public void mouseDragged(MouseEvent e) {
    int squareSize = BoardConstants.squareSize << 3;
    if ((e.getX() < squareSize) && (e.getY() < squareSize)) {
      button = e.getButton(); //Above if statement ensures the click is within the bounds
      dMX = e.getX();
      dMY = e.getY();
      draggedMouse = true; //Set the flag for a dragged event
    }
  }
  @Override
  public void mouseMoved(MouseEvent arg0) {}
  @Override
  public void mouseClicked(MouseEvent arg0) {}
  @Override
  public void mouseEntered(MouseEvent arg0) {}
  @Override
  public void mouseExited(MouseEvent arg0) {}
}
