
import javafx.scene.paint.Color;

/** Class for creating brick game objects. This allows a durability to be set for bricks with corresponding colours, which change as the bricks take hits. */
public class Brick extends GameObj
{
    
    /** Standard brick contructor with parameters for setting the position, width, height and colour. */
    public Brick( int x, int y, int w, int h, Color c )
    {
        topX   = x;       
        topY = y;
        width  = w; 
        height = h; 
        colour = c;
    }
    
    /** Brick contructor with parameter z, for setting brick durability. */
    public Brick( int x, int y, int w, int h, int z)
    {
        topX = x;       
        topY = y;
        width  = w; 
        height = h;
        dur = z;
        colour = Color.BLUE;
        
        switch(dur) {
            case 0:
                colour = Color.BLUE;
                break;
            case 1:
                colour = Color.GREEN;
                break;
            case 2:
                colour = Color.RED;
                break;
        }
    }
}
