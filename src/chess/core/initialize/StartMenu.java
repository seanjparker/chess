package chess.core.initialize;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import chess.core.bitboards.BoardConstants;

@SuppressWarnings("serial")
public class StartMenu extends JFrame {
	private JPanel contentPane;
	public StartMenu() {
		setTitle("Chess - Menu"); //Sets the menu title
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //If closed, exit the program
		setBounds(100, 100, 450, 342);

		setLocationRelativeTo(null); //Sets the location to the centre of the screen
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnOnePl = new JButton("One Player"); //Adds a new button
		btnOnePl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				init(0); //Sets the game mode to one player
				Main.moveTimer.start(false);
			} //Event handler for the button when pressed, this listener is activated
		});

		btnOnePl.setBounds(10, 11, 200, 140);
		contentPane.add(btnOnePl);
		
		JButton btnTwoPl = new JButton("Two Player"); //Adds a new button
		btnTwoPl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				init(1); //Sets the game mode to two player
				Main.moveTimer.start(false);
			} //This event is triggered when the button is pressed
		});

		btnTwoPl.setBounds(234, 11, 200, 140);
		contentPane.add(btnTwoPl);
		
		JButton btnTimedGame = new JButton("Timed Game"); //Adds a new button
		btnTimedGame.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				init(3); //If this button is pressed, the game mode is set accordingly
				Main.t1.start(false); //One of the timers is started
				Main.t2.start(true);
			}
		});
		btnTimedGame.setBounds(123, 162, 200, 140);
		contentPane.add(btnTimedGame);
		
		getRootPane().setDefaultButton(btnTwoPl); //Sets the default selected button
	}
	
	private void init(int gm) {
		BoardConstants.gameMode = gm; //Sets which game mode was selected
		dispose(); //Destroys the current GUI
	}
}
