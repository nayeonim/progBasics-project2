import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PShape;

/**
 * This class acts as the View and Controller for the SpaceChallenge game.
 * It draws the canvas at every frame, as well as update variables and game states by listening to mouse clicks and keyboard inputs.
 * It contains a model of the game, and it refers to this in order to set and get data about the game.
 * 
 * @author stefanieim
 *
 */
public class Processing extends PApplet {
  public final static int CANVAS_X = 960;
  public final static int CANVAS_Y = 540;
  public final static int CANVAS_VERTICAL_INC = (CANVAS_Y/7);
  public final static int BTN_WIDTH = CANVAS_X/4;
  public final static int BTN_HEIGHT = CANVAS_VERTICAL_INC;
  public final static int NAMEBOX_WIDTH = BTN_WIDTH;
  public final static int NAMEBOX_HEIGHT = BTN_HEIGHT - (CANVAS_Y/27);
  public final static int TEXTSIZE_BTN = 24;
  public final static int TEXTSIZE_TITLE = 36;
  public final static int TEXTSIZE_SUBTITLE = 16;
  public final static int TEXTSIZE_CAPTION = 12;

  public GameModel model; //the game model (stores game data)

  private Integer numOfPlayers; //the number of players in this game

  //variables containing data for players, as an ordered list or map.
  private Map<Integer, String> nameBoxes; 
  private List<List<PShape>> listOfPlayersCircles; //a list of the lists of all players' circles
  private List<List<Integer[]>> listOfColors;  //a list of the lists of colors(rgb array of integers) for the circles for each player.

  //indicators for pop-up messages.
  public boolean timesUpMsgShowing;
  public boolean resetMsgShowing;

  //all buttons
  private PShape btnSingle, btnDual, btnConfirmName, btnStart, btnNext, btnReset, btnResetConfirm, btnResetCancel;



  //----------------------------------------------------------------------------
  //SETUP-----------------------------------------------------------------------
  //----------------------------------------------------------------------------
  /**
   * This function will set up the initial canvas by running once when the program starts.
   */
  public void settings() {
    size(CANVAS_X, CANVAS_Y); // Set the canvas width and height
  }

  /**
   * This function will set up all variables by running once when the program starts.
   */
  public void setup() {
    this.model = new GameModel();
    this.numOfPlayers = null;

    this.nameBoxes = new HashMap<>();

    this.listOfColors = new ArrayList<List<Integer[]>>();
    this.listOfPlayersCircles = new ArrayList<List<PShape>>();

    this.timesUpMsgShowing = false;
    this.resetMsgShowing = false;

    this.btnSingle = createShape();
    this.btnDual = createShape();
    this.btnConfirmName = createShape();
    this.btnStart = createShape();
    this.btnNext = createShape();
    this.btnReset = createShape();
    this.btnResetConfirm = createShape();
    this.btnResetCancel = createShape();
  }



  //----------------------------------------------------------------------------
  //DRAW------------------------------------------------------------------------
  //----------------------------------------------------------------------------

