package chess.core.initialize;

import chess.core.bitboards.Board;
import chess.core.bitboards.Pair;

public class BitboardInit {

  public static void initBitboards() {
    String ChessBoard[][] =
        {{"r", "n", "b", "q", "k", "b", "n", "r"},
         {"p", "p", "p", "p", "p", "p", "p", "p"},
         {" ", " ", " ", " ", " ", " ", " ", " "}, 
         {" ", " ", " ", " ", " ", " ", " ", " "},
         {" ", " ", " ", " ", " ", " ", " ", " "}, 
         {" ", " ", " ", " ", " ", " ", " ", " "},
         {"P", "P", "P", "P", "P", "P", "P", "P"}, 
         {"R", "N", "B", "Q", "K", "B", "N", "R"}};

    arrayToBitboard(ChessBoard);
  }

  public static void arrayToBitboard(String[][] ChessBoard) {
    long WP = 0L, WN = 0L, WB = 0L, WR = 0L, WQ = 0L, WK = 0L, BP = 0L, BN = 0L, BB = 0L, BR = 0L,
        BQ = 0L, BK = 0L;
    String binary; // Stores the binary string that represents the specific bitboard
    for (int i = 0; i < 64; i++) {
      binary = "0000000000000000000000000000000000000000000000000000000000000000";
      binary = binary.substring(i + 1) + "1" + binary.substring(0, i);
      switch (ChessBoard[i / 8][i % 8]) {
        case "P": // When a P(pawn) is found, apply bit to bitboard
          WP += stringToBitboard(binary);
          break;
        case "N": // When a N(knight) is found, apply bit to bitboard
          WN += stringToBitboard(binary);
          break;
        case "B": // When a B(bishop) is found, apply bit to bitboard
          WB += stringToBitboard(binary);
          break;
        case "R": // When a R(rook) is found, apply bit to bitboard
          WR += stringToBitboard(binary);
          break;
        case "Q": // When a Q(queen) is found, apply bit to bitboard
          WQ += stringToBitboard(binary);
          break;
        case "K": // When a K(king) is found, apply bit to bitboard
          WK += stringToBitboard(binary);
          break;
        case "p": // When a p(pawn) is found, apply bit to bitboard
          BP += stringToBitboard(binary);
          break;
        case "n": // When a n(knight) is found, apply bit to bitboard
          BN += stringToBitboard(binary);
          break;
        case "b": // When a b(bishop) is found, apply bit to bitboard
          BB += stringToBitboard(binary);
          break;
        case "r": // When a r(rook) is found, apply bit to bitboard
          BR += stringToBitboard(binary);
          break;
        case "q": // When a q(queen) is found, apply bit to bitboard
          BQ += stringToBitboard(binary);
          break;
        case "k": // When a k(king) is found, apply bit to bitboard
          BK += stringToBitboard(binary);
          break;
      }
    }
    // Apply all local bitboards to global bitboard class
    Board.pieces[0] = new Pair(WP, 0); // Apply the generated bitboards to global
    Board.pieces[1] = new Pair(WN, 0);
    Board.pieces[2] = new Pair(WB, 0);
    Board.pieces[3] = new Pair(WR, 0);
    Board.pieces[4] = new Pair(WQ, 0);
    Board.pieces[5] = new Pair(WK, 0);

    Board.pieces[6] = new Pair(BP, 1);
    Board.pieces[7] = new Pair(BN, 1);;
    Board.pieces[8] = new Pair(BB, 1);;
    Board.pieces[9] = new Pair(BR, 1);;
    Board.pieces[10] = new Pair(BQ, 1);;
    Board.pieces[11] = new Pair(BK, 1);;
  }

  private static long stringToBitboard(String binaryString) {
    if (binaryString.charAt(0) == '0') {
      return Long.parseLong(binaryString, 2); // Positive number
    } else {
      return Long.parseLong("1" + binaryString.substring(2), 2) * 2; // Negative number
    }
  }
}
