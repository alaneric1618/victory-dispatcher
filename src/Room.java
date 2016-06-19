import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Room {

  public static BufferedImage spriteMap;
  public static BufferedImage hp;
  public static BufferedImage hp1;
  public static BufferedImage hp2;
  public static BufferedImage hp3;
  public static BufferedImage hp4;
  static {
    try {
      spriteMap = ImageIO.read(VD.class.getResourceAsStream("/media/lot.png")); // Frames to animate
      spriteMap = Util.convertImageToNative(spriteMap);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      hp = ImageIO.read(VD.class.getResourceAsStream("/media/side.png")); // Frames to animate
      hp1 = ImageIO.read(VD.class.getResourceAsStream("/media/side.png")); // Frames to animate
      hp2 = ImageIO.read(VD.class.getResourceAsStream("/media/side.png")); // Frames to animate
      hp3 = ImageIO.read(VD.class.getResourceAsStream("/media/side.png")); // Frames to animate
      hp4 = ImageIO.read(VD.class.getResourceAsStream("/media/side.png")); // Frames to animate
      hp =  Util.convertImageToNative(hp);
      hp1 = Util.convertImageToNative(hp1);
      hp2 = Util.convertImageToNative(hp2);
      hp3 = Util.convertImageToNative(hp3);
      hp4 = Util.convertImageToNative(hp4);
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (int x = 0; x < hp1.getWidth(); x++) {
      for (int y = 0; y < hp1.getHeight(); y++) {
        int alpha = (hp1.getRGB(x, y) >> 24);
        if (alpha != 0) {
          hp1.setRGB(x, y, Color.cyan.getRGB());
          hp2.setRGB(x, y, Color.magenta.getRGB());
          hp3.setRGB(x, y, Color.yellow.getRGB());
          hp4.setRGB(x, y, Color.white.getRGB());
        }
      }
    }
  }

  VD vd;
  boolean isLoading = true;
  double loadingTime = 0;
  ArrayList<Entity> scene = new ArrayList<Entity>();
  HashMap<Tank.Player, Tank> tanks = new HashMap<Tank.Player, Tank>();
  HashMap<Tank.Player, Double> hps = new HashMap<Tank.Player, Double>();
  ArrayList<Block> blocks = new ArrayList<Block>();
  ArrayList<Bullet> bullets = new ArrayList<Bullet>();
  ArrayList<Decal> decals = new ArrayList<Decal>();
  ArrayList<Text> texts = new ArrayList<Text>();
  double shownHP1 = 70.0;
  double shownHP2 = 70.0;
  double shownHP3 = 70.0;
  double shownHP4 = 70.0;
  boolean isWinner = false;
  Tank.Player winner = null;
  double winnerTime = 0.0;
  Font font = new Font("SansSerif", Font.PLAIN, 1);
  public Rectangle roomRect = new Rectangle(0, 0, vd.WIDTH - 32, vd.HEIGHT - 64);

  public Room(VD vd, Tank... tanks) {
    this.vd = vd;
    for (Tank tank : tanks) {
      if (tank != null) {
        System.out.println(tank.getName());
      }
      add(tank);
    }
    for (int i = 2; i < 9; i++) {
      if (i != 5) {
        Block block = new Block(Block.Type.H, 4 + i, 6);
        blocks.add(block);
        Block block2 = new Block(Block.Type.V, 9, 1 + i);
        blocks.add(block2);
      } else {
        Block block = new Block(Block.Type.ALL, 9, 6);
        blocks.add(block);
      }
      Block block = new Block(Block.Type.DL, 9, 2);
      blocks.add(block);
      block = new Block(Block.Type.DR, 9, 2);
      blocks.add(block);
      block = new Block(Block.Type.H, 10, 2);
      blocks.add(block);
      block = new Block(Block.Type.H, 8, 2);
      blocks.add(block);

      block = new Block(Block.Type.UL, 9, 10);
      blocks.add(block);
      block = new Block(Block.Type.UR, 9, 10);
      blocks.add(block);
      block = new Block(Block.Type.H, 10, 10);
      blocks.add(block);
      block = new Block(Block.Type.H, 8, 10);
      blocks.add(block);
    }
  }

  public void update(float dt) {
    if (isLoading) {
      if (loadingTime > 1.0) {
        isLoading = false;
      }
      loadingTime += 0.15;
      return;
    }
    synchronized (bullets) {
      ArrayList<Bullet> toRemoveBullets = new ArrayList<Bullet>();
      for (Bullet bullet : bullets) {
        Rectangle box = bullet.getBoundingBox();
        if (bullet.time > 5000) {
          toRemoveBullets.add(bullet);
        }
        bullet.update(dt);
        // collision detect bullet with screen
        if (box.x < 8 || box.x > vd.WIDTH - 10 || box.y < 0 || box.y > vd.HEIGHT - 72) {
          toRemoveBullets.add(bullet);
          synchronized (decals) {
            decals.add(new Decal(Decal.Type.FIRE, box.x - 32, box.y - 32));
          }
          AudioPlayer.EXPLOSION.play();
          for (int i = 0; i < 20; i++) {
            int xr = (int) (10 * Math.random()) + 11;
            int yr = (int) (10 * Math.random()) + 11;
            synchronized (decals) {
              decals.add(new Decal(Decal.Type.SMOKE, box.x - 32 + xr, box.y - 32 + yr, bullet.angle));
            }
          }
        }
        // collision detect bullet w/ blocks
        for (Block block : blocks) {
          if (bullet.intersects(block)) {
            toRemoveBullets.add(bullet);
            block.destroy();
            synchronized (decals) {
              decals.add(new Decal(Decal.Type.FIRE, box.x - 32, box.y - 32));
            }
            AudioPlayer.EXPLOSION.play();
            for (int i = 0; i < 20; i++) {
              int xr = (int) (10 * Math.random()) + 11;
              int yr = (int) (10 * Math.random()) + 11;
              synchronized (decals) {
                decals.add(new Decal(Decal.Type.SMOKE, box.x - 32 + xr, box.y - 32 + yr, bullet.angle));
              }
            }
          }
        }
        // collision detect bullet w/ tanks
        synchronized (tanks) {
          ArrayList<Tank.Player> playersToRemove = new ArrayList<Tank.Player>();
          for (Tank tank : tanks.values()) {
            if (tank == null)
              continue;
            if (bullet.intersects(tank) && bullet.getPlayer() != tank.getPlayer()) {
              toRemoveBullets.add(bullet);
              for (int i = 0; i < 20; i++) {
                int xr = (int) (10 * Math.random()) + 11;
                int yr = (int) (10 * Math.random()) + 11;
                synchronized (decals) {
                  decals.add(new Decal(Decal.Type.SMOKE, box.x - 32 + xr, box.y - 32 + yr, bullet.angle));
                }
                if (i % 6 == 0) {
                  synchronized (decals) {
                    decals.add(new Decal(Decal.Type.DEBRIS, box.x - 32, box.y - 32, bullet.angle));
                  }
                }
              }
              AudioPlayer.EXPLOSION.play();
              synchronized (decals) {
                decals.add(new Decal(Decal.Type.FIRE, box.x - 32, box.y - 32));
              }
              Tank.Player player = tank.getPlayer();
              tank.onHit();
              double hp = (double) hps.get(player);
              // deduce health
              double newHP = hp - 23.0;
              if (newHP < 0.0)
                newHP = -1.0;
              hps.put(player, new Double(newHP));
              if (newHP < 0.0) { // dead
                playersToRemove.add(player);
                for (int i = 0; i < 20; i++) {
                  synchronized (decals) {
                    decals.add(new Decal(Decal.Type.DEBRIS, box.x - 32, box.y - 32, bullet.angle));
                  }
                }
                synchronized (decals) {
                  decals.add(new Decal(Decal.Type.BLAST, box.x - 32, box.y - 32, bullet.angle));
                }
              }
            }
            for (Tank.Player player : playersToRemove) {
              tanks.remove(player);
              this.checkForWinner();
            }

          }
        }
      }
      for (Bullet bullet : toRemoveBullets) {
        bullets.remove(bullet);
      }
    }
    synchronized (decals) {
      ArrayList<Decal> toRemoveDecals = new ArrayList<Decal>();
      for (Decal decal : decals) {
        if (decal.isDestroyed()) {
          toRemoveDecals.add(decal);
        }
      }
      for (Decal decal : toRemoveDecals) {
        decals.remove(decal);
      }
    }
    synchronized (tanks) {
      ArrayList<Tank.Player> playersToRemove = new ArrayList<Tank.Player>();
      for (Tank tank : tanks.values()) {
        if (tank == null)
          continue;
        tank.update(dt);
        if (tank.getAverageUpdateTimes() > 7) {
          synchronized (texts) {
            Text text = new Text("Too Long", 0.2);
            text.xStorage = (tank.getBoundingBox().x);
            text.yStorage = (tank.getBoundingBox().y);
            //texts.add(text);
          }
          playersToRemove.add(tank.getPlayer());
        }
      }
      for (Tank.Player player : playersToRemove) {
        tanks.remove(player);
        this.checkForWinner();
      }
    }
  }
  
  public void checkForWinner() {
    // search winner;
    if (getPlayerCount() <= 1) {
      isWinner = true;
      Tank.Player winnerVar = null;
      for (Tank.Player p : tanks.keySet()) {
        Tank potentialTank = tanks.get(p);
        if (potentialTank != null) {
          // winner found
          winnerVar = p;
        }
      }
      winner = winnerVar;
    }
  }

  public double getHP(Tank.Player player) {
    if (hps.containsKey(player)) {
      Double hp = hps.get(player);
      return hp;
    } else {
      return 0.0;
    }

  }

  public void setHP(Tank.Player player, double hp) {
    hps.put(player, new Double(hp));
  }

  public void add(Tank tank) {
    Tank.Player player = getNewPlayerEnum();
    if (tank != null) {
      tank.setRoom(this);
      hps.put(player, new Double(100));
    }
    tanks.put(player, tank);
  }

  public void add(Bullet bullet) {
    synchronized (bullets) {
      bullets.add(bullet);
    }
  }

  public void remove(Tank tank) {

  }

  public int getPlayerCount() {
    int count = 0;
    for (Tank tank : tanks.values()) {
      if (tank != null) {
        count++;
      }
    }
    return count;
  }

  public Tank.Player getNewPlayerEnum() {
    final int nplayers = tanks.size();
    switch (nplayers) {
      case 0:
        return Tank.Player.P1;
      case 1:
        return Tank.Player.P2;
      case 2:
        return Tank.Player.P3;
      case 3:
        return Tank.Player.P4;
      default:
        return Tank.Player.NONE;
    }
  }

  public void draw(Graphics2D g) {
    if (isLoading) {
      g.setColor(Color.black);
      g.setClip(new Rectangle(0, 0, vd.WIDTH, vd.HEIGHT));
      int x = 120;
      int y = 300;
      int w = 400;
      int h = 35;
      int i = (int) (w * (loadingTime));
      g.setColor(new Color(128, 128, 255));
      g.drawRect(x, y, w, h);
      g.setColor(new Color(50, 50, 180));
      g.fillRect(x + 1, y + 1, i - 2, h - 2);
      Font font = new Font("SansSerif", Font.PLAIN, (int) 48);
      g.setFont(font);
      g.drawString("loading", 240, 250);
      return;
    }
    // Draw background
    g.setColor(Color.black);

    g.setClip(new Rectangle(0, 0, vd.WIDTH, vd.HEIGHT));
    // System.out.println(spriteMap);
    g.drawImage(spriteMap, new AffineTransform(0.615f, 0f, 0f, 0.42f, -250.0, -180.0), null);
    // Draw blocks
    for (Block block : blocks) {
      block.draw(g);
    }
    // draw pause
    if (vd.paused) {

    }
    g.setColor(new Color(55, 55, 55));
    g.drawLine(0, vd.HEIGHT - 64, vd.WIDTH, vd.HEIGHT - 64);
    g.setColor(new Color(125, 125, 125));
    g.fillRect(0, vd.HEIGHT - 64, vd.WIDTH, 128);
    // DEBUG
    if (vd.DEBUG) {

    }
    synchronized (tanks) {
      for (Tank tank : tanks.values()) {
        if (tank == null)
          continue;
        tank.draw(g);
      }
    }
    synchronized (bullets) {
      for (Bullet bullet : bullets) {
        bullet.draw(g);
      }
    }
    synchronized (decals) {
      for (Decal decal : decals) {
        decal.draw(g);
      }
    }
    // HUD
    for (Tank.Player player : hps.keySet()) {
      double health = (double) hps.get(player);
      double shownHealth = 0.0;
      double diff = 0.0;
      g.setColor(player.getColor());
      int i = 0;
      BufferedImage img = null;
      switch (player) {
        case P1:
          i = 0 * 155 + 20;
          img = hp1;
          diff = Math.abs(shownHP1 - health);
          if (diff > 0.01 && shownHP1 > health)
            shownHP1 -= diff / 5.0;
          if (diff > 0.01 && shownHP1 < health)
            shownHP1 += diff / 5.0;
          shownHealth = shownHP1;
          break;
        case P2:
          i = 1 * 155 + 20;
          img = hp2;
          diff = Math.abs(shownHP2 - health);
          if (diff > 0.01 && shownHP2 > health)
            shownHP2 -= diff / 5.0;
          if (diff > 0.01 && shownHP2 < health)
            shownHP2 += diff / 5.0;
          shownHealth = shownHP2;
          break;
        case P3:
          i = 2 * 155 + 20;
          img = hp3;
          diff = Math.abs(shownHP3 - health);
          if (diff > 0.01 && shownHP3 > health)
            shownHP3 -= diff / 5.0;
          if (diff > 0.01 && shownHP3 < health)
            shownHP3 += diff / 5.0;
          shownHealth = shownHP3;
          break;
        case P4:
          i = 3 * 155 + 20;
          img = hp4;
          diff = Math.abs(shownHP2 - health);
          if (diff > 0.01 && shownHP4 > health)
            shownHP4 -= diff / 5.0;
          if (diff > 0.01 && shownHP4 < health)
            shownHP4 += diff / 5.0;
          shownHealth = shownHP4;
          break;
        default:
          break;
      }
      if (img != null) {
        int pixels = (int) (1.15 * (shownHealth / 2.0));
        g.drawImage(hp, new AffineTransform(1f, 0f, 0f, 1f, i, 400), null);
        g.clipRect(i - 1, 400, pixels + 4, 64);
        g.drawImage(img, new AffineTransform(1f, 0f, 0f, 1f, i, 400), null);
        // g.setClip(new Rectangle(-vd.WIDTH, -vd.HEIGHT, vd.WIDTH*2, vd.HEIGHT*2));
        g.setClip(new Rectangle(0, 0, vd.WIDTH, vd.HEIGHT));
      }
    }
    // VICTORY
    if (isWinner && !vd.paused) {
      winnerTime += 1.0;
      double prob = Math.random();
      if (prob < 0.05) {
        int xc = (int) (640 * Math.random());
        int yc = (int) (480 * Math.random());
        synchronized (decals) {
          decals.add(new Decal(Decal.Type.BLAST, xc - 32, yc - 32, 0.0));
        }
      }
    }
    double fontSize = winnerTime;
    if (winnerTime > 96.0) {
      fontSize = 96.0;
    }
    if (winner != null) {
      g.setColor(winner.getColor());
    }
    Font font = new Font("SansSerif", Font.PLAIN, (int) fontSize);
    g.setFont(font);
    int x = (int) (30 * Math.cos(winnerTime / 50.0));
    int y = (int) (10 * Math.cos(winnerTime / 15.0));
    g.drawString("WINNER", (int) (305 - 2 * fontSize) + x, 235 + y);
    // Extra Text
    synchronized (texts) {
      for (Text text : texts) {
        text.draw(g, (int)text.xStorage, (int)text.yStorage);
      }
    }
  }

  public Polygon getSight(Point p, double angle, double fov) {
    Polygon poly = new Polygon();
    poly.addPoint(p.x, p.y);
    int stepSize = 2;
    int i = 0;
    int count = (int) (fov / stepSize);
    if (fov < 2.0) {
      double x = p.x + 800 * Math.cos(Math.toRadians(angle));
      double y = p.y + 800 * Math.sin(Math.toRadians(angle));
      Line2D l = new Line2D.Double(p.x, p.y, x, y);
      for (Block block : blocks) {
        Rectangle r = block.boundingBox;
        if (l.intersects(r)) {
          double xt = r.x + r.width / 2.0;
          double yt = r.y + r.height / 2.0;
          double dist = Math.sqrt((xt - p.x) * (xt - p.x) + (yt - p.y) * (yt - p.y));
          x = p.x + dist * Math.cos(Math.toRadians(angle));
          y = p.y + dist * Math.sin(Math.toRadians(angle));
          l = new Line2D.Double(p.x, p.y, x, y);
        }
      }
      poly.addPoint((int) x, (int) y);
    } else {
      for (double theta = angle - (fov / 2); i < count; theta += stepSize) {
        double x = p.x + 800 * Math.cos(Math.toRadians(theta));
        double y = p.y + 800 * Math.sin(Math.toRadians(theta));
        Line2D l = new Line2D.Double(p.x, p.y, x, y);
        for (Block block : blocks) {
          Rectangle r = block.boundingBox;
          if (l.intersects(r)) {
            double xt = r.x + r.width / 2.0;
            double yt = r.y + r.height / 2.0;
            double dist = Math.sqrt((xt - p.x) * (xt - p.x) + (yt - p.y) * (yt - p.y));
            x = p.x + dist * Math.cos(Math.toRadians(theta));
            y = p.y + dist * Math.sin(Math.toRadians(theta));
            l = new Line2D.Double(p.x, p.y, x, y);
          }
        }
        poly.addPoint((int) x, (int) y);
        i++;
      }
    }
    return poly;
  }

  public boolean isLocationFree(Rectangle r) {
    boolean free = true;
    if (!roomRect.contains(r)) {
      return false;
    }
    for (int i = 0; i < blocks.size(); i++) {
      Block block = blocks.get(i);
      if (r.intersects(block.boundingBox)) {
        free = false;
      }
    }
    return free;
  }

  public HashSet<VisibleEntity> getVisibleEntities(Tank forTank, Polygon poly1, Polygon poly2) {
    HashSet<VisibleEntity> ents = new HashSet<VisibleEntity>();
    synchronized (tanks) {
      for (Tank tank : tanks.values()) {
        if (tank == null)
          continue;
        if (tank == forTank)
          continue;
        Rectangle box = tank.getBoundingBox();
        if (poly1.intersects(box) || poly2.intersects(box)) {
          VisibleEntity.Type type = VisibleEntity.Type.TANK;
          VisibleEntity.Side side;
          if (tank.getPlayer() == forTank.getPlayer()) {
            side = VisibleEntity.Side.GOOD;
          } else {
            side = VisibleEntity.Side.BAD;
          }
          Rectangle rect = box;
          double dir = tank.getDir();
          double turretDir = tank.getTurretDir();
          double speed = tank.getSpeed();
          VisibleEntity ent = new VisibleEntity(type, side, rect, dir, turretDir, speed, false);
          ents.add(ent);
        }
      }
    }
    synchronized (blocks) {
      for (Block block : blocks) {
        Rectangle box = block.getBoundingBox();
        if ((poly1.intersects(box) || poly2.intersects(box)) && !block.isDestroyed()) {
          VisibleEntity.Type type = VisibleEntity.Type.BLOCK;
          VisibleEntity.Side side = VisibleEntity.Side.NEUTRAL;
          Rectangle rect = box;
          double dir = 0.0;
          double turretDir = 0.0;
          double speed = 0.0;
          boolean isDestroyed = block.isDestroyed();
          VisibleEntity ent = new VisibleEntity(type, side, rect, dir, turretDir, speed, isDestroyed);
          ents.add(ent);
        }
      }
    }
    synchronized (bullets) {
      for (Bullet bullet : bullets) {
        Rectangle box = bullet.getBoundingBox();
        if (poly1.intersects(box) || poly2.intersects(box)) {
          VisibleEntity.Type type = VisibleEntity.Type.BULLET;
          VisibleEntity.Side side;
          if (bullet.getPlayer() == forTank.getPlayer()) {
            side = VisibleEntity.Side.GOOD;
          } else {
            side = VisibleEntity.Side.BAD;
          }
          Rectangle rect = box;
          double dir = bullet.angle;
          double turretDir = 0.0;
          double speed = bullet.getSpeed();
          VisibleEntity ent = new VisibleEntity(type, side, rect, dir, turretDir, speed, false);
          ents.add(ent);
        }
      }
    }
    return ents;
  }


}
