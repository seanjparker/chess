package chess.core.bitboards.eval;

import chess.core.bitboards.CBoard;
import chess.core.bitboards.Type;
import chess.core.utils.Utils;

public class Evaluation {
	//Piece score tables are used to evaluate the current position of all pieces, higher score = better position
	private static final int PAWN[] = { 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50, 50, 10, 10, 20, 30, 30, 20,
			10, 10, 5, 5, 10, 25, 25, 10, 5, 5, 0, 0, 0, 20, 20, 0, 0, 0, 5, -5, -10, 0, 0, -10, -5, 5, 5, 10, 10, -20,
			-20, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static final int KNIGHT[] = { -50, -40, -30, -30, -30, -30, -40, -50, -40, -20, 0, 0, 0, 0, -20, -40, -30,
			0, 10, 15, 15, 10, 0, -30, -30, 5, 15, 20, 20, 15, 5, -30, -30, 0, 15, 20, 20, 15, 0, -30, -30, 5, 10, 15,
			15, 10, 5, -30, -40, -20, 0, 5, 5, 0, -20, -40, -50, -40, -30, -30, -30, -30, -40, -50 };

	private static final int BISHOP[] = { -20, -10, -10, -10, -10, -10, -10, -20, -10, 0, 0, 0, 0, 0, 0, -10, -10, 0, 5,
			10, 10, 5, 0, -10, -10, 5, 5, 10, 10, 5, 5, -10, -10, 0, 10, 10, 10, 10, 0, -10, -10, 10, 10, 10, 10, 10,
			10, -10, -10, 5, 0, 0, 0, 0, 5, -10, -20, -10, -10, -10, -10, -10, -10, -20 };

	private static final int ROOK[] = { 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, 10, 10, 10, 10, 5, -5, 0, 0, 0, 0, 0, 0, -5,
			-5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, 0,
			0, 0, 5, 5, 0, 0, 0 };

	private static final int QUEEN[] = { -20, -10, -10, -5, -5, -10, -10, -20, -10, 0, 0, 0, 0, 0, 0, -10, -10, 0, 5, 5,
			5, 5, 0, -10, -5, 0, 5, 5, 5, 5, 0, -5, 0, 0, 5, 5, 5, 5, 0, -5, -10, 5, 5, 5, 5, 5, 0, -10, -10, 0, 5, 0,
			0, 0, 0, -10, -20, -10, -10, -5, -5, -10, -10, -20 };

	private static final int KING_MID[] = { -30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40,
			-30, -30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -20, -30, -30, -40,
			-40, -30, -30, -20, -10, -20, -20, -20, -20, -20, -20, -10, 20, 20, 0, 0, 0, 0, 20, 20, 20, 30, 10, 0, 0,
			10, 30, 20 };

	private static final int KING_END[] = { -50, -40, -30, -20, -20, -30, -40, -50, -30, -20, -10, 0, 0, -10, -20, -30,
			-30, -10, 20, 30, 30, 20, -10, -30, -30, -10, 30, 40, 40, 30, -10, -30, -30, -10, 30, 40, 40, 30, -10, -30,
			-30, -10, 20, 30, 30, 20, -10, -30, -30, -30, 0, 0, 0, 0, -30, -30, -50, -30, -30, -30, -30, -30, -30,
			-50 };

	public static int eval(int player) {
		return staticPosition(player);
	}

	private static int staticPosition(int player) {
		int score = 0, material = material(player), oMaterial = material(player ^ 1); //Gets material score for current and other player
		score += material + pieceSquareTables(player, material);//Gets position score for current player
		score -= oMaterial + pieceSquareTables(player ^ 1, oMaterial);//Gets position score for other player
		score += kSaftey(player); //Checks if the king is in check
		return score; //Returns the score for the node
	}

	private static int material(int player) {
		int s = 0;
		for (int i = 0; i < CBoard.pieces.length; i++) { //Gets score baced on number of piece on board
			s += Type.SCORE[i] * Utils.popCount(CBoard.pieces[i][player]);
		}
		return s;
	}

	private static int kSaftey(int p) {
		if (CBoard.kingInCheck(p)) {
			if (CBoard.kingInCheckmate(p)) {
				return -5000000; //If the player is in check, ensure this move that leads to checkmate will not be played					
			} else {
				return -500000;
			}
		}
		return 0; //Otherwise, this should not affect final score
	}

	private static int pieceSquareTables(int player, int material) {
		int s = 0;
		// Piece Placement
		for (int i = 0; i < CBoard.pieces.length; i++) {
			long bb = CBoard.pieces[i][player];
			while (bb != 0) { //Based on current position of the piece in the board, get the score from piece tables
				int bitPos = Utils.bitPosition(bb);
				if (player == 1) { bitPos ^= 56; }

				if (Type.PAWN == i) {
					s += PAWN[bitPos]; break;
				} else if (Type.BISHOP == i) {
					s += BISHOP[bitPos]; break;
				} else if (Type.KNIGHT == i) {
					s += KNIGHT[bitPos]; break;
				} else if (Type.QUEEN == i) {
					s += QUEEN[bitPos]; break;
				} else if (Type.ROOK == i) {
					s += ROOK[bitPos]; break;
				} else if (Type.KING == i) {
					if (Math.floor((material / 100)) <= 13) {
						s += KING_END[bitPos]; // End game
					} else {
						s += KING_MID[bitPos]; // Mid game
					}
					break;
				}
				bb &= bb - 1; // Get the next bit in the bitboard
			}
		}
		return s; //Return sum of the final score from all pieces
	}
}