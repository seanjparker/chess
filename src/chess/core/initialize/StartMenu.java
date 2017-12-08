package chess.core.initialize;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import chess.core.bitboards.BoardConstants;

@SuppressWarnings("serial")
public class StartMenu extends JFrame {
	private JPanel contentPane;
	private JButton btnOnePl, btnTwoPl, btnTimedGame;
	
	public StartMenu() {
		setTitle("Ϲʜеςς | Menu"); //Sets the menu title
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //If closed, exit the program

		setLocationRelativeTo(null); //Sets the location to the centre of the screen
		
		contentPane = new JPanel();
		setContentPane(contentPane);
		getContentPane().setLayout(new FlowLayout());
		
		btnOnePl = new JButton("One Player"); //Adds a new button
		btnOnePl.addActionListener(e -> {
          init(0);
          //Main.moveTimer.start(false);
		});
	    getContentPane().add(btnOnePl);
		
		btnTwoPl = new JButton("Two Player"); //Adds a new button
		btnTwoPl.addActionListener(e -> {
          init(1);
          //Main.moveTimer.start(false);
        });
        getContentPane().add(btnTwoPl);
		
		btnTimedGame = new JButton("Timed Game"); //Adds a new button
		btnTimedGame.addActionListener(e -> {
          init(2);
          //Main.t1.start(false);
          //Main.t2.start(true);
        });
		getContentPane().add(btnTimedGame);
		
		pack();
		//getRootPane().setDefaultButton(btnTwoPl); //Sets the default selected button
	}
	
	private void init(int gm) {
		BoardConstants.gameMode = gm; //Sets which game mode was selected
		dispose();
	}
}
