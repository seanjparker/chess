package chess.core.utils;

import java.util.List;

import chess.core.bitboards.moves.Move;

public class Sorting {
	public static List<Move> quickSort(List<Move> a, int l, int h) {
		int i = partition(a, l , h); //Gets the initial partition
		if (l < i - 1) { //if partition is above the lowest
			quickSort(a, l, i - 1); //recursive quicksort
		}
		if (i < h) { //if partition is below highest after recursion
			quickSort(a, i, h); //perform recursion again
		}
		return a; //return the sorted array
	}
	
	private static int partition(List<Move> a, int l, int h) {
		Move p = a.get((l + h) / 2); //Gets the move at the current position
		int i = l, j = h;
		while (i <= j) {
			while (a.get(i).getScore() < p.getScore()) { i++; } //get position where not sorted
			while (a.get(j).getScore() > p.getScore()) { j--; }
			
			if (i <= j) {
				swap(a, i, j); //Swaps the unsorted elements
				i++;
				j--;
			}
		}
		return i;
	}
	
	private static final List<Move> swap(List<Move> a, int p1, int p2) {
		Move a1 = a.get(p1); //Swaps the positions of the unsprted elements
		a.set(p1, a.get(p2));
		a.set(p2, a1);
		return a;
	}
}
