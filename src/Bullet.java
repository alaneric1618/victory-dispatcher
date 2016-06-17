import java.awt.*;

public class Bullet extends Entity {

  private Tank.Player player;
  private double x;
  private double y;
  public double time = 0;
  final public double angle;

  public Bullet(Tank.Player player, double x, double y, double angle) {
    super();
    this.player = player;
    spriteSize = 1.6f;
    this.x = x;
    this.y = y;
    this.angle = angle;
    boundingBox = new Rectangle((int) x + 4, (int) y + 4, 8, 8);
    boundingSprite = new Rectangle((int) x, (int) y, 16, 16);
  }

  public Tank.Player getPlayer() {
    return player;
  }

  public void update(float dt) {
    super.update(dt);
    time += dt;
    x += 9.0 * Math.cos(Math.toRadians(angle));
    y += 9.0 * Math.sin(Math.toRadians(angle));
    boundingBox = new Rectangle((int) x + 4, (int) y + 4, 8, 8);
    boundingSprite = new Rectangle((int) x, (int) y, 16, 16);
  }

  public void draw(Graphics2D g) {
    super.draw(g);
    int index = (int) (((angle + 270 + (3600)) % 360) / 7.5);
    drawSprite(g, 64, index, 1, -37, -32);
  }

}
