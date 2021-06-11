//Author:
//File Name: Main.java
//Project Name: Forest Runner
//Creation Date: June 8, 2021
//Modified Date: June 18, 2021
//Description:

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;

import Engine.Core.*;
import Engine.Gfx.*;

import java.util.ArrayList;

public class Main extends AbstractGame
{
  //Required Basic Game Functional Data
  /**
   *  GraphicsDevice class describes the graphics devices that might be available in a particular graphics environment.
   *  These include screen and printer devices. Note that there can be many screens and many printers in an instance of GraphicsEnvironment .
   *  Each graphics device has one or more GraphicsConfiguration objects associated with it.
   *  These objects specify the different configurations in which the GraphicsDevice can be used.
   *  Ref :https://docs.oracle.com/cd/E17802_01/j2se/j2se/1.5.0/jcp/beta1/apidiffs/java/awt/GraphicsDevice.html
   */

  private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
  private static int screenWidth = device.getDisplayMode().getWidth();
  private static int screenHeight = device.getDisplayMode().getHeight();
  private static int windowWidth;
  private static int windowHeight;

  //Store how many milliseconds are in one second
  private static final float SECOND = 1000f;
  private static final int TITLE_BAR_HEIGHT = 25;

  //Game States - Add/Remove/Modify as needed
  //These are the most common game states, but modify as needed
  //You will ALSO need to modify the two switch statements in Update and Draw
  private static final int MENU = 0;
  private static final int SETTINGS = 1;
  private static final int INSTRUCTIONS = 2;
  private static final int GAMEPLAY = 3;
  private static final int PAUSE = 4;
  private static final int ENDGAME = 5;

  //Required Basic Game Visual data used in main below
  private static String gameName = "Forest Runner";
  //60 frame/ seconds -> smooth screen transition
  private static int fps = 60;

  //Store and set the initial game state, typically MENU to start
  //private int gameState = GAMEPLAY;
  private int gameState = MENU;

  /////////////////////////////////////////////////////////////////////////////////////
  // Define your Global variables and constants here (They do NOT need to be static) //
  /////////////////////////////////////////////////////////////////////////////////////

  final int NO_OBJECT = -1;

  //Player Movement directions
  final int UP = -1;
  final int DOWN = 1;
  final int LEFT = -1;
  final int RIGHT = 1;
  final int STOP = 0;

  //Menu Options
  static final int PLAY = 1;
  static final int INSTRUCT =2;
  static final int EXIT = 3;

  //Menu Movement
  static final float MENU_DELTA_Y = 115f;

  //Main Menu Attributes
  int menuOption = PLAY;


  //SpriteSheet Img Object
  SpriteSheet bgImg;
  SpriteSheet InstructImg;
  SpriteSheet playerImg;
  //enemy1 : can kill to jump onto them.
  SpriteSheet enemy1Img;
  //enemy2 : can kill to punch
  SpriteSheet enemy2Img;
  //enemy3 : can't kill and need to escape (or duck)
  SpriteSheet enemy3Img;
  SpriteSheet titleBGImg;
  SpriteSheet indicatorImg;


  //Position and Speed of Player
  Vector2F playerPos;
  Vector2F playerSpeed = new Vector2F(0,0);

  //Text Location
  Vector2F titleTxtLoc;
  Vector2F scoreTxtLoc;
  Vector2F healthTxtLoc;
  Vector2F healthGageTxtLoc;
  Vector2F timerTxtLoc;
  Vector2F promptTxtLoc;

  //Text Display
  String healthMsg = "Health: ";
  String healthGageMsg = "Health Gage:";
  String highScoreMsg = "HIGH SCORE: ";
  String scoreMsg = "SCORE: ";
  String timerMsg = "Timer: ";
  String endRoundMsg = "Congratulations!  You completed Round ";
  String endScoreMsg = "Your final score is ";
  String continueMsg = "Press <ENTER> to continue";
  String newHighScoreMsg = "You got the HIGH SCORE!!";


