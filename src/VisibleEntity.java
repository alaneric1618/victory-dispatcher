import java.awt.*;

public class VisibleEntity {

  public enum Type {
    TANK, BLOCK, BULLET
  }

  public enum Side {
    GOOD, BAD, NEUTRAL
  }

  public Type type;
  public Side side;
  public Rectangle rect;
  public double dir;
  public double turretDir;
  public double speed;
  public boolean isDestroyed;

  public VisibleEntity(Type type, Side side, Rectangle rect, double dir, double turretDir,
      double speed, boolean isDestoryed) {
    this.type = type;
    this.side = side;
    this.rect = rect;
    this.dir = dir;
    this.turretDir = turretDir;
    this.speed = speed;
    this.isDestroyed = false;
  }
}
