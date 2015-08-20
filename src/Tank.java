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

public class Tank extends Entity {

    private Room room;
    private int hp;
    private float cannonCooldown;
    private Polygon treadSight;
    private Polygon turretSight;
    private double tread;
    private double desiredTread;
    private double turret;
    private double desiredTurret;
    private double vel;
    private double x;
    private double y;
    private double centerX;
    private double centerY;
    private double turretX;
    private double turretY;
    private double cannonX;
    private double cannonY;
    private boolean isTurretLocked = true;
    private boolean[] keys = new boolean[256];
    private double time = 0.0;
    private KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

    public Tank(Room room) {
	this.room = room;
	manager.addKeyEventDispatcher(new KeyEventDispatcher() {
		public boolean dispatchKeyEvent(KeyEvent e) {
		    int code = e.getKeyCode();
		    if (code < 256) {
			if (KeyEvent.KEY_PRESSED == e.getID()) {
			    keys[code] = true;
			}
			if (KeyEvent.KEY_RELEASED == e.getID()) {
			    keys[code] = false;
			}
		    }
		    return true;
		}
	});
	treadSight = new Polygon();
	turretSight = new Polygon();
	int hp = 100;
	float cannonCooldown = -1;
	boundingSprite = new Rectangle((int)x, (int)y, 64, 64);
	boundingBox = new Rectangle((int)x+16, (int)y+16, 32, 32);	
    }
    
    protected TankControl getTankControls() {
	return new TankControl();
    }

    @Override
    public void update(float dt) {
	time += dt;
	double testX;
	double testY;
	Rectangle testRect;
	//test X and Y
	testX = x+vel*Math.cos(Math.toRadians(tread));
	testY = y+vel*Math.sin(Math.toRadians(tread));
	testRect = new Rectangle((int)testX+16, (int)testY+16, 32, 32);
	if (room.isLocationFree(testRect)) {
	    x += vel*Math.cos(Math.toRadians(tread));
	    y += vel*Math.sin(Math.toRadians(tread));
	}
	//test X
	testX = x+vel*Math.cos(Math.toRadians(tread));
	testY = y;
	testRect = new Rectangle((int)testX+16, (int)testY+16, 32, 32);
	if (room.isLocationFree(testRect)) {
	    x += vel*Math.cos(Math.toRadians(tread));
	}
	//test X
	testX = x;
	testY = y+vel*Math.sin(Math.toRadians(tread));
	testRect = new Rectangle((int)testX+16, (int)testY+16, 32, 32);
	if (room.isLocationFree(testRect)) {
	    y += vel*Math.sin(Math.toRadians(tread));
	}
	centerX = x+32;
	centerY = y+32;
	turretX = x+32-(14*Math.cos(Math.toRadians(tread)));
	turretY = y+20-(8*Math.sin(Math.toRadians(tread)));
	cannonX = turretX+(20*Math.cos(Math.toRadians(turret)));
	cannonY = turretY+(20*Math.sin(Math.toRadians(turret)));
	vel=0;
	boundingSprite = new Rectangle((int)x, (int)y, 64, 64);
	boundingBox = new Rectangle((int)x+16, (int)y+16, 32, 32);
	if (Math.abs(desiredTread-tread) > 8.0) {
	    if (desiredTread > tread) {
		tread += 10.0;
		if (isTurretLocked) {
		    desiredTurret += 10.0;
		    turret += 10.0;
		}
	    }
	    if (desiredTread < tread) {
		tread -= 10.0;
		if (isTurretLocked) {
		    desiredTurret -= 10.0;
		    turret -= 10.0;
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
    }

    protected void updateSight() {
	treadSight.reset();
	turretSight.reset();
	treadSight = room.getSight(new Point((int)centerX, (int)centerY), tread, 45);
	turretSight = room.getSight(new Point((int)turretX, (int)turretY), turret, 45);
    }

    protected void turnTread(double deg, boolean isAbsolute) {
	if (isAbsolute) {
	    desiredTread = deg;
	} else {
	    desiredTread += deg;
	}
    }

    protected void turnTurret(double deg, boolean isAbsolute) {
	isTurretLocked = false;
	if (isAbsolute) {
	    desiredTurret = deg;
	} else {
	    desiredTurret += deg;
	}
    }

    @Override
    public void draw(Graphics2D g) {
	super.draw(g);
	handleUserControl();
	int index = (int)(((-tread+90+(3600))%360)/7.5);
        int turretIndex = (int)(((-turret+90+(3600))%360)/7.5);
	drawSprite(g, 64, index, 0, 5, 0);
        drawSprite(g, 64, turretIndex, 2, (int)(turretX-centerX)+5, (int)(turretY-centerY)+12);
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
	    int losX = ((int)turretX+(int)(700*Math.cos(Math.toRadians(turret))));
	    int losY = ((int)turretY+(int)(700*Math.sin(Math.toRadians(turret))));
	    g.setColor(new Color(200, 0, 0));
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
	    g.drawLine(cx2, cy2, losX, losY);
	    g.setColor(new Color(50, 50, 100));
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
	    g.fillPolygon(treadSight);
	    g.fillPolygon(turretSight);
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
    }

    private void handleUserControl() {
	if (keys[KeyEvent.VK_A]) {
	    turnTread(-10.0, false);
	}
	if (keys[KeyEvent.VK_D]) {
	    turnTread(10.0, false);
	}
	if (keys[KeyEvent.VK_W]) {
	    vel = 3;
	}
	if (keys[KeyEvent.VK_S]) {
	    vel = -2;
	}
	if (keys[KeyEvent.VK_J]) {
	    turnTurret(-5.0, false);
	}
	if (keys[KeyEvent.VK_K]) {
	    turnTurret(5.0, false);
	}
	if (keys[KeyEvent.VK_H]) {
	    VD.DEBUG = !VD.DEBUG;
	}
	if (keys[KeyEvent.VK_L]) {
	    isTurretLocked = !isTurretLocked;
	}
    }

    public String toString() {
	String s = "Tread:"+tread+"\t";
	s +="Turret:"+turret+"\t";
	return s;
    }
}


