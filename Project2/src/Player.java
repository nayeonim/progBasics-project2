
/**
 * This class represents a player in a game.
 * A player has a integer key (zero-based indexing), a name,
 * a score, and a countdown timer.
 * @author stefanieim
 */
public class Player {
  public final static int TIMELIMIT = 3; //in seconds
  
  private final int key; //zero-based indexing
  private String name;
  private int score;
  public final CountdownTimer timer; //each player has its own timer
  

  /**
   * Constructs a new player with the given key, a null name, 
   * a score of 0, and a countdown timer set to the TIMELIMIT constant
   * @param key the given integer to set this player's key to
   */
  public Player(int key) {
    this.key = key;
    this.name = null;
    this.score = 0;
    this.timer = new CountdownTimer(TIMELIMIT);
  }
  
  /**
   * Return this player's name
   * @return the String value of the player's name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Set this player's name as the given string value.
   * @param str the given string to set this player's name to
   */
  public void setName(String str) {
    this.name = str;
  }
  
  /**
   * Returns the key attached to this player.
   * @return the integer key of this player
   */
  public int getKey() {
    return this.key;
  }
  
  /**
   * Increase this player's score by one.
   */
  public void scoreUp() {
    this.score++;
  }

  /**
   * Set this player's score as the given score.
   * @param score the given score to set this player's score to
   */
  public void setScore(int score) {
    this.score = score;
  }
  
  /**
   * Get this player's current score.
   * @return this player's current score.
   */
  public int getScore() {
    return this.score;
  }
}
