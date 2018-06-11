package chess.core.display.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import chess.core.bitboards.Board;
import chess.core.bitboards.moves.pieces.PieceType;
import chess.core.display.input.MousePressListener;

public class CreateBoard extends JFrame {

  private JPanel contentPane; //Panel for containing all buttons
  private final ButtonGroup pieceBtnGroup = new ButtonGroup(); //New button group(radio buttons)
  private final ButtonGroup colBtnGroup = new ButtonGroup();
  public static int whichSelected = 0; //White = 0, Black = 1
  public static PieceType pieceSelected = null; //Piece index selected when editing the board

  public CreateBoard() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        Board.setCreating(false); //When GUI closed, set flag
      }
    });

    Board.setCreating(true); //If opened, create flag

    setTitle("Editor"); //Set title
    setResizable(false); //Diabale resizing
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Only close createboard gui
    setBounds(100, 100, 360, 200);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    JRadioButton rdbtnBlack = new JRadioButton("Black"); //Create a new button
    rdbtnBlack.addMouseListener((MousePressListener) e ->
      whichSelected = 1 //Black is selected
    );
    colBtnGroup.add(rdbtnBlack);
    rdbtnBlack.setBounds(30, 10, 80, 23);
    contentPane.add(rdbtnBlack); //Add button to content pane

    JRadioButton rdbtnWhite = new JRadioButton("White"); //New button
    rdbtnWhite.addMouseListener((MousePressListener) e ->
      whichSelected = 0 //White is selected
    );
    colBtnGroup.add(rdbtnWhite);
    rdbtnWhite.setBounds(110, 10, 80, 23);
    contentPane.add(rdbtnWhite); //Add button

    JRadioButton rdbtnKing = new JRadioButton("King");
    rdbtnKing.addMouseListener((MousePressListener) e ->
      pieceSelected = PieceType.KING //King is selected event
    );
    pieceBtnGroup.add(rdbtnKing);
    rdbtnKing.setBounds(30, 61, 80, 23);
    contentPane.add(rdbtnKing);

    JRadioButton rdbtnQueen = new JRadioButton("Queen");
    rdbtnQueen.addMouseListener((MousePressListener) e ->
      pieceSelected = PieceType.QUEEN
    );
    pieceBtnGroup.add(rdbtnQueen);
    rdbtnQueen.setBounds(30, 87, 80, 23);
    contentPane.add(rdbtnQueen);

    JRadioButton rdbtnRook = new JRadioButton("Rook");
    rdbtnRook.addMouseListener((MousePressListener) e ->
      pieceSelected = PieceType.ROOK //Rook is selected event
    );
    pieceBtnGroup.add(rdbtnRook);
    rdbtnRook.setBounds(30, 113, 80, 23);
    contentPane.add(rdbtnRook);

    JRadioButton rdbtnBishop = new JRadioButton("Bishop");
    rdbtnBishop.addMouseListener((MousePressListener) e ->
      pieceSelected = PieceType.BISHOP //Bishop is selected event
    );
    pieceBtnGroup.add(rdbtnBishop);
    rdbtnBishop.setBounds(103, 61, 80, 23);
    contentPane.add(rdbtnBishop);

    JRadioButton rdbtnKnight = new JRadioButton("Knight");
    rdbtnKnight.addMouseListener((MousePressListener) e ->
      pieceSelected = PieceType.KNIGHT //Knight is selected event
    );
    pieceBtnGroup.add(rdbtnKnight);
    rdbtnKnight.setBounds(103, 87, 80, 23);
    contentPane.add(rdbtnKnight);

    JRadioButton rdbtnPawn = new JRadioButton("Pawn");
    rdbtnPawn.addMouseListener((MousePressListener) e ->
      pieceSelected = PieceType.PAWN //Pawn is selected event
    );
    pieceBtnGroup.add(rdbtnPawn);
    rdbtnPawn.setBounds(103, 113, 80, 23);
    contentPane.add(rdbtnPawn);

    JButton btnClear = new JButton("Clear");
    btnClear.addMouseListener((MousePressListener) e ->
      clearBoards() //Clears the chess board
    );
    btnClear.setBounds(193, 11, 67, 135);
    contentPane.add(btnClear);

    JButton btnReset = new JButton("Reset");
    btnReset.addMouseListener((MousePressListener) e ->
      resetBoards() //Resets the chessboard to its original state
    );
    btnReset.setBounds(270, 11, 74, 135);
    contentPane.add(btnReset);
  }

  private void clearBoards() { Board.clear(); } //Clear the chessboard
  private void resetBoards() { Board.reset(); } //Reset the chessboard when called
}
