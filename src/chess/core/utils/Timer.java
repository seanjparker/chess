package chess.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class Timer {
	private final int MAX_TIME = 86400; //Maximum timer time in seconds (24 hours)
	
	private List<Integer> moveTimes = new ArrayList<Integer>(); //Move time for each white move
	private long startTime, endTime, pausedTime;
	private int length; //length of the timer
	private boolean isRunning = false; //flag for when the timer is running
	
	public Timer(int lengthSeconds) { this.length = lengthSeconds; } //initalise the timer
	
	public void start(boolean startPaused) {
		if (!isRunning) {
			if (!startPaused) {			
				this.startTime = System.currentTimeMillis(); //inital start time
				this.endTime = startTime + (this.length * 1000); //end time based on the length
				this.isRunning = true; //set the running flag
			} else { //Start the timer as paused
				this.startTime = System.currentTimeMillis();
				this.pausedTime = System.currentTimeMillis();
				this.endTime = startTime + (this.length * 1000);
				this.isRunning = false;
			}
		} else {
			System.err.println("Timer has already started.");
		}
	}
	
	public void pause() {
		if (this.isRunning) { //If timer is running
			this.pausedTime = System.currentTimeMillis(); //Sets the timer when the timer was paused
			this.isRunning = false; //Sets the running flag
		}
	}
	
	public void flip() {
		if (this.isRunning) { this.pause(); } //Flips the timer state
		else { this.resume(); }
	}
	
	public void resume() {
		if (!this.isRunning) { //If the timer is not running
			this.startTime = System.currentTimeMillis(); //Start the timer
			this.endTime += (this.startTime - this.pausedTime); //Set time when timer will end
			this.isRunning = true; //set the flag for running
		}
	}
	
	public void reset() {
		this.startTime = System.currentTimeMillis(); //Resets the timer to orginal time
		this.endTime = startTime + (this.length * 1000);
		this.isRunning = false;
	}

	public boolean isPaused() {
		return !this.isRunning; //Flips the timer state
	}

	private int getRemaining() {
		if (this.isRunning) { //Gets the remaining time
			int remain = (int) ((this.endTime - System.currentTimeMillis()) / 1000);
			if (remain <= 0) {
				return 0; //If the timer is over, return 0
			} else {
				return remain;
			}
		} else {
			if (this.pausedTime > 0) { //If the timer is paused, return the remaining time
				return (int) (this.endTime - this.pausedTime) / 1000;
			} else {
				return (int) (this.endTime - this.startTime) / 1000;
			}
		}
	}
	
	public boolean isFinished() {
		return (getRemaining() == 0) | false; //Is the timer finished?
	}
	
	public int[] convert() {
		//Seconds, Minutes, Hours...
		int[] time = new int[3];
		int remainingSeconds = getRemaining(); //Get the remaining time for the timer

		if (remainingSeconds > MAX_TIME) { 
			remainingSeconds = MAX_TIME; //If the time is over maximum, reduce to max
		} else {
			int remainingMinutes = remainingSeconds / 60; //Get the minutes remaining
			remainingSeconds -= remainingMinutes * 60;
			
			int remainingHours = remainingMinutes / 60; //Get the remaining hours
			remainingMinutes -= remainingHours * 60;
			
			time[0] = remainingSeconds; //Sets the remaining seconds
			time[1] = remainingMinutes; //Sets the remaining minutes
			time[2] = remainingHours; //Sets the remaining hours
		}
				
		return time;	
	}
	
	public void addTime() {
		int time = (int) (System.currentTimeMillis() - this.startTime);
		moveTimes.add(time); //Adds a time, time for the move
	}
	public int calculateAverage() { //Calculates the average time for the whole game
		OptionalDouble average = moveTimes.stream().mapToDouble(a -> a).average(); //Gets am average in double type
		return (int) Math.floor((average.isPresent() ? average.getAsDouble() : 0) / 1000); //Rounds down time in seconds
	}

}
