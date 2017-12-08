package chess.core.display;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import chess.core.bitboards.BoardConstants;
import chess.core.bitboards.CBoard;
import chess.core.bitboards.moves.Moves;
import chess.core.display.window.CreateBoard;
import chess.core.utils.FEN;

public class MenuBar {
	private Moves m = new Moves();
    
	public JMenuBar initMenuBar() {
		JMenuBar menuBar; //Main menu bar
		JMenu fileMenu, optionsMenu; //All the parent menu options
		JMenuItem createMenuItem, newGameMenuItem, loadGameMenuItem, saveGameMenuItem, forfitMenuItem, undoMenuItem, redoMenuItem, difficultyMenuItem, playerStatsMenuItem, highscoresMenuItem;
		//Sub menu items that are contained in the parent menus
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		//Creates a new action for each sub menu item
		newGameMenuItem = new JMenuItem(new AbstractAction("New Game") {
			public void actionPerformed(ActionEvent e) {
				int s = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new game?", "Restart", JOptionPane.YES_NO_OPTION);
				if (s == 0) { CBoard.initChess(); } //Starts a new chess game with original positions
			} //When the menu item is clicked, this action is performed
		});
		fileMenu.add(newGameMenuItem);
		//Each menu item contains a listener that is executed when clicked
		createMenuItem = new JMenuItem(new AbstractAction("Edit Board") {
			public void actionPerformed(ActionEvent e) {
				CreateBoard cb = new CreateBoard();
				cb.setVisible(true); //Displays the GUI for board editing
				cb.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
		fileMenu.add(createMenuItem);
		
		loadGameMenuItem = new JMenuItem(new AbstractAction("Load Game"){
			public void actionPerformed(ActionEvent e) {
				String fileName = JOptionPane.showInputDialog("Enter the name of the file to load");
				if (fileName != null) {	//Is the location is valid load the fen record
					FEN f = new FEN(fileName, Chessboard.FILE_PATH);
					String fen = f.loadFEN(); //Load the fen from file
					f.parseFEN(fen);//Parse the fen record from the file
				}	
			}
		});
		fileMenu.add(loadGameMenuItem);
		
		saveGameMenuItem = new JMenuItem(new AbstractAction("Save Game"){
			public void actionPerformed(ActionEvent e) {
				String fileName = JOptionPane.showInputDialog("Enter the name of the save file");
				if (fileName != null) {	//Allows the user to save the current game
					FEN f = new FEN(fileName, Chessboard.FILE_PATH);
					f.saveFEN(); //Generate and save the fen record to a file
				}	
			}
		});
		fileMenu.add(saveGameMenuItem);
		
		optionsMenu = new JMenu("Options");
		menuBar.add(optionsMenu);

		undoMenuItem = new JMenuItem(new AbstractAction("Undo Move") {
			public void actionPerformed(ActionEvent e) {	
				//Call the undo move procedure
				m.undoMove(CBoard.getPlayer() ^ 1, null); //Undoes the last move
			}
		});
		redoMenuItem = new JMenuItem(new AbstractAction("Redo Move") {
			public void actionPerformed(ActionEvent e) {
				//Call the redo move procedure
				m.redoMove(CBoard.getPlayer() ^ 1); //Redoes the previous move only if one is available
			}
		});
		forfitMenuItem = new JMenuItem(new AbstractAction("Forfeit") {
			public void actionPerformed(ActionEvent e) {
				m.forfitCurrent(); //Forces the end of game
			}
		});
		difficultyMenuItem = new JMenuItem(new AbstractAction("Difficulty") {
			public void actionPerformed(ActionEvent e) {
				int diff = 1;
				Object[] poss = {"Easy", "Medium", "Hard", "Insane"}; //Object for choices in drop-down
				String s = (String)JOptionPane.showInputDialog(null, "Choose the AI difficulty:", "AI Difficulty", JOptionPane.INFORMATION_MESSAGE, null, poss, "Medium");
				if ((s != null) && (s.length() > 0)) { //Shows the pieces in a drop down
					switch (s) {
					case "Easy": diff = 1; break;
					case "Medium": diff = 2; break;
					case "Hard": diff = 3; break;
					case "Insane": diff = 4; break;
					}
				}
				BoardConstants.PVS_DEPTH = diff << 1;
			}
		});
		
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z,
				java.awt.Event.CTRL_MASK)); //Creates a shortcut for undoing a move (CTRL+Z)
		//undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		//		java.awt.event.KeyEvent.VK_Z,
		//		java.awt.Event.SHIFT_MASK)); //Creates a shortcut for undoing a move (CTRL+Z)
		
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Y, 
				java.awt.Event.CTRL_MASK)); //Creates a shortcut for redoing a move (CTRL+Y)
		//redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		//		java.awt.event.KeyEvent.VK_Y, 
		//		java.awt.Event.SHIFT_MASK)); //Creates a shortcut for redoing a move (CTRL+Y)
		
		optionsMenu.add(forfitMenuItem);
		optionsMenu.add(undoMenuItem);
		optionsMenu.add(redoMenuItem);
		optionsMenu.add(difficultyMenuItem);
		
		
		return menuBar; //This is the completed menu bar - this code only executed once at the start of the program
	}
}
