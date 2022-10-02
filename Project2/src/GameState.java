
/**
 * Represents the different states of a game:
 * - INTRO : choosing player mode(single vs. dual) + setting player names
 * - PLAYING_STALE: waiting before a player starts their round
 * - PLAYING_ACTIVE: the actual game play
 * - FINAL: final screen with winning/draw/score message.
 * 
 * @author stefanieim
 */
public enum GameState {
  INTRO,           //choosing player mode(single vs. dual) + setting player names
  PLAYING_STALE,   //waiting before a player starts their round
  PLAYING_ACTIVE,  //the actual game play
  FINAL;           //final screen with winning/draw/score message
}

