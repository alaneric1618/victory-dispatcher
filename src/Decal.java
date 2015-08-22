import java.awt.*;

public class Decal extends Entity {

    public enum Type {
	FIRE,
	SMOKE
    }

    private Type type;
    private double tick = 0;
    private boolean isDestroyedBool = false;
    private double dir = 0.0;
    private double speed = 0.0;
    private double tickSpeed = 1.0;


    public Decal(Type type, int x, int y) {
        this(type, x, y, 0.0);
    }

    public Decal(Type type, int x, int y, double angle) {
	this.type = type;
	this.boundingSprite = new Rectangle(x, y, 32, 32);
	spriteSize = 2.0f;
        if (type == Type.SMOKE) {
            spriteSize = (float)(0.5f + 1.0*Math.random());
            tickSpeed = 0.5+Math.random();
            if (Math.random() < 0.9) {
                dir = Math.toRadians(angle) + 0.75*Math.PI*Math.random() - 1.375*Math.PI;
            } else {
                dir = Math.toRadians(angle);
            }
            speed = 6+6*Math.random();
        }
    }

    public boolean isDestroyed() {
	return isDestroyedBool;
    }

    public void draw(Graphics2D g) {
	if (isDestroyedBool) return;
	if (tick <= 17 && type == Type.FIRE) {
	    tick += 3;
	} else if (tick <= 46 && type == Type.SMOKE) {
	    tick += tickSpeed;
	} else {
	    isDestroyedBool = true;
	}
	switch (type) {
	case FIRE:
	    drawSprite(g, (34-(int)tick)*(32), 8*(32)+7, 32, 32);
	    break;
	case SMOKE:
            Rectangle r = boundingSprite;
            if (speed > 0) {
                speed = speed/1.1;
            }
            this.boundingSprite = new Rectangle((int)(r.x+speed*Math.cos(dir)), (int)(r.y+speed*Math.sin(dir)), 32, 32);
	    drawSprite(g, (34+(int)tick)*(32), 8*(32)+7, 32, 32);
	    break;
	}
    }
    
}
