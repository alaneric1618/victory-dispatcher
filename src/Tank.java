import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.geom.*;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Tank extends Entity implements TankInterface {

    public enum Player {
        P1,
        P2,
        P3,
        P4,
        NONE
    }

    private Tank.Player player;
    private Room room;
    private Color color = Color.white;
    private int hp;
    private float cannonCooldown;
    private Polygon treadSight;
    private Polygon turretSight;
    private double tread;
    private double desiredTread;
    private double turret;
    private double desiredTurret;
    private double speed;
    private double lastSpeed;
    private double x;
    private double y;
    private double centerX;
    private double centerY;
    private double turretX;
    private double turretY;
    private double cannonX;
    private double cannonY;
    private boolean isTurretLocked = true;
    private double time = 0.0;
    private double bulletTime = 0.0;
    private double bulletWait = 850.0;
    private float turretSize = 1.0f;
    private double turretPull = 1.0;

    public Tank() {
        this.player = player;
	treadSight = new Polygon();
	turretSight = new Polygon();
	int hp = 100;
	float cannonCooldown = -1;
	onCreation();
    }

    public Tank.Player getPlayer() {
        return player;
    }

    final public void setRoom(Room room) {
	player = room.getNewPlayerEnum();
	switch (player) {
	case P1:
	    x = 10; y = 10;
	    desiredTread = 45.0; desiredTurret = desiredTread;
	    color = Color.cyan;
	    break;
	case P2:
	    x = 510; y = 350;
	    desiredTread = 225.0; desiredTurret = desiredTread;
	    color = Color.magenta;
	    break;
	case P3:
	    x = 10; y = 350;
	    desiredTread = 315.0; desiredTurret = desiredTread;
	    color = Color.yellow;
	    break;
	case P4:
	    x = 510; y = 10;
	    desiredTread = 135.0; desiredTurret = desiredTread;
	    color = Color.black;
	    break;
	default:
	    x = 225; y = 175;
	    desiredTread = 270.0; desiredTurret = desiredTread;
	    break;
	}
	boundingSprite = new Rectangle((int)x, (int)y, 64, 64);
	boundingBox = new Rectangle((int)x+16, (int)y+16, 32, 32);
	this.room = room;
    }
        
    @Override
    final public void update(float dt) {
	if (room != null) {
	    loop(dt);
	    time += dt;
            bulletTime += dt;
	    double testX;
	    double testY;
	    Rectangle testRect;
	    //test X and Y
	    testX = x+speed*Math.cos(Math.toRadians(tread));
	    testY = y+speed*Math.sin(Math.toRadians(tread));
	    testRect = new Rectangle((int)testX+16, (int)testY+16, 32, 32);
	    if (room.isLocationFree(testRect)) {
		x += speed*Math.cos(Math.toRadians(tread));
		y += speed*Math.sin(Math.toRadians(tread));
	    }
	    //test X
	    testX = x+speed*Math.cos(Math.toRadians(tread));
	    testY = y;
	    testRect = new Rectangle((int)testX+16, (int)testY+16, 32, 32);
	    if (room.isLocationFree(testRect)) {
		x += speed*Math.cos(Math.toRadians(tread));
	    }
	    //test X
	    testX = x;
	    testY = y+speed*Math.sin(Math.toRadians(tread));
	    testRect = new Rectangle((int)testX+16, (int)testY+16, 32, 32);
	    if (room.isLocationFree(testRect)) {
		y += speed*Math.sin(Math.toRadians(tread));
	    }
	    centerX = x+32;
	    centerY = y+32;
	    turretX = x+32-(14*Math.cos(Math.toRadians(tread)))-(((turretPull-1)*32)*Math.cos(Math.toRadians(turret)));
	    turretY = y+20-(8*Math.sin(Math.toRadians(tread)))-(((turretPull-1)*32)*Math.sin(Math.toRadians(turret)));
	    cannonX = turretX+(20*Math.cos(Math.toRadians(turret)));
	    cannonY = turretY+(20*Math.sin(Math.toRadians(turret)));
	    lastSpeed = speed;
	    speed=0;
            turretSize = ((turretSize-1.0f)/2.0f)+1.0f;
            turretPull = ((turretPull-1.0f)/1.3f)+1.0f;
	    boundingSprite = new Rectangle((int)x, (int)y, 64, 64);
	    boundingBox = new Rectangle((int)x+16, (int)y+16, 32, 32);
	    double treadDiff = Math.abs(desiredTread-tread);
	    double treadRate = treadDiff/2.0;
	    if (treadDiff > 1.0) {
		if (desiredTread > tread) {
		    tread += treadRate;
		    if (isTurretLocked) {
			desiredTurret += treadRate;
			turret += treadRate;
		    }
		}
		if (desiredTread < tread) {
		    tread -= treadRate;
		    if (isTurretLocked) {
			desiredTurret -= treadRate;
			turret -= treadRate;
		    }
		}
	    }
	    double turretDiff = Math.abs(desiredTurret-turret);
	    if (turretDiff > 1.0) {
		if (desiredTurret > turret) {
		    turret += turretDiff/5.0;
		}
		if (desiredTurret < turret) {
		    turret -= turretDiff/5.0;
		}
	    }
	    updateSight();
	    loop(dt);
	}
    }

    final private void updateSight() {
	if (room != null) {
	    treadSight.reset();
	    turretSight.reset();
	    treadSight = room.getSight(new Point((int)centerX, (int)centerY), tread, 45);
	    turretSight = room.getSight(new Point((int)turretX, (int)turretY), turret, 45);
	}
    }

    @Override
    final public void draw(Graphics2D g) {
	super.draw(g);
        //draw tracker
        Polygon poly = new Polygon();
        double rot = ((int)(time/6)%360);
        for (double theta = 0+rot; theta < 320.0+rot; theta+=5) {
            double xt = centerX+34*Math.cos(Math.toRadians(theta));
            double yt = centerY+22*Math.sin(Math.toRadians(theta));
            poly.addPoint((int)xt, (int)yt);
        }
        for (double theta = 320+rot; theta > 0.0+rot; theta-=5) {
            double xt = centerX+28*Math.cos(Math.toRadians(theta));
            double yt = centerY+16*Math.sin(Math.toRadians(theta))-2;
            poly.addPoint((int)xt, (int)yt);
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.setColor(color);
        g.fillPolygon(poly);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        //draw laser
        int losX = ((int)turretX+(int)(700*Math.cos(Math.toRadians(turret))));
        int losY = ((int)turretY+(int)(700*Math.sin(Math.toRadians(turret))));
        g.setColor(color);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        Polygon laser = room.getSight(new Point((int)cannonX, (int)cannonY), turret, 1.0);
        if (laser.npoints == 2) {
            g.drawLine(laser.xpoints[0], laser.ypoints[0], laser.xpoints[1], laser.ypoints[1]);
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        //get index
	int index = (int)(((tread+270+(3600))%360)/7.5);
        int turretIndex = (int)(((turret+270+(3600))%360)/7.5);
	drawSprite(g, 64, index, 3, 5, 0);
        this.spriteSize = turretSize;
        drawSprite(g, 64, turretIndex, 0, 
                   (int)(turretX-centerX)+5-(int)((turretSize-1)*32),
                   (int)(turretY-centerY)+12-(int)((turretSize-1)*32));
        this.spriteSize = 1.0f;
	if (VD.DEBUG) {
	    int cx1 = (int)x;
	    int cy1 = (int)y;
	    int xa = (int)(42*Math.cos(Math.toRadians(tread)));
	    int ya = (int)(42*Math.sin(Math.toRadians(tread)));
	    g.setColor(Color.green);
	    g.drawLine(cx1+32, cy1+32, cx1+32+xa, cy1+32+ya);
	    g.fillOval(cx1+32+xa-2, cy1+32+ya-2, 4, 4);

	    int cx2 = (int)turretX;
	    int cy2 = (int)turretY;
	    int xb = (int)cannonX;
	    int yb = (int)cannonY;
	    g.setColor(Color.blue);
	    g.fillOval((int)cx2-2, (int)cy2-2, 4, 4);
	    g.drawLine(cx2, cy2, xb, yb);
	    g.fillOval(xb-2, yb-2, 4, 4);
	    g.setColor(color);
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
	    g.fillPolygon(treadSight);
	    g.fillPolygon(turretSight);
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
    }

    public String toString() {
	String s = "Tread:"+tread+"\t";
	s +="Turret:"+turret+"\t";
	return s;
    }

    ///////START OF USER AI INTERFACE/////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    //implementable start
    public void onCreation() {

    }
    public void onHit() {

    }
    public void loop(float dt) {

    }
    //implementable end

    //callable start
    final protected double getSpeed() {
	return lastSpeed;
    }

    final protected double getDir() {
	return tread;
    }

    final protected double getTurretDir() {
	return turret;
    }
    
    final protected HashSet<VisibleEntity> getVisibleEntities() {
	if (room == null) return null;
	HashSet<VisibleEntity> visible = room.getVisibleEntities(this, treadSight, turretSight);
	return visible;
    }

    final protected void forward() {
	speed = 3.0;
    }

    final protected void backward() {
	speed = -2.0;
    }

    final protected void turnTread(double deg, boolean isAbsolute) {
	if (isAbsolute) {
	    desiredTread = deg;
	} else {
	    desiredTread += deg;
	}
    }

    final protected void turnTurretTo(double x, double y) {
    }
    
    final protected void turnTurret(double deg, boolean isAbsolute) {
	isTurretLocked = false;
	if (isAbsolute) {
	    desiredTurret = deg;
	} else {
	    desiredTurret += deg;
	}
    }

    final protected boolean isFireAllowed() {
        return bulletTime > bulletWait;
    }

    final protected void fire() {
        if (bulletTime > bulletWait) {
            bulletTime = 0.0;
            Bullet bullet = new Bullet(player, cannonX-8, cannonY-8, turret);
            turretSize = 1.2f;
            turretPull = 1.2f;
            room.add(bullet);
        }
    }
    //callable end

    ///////END OF USER AI INTERFACE///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    
}


