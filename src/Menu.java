import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.awt.geom.AffineTransform;
import java.io.*;
import java.awt.event.*;

public class Menu {

    public Tank t1;
    public Tank t2;
    public Tank t3;
    public Tank t4;
    
    Entity black = new Black(this);
    Entity mcleod = new McLeod(this);
    Entity opener = new Opener(this);
    Entity mainmenu = new MainMenu(this);

    public static BufferedImage logo;
    static {
		try {
			InputStream is = VD.class.getResourceAsStream("/media/mcleod_logo.png");
		    logo = ImageIO.read(is);
		} catch(Exception e) {
		    e.printStackTrace();
		}
    }
    public static BufferedImage titleFront;
    public static BufferedImage titleBack;
    static {
		try {
		    titleFront = ImageIO.read(VD.class.getResourceAsStream("/media/title_front.png"));
		} catch(Exception e) {
		    e.printStackTrace();
		}
    }
    static {
		try {
		    titleBack = ImageIO.read(VD.class.getResourceAsStream("/media/title_back.png"));
		} catch(Exception e) {
		    e.printStackTrace();
		}
    }
    public static BufferedImage nope;
    static {
		try {
		    nope = ImageIO.read(VD.class.getResourceAsStream("/media/nope.png"));
		} catch(Exception e) {
		    e.printStackTrace();
		}
    }
    public static Text presented = new Text("Presented By", 0.5);

    
    public VD vd;
    public Menu(VD vd) {
    	this.vd = vd;
    }
    
    public ArrayList<Entity> getMenus() {
        ArrayList<Entity> menus = new ArrayList<Entity>();
        String startupMode = Util.getProperty("startup-mode");
        if ("test".compareTo(startupMode)==0) {
        	; //Don't add any menues
        } else {
        	menus.add(black);
        	menus.add(mcleod);
        	menus.add(opener);
        }
        menus.add(mainmenu);
        return menus;
    }

    static class Black extends Entity {
    	private Menu menus;
    	Black(Menu menus) {
    		this.menus = menus;
    	}
            {this.setMaxAge(800L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, 640, 480);
            }
    };

    static class McLeod extends Entity {
    	private Menu menus;
    	McLeod(Menu menus) {
    		this.menus = menus;
    	}
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

    static class Opener extends Entity {
    	private Menu menus;
    	Opener(Menu menus) {
    		this.menus = menus;
    	}
            {this.setMaxAge(6500L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.blue);
                g.fillRect(0, 0, 640, 480);
            }
    };

    class MainMenu extends Entity {
    	private Menu menu;
    	MainMenu(Menu menu) {
    		this.menu = menu;
    		int firstOpponent = (int)((tanks1.size()-1)*Math.random()+2);
			tanks1.add(0, null); 
			tanks2.add(0, null); 
			tanks3.add(0, null); 
			tanks4.add(0, null);
			i1 = 1;
			i2 = firstOpponent;
			i3 = 0;
			i4 = 0;
			if (tanks1.size() > i1) menu.t1 = tanks1.get(i1);
			if (tanks2.size() > i2) menu.t2 = tanks1.get(i2);
			if (tanks3.size() > i3) menu.t3 = tanks1.get(i3);
			if (tanks4.size() > i4) menu.t4 = tanks1.get(i4);
		    String startupMode = Util.getProperty("startup-mode");
		    if ("test".compareTo(startupMode)==0) {
		    	String tank1Name = Util.getProperty("startup-tank1-name");
		    	String tank2Name = Util.getProperty("startup-tank2-name");
		    	for (Tank tank : tanks1) {
		    		if (tank != null) {
		    			String className = tank.getClass().getName();
			    		if (tank1Name.compareTo(className)==0) {
			    			menu.t1 = tank;
			    			i1 = tanks1.indexOf(tank);
			    		}
		    		}
		    	}
		    	for (Tank tank : tanks2) {
		    		if (tank != null) {
		    			String className = tank.getClass().getName();
			    		if (tank2Name.compareTo(className)==0) {
			    			menu.t2 = tank;
			    			i2 = tanks2.indexOf(tank);
			    		}
		    		}
		    	}
		    	menu.t3 = null;
		    	menu.t4 = null;
		    }
			phrase = phrases[(int)(phrases.length*Math.random())];
    	}
	    int selector = 1;
        int i1;
        int i2;
        int i3;
        int i4;
        Loader loader = Util.loader;
        ArrayList<Tank> tanks1 = loader.getTanks();
        ArrayList<Tank> tanks2 = loader.getTanks();
        ArrayList<Tank> tanks3 = loader.getTanks();
        ArrayList<Tank> tanks4 = loader.getTanks();
	    String phrase = "";
	    String[] phrases = new String[] {
	    		"Now in technicolor.",
	    		"Dispatching victory since 2015.",
	    		"Available in TRANSACT MODE.",
	    		"Did you check in LogicTransferToSettlement?",
	    		"Here comes the BOOM.",
	    		"A handful of people in a leaky reefer to save the world.",
	    };
	    ArrayList<Particle> particles = new ArrayList<Particle>();
	    
