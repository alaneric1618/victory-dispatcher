
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

@SuppressWarnings("serial")
public class VD extends JFrame {

    public enum OS {
        WIN,
        MAC,
        UNIX,
        UNKNOWN;

        public String toString() {
            String s = "Unknown Operating System";
            switch (this) {
            case WIN: s = "Windows"; break;
            case MAC: s = "OSX"; break;
            case UNIX: s = "Unix Based"; break;
            }
            return s;
        }
    }
    
    public static Thread gameThread;
    public static GamePanel runningGamePanel;
    public static VD frame;
    public static VD.OS os;

    static {
        String osString = System.getProperty("os.name");
        if (osString.toUpperCase().indexOf("WIN") >= 0) {
            os = VD.OS.WIN;
        } else if (osString.toUpperCase().indexOf("MAC") >= 0) {
            os = VD.OS.MAC;
        } else if (osString.toUpperCase().indexOf("NIX") >= 0) {
            os = VD.OS.UNIX;
        } else {
            os = VD.OS.UNKNOWN;
        }
    }
    
    public GamePanel gamePanel = new GamePanel();
    public Graphics g;
    public boolean running;
    public long dt = 0L;
    public long timePreviousFrame = System.currentTimeMillis();
    public long timeCurrentFrame  = System.currentTimeMillis();
    public long timeStarted       = System.currentTimeMillis();
    public long timeInterval      = 40L; //25 fps
    public Room currentRoom;
    
    int currentButton = 0;

    public static boolean paused = false;
    public static final int WIDTH = 640;
    public static final int HEIGHT= 480;
    public static boolean DEBUG = false;
    public static BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    public static double hScale = 1.0f;
    public static double vScale = 1.0f;
    public static int hOffset = 0;
    public static int vOffset = 0;
    public static boolean isFullScreen = false;
    public static KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    public static boolean[] keys = new boolean[256];


    //loader and opening screen static loading order is important
    public static ArrayList<Entity> openingScreens = Menus.getMenus();

    public VD() {
        this.initGame();
    }

