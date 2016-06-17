import java.awt.*;

public class Block extends Entity {

  public enum Type {
    H, V, B, UL, UR, DL, DR, ALL
  };

  private Block.Type type;
  private int h;
  private int w;
  private int i;
  private int xoff = 0;
  private int rubble = (int) (7 * Math.random()) + 7;
  private boolean isDestroyedBool = false;

  public Block(Type type, int gridX, int gridY) {
    super();
    this.type = type;
    switch (type) {
      case H:
        h = 20;
        w = 32;
        i = 2;
        break;
      case V:
        h = 32;
        w = 16;
        i = 3;
        break;
      case B:
        h = 20;
        w = 32;
        i = 2;
        break;
      case UL:
        h = 26;
        w = 23;
        i = 0;
        xoff = 3;
        break;
      case UR:
        h = 26;
        w = 23;
        i = 1;
        xoff = -3;
        break;
      case DL:
        h = 20;
        w = 23;
        i = 5;
        xoff = -3;
        break;
      case DR:
        h = 20;
        w = 23;
        i = 4;
        xoff = 3;
        break;
      case ALL:
        h = 26;
        w = 32;
        i = 6;
        break;
    }
    this.boundingBox =
        new Rectangle(32 * gridX + (16 - (w / 2)) + xoff, 32 * gridY + (32 - h), w, h);
    this.boundingSprite = new Rectangle(32 * gridX, 32 * gridY, 32, 32);
  }

  public boolean isDestroyed() {
    return isDestroyedBool;
  }

  public void destroy() {
    isDestroyedBool = true;
    this.boundingBox = new Rectangle(0, 0, 0, 0);
  }

  public void draw(Graphics2D g) {
    super.draw(g);
    if (!isDestroyedBool) {
      drawSprite(g, i * (32), 8 * (32) + 7, 32, 32);
    } else {
      drawSprite(g, rubble * (32), 8 * (32), 32, 32);
    }
  }

}
