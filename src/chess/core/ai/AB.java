package chess.core.ai;

import java.util.List;
import chess.core.bitboards.Board;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.eval.Evaluation;
import chess.core.bitboards.moves.Move;
import chess.core.bitboards.moves.Moves;
import chess.core.utils.Sorting;

public class AB {

  public Move alphaBetaMax(Move m, int alpha, int beta, int depth, int player) {
    if (depth == 0) {
      m.setScore(Evaluation.eval(player));
      return m;
    } // If at leaf node, evaluate board
    List<Move> moves = Board.getPossibleMoves(player); // Gets a list of current possible moves
    if (moves.size() > 0) {
      moves = sortMoves(moves); // Sorts the moves from best to worst
      for (Move currentMove : moves) {
        makeMove(currentMove);
        Move nextMove = alphaBetaMin(currentMove, alpha, beta, depth - 1, currentMove.getPlayer() ^ 1);
        int score = nextMove.getScore();
        unmakeMove(currentMove);

        if (depth == BoardConstants.PVS_DEPTH)
          m = nextMove;
        if (score >= beta) {
          m.setScore(beta);
          return m;
        }
        if (score > alpha)
          alpha = score;
      }
      m.setScore(alpha);
      return m;
    }
    return null;
  }

  private Move alphaBetaMin(Move m, int alpha, int beta, int depth, int player) {
    if (depth == 0) {
      m.setScore(-Evaluation.eval(player));
      return m;
    }
    List<Move> moves = Board.getPossibleMoves(player);
    if (moves.size() > 0) {
      moves = sortMoves(moves);
      for (Move currentMove : moves) {
        makeMove(currentMove);
        Move rMove = alphaBetaMax(currentMove, alpha, beta, depth - 1, currentMove.getPlayer() ^ 1);
        int score = rMove.getScore();
        unmakeMove(currentMove);
        // The only difference between alphaBetaMin and alphaBetaMax is the condition for best move
        if (score <= alpha) {
          m.setScore(alpha);
          return m;
        } // if score is less than alpha(min score), this is best move for minimising player
        if (score > beta)
          beta = score;
      }
      m.setScore(beta);
      return m;
    }
    return null;
  }

  private void unmakeMove(Move m) {
    Moves.undoMove(m.getPlayer(), m);
  }

  private void makeMove(Move m) {
    Moves.applyMove(m, m.getPlayer(), m.getType(), m.getMoveReg(), true);
  }

  private List<Move> sortMoves(List<Move> moves) {
    for (Move currentMove : moves) {
      makeMove(currentMove);
      currentMove.setScore(Evaluation.eval(currentMove.getPlayer()));
      unmakeMove(currentMove);
      currentMove.flipMove();
    }

    return Sorting.quickSort(moves, 0, moves.size() - 1);
  }
}
