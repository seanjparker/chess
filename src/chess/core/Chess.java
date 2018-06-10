package chess.core;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import chess.core.display.GUIHandler;

public class Chess {
  public static void main(String[] args) {
    String lcOSName = System.getProperty("os.name");
    boolean IS_MAC = lcOSName.contains("OS X");
    if (IS_MAC) {
      //place menu items on the mac toolbar
      System.setProperty("apple.laf.useScreenMenuBar", "true");

      //use smoother fonts
      System.setProperty("apple.awt.textantialiasing", "true");

      //ref: http://developer.apple.com/releasenotes/Java/Java142RNTiger/1_NewFeatures/chapter_2_section_3.html
      System.setProperty("apple.awt.graphics.EnableQ2DX","true");
    }

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException  | IllegalAccessException | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
   
    new GUIHandler().setVisible(true);
  }
}
