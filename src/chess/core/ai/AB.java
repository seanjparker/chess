package chess.core.ai;

import java.util.List;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.CBoard;
import chess.core.bitboards.eval.Evaluation;
import chess.core.bitboards.moves.Move;
import chess.core.bitboards.moves.Moves;
import chess.core.utils.Sorting;

public class AB {
	private Moves moves = new Moves();
	private Move m;
	private int alpha, beta, depth, player;
	
	public AB(Move m, int alpha, int beta, int depth, int player) { //Class constructor
		this.m = m; 
		this.alpha = alpha;
		this.beta = beta;
		this.depth = depth;
		this.player = player;
	}
	
	public Move startSearch() {
		return alphaBetaMax(this.m, this.alpha, this.beta, this.depth, this.player); //Starts the search with initial values
	}
	
	private Move alphaBetaMax(Move m, int alpha, int beta, int depth, int player) { //Search maximising player
		if (depth == 0) { m.setScore(Evaluation.eval(player)); return m; } //If at leaf node, evaluate board
		List<Move> moves = CBoard.getAIMoves(player); //Gets a list of current possible moves
		if (moves.size() > 0) { //If valid moves
			moves = sortMoves(moves); //Sorts the moves from best to worst
			for (int i = 0; i < moves.size(); i++) { //Interates through all possible moves found
				Move m1 = moves.get(i); //Gets the current indexed move
				makeMove(m1); //Makes the move
				Move rMove= alphaBetaMin(m1, alpha, beta, depth - 1, m1.getPlayer() ^ 1); //Recursive call for minimising player
				int score = rMove.getScore(); //Gets the score of the current node
				unmakeMove(m1); //Undoes the move, returning board to original state
				if (depth == BoardConstants.PVS_DEPTH) { m = rMove; } //If at root node, set the move
				if (score >= beta) { m.setScore(beta); return m; } //Depending on the score, set the best move
				if (score > alpha) { alpha = score; }
			}
			m.setScore(alpha); return m; //Sets the score and returns the move 
		}
		return null;
	}
	private Move alphaBetaMin(Move m, int alpha, int beta, int depth, int player) { //Search minimising player
		if (depth == 0) { m.setScore(-Evaluation.eval(player)); return m; }		
		List<Move> moves = CBoard.getAIMoves(player);
		if (moves.size() > 0) {
			moves = sortMoves(moves);
			for (int i = 0; i < moves.size(); i++) {
				Move m1 = moves.get(i);				
				makeMove(m1);
				Move rMove= alphaBetaMax(m1, alpha, beta, depth - 1, m1.getPlayer() ^ 1);
				int score = rMove.getScore();
				unmakeMove(m1);
				//The only difference between alphaBetaMin and alphaBetaMax is the condition for best move
				if (score <= alpha) { m.setScore(alpha); return m; } //if score is less than alpha(min score), this is best move for minimising player 
				if (score > beta) { beta = score; } //otherwise this is the best score
			}
			m.setScore(beta); return m; //Return the move with the highest score
		}
		return null;
	}
	
	private void unmakeMove(Move m) {
		moves.undoMove(m.getPlayer(), m); //Undo the move passed to the method
	}
	
	private void makeMove(Move m) {
		moves.applyMove(m, m.getPlayer(), m.getType(), m.getMoveReg(), true); //Apply the move
	}
	
	private List<Move> sortMoves(List<Move> moves) {
		for (int i = 0; i < moves.size(); i++) {
			Move m = moves.get(i);
			makeMove(m); //Make the current move
			moves.get(i).setScore(Evaluation.eval(m.getPlayer())); //Get the new evalution of the game board
			unmakeMove(m); //Undo the move
			moves.get(i).flipMove(); //Flip the move back so the move can be made correctly next time
		}		
		//Every move has been evaluated, sort the moves - from best to worst score
		moves = Sorting.quickSort(moves, 0, moves.size() - 1); //Apply the sort algortihm to the current move set
		return moves;
	}
}
