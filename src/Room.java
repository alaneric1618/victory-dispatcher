import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Room {

    public boolean paused = false;

    ArrayList<Entity> scene = new ArrayList<Entity>();
    ArrayList<Tank> tanks = new ArrayList<Tank>();
    ArrayList<Block> blocks = new ArrayList<Block>();
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    ArrayList<Decal> decals = new ArrayList<Decal>();
    // int x = 0;
    // int y = 0; //for debug
    //int frame = 0; //for debug
    //int globalCount = 0;
    //int dotTimer = 0;
    //boolean isGlobal = false;
    //public int xcoor;
    //public int ycoor;
    //public boolean buttonPress;
    public Rectangle roomRect = new Rectangle(0, 0, VD.WIDTH-32, VD.HEIGHT-64);

    public Room() {
        Tank player = new TankPlayer();
	Tank cpu = new TankMajorTom();
	player.setRoom(this);
	tanks.add(player);
	cpu.setRoom(this);	
	tanks.add(cpu);
	for (int i = 2; i < 9; i++) {
	    if (i != 5) {
		Block block = new Block(Block.Type.H, 4+i, 6);
		blocks.add(block);
		Block block2 = new Block(Block.Type.V,9, 1+i);
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
        synchronized (bullets) {
	    ArrayList<Bullet> toRemoveBullets = new ArrayList<Bullet>();
            for (Bullet bullet : bullets) {
		Rectangle box = bullet.getBoundingBox();		
                if (bullet.time > 3000) {
                    toRemoveBullets.add(bullet);
                }
                bullet.update(dt);
		if (box.x < 8 || box.x > VD.WIDTH-32
		    || box.y < 0 || box.y > VD.HEIGHT-72) {
		    toRemoveBullets.add(bullet);
		    decals.add(new Decal(Decal.Type.FIRE, box.x-32, box.y-32));
                    for (int i = 0; i < 5; i++) {
                        int xr = (int)(10*Math.random())+11;
                        int yr = (int)(10*Math.random())+11;
                        decals.add(new Decal(Decal.Type.SMOKE, box.x-32+xr, box.y-32+yr, bullet.angle));
                    }
		}
		//collision detect bullet w/ blocks
		for (Block block : blocks) {
		    if (bullet.intersects(block)) {
			toRemoveBullets.add(bullet);
			block.destroy();
                        decals.add(new Decal(Decal.Type.FIRE, box.x-32, box.y-32));
                        for (int i = 0; i < 5; i++) {
                            int xr = (int)(10*Math.random())+11;
                            int yr = (int)(10*Math.random())+11;
                            decals.add(new Decal(Decal.Type.SMOKE, box.x-32+xr, box.y-32+yr, bullet.angle));
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
	for (Tank tank : tanks) {
	    tank.update(dt);
	}
    }

    public int getTankCount() {
	return tanks.size();
    }

    public void addBullet(Bullet bullet) {
        synchronized (bullets) {
            bullets.add(bullet);
        }
    }

    public void draw(Graphics2D g) {
        //Draw background
        g.setColor(new Color(128, 128, 128));
        g.fillRect(0, 0, VD.WIDTH, VD.HEIGHT);
        //Draw blocks
        for (Block block : blocks) {
            block.draw(g);
        }
        //Draw pause
        if (paused) {
            Symbols pauseSymbols = new Symbols("paused", 130, 20);
            pauseSymbols.alignment = Symbols.Alignment.LEFT_JUSTIFIED;
            pauseSymbols.draw(g);
        }
	g.setColor(Color.black);
	g.drawLine(0, VD.HEIGHT-64, VD.WIDTH, VD.HEIGHT-64);
        //DEBUG
        if (VD.DEBUG) {
            
        }
	for (Tank tank : tanks) {
	    tank.draw(g);
	}
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
	for (Decal decal : decals) {
	    decal.draw(g);
	}
    }
    
    public Polygon getSight(Point p, double angle, double fov) {
	Polygon poly = new Polygon();
	poly.addPoint(p.x, p.y);
	int stepSize = 2;
	int i = 0;
	int count = (int)(fov/stepSize);
	for (double theta = angle-(fov/2); i < count; theta+=stepSize ) {
	    double x = p.x+800*Math.cos(Math.toRadians(theta));
	    double y = p.y+800*Math.sin(Math.toRadians(theta));
	    Line2D l = new Line2D.Double(p.x, p.y, x, y);
	    for (Block block : blocks) {
	    	Rectangle r = block.boundingBox;
	    	if (l.intersects(r)) {
		    double xt = r.x+r.width/2.0;
		    double yt = r.y+r.height/2.0;		    
		    double dist = Math.sqrt((xt-p.x)*(xt-p.x) + (yt-p.y)*(yt-p.y));
		    x = p.x+dist*Math.cos(Math.toRadians(theta));
		    y = p.y+dist*Math.sin(Math.toRadians(theta));
		    l = new Line2D.Double(p.x, p.y, x, y);
	    	}
		
	    }
	    poly.addPoint((int)x, (int)y);
	    i++;
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
	for (Tank tank : tanks) {
	    if (tank == forTank) continue;
	    Rectangle box = tank.getBoundingBox();
	    if (poly1.intersects(box) || poly2.intersects(box)) {
		VisibleEntity.Type type = VisibleEntity.Type.TANK;
		VisibleEntity.Side side = VisibleEntity.Side.BAD;
		Rectangle rect = box;
		double dir = tank.getDir();
		double turretDir = tank.getTurretDir();
		double speed = tank.getSpeed();
		VisibleEntity ent = new VisibleEntity(type, side, rect, dir, turretDir, speed);
		ents.add(ent);
	    }
	}
	for (Block block : blocks) {
	    Rectangle box = block.getBoundingBox();
	    if (poly1.intersects(box) || poly2.intersects(box)) {
		VisibleEntity.Type type = VisibleEntity.Type.BLOCK;
		VisibleEntity.Side side = VisibleEntity.Side.NEUTRAL;
		Rectangle rect = box;
		double dir = 0.0;
		double turretDir = 0.0;
		double speed = 0.0;
		VisibleEntity ent = new VisibleEntity(type, side, rect, dir, turretDir, speed);
		ents.add(ent);
	    }
	}
	return ents;
    }

}

