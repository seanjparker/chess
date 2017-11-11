package chess.core.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import chess.core.ai.AB;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.CBoard;
import chess.core.bitboards.Type;
import chess.core.bitboards.moves.Move;
import chess.core.bitboards.moves.MoveHistory;
import chess.core.bitboards.moves.Moves;
import chess.core.display.input.MouseHandler;
import chess.core.display.window.CreateBoard;
import chess.core.initialize.Main;
import chess.core.online.DatabaseUser;
import chess.core.utils.Timer;
import chess.core.utils.Utils;

public class GUI {
	public static final String FILE_PATH = "./saves"; //Relative file path where to save the files
	private static final String GFX_PATH = "/gfx/chessPieces.png"; //Location of the chess images
	public static int gameOver = -1; //Game over state
	
	public String possMoves = ""; //Holds the possible moves of the piece during right click
	private Image chessPieceImage; //Image of the chess images
	private Moves m; //Used to access all function in the moves class
	
	private double squareSize = 0D; //Size of each square on the chess board
	
	private int width, height; //width and height of the screen
	private int border = 0; //Boder to offset the chessboard
	private int draggedIndex = -1; //Which piece is being dragged?
	
	public GUI(int width, int height) {
		this.width = width; //Initalize the GUI
		this.height = height;
		m = new Moves(); //Initalize the moves class
	}

	public void initGUI() {
		URL url = GUI.class.getResource(GFX_PATH); //Initalize the image loader
		chessPieceImage = new ImageIcon(url).getImage(); //Load the image
	
		CBoard.initChess(); //Initlaize the chess board
		
		squareSize = BoardConstants.squareSize; //Sets GUI variables
		border = BoardConstants.border;
		width = BoardConstants.WIDTH_F;
		height = BoardConstants.HEIGHT_F;
	}
	
	public void drawBoard(Graphics g) { //
		for (int i = 0; i < 64; i += 2) { //Draw chess board
			g.setColor(new Color(240, 240, 240)); //White square
			g.fillRect((int) ((i % 8 + (i / 8) % 2) * squareSize) + border, (int) ((i / 8) * squareSize) + border, (int) squareSize, (int) squareSize);
			g.setColor(new Color(135, 135, 135)); //Black square
			g.fillRect((int) (((i + 1) % 8 - ((i + 1) / 8) % 2) * squareSize) + border, (int) (((i + 1) / 8) * squareSize) + border, (int) squareSize, (int) squareSize);
		}
	}
	
	public void drawBorders(Graphics g) {
		int board = (int) (8 * squareSize);
		int boardPlusBorder = board + 2 * border;
		
		//Color of the game board
		g.setColor(new Color(15, 70, 90));
		
		/* Lines to show bevel on edge */
		g.fill3DRect(border, 0, board, border, true);
		g.fill3DRect(0, border, border, board, true);
		g.fill3DRect(border, border + board, board, border, true);
		g.fill3DRect(board + border, border, border, board, true);
		
		/* Surrounds the piece capture area */
		g.fillRect(boardPlusBorder, 0, width - boardPlusBorder, border); 
		g.fillRect(boardPlusBorder, board + border, width - boardPlusBorder, border);
		g.fillRect(width - border, 0, border, boardPlusBorder);
		
		/* Single squares to show raised bevel */
		g.fill3DRect(0, 0, border, border, true);
		g.fill3DRect(0, border + board, border, border, true);
		g.fill3DRect(board + border, 0, border, border, true);
		g.fill3DRect(board + border, board + border, border, border, true);
	}
	
	public void drawPieces(Graphics g) {
		for (int l = 0; l < CBoard.pieces.length; l++) {
			for (int k = 0; k < CBoard.pieces[l].length; k++) {
				long bb = CBoard.pieces[l][k];
				while (bb != 0) {					
					int i = Utils.bitPosition(bb); //Gets the current bit location
					bb &= bb - 1; //Gets the next bit location					
					g.drawImage(chessPieceImage, (int) ((i % 8) * squareSize) + border, (int) ((i / 8) * squareSize) + border, (int) (((i % 8) + 1) * squareSize) + border, (int) (((i / 8) + 1) * squareSize) + border, l * 64, k * 64, (l + 1) * 64, (k + 1) * 64, null);
				} //Above line draws a specific portion of the image to the screen
			}
		}
	}
	
	public void drawPossible(Graphics g) {
		if (possMoves.length() > 0) { //Once there are moves to display
			for (int j = 0; j < possMoves.length(); j+= 2) {
				String tMove = possMoves.substring(j, j + 2); //Parse the string
				//Gets the x and y location
				int x = ((int)(Character.getNumericValue(tMove.charAt(0)) * squareSize) + border);
				int y = ((int)(Character.getNumericValue(tMove.charAt(1)) * squareSize) + border);
				int r = (int) squareSize / 2;
				
				g.setColor(new Color(100, 180, 0));
				g.fillOval(x + (r / 2), y + (r / 2), r, r); //Draws at specific location, an oval
			}
		}
	}
	
