import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Model part of the game (MVC), which keeps track of the data necessary for game play.
 * @author stefanieim
 *
 */
public class GameModel {
  private final List<Player> players;
  private GameState state;
  private int turn; //index of the player with the current turn
  
  
  /**
   * Creates a new model for a game with default values:
   * - an empty list of players,
   * - the state as the introduction state (very first)
   * - and the turn as the 0th (first) player.
   */
  public GameModel() {
    this.players = new ArrayList<Player>();
    this.state = GameState.INTRO;
    this.turn = 0;
  }
  
  /**
   * Adds the given number of players to this model's list of players 
   * @param howMany the number of players to add
   */
  public void addPlayers(int howMany) {
    for (int i = 0; i < howMany; i++) {
      this.players.add(new Player(i));
    }
  }

  /**
   * Returns the list of players in this game model.
   * @return the list of players in this game model.
   */
  public List<Player> getPlayersList() {
    return this.players;
  }

  /**
   * Returns the player with the index equivalent to the given key.
   * @param key the key of the player to get
   * @return the play with the given key
   */
  public Player getPlayer(int key) {
    return this.players.get(key);
  }

  /**
   * Returns the current state of this game.
   * @return the current state of this game as a GameState enum type.
   */
  public GameState getGameState() {
    return this.state;
  }

  /**
   * Sets this game's state as the given GameState enum value.
   * @param s the given GameState to set this game's state to 
   */
  public void setGameState(GameState s) {
    this.state = s;
  }

  /**
   * Returns the index of the player whose turn it is.
   * @return
   */
  public int getTurn() {
    return turn;
  }

  /**
   * Moves onto the next player.
   */
  public void nextTurn() {
    this.turn++;
  }
  
  /**
   * Return the winner among the players of this game. 
   * If it is a draw, return a player with -1 as its key.
   * @return the player with the highest score.
   */
  public Player determineWinner() {
    Player highestScore = getPlayer(0);
    
    //compare all other players with the current highest score player
    for (int i = 1; i < this.players.size() ; i++) {
      int compareValue = Integer.compare(highestScore.getScore(), getPlayer(i).getScore());
      /* returns 0 if same
       *         - if y is greater
       *         + if x is greater
       */
      if (compareValue > 0) { //original highestScore player still has highest score
        //do nothing
      } 
      else if (compareValue < 0) { //new player being compared has higher score than original highestScore player
        highestScore = getPlayer(i);
      }
      else { //draw (same score, no winner)
        highestScore = new Player(-1);
        highestScore.setName("none");
        highestScore.setScore(getPlayer(i).getScore());
      }
    }
    return highestScore;
  }
}
