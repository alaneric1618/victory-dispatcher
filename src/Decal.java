import java.awt.*;

public class Decal extends Entity {

    public enum Type {
	FIRE,
	SMOKE
    }

    private Type type;
    private int tick = 0;
    private boolean isDestroyedBool = false;

    public Decal(Type type, int x, int y) {
	this.type = type;
	this.boundingSprite = new Rectangle(x, y, 32, 32);
	spriteSize = 2.0f;
    }

    public boolean isDestroyed() {
	return isDestroyedBool;
    }

    public void draw(Graphics2D g) {
	if (isDestroyedBool) return;
	if (tick <= 17 && type == Type.FIRE) {
	    tick += 3;
	} else if (tick <= 46 && type == Type.SMOKE) {
	    tick += 1;
	} else {
	    isDestroyedBool = true;
	}
	switch (type) {
	case FIRE:
	    drawSprite(g, (34-tick)*(32), 8*(32)+7, 32, 32);
	    break;
	case SMOKE:
	    drawSprite(g, (34+tick)*(32), 8*(32)+7, 32, 32);
	    break;
	}
    }
    
}
