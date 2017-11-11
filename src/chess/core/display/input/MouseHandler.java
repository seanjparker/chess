package chess.core.display.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import chess.core.bitboards.BoardConstants;

public class MouseHandler implements MouseListener, MouseMotionListener {
	public static int mX, mY, nMX, nMY, dMX, dMY;
	public static int button;
	public static boolean pressedMouse = false, releasedMouse = false, draggedMouse = false;
	private int border = 10;
	
	public void mousePressed(MouseEvent e) { //Mouse pressed event
		double squareSize = BoardConstants.squareSize;
		if (((e.getX() - squareSize) < (8 * squareSize)) && ((e.getY() - squareSize) < (8 * squareSize))) {
			button = e.getButton(); //Above if statement ensures the click is within the bounds
			mX = e.getX() - border;
			mY = e.getY() - (int) squareSize + border;
			pressedMouse = true; //Set the flag - mouse is pressed
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		double squareSize = BoardConstants.squareSize;
		if (((e.getX() - squareSize) < (8 * squareSize)) && ((e.getY() - squareSize) < (8 * squareSize))) {
			button = e.getButton(); //Above if statement ensures the click is within the bounds
			nMX = e.getX() - border;
			nMY = e.getY() - (int) squareSize + border;
			releasedMouse = true; //Set the flag for a mouse released event
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		double squareSize = BoardConstants.squareSize;
		if (((e.getX() - squareSize) < (8 * squareSize)) && ((e.getY() - squareSize) < (8 * squareSize))) {
			button = e.getButton(); //Above if statement ensures the click is within the bounds
			dMX = e.getX() - border;
			dMY = e.getY() - (int) squareSize + border;
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
