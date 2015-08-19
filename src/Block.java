import java.awt.*;

public class Block extends Entity {

    public Block(int gridX, int gridY) {
        super();
        this.boundingBox = new Rectangle(16*gridX, 16*gridY, 16, 16);
	this.boundingSprite = new Rectangle(16*gridX, 16*gridY, 16, 16);
    }

    public void draw(Graphics2D g) {
        super.draw(g);
    }
    
}

