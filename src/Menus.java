import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.io.File;

public class Menus {
    public static BufferedImage logo;
    static {
	try {
	    logo = ImageIO.read(new File("./media/mcleod_logo.png")); //Frames to animate
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    public static ArrayList<Entity> getMenus() {
        ArrayList<Entity> menus = new ArrayList<Entity>();
        menus.add(black);
        menus.add(mcleod);
        menus.add(opener);
        menus.add(mainMenu);
        return menus;
    }

    private static Entity black = new Entity() {
            {this.setMaxAge(1000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, 640, 480);
            }
    };

    private static Entity mcleod = new Entity() {
            {this.setMaxAge(6000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
		double t = ((double)this.getAge()/(double)this.getMaxAge());
		//Fade In
		if (t < 0.2) {
		    double x = t*5.0;
		    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)x));
		    g.setColor(Color.black);
		    g.fillRect(0, 0, 640, 480);
		}
		//Fade In
		if (t > 0.7) {
		    double x = (1.0-(t-0.7)*3.33);
		    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)x));
		    g.setColor(Color.black);
		    g.fillRect(0, 0, 640, 480);
		}
		Polygon left = new Polygon();
		Polygon road = new Polygon();
		Polygon right = new Polygon();
		left.addPoint(320, 140);
		right.addPoint(320, 140);
		for (int i = 0; i < 10; i++) {
		    double u = ((int)((t*1000)+(i*300))%1000)/1000.0;
		    double v = (i/10.0);
		    int offset = (int)(2*v*Math.sin(198*u) + 20*v);
		    int x = (int)(-72*(v)+320*(1.0-v))-offset;
		    int y = (int)(520*(v)+140*(1.0-v));
		    left.addPoint(x, y);
		    road.addPoint(x, y);
		    right.addPoint(640-x, y);
		}
		for (int i = 9; i > 0; i--) {
		    double u = ((int)((t*1000)+(i*300))%1000)/1000.0;
		    double v = (i/10.0);
		    int offset = (int)(2*v*Math.sin(205*u) + 20*v);
		    int x = (int)(-72*(v)+320*(1.0-v))+offset;
		    int y = (int)(520*(v)+140*(1.0-v));
		    left.addPoint(x, y);
		    road.addPoint(640-x, y);
		    right.addPoint(640-x, y);
		}
		g.setColor(new Color(30, 30, 30));
		g.fillPolygon(road);
		g.setColor(new Color(160, 160, 160));
		g.fillPolygon(left);
		g.fillPolygon(right);
		g.setColor(Color.yellow);
		for (int i = 0; i < 20; i++) {
		    double u = ((int)((t*1000)+(i*50))%1000)/1000.0;
		    u = 60.0*u*u;
		    double v = (i/10.0);
		    int xOffset = (int)(12*u);
		    int yOffset = (int)(80*u);
		    int x = (int)(320);
		    int y = (int)(520*(u)+140*(1.0-u));
		    Polygon line = new Polygon();
		    line.addPoint(x+xOffset, y-yOffset);
		    line.addPoint(x+xOffset+(int)(12*u), y+yOffset);
		    line.addPoint(x-xOffset-(int)(12*u), y+yOffset);
		    line.addPoint(x-xOffset, y-yOffset);
		    g.fillPolygon(line);
		}
		g.setColor(Color.black);
		g.fillRect(0, 0, 640, 160);
		g.drawImage(logo, 110, -18, null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
    };

    private static Entity opener = new Entity() {
            {this.setMaxAge(3000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.blue);
                g.fillRect(0, 0, 640, 480);
            }
    };

    private static Entity mainMenu = new Entity() {
            {this.setMaxAge(3000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.red);
                g.fillRect(0, 0, 640, 480);
            }
    };

    





}

