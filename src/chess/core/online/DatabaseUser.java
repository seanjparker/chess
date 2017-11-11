package chess.core.online;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUser {
	public Connection c = null;
	
	public DatabaseUser() {
		c = Database.initSQLConnection(); //Opens the SQL connection
	}
	
	public String[] getUserStats(String username) {
		String[] userStats = {"0", "0", "0", "0"};
		if (this.c != null) { //Only perform if connection is valid
			String userId = Database.getUserID(this.c, username); //Gets user id based on username
			if (userId != null) {
				String query = "SELECT wins, losses, draws, wlper FROM scores, users WHERE (`chess`.`scores`.`user_id` = `chess`.`users`.`user_id`) AND (`scores`.`user_id` = ?)";
				try { //Attempts to get the user stats from the table result set
					PreparedStatement pre = c.prepareStatement(query); //Initalise the query
					pre.setString(1, userId);
					ResultSet res = pre.executeQuery(); //Performs the query					
					while (res.next()) {
						userStats[0] = res.getString("wins"); //Gets the data from the result set
						userStats[1] = res.getString("losses"); //Gets the data from the result set
						userStats[2] = res.getString("draws"); //Gets the data from the result set
						userStats[3] = res.getString("wlper"); //Gets the data from the result set
					}
					res.close();
					pre.close();
				} catch (SQLException e) { //Catches any SQL exception that may occur
					e.printStackTrace();
				}
			}
		}
		return userStats; //Returns the user stats
	}
	
	public int[] getPrevGames(String username) {
		String prevGames = "";
		if (c != null) { //Only access database if the connection is valid
			String userId = Database.getUserID(this.c, username); //Gets the user id accoring to the username
			if (userId != null) {
				String query = "SELECT prevgames FROM scores, users WHERE (`chess`.`scores`.`user_id` = `chess`.`users`.`user_id`) AND (`scores`.`user_id` = ?)";
				try { //Attempts execute the query
					PreparedStatement pre = c.prepareStatement(query);
					pre.setString(1, userId);
					ResultSet res = pre.executeQuery();
					//Executes the query and get the past games from the result set
					while (res.next()) { prevGames = res.getString("prevgames"); }
					
					res.close();
					pre.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return getTimes(prevGames); //Convert the data to integer array
	}
			
	private int[] getTimes(String prevMoves) {
		final int TGAMES = prevMoves.length() - prevMoves.replace("#", "").length(); //Determines the current number of games
		final String BREAK = "#"; //Breakpoint identifier
		int[] times = new int[TGAMES]; //Creates a new array to store the game times
		int c = 0, k = 0; //Temp variables for counters
		if (!prevMoves.isEmpty()) {
			for (int i = 0; i < TGAMES; i++) { //Loops for number of games to set	
				k = prevMoves.indexOf(BREAK, c); //Gets the next position of the breakpoint
				times[i] = Integer.parseInt(prevMoves.substring(c, k)); //Converts the next time of the substring to integer
				c += Integer.toString(times[i]).length() + 1; //Increments the string position tracker based on time length
			}
			return times; //Returns the times as integers
		}
		return null;	
	}
	public void setUserStats(int wins, int losses, int draws, String averageTime, String user) {
		if (this.c != null) {
			boolean newUserStats = false;
			String[] current = getUserStats(user); //Gets the current user stats
			int[] prevMoves = getPrevGames(user); //Gets the previous games from the database
			String prevGames = averageTime + "#";
			if (prevMoves != null) {
				int limit = 0;
				if (prevMoves.length == 5) { limit = 1; } //Remove the last time if max reached
				for (int i = 0; i < prevMoves.length - limit; i++) {
					prevGames += "" + prevMoves[i] + "#"; //Create a new string of the five times
				}					
			}
			int newWins = Integer.parseInt(current[0]) + wins; //Sets the new wins
			int newLosses = Integer.parseInt(current[1]) + losses; //Sets the new losses
			int newDraws = Integer.parseInt(current[2]) + draws; //Sets the new draws
			int wlper =  (int) Math.floor((newWins * 100) / (newWins + newLosses + newDraws)); //Calculates the win percentage
			
			String userId = Database.getUserID(this.c, user);
			if ((newWins - wins) + (newLosses - losses) + (newDraws - draws) > 0) {
				String query = "UPDATE `chess`.`scores` SET `WINS`=?, `LOSSES`=?, `DRAWS`=?, `WLPER`=?, `PREVGAMES`=? WHERE `USER_ID`=?";
				try { //Creates the query
					PreparedStatement pre = this.c.prepareStatement(query);
					pre.setString(1, Integer.toString(newWins));
					pre.setString(2, Integer.toString(newLosses));
					pre.setString(3, Integer.toString(newDraws));
					pre.setString(4, Integer.toString(wlper));
					pre.setString(5, prevGames);
					pre.setString(6, userId);
					
					pre.executeUpdate(); //Executes the updates, pushing to te database
					pre.close();
				} catch (SQLException e) { //Catches any exception that may occur
					e.printStackTrace();
				}
			} else {
				newUserStats = true;
				String query = "INSERT INTO `chess`.`scores` (`USER_ID`, `USER`, `WINS`, `LOSSES`, `DRAWS`,`WLPER`, `PREVGAMES`) VALUES (?,?,?,?,?,?,?)";
				try { //Creates the query
					PreparedStatement pre = this.c.prepareStatement(query);
					pre.setString(1, userId);
					pre.setString(2, user);
					pre.setString(3, Integer.toString(newWins));
					pre.setString(4, Integer.toString(newLosses));
					pre.setString(5, Integer.toString(newDraws));
					pre.setString(6, Integer.toString(wlper));
					pre.setString(7, prevGames);
					
					pre.executeUpdate(); //Executes the updates, pushing to the database
					pre.close();
				} catch (SQLException e) { //Catches any exception that may occur
					e.printStackTrace();
				}
			}
			
			int finalScore = (int) Math.floor(wlper * Integer.parseInt(averageTime)); //Takes into account w/l and time to calculate a score
			setHighscores(finalScore, user, newUserStats); //Pushes score to the database
		}
	}
	
	private void setHighscores(int newScore, String user, boolean newUserStats) {
		if (this.c != null) {
			String userID = Database.getUserID(this.c, user); //Gets the user id to identify the user
			if (newUserStats) {
				String q3 = "INSERT INTO `chess`.`highscores` (`USER_ID`, `NAME`, `SCORE`) VALUES (?, ?, ?)";
				try { //Query to insert a new score to the database
					PreparedStatement pre = this.c.prepareStatement(q3);
					pre.setString(1, userID);
					pre.setString(2, user);
					pre.setInt(3, newScore);
					pre.executeUpdate(); //Executes the update to enter a new score
				} catch (SQLException e) { //Catches any exception that may occur
					e.printStackTrace();
				}				
			} else {
				String q1 = "SELECT score FROM `chess`.`highscores` WHERE (`USER_ID` = ?)";
				int score = -1;
				try {
					PreparedStatement pre = this.c.prepareStatement(q1); //Creates the statement
					pre.setString(1, userID);				
					ResultSet res = pre.executeQuery();	//Executes the update			
					if (res.next()) { score = res.getInt("score"); } //Gets the current score from the table
					res.close();
					pre.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (score < newScore) { 
					String q2 = "UPDATE `chess`.highscores` SET `SCORE` = ? WHERE (`USER_ID` = ?)";
					try { //Query to update the score, new score pushed to the table
						PreparedStatement pre = this.c.prepareStatement(q2);
						pre.setInt(1, newScore); //Sets the score in the query
						pre.setString(2, userID); //Sets the user id in the query
						pre.executeUpdate(); //Executes the update
					} catch (SQLException e) { //Catches any exceptions that may occur
						e.printStackTrace();
					}					
				}
			}
		}
	}
	
	public String[] getHighscore(String user, String colName) {
		String[] results = null;
		int size = -1;
		if (this.c != null) {
			String q0 = "SELECT COUNT(SCORE) AS count FROM `chess`.`highscores`"; //Gets a count of the scores in the table
			try {
				PreparedStatement pre = this.c.prepareStatement(q0); //Creates the query for the result set
				ResultSet res = pre.executeQuery();
				if (res.next()) { size = Math.min(res.getInt("count"), 10); }  //Set the size, maximum size is 10
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String q1 = "SELECT " + colName + " FROM `chess`.`highscores` ORDER BY CAST(score AS SIGNED) DESC LIMIT 10";
			try { //Gets the scores/usernames from the table, orders them by the score from highest to lowest, maximum of 10 results
				PreparedStatement pre = this.c.prepareStatement(q1);
				ResultSet res = pre.executeQuery();
				results = new String[size];
				for (int i = 1; i <= size; i++) { //Loops through the result set with a limit of max size
					if (res.next()) {
						results[i - 1] = res.getString(1); //Gets the scores from the result set
					}					
				}
				res.close();
				pre.close();
			} catch (SQLException e) { //Catches any sql exceptions that occurs during data fetch
				e.printStackTrace();
			}
		}
		return results; //Returns all the highscores from the database
	}
}
