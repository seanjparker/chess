package chess.core.display.window;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class UserStats extends JPanel {
	private final int PREF_W = 850; //Height of the GUI
	private final int PREF_H = 650;	//Width of the GUI
	private final int GRAPH_W = 600; //Height of the graph
	private final int GRAPH_H = 525; //Width of the graph
	
	
	private final int MAX_SCORE = 120; //Max of 2 min per move, anything above 2 min is reduced to max
	private final int BORDER_GAP = 30; //Offset to provide a border
	private final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180); //Colour of each graph point
	private final Stroke GRAPH_STROKE = new BasicStroke(3f); //Thickness of each point
	private final int GRAPH_POINT_WIDTH = 5; //This shows the last 5 games
	private final int Y_HATCH = 10;	//Width of each hatch mark
	private final int INFO_W = GRAPH_W + BORDER_GAP; //Position of user information on the GUI
	private final int INFO_H = BORDER_GAP;
	
	private String win, loss, draw, wlper; //Data about user from database
	private int[] prev; //Previous games average times
	
	
	public void createAndShowGUI(String[] stats, int[] prevGames) {
		setStats(stats, prevGames); //Gets the statistics from the database about the user
		
		JFrame f = new JFrame("User Statistics"); //Creates the GUI and sets the title
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Sets the close operation
		f.setSize(getPreferredSize()); //Sets the size of the GUI
		f.getContentPane().add(this); //Adds the container to the JPanel
		f.setLocationRelativeTo(null); //Sets the GUI location to the centre of the screen
		f.setResizable(false); //Disables screen resizing
		f.setVisible(true); //The user can view the GUI
	}
	
	private void setStats(String[] stats, int[] prevGames) {
		if (stats != null) {
			this.win = stats[0]; //Sets the data accordingly
			this.loss = stats[1];
			this.draw = stats[2];
			this.wlper = stats[3];
			this.prev = prevGames;			
		}
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; //Graphics2D is more powerful than Graphics
		
		if (this.prev != null) { //Draw Graph
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Provides smooth edges
			double xScale = ((double) GRAPH_W - 2 * BORDER_GAP) / (prev.length - 1); //x Scale based on data avaliable
			double yScale = ((double) GRAPH_H - 2 * BORDER_GAP) / (MAX_SCORE - 1);//y scale
			
			List<Point> graphPoints = new ArrayList<Point>();
			for (int i = 0; i < prev.length; i++) {
				int x1 = (int) (i * xScale + BORDER_GAP);
				int y1 = (int) ((MAX_SCORE - getScoreFromTime(prev[i])) * yScale + BORDER_GAP);
				graphPoints.add(new Point(x1, y1)); //Create a new point in the graph bounds based on magnitude of data
			}
			
			//Draw white background
			g2.setColor(Color.WHITE);
			g2.fillRect(BORDER_GAP, BORDER_GAP, GRAPH_W - 2 * BORDER_GAP, GRAPH_H - 2 * BORDER_GAP);
			g2.setColor(Color.BLACK);
			
			//Create x and y axes 
			g2.drawLine(BORDER_GAP, GRAPH_H - BORDER_GAP, BORDER_GAP, BORDER_GAP);
			g2.drawLine(BORDER_GAP, GRAPH_H - BORDER_GAP, GRAPH_W - BORDER_GAP, GRAPH_H - BORDER_GAP);
			
			//Create hatch marks for y axis. 
			for (int i = 0; i < Y_HATCH; i++) {
				int x0 = BORDER_GAP;
				int y0 = GRAPH_H - (((i + 1) * (GRAPH_H - BORDER_GAP * 2)) / Y_HATCH + BORDER_GAP);
				g2.drawLine(x0, y0, x0 + GRAPH_POINT_WIDTH, y0);
				
				//Set hatch labels
				FontMetrics current = g2.getFontMetrics();
				String text = Integer.toString((MAX_SCORE / Y_HATCH) * (i + 1));
				g2.drawString(text, x0 - current.stringWidth(text), y0);
			}
			
			//x axis
			for (int i = 0; i < prev.length - 1; i++) {
				int x0 = (i + 1) * (GRAPH_W - BORDER_GAP * 2) / (prev.length - 1) + BORDER_GAP;
				int y0 = GRAPH_H - BORDER_GAP;
				g2.drawLine(x0, y0, x0, y0 - GRAPH_POINT_WIDTH);
				
				//Set hatch labels
				FontMetrics current = g2.getFontMetrics();
				String text = Integer.toString(i + 2);
				g2.drawString(text, x0 - (current.stringWidth(text) / 2), y0 - current.getHeight());
			}
			Stroke oldStroke = g2.getStroke();
			g2.setColor(Color.GREEN);
			g2.setStroke(GRAPH_STROKE);
			//Draws lines between the points
			for (int i = 0; i < graphPoints.size() - 1; i++) {
				Point c1 = graphPoints.get(i);
				Point c2 = graphPoints.get(i + 1);
				g2.drawLine(c1.x, c1.y, c2.x, c2.y);         
			}
			
			g2.setStroke(oldStroke);      
			g2.setColor(GRAPH_POINT_COLOR);
			//Draws all the points on to the graph
			for (int i = 0; i < graphPoints.size(); i++) {
				int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
				int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
				g2.fillOval(x, y, GRAPH_POINT_WIDTH, GRAPH_POINT_WIDTH);
			}
		}
		
		//Draw info
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("TimesRoman", Font.BOLD, 32));
		g2.drawString("Wins: " + win, INFO_W, INFO_H + 60);
		g2.drawString("Losses: " + loss, INFO_W, INFO_H + 120);
		g2.drawString("Draws: " + draw, INFO_W, INFO_H + 180);
		g2.drawString("W/L: " + wlper + "%", INFO_W, INFO_H + 240);
		
	}
	//If the time is too big, reduce to maximum
	private int getScoreFromTime(int time) { return Math.min(time, MAX_SCORE); }
	//This is an overriden function, gets the defined size of the GUI
	public Dimension getPreferredSize() { return new Dimension(PREF_W, PREF_H); }
}