  //Font Setting
  Font msgFont = new Font("Apple Casual", Font.BOLD, 20);
  Font hudTitleFont = new Font("Century Gothic", Font.BOLD, 50);
  Font hudDataFont = new Font("Century Gothic", Font.BOLD + Font.ITALIC, 20);


  //Track the Inactive object location
  static final Vector2F INACTIVE = new Vector2F(-200,-200);

  //Game Data
  int score = 0;
  int highScore = 0;
  int healthPercent = 100;
  int health= 3;
  int timer = 0;

















  public static void main(String[] args) 
  {
    /***********************************************************************
                        DO NOT TOUCH THIS SECTION
    ***********************************************************************/
    windowWidth = screenWidth;
    windowHeight = screenHeight - TITLE_BAR_HEIGHT;

    GameContainer gameContainer = new GameContainer(new Main(), gameName, screenWidth, screenHeight, fps);
    gameContainer.Start();
  }

  public void LoadContent(GameContainer gc)
  {

    //Load Main Menu objects
    titleBGImg = new SpriteSheet(LoadImage.FromFile("resources/images/backgrounds/Forest_Title.png"));
    titleBGImg.destRec = new Rectangle(0,0,windowWidth, windowHeight);
    indicatorImg = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/Arrow.png"));
    indicatorImg.destRec = new Rectangle(390, 340, indicatorImg.destRec.width, indicatorImg.destRec.height);

    //Load Instruction Menu objects
    InstructImg = new SpriteSheet(LoadImage.FromFile("resources/images/backgrounds/Instruction.png"));
    InstructImg.destRec = new Rectangle(0,0,windowWidth, windowHeight);

    //Load the base, non-scrolling background image
    bgImg = new SpriteSheet(LoadImage.FromFile("resources/images/backgrounds/forest_bg.jpg"));
    bgImg.destRec = new Rectangle(0,0,windowWidth, windowHeight);


    //Location of Text in GamePlay
    titleTxtLoc = new Vector2F(windowWidth - 400,65);
    scoreTxtLoc = new Vector2F(100,100);
    healthTxtLoc = new Vector2F(300,100);
    healthGageTxtLoc = new Vector2F(500,100);
    timerTxtLoc = new Vector2F(740,100);


    //Load Player Image

    playerImg = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/player.png"),1,4,0,4,4);
    playerImg.destRec = new Rectangle(60,
            windowHeight - (int)(playerImg.GetFrameHeight() * 2f),
            (int)(playerImg.GetFrameWidth()),
            (int)(playerImg.GetFrameHeight()));
    playerImg.StartAnimation();

    //Load enemy1 Image
    enemy1Img = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/jump_enemy.png"),1,6,0,6,3);
    enemy1Img.destRec = new Rectangle(300,
            windowHeight - (int)(playerImg.GetFrameHeight() * 2f),
            (int)(enemy1Img.GetFrameWidth()),
            (int)(enemy1Img.GetFrameHeight()));
    enemy1Img.StartAnimation();

    //Load enemy2 Image
    enemy2Img = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/punch_enemy.png"),1,8,0,8,3);
    enemy2Img.FlipHorizontally();
    enemy2Img.destRec = new Rectangle(600,
            windowHeight - (int)(playerImg.GetFrameHeight() * 2f),
            (int)(enemy2Img.GetFrameWidth()),
            (int)(enemy2Img.GetFrameHeight()));
    enemy2Img.StartAnimation();
    //Load enemy3 Image
    enemy3Img = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/duck_enemy.png"),2,12,0,24,4);
    enemy3Img.destRec = new Rectangle(200,
            300,
            (int)(enemy3Img.GetFrameWidth()),
            (int)(enemy3Img.GetFrameHeight()));
    enemy3Img.StartAnimation();




  }