  /**
   * This method is executed repeatedly to draw onto the canvas at every frame.
   */
  public void draw() {
    background(38, 0, 75);

    switch (model.getGameState()) {
      case INTRO:
        //player mode not chosen yet
        if (numOfPlayers == null) { 
          drawButton(this.btnSingle, CANVAS_X/5, CANVAS_VERTICAL_INC*3, 250, 0, 255, 253, 135, 255, "SINGLE PLAYER", 255, 255, 255);
          drawButton(this.btnDual, (CANVAS_X - CANVAS_X/5 - BTN_WIDTH), (CANVAS_VERTICAL_INC*3), 0, 224, 255, 157, 243, 255, "DUAL PLAYER", 38, 0, 75);
          fill(255);
          stroke(255);
          textAlign(CENTER, TOP);
          textSize(TEXTSIZE_SUBTITLE);
          text("Choose Game-play Mode", CANVAS_X/2, CANVAS_VERTICAL_INC*2);
        }
        //player mode is chosen (so now the user needs to type the names of players);
        else { 
          background(38, 0, 75);
          fill(255);
          stroke(255);
          textAlign(CENTER, TOP);
          textSize(TEXTSIZE_SUBTITLE);
          text("Enter player name (max 10 characters)", CANVAS_X/2, CANVAS_VERTICAL_INC*2);

          for (int i = 0; i < numOfPlayers; i++) {
            drawNameBox(i); //draw name boxes for number of players
          }

          //if all name boxes have a string value in it, enable the CONFIRM button
          if (checkAllNameBoxesFilled()) {
            drawButton(this.btnConfirmName, (CANVAS_X/2 - BTN_WIDTH/2), (CANVAS_VERTICAL_INC*5), 255, 153, 0, 255, 207, 135, "CONFIRM", 255, 255, 255);
          }
          //if an empty name box remains, disable the CONFIRM button
          else {
            drawButton(this.btnConfirmName, (CANVAS_X/2 - BTN_WIDTH/2), (CANVAS_VERTICAL_INC*5), 153, 153, 153, 153, 153, 153, "CONFIRM (disabled)", 116, 116, 116);
          }
        }
        break;

      case PLAYING_STALE:
        background(38, 0, 75);
        drawStartPopUp(); //draw the pop up message that contains whose turn it is and the instructions.
        drawButton(this.btnStart, (CANVAS_X/2) - (BTN_WIDTH/2), (int)(CANVAS_VERTICAL_INC*3.8), 80, 2, 106, 133, 40, 165, "START", 255, 255, 255);
        break;

      case PLAYING_ACTIVE:
        background(38, 0, 75);
        Player currentPlayer = this.model.getPlayer(model.getTurn());
        //timer
        if (currentPlayer.timer.currentCountdownTime > 0) {  
          currentPlayer.timer.setCurrentTime(millis());
        }
        //when timer has ended
        if (currentPlayer.timer.hasEnded()){ //time has ended
          timesUpMsgShowing = true; //show time's up message pop up
        }
        //draw all the circles created with user's spacebar keyboard input
        drawListOfCircles(); 
        //draw time's up message and the NEXT button
        if (timesUpMsgShowing) {
          drawTimesUpMsg();
          drawButton(this.btnNext, (CANVAS_X/2) - (BTN_WIDTH/2), (int)(CANVAS_VERTICAL_INC*3.8), 80, 2, 106, 133, 40, 165, "NEXT", 255, 255, 255);
        }
        break;

      case FINAL:
        background(38, 0, 75);
        drawFinalMessage();
        break;

      default:
        throw new IllegalStateException();
    }

    //header always drawn at the end so that nothing covers it
    drawHeader(model.getGameState()); 

    //reset button for all states (except for very first screen)
    drawButton(this.btnReset, CANVAS_X - BTN_WIDTH/2 - CANVAS_X/32, CANVAS_Y - BTN_HEIGHT/2 - CANVAS_X/32, 255, 59, 59, 255, 115, 115, "RESET", 255, 255, 255);

    if (resetMsgShowing) {
      drawResetMessagePopUp(); //draw reset message (i.e. "are you sure?" message) pop-up
    }
  }

  //DRAW helpers------------------------------------  

