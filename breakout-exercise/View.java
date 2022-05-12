

// The View class creates and manages the GUI for the application.
// It doesn't know anything about the game itself, it just displays
// the current state of the Model, and handles user input

// We import lots of JavaFX libraries (we may not use them all, but it
// saves us having to thinkabout them if we add new code)
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.util.*;

import java.util.HashMap;

public class View implements EventHandler<KeyEvent>
{ 
    // variables for components of the user interface
    public int width;       // width of window
    public int height;      // height of window

    // user interface objects
    public Pane pane;       // basic layout pane
    // public Canvas canvas;   // canvas to draw game on
    public Label infoText;  // info at top of screen

    // The other parts of the model-view-controller setup
    public Controller controller;
    public Model model;

    // Data we access from the Model
    public GameObj   bat;            // The bat
    public GameObj   ball;           // The ball
    /** An array that contains all the brick objects for the level */
    public ArrayList<Brick> bricks;  // The bricks
    public int       score =  0;     // The score
    
    // For each GameObj instance we get from the model, we create a Rectangle object
    // to display on the screen. We use a HashMap to store these Rectangle objects
    // so we can use the same one each time
    public HashMap<GameObj, Rectangle> rectangleStore = new HashMap<>();
    
    // constructor method - we get told the width and height of the window
    public View(int w, int h)
    {
        Debug.trace("View::<constructor>");
        width = w;
        height = h;
    }

    // start is called from the Main class, to start the GUI up
    
    public void start(Stage window) 
    {
        // breakout is basically one big drawing canvas, and all the objects are
        // drawn on it as rectangles, except for the text at the top - this
        // is a label which sits 'in front of' the canvas.
        
        // Note that it is important to create control objects (Pane, Label etc) 
        // here not in the constructor (or as initialisations to instance variables),
        // to make sure everything is initialised in the right order
        pane = new Pane();       // a simple layout pane
        pane.setId("Breakout");  // Id to use in CSS file to style the pane if needed
        
        // infoText box for the score - a label which we position at the top of the Pane
        // to show the current score
        infoText = new Label("BreakOut: Score = " + score);
        infoText.setTranslateX(50);  // these commands set the position of the text box
        infoText.setTranslateY(10);  // (measuring from the top left corner)
        
        // Make a new JavaFX Scene, containing the complete GUI
        Scene scene = new Scene(pane, width, height);   
        scene.getStylesheets().add("breakout.css"); // tell the app to use our css file

        // Add an event handler for key presses. By using 'this' (which means 'this 
        // view object itself') we tell JavaFX to call the 'handle' method (below)
        // whenever a key is pressed
        scene.setOnKeyPressed(this);

        // put the scene in the window and display it
        window.setScene(scene);
        window.show();
    }

    // Event handler for key presses - it just passes the event to the controller
    public void handle(KeyEvent event)
    {
        // send the event to the controller
        controller.userKeyInteraction( event );
    }
    
    // drawing the game image
    public void drawPicture()
    {
        // the game loop is running 'in the background' so we have
        // add the following line to make sure it doesn't change
        // the model in the middle of us updating the image
        synchronized ( model ) 
        {
            // remove all the children from the pane ('clear the screen')
            pane.getChildren().clear();
            // update the score display string with the new score 
            infoText.setText("BreakOut: Score = " + score);
            // add the label to the Pane
            pane.getChildren().add(infoText);  
            
            // draw the bat and ball
            displayGameObj( ball );   // Display the Ball
            displayGameObj( bat  );   // Display the Bat
            
            for (Brick brick: bricks) {
                if (brick.visible) {
                    displayGameObj(brick);
                }
            } 
            
            // *[2]****************************************************[2]*
            // * Display the bricks that make up the game                 *
            // * Fill in code to display bricks from the brick array      *
            // * Remember only a visible brick is to be displayed         *
            // ************************************************************
            
        }
    }

    // Display a game object - create a Rectangle object (if necessary) and
    // add it to the Pane in the right position
    public void displayGameObj( GameObj go )
    {
        Rectangle s;
        // check whether we have seen this GameObject before
        if (rectangleStore.containsKey(go) ) {
            // yes we have - so we already have a Rectangle object for it in the store
            s = rectangleStore.get(go);
            // *****************
            s.setFill(go.colour);
        } else {
            // no we haven't, so make one and add it to the store (for next time)
            s = new Rectangle();
            // set properties for it that are not going to change
            s.setFill(go.colour);
            s.setWidth(go.width);
            s.setHeight(go.height);
            // add it to the pane
            rectangleStore.put(go,s);
        }
        // set the position of rectangle
        s.setX(go.topX);
        s.setY(go.topY);
        // and add it to the Pane
        pane.getChildren().add(s);
    }

    // This is how the Model talks to the View
    // This method gets called BY THE MODEL, whenever the model changes
    // It has to do whatever is required to update the GUI to show the new game position
    public void update()
    {
        // Get from the model the ball, bat, bricks & score
        ball    = model.getBall();              // Ball
        bricks  = model.getBricks();            // Bricks
        bat     = model.getBat();               // Bat
        score   = model.getScore();             // Score
        //Debug.trace("Update");
        drawPicture();                     // Re draw game
    }
}