	public void drawCaptured(Graphics g) {
		if (!MoveHistory.isEmpty()) { //Have moves been played
			int tCounterW = -1, tCounterB = -1;			
			int x = (int) (8 * squareSize) + (2 * border); //Gets the x offset
			
			for (int i = 0; i < MoveHistory.getSize(); i++) { //Goes through all past moves
				if (MoveHistory.getItemAt(i).getType() == 0) { /* Did the move in include a capture? */ 
					int itemIndex = MoveHistory.getItemAt(i).getPieceCI(); //Gets the captured piece index
					int colIndex = MoveHistory.getItemAt(i).getPlayer(); //Gets the player colour
					int y = border + (64 * colIndex);
					
					if (colIndex == 0) {
						tCounterW++; //Draws a white piece, the images are overlapped by 1/2 the width of a piece
						g.drawImage(chessPieceImage, (x + (tCounterW * 64)) - (32 * tCounterW), y, (x + ((tCounterW + 1) * 64)) - (32 * tCounterW), border + (64 * (colIndex + 1)), itemIndex * 64, (colIndex ^ 1) * 64, (itemIndex + 1) * 64, ((colIndex ^ 1) + 1) * 64, null);
					} else if (colIndex == 1) {
						tCounterB++; //Draws a black piece, the images are overlapped by 1/2 the width of a piece
						g.drawImage(chessPieceImage, (x + (tCounterB * 64)) - (32 * tCounterB), y, (x + ((tCounterB + 1) * 64)) - (32 * tCounterB), border + (64 * (colIndex + 1)), itemIndex * 64, (colIndex ^ 1) * 64, (itemIndex + 1) * 64, ((colIndex ^ 1) + 1) * 64, null);
					}
				}
			}
		}
	}
	
	public void drawAlgebraic(Graphics g) {
		if (!MoveHistory.isEmpty()) { //Have moves been played?
			setText(g, Color.BLUE); //Sets the text colour
			FontMetrics c = g.getFontMetrics();
			int x = (int) (8 * squareSize) + (border * 3);
			int y = (border * 3) + 128;
			
			int j = 0, end = (int) Math.floor((2 * (height - y - border)) / c.getHeight()); //Gets the maximum moves that can be displayed at once
			//Should text scroll?
			if ((((MoveHistory.getSize() / 2) * c.getHeight()) + y + border) >= height) {
				j += ((MoveHistory.getSize() - end));
				if ((j % 2) != 0) { j++; } //Is is a white or black move
			}
			
			for (int i = j; i < MoveHistory.getSize(); i++) { //AT which move should we start drawing
				String alg = Utils.convertToAlgebraic(MoveHistory.getItemAt(i)); //Gets the text to draw
				int offX = c.stringWidth(Integer.toString((i / 2) + 1) + ". "); //Gets the width of the text
				int offY = ((i - j) / 2) * c.getHeight(); //Gets the height offset
				
				if (i % 2 == 0) {
					g.drawString(Integer.toString((i / 2) + 1) + ".", x, y + offY); //Draws the current move number
					g.drawString(alg, x + offX, y + offY); //Draws the algebraic move notation
				} else { //Draws a black move
					g.drawString(alg, x + (offX + border) + c.stringWidth(Utils.convertToAlgebraic(MoveHistory.getItemAt(i - 1))), y + offY);
				}
			}
		}
	}
	
	public void drawUser(Graphics g) {
		setText(g, Color.RED); //Sets the text colour
		
		String text = "Logged In: " + BoardConstants.username; //The text to draw
		int x = width - g.getFontMetrics().stringWidth(text) - (border * 2); //Ensure that the text does not go off screen
		int y = height - g.getFontMetrics().getHeight();
		
		g.drawString(text, x, y); //Draws the text at the specified location
	}
	
	public void drawPieceAnimation(Graphics g) {		
		if (MouseHandler.draggedMouse) { //Is the mouse being dragged
			int mX = MouseHandler.dMX; //Gets the current x on the screen
			int mY = MouseHandler.dMY; //Gets the current y on the screen
			if (draggedIndex != -1) { //Is a piece clicked
				int p = CBoard.getPlayer(); //Gets the current player
				g.drawImage(chessPieceImage, mX - (int) (squareSize / 2), mY - (int) (squareSize / 2), (int) (mX + squareSize), (int) (mY + squareSize), draggedIndex * 64, p * 64, (draggedIndex + 1) * 64, (p + 1) * 64, null);
			} //Above line draws the scpefic piece of the colour, uses this data to selct only part of image + draw
		}
	}
	
