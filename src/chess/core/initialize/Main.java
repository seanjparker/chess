package chess.core.initialize;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import chess.core.bitboards.BoardConstants;
import chess.core.display.GUI;
import chess.core.display.MenuBar;
import chess.core.display.input.MouseHandler;
import chess.core.online.Login;
import chess.core.utils.Timer;

public class Main extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private static final double UFPS = 60.0; //Update rate of the game
	
	private Thread gameThread; //Main game thread
	private JFrame frame; //Main game frame
	private GUI ui; //ui object to acccess drawing methods
	
	private static MouseHandler m;
	private static MenuBar mb;
	
	public static Timer t1 = new Timer(600); //White timer, length = 10m
	public static Timer t2 = new Timer(600); //Black timer, length = 10m 
	public static Timer moveTimer = new Timer(1); //Move timer for whites average move calculation
	
	private boolean running = false; //True when the game is running
	
	public Main() {
		ui = new GUI(1000, 600); //ui size
		frame = new JFrame();
		mb = new MenuBar(); //creates the menu bar
		m = new MouseHandler(); //enables mouse input
	}
	
	private synchronized void start() {
		running = true; //Sets the game state to running
		gameThread = new Thread(this, "Display"); //Creates the game thread
		gameThread.start(); //Starts the game thread for rendering
		
		init();
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		
		final double ns = 1000000000.0 / UFPS; //Amount of time to wait for 60 updates per second
		double delta = 0;
		int frames = 0, updates = 0; //counts the number of updates currently

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) { //When an update is required, occurs x60 /s
				update(); //Get mouse imputs
				updates++;
				delta--;
			
				frame.repaint(); //Render to the screen
				frames++;
			}
			
			if ((System.currentTimeMillis() - timer) > 1000) {
				timer += 1000;
				frame.setTitle("Chess Engine" + " - " + updates + " UPS, " + frames + " FPS");
				//Updates the title to show current fps and updates
				updates = 0; //Resets fps counter
				frames = 0;
			}
		}
	}
	
	private void update() {
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
	}
	
	public void paintComponent(Graphics g) { 		
		ui.drawBorders(g); //Draws the borders
		ui.drawBoard(g); //Draws the actual squares for the board
		ui.drawPieces(g); //Draws the pieces on the board		
		ui.drawCaptured(g); //Draws the captured pieces on the side of the board
		ui.drawPossible(g); //Only draws when right click - draw the possible pieces
		ui.drawAlgebraic(g); //Writes the algebraic move notation to the screen
		ui.drawUser(g); //Writes the username to the screen
		ui.drawPieceAnimation(g); //Draws the selected piece on the screen at the current mouse location
		ui.drawTimer(g, t1, t2); //If the user is playing a timed game, draw the timer for both players
	}
	
	private void init() {
		frame.setTitle("Chess Engine"); //Sets the title
		frame.setSize(1000, 600); //Sets the size
		frame.setLocationRelativeTo(null); // Sets the position to the middle of the screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //When close pressed, terminate the process
		frame.add(this); //Add panel to the main frame
		frame.setResizable(false); //Disable resizing, prevent graphical errors
		frame.addMouseListener(m); //Enables mouse inputs to the ui
		frame.addMouseMotionListener(m); //Enables dragging the mouse inputs
		frame.setJMenuBar(mb.initMenuBar()); //Adds the menu bar to the ui
		frame.setVisible(true); //Shows the ui
		
		BoardConstants.squareSize = (int)(Math.min(getHeight() - 17, getWidth() + 20)) / 8;
		BoardConstants.WIDTH_F = getWidth();
		BoardConstants.HEIGHT_F = getHeight();
		
		ui.initGUI(); //Initalize the GUI
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		
		main.start(); //Starts the main game thread
		Login lg = new Login(); //Initalize the login
		lg.initLogin(); //Shows the login screen in front of the game ui
	}
}
