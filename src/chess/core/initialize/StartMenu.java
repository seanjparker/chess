package chess.core.initialize;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.BoardConstants.Mode;
import chess.core.display.GUIHandler;

public class StartMenu extends JFrame {
  private JPanel contentPane;
  private JButton btnOnePl, btnTwoPl;

  public StartMenu() {
    setTitle("Ϲʜеss | Menu");
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    toFront();

    contentPane = new JPanel();
    setContentPane(contentPane);
    getContentPane().setLayout(new FlowLayout());

    btnOnePl = new JButton("One Player");
    btnOnePl.addActionListener(e -> start(Mode.ONE_PLAYER));
    getContentPane().add(btnOnePl);

    btnTwoPl = new JButton("Two Player");
    btnTwoPl.addActionListener(e -> start(Mode.TWO_PLAYER));
    getContentPane().add(btnTwoPl);

    getRootPane().setDefaultButton(btnTwoPl);

    pack();
  }

  private void start(Mode m) {
    BoardConstants.gamemode = m;
    dispose();
  }
}
