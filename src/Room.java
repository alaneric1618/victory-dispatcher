import java.awt.Rectangle;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics2D;
import java.util.ArrayList;

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

