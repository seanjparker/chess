package chess.core.display;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import chess.core.bitboards.BoardConstants;
import chess.core.display.input.MouseHandler;
import chess.core.display.window.StartMenu;

public class GUIHandler extends JFrame {
  private final double UFPS = 60.0;

  private boolean running = true;

  private final Chessboard cb;

  public GUIHandler() {
    final int prefWidth = Toolkit.getDefaultToolkit().getScreenSize().width >> 1;
    final int prefHeight = Toolkit.getDefaultToolkit().getScreenSize().height >> 1;

    final JMenuBar mb = new MenuBar().initMenuBar();
    cb = new Chessboard(prefWidth, prefHeight);
    cb.initChessboard(prefWidth, prefHeight);
    
    setTitle("Ϲhess");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setJMenuBar(mb);
    
    Container contents = getContentPane();
    contents.setLayout(new GridLayout(1, 0));
    contents.add(cb);

    pack();
    
    setLocationRelativeTo(null);

    new StartMenu().setVisible(true);

    run();
  }

  private void update() {
    if (!this.isVisible() && BoardConstants.gamemode != null)
      this.setVisible(true);
    if (Chessboard.gameOver == -1) {
      if (MouseHandler.pressedMouse) { 
        cb.pressedEvent(); //When a click occured, set the flag
        MouseHandler.pressedMouse = false; 
      } else if (MouseHandler.releasedMouse) { 
        cb.possMoves = ""; 
        cb.releasedEvent(); //If the user releases the click
        MouseHandler.releasedMouse = false; 
        MouseHandler.draggedMouse = false; 
      } else if (MouseHandler.draggedMouse) {
        cb.draggedEvent(); //If dragging the mouse, set flag required for animation 
      }
    }  
  }

  public void run() {
    long lastTime = System.nanoTime();
    long timer = System.currentTimeMillis();

    final double ns = 1000000000.0 / UFPS;
    double delta = 0;

    while (running) {
      long now = System.nanoTime();
      delta += (now - lastTime) / ns;
      lastTime = now;
      while (delta >= 1) {
        update();
        repaint();
        delta--;
      }

      if ((System.currentTimeMillis() - timer) > 1000)
        timer += 1000;
    }
  }
}
