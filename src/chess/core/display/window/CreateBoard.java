package chess.core.display.window;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import chess.core.bitboards.Board;

public class CreateBoard extends JFrame {

  private JPanel contentPane; //Panel for containing all buttons
  private final ButtonGroup buttonGroup = new ButtonGroup(); //New button group(radio buttons)
  public static int whichSelected = 0; //White = 0, Black = 1
  public static int pieceSelected = 0; //Piece index selected when editing the board

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

    JButton btnBlack = new JButton("Black"); //Create a new button
    btnBlack.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        whichSelected = 1; //Black is selected
      }
    });
    btnBlack.setBounds(20, 11, 150, 23);
    contentPane.add(btnBlack); //Add button to content pane

    JButton btnWhite = new JButton("White"); //New button
    btnWhite.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        whichSelected = 0; //White is selected
      }
    });
    btnWhite.setBounds(20, 123, 150, 23);
    contentPane.add(btnWhite); //Add button

    JRadioButton rdbtnKing = new JRadioButton("King");
    rdbtnKing.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        pieceSelected = 5; //King is selected event
      }
    });
    buttonGroup.add(rdbtnKing);
    rdbtnKing.setBounds(30, 41, 71, 23);
    contentPane.add(rdbtnKing);

    JRadioButton rdbtnQueen = new JRadioButton("Queen");
    rdbtnQueen.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        pieceSelected = 3; //Queen is selected event
      }
    });
    buttonGroup.add(rdbtnQueen);
    rdbtnQueen.setBounds(30, 67, 71, 23);
    contentPane.add(rdbtnQueen);

    JRadioButton rdbtnRook = new JRadioButton("Rook");
    rdbtnRook.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        pieceSelected = 4; //Rook is selected event
      }
    });
    buttonGroup.add(rdbtnRook);
    rdbtnRook.setBounds(30, 93, 71, 23);
    contentPane.add(rdbtnRook);

    JRadioButton rdbtnBishop = new JRadioButton("Bishop");
    rdbtnBishop.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        pieceSelected = 1; //Bishop is selected event
      }
    });
    buttonGroup.add(rdbtnBishop);
    rdbtnBishop.setBounds(103, 41, 67, 23);
    contentPane.add(rdbtnBishop);

    JRadioButton rdbtnKnight = new JRadioButton("Knight");
    rdbtnKnight.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        pieceSelected = 2; //Knight is selected event
      }
    });
    buttonGroup.add(rdbtnKnight);
    rdbtnKnight.setBounds(103, 67, 67, 23);
    contentPane.add(rdbtnKnight);

    JRadioButton rdbtnPawn = new JRadioButton("Pawn");
    rdbtnPawn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        pieceSelected = 0; //Pawn is selected event
      }
    });
    buttonGroup.add(rdbtnPawn);
    rdbtnPawn.setBounds(103, 93, 67, 23);
    contentPane.add(rdbtnPawn);

    JButton btnClear = new JButton("Clear");
    btnClear.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        clearBoards(); //Clears the chess board
      }
    });
    btnClear.setBounds(193, 11, 67, 135);
    contentPane.add(btnClear);

    JButton btnReset = new JButton("Reset");
    btnReset.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        resetBoards(); //Resets the chessboard to its original state
      }
    });
    btnReset.setBounds(270, 11, 74, 135);
    contentPane.add(btnReset);

    rdbtnPawn.setSelected(true); //Sets the default selected
    btnWhite.setSelected(true); //Sets the default selected
  }

  private void clearBoards() { Board.clear(); } //Clear the chessboard
  private void resetBoards() { Board.reset(); } //Reset the chessboard when called
}