	public void drawTimer(Graphics g, Timer timerWhite, Timer timerBlack) {
		if (getGM() == 3) {
			if (timerWhite.isFinished()) { gameOver = 0; } //Is the timer finished
			if (timerBlack.isFinished()) { gameOver = 1; }
			
			if (!timerWhite.isPaused()) { //If white's timer is not over, draw white as current 
				setText(g, Color.BLACK); //Sets the text to black
				extraDrawTimes(g, timerWhite.convert(), 0); //Draws white's timer a running
				setText(g, Color.GRAY); //Sets the colour for black
				extraDrawTimes(g, timerBlack.convert(), 1); //Black's timer is paused
			} else {
				setText(g, Color.GRAY); //Opposite if white's timer is paused
				extraDrawTimes(g, timerWhite.convert(), 0);
				setText(g, Color.BLACK);
				extraDrawTimes(g, timerBlack.convert(), 1);
			}
		}
	}
	
	private void extraDrawTimes(Graphics g, int[] times, int player) {
		int mod = 0;
		if (player != 0) { mod = g.getFontMetrics().getHeight(); } //Draws whites timer first, then black
		g.drawString("" + times[2] + "h : " + times[1] + "m : " + times[0] + "s", width - 200, (height / 2) + mod);
	}
	
	private void setText(Graphics g, Color c) {
		g.setFont(new Font("TimesRoman", Font.BOLD, 18)); //Sets the text font to times new roman and size 18
		g.setColor(c); //Sets the colour to the one specified
	}

	public void pressedEvent() {
		if ((MouseHandler.button == MouseEvent.BUTTON1) || (MouseHandler.button == MouseEvent.BUTTON3)) {
			int mX = MouseHandler.mX; //Gets the current x and y where the mouse was clicked
			int mY = MouseHandler.mY;
			possMoves = CBoard.getPossibleMoves(mX, mY, (int) squareSize); //Gets the possible move for the piece at the location clicked
		}
	}
	
	public void releasedEvent() {
		int mX = MouseHandler.mX; //Gets the start location of the click
		int mY = MouseHandler.mY;
		int nMX = MouseHandler.nMX; //Gets the end location of the click
		int nMY = MouseHandler.nMY; //These are combined to form a from->to move
		
		if (getGM() == 0 && !CBoard.isCreating()) { //One Player
			if (MouseHandler.button == MouseEvent.BUTTON1) {
				int p = CBoard.getPlayer();
				String move = Utils.calculateDragMove(mX, mY, nMX, nMY, squareSize); //Formulates move notation from mouse locations
				int moveType = CBoard.whichMoveType(move, p); //Which move type was the move that was played
				if (m.applyMove(null, p, moveType, move, true)) { //Check that the user actually moved a piece
					if (!checkingForChecksSinglePlayer(p ^ 1)) {
						performAIMove();
					}
				}
			}
		} else if ((getGM() == 1 || getGM() == 3) && !CBoard.isCreating()) { //Two Player or timed game
			if (MouseHandler.button == MouseEvent.BUTTON1) {
				int p = CBoard.getPlayer();
				String move = Utils.calculateDragMove(mX, mY, nMX, nMY, squareSize); //Formulates the move from mouse positions
				standardMove(move, p); //Just a normal move if the king is not in check
				//Switch the timers
				if (getGM() == 3) {
					Main.t1.flip(); Main.t2.flip(); //Flips the states of both timers
				} else if (getGM() == 0 || getGM() == 1) {
					Main.moveTimer.flip(); //Ensures the move timer records the white players move time correctly
				}
			}
		}
		if (CBoard.isCreating()) { //Editing the board	
			int shift = Utils.getShiftFrom("" + (int)(nMY / squareSize) + "" + (int)(nMX / squareSize));
			int pieceIndex = CreateBoard.pieceSelected;
			int playerSelected = CreateBoard.whichSelected;	
			if (((CBoard.pieces[pieceIndex][playerSelected] >> shift) & 1) == 0) {
				CBoard.pieces[pieceIndex][playerSelected] ^= (1L << shift); //Adds a new piece to the chessboard
			} else {
				CBoard.pieces[pieceIndex][playerSelected] &= ~(1L << shift); //Removes a piece from the chessboard
			}
		}
		
		this.draggedIndex = -1; //Reset the piece currently selected
	}
	
