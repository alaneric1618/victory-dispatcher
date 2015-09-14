import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.lang.*;
import java.awt.event.*;

public class Menus {
    public static BufferedImage logo;
    static {
	try {
	    logo = ImageIO.read(new File("./media/mcleod_logo.png"));
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    public static BufferedImage title;
    static {
	try {
	    title = ImageIO.read(new File("./media/title.png"));
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    public static BufferedImage nope;
    static {
	try {
	    nope = ImageIO.read(new File("./media/nope.png"));
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    public static Text presented = new Text("Presented By", 0.5);

    public static ArrayList<Entity> getMenus() {
        ArrayList<Entity> menus = new ArrayList<Entity>();
        menus.add(black);
        menus.add(mcleod);
        menus.add(opener);
        menus.add(mainMenu);
        return menus;
    }

    private static Entity black = new Entity() {
            {this.setMaxAge(800L);}
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
		//Fade Out
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
		    int y = (int)(520*(v)+140*(1.0-v));
		    int x = (int)(-72*(v)+320*(1.0-v))-offset;
		    left.addPoint(x, y);
		    road.addPoint(x, y);
		    right.addPoint(640-x, y);
		}
		for (int i = 9; i > 0; i--) {
		    double u = ((int)((t*1000)+(i*300))%1000)/1000.0;
		    double v = (i/10.0);
		    int offset = (int)(2*v*Math.sin(205*u) + 20*v);
		    int y = (int)(520*(v)+140*(1.0-v));
		    int x = (int)(-72*(v)+320*(1.0-v))+offset;
		    left.addPoint(x, y);
		    road.addPoint(640-x, y);
		    right.addPoint(640-x, y);
		}
		g.setColor(new Color(30, 30, 30));
		g.fillPolygon(road);
		g.setColor(new Color(120, 120, 120));
		g.fillPolygon(left);
		g.fillPolygon(right);
		g.setColor(Color.yellow);
		for (int i = 0; i < 20; i++) {
		    double u = ((int)((t*1000)+(i*50))%1000)/1000.0;
		    u = 60.0*u*u;
		    double v = (i/10.0);
		    int xOffset = (int)(12*u);
		    int yOffset = (int)(80*u);
		    int y = (int)(520*(u)+140*(1.0-u));
		    int x = (int)(320);
		    Polygon line = new Polygon();
		    line.addPoint(x+xOffset, y-yOffset);
		    line.addPoint(x+xOffset+(int)(12*u), y+yOffset);
		    line.addPoint(x-xOffset-(int)(12*u), y+yOffset);
		    line.addPoint(x-xOffset, y-yOffset);
		    g.fillPolygon(line);
		}
		g.setColor(Color.black);
		g.fillRect(0, 0, 640, 160);
		//g.drawImage(logo, 110, -18, null);
		g.drawImage(logo, new AffineTransform(0.7f, 0f , 0f , 0.7f, 180, 100), null);
		presented.draw(g, 150, 130);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
    };

    private static Entity opener = new Entity() {
            {this.setMaxAge(6500L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.blue);
                g.fillRect(0, 0, 640, 480);
            }
    };

    private static Entity mainMenu = new Entity() {
	    int selector = 1;
            int i1 = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            Tank t1;
            Tank t2;
            Tank t3;
            Tank t4;
            ArrayList<Tank> tanks = new Loader().getTanks();
	    {
		tanks.add(0, null);
	    }

            final private void handleUserControl() {
                if (VD.keys[KeyEvent.VK_UP]) {
		    if (selector == 1) {
			i1++; i1 = i1 % (tanks.size());
		    }
		    if (selector == 2) {
			i2++; i2 = i2 % (tanks.size());
		    }
		    if (selector == 3) {
			i3++; i3 = i3 % (tanks.size());
		    }
		    if (selector == 4) {
			i4++; i4 = i4 % (tanks.size());
		    }
                    VD.keys[KeyEvent.VK_UP] = false;
                }
                if (VD.keys[KeyEvent.VK_DOWN]) {
		    if (selector == 1) {
			i1--; if (i1 < 0) {i1 = tanks.size()-1;}
		    }
		    if (selector == 2) {
			i2--; if (i2 < 0) {i2 = tanks.size()-1;}
		    }
		    if (selector == 3) {
			i3--; if (i3 < 0) {i3 = tanks.size()-1;}
		    }
		    if (selector == 4) {
			i4--; if (i4 < 0) {i4 = tanks.size()-1;}
		    }
                    VD.keys[KeyEvent.VK_DOWN] = false;
                }
		if (VD.keys[KeyEvent.VK_LEFT]) {
		    selector--; if (selector < 1) {selector = 4;}
		    VD.keys[KeyEvent.VK_LEFT] = false;
		}
		if (VD.keys[KeyEvent.VK_RIGHT]) {
		    selector++; selector = (selector % 5);
		    if (selector == 0) selector++;
		    VD.keys[KeyEvent.VK_RIGHT] = false;
		}
		if (selector == 1) {
		    if (i1 >= 0 && (i1 < tanks.size())) t1 = tanks.get(i1);
		}
		if (selector == 2) {
		    if (i2 >= 0 && (i2 < tanks.size())) t2 = tanks.get(i2);
		}
		if (selector == 3) {
		    if (i3 >= 0 && (i3 < tanks.size())) t3 = tanks.get(i3);
		}
		if (selector == 4) {
		    if (i4 >= 0 && (i4 < tanks.size())) t4 = tanks.get(i4);
		}

            }
            public void update(float dt) {
                super.update(dt);
                handleUserControl();
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.white);
                //g.fillRect(100, 200, 92, 92);
                //g.fillRect(213, 200, 92, 92);
		//g.fillRect(326, 200, 92, 92);
                //g.fillRect(440, 200, 92, 92);
		//DRAW TITLE
		g.drawImage(title, new AffineTransform(1.0f, 0f , 0f , 1.0f, 160, 0), null);
		//PLAYER 1
                if (t1 != null) {
                    g.drawImage(t1.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 101, 201), null);
                    Text text = new Text(i1+". "+t1.getName(), 0.2, Text.Align.CENTER, (selector==1));
                    text.draw(g, 146, 300);
                } else {
                    g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 101, 201), null);
                    Text text = new Text(i1+". "+"None", 0.2, Text.Align.CENTER, (selector==1));
                    text.draw(g, 146, 300);
		}
		//PLAYER 2
                if (t2 != null) {
                    g.drawImage(t2.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 214, 201), null);
                    Text text = new Text(i2+". "+t2.getName(), 0.2, Text.Align.CENTER, (selector==2));
                    text.draw(g, 259, 300+20);
                } else {
                    g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 214, 201), null);
                    Text text = new Text(i2+". "+"None", 0.2, Text.Align.CENTER, (selector==2));
                    text.draw(g, 259, 300+20);
		}
		//PLAYER 3
                if (t3 != null) {
                    g.drawImage(t3.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 327, 201), null);
                    Text text = new Text(i3+". "+t3.getName(), 0.2, Text.Align.CENTER, (selector==3));
                    text.draw(g, 372, 300);
                } else {
                    g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 327, 201), null);
                    Text text = new Text(i3+". "+"None", 0.2, Text.Align.CENTER, (selector==3));
                    text.draw(g, 372, 300);
		}
		//PLAYER 4
                if (t4 != null) {
                    g.drawImage(t4.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 441, 201), null);
                    Text text = new Text(i4+". "+t4.getName(), 0.2, Text.Align.CENTER, (selector==4));
                    text.draw(g, 486, 300+20);
                } else {
                    g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 441, 201), null);
                    Text text = new Text(i4+". "+"None", 0.2, Text.Align.CENTER, (selector==4));
                    text.draw(g, 486, 300+20);
		}
            }


    };
}

