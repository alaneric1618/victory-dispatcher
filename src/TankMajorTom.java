import java.awt.Rectangle;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class TankMajorTom extends Tank implements TankInterface {

  {
    name = "Major Tom";
    try {
      icon = ImageIO.read(VD.class.getResourceAsStream("/media/tom.png")); // Frames to animate
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  double time = 0.0;
  double timeSinceCheck = 0.0;
  double elapsedTimeCheck = 300.0;
  double aggro = 0.5;
  int state = 1;  /// 1-scan   2-manuver  3-engage
  double timeInScan = 0.0;
  double timeInManuver = 0.0;
  double timeInEngage = 0.0;
  ArrayList<Rectangle> blocks = new ArrayList<Rectangle>();
  VisibleEntity firstTank;
  double timeSinceFirstTank;
  VisibleEntity secondTank;
  double timeSinceSecondTank;
  VisibleEntity closestBullet;
  double timeSinceBullet;
  Rectangle attackPriority;
  Rectangle defensePriority;

  public void onCreation() {
    
  }

  public void onHit() {

  }

  public void loop(float dt) {
    time += (double) dt;
    timeSinceCheck += dt;
    timeSinceFirstTank += dt;
    timeSinceSecondTank +=dt;
    timeSinceBullet += dt;
    // Do an inventory of where all the known objects are at
    if (timeSinceCheck > elapsedTimeCheck) {
      timeSinceCheck = 0.0;
      check();
    }
    // Project the movements of all known objects
    projectMovements();
    if (state <= 1) {
      scan(dt);
    } else if (state == 2) {
      manuver(dt);
    } else if (state >= 3) {
      engage(dt);
    }
  }
  
  public void scan(double dt) {
    this.talk("One");
    this.timeInScan += dt;
    if (this.firstTank != null || this.secondTank != null) {
      state = 3;
      timeInScan = 0.0;
      return;
    }
    if (timeInScan > 3000.0) {
      state = 2;
      timeInScan = 0.0;
      return;
    }
    this.turnTurret(3.5, false);
    this.turnTread(-3.0, false);
  }
  
  public void manuver(double dt) {
    this.talk("Two");
    this.timeInManuver += dt;
    if (timeInManuver > 1500.0) {
      this.fire();
      this.turnTurretTo((int)(640*Math.random()), (int)(480*Math.random()));
      timeInManuver = 0.0;
    }
    if (this.closestBullet != null) {
      Rectangle r1 = this.getBoundingBox();
      Rectangle r2 = closestBullet.rect;
      double dist = Math.sqrt( (r2.x-r1.x)*(r2.x-r1.x) + (r2.y-r1.y)*(r2.y-r1.y)   );
      if (dist < 20) {
        this.forward();
      }
    }
  }
  
  public void engage(double dt) {
    this.talk("Three");
    this.timeInEngage += dt;
  }
  
  public void projectMovements() {
    if (firstTank != null) {
      VisibleEntity e = firstTank;
      Rectangle r = e.rect;
      firstTank.rect = new Rectangle(r.x + (int)(e.speed*Math.cos(Math.toRadians(e.dir))), r.y + (int)(e.speed*Math.sin(Math.toRadians(e.dir))), r.width, r.height);
    }
    if (secondTank != null) {
      VisibleEntity e = secondTank;
      Rectangle r = e.rect;
      secondTank.rect = new Rectangle(r.x + (int)(e.speed*Math.cos(Math.toRadians(e.dir))), r.y + (int)(e.speed*Math.sin(Math.toRadians(e.dir))), r.width, r.height);
    }
    if (closestBullet != null) {
      VisibleEntity e = closestBullet;
      Rectangle r = e.rect;
      closestBullet.rect = new Rectangle(r.x + (int)(e.speed*Math.cos(Math.toRadians(e.dir))), r.y + (int)(e.speed*Math.sin(Math.toRadians(e.dir))), r.width, r.height);
    }
  }
  
  public void check() {
    VisibleEntity firstClosest = null;
    VisibleEntity secondClosest = null;
    VisibleEntity minBullet = null;
    for (VisibleEntity ent : this.getVisibleEntities()) {
      // BLOCKS
      if (ent.type == VisibleEntity.Type.BLOCK) {
        if (!containsBlock(ent.rect) && !ent.isDestroyed) {
          blocks.add(ent.rect);
        }
        if (containsBlock(ent.rect) && ent.isDestroyed) {
          blocks.remove(ent.rect);
        }
      }
      if (ent.type == VisibleEntity.Type.TANK) {
        Rectangle r1 = this.getBoundingBox();
        Rectangle r2 = ent.rect;
        double dist = Math.sqrt( (r2.x-r1.x)*(r2.x-r1.x) + (r2.y-r1.y)*(r2.y-r1.y)   );
        if (firstClosest == null && secondClosest == null) {
          firstClosest = ent;
          secondClosest = ent;
        } else {
          Rectangle r3 = firstClosest.rect;
          Rectangle r4 = secondClosest.rect;
          double dist1 = Math.sqrt( (r2.x-r1.x)*(r2.x-r1.x) + (r2.y-r1.y)*(r2.y-r1.y)   );
          double dist2 = Math.sqrt( (r2.x-r1.x)*(r2.x-r1.x) + (r2.y-r1.y)*(r2.y-r1.y)   );
          if (dist < dist1) { // closer than first
            secondClosest = firstClosest;
            firstClosest = ent;
          } else if (dist < dist2) { // closer than second
            secondClosest = ent;
          }
        }
      }
      if (ent.type == VisibleEntity.Type.BULLET) {
        if (minBullet == null) {
          minBullet = ent;
        } else {
          Rectangle r  = this.getBoundingBox();
          Rectangle r1 = ent.rect;
          Rectangle r2 = minBullet.rect;
          double dist1 = Math.sqrt( (r1.x-r.x)*(r1.x-r.x) + (r1.y-r.y)*(r1.y-r.y)   );
          double dist2 = Math.sqrt( (r2.x-r.x)*(r2.x-r.x) + (r2.y-r.y)*(r2.y-r.y)   );
          if (dist1 < dist2) {
            minBullet = ent;
          }
        }
      }
    }
    if (firstClosest != null) {
      this.firstTank = firstClosest;
      this.timeSinceFirstTank = 0.0;
    }
    if (secondClosest != null) {
      this.secondTank = secondClosest;
      this.timeSinceSecondTank = 0.0;
    }
    if (minBullet != null) {
      this.closestBullet = minBullet;
      this.timeSinceBullet = 0.0;
    }
  }
  
  public boolean containsBlock(Rectangle r) {
    for (Rectangle block : blocks) {
      if (block.intersects(r)) {
        return true;
      }
    }
    return false;
  }  
  
  
  
}
