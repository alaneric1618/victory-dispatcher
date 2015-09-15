import java.awt.*;
import java.awt.geom.*;

public class VisibleEntity {

    public enum Type {
	TANK,
	BLOCK,
	BULLET
    }
    
    public enum Side {
	GOOD,
	BAD,
	NEUTRAL
    }

    public final Type type;
    public final Side side;
    public final Rectangle rect;
    public final double dir;
    public final double turretDir;
    public final double speed;

    public VisibleEntity(
			 Type type,
			 Side side,
			 Rectangle rect,
			 double dir,
			 double turretDir,
			 double speed
			 ) {
	this.type = type;
	this.side = side;
	this.rect = rect;
	this.dir = dir;
	this.turretDir = turretDir;
	this.speed = speed;
    }
}
