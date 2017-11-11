package chess.core.initialize;

import chess.core.bitboards.CBoard;
import chess.core.bitboards.Type;

public class BitboardInit {
	
	public static void initBitboards() {
		String ChessBoard[][] = 
			  { { "r", "n", "b", "q", "k", "b", "n", "r" }, 
				{ "p", "p", "p", "p", "p", "p", "p", "p" }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " },
				{ " ", " ", " ", " ", " ", " ", " ", " " }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " }, 
				{ "P", "P", "P", "P", "P", "P", "P", "P" },
				{ "R", "N", "B", "Q", "K", "B", "N", "R" } };
		String ChessBoard1[][] = 
			  { { " ", "k", " ", " ", " ", " ", " ", "p" }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " },
				{ " ", " ", " ", " ", " ", " ", " ", " " }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " }, 
				{ " ", " ", " ", " ", " ", " ", " ", " " },
				{ "R", "N", "B", "Q", "K", " ", " ", " " } };
		
			arrayToBitboard(ChessBoard); //This 2D array of strings is used to store the inital states of the chessboard
	}
	
	public static void arrayToBitboard(String[][] ChessBoard) {
		long WP = 0L, WN = 0L, WB = 0L, WR = 0L, WQ = 0L, WK = 0L, BP = 0L, BN = 0L, BB = 0L, BR = 0L, BQ = 0L, BK = 0L;
		String binary; //Stores the binary string that represents the specific bitboard
		for (int i = 0; i < 64; i++) {
			binary = "0000000000000000000000000000000000000000000000000000000000000000";
			binary = binary.substring(i + 1) + "1" + binary.substring(0, i);
			switch (ChessBoard[i / 8][i % 8]) {
			case "P": //When a P(pawn) is found, apply bit to bitboard
				WP += stringToBitboard(binary); break;
			case "N": //When a N(knight) is found, apply bit to bitboard
				WN += stringToBitboard(binary); break;
			case "B": //When a B(bishop) is found, apply bit to bitboard
				WB += stringToBitboard(binary); break;
			case "R": //When a R(rook) is found, apply bit to bitboard
				WR += stringToBitboard(binary); break;
			case "Q": //When a Q(queen) is found, apply bit to bitboard
				WQ += stringToBitboard(binary); break;
			case "K": //When a K(king) is found, apply bit to bitboard
				WK += stringToBitboard(binary); break;
			case "p": //When a p(pawn) is found, apply bit to bitboard
				BP += stringToBitboard(binary); break;
			case "n": //When a n(knight) is found, apply bit to bitboard
				BN += stringToBitboard(binary); break;
			case "b": //When a b(bishop) is found, apply bit to bitboard
				BB += stringToBitboard(binary); break;
			case "r": //When a r(rook) is found, apply bit to bitboard
				BR += stringToBitboard(binary); break;
			case "q": //When a q(queen) is found, apply bit to bitboard
				BQ += stringToBitboard(binary); break;
			case "k": //When a k(king) is found, apply bit to bitboard
				BK += stringToBitboard(binary); break;
			}
		}
		//Apply all local bitboards to global bitboard class
		CBoard.pieces[Type.PAWN][0] = WP; //Apply the generated bitboards to global
		CBoard.pieces[Type.KNIGHT][0] = WN;
		CBoard.pieces[Type.BISHOP][0] = WB;
		CBoard.pieces[Type.ROOK][0] = WR;
		CBoard.pieces[Type.QUEEN][0] = WQ;
		CBoard.pieces[Type.KING][0] = WK;
		
		CBoard.pieces[Type.PAWN][1] = BP;
		CBoard.pieces[Type.KNIGHT][1] = BN;
		CBoard.pieces[Type.BISHOP][1] = BB;
		CBoard.pieces[Type.ROOK][1] = BR;
		CBoard.pieces[Type.QUEEN][1] = BQ;
		CBoard.pieces[Type.KING][1] = BK;
	}
	
	private static long stringToBitboard(String binaryString) {
		//Convert the binary string to the long data type
		if (binaryString.charAt(0) == '0') {
			return Long.parseLong(binaryString, 2); //Positive number
		} else {
			return Long.parseLong("1" + binaryString.substring(2), 2) * 2; //Negative number
		}
	}
}