  /**
   * Draws a button onto the canvas.
   * @param s the PShape object of this button
   * @param topLeftX the x position of the top left corner that this button will be drawn on.
   * @param topLeftY the y position of the top left corner that this button will be drawn on.
   * @param r the r value for this button's color (rgb)
   * @param g the g value for this button's color (rgb)
   * @param b the b value for this button's color (rgb)
   * @param hoverR the r value for this button's hover state color (rgb)
   * @param hoverG the g value for this button's hover state color (rgb)
   * @param hoverB the b value for this button's hover state color (rgb)
   * @param text the text to be displayed for this button
   * @param textR the r value for this button's text color (rgb)
   * @param textG the g value for this button's text color (rgb)
   * @param textB the b value for this button's text color (rgb)
   */
  private void drawButton(PShape s, int topLeftX, int topLeftY, 
      int r, int g, int b, int hoverR, int hoverG, int hoverB, 
      String text, int textR, int textG, int textB) {

    //hover
    if ((topLeftX <= mouseX && mouseX <= topLeftX + BTN_WIDTH)
        && (topLeftY <= mouseY && mouseY <= topLeftY + BTN_HEIGHT)) {
      s.beginShape();
      s.fill(color(hoverR,hoverG,hoverB));
      s.stroke(color(hoverR,hoverG,hoverB));
      s.vertex(topLeftX, topLeftY);
      s.vertex(topLeftX+BTN_WIDTH, topLeftY);
      s.vertex(topLeftX+BTN_WIDTH, topLeftY+BTN_HEIGHT);
      s.vertex(topLeftX, topLeftY+BTN_HEIGHT);
      s.vertex(topLeftX, topLeftY);
      s.endShape();
    }
    //normal
    else {
      s.beginShape();
      s.fill(color(r,g,b));
      s.stroke(color(r,g,b));
      s.vertex(topLeftX, topLeftY);
      s.vertex(topLeftX+BTN_WIDTH, topLeftY);
      s.vertex(topLeftX+BTN_WIDTH, topLeftY+BTN_HEIGHT);
      s.vertex(topLeftX, topLeftY+BTN_HEIGHT);
      s.vertex(topLeftX, topLeftY);
      s.endShape();
    }
    shape(s);

    //text
    fill(color(textR, textG, textB));
    textAlign(CENTER, CENTER);
    textSize(TEXTSIZE_BTN);
    if (s.equals(this.btnReset)) {
      text(text, topLeftX + (BTN_WIDTH/3), topLeftY + (int)(BTN_HEIGHT/2.5));
    }
    else {
      text(text, topLeftX + (BTN_WIDTH/2), topLeftY + (BTN_HEIGHT/2));
    }
    //hide reset button for first screen
    if (s.equals(this.btnReset) && numOfPlayers == null) {
      s.beginShape();
      s.fill(color(38, 0, 75));
      s.stroke(color(38, 0, 75));
      s.vertex(topLeftX, topLeftY);
      s.vertex(topLeftX+BTN_WIDTH, topLeftY);
      s.vertex(topLeftX+BTN_WIDTH, topLeftY+BTN_HEIGHT);
      s.vertex(topLeftX, topLeftY+BTN_HEIGHT);
      s.vertex(topLeftX, topLeftY);
      s.endShape();
      shape(s);
    }
  }

