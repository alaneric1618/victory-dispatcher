import java.awt.*;

public class Decal extends Entity {

    public enum Type {
	FIRE,
	SMOKE,
        DEBRIS,
        BLAST
    }

    private Type type;
    private double tick = 0;
    private boolean isDestroyedBool = false;
    private double dir = 0.0;
    private double speed = 0.0;
    private double tickSpeed = 1.0;
    private double z = 0.5;
    private double wait = 0.0;
    private int x;
    private int y;


    public Decal(Type type, int x, int y) {
        this(type, x, y, 0.0);
    }

    public Decal(Type type, int x, int y, double angle) {
        this.x = x;
        this.y = y;
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
        } else if (type == Type.DEBRIS) {
            dir = 2.0*Math.PI*Math.random();
            this.boundingSprite = new Rectangle(x+28, y+28, 32, 32);
            spriteSize = (float)z;
            speed = 1+2*Math.random();
            wait = 6*Math.random();
        } else if (type == Type.BLAST) {
            this.boundingSprite = new Rectangle(x-32, y-32, 32, 32);
            spriteSize = 4.0f;
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
        } else if (tick <= 28 && type == Type.DEBRIS) {
            double dt = 0.4+(speed/15.0);
            wait -= dt;
            if (wait < 0.0) {
                tick += dt;
                z = Math.abs((-(tick*tick)+30*tick)/600.0)+0.2;
                spriteSize = (float)z;
            }
	} else if (tick <= 80 && type == Type.BLAST) {
	    tick += 0.7;
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
        case DEBRIS:
            if (wait < 0.0) {
                Rectangle r2 = boundingSprite;
                this.boundingSprite = new Rectangle((int)(r2.x+speed*Math.cos(dir)), (int)(r2.y+speed*Math.sin(dir)), 32, 32);
                drawSprite(g, ((int)82+(int)((tick+(2*speed))%8))*(34)+10, 8*(32)+7, 31, 32); //56 X
            }
            break;
        case BLAST:
            if (tick < 19) {
                this.boundingSprite = new Rectangle(x-32, y-32, 32, 32);
                drawSprite(g, (34-(int)(tick%18))*(32), 8*(32)+7, 32, 32);
            } 
            if (tick > 12 && tick < 30){
                this.boundingSprite = new Rectangle(x-32+12, y-32+12, 32, 32);
                drawSprite(g, (34-(int)((tick-12)%18))*(32), 8*(32)+7, 32, 32);
            }
            if (tick > 22 && tick < 40){
                this.boundingSprite = new Rectangle(x-32-12, y-32-12, 32, 32);
                drawSprite(g, (34-(int)((tick-22)%18))*(32), 8*(32)+7, 32, 32);
            }
            break;
	}
    }
    
}