	    final private void handleUserControl() {
		    if (Keyboard.keys[KeyEvent.VK_UP]) {
	    		if (AudioPlayer.SELECT_LOW1.isPlaying()) {
	    			AudioPlayer.SELECT_LOW1.stop();
	    			AudioPlayer.SELECT_LOW2.play();
	    		} else {
	                AudioPlayer.SELECT_LOW2.stop();
	                AudioPlayer.SELECT_LOW1.play();
	    		}
			    if (selector == 1) {
			    	i1++; i1 = i1 % (tanks1.size());
			    }
			    if (selector == 2) {
			    	i2++; i2 = i2 % (tanks2.size());
			    }
			    if (selector == 3) {
			    	i3++; i3 = i3 % (tanks3.size());
			    }
			    if (selector == 4) {
			    	i4++; i4 = i4 % (tanks4.size());
			    }
			    Keyboard.keys[KeyEvent.VK_UP] = false;
	    	}
	    	if (Keyboard.keys[KeyEvent.VK_DOWN]) {
	            if (AudioPlayer.SELECT_LOW1.isPlaying()) {
	                AudioPlayer.SELECT_LOW1.stop();
	                AudioPlayer.SELECT_LOW2.play();
	            } else {
	                AudioPlayer.SELECT_LOW2.stop();
	                AudioPlayer.SELECT_LOW1.play();
	            }
			    if (selector == 1) {
			    	i1--; if (i1 < 0) {i1 = tanks1.size()-1;}
			    }
			    if (selector == 2) {
			    	i2--; if (i2 < 0) {i2 = tanks2.size()-1;}
			    }
			    if (selector == 3) {
			    	i3--; if (i3 < 0) {i3 = tanks3.size()-1;}
			    }
			    if (selector == 4) {
			    	i4--; if (i4 < 0) {i4 = tanks4.size()-1;}
			    }
			    Keyboard.keys[KeyEvent.VK_DOWN] = false;
	    	}
	    	if (Keyboard.keys[KeyEvent.VK_LEFT]) {
	            if (AudioPlayer.SELECT_HI1.isPlaying()) {
	                AudioPlayer.SELECT_HI1.stop();
	                AudioPlayer.SELECT_HI2.play();
	            } else {
	                AudioPlayer.SELECT_HI2.stop();
	                AudioPlayer.SELECT_HI1.play();
	            }
			    selector--; if (selector < 1) {selector = 4;}
			    Keyboard.keys[KeyEvent.VK_LEFT] = false;
	    	}
	    	if (Keyboard.keys[KeyEvent.VK_RIGHT]) {
	            if (AudioPlayer.SELECT_HI1.isPlaying()) {
	                AudioPlayer.SELECT_HI1.stop();
	                AudioPlayer.SELECT_HI2.play();
	            } else {
	                AudioPlayer.SELECT_HI2.stop();
	                AudioPlayer.SELECT_HI1.play();
	            }
			    selector++; selector = (selector % 5);
			    if (selector == 0) selector++;
			    Keyboard.keys[KeyEvent.VK_RIGHT] = false;
	    	}
	    	if (Keyboard.keys[KeyEvent.VK_F2]) {
	    		new Editor();
	    		menu.vd.dispose();
	    		Keyboard.keys[KeyEvent.VK_F2] = false;
	    	}
			if (selector == 1) {
			    if (i1 >= 0 && (i1 < tanks1.size())) menu.t1 = tanks1.get(i1);
			}
			if (selector == 2) {
			    if (i2 >= 0 && (i2 < tanks2.size())) menu.t2 = tanks2.get(i2);
			}
			if (selector == 3) {
			    if (i3 >= 0 && (i3 < tanks3.size())) menu.t3 = tanks3.get(i3);
			}
			if (selector == 4) {
			    if (i4 >= 0 && (i4 < tanks4.size())) menu.t4 = tanks4.get(i4);
			}
		}
	    
