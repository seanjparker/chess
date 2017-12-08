package chess.core.display;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import chess.core.display.input.MouseHandler;
import chess.core.utils.Timer;

public class GUIHandler extends JFrame {
  private final double UFPS = 60.0;
  
  private Chessboard cb;
  
  private MouseHandler m;
  private MenuBar mb;
  
  public static Timer t1 = new Timer(600); //White timer, length = 10m
  public static Timer t2 = new Timer(600); //Black timer, length = 10m 
  public static Timer moveTimer = new Timer(1);
  
  private boolean running = true;
  
  public GUIHandler() {
    mb = new MenuBar();
    m = new MouseHandler();
    
    final int prefWidth = Toolkit.getDefaultToolkit().getScreenSize().width >> 1;
    final int prefHeight = Toolkit.getDefaultToolkit().getScreenSize().height >> 1;
    
    setTitle("Ϲʜеςς");
    setPreferredSize(new Dimension(prefWidth, prefHeight));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
     
    Container contents = getContentPane();
    contents.setLayout(new GridLayout(1,0));
    cb = new Chessboard(prefWidth, prefHeight);
    contents.add(cb);
    
    setJMenuBar(mb.initMenuBar()); //Adds the menu bar to the ui
    pack();
    
    cb.initChessboard();
  }
  
  
  private void update() {
    /*
    if (GUI.gameOver == -1) {
        if (MouseHandler.pressedMouse) {
            ui.pressedEvent(); //When a click occured, set the flag
            MouseHandler.pressedMouse = false;
        } else if (MouseHandler.releasedMouse) {
            ui.possMoves = "";
            ui.releasedEvent(); //If the user releases the click
            MouseHandler.releasedMouse = false;
            MouseHandler.draggedMouse = false;
        } else if (MouseHandler.draggedMouse) {
            ui.draggedEvent(); //If dragging the mouse, set flag required for animation
        }
    } else {
        ui.timedLossDialog(); //If game over, timed loss dialog, prevents overflow of renders
    }
    */
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
        
        if ((System.currentTimeMillis() - timer) > 1000) {
            timer += 1000;
        }
    }
}
}
