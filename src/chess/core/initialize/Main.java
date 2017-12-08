package chess.core.initialize;

import chess.core.display.GUIHandler;

public class Main {
	public static void main(String[] args) {
	    new StartMenu().setVisible(true);
	     GUIHandler display = new GUIHandler();
	     display.setVisible(true);
	     display.run();
	}
}
