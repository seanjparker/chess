package chess.core.online;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import chess.core.bitboards.BoardConstants;
import chess.core.initialize.StartMenu;
import chess.core.utils.Utils;

public class Login extends JFrame {
	private static final long serialVersionUID = -847364886628996059L;
	
	private JTextField textField;
	private JPasswordField passwordField;
	
	private Connection c = null;
	
	public Login() {		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				c = Database.initSQLConnection();
				if (checkConnection(c) == false) {
					start(); //Start is connection null, guest login
				}
			}
		});

		setTitle("Login");
		setBounds(100, 100, 265, 153);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //If closed, terminate the program
		getContentPane().setLayout(null);
		//Auto generated code based on predefined button layout
		textField = new JTextField();
		textField.setBounds(82, 11, 140, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(82, 42, 140, 20);
		getContentPane().add(passwordField);
		
		JLabel lblUsername = new JLabel("Username:"); //Username label and text field
		lblUsername.setBounds(10, 14, 62, 14);
		getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:"); //Username label and text field
		lblPassword.setBounds(10, 45, 62, 14);
		getContentPane().add(lblPassword);
		
		JButton btnGo = new JButton("Login"); //Login button
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login(c);
			}
		});
		btnGo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnGo.setBounds(133, 84, 106, 23);
		getContentPane().add(btnGo);
		
		JButton btnRegister = new JButton("Register"); //Register button
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				register(c);
			}
		});
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnRegister.setBounds(10, 84, 113, 23);
		getContentPane().add(btnRegister);
		
		getRootPane().setDefaultButton(btnGo);
	}
	
	private boolean checkConnection(Connection c) {
		return !(c == null); //If connection not valid, return tryue, otherwise false
	}
	
	private void login(Connection c) {
		if (c != null) {
			if (Database.authLogin(c, textField.getText(), new String(passwordField.getPassword()))) {
				BoardConstants.username = textField.getText(); //Set the username to write to the screen
				start(); //If valid login, start the program
			} else {
				System.err.println("Failed to login"); //If login failed, not start yet
				Utils.simpleDialog("Login Error", "Failed to login", JOptionPane.ERROR_MESSAGE); //Show an error message
			}
		}
	}
	
	private void register(Connection c) {
		//you create a salt with getNextSalt
		//you ask the user his password and use the hash method to generate a salted and hashed password. 
		//The method returns a byte[] which you can save as is in a database with the salt
		if (c != null) {
			if (!Database.executeInsertUser(c, textField.getText(), new String(passwordField.getPassword()))) {
				passwordField.setText("");
				Utils.simpleDialog("Error", "Error with username/password: \n- No whitespaces in username\n- Username length of 4-12 characters\n- Username cannot contain special characters e.g: !@#$%^&*");
			} else {
				passwordField.setText("");
				Utils.simpleDialog("Success", "Success: Registered as new user");
			}
		}
	}
	
	private void start() { 
		this.dispose(); //Terminate the current GUI
		
		StartMenu s = new StartMenu(); //Shows the game mode selection
		s.setVisible(true); //Show the game mode selection menu
	}
	
	public void initLogin() {
		this.setLocationRelativeTo(null); //Sets its location to the centre of the screen
		this.setVisible(true); //Shows the login gui
	}
}
