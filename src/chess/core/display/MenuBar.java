package chess.core.display;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import chess.core.bitboards.Board;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.moves.Moves;
import chess.core.display.window.CreateBoard;
import chess.core.utils.FEN;

public class MenuBar {
  private final Moves m = new Moves();

  public JMenuBar initMenuBar() {
    JMenuBar menuBar;
    JMenu fileMenu, optionsMenu;
    JMenuItem createMenuItem, newGameMenuItem, loadGameMenuItem, saveGameMenuItem, forfeitMenuItem,
    undoMenuItem, redoMenuItem, difficultyMenuItem;

    menuBar = new JMenuBar();

    fileMenu = new JMenu("File");
    menuBar.add(fileMenu);

    newGameMenuItem = new JMenuItem("New Game");
    newGameMenuItem.addActionListener(e -> {
      boolean startNew = (JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new game?",
          "Restart", JOptionPane.YES_NO_OPTION) == 0);
      if (startNew)
        Board.initChess();
    });

    fileMenu.add(newGameMenuItem);
    createMenuItem = new JMenuItem("Create Game");
    createMenuItem.addActionListener(e -> {
      CreateBoard cb = new CreateBoard();
      cb.setVisible(true);
      cb.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    });
    fileMenu.add(createMenuItem);

    loadGameMenuItem = new JMenuItem("Load Game");
    loadGameMenuItem.addActionListener(e -> {
      String fileName = JOptionPane.showInputDialog("Enter the name of the file to load");
      if (fileName != null) {
        FEN f = new FEN(fileName, Chessboard.FILE_PATH);
        String fen = f.loadFEN();
        f.parseFEN(fen);
      }
    });
    fileMenu.add(loadGameMenuItem);

    saveGameMenuItem = new JMenuItem("Save Game");
    saveGameMenuItem.addActionListener(e -> {
      String fileName = JOptionPane.showInputDialog("Enter the name of the save file");
      if (fileName != null)
        new FEN(fileName, Chessboard.FILE_PATH).saveFEN();
    });
    fileMenu.add(saveGameMenuItem);

    optionsMenu = new JMenu("Options");
    menuBar.add(optionsMenu);

    undoMenuItem = new JMenuItem("Undo");
    undoMenuItem.addActionListener(e -> {
      Moves.undoMove(Board.getPlayer() ^ 1, null);
    });

    redoMenuItem = new JMenuItem("Redo");
    redoMenuItem.addActionListener(e -> {
      Moves.redoMove(Board.getPlayer() ^ 1);
    });

    forfeitMenuItem = new JMenuItem("Forfeit");
    forfeitMenuItem.addActionListener(e -> {
      Moves.forfitCurrent();
    });

    difficultyMenuItem = new JMenuItem("Difficulty");
    difficultyMenuItem.addActionListener(e -> {
      int diff = 1;
      String[] poss = {"Easy", "Medium", "Hard", "Insane"};
      String s = (String) JOptionPane.showInputDialog(null, "Choose the AI difficulty:",
          "AI Difficulty", JOptionPane.INFORMATION_MESSAGE, null, poss, "Medium");
      if (s != null && s.length() > 0) {
        switch (s) {
          case "Easy":
            diff = 1;
            break;
          case "Medium":
            diff = 2;
            break;
          case "Hard":
            diff = 3;
            break;
          case "Insane":
            diff = 4;
            break;
        }
      }
      BoardConstants.PVS_DEPTH = diff << 1;
    });

    undoMenuItem.setAccelerator(
      KeyStroke.getKeyStroke(
        KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    redoMenuItem.setAccelerator(
      KeyStroke.getKeyStroke(
        KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    optionsMenu.add(forfeitMenuItem);
    optionsMenu.add(undoMenuItem);
    optionsMenu.add(redoMenuItem);
    optionsMenu.add(difficultyMenuItem);


    return menuBar;
  }
}
