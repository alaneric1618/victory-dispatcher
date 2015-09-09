import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.Graphics2D;

public class Entity {

    //This is a single sprite map which is available to any object in the room.
    //The prefered way to draw from the sprite map is with the drawSprite method.
    public static BufferedImage spriteMap;
    static {
	try {
	    spriteMap = ImageIO.read(new File("./media/vd_master_sheet.png")); //Frames to animate
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    //The bounding box is used for collision detection, updating and drawing.
    protected Rectangle boundingBox = new Rectangle();
    protected Rectangle boundingSprite = new Rectangle();
    protected float spriteSize = 1.0f;
    protected long maxAge = Long.MAX_VALUE;
    protected long age = 0L;

    public Entity() {

    }

    public Rectangle getBoundingBox() {
	return boundingBox;
    }

    public boolean isOld() {
        return (age > maxAge);
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public long getAge() {
        return age;
    }

    //Used for collision detection.
    //Note the polymorphic ability of a child of the Entity.
    public boolean intersects(Entity other) {
	Rectangle otherRect = other.boundingBox;
	Rectangle rect = this.boundingBox;
	return rect.intersects(otherRect);
    }

    public void update(float dt) {
        age += (long)dt;
    }

    public void draw(Graphics2D g) {
	if (VD.DEBUG) {
	    g.setColor(Color.red);
	    g.draw(boundingBox);
	    g.setColor(Color.white);
	    g.draw(boundingSprite);	    
	}
    }

    // drawSprite
    //    This is the easiest way to draw a sprite from the spritemap.
    //    g - graphics context
    //    size - the standard size desired from spritemap.
    //           This is because a single spritemap may have various standard sizes.
    //    i - tile column number
    //    j - tile row number
    public void drawSprite(Graphics2D g, int i, int j, int w, int h) {
	int offset = 4;
	double x = boundingSprite.getX();
	double y = boundingSprite.getY();
	g.clipRect((int)x, (int)y, (int)(w*spriteSize), (int)(h*spriteSize));
	g.drawImage(Entity.spriteMap, new AffineTransform(spriteSize, 0f , 0f , spriteSize, (x-(i*spriteSize)), (y-(j*spriteSize))), null);
	g.setClip(new Rectangle(0, 0, VD.WIDTH, VD.HEIGHT));
    }

    // drawSprite
    //    This is the easiest way to draw a sprite from the spritemap.
    //    g - graphics context
    //    size - the standard size desired from spritemap.
    //           This is because a single spritemap may have various standard sizes.
    //    i - tile column number
    //    j - tile row number
    public void drawSprite(Graphics2D g,int size, int i, int j) {
	int offset = 4;
	double x = boundingSprite.getX();
	double y = boundingSprite.getY();
	g.clipRect((int)x, (int)y, (int)(size*spriteSize), (int)(size*spriteSize));
	g.drawImage(Entity.spriteMap, new AffineTransform(spriteSize, 0f , 0f , spriteSize, x-offset-(i*size*spriteSize), y-offset-1-(j*size*spriteSize)), null);
	g.setClip(new Rectangle(0, 0, VD.WIDTH, VD.HEIGHT));
    }

    // drawSprite
    //    This is the easiest way to draw a sprite from the spritemap.
    //    g - graphics context
    //    size - the standard size desired from spritemap.
    //           This is because a single spritemap may have various standard sizes.
    //    i - tile column number
    //    j - tile row number
    //    offsetX (or Y) - Relative offset to draw in pixels from bounding box.
    public void drawSprite(Graphics2D g, int size, int i, int j, int offsetX, int offsetY) {
	int offset = 4;
	double x = boundingSprite.getX();
	double y = boundingSprite.getY();
	g.clipRect((int)x+offsetX, (int)y+offsetY, (int)(size*spriteSize), (int)(size*spriteSize));
	g.drawImage(Entity.spriteMap, new AffineTransform(spriteSize, 0f , 0f , spriteSize, x-offset-(i*size*spriteSize)+offsetX, y-offset-1-(j*size*spriteSize)+offsetY), null);
	g.setClip(new Rectangle(0, 0, VD.WIDTH, VD.HEIGHT));
    }


}
