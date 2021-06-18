//Author:
//File Name: Main.java
//Project Name: Forest Runner
//Creation Date: June 8, 2021
//Modified Date: June 18, 2021
//Description:

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import Engine.Core.*;
import Engine.Gfx.*;

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
  private static final float TOTAL_GAME = 18000f;
  private static final float DIFFICULTY = 500f;
  private static final int TITLE_BAR_HEIGHT = 25;


  //Game States - Add/Remove/Modify as needed
  //These are the most common game states, but modify as needed
  //You will ALSO need to modify the two switch statements in Update and Draw
  private static final int MENU = 0;
  private static final int SETTINGS = 1;
  private static final int INSTRUCTIONS = 2;
  private static final int GAMEPLAY = 3;
  private static final int PAUSE = 4;
  private static final int ENDROUND = 5;
  private static final int ENDGAME = 6;

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




  //Player Movement directions
  final int UP = -1;
  final int DOWN = 1;
  final int LEFT = 1;
  final int RIGHT = -1;
  final int STOP = 0;
  final int MAX_BULLETS = 6;
  final int MAX_ENEMIES = 3;
  static final float BASE_SPEED = 3f;
  static final float MAX_SPEED = 15f;
  int numEnemy1 = 1;
  int numEnemy2 = 1;
  int numEnemy3 = 2;
  static final int INVALID = -1;
  //The amount of time between enemy spawns
  final float SPAWN_TIME = 500;
  static final int KILL_PTS = 10;
  static final int TIME_PTS =1;


  /**
   * Player Global Variables
   */
  SpriteSheet [] playerImg = new SpriteSheet[3];
  //Store the current speed of the player
  Vector2F playerSpeed = new Vector2F(0f,0f);
  //Position and Speed of Player
  Vector2F playerPos;

  final float GRAVITY = 9.8f/fps;

  //Player Action Index
  final int WALK = 0;
  final int JUMP = 1;
  final int DUCK = 2;
  //Track the forces working against the player every update
  //In this demo the player will not be moving horizontally
  Vector2F forces = new Vector2F(0, GRAVITY);

  //The initial jump speed that will be reduced by gravity each update
  float jumpSpeed = -6.5f;

  //Track whether the player is on the ground or not,
  //dictating when jump is allowed and friction is applied
  boolean grounded = false;

  //Track the current state of the player
  int playerState = WALK;



  //Track the player's horizontal acceleration data
  float accel = 0.2f;
  float friction = accel * 0.5f;
  float tolerance = friction * 0.9f;
  float maxPlayerSpeed = 7f;


  //Menu Options
  static final int PLAY = 1;
  static final int INSTRUCT =2;
  static final int EXIT = 3;


  //Menu Movement
  static final float MENU_DELTA_Y = 115f;

  //Main Menu Attributes
  int menuOption = PLAY;

  static final int MAX_ROUND = 3;

  //SpriteSheet Img Object
  SpriteSheet bg1Img;
  SpriteSheet bg2Img;
  SpriteSheet InstructImg;
  SpriteSheet titleBGImg;
  SpriteSheet indicatorImg;
  SpriteSheet [] bulletImgs = new SpriteSheet[MAX_BULLETS];
  SpriteSheet [] explosions = new SpriteSheet[MAX_BULLETS * 2];

  //Position of BackGround
  Vector2F bg1Pos;
  Vector2F bg2Pos;
  float scrollSpeed = 4f;



  //The enemy game objects
  GameCircle [] circleEnemies = new GameCircle[MAX_ENEMIES];
  GameRectangle [] rectangles1Enemies = new GameRectangle[MAX_ENEMIES];
  GameRectangle [] rectangles2Enemies = new GameRectangle[MAX_ENEMIES];



  //The random speed ranges for the enemy movement
  //Active obstacles will move at a speed of 4.5 pixels/update, to the opposite edge of the screen where they deactivate
  float enemySpeed = 4.5f;
  float maxEnemySpeed = 10f;
  float circleFrequency = 1000000f;
  float rectangles1Frequency = 300000f;
  float rectangles2Frequency = 700000f;

  //-1 == false
  final int NO_OBJECT = -1;



  //The size of the enemies before and after being hit
  final float BIG_RAD = 15f;
  final float SMALL_RAD = 5f;

  float spawnTimeLeft = SPAWN_TIME;


  //Position of Bullets
  Vector2F [] bulletsPos = new Vector2F[MAX_BULLETS];

  float bulletSpeed = 5f;
  //location of object
  int xDir = STOP;
  int yDir = STOP;

  //Text Location
  Vector2F titleTxtLoc;
  Vector2F scoreTxtLoc;
  Vector2F healthTxtLoc;
  Vector2F timerTxtLoc;

  //Text Display
  String healthMsg = "Health: ";
  String highScoreMsg = "HIGH SCORE: ";
  String scoreMsg = "SCORE: ";
  String timerMsg = "Timer: ";
  String endRoundMsg = "Congratulations!  You completed Round ";
  String endScoreMsg = "Your final score is ";
  String continueMsg = "Press <ENTER> to continue";
  String newHighScoreMsg = "You got the HIGH SCORE!!";
  String returnMenuMsg = "Press ENTER to return to menu";


  //Font Setting
  Font msgFont1 = new Font("Apple Casual", Font.BOLD, 20);
  Font msgFont2 = new Font("Apple Casual", Font.BOLD, 35);
  Font hudTitleFont = new Font("Century Gothic", Font.BOLD, 50);
  Font hudDataFont = new Font("Century Gothic", Font.BOLD + Font.ITALIC, 20);


  //Track the Inactive object location
  static final Vector2F INACTIVE = new Vector2F(-200,-200);

  //Game Data
  int roundNum = 1;
  int score = 0;
  int highScore = 0;
  int health= 3;
  float timer = 0;

  //timer
  //store the amount of time the repeating timer will take before resetting
  final int REPEAT_TIME = 3000;

  //Store the amount of time the active "ability" will last for
  //Note: there may be different types of active timers that take different
  //amount of times, so more constants may be needed
  final int ACTIVE_TIME = 10000;

  //A timer used to track the total time passed while in game play (in milliseconds)
  float clockTimer = 0;

  //An automatic timer that ticks down to 0, then produces an action and resets (like an alarm clock)
  float repeatingTimer = REPEAT_TIME;

  //A timer activated by some event (e.g. picking up a timed power-up). Typically, These
  //timers can only be activated if not already active
  float activatedTimer = 0;




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

    //Load the base, scrolling background image
    bg1Img = new SpriteSheet(LoadImage.FromFile("resources/images/backgrounds/forest_bg.jpg"));
    bg1Img.destRec = new Rectangle(0,0,windowWidth, windowHeight);
    bg2Img = new SpriteSheet(LoadImage.FromFile("resources/images/backgrounds/forest_bg.jpg"));
    bg2Img.destRec = new Rectangle(windowWidth,0,windowWidth, windowHeight);
    bg1Pos = new Vector2F(bg1Img.destRec.x, bg1Img.destRec.y);
    bg2Pos = new Vector2F(bg2Img.destRec.x, bg2Img.destRec.y);



    //Location of Text in GamePlay
    titleTxtLoc = new Vector2F(windowWidth - 400,65);
    scoreTxtLoc = new Vector2F(100,100);
    healthTxtLoc = new Vector2F(300,100);
    timerTxtLoc = new Vector2F(740,100);


    //Load Player Image
    //Setup the player image, including it screen location, size and true position
    playerImg[WALK] = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/player.png"),1,4,0,4,4);
    playerImg[WALK].destRec = new Rectangle(100,100,playerImg[WALK].GetFrameWidth(),playerImg[WALK].GetFrameHeight());


    playerImg[JUMP] = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/player_jump.png"));
    playerImg[JUMP].destRec = new Rectangle(100,100,playerImg[JUMP].GetFrameWidth(),playerImg[JUMP].GetFrameHeight());

    playerImg[DUCK] = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/player_duck.png"));
    playerImg[DUCK].destRec = new Rectangle(100,100,playerImg[DUCK].GetFrameWidth(),playerImg[DUCK].GetFrameHeight());

    //All images will share the same position, however you may have to rethink this if
    //all of your player state imagery doesn't have matching sizes!
    playerPos = new Vector2F(playerImg[WALK].destRec.x,playerImg[WALK].destRec.y);

    //Player starts in the walk state, so begin animating it
    playerImg[WALK].StartAnimation();

    //Load Bullet Image
    for (int i = 0; i < bulletImgs.length; i++)
    {
      bulletImgs[i] = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/bullet.png"),1,10,0,10,3);
      bulletImgs[i].destRec = new Rectangle(0,0,bulletImgs[i].GetFrameWidth(),bulletImgs[i].GetFrameHeight());
      bulletImgs[i].SetVisible(false);
      bulletsPos[i] = new Vector2F(bulletImgs[i].destRec.x,bulletImgs[i].destRec.y);
    }

    //Load deactive Explosions
    for (int i = 0; i < explosions.length; i++)
    {
      explosions[i] = new SpriteSheet(LoadImage.FromFile("resources/images/sprites/explode2.png"),
              5,5,0,23,2);
      explosions[i].destRec = new Rectangle((int)INACTIVE.x, (int)INACTIVE.y, explosions[i].GetFrameWidth(), explosions[i].GetFrameHeight());
    }

    //Load Circle Enemy Image

    //Create and setup all enemies to be inactive
    for (int i = 0; i < circleEnemies.length; i++)
    {
      circleEnemies[i] = new GameCircle(0,0,BIG_RAD,2,Helper.GREEN,Helper.YELLOW,0);
    }

    //Load enemy2 Image
    for (int i = 0; i < rectangles1Enemies.length; i++)
    {
      //GameRectangle(int x, int y, int width, int height, float borderWidth, Color borderColor, Color fillColor, float transparency)
      rectangles1Enemies[i] = new GameRectangle(0,0,30,50,3,Helper.GRAY,Helper.WHITE,0);
    }


    //Load enemy3 Image

    for (int i = 0; i < rectangles2Enemies.length; i++)
    {
      //GameRectangle(int x, int y, int width, int height, float borderWidth, Color borderColor, Color fillColor, float transparency)
      rectangles2Enemies[i] = new GameRectangle(0,0,60,20,3,Helper.GRAY,Helper.ORANGE,0);
    }





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

      case ENDROUND:
        //Get user input to begin the next round
        UpdateEndRound(gc);
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

      case ENDROUND:
        //Get user input to begin the next round
        DrawEndRound(gfx);
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

    int spawnIdx = NO_OBJECT;

    xDir = RIGHT;
    timer += SECOND;

    //1 point for each full second of play
    if ( timer % 100000 == 0){
      score ++;
    }
    //store circle data for collision detection between the bullets and Enemies
    GameCircle bulletCirc;
    GameCircle enemyCirc;


    //Update scrolling screen
    ScrollScreen(bg1Img, bg2Img, bg1Pos, bg2Pos, scrollSpeed);


    ////////////////////
    // HANDLE USER INPUT
    ////////////////////

    /**
     * Player Movement1 :Jump
     */
    //Jump if the player hits space and is on the ground
    if (Input.IsKeyPressed(KeyEvent.VK_SPACE) && grounded == true)
    {
      playerSpeed.y = jumpSpeed;
      playerState = JUMP;
      playerImg[WALK].StopAnimation();
    }

    /**
     * Player Movement2 :Duck
     */
    //Hold the down key to DUCK, release to return to WALK
    if (Input.IsKeyDown(KeyEvent.VK_DOWN) && grounded == true)
    {
      playerState = DUCK;
      playerImg[WALK].StopAnimation();
    }
    else if (Input.IsKeyReleased(KeyEvent.VK_DOWN) && grounded == true)
    {
      playerState = WALK;
      playerImg[WALK].StartAnimation();
    }

    //Add gravity to the player's y Speed (even if not jumping, to allow for falling)
    playerSpeed.y += forces.y;

    //Move the image of the player using its current speed and position
    //NOTE: Because you may move horizontally or vertically in your game, and potentially
    //change player states often, it is best to move all player images together to keep
    //them lined up to the true position value
    MoveGameObjects(playerImg, playerPos, playerSpeed);

    //Detect outer wall collision with the player and keep them on the screen
    PlayerWallCollision();

    /**
     * Player Movement3 :Shoot
     */
    //Shoot a bullet on the space key if one is available
    if (Input.IsKeyReleased(KeyEvent.VK_S))
    {
      spawnIdx = FindInactiveObject(bulletImgs);

      if (spawnIdx != NO_OBJECT)
      {
        bulletImgs[spawnIdx].SetVisible(true);
        bulletImgs[spawnIdx].StartAnimation();

        bulletImgs[spawnIdx].destRec.x = playerImg[WALK].destRec.x + playerImg[WALK].GetFrameWidth()-50;
        bulletImgs[spawnIdx].destRec.y = playerImg[WALK].destRec.y + playerImg[WALK].GetFrameHeight()/2 - bulletImgs[spawnIdx].GetFrameHeight()/2;
        bulletsPos[spawnIdx].x = bulletImgs[spawnIdx].destRec.x;
        bulletsPos[spawnIdx].y = bulletImgs[spawnIdx].destRec.y;
      }
    }


    //update the location of Bullets
    for (int i = 0; i < bulletImgs.length; i++)
    {
      if (bulletImgs[i].GetVisible())
      {
        bulletsPos[i].x = bulletsPos[i].x + bulletSpeed;
        bulletImgs[i].destRec.x = (int)bulletsPos[i].x;
      }
    }

    BulletsWallCollision();



    //Deactivate any complete explosion animations
    for (int i = 0; i < explosions.length; i++)
    {
      if (explosions[i].destRec.x != INACTIVE.x)
      {
        if (explosions[i].IsAnimating() == false)
        {
          explosions[i].destRec.x = (int)INACTIVE.x;
          explosions[i].destRec.y = (int)INACTIVE.y;
        }
      }
    }






    //Every specific time to generate Circle enemy
    if (timer % circleFrequency == 0)
    {
      //Find the first inactive enemy
      spawnIdx = FindInactiveCircleEnemies(circleEnemies);

      //If an inactive enemy was found, spawn it and randomize its location and speed
      if (spawnIdx != NO_OBJECT)
      {
        circleEnemies[spawnIdx].SetTransparency(1f);
        circleEnemies[spawnIdx].TranslateTo(Helper.RandomValue( windowWidth- BIG_RAD,windowWidth),windowHeight-BIG_RAD-50);
        circleEnemies[spawnIdx].SetRad(BIG_RAD);

      }

    }

    //Move each active enemy to the left of the screen at its own random speed
    for (int i = 0; i < circleEnemies.length; i++)
    {
      //Only move active enemies
      if (circleEnemies[i].GetTransparency() > 0)
      {
        circleEnemies[i].Translate(-enemySpeed ,0);
      }
    }

    //If an active enemy leaves the left of the screen, deactivate it and reduce score
    for (int i = 0; i < circleEnemies.length; i++)
    {
      //Only collide active enemies
      if (circleEnemies[i].GetTransparency() > 0)
      {
        //Has the enemy completely passed the left edge
        if (circleEnemies[i].GetCentre().x + circleEnemies[i].GetRad() <= 0)
        {
          circleEnemies[i].SetTransparency(0);
        }
      }
    }

    /////////////////////
    // Handle Collisions
    /////////////////////

    //Collision detection
    //Enemies vs player
    //If an active enemy hits the player, deactivate it and reduce health
    for (int i = 0; i < circleEnemies.length; i++)
    {
      //Only collied active enemies
      if (circleEnemies[i].GetTransparency() > 0)
      {
        //Does the current enemy intersect the player
        if (Helper.Intersects(circleEnemies[i], playerImg[playerState].destRec))
        {
          //Deactivate the enemy
          circleEnemies[i].SetTransparency(0);

          //reduce health based on the size of the enemy
          if (circleEnemies[i].GetRad() == BIG_RAD)
          {
            health -= 1;
          }
          else
          {
            health = health - 1;
          }
        }
      }
    }

    //Bullets vs Enemies
    //Compare each active bullet against each active enemy looking for a collision
    for (int i = 0; i < bulletImgs.length; i++)
    {
      //Only compare active bullets
      if (bulletImgs[i].GetVisible())
      {
        //Compare the current bullet against EACH active enemy
        for (int j = 0; j < circleEnemies.length; j++)
        {
          //Only compare the active enemies
          if (circleEnemies[j].GetTransparency() > 0)
          {
            //Collied the current bullet with the current enemy
            if (Helper.Intersects(circleEnemies[j], bulletImgs[i].destRec))
            {
              //On the first hit reduce size, the second hit deactivates the bullet and adds score
              if (circleEnemies[j].GetRad() == BIG_RAD)
              {
                circleEnemies[j].SetRad(SMALL_RAD);
              }
              else
              {
                circleEnemies[j].SetTransparency(0);
                //TODO: Start explosion at weakEnemies[j] destRec
                TriggerExplosion((int)circleEnemies[j].GetCentre().x, (int)circleEnemies[j].GetCentre().y);
                //10 points for each obstacle dealt with (dodged, destroyed, etc.)
                score = score + KILL_PTS;

              }
            }
          }
        }
      }
    }

    //Every specific time to generate Rectangle1 enemy

    if (timer % rectangles1Frequency == 0)
    {
      //Find the first inactive enemy
      spawnIdx = FindInactiveRectangleEnemies(rectangles1Enemies);

      //If an inactive enemy was found, spawn it and randomize its location and speed
      if (spawnIdx != NO_OBJECT)
      {
        rectangles1Enemies[spawnIdx].SetTransparency(1f);
        rectangles1Enemies[spawnIdx].TranslateTo(windowWidth- rectangles1Enemies[spawnIdx].GetLeft(),windowHeight-80 );
      }
    }

    //Every specific time to generate Rectangle2 enemy

    if (timer % rectangles2Frequency == 0)
    {
      //Find the first inactive enemy
      spawnIdx = FindInactiveRectangleEnemies(rectangles2Enemies);

      //If an inactive enemy was found, spawn it and randomize its location and speed
      if (spawnIdx != NO_OBJECT)
      {
        rectangles2Enemies[spawnIdx].SetTransparency(1f);
        rectangles2Enemies[spawnIdx].TranslateTo(windowWidth- rectangles2Enemies[spawnIdx].GetLeft(),windowHeight-(int)playerImg[WALK].destRec.getHeight());
      }
    }


    //Move each active enemy to the left of the screen at its own random speed
    for (int i = 0; i < rectangles2Enemies.length; i++)
    {
      //Only move active enemies
      if (rectangles2Enemies[i].GetTransparency() > 0)
      {
        rectangles2Enemies[i].Translate(-enemySpeed ,0);
      }
    }

    //If an active enemy leaves the left of the screen, deactivate it and reduce score
    for (int i = 0; i < rectangles2Enemies.length; i++)
    {
      //Only collide active enemies
      if (rectangles2Enemies[i].GetTransparency() > 0)
      {
        //Has the enemy completely passed the left edge
        if (rectangles2Enemies[i].GetCentre().x + rectangles2Enemies[i].GetRec().getWidth() <= 0)
        {
          rectangles2Enemies[i].SetTransparency(0);
        }
      }
    }

    //Move each active enemy to the left of the screen at its own random speed
    for (int i = 0; i < rectangles1Enemies.length; i++)
    {
      //Only move active enemies
      if (rectangles1Enemies[i].GetTransparency() > 0)
      {
        rectangles1Enemies[i].Translate(-enemySpeed ,0);
      }
    }

    //If an active enemy leaves the left of the screen, deactivate it and reduce score
    for (int i = 0; i < rectangles1Enemies.length; i++)
    {
      //Only collide active enemies
      if (rectangles1Enemies[i].GetTransparency() > 0)
      {
        //Has the enemy completely passed the left edge
        if (rectangles1Enemies[i].GetCentre().x + rectangles1Enemies[i].GetRec().getWidth() <= 0)
        {
          rectangles1Enemies[i].SetTransparency(0);
        }
      }
    }



    if (health < 1 || (roundNum > 4)){
      EndGame();
    }




    //4: All enemies destroyed and game is not over
    //condition1 : not game end
    if ((gameState != ENDGAME) && (timer == 3000000) && (health >= 1) && (roundNum <4))
    {
      //Player won the round
      //endRoundText.UpdateText(endRoundMsg + roundNum);
      roundNum = Math.min(roundNum + 1, MAX_ROUND);
      gameState = ENDROUND;
      //condition2 : game end
    }




  }

  private void ScrollScreen(SpriteSheet img1, SpriteSheet img2, Vector2F pos1, Vector2F pos2, float speed)
  {
    //Move the true positions of the screens in the set direction
    pos1.x = pos1.x + (xDir * speed);
    pos2.x = pos2.x + (xDir * speed);

    //NOTE: We are using an if else if statement here because it can only ever
    //be one of the scenarios at a time and we don't want to separate left from right
    //because the left may shift it to the right and then the right may unintentially
    //go into its if statement as well (if it were not an if else-if)

    //When a background image goes off screen shift it to the other side
    if (pos1.x < -windowWidth)
    {
      //First image has moved off the screen left
      pos1.x = pos1.x + (2 * windowWidth);
    }
    else if (pos2.x < -windowWidth)
    {
      //Second image has moved off the screen left
      pos2.x = pos2.x + (2 * windowWidth);
    }
    else if (pos1.x > windowWidth)
    {
      //First image has moved off the screen right
      pos1.x = pos1.x - (2 * windowWidth);
    }
    else if (pos2.x > windowWidth)
    {
      //Second image has moved off the screen right
      pos2.x = pos2.x - (2 * windowWidth);
    }

    //Now that the true position has settled, set the draw position
    img1.destRec.x = (int)pos1.x;
    img2.destRec.x = (int)pos2.x;
  }


  //Pre: object is non-null SpriteSheet, with a location at truePos and
  //     given speed components in speed
  //Post: The object will be moved from its relative position by speed
  //Desc: Apply the speed to the objects true position and then approximated for its draw
  private void MoveGameObject(SpriteSheet object, Vector2F truePos, Vector2F speed)
  {
    //Add the speed components to the object's true position
    truePos.x = truePos.x + speed.x;
    truePos.y = truePos.y + speed.y;

    //Set the object's drawn position to rounded down true position
    object.destRec.x = (int)truePos.x;
    object.destRec.y = (int)truePos.y;
  }

  //Pre: object is non-null SpriteSheet array, with a location at truePos and
  //     given speed components in speed
  //Post: The objects will be moved from their relative position by speed
  //Desc: Apply the speed to the objects true position and then approximated for its draw
  private void MoveGameObjects(SpriteSheet [] objects, Vector2F truePos, Vector2F speed)
  {
    //Add the speed components to the object's true position
    truePos.x = truePos.x + speed.x;
    truePos.y = truePos.y + speed.y;

    //Set all of the object's drawn positions to rounded down true position
    for (int i = 0; i < objects.length; i++)
    {
      objects[i].destRec.x = (int)truePos.x;
      objects[i].destRec.y = (int)truePos.y;
    }
  }

  //Pre: object is non-null SpriteSheet array, with a location at truePos and
  //     x,y represent the target coordinate
  //Post: The objects will be moved to the position x,y
  //Desc: Relocate the objects to the specified coordinate
  private void MoveGameObjectsTo(SpriteSheet [] objects, Vector2F truePos, float x, float y)
  {
    //Set the true position to the given point
    truePos.x = x;
    truePos.y = y;

    //Set all of the object's drawn positions to rounded down true position
    for (int i = 0; i < objects.length; i++)
    {
      objects[i].destRec.x = (int)truePos.x;
      objects[i].destRec.y = (int)truePos.y;
    }
  }

  //Pre: None
  //Post: None
  //Desc: Detect wall collision with the player and stop their movement and keep them on screen
  private void PlayerWallCollision()
  {
    //If the player hits the side walls, pull them in bounds and stop their horizontal movement
    if (playerImg[playerState].destRec.x < 0)
    {
      MoveGameObjectsTo(playerImg, playerPos, 0, playerPos.y);
      playerSpeed.x = 0;
    }
    else if (playerImg[playerState].destRec.x + playerImg[playerState].destRec.width > windowWidth)
    {
      MoveGameObjectsTo(playerImg, playerPos, windowWidth - playerImg[playerState].destRec.width, playerPos.y);
      playerSpeed.x = 0;
    }

    //If the player hits the top/bottom walls, pull them in bounds and stop their vertical movement
    if (playerImg[playerState].destRec.y < 0)
    {
      MoveGameObjectsTo(playerImg, playerPos, playerPos.x, 0);
      playerSpeed.y = 0;
    }
    else if (playerImg[playerState].destRec.y + playerImg[playerState].destRec.height >= windowHeight)
    {
      //Readjust the player to be standing directly on the ground
      MoveGameObjectsTo(playerImg, playerPos, playerPos.x, windowHeight - playerImg[playerState].destRec.height);
      playerSpeed.y = 0f;

      //Only switch back to walk state if player is landing from a jump
      //As they may already be in the WALK or DUCK states
      if (playerState == JUMP)
      {
        playerState = WALK;
        playerImg[WALK].StartAnimation();
      }

      //The player just landed on the ground
      grounded = true;
    }
    else
    {
      //The player is off the ground, either jumping or falling
      playerState = JUMP;
      playerImg[WALK].StopAnimation();
      grounded = false;
    }
  }


  private void BulletsWallCollision(){
    for (int i = 0; i < bulletImgs.length; i++)
    {
      if (bulletImgs[i].GetVisible())
      {
        if (bulletImgs[i].destRec.x >= windowWidth)
        {
          //Deactivate the bullet
          bulletImgs[i].SetVisible(false);
          bulletImgs[i].StopAnimation();
        }
      }
    }

  }

  //bullets and enemies
  private int FindInactiveObject(SpriteSheet [] objects)
  {
    for (int i = 0; i < objects.length; i++)
    {
      if (objects[i].GetVisible() == false)
      {
        return i;
      }
    }

    return NO_OBJECT;
  }

  private int FindInactiveCircleEnemies(GameCircle[] enemies)
  {
    //Check every enemy in the array
    for (int i = 0; i < enemies.length; i++)
    {
      //If the current enemy is inactive, return its index
      if (enemies[i].GetTransparency() == 0)
      {
        return i;
      }
    }

    //No inactive enemy was found
    return NO_OBJECT;
  }

  private int FindInactiveRectangleEnemies(GameRectangle[] enemies)
  {
    //Check every enemy in the array
    for (int i = 0; i < enemies.length; i++)
    {
      //If the current enemy is inactive, return its index
      if (enemies[i].GetTransparency() == 0)
      {
        return i;
      }
    }

    //No inactive enemy was found
    return NO_OBJECT;
  }


  //Pre: x and y are valid screen coordinates, obj is the object being moved and loc is its true position
  //Post: None
  //Desc: Move obj to the given location
  private void SetObjectPosition(float x, float y, SpriteSheet obj, Vector2F loc)
  {
    //Set the object's true position
    loc.x = x;
    loc.y = y;

    //Set the objects drawn position based on its true position
    obj.destRec.x = (int)loc.x;
    obj.destRec.y = (int)loc.y;
  }





  //Pre: deltaX and deltaY are relative movement amounts, obj is the object being moved and loc is its true position
  //Post: None
  //Desc: Move obj by a given change in x and change in y
  private void MoveObject(float deltaX, float deltaY, SpriteSheet obj, Vector2F loc)
  {
    //Move the object relative to its current location
    SetObjectPosition(loc.x + deltaX, loc.y + deltaY, obj, loc);
  }

  //Pre: objects are a set of images with at least one image, locs are their true positions
  //Post: None
  //Desc: Loops through and deactivates all given objects
  private void SetObjectsInactive(SpriteSheet[] objects, Vector2F[] locs)
  {
    //Loop through each object and set its position to the inactive location
    for (int i = 0; i < objects.length; i++)
    {
      SetObjectPosition(INACTIVE.x, INACTIVE.y, objects[i], locs[i]);
    }
  }



  //Pre: objects is set of images with at least one image, numObjects indicates how many images to check
  //Post: Returns the index of the first inactive object in the list, -1 if all are currently active
  //Desc: Scans through the collection looking for an object not in the inactive location
  private int FindInactiveIndex(SpriteSheet [] objects, int numObjects)
  {
    //Search through each SpriteSheet
    for (int i = 0; i < numObjects; i++)
    {
      //If the SpriteSheet is stored at the inactive location, return its index
      if (objects[i].destRec.x == INACTIVE.x && objects[i].destRec.y == INACTIVE.y)
      {
        return i;
      }
    }

    //No inactive object exists, return INVALID
    return INVALID;
  }



  //Pre: gc is the Game controller
  //Post: None
  //Desc: Handle End of Round logic
  private void UpdateEndRound(GameContainer gc)
  {
    //Reset the round on the ENTER key
    if (Input.IsKeyReleased(KeyEvent.VK_ENTER))
    {
      SetupRound();
      gameState = GAMEPLAY;
    }
  }



  //Pre: gc is the Game controller
  //Post: None
  //Desc: Handle End of Game logic
  private void UpdateEndGame(GameContainer gc)
  {
    //Reset the game on the ENTER key
    if(Input.IsKeyReleased(KeyEvent.VK_ENTER))
    {
      ResetGame();
      gameState = MENU;
    }
  }

  //Pre: None
  //Post: None
  //Desc: Set all game data except round and score back to their starting values
  private void SetupRound()
  {
    //Track data used for placing enemies evenly on the screen
    int spacing;
    int posX;
    int posY;

    //Store the index of a game object to be activated
    int index = INVALID;

    timer = 0;
    //set circle enemy frequency
    circleFrequency -= 100000;
    rectangles1Frequency -= 100000;
    rectangles2Frequency -= 100000;



    int spawnIdx = NO_OBJECT;
    xDir = RIGHT;

    //Reset bullets
    SetObjectsInactive(bulletImgs, bulletsPos);

    //Deactivate all explosions
    for (int i = 0; i < explosions.length; i++)
    {
      explosions[i].StopAnimation();
      explosions[i].destRec.x = (int)INACTIVE.x;
      explosions[i].destRec.y = (int)INACTIVE.y;
    }


  }


  //Pre: x and y are valid coordinates on the screen
  //Post: None
  //Desc: An explosion animation is set to begin at the given location
  private void TriggerExplosion(int x, int y)
  {
    //Find the first available explosion animation
    int index = FindInactiveIndex(explosions, explosions.length);

    //Activate the explosion if possible
    if (index >= 0)
    {
      explosions[index].destRec.x = x;
      explosions[index].destRec.y = y;
      explosions[index].StartAnimation(1);
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
    Draw.Sprite(gfx, bg1Img);
    Draw.Sprite(gfx, bg2Img);
    Draw.Sprite(gfx,playerImg[playerState]);
    DrawActiveObjects(gfx, bulletImgs);

    //Draw each active enemy
    for (int i = 0; i < circleEnemies.length; i++)
    {
      if (circleEnemies[i].GetTransparency() > 0)
      {
        circleEnemies[i].Draw(gfx);
      }
    }


    for (int i = 0; i <  rectangles1Enemies.length; i++)
    {
      if ( rectangles1Enemies[i].GetTransparency() > 0)
      {
        rectangles1Enemies[i].Draw(gfx);
      }
    }

    for (int i = 0; i <  rectangles2Enemies.length; i++)
    {
      if ( rectangles2Enemies[i].GetTransparency() > 0)
      {
        rectangles2Enemies[i].Draw(gfx);
      }
    }



    //Draw Explosions
    for (int i = 0; i < explosions.length; i++)
    {
      if (explosions[i].destRec.x != INACTIVE.x)
      {
        Draw.Sprite(gfx, explosions[i]);
      }
    }



    Draw.Text(gfx,gameName,titleTxtLoc.x,titleTxtLoc.y,hudTitleFont,Helper.DEEPBLUE,0.6f);

    Draw.Text(gfx,scoreMsg , scoreTxtLoc.x, scoreTxtLoc.y, hudDataFont, Helper.BLACK, 1f);
    Draw.Text(gfx, score + " pts.", scoreTxtLoc.x + 75, scoreTxtLoc.y, hudDataFont, Helper.BLACK, 1f);
    Draw.Text(gfx,healthMsg,healthTxtLoc.x, healthTxtLoc.y, msgFont1, Helper.BLACK,1f);
    Draw.Text(gfx, health + "/3", healthTxtLoc.x + 75, healthTxtLoc.y, hudDataFont, Helper.BLACK, 1f);
    Draw.Text(gfx,timerMsg,timerTxtLoc.x,timerTxtLoc.y,hudDataFont, Helper.BLACK, 1f );
    Draw.Text(gfx,(int)(timer/SECOND) + " sec.",timerTxtLoc.x + 75,timerTxtLoc.y,hudDataFont, Helper.BLACK, 1f );

  }


  private void DrawActiveObjects(Graphics2D gfx, SpriteSheet[] objects)
  {
    //Loop through each SpriteSheet
    for (int i = 0; i < objects.length; i++)
    {
      //Only draw the objects not at the inactive location
      //if(objects[i].destRec.x != INACTIVE.x)
      //{
      Draw.Sprite(gfx, objects[i]);
      //}
    }
  }
  //Pre: gfx is the window being drawn to
  //Post: None
  //Desc: Draw the End Round state
  private void DrawEndRound(Graphics2D gfx)
  {
    //Draw Background images
    Draw.Sprite(gfx, bg1Img);
    Draw.Sprite(gfx, bg2Img);

    //Draw HUD Elements
    Draw.Text(gfx, endRoundMsg + (roundNum - 1), windowWidth-650, windowHeight / 2, msgFont2, Helper.DEEPBLUE, 1f);
    Draw.Text(gfx, continueMsg, windowWidth/2, windowHeight - 50, msgFont2, Helper.DEEPBLUE, 1f);
  }

  //Pre: gfx is the window being drawn to
  //Post: None
  //Desc: Draw the End Game state
  private void DrawEndGame(Graphics2D gfx)
  {
    //Draw Background images
    Draw.Sprite(gfx, bg1Img);
    Draw.Sprite(gfx, bg2Img);


    //Draw HUD Elements
    Draw.Text(gfx, endScoreMsg + score, 190, windowHeight / 2, msgFont1, Helper.YELLOW, 1f);

    //Tell the user if they earned the high score!
    if (score == highScore)
    {
      Draw.Text(gfx, newHighScoreMsg, 170, windowHeight / 2 + 50, msgFont1, Helper.WHITE, 1f);
      Draw.Text(gfx, newHighScoreMsg, 168, windowHeight / 2 + 48, msgFont1, Helper.MAGENTA, 1f);
    }

    //Display Enter to continue message
    Draw.Text(gfx, returnMenuMsg, 160, windowHeight - 30, msgFont1, Helper.RED, 1f);
  }


  //Pre: gc is the Game controller
  //Post: None
  //Desc: Reset all game data to their original values and positions
  private void ResetGame()
  {
    score = 0;
    roundNum = 1;
    SetupRound();

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