        public void update(float dt) {
            super.update(dt);
            handleUserControl();
        }
        public void draw(Graphics2D g) {
	int characterY = 320;
	int characterTitleY = 420;
            g.setColor(Color.white);
	//Particles
	particles.add(new Particle());
	for (int i = 0; i < particles.size(); i++) {
	    Particle p = particles.get(i);
	    if (p.age > 4000) {
		particles.remove(i--);
	    }
	}
	for (Particle p : particles) {
	    p.updateDraw(g);
	}
	//DRAW TITLE
	g.drawImage(titleBack, new AffineTransform(1.0f, 0f , 0f , 1.0f, 0, 0), null);
	g.drawImage(titleFront, new AffineTransform(1.0f, 0f , 0f , 1.0f, 0, 0), null);
	//PLAYER 1
            if (menu.t1 != null) {
                g.drawImage(menu.t1.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 101, characterY), null);
                Text text = new Text(menu.t1.getName(), 0.2, Text.Align.CENTER, (selector==1));
                text.draw(g, 146, characterTitleY);
            } else {
                g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 101, characterY), null);
                Text text = new Text("None", 0.2, Text.Align.CENTER, (selector==1));
                text.draw(g, 146, characterTitleY);
	}
	//PLAYER 2
            if (menu.t2 != null) {
                g.drawImage(menu.t2.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 214, characterY), null);
                Text text = new Text(menu.t2.getName(), 0.2, Text.Align.CENTER, (selector==2));
                text.draw(g, 259, characterTitleY+20);
            } else {
                g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 214, characterY), null);
                Text text = new Text("None", 0.2, Text.Align.CENTER, (selector==2));
                text.draw(g, 259, characterTitleY+20);
	}
	//PLAYER 3
            if (menu.t3 != null) {
                g.drawImage(menu.t3.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 327, characterY), null);
                Text text = new Text(menu.t3.getName(), 0.2, Text.Align.CENTER, (selector==3));
                text.draw(g, 372, characterTitleY);
            } else {
                g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 327, characterY), null);
                Text text = new Text("None", 0.2, Text.Align.CENTER, (selector==3));
                text.draw(g, 372, characterTitleY);
	}
	//PLAYER 4
            if (menu.t4 != null) {
                g.drawImage(menu.t4.getIcon(), new AffineTransform(2.0f, 0f , 0f , 2.0f, 441, characterY), null);
                Text text = new Text(menu.t4.getName(), 0.2, Text.Align.CENTER, (selector==4));
                text.draw(g, 486, characterTitleY+20);
            } else {
                g.drawImage(nope, new AffineTransform(2.0f, 0f , 0f , 2.0f, 441, characterY), null);
                Text text = new Text("None", 0.2, Text.Align.CENTER, (selector==4));
                text.draw(g, 486, characterTitleY+20);
	}
	//Phrase
	Text phraseText = new Text(phrase, 0.2, Text.Align.CENTER);
	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
	phraseText.draw(g, 320, 200);
	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	//Periodic drawing
	if ( ((int)this.getAge())%1000 < 500 ) {
		//Draw Insert Coin
	    Text text = new Text("Insert Coin", 0.3, Text.Align.CENTER);
	    text.draw(g, 320, 250);
	}
	//Draw Edit
	Text f2 = new Text("Press F2 for Tank Editor", 0.18, Text.Align.CENTER);
	f2.draw(g, 320, 275);
        }
    };
}

class Particle {
    public double x = 150.0+150.0*Math.random();
    public double y = 50.0+100.0*Math.random();
    public double u = 3-9*Math.random();
    public double v = 0.0;
    public double a = 0.05+Math.random()/10.0;
    public int age = 0;
    public void updateDraw(Graphics g) {
	//age
	age++;
	//vel
	u = u;
	v -= a;
	//dis
	x += u;
	y += v;
	//draw
	g.setColor(new Color(200, 160-((18*age)%160), 10));
	g.fillRect((int)x, (int)y, 2, 2);
	g.fillRect(640-(int)x, (int)y, 2, 2);
    }
}
