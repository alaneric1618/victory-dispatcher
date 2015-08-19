import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Tank extends Entity implements KeyListener {

    private Room room;
    private int hp;
    private float cannonCooldown;
    private Arc2D sight;
    private Arc2D hearing;
    private double tread;
    private double desiredTread;
    private double turret;
    private double desiredTurret;
    private double vel;
    private double x;
    private double y;
    private double turretX;
    private double turretY;
    private double cannonX;
    private double cannonY;
    private boolean isTurretLocked = true;

    private int[] seqi = new int[] {
	0,0,0,1,2,3,4,
	6,1,1,1,1,1,1,
	2,3,4,6,2,2,2,
	2,2,3,4,5,6,3,
	3,3,4,5,6,4,4,
	4,0,5,5,3,5,6,
	5,5,6,0,0,0
    };

    private int[] seqj = new int[] {
	4,5,6,0,0,0,0,
	0,1,2,3,4,5,6,
	1,1,1,1,2,3,4,
	5,6,2,2,2,2,4,
	5,6,3,3,3,4,5,
	6,0,0,1,3,4,4,
	5,6,5,1,2,3
    };

    public Tank(Room room) {
	int hp = 100;
	float cannonCooldown = -1;
	//desiredTread = 0;
	boundingSprite = new Rectangle((int)x, (int)y, 64, 64);
	boundingBox = new Rectangle((int)x+16, (int)y+16, 32, 32);	
    }
    
    protected TankControl getTankControls() {
	return new TankControl();
    }

    @Override
    public void update(float dt) {
	x += vel*Math.cos(Math.toRadians(tread));
	y += vel*Math.sin(Math.toRadians(tread));
	turretX = x+32-(16*Math.cos(Math.toRadians(tread)));
	turretY = y+32-(16*Math.sin(Math.toRadians(tread)));
	cannonX = turretX+(20*Math.cos(Math.toRadians(turret)));
	cannonY = turretY+(20*Math.sin(Math.toRadians(turret)));
	vel=0;
	boundingSprite = new Rectangle((int)x, (int)y, 64, 64);
	boundingBox = new Rectangle((int)x+16, (int)y+16, 32, 32);
	if (Math.abs(desiredTread-tread) > 8.0) {
	    if (desiredTread > tread) {
		tread += 7.5;
		if (isTurretLocked) {
		    desiredTurret += 7.5;
		}
	    }
	    if (desiredTread < tread) {
		tread -= 7.5;
		if (isTurretLocked) {
		    desiredTurret -= 7.5;
		}
	    }
	}
	if (Math.abs(desiredTurret-turret) > 8.0) {
	    if (desiredTurret > turret) {
		turret += 7.5;
	    }
	    if (desiredTurret < turret) {
		turret -= 7.5;
	    }
	}

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
	int index = (int)(((-tread+(3600))%360)/7.5);
	drawSprite(g, 64, seqi[index], seqj[index]);
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
	    
	}
    }

    public void keyPressed(KeyEvent e) {
	int code = e.getKeyCode();
        if (e.VK_A == code) {
	    turnTread(-7.5, false);
        }
        if (e.VK_D == code) {
	    turnTread(7.5, false);
        }
        if (e.VK_W == code) {
	    vel = 3;
        }
        if (e.VK_S == code) {
	    vel = -2;
        }
	if (e.VK_Q == code) {
	    turnTurret(-7.5, false);
	}
	if (e.VK_E == code) {
	    turnTurret(7.5, false);
	}
	if (e.VK_L == code) {
	    isTurretLocked = !isTurretLocked;
	}
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public String toString() {
	String s = "Tread:"+tread+"\t";
	s +="Turret:"+turret+"\t";
	return s;
    }
}


