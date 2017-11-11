package chess.core.display.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import chess.core.bitboards.BoardConstants;

public class HighscoreStats extends JPanel {
	private final int PREF_W = 400, PREF_H = 400; //Height and width of the GUI
	private final int xOFFSET = PREF_W / 4; //Offset to provide a border
	private final int yOFFSET = 20; //Offset to provide a border
	private String[] leaderboard = null, names = null; //Data from the database
	
	public void createAndShowGUI(String[] leaderboard, String[] names) {
		setStats(leaderboard, names); //Gets and sets the data from the database
		
		JFrame f = new JFrame("Highscores"); //Creates the JFrame and sets the title
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Sets the close operation
		f.setSize(getPreferredSize()); //Sets the size
		f.getContentPane().add(this);
		f.setLocationRelativeTo(null);
		f.setResizable(false); //Prevents resizing
		f.setVisible(true); //Makes the GUI visible
	}
	
	private void setStats(String[] leaderboard, String[] names) {
		this.leaderboard = leaderboard; //Sets the data from the database
		this.names = names;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; //Creates a Graphics2D object which is more powerful
		g2.setFont(new Font("TimesRoman", Font.BOLD, 24));
		FontMetrics current = g2.getFontMetrics(); //Gets the current font settings
		
		if (leaderboard != null && names != null) { //Checks to see if data is valid
			for (int i = 0; i < leaderboard.length; i++) { //Draws all the data from the database
				g2.setColor(Color.BLACK);
				if (names[i].equals(BoardConstants.username)) { g2.setColor(Color.RED); }
				g2.drawString((i + 1) + ". " + names[i] + ": " + leaderboard[i], xOFFSET, yOFFSET + (current.getHeight() * i)); //Draws the score and name
			}
		}
	}
	public Dimension getPreferredSize() { return new Dimension(PREF_W, PREF_H); } //Gets the size of the GUI
}