    //Game Initialization - Place something here if you only want it to happen globally when the game is started.
    public void initGame() {
        AudioPlayer.init();
        AudioPlayer.stopAll();
        running = true;
        JFrame frame = this;
        this.frame = this;
        if (os == VD.OS.WIN) {
            frame.setMinimumSize(new Dimension(VD.WIDTH+18 , VD.HEIGHT+30));
        } else {
            frame.setMinimumSize(new Dimension(VD.WIDTH , VD.HEIGHT));
        }
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screen.width/2)-(VD.WIDTH/2), (screen.height/2)-(VD.HEIGHT/2), VD.WIDTH, VD.HEIGHT);
        frame.setBackground(Color.black);
        frame.setTitle("Victory Dispatcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        g = frame.getContentPane().getGraphics();
        frame.getContentPane().add(gamePanel);
        runningGamePanel = gamePanel;
        //currentRoom = new Room(new TankPlayer(), new TankMajorTom(), new TankMajorTom(), new TankMajorTom());
        frame.pack();
        frame.setVisible(true);
	//toggleFullScreen();
	AudioPlayer.OPENER.play();
        //KEYBOARD
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
        handleUserControl();
        if (!paused) {
            this.update(dt);
        }
        this.repaint();
        //SLEEP IF NEEDED
        try {
            long timeComputationEnd = System.currentTimeMillis();
            long timeComputationTaken = timeComputationEnd - timeComputationStart;
            long timeToSleep = this.timeInterval - timeComputationTaken;
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

    final private void handleUserControl() {
        if (keys[KeyEvent.VK_ESCAPE] || keys[KeyEvent.VK_Q]) {
            System.exit(0);
        }
        //TOGGLE KEYS
        if (keys[KeyEvent.VK_F]) {
            toggleFullScreen();
            keys[KeyEvent.VK_F] = false;
        }
        if (keys[KeyEvent.VK_P]) {
            paused = !paused;
            keys[KeyEvent.VK_P] = false;
        }
        if (keys[KeyEvent.VK_H]) {
            VD.DEBUG = !VD.DEBUG;
            keys[KeyEvent.VK_H] = false;
        }
    }
    
    //UPDATE LOOP
    public void update(long dt) {
        try {
            if (openingScreens != null) {
                if (!openingScreens.isEmpty()) {
                    Entity screen = openingScreens.get(0);
                    screen.update(dt);
                    if (screen.isOld() || keys[KeyEvent.VK_SPACE] || keys[KeyEvent.VK_ENTER]) {
                        keys[KeyEvent.VK_SPACE] = false;
                        keys[KeyEvent.VK_ENTER] = false;
                        openingScreens.remove(0);
			if (openingScreens.isEmpty()) {
			    AudioPlayer.OPENER.stop();
			    currentRoom = new Room(Menus.t1, Menus.t2, Menus.t3, Menus.t4);
			}
                    }
                } else {
                    openingScreens = null;
                }
            } else {
                if (currentRoom != null) {
                    currentRoom.update(dt);
                }
            }
        } catch (ConcurrentModificationException e) {
            //e.printStackTrace();
        }
    }

    public void setCurrentRoom(Room room) {
	currentRoom = room;
    }
    
    public class GamePanel extends JPanel {
        //DRAW LOOP
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.translate(hOffset, vOffset);
            g2.scale(VD.hScale, VD.vScale);
            if (openingScreens != null) {
                if (!openingScreens.isEmpty()) {
                    Entity screen = openingScreens.get(0);
                    if (screen != null) {
                        screen.draw(g2);
                    }
                }
            } else {
                if (currentRoom != null) {
                    currentRoom.draw(g2);
                }
            }
            g2.setColor(Color.black);
        }
    }

    static public Point getOriginOnScreen() {
        if (runningGamePanel != null) {
            return runningGamePanel.getLocationOnScreen();
        } else {
            return null;
        }
    }

    public static void toggleFullScreen() {
        if (frame != null) {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();         
            if (isFullScreen) {
                isFullScreen = !isFullScreen;
                frame.removeNotify();
                hScale = 1.0;
                vScale = 1.0;
                hOffset = 0;
                vOffset = 0;
                if (os == VD.OS.WIN) {
                    frame.setBounds((screen.width/2)-(VD.WIDTH/2), (screen.height/2)-(VD.HEIGHT/2), VD.WIDTH+18, VD.HEIGHT+30);
                } else {
                    frame.setBounds((screen.width/2)-(VD.WIDTH/2), (screen.height/2)-(VD.HEIGHT/2), VD.WIDTH, VD.HEIGHT);
                }
                frame.setUndecorated(false);
                frame.addNotify();
            } else {
                isFullScreen = !isFullScreen;
                double hRatio = (double)VD.WIDTH/screen.width;
                double vRatio = (double)VD.HEIGHT/screen.height;
                double windowRatio = (double)VD.WIDTH/VD.HEIGHT;
                double screenRatio = (double)screen.width/screen.height;
                if (windowRatio > screenRatio) { //restrict width
                    hScale = hScale/hRatio;
                    vScale = vScale/hRatio;
                    hOffset = (int)((screen.width - (hScale*VD.WIDTH))/2.0);
                    vOffset = (int)((screen.height - (vScale*VD.HEIGHT))/2.0);
                } else { //restrict height
                    hScale = hScale/vRatio;
                    vScale = vScale/vRatio;
                    hOffset = (int)((screen.width - (hScale*VD.WIDTH))/2.0);
                    vOffset = (int)((screen.height - (vScale*VD.HEIGHT))/2.0);
                }
                frame.removeNotify();
                frame.setUndecorated(true);
                frame.setBounds(0, 0, screen.width, screen.height);
                frame.addNotify();
            }
            // Since the focus is frozen all keys will also be frozen.
            // REMOVE FROZEN KEYS
            for (int i = 0; i < 256; i++) {
                keys[i] = false;
            }
            //REQUEST FOCUS
            frame.requestFocus();
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
