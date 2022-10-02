import processing.core.PApplet;

/**
 * Represents a timer that counts down from the DURATION.
 * @author stefanieim
 *
 */
public class CountdownTimer {
  public static int DURATION;
  private int startTime;  //saved time
  public int currentCountdownTime; //current countdown time
  
  /**
   * Creates a new countdown timer that counts down to zero from the given time limit.
   * @param timeLimit the time (in seconds) to count down from
   */
  public CountdownTimer(int timeLimit) {
    CountdownTimer.DURATION = timeLimit;
    this.startTime = timeLimit;
    this.currentCountdownTime = startTime;
  }

  /**
   * Sets the start time of this timer (the saved timestamp at the point in which the Processing class calls on this method)
   * @param millis the timestamp (in milliseconds) at which the timer should start
   */
  public void setStartTime(int millis) {
    this.startTime = millis;
  }
  
  /**
   * Calculates and updates the current countdown time, based on the duration(time limit),
   * and the given current timestamp from the Processing class.
   * 
   * @param timestamp the given current time stamp from the Processing class.
   */
  public void setCurrentTime(int timestamp) {
    //the current countdown time is the total duration minus the amount of time that has passed since the saved start time
    this.currentCountdownTime = DURATION - (timestamp - this.startTime)/1000;
  }

  /**
   * Determines if the timer has ended (i.e. the current countdown time becomes zero)
   * @return true if the timer has ended.
   */
  public boolean hasEnded() {
    return this.currentCountdownTime <= 0;
  }

}
