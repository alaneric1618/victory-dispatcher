import java.awt.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Room implements KeyListener {

    public boolean paused = false;

    ArrayList<Entity> scene = new ArrayList<Entity>();
    Tank tank;
    ArrayList<Block> blocks = new ArrayList<Block>();
    int x = 0;
    int y = 0; //for debug
    int frame = 0; //for debug
    int globalCount = 0;
    int dotTimer = 0;
    boolean isGlobal = false;
    public int xcoor;
    public int ycoor;
    public boolean buttonPress;
    public Rectangle roomRect = new Rectangle(0, 0, VD.WIDTH-32, VD.HEIGHT-64);


    public Room() {
        tank = new Tank(this);
	for (int i = 0; i < 10; i++) {
	    Block block = new Block(4+i, 10);
	    blocks.add(block);
	    Block block2 = new Block(8+i, 18);
	    blocks.add(block2);
	}
    }

    public void update(float dt) {
	tank.update(dt);
    }

    public void draw(Graphics2D g) {
        //Draw background
        g.setColor(new Color(128, 128, 128));
        g.fillRect(0, 0, VD.WIDTH, VD.HEIGHT);
        //Draw blocks
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
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
            g.setColor(Color.white); //DEMO...DELETE LATER
            g.drawLine(0, y, VD.WIDTH, y); //DEMO...DELETE LATER
        }
	tank.draw(g);
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

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

}

