

// The model represents all the actual content and functionality of the game
// For Breakout, it manages all the game objects that the View needs
// (the bat, ball, bricks, and the score), provides methods to allow the Controller
// to move the bat (and a couple of other functions - change the speed or stop 
// the game), and runs a background process (a 'thread') that moves the ball 
// every 20 milliseconds and checks for collisions 

import java.io.File;
import javafx.scene.paint.*;
import javafx.application.Platform;
import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;

public class Model 
{
    // First,a collection of useful values for calculating sizes and layouts etc.

    public int B              = 6;      // Border round the edge of the panel
    public int M              = 40;     // Height of menu bar space at the top

    public int BALL_SIZE      = 30;     // Ball side
    public int BRICK_WIDTH    = 50;     // Brick size
    public int BRICK_HEIGHT   = 30;

    public int BAT_MOVE       = 5;      // Distance to move bat on each keypress
    public int BALL_MOVE      = 3;      // Units to move the ball on each step

    public int HIT_BRICK      = 50;     // Score for hitting a brick
    public int HIT_BOTTOM     = -200;   // Score (penalty) for hitting the bottom of the screen

    // The other parts of the model-view-controller setup
    View view;
    Controller controller;

    // The game 'model' - these represent the state of the game
    // and are used by the View to display it
    public GameObj ball;                // The ball
    public ArrayList<Brick> bricks;     // The bricks
    public GameObj bat;                 // The bat
    public int score = 0;               // The score
    public int brickCount = 0;          // Game state, end when it reaches 0

    // variables that control the game 
    public String gameState = "running";// Set to "finished" to end the game
    public int gameLevel = 3;           // 
    public boolean fast = false;        // Set true to make the ball go faster

    // initialisation parameters for the model
    public int width;                   // Width of game
    public int height;                  // Height of game
    
    // Music/sounds
    public String musicPath = "C:\\Users\\Student\\OneDrive - University of Brighton\\Module Documents\\CI401\\Semester 2\\Coursework\\sounds\\The-Army-of-Minotaur.mp3";
    public Media media = new Media(new File(musicPath).toURI().toString());
    public MediaPlayer mediaPlayer = new MediaPlayer(media);
    
    public String ballfxPath = "C:\\Users\\Student\\OneDrive - University of Brighton\\Module Documents\\CI401\\Semester 2\\Coursework\\sounds\\ballbounce.wav";
    public AudioClip ballBounce = new AudioClip(new File(ballfxPath).toURI().toString());

    // CONSTRUCTOR - needs to know how big the window will be
    public Model( int w, int h )
    {
        Debug.trace("Model::<constructor>");  
        width = w; 
        height = h;


    }

    
    // Animating the game
    // The game is animated by using a 'thread'. Threads allow the program to do 
    // two (or more) things at the same time. In this case the main program is
    // doing the usual thing (View waits for input, sends it to Controller,
    // Controller sends to Model, Model updates), but a second thread runs in 
    // a loop, updating the position of the ball, checking if it hits anything
    // (and changing direction if it does) and then telling the View the Model 
    // changed.
    
    // When we use more than one thread, we have to take care that they don't
    // interfere with each other (for example, one thread changing the value of 
    // a variable at the same time the other is reading it). We do this by 
    // SYNCHRONIZING methods. For any object, only one synchronized method can
    // be running at a time - if another thread tries to run the same or another
    // synchronized method on the same object, it will stop and wait for the
    // first one to finish.
    
    // Start the animation thread
    public void startGame()
    {
        initialiseGame();                           // set the initial game state
        Thread t = new Thread( this::runGame );     // create a thread running the runGame method
        t.setDaemon(true);                          // Tell system this thread can die when it finishes
        t.start();                                  // Start the thread running
    }   
    
