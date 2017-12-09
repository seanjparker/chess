package chess.core.utils;

import javax.swing.JOptionPane;
import chess.core.bitboards.Type;
import chess.core.bitboards.Type.Piece;
import chess.core.bitboards.moves.Move;

public class Utils {
	
	public static int convert2D1D(int x, int y) {
		return 8 * y + x; //Gets 1d array coordinates from 2d coordinates
	}
	
	public static int getShiftFrom(String move) {
		return getY(move, true) + getX(move, true); //Gets bitboard shift
	}
	public static int getShiftTo(String move) {
		return getY(move, false) + getX(move, false); //Gets bitboard shift
	}
	
	public static int getX(String move, boolean tf) {
		//Rank - True means the current square of the piece, False if the square moving to
		if (tf) {
			return Character.getNumericValue(move.charAt(1)); //gets numeric value of the character
		} else {
			return Character.getNumericValue(move.charAt(3)); //gets numeric value of the character
		}
	}
	public static int getY(String move, boolean tf) {
		//File - True means the current square of the piece, False if the square moving to
		if (tf) {
			return Character.getNumericValue(move.charAt(0)) * 8; //gets numeric value of the character	 		
		} else {
			return Character.getNumericValue(move.charAt(2)) * 8; //gets numeric value of the character
		}
	}
	
	public static int getX(long pieceBB) {
		return Long.numberOfTrailingZeros(pieceBB) % 8; //Rank
	}
	public static int getY(long pieceBB) {
		return Long.numberOfTrailingZeros(pieceBB) / 8; //File
	}
	
	public static String calculateDragMove(int mX, int mY, int mX1, int mY1, double squareS) {
		//This will determine the co-ordinates of the point where the user clicked
		return "" + (int)(mY / squareS) + (int)(mX / squareS) + (int)(mY1 / squareS) + (int)(mX1 / squareS);
	}
	
	public static Move flipMove(Move m) {
		return m.flipMove(m); //flips the move
	}
	
	public static int popCount(long b) {
		if (b == 0) { return 0; } //no bits present
		if ((b & (b - 1)) == 0) { return 1; } //only one bit present
		int r = 0; //if not zero or one, we have to count
		while (b != 0) {
			r++;
			b &= (b - 1);
		}
		return r; //return the number of bits
	}	
	public static int bitPosition(long bb) {
		return Long.numberOfTrailingZeros(LSB1(bb)); //Gets the bit position in the bitboard
	}
	public static long LSB1(long bb) {
		return bb & -bb; //Gets the next bit in the bitboard
	}
	
	//simple information dialog
	public static void simpleDialog(String titleMessage, String mainMessage) {
		JOptionPane.showMessageDialog(null, mainMessage, titleMessage, JOptionPane.INFORMATION_MESSAGE);
	}
	public static void simpleDialog(String titleMessage, String mainMessage, int messageType) {
		JOptionPane.showMessageDialog(null, mainMessage, titleMessage, messageType);
	}
	public static String convertToAlgebraic(Move m) {
		final int[] algY = {8, 7, 6, 5, 4, 3, 2, 1}; //This is to reverse the y axis co-ordinates
		int x1, x2, y2;
		String r = null;
		
		x1 = m.getFromX();
		x2 = m.getToX();
		y2 = m.getToY();
		
		if (m.getPieceCI() != -1) { //capture
			if (m.getPieceI() == Piece.PAWN.id) { //pawn is being moved
				r = "" + ((char) (97 + x1)) + "x" + ((char) (97 + x2)) + algY[y2]; //pawn capture to a location
			} else {
				r = "" + Type.tPieceString[m.getPieceI()] + "x" + ((char) (97 + x2)) + algY[y2]; //other piece capture
			}
		} else if (m.getType() == 4) { //promotion
			r = "" + ((char) (97 + x2)) + algY[y2] + Type.tPieceString[m.getPieceI()];
		} else if (m.getType() == 2) { //castle, queen side or king side
			if (m.getLS() == 0) { r = "0-0-0"; } //Queen side castle
			else if (m.getLS() == 1) { r = "0-0"; } //King side castle
		} else if (m.getType() == 3) { //en passant, specific characters required
			r = "" + ((char) (97 + x1)) + "x" + ((char) (97 + x2)) + algY[y2] + "e.p.";
		} else { //normal move
			r = "" + Type.tPieceString[m.getPieceI()] + ((char) (97 + x2)) + algY[y2];
		}
		
		if (m.getCheck()) { 
			r += "+"; //check was made
		} else if (m.getCheckmate()) { 
			r += "#"; //a player has won, checkmate
			if (m.getPlayerWon() == 0) {
				r += " 1-0"; //1-0 for white winning
			} else {
				r += " 0-1"; //0-1 for black winning
			}
		}
		return r;
	}
	//Dialog with buttons that the user can click to perform different actions
	public static int showDialog(String title, String message) {
		int op1 = JOptionPane.DEFAULT_OPTION, op2 = JOptionPane.INFORMATION_MESSAGE;
		Object[] options = new Object[]{"New Game", "Go Back"};
		return JOptionPane.showOptionDialog(null, message, title, op1, op2, null, options, options[0]);
	}
	public static int showDialog(String title, String message, Object[] options) {
		int op1 = JOptionPane.DEFAULT_OPTION, op2 = JOptionPane.INFORMATION_MESSAGE;
		return JOptionPane.showOptionDialog(null, message, title, op1, op2, null, options, options[0]);
	}
}
