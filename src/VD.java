import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import javax.swing.*;
import java.util.*;

@SuppressWarnings("serial")
public class VD extends JFrame implements KeyListener {

    public static Thread gameThread;
    
    GamePanel gamePanel = new GamePanel();
    public Graphics g;
    public boolean running;
    public boolean paused = false;
    public long dt = 0L;
    public long timePreviousFrame = System.currentTimeMillis();
    public long timeCurrentFrame  = System.currentTimeMillis();
    public long timeStarted       = System.currentTimeMillis();
    public long timeInterval      = 40L; //25 fps
    public Room currentRoom;
    
    int currentButton = 0;

    public static final int WIDTH = 640;
    public static final int HEIGHT= 480;
    public static boolean DEBUG = false;

    public VD() {
        this.initGame();
    }

    //Game Initialization - Place something here if you only want it to happen globally when the game is started.
    public void initGame() {
	AudioPlayer.init();
        AudioPlayer.stopAll();
        running = true;
        JFrame frame = this;
        frame.setMinimumSize(new Dimension(VD.WIDTH , VD.HEIGHT));
        frame.setTitle("Victory Dispatcher");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        g = frame.getContentPane().getGraphics();
        frame.getContentPane().add(gamePanel);
        currentRoom = new Room();
        gamePanel.room = currentRoom;
        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
        while (running) {
            gameLoop();
        }
    }

    public void gameLoop() {
        //CLOCK
        this.timeCurrentFrame = System.currentTimeMillis();
        this.dt = this.timeCurrentFrame - this.timePreviousFrame;
        this.timePreviousFrame = this.timeCurrentFrame;
        long timeComputationStart = System.currentTimeMillis();
        //UPDATE AND DRAW
        if (!paused) this.update(dt);
        this.repaint();
        //this.paintComponent(g);
        //SLEEP IF NEEDED
        try {
            long timeComputationEnd = System.currentTimeMillis();
            long timeComputationTaken = timeComputationEnd - timeComputationStart;
            long timeToSleep = this.timeInterval - timeComputationTaken;
	    //System.out.println("Time taken:" + dt + "  ");
            if (timeToSleep >= 0) {
                Thread.sleep(timeToSleep);
            } else {
                Thread.sleep(0);
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not sleep main thread.");
            e.printStackTrace();
        }
    } 

    //Game Release - Do before game closes
    public void releaseGame() {

    }

    //UPDATE LOOP
    public void update(long dt) {
        currentRoom.update(dt);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
            if (paused) {
                paused = false;
                currentRoom.paused = false;
            }
            else if (!paused) {
                paused = true;
                currentRoom.paused = true;
            }
        }
	if (keyEvent.getKeyCode() == KeyEvent.VK_H) {
	    VD.DEBUG = !VD.DEBUG;
	}
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    public class GamePanel extends JPanel {
        public Room room;
        //DRAW LOOP
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            currentRoom.draw(g2);
        }
    }

    public static void main(String[] args) {
	gameThread = new Thread(new Runnable() {
	 	public void run() {
		    VD game = new VD();
	 	}
	});
	gameThread.start();
    }

}
