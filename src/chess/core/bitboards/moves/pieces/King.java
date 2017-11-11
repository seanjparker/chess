package chess.core.bitboards.moves.pieces;

import chess.core.bitboards.moves.Moves;

public class King extends Piece {
	@Override
	public long wGetPossibleTargets(long pieceBB, long empty) {
		long targets = Moves.eastOne(pieceBB) | Moves.westOne(pieceBB);
		long kingTemp = pieceBB | targets;
		targets |= Moves.northOne(kingTemp) | Moves.southOne(kingTemp);
		return targets & empty; //Gets all moves adjacent to piece
	}
	
	@Override
	public long bGetPossibleTargets(long pieceBB, long empty) {
		long targets = Moves.eastOne(pieceBB) | Moves.westOne(pieceBB);
		long kingTemp = pieceBB | targets;
		targets |= Moves.northOne(kingTemp) | Moves.southOne(kingTemp);
		return targets & empty; //Gets all moves adjacent to piece
	}
	
	@Override
	public long wGetPossibleCaptures(long pieceBB, long bOccupied, long empty) {
		long targets = Moves.eastOne(pieceBB) | Moves.westOne(pieceBB);
		long kingTemp = pieceBB | targets;
		targets |= Moves.northOne(kingTemp) | Moves.southOne(kingTemp);
		return targets & (~empty) & bOccupied; //Gets all captures that are adjacent to piece
	}
	
	@Override
	public long bGetPossibleCaptures(long pieceBB, long wOccupied, long empty) {
		long targets = Moves.eastOne(pieceBB) | Moves.westOne(pieceBB);
		long kingTemp = pieceBB | targets;
		targets |= Moves.northOne(kingTemp) | Moves.southOne(kingTemp);
		return targets & (~empty) & wOccupied; //Gets all captures that are adjacent to piece
	}
	
	@Override
	public long wGetPossiblePieces(long pieceBB, long empty, long bOcc) {
		return wGetPossibleTargets(pieceBB, empty) | (wGetPossibleCaptures(pieceBB, bOcc, empty)); //Gets moves and captures
	}

	@Override
	public long bGetPossiblePieces(long pieceBB, long empty, long wOcc) {
		return bGetPossibleTargets(pieceBB, empty) | (bGetPossibleCaptures(pieceBB, wOcc, empty)); //Get moves and captures
	}


}