  public void Update(GameContainer gc, float deltaTime)
	{
    switch (gameState)
    {
      case MENU:
        //Get and implement menu interactions
        UpdateMenu(gc);
        break;

      case INSTRUCTIONS:
        //Update Instruction Menu
        UpdateInstructions(gc);
        break;
      case GAMEPLAY:
        //Implement standared game logic (input, update game objects, apply physics, collision detection)
        UpdateGamePlay(gc, deltaTime);
        break;

      case ENDGAME:
        //Wait for final input based on end of game options (end, restart, etc.)
        UpdateEndGame(gc);
        break;
    }
	}

  public void Draw(GameContainer gc, Graphics2D gfx) 
	{
    switch (gameState)
    {
      case MENU:
        //Draw the possible menu options
        DrawMenu(gfx);
        break;

      case INSTRUCTIONS:
        //Draw all instruction
        DrawInstructions(gfx);
        break;
      case GAMEPLAY:
        //Draw all game objects on each layers (background, middleground, foreground and user interface)
        DrawGamePlay(gfx);
        break;

      case ENDGAME:
        //Draw the final feedback and prompt for available options (exit,restart, etc.)
        DrawEndGame(gfx);
        break;
    }
	}

  //Pre: gc is the Game controller
  //Post: None
  //Desc: Handle Main Menu logic
  private void UpdateMenu(GameContainer gc)
  {
    //Navigate the menu with the up and down arrow keys
    if (Input.IsKeyReleased(KeyEvent.VK_UP) && menuOption == EXIT)
    {
      menuOption = INSTRUCT;
      indicatorImg.destRec.y -= MENU_DELTA_Y;
    }else if (Input.IsKeyReleased(KeyEvent.VK_UP) && menuOption == INSTRUCT){
      menuOption = PLAY;
      indicatorImg.destRec.y -= MENU_DELTA_Y;
    } else if (Input.IsKeyReleased(KeyEvent.VK_DOWN) && menuOption == PLAY)
    {
      menuOption = INSTRUCT;
      indicatorImg.destRec.y += MENU_DELTA_Y;
    }else if (Input.IsKeyReleased(KeyEvent.VK_DOWN) && menuOption == INSTRUCT){
      menuOption = EXIT;
      indicatorImg.destRec.y += MENU_DELTA_Y;
    }

    //Trigger the chosen menu option on either the space bar or enter key
    if (Input.IsKeyReleased(KeyEvent.VK_SPACE) || Input.IsKeyReleased(KeyEvent.VK_ENTER))
    {
      switch (menuOption)
      {
        case PLAY:
          //Setup the first round
          //SetupRound();
          gameState = GAMEPLAY;
          break;

        case INSTRUCT:
          gameState = INSTRUCTIONS;
          break;
        case EXIT:
          //End the game
          gc.Stop();
          break;

      }
    }
  }

  //Pre: gc is the Game controller
  //Post: None
  //Desc: Handle Instructions Menu logic
  private void UpdateInstructions(GameContainer gc){

    if (Input.IsKeyReleased(KeyEvent.VK_ESCAPE))
    {
      gameState = MENU;
    }

  }


  //Pre: gc is the Game controller, 
  //     deltaTime is the amount of time passed since the last
  //     time UpdateGamePlay was called
  //Post: None
  //Desc: Handle regular game play logic
  private void UpdateGamePlay(GameContainer gc, float deltaTime)
  {

    if (Input.IsKeyReleased(KeyEvent.VK_SPACE))
    {
      if (playerImg.IsAnimating())
      {
        playerImg.StopAnimation();
      }
      else
      {
        playerImg.StartAnimation();
      }
    }

    if (Input.IsKeyReleased(KeyEvent.VK_CONTROL))
    {

    }





  }



  //Pre: gc is the Game controller
  //Post: None
  //Desc: Handle End of Game logic
  private void UpdateEndGame(GameContainer gc)
  {
    //Reset the game on the ENTR key
    if(Input.IsKeyReleased(KeyEvent.VK_ENTER))
    {
      ResetGame();
      gameState = MENU;
    }
  }

