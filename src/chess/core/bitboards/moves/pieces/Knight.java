package chess.core.bitboards.moves.pieces;

import chess.core.bitboards.moves.Moves;

public class Knight extends Piece {

	private long getAllPos(long pieceBB) { //Gets all L-Shaped moves
		return Moves.noNoEa(pieceBB) | Moves.noEaEa(pieceBB) | Moves.soEaEa(pieceBB) | Moves.soSoEa(pieceBB) | Moves.soSoWe(pieceBB) | Moves.soWeWe(pieceBB) | Moves.noWeWe(pieceBB) | Moves.noNoWe(pieceBB);
	}
	
	@Override
	public long wGetPossibleTargets(long pieceBB, long empty) {
		return getAllPos(pieceBB) & empty; //Gets white moves
	}
	
	@Override
	public long bGetPossibleTargets(long pieceBB, long empty) {
		return getAllPos(pieceBB) & empty; //Gets black moves
	}
	
	@Override
	public long wGetPossibleCaptures(long pieceBB, long bOccupied, long empty) {
		return getAllPos(pieceBB) & bOccupied & ~empty; //Get white captures
	}
	
	@Override
	public long bGetPossibleCaptures(long pieceBB, long wOccupied, long empty) {
		return getAllPos(pieceBB) & wOccupied & ~empty; //Get black captures
	}
	
	@Override
	public long wGetPossiblePieces(long pieceBB, long empty, long bOcc) { //Gets moves and captures
		return wGetPossibleTargets(pieceBB, empty) | (wGetPossibleCaptures(pieceBB, bOcc, empty));
	}

	@Override
	public long bGetPossiblePieces(long pieceBB, long empty, long wOcc) { //Gets moves and captures
		return bGetPossibleTargets(pieceBB, empty) | (bGetPossibleCaptures(pieceBB, wOcc, empty));
	}



}