  /**
   * Draws a name box for the player with the given key.
   * When the name box has not yet been typed into, or if the user is not hovering over it,
   * it is purple with the message "hover to type".
   * If the user is hovering over the name box, or the name box contains a non-empty string,
   * it is white, with the contained string displayed inside the box.
   * @param playerKey the integer index of the player to draw the name box for
   */
  private void drawNameBox(int playerKey) {
    if (this.nameBoxes.get(playerKey) != "") { //when something has been typed
      fill(255);
      stroke(255);
      rectMode(CENTER);
      rect(CANVAS_X/2, //x
          CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*playerKey), //y
          NAMEBOX_WIDTH, NAMEBOX_HEIGHT); //w & h
      fill(0);
      textAlign(CENTER, CENTER);
      textSize(TEXTSIZE_BTN);
      text(this.nameBoxes.get(playerKey), 
          CANVAS_X/2, //x
          CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*playerKey));//y
    }
    else if (hoverOverNameBox() == playerKey) { //when hovering over box
      fill(255);
      stroke(255);
      rectMode(CENTER);
      rect(CANVAS_X/2, //x
          CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*playerKey), //y
          NAMEBOX_WIDTH, NAMEBOX_HEIGHT); //w & h
      fill(0);
      textAlign(CENTER, CENTER);
      textSize(TEXTSIZE_BTN);
      text(this.nameBoxes.get(playerKey), 
          CANVAS_X/2, //x
          CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*playerKey));//y
    }
    else {//nothing has been typed yet
      fill(color(147, 99, 194));
      stroke(color(147, 99, 194));
      rectMode(CENTER);
      rect(CANVAS_X/2, //x
          CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*playerKey), //y
          NAMEBOX_WIDTH, NAMEBOX_HEIGHT); //w & h
      fill(0);
      textAlign(CENTER, CENTER);
      textSize(TEXTSIZE_BTN);
      text("hover to type", 
          CANVAS_X/2, //x
          CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*playerKey));//y
    }
  }

  /** 
   * Draws the pop up message that is displayed right before the player enters the actual game play state.
   * This message contains the name of the player whose turn it is, the instructions for the game.
   */
  private void drawStartPopUp() {
    Player currentPlayer = this.model.getPlayer(this.model.getTurn());
    fill(color(159, 59, 193));
    stroke(color(159, 59, 193));
    rectMode(CENTER);
    rect(CANVAS_X/2, CANVAS_Y/2, CANVAS_X/3, CANVAS_Y/2);
    fill(255);
    textAlign(CENTER, CENTER);
    textSize(TEXTSIZE_BTN);
    text(currentPlayer.getName() + "'s turn", CANVAS_X/2, (int)(CANVAS_VERTICAL_INC*2.3));
    fill(color(239,190,255));
    textSize(TEXTSIZE_SUBTITLE);
    text("Press the SPACE bar as many times\n" +"as possible within " 
        + Player.TIMELIMIT + " seconds!", CANVAS_X/2, CANVAS_VERTICAL_INC*3);
  }

  /**
   * Draws the list of circles that is being created with each key press on the player's spacebar.
   * Takes the circle from the listOfPlayersCircles and the colors from the list of colors.
   */
  private void drawListOfCircles() {
    List<PShape> circles = this.listOfPlayersCircles.get(model.getTurn());
    List<Integer[]> colors = this.listOfColors.get(model.getTurn());

    for (int i = 0; i < circles.size(); i++) {
      PShape circle = circles.get(i);
      Integer r = colors.get(i)[0];
      Integer g = colors.get(i)[1];
      Integer b = colors.get(i)[2];
      circle.setFill(color(r,g,b));
      circle.setStroke(false);
      shape(circle);
    }
  }

  /**
   * Draws the Time's up pop up screen, which is displayed when the time limit for the current
   * player's round has been reached. This message displays the name of the player whose time is up.
   */
  private void drawTimesUpMsg() {
    Player currentPlayer = this.model.getPlayer(this.model.getTurn());
    fill(color(159, 59, 193));
    stroke(color(159, 59, 193));
    rectMode(CENTER);
    rect(CANVAS_X/2, CANVAS_Y/2, CANVAS_X/3, CANVAS_Y/2);
    fill(255);
    textAlign(CENTER, CENTER);
    textSize(TEXTSIZE_SUBTITLE);
    text(currentPlayer.getName(), CANVAS_X/2, (int)(CANVAS_VERTICAL_INC*2.3));
    fill(color(239,190,255));
    textSize(TEXTSIZE_BTN);
    text("Time's Up!", CANVAS_X/2, (int)(CANVAS_VERTICAL_INC*2.7));
    textAlign(CENTER, CENTER);
    fill(255);
    textSize(TEXTSIZE_TITLE);
    text(Integer.toString(currentPlayer.getScore()), CANVAS_X/2-CANVAS_X/48, (int)(CANVAS_VERTICAL_INC*3.25));
    textAlign(CENTER, CENTER);
    fill(color(239,190,255));
    textSize(TEXTSIZE_SUBTITLE);
    text("points", CANVAS_X/2+CANVAS_X/32, (int)(CANVAS_VERTICAL_INC*3.3));
  }

  /**
   * Draw the final message in the final screen of the game.
   * If the game is in single player mode, then the method simply displays the final score of the player.
   * If the game is in multi player mode, then the method determines who the winner is, and displays a 
   * winner message or a draw message
   */
  private void drawFinalMessage() {
    fill(0);
    stroke(0);
    rectMode(CENTER);
    rect(CANVAS_X/2, CANVAS_Y/2, CANVAS_X/2, CANVAS_Y/2);

    fill(255);
    stroke(255);
    textAlign(CENTER, CENTER);
    textSize(TEXTSIZE_BTN);
    
    Player winner = model.determineWinner(); 

    if (numOfPlayers == 1) { //single player (doesn't need winner/loser)
      text(winner.getName() + "'s Final Score is", CANVAS_X/2, (int)(CANVAS_VERTICAL_INC*2.5));
    } 
    else {
      if (winner.getName().equals("none")) { //draw
        text("It's a draw!", CANVAS_X/2, (int)(CANVAS_VERTICAL_INC*2.5));
      }
      else {
        text(winner.getName() + " WINS with", CANVAS_X/2, (int)(CANVAS_VERTICAL_INC*2.5));
      }
    }
    fill(color(250, 0, 255));
    stroke(color(250, 0, 255));
    textSize(52);
    text(winner.getScore(), CANVAS_X/2, CANVAS_Y/2);
    fill(color(110,0,149));
    stroke(color(110,0,149));
    textSize(TEXTSIZE_BTN);
    text("points", CANVAS_X/2, (int)(CANVAS_VERTICAL_INC*4.2));
  }

  /**
   * Draws the header of the game, depending on the given state of the game.
   * Initially, the header only has the game title, but after the player chooses the game-play mode (single vs. dual), 
   * this information is added next to the title. When the player enters the PLAYING_STALE state (the pop-up message 
   * with the START button that starts the countdown and actual game play), the player's scoreboard is added to the 
   * header as well.
   * @param state the state of the game to draw the header for.
   */
  private void drawHeader(GameState state) {
    //draw header bg
    fill(color(21, 0, 41));
    stroke(color(21, 0, 41));
    rectMode(CORNER);
    rect(0, 0, CANVAS_X, CANVAS_VERTICAL_INC);
    //draw header title
    fill(color(115, 49, 180));
    stroke(color(115, 49, 180));
    textAlign(TOP, LEFT);
    textSize(TEXTSIZE_TITLE);
    text("SpaceChallenge", 20, 50);
    if (this.numOfPlayers != null) { //after single vs. dual play mode has been chosen,
      textAlign(TOP, LEFT);          //add it to the header
      textSize(TEXTSIZE_SUBTITLE);
      if (this.numOfPlayers == 1) {
        fill(color(250, 0, 255));
        stroke(color(250, 0, 255));
        text("Single-Player Mode", 300, 50);
      } 
      else if (this.numOfPlayers == 2) {
        fill(color(0, 224, 255));
        stroke(color(0, 224, 255));
        text("Dual-Player Mode", (int)(CANVAS_X/3.2), (int)(CANVAS_Y/10.8));
      }
    }
    if ((model.getGameState() == GameState.PLAYING_STALE)
        || (model.getGameState() == GameState.PLAYING_ACTIVE)
        || (model.getGameState() == GameState.FINAL)){
      for (int i = 0; i < numOfPlayers; i++) {
        drawScoreBoard(i);
      }
    }
  }

  /**
   * Draws the scoreboard for a given player within the header.
   * The scoreboard shows the name of the player, the time they have remaining, 
   * and their current number of points.
   * @param playerKey the index of the player to draw the scoreboard for
   */
  private void drawScoreBoard(int playerKey) {
    int boardWidth = CANVAS_X/6;
    int boardHeight = CANVAS_VERTICAL_INC;
    int variableWidth = (playerKey * (boardWidth + 12));
    if (playerKey == this.model.getTurn()) {
      fill(color(110,0,149));
      stroke(color(110,0,149));
    }
    else {
      fill(color(21,49,123));
      stroke(color(21,49,123));
    }
    rectMode(CORNER);
    rect(CANVAS_X - (boardWidth + variableWidth), 
        0, boardWidth, boardHeight);

    textAlign(CENTER, BOTTOM);

    //player name
    fill(color(235,178,255));
    stroke(color(235,178,255));
    textSize(TEXTSIZE_SUBTITLE);
    text(this.model.getPlayer(playerKey).getName(), 
        CANVAS_X - (boardWidth + variableWidth) + boardWidth/2, 22);

    //static text
    fill(color(210,79,255));
    stroke(color(210,79,255));
    textSize(TEXTSIZE_SUBTITLE);
    text("seconds",
        CANVAS_X - (boardWidth + variableWidth) + 64, 48);
    textSize(TEXTSIZE_CAPTION);
    text("time remaining", 
        CANVAS_X - (boardWidth + variableWidth) + 56, 66);
    textSize(TEXTSIZE_CAPTION);
    text("points", 
        CANVAS_X - (boardWidth + variableWidth) + 138, 66);

    //variable text
    fill(255);
    stroke(255);
    textSize(TEXTSIZE_CAPTION);
    text(this.model.getPlayer(playerKey).getScore(),
        CANVAS_X - (boardWidth + variableWidth) + 138, 48);//points
    textSize(TEXTSIZE_CAPTION);
    text(this.model.getPlayer(playerKey).timer.currentCountdownTime,
        CANVAS_X - (boardWidth + variableWidth) + 24, 48);//sec remaining
  }

  /**
   * Draws the pop up message when the user clicks on the RESET button.
   * The pop up message asks if the user is sure about resetting, 
   * and provides a button to confirm the reset and another one to cancel the reset.
   */
  private void drawResetMessagePopUp() {
    if (numOfPlayers != null) {
      fill(100);
      stroke(100);
      rectMode(CENTER);
      rect(CANVAS_X/2, CANVAS_Y/2, CANVAS_X/2, CANVAS_Y/2);fill(255);
      fill(255);
      stroke(255);
      textAlign(CENTER, CENTER);
      textSize(TEXTSIZE_SUBTITLE);
      text("Are you sure you want to reset?", CANVAS_X/2, CANVAS_Y/3);
      drawButton(this.btnResetConfirm, (CANVAS_X/2 - BTN_WIDTH/2), (int)(CANVAS_VERTICAL_INC*2.7), 0, 0, 0, 50, 50, 50, "Yes, reset", 255, 255, 255);
      drawButton(this.btnResetCancel, (CANVAS_X/2 - BTN_WIDTH/2), (int)(CANVAS_VERTICAL_INC*4), 0, 0, 0, 50, 50, 50, "No, go back", 255, 255, 255);
    }
  }




  //----------------------------------------------------------------------------
  //MOUSECLICK------------------------------------------------------------------
  //----------------------------------------------------------------------------

  /**
   * Executes commands for when a mouse click occurs while the program is running,
   * depending on the state of the game. The mouse clicks trigger responses by different buttons.
   * All buttons are unique to a GameState (e.g. START button is in the STALE state, NEXT button 
   * is in the ACTIVE state), except for the RESET button which is in all game states, 
   * except for the very first screen.
   */
  public void mouseClicked() {
    switch (model.getGameState()) {
      case INTRO:
        if (numOfPlayers == null) { //number of players (single vs. dual) has not yet been chosen
          playModeButtonListener();
        }
        else { //time to type player names
          confirmButtonListener();
        }
        break;
      case PLAYING_STALE:
        startButtonListener();
        break;
      case PLAYING_ACTIVE:
        if (this.timesUpMsgShowing) { //only when a player's time is up
          nextButtonListener(); 
        }
        break;
      case FINAL:
        break;
      default:
        throw new IllegalStateException();
    }
    resetButtonListener(); //reset button in all game states
  }

  //MOUSECLICK helpers------------------------------------  

  /**
   * Listens to the play mode buttons (i.e. SINGLE vs. DUAL), during the INTRO game state.
   * Sets up the player variables, depending on the number of players indicated by the clicking of one of the buttons.
   */
  private void playModeButtonListener() {
    if (clickedOn(this.btnSingle)) { // single player mode
      setPlayerVariables(1);
    }
    else if (clickedOn(this.btnDual)) { //two players mode
      setPlayerVariables(2);
    }
  }

  /**
   * Listens to the CONFIRM button (during INTRO game state, where user is typing in player names).
   * Only listen and allow clicking of CONFIRM button if there aren't any empty name boxes.
   * When CONFIRM is clicked, it will set players' names to the string values typed in each name box,
   * then moves onto the next game state.
   */
  private void confirmButtonListener() {
    if (clickedOn(this.btnConfirmName)) {
      if (checkAllNameBoxesFilled()) {//check that all names have been filled.
        for (int i = 0; i < numOfPlayers; i++) {
          this.model.getPlayer(i).setName(this.nameBoxes.get(i)); //set players' names
        }
        this.model.setGameState(GameState.PLAYING_STALE); //move onto next game state.
      }
    }
  }

  /**
   * Determines if all name boxes have been filled -- returns true if they have been, false if there still are empty boxes.
   * @return true if all name boxes have been filled, false if they haven't
   */
  private boolean checkAllNameBoxesFilled() {
    for (Map.Entry<Integer, String> set : this.nameBoxes.entrySet()) { //for all name boxes
      String name = set.getValue();
      if (name == "") {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets up all variables that is required for the players, based on given number of players.
   * - the number of players is saved to the numOfPlayers variable,
   * - a new player object is added to the model for the given number of players,
   * - a list of circles and a list of color values (rgb), is added for each player, and
   * - a name box is created for each player, containing an empty string.
   * 
   * @param numOfPlayers the number of players in this game.
   */
  private void setPlayerVariables(int numOfPlayers) {
    this.numOfPlayers = numOfPlayers;
    this.model.addPlayers(numOfPlayers);
    for (int i = 0; i < numOfPlayers; i++) {
      this.listOfPlayersCircles.add(new ArrayList<PShape>());
      this.listOfColors.add(new ArrayList<Integer[]>());
      this.nameBoxes.put(i, "");
    }
  }

  /**
   * Listens to the START button before a player start's their round.
   * The timer starts and player enters the actual game play stage as soon as the button is clicked.
   */
  private void startButtonListener() {
    if (clickedOn(this.btnStart)) {
      this.model.getPlayer(model.getTurn()).timer.setStartTime(millis());
      this.model.setGameState(GameState.PLAYING_ACTIVE);
    }
  }

  /**
   * Listens to the NEXT button after a player's round is done and the Time's Up message shows up.
   * If there are still players that have not yet played, the method sets the game for the next player's turn.
   * If all players have already played, then goes to the final screen.
   */
  private void nextButtonListener() {
    if (clickedOn(this.btnNext)) {
      if (model.getTurn() < this.numOfPlayers - 1) { //there are still more players who need to play
        model.nextTurn();
        model.setGameState(GameState.PLAYING_STALE);
      }
      else { //last player's turn
        model.setGameState(GameState.FINAL);
      }
      this.timesUpMsgShowing = false;
    }
  }

  /**
   * Listens to the RESET button during all stages throughout the game (except for very first screen).
   * When the user clicks on the reset button, a reset message pops up asking if the user is sure.
   * If this message is showing, and the user clicks on the RESET CONFIRM button, the game resets.
   * If the user clicks on the RESET CANCEL button, the reset message goes away.
   */
  private void resetButtonListener() {
    if (resetMsgShowing) {
      if (clickedOn(this.btnResetCancel)) { //cancel reset
        this.resetMsgShowing = false; //remove pop up screen
      }
      else if (clickedOn(this.btnResetConfirm)) { //confirm reset
        this.setup();//reset
      }
    } 
    else { //detect clicking on reset button
      if (clickedOn(this.btnReset) && numOfPlayers != null) {
        this.resetMsgShowing = true;
      }
    }
  }

  /**
   * Determines if the mouse has clicked on the given button
   * @param btn the given button to be checked for mouse click
   * @return true if the mouse click's position is within the given button's area
   */
  private boolean clickedOn(PShape btn) {
    return (btn.getVertexX(0) <= mouseX && mouseX <= btn.getVertexX(0)+ btn.height)
        && (btn.getVertexY(0) <= mouseY && mouseY <= btn.getVertexY(0)+ btn.height);
  }





  //----------------------------------------------------------------------------
  //KEYBOARD--------------------------------------------------------------------
  //----------------------------------------------------------------------------

  /**
   * Executes commands for when a keyboard input occurs while the program is running, depending on the state of the game. 
   * Currently, there are two points in which keyboard inputs are utilized within the game. 
   * The first is during the INTRO state where the user is inputting the names of the players, and 
   * the second is during the ACTIVE playing state where the user is hitting the SPACEbar to score points.
   */
  public void keyPressed() {
    //1. Keyboard functionality for entering player names
    if (this.model.getGameState() == GameState.INTRO) {
      if (numOfPlayers != null) { //name entering screen
        for (int i = 0; i < numOfPlayers; i++) {
          //if the user is hovering over a name box, enable typing into that box
          if (hoverOverNameBox() == i) {
            String orig = this.nameBoxes.get(i);
            if (orig != "" && key == BACKSPACE) { //backspace functionality (except when a name box has nothing in it)
              this.nameBoxes.put(i, orig.substring(0, orig.length()-1));
            }
            else if (key == '\n') { //disabling enter key
              //do nothing
            }
            else { //adding typed key to the original string in the name box
              this.nameBoxes.put(i, orig + key);
            }
          }
        }       
      }
    }
    //2. Keyboard functionality for player scoring points
    else if (model.getGameState() == GameState.PLAYING_ACTIVE) {
      if (key == ' ' && !timesUpMsgShowing) {
        int turn = model.getTurn();
        //increase the score
        model.getPlayer(turn).scoreUp();
        //add a circle with a random diameter at a random position on the canvas
        int randomDiameter = (int)random(200);
        PShape randomCircle = createShape(ELLIPSE, random(CANVAS_X), random(CANVAS_Y), randomDiameter, randomDiameter);
        listOfPlayersCircles.get(turn).add(randomCircle);
        //add random rgb color values for this random circle
        Integer[] randomColors = {(int)random(255),(int)random(255),(int)random(255)};
        listOfColors.get(turn).add(model.getPlayer(turn).getScore()-1, randomColors);
      }
    }
  }

  /**
   * Returns the integer index (zero-based) of the name box that the user's mouse is hovering over.
   * Returns -1 if the user's mouse is NOT hovering over any of the name boxes.
   * @return the number of the name box that the mouse is hovering over, -1 if none.
   */
  private Integer hoverOverNameBox() {
    for (int i = 0; i < numOfPlayers; i++) {
      if ((CANVAS_X/2 - (NAMEBOX_WIDTH/2) <= mouseX && mouseX <= CANVAS_X/2 + (NAMEBOX_WIDTH/2))
          && (CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*i) - (NAMEBOX_HEIGHT/2) <= mouseY
          && mouseY <= CANVAS_VERTICAL_INC*3 + (CANVAS_VERTICAL_INC*i) + (NAMEBOX_HEIGHT/2))) {
        return i;
      }
    }
    return -1;
  }



  //----------------------------------------------------------------------------
  //MAIN------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  // Driver code
  public static void main(String[] args) {
    PApplet.main(new String[] {"--present", "Processing"});
  }
}
