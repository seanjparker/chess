package chess.core.bitboards;

public class BoardConstants {
  public static int WIDTH_F = 0, HEIGHT_F = 0; // Height and width of the screen
  public static int squareSize = 0; // Square size of the chessboard squares
  public static int PVS_DEPTH = 3; // Search depth of the AI

  public static enum Mode {
    ONE_PLAYER, TWO_PLAYER,
  }

  public static Mode gamemode = null;
  
  public static final long FILE_A = ~(72340172838076673L); //Used to prevent file wraps along the right
  public static final long FILE_H = ~(-9187201950435737472L); //Used to prevent file wraps along the left
  public static final long FILE_AB = ~(217020518514230019L); //Used to prevent file wraps along the two right most files
  public static final long FILE_GH = ~(-4557430888798830400L); //Used to prevent file wraps along the two left most files
  public static final long RANK_4 = 4278190080L; //Used to only allow double pawn movements one the first move (Black)
  public static final long RANK_5 = 1095216660480L; //Used to only allow double pawn movements one the first move (White)
  public static final long RANK_MASKS[] = // from Rank 1 to Rank 8
          { 0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L,
                  0xFF00000000000000L };
  public static final long FILE_MASKS[] = // from File A to File H
          { 0x101010101010101L, 0x202020202020202L, 0x404040404040404L, 0x808080808080808L, 0x1010101010101010L,
                  0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L };
  public static final long DIAGONAL_MASKS[] = //from top left to bottom right
          { 0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L, 0x1020408102040L, 0x102040810204080L,
                  0x204081020408000L, 0x408102040800000L, 0x810204080000000L, 0x1020408000000000L,
                  0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L };
  public static final long ANTIDIAGONAL_MASKS[] = // from top right to bottom left
          { 0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L, 0x80402010080402L,
                  0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L,
                  0x804020100000000L, 0x402010000000000L, 0x201000000000000L, 0x100000000000000L };

  public static final int INITIAL_ROOK_POSITIONS[] = { 56, 63, 0, 7 }; //Rook initial positions
  public static final int FINAL_ROOK_POSITIONS[] = { 59, 61, 3, 5 }; //Rook final positions

  public static final int INITIAL_KING_POSITIONS[] = { 60, 60, 4, 4 }; //King initial positions
  public static final int FINAL_KING_POSITIONS[] = { 58, 62, 2, 6 }; //King final positions
/*
  public static final long FILE_A = ~(72340172838076673L);//0xfefefefefefefefeL;//~(72340172838076673L); // Used to prevent file wraps along the right
  public static final long FILE_H = ~(-9187201950435737472L);//0x7f7f7f7f7f7f7f7fL;//~(-9187201950435737472L); // Used to prevent file wraps along the left
  public static final long FILE_AB = ~(217020518514230019L);//0xfcfcfcfcfcfcfcfcL;//~(217020518514230019L); // Used to prevent file wraps along the two right most files
  public static final long FILE_GH = ~(-4557430888798830400L);//0x3f3f3f3f3f3f3f3fL;//~(-4557430888798830400L); // Used to prevent file wraps along the two left most files
  public static final long RANK_4 = 4278190080L;//0xff000000L;//4278190080L; // Used to only allow double pawn movements one the first move (Black)
  public static final long RANK_5 = 1095216660480L;//0xff00000000L;//1095216660480L; // Used to only allow double pawn movements one the first move (White)
  public static final long RANK_MASKS[] = {
      0xFFL,
      0xFF00L,
      0xFF0000L,
      0xFF000000L,
      0xFF00000000L,
      0xFF0000000000L,
      0xFF000000000000L,
      0xFF00000000000000L
      }; // from Rank 1 to Rank 8
  
  public static final long FILE_MASKS[] = {
      0x101010101010101L,
      0x202020202020202L,
      0x404040404040404L,
      0x808080808080808L,
      0x1010101010101010L,
      0x2020202020202020L,
      0x4040404040404040L,
      0x8080808080808080L
      }; // from File A to File H
  
  public static final long DIAGONAL_MASKS[] = {
      0x1L, 0x102L, 0x10204L, 0x1020408L,
      0x102040810L, 0x10204081020L, 0x1020408102040L, 0x102040810204080L,
      0x204081020408000L, 0x408102040800000L, 0x810204080000000L, 0x1020408000000000L,
      0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L
      }; // from top left to bottom right
  public static final long ANTIDIAGONAL_MASKS[] = {
      0x80L, 0x8040L, 0x804020L, 0x80402010L,
      0x8040201008L, 0x804020100804L, 0x80402010080402L, 0x8040201008040201L,
      0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L, 0x804020100000000L,
      0x402010000000000L, 0x201000000000000L, 0x100000000000000L
      }; // from top right to bottom left

  public static final int INITIAL_ROOK_POSITIONS[] = {56, 63, 0, 7}; // Rook initial positions
  public static final int FINAL_ROOK_POSITIONS[] = {59, 61, 3, 5}; // Rook final positions

  public static final int INITIAL_KING_POSITIONS[] = {60, 60, 4, 4}; // King initial positions
  public static final int FINAL_KING_POSITIONS[] = {58, 62, 2, 6}; // King final positions
  */
}