  //Pre: gfx is the window being drawn to
  //Post: None
  //Desc: Draw the Main menu
  private void DrawMenu(Graphics2D gfx)
  {
    Draw.Sprite(gfx, titleBGImg);
    Draw.Sprite(gfx, indicatorImg);
  }

  //Pre: gfx is the window being drawn to
  //Post: None
  //Desc: Draw the Instructions menu
  private void DrawInstructions (Graphics2D gfx)
  {
    Draw.Sprite(gfx, InstructImg);
  }

  //Pre: gfx is the window being drawn to
  //Post: None
  //Desc: Draw the regular game play
  private void DrawGamePlay(Graphics2D gfx)
  {
    Draw.Sprite(gfx, bgImg);
    Draw.Sprite(gfx, playerImg);
    Draw.Sprite(gfx, enemy1Img);
    Draw.Sprite(gfx, enemy2Img);
    Draw.Sprite(gfx, enemy3Img);

    Draw.Text(gfx,gameName,titleTxtLoc.x,titleTxtLoc.y,hudTitleFont,Helper.DEEPBLUE,0.6f);

    Draw.Text(gfx,scoreMsg , scoreTxtLoc.x, scoreTxtLoc.y, hudDataFont, Helper.BLACK, 1f);
    Draw.Text(gfx, score + " pts.", scoreTxtLoc.x + 75, scoreTxtLoc.y, hudDataFont, Helper.BLACK, 1f);
    Draw.Text(gfx,healthMsg,healthTxtLoc.x, healthTxtLoc.y, msgFont, Helper.BLACK,1f);
    Draw.Text(gfx, health + "/3", healthTxtLoc.x + 75, healthTxtLoc.y, hudDataFont, Helper.BLACK, 1f);
    Draw.Text(gfx,healthGageMsg,healthGageTxtLoc.x,healthGageTxtLoc.y,msgFont, Helper.BLACK,1f);
    Draw.Text(gfx, healthPercent + "%", healthGageTxtLoc.x + 140, healthGageTxtLoc.y, hudDataFont, Helper.BLACK, 1f);

    Draw.Text(gfx,timerMsg,timerTxtLoc.x,timerTxtLoc.y,hudDataFont, Helper.BLACK, 1f );
    Draw.Text(gfx,timer + " sec.",timerTxtLoc.x + 75,timerTxtLoc.y,hudDataFont, Helper.BLACK, 1f );

  }



  //Pre: gfx is the window being drawn to
  //Post: None
  //Desc: Draw the End Game state
  private void DrawEndGame(Graphics2D gfx)
  {
    //Draw Background images
    Draw.Sprite(gfx, bgImg);


    //Draw HUD Elements
    Draw.Text(gfx, endScoreMsg + score, 190, windowHeight / 2, msgFont, Helper.YELLOW, 1f);

    //Tell the user if they earned the high score!
    if (score == highScore)
    {
      Draw.Text(gfx, newHighScoreMsg, 170, windowHeight / 2 + 50, msgFont, Helper.WHITE, 1f);
      Draw.Text(gfx, newHighScoreMsg, 168, windowHeight / 2 + 48, msgFont, Helper.MAGENTA, 1f);
    }

    //Display Enter to continue message
    Draw.Text(gfx, continueMsg, 160, windowHeight - 30, msgFont, Helper.RED, 1f);
  }


  //Pre: gc is the Game controller
  //Post: None
  //Desc: Reset all game data to their original values and positions
  private void ResetGame()
  {
    score = 0;
  }

  //Pre: None
  //Post: None
  //Desc: Determine high score changes and send the game into the end state
  private void EndGame()
  {
    //Update the high score if necessary
    if (score > highScore)
    {
      highScore = score;
    }

    //Send the game to end state
    gameState = ENDGAME;
  }

}