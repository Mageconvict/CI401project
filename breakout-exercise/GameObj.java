
// An object in the game, represented as a rectangle, with a position,
// a size, a colour and a direction of movement.

// Watch out for the different spellings of Color/colour - the class uses American
// spelling, but we have chosen to use British spelling for the instance variable!

// import the JavaFX Color class
import javafx.scene.paint.Color;

public class GameObj
{
    // state variables for a game object
    /** States whether the game objec is visible */
    public boolean visible  = true;     // Can be seen on the screen (change to false when the brick gets hit)
    /** The top left X coordinate of the object */
    public int topX   = 0;              // Position - top left corner X
    public int topY   = 0;              // position - top left corner Y
    /** The width of the object */
    public int width  = 0;              // Width of object
    /** The height of the object */
    public int height = 0;              // Height of object
    /** The colour of the object */
    public Color colour;                // Colour of object
    /** The X direction of the object (0 if stationary) */
    public int   dirX   = 2;            // Direction X (1, 0 or -1)
    /** The Y direction of the object (0 if stationary) */
    public int   dirY   = 1;            // Direction Y (1, 0 or -1)
    
    public int dur = 0;                 // 'Durability' of the object - no. of hits needed to break brick

    public GameObj()
    {
    
    }
    
    public GameObj( int x, int y, int w, int h, Color c )
    {
        topX   = x;       
        topY = y;
        width  = w; 
        height = h; 
        colour = c;
    }
    
    public GameObj( int x, int y, int w, int h, Color c, int z )
    {
        topX   = x;       
        topY = y;
        width  = w; 
        height = h; 
        colour = c;
        dur = z;
    }

    // move in x axis
    public void moveX( int units )
    {
        topX += units * dirX;
    }

    // move in y axis
    public void moveY( int units )
    {
        topY += units * dirY;
    }

    // change direction of movement in x axis (-1, 0 or +1)
    public void changeDirectionX()
    {
        dirX = -dirX;
    }

    // change direction of movement in y axis (-1, 0 or +1)
    public void changeDirectionY()
    {
        dirY = -dirY;
    }

    // Detect collision between this object and the argument object
    // It's easiest to work out if they do NOT overlap, and then
    // return the opposite
   /* public boolean hitBy( GameObj obj )
    {
        boolean separate =  
            topX >= obj.topX+obj.width     ||    // '||' means 'or'
            topX+width <= obj.topX         ||
            topY >= obj.topY+obj.height    ||
            topY+height <= obj.topY        ;
        
        // use ! to return the opposite result - hitBy is 'not separate')
        return(! separate);  
          
    } */
    
    public boolean hitByTop( GameObj obj )
    {
        boolean separate =
            topY >= obj.topY+obj.height    ||
            topY+height <= obj.topY        ;
            
        return(! separate);
    }
    
    public boolean hitBySide( GameObj obj )
    {
        boolean separate =
            topX >= obj.topX+obj.width     ||
            topX+width <= obj.topX         ;
            
        return(! separate);
    }
    
   /* public boolean colliding( GameObj obj )
    {
        boolean overlap =
            topX >= obj.topX && topX <= obj.topX+obj.width              ||
            topX+width >= obj.topX && topX+width <= obj.topX+obj.width  &&
            topY >= obj.topY && topY <= obj.topY+obj.height             ||
            topY+height >= obj.topY && topY+height <= obj.topY+obj.height ;
            
        return (!overlap);
    }*/

}