	private void performAIMove() {
		int prevMoves = MoveHistory.getSize();					
		AB search = new AB(null, Integer.MIN_VALUE, Integer.MAX_VALUE, BoardConstants.PVS_DEPTH, 1); //Initalize the search
		Move nextMove = search.startSearch(); //Begin the search function
		if (nextMove != null){
			nextMove.flipMove();
			MoveHistory.remove(prevMoves, MoveHistory.getSize()); //Clear the MoveHistory
			m.applyMove(nextMove, nextMove.getPlayer(), nextMove.getType(), nextMove.getMoveReg(), true); //Performs the move					
			CBoard.setPlayer(0); //Now white's move						
		}
		int p = CBoard.getPlayer();
		checkingForChecksSinglePlayer(p); //Did the AI put the player in check/checkmate
	}
	
	public void draggedEvent() {
		if ((MouseHandler.draggedMouse) && (draggedIndex == -1)) {		
			int rank = (int)(MouseHandler.mX / squareSize); //Gets the current x and y of the piece clicked
			int file = (int)(MouseHandler.mY / squareSize);
			int pieceI = CBoard.getBBIndex(rank, file, CBoard.getPlayer()); //Get the pieceIndex at the current position on the board
			if (pieceI != -1) { draggedIndex = pieceI; } //Sets the current piece clicked
		}
	}
	
	private boolean checkingForChecksSinglePlayer(int p) {
		if (CBoard.kingInCheck(p)) { //Is white in check
			if (CBoard.kingInCheckmate(p)) { //Is white in checkmate
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
			if (p == 0) { Main.moveTimer.addTime(); } //If white's turn, add a turn time
			p = CBoard.getPlayer(); //Get the updated player
			if (CBoard.kingInCheck(p)) { //Did the last move put the other player in check
				if (CBoard.kingInCheckmate(p)) { //If the king is in check, is the king in checkmate		
					checkmateDialog(p); //If true, game over
				} else {
					checkDialog(p); //Is king in check, inform user
				}
			}
		}
	}
	
	private void standardMove(String move, int p) {		
		int moveType = CBoard.whichMoveType(move, p); //Gets the move type of the current move
		if (moveType >= 0) { //Do a preliminary test to ensure move was valid
			if (m.applyMove(null, p, moveType, move, true)) { //Perform the move, determine if king moved out of check
				checkingForChecksTwoPlayer(p);
			}
		}		
	}
	
	private void checkmateDialog(int p) {
		DatabaseUser db = new DatabaseUser();
		int averageTime = Main.moveTimer.calculateAverage(), v; //Gets the average move time for white
		if (p == 0) {
			v = Utils.showDialog("Checkmate!!", "Black has won! \nGame Over");
			if (db.c != null) { playerLost(averageTime, db); } //Update the database score
		} else {
			v = Utils.showDialog("Checkmate!!", "White has won! \nGame Over"); //Shows the user won won and lost
			if (db.c != null) { playerWon(averageTime, db); } //Update the database score
		}
		if (v == 0) { //Restart
			CBoard.reset();
		} else { //Dont Restart
			MoveHistory.peekNext().setCheckmate(true);
			MoveHistory.peekNext().setPlayerWon(p);
		}
	}
	private void checkDialog(int p) {
		Utils.simpleDialog("Check!", "Check"); //Shows the user that a check occured
		MoveHistory.peekNext().setCheck(true); //Sets the check status for the current move
	}
	public void timedLossDialog() {
		DatabaseUser db = new DatabaseUser();
		int averageTime = Main.moveTimer.calculateAverage(), v;
		Main.t1.pause(); Main.t2.pause();
		if (CBoard.getPlayer() == 0) {
			v = Utils.showDialog("Game Over!!", "Black has won due to White's Timer");
			if (db.c != null) { playerLost(averageTime, db); }
		} else {
			v = Utils.showDialog("Game Over!!", "White has won due to Black's Timer");
			if (db.c != null) { playerWon(averageTime, db); }
		}
		gameOver = -1;
		Main.t1.reset(); Main.t2.reset();
		if (v == 0) { 
			CBoard.reset();
			Main.t1.start(false);
		} else {
			Main.t1.start(true);
		}

	}
	private void playerWon(int averageTime, DatabaseUser db) {
		db.setUserStats(1, 0, 0, Integer.toString(averageTime), BoardConstants.username); //Updates the number of wins
	}
	private void playerLost(int averageTime, DatabaseUser db) {
		db.setUserStats(0, 1, 0, Integer.toString(averageTime), BoardConstants.username); //Updates the number of losses
	}
	private void playerDrew(int averageTime, DatabaseUser db) {
		db.setUserStats(0, 0, 1, Integer.toString(averageTime), BoardConstants.username); //Updates the number of draws
	}
	public int getGM() { return BoardConstants.gameMode; } //Gets the current game mode 
}