    // Initialise the game - reset the score and create the game objects 
    public void initialiseGame()
    {       
        score = 0;
        ball   = new GameObj(width/2, height/2, BALL_SIZE, BALL_SIZE, Color.RED );
        bat    = new GameObj(width/2, height - BRICK_HEIGHT*3/2, BRICK_WIDTH*3, 
            BRICK_HEIGHT/4, Color.GRAY);
        bricks = new ArrayList<Brick>();
        
        for (int i = 10; i >=0; i--) {
           bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 100, BRICK_WIDTH, BRICK_HEIGHT, 0 ));
           bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 60, BRICK_WIDTH, BRICK_HEIGHT, 1 ));
        }
        
        /*for (Brick brick: bricks) {
            brick.getStyleClass().add("brick");
        }*/
        
        
        brickCount = bricks.size();
        
        //mediaPlayer.setAutoPlay(true);
        
        // *[1]******************************************************[1]*
        // * Fill in code to make the bricks array                      *
        // **************************************************************
        
        
    }

    
    // The main animation loop
    public void runGame()
    {
        try
        {
            Debug.trace("Model::runGame: Game starting"); 
            // set game true - game will stop if it is set to "finished"
            setGameState("running");
            while (!getGameState().equals("finished"))
            {
                updateGame();                        // update the game state
                modelChanged();                      // Model changed - refresh screen
                Thread.sleep( getFast() ? 10 : 20 ); // wait a few milliseconds
            }
            Debug.trace("Model::runGame: Game finished"); 
        } catch (Exception e) 
        { 
            Debug.error("Model::runAsSeparateThread error: " + e.getMessage() );
        }
    }
  
    // updating the game - this happens about 50 times a second to give the impression of movement
    public synchronized void updateGame()
    {
        // move the ball one step (the ball knows which direction it is moving in)
        ball.moveX(BALL_MOVE);                      
        ball.moveY(BALL_MOVE);
        // get the current ball possition (top left corner)
        int x = ball.topX;  
        int y = ball.topY;
        // Deal with possible edge of board hit
        if (x >= width - B - BALL_SIZE) {ball.changeDirectionX(); ballBounce.play();}
        if (x <= 0 + B)  {ball.changeDirectionX(); ballBounce.play();}
        if (y >= height - B - BALL_SIZE)  // Bottom
        { 
            ball.changeDirectionY(); 
            addToScore( HIT_BOTTOM );     // score penalty for hitting the bottom of the screen
            ballBounce.play();
        }
        if (y <= 0 + M)  {ball.changeDirectionY(); ballBounce.play();}
        
        //bricks.add(new GameObj(width/2, height/2, BRICK_WIDTH, BRICK_HEIGHT, Color.BLUE ));
        
       // check whether ball has hit a (visible) brick
        boolean hit = false;
        
        for (Brick brick: bricks) {
            if ((brick.hitByTop(ball)&&brick.hitBySide(ball)) && brick.visible && brick.dur == 0) {
                brick.visible = false;
                hit = true;
                addToScore( HIT_BRICK );
                brickCount--;
            }
            else if ((brick.hitByTop(ball)&&brick.hitBySide(ball)) && brick.visible && brick.dur > 0) {
                hit = true;
                brick.dur--;
                switch(brick.dur) {
                    case 0:
                        brick.colour = Color.BLUE;
                        break;
                    case 1:
                        brick.colour = Color.GREEN;
                        break;
                    case 2:
                        brick.colour = Color.RED;
                        break;
                }
                addToScore( HIT_BRICK );
            }
        }
        
        // *[3]******************************************************[3]*
        // * Fill in code to check if a visible brick has been hit      *
        // * The ball has no effect on an invisible brick               *
        // * If a brick has been hit, change its 'visible' setting to   *
        // * false so that it will 'disappear'                          * 
        // **************************************************************
        
        if (brickCount == 0) {
            setGameLevel(--gameLevel);
            bricks.clear();
            ball.topX = width/2;
            ball.topY = height/2;
            
            switch (gameLevel) {
            case 1:
                for (int i = 10; i >=0; i--) {
                    bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 100, BRICK_WIDTH, BRICK_HEIGHT, 0 ));
                    bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 60, BRICK_WIDTH, BRICK_HEIGHT, 1 ));
                }
                brickCount = bricks.size();
                break;
            
            case 2:
                for (int i = 10; i >=0; i--) {
                    bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 100, BRICK_WIDTH, BRICK_HEIGHT, 1));
                    bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 60, BRICK_WIDTH, BRICK_HEIGHT, 2));
                }
                brickCount = bricks.size();
                break;
                
            case 3:
                for (int i = 10; i >=0; i--) {
                    bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 140, BRICK_WIDTH, BRICK_HEIGHT, 2));
                    bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 100, BRICK_WIDTH, BRICK_HEIGHT, 1));
                    bricks.add(new Brick((BRICK_WIDTH*i)+3*i+10, 60, BRICK_WIDTH, BRICK_HEIGHT, 2));
                }
                brickCount = bricks.size();
                break;
            }
        }
        
        if (hit) {
            ball.changeDirectionY();
            ballBounce.play();
        }
        // check whether ball has hit the bat
        
        if ( ball.hitByTop(bat) && ball.hitBySide(bat)) {
            ball.changeDirectionY();
            ballBounce.play();
        }
        
        if ( ball.hitBySide(bat) && (ball.topY >= height - BRICK_HEIGHT*3/2)) {
            ball.changeDirectionX();
            ballBounce.play();
        }
        
        /*if ( ball.hitBySide(bat) && (ball.topY+height>755 && ball.topY<762)) {
            ball.topY = 725;
        }*/
    }

    // This is how the Model talks to the View
    // Whenever the Model changes, this method calls the update method in
    // the View. It needs to run in the JavaFX event thread, and Platform.runLater 
    // is a utility that makes sure this happens even if called from the
    // runGame thread
    public synchronized void modelChanged()
    {
        Platform.runLater(view::update);
    }
    
    
    // Methods for accessing and updating values
    // these are all synchronized so that the can be called by the main thread 
    // or the animation thread safely
    
    // Change game state - set to "running" or "finished"
    public synchronized void setGameState(String value)
    {  
        gameState = value;
    }
    
    // Return game running state
    public synchronized String getGameState()
    {  
        return gameState;
    }
    
    /** Setter method for the gameLevel */
    public synchronized void setGameLevel(int value)
    {
        gameLevel = value;
        if (gameLevel == 0) {
            setGameState("finished");
        }
    }
    
    /** Getter method for the gameLevel */
    public synchronized int getGameLevel()
    {
        return gameLevel;
    }

    // Change game speed - false is normal speed, true is fast
    public synchronized void setFast(Boolean value)
    {  
        fast = value;
    }
    
    // Return game speed - false is normal speed, true is fast
    public synchronized Boolean getFast()
    {  
        return(fast);
    }

    // Return bat object
    public synchronized GameObj getBat()
    {
        return(bat);
    }
    
    // return ball object
    public synchronized GameObj getBall()
    {
        return(ball);
    }
    
    // return bricks
    public synchronized ArrayList<Brick> getBricks()
    {
        return(bricks);
    }
    
    // return score
    public synchronized int getScore()
    {
        return(score);
    }
    
     // update the score
    public synchronized void addToScore(int n)    
    {
        score += n;        
    }
    
    // move the bat one step - -1 is left, +1 is right
    public synchronized void moveBat( int direction )
    {        
        int dist = direction * BAT_MOVE;    // Actual distance to move
        Debug.trace( "Model::moveBat: Move bat = " + dist );
        bat.moveX(dist);
    }
}   
    