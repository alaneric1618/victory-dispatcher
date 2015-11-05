
import java.awt.event.KeyEvent;
import java.awt.*;
import java.awt.image.*;
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

    public Thread gameThread;
    public GamePanel runningGamePanel;
    public VD frame;
    public VD.OS os;

    {
    	//ADD TO CLASSPATH
    	String classpath = System.getProperty("java.class.path");
		//classpath += ";"+Editor.getAIDirectory().getAbsolutePath()+"/";
    	classpath = Util.getAIDirectory().getAbsolutePath()+";"+classpath;
		System.setProperty("java.class.path", classpath);
		System.out.println("CP:"+classpath);
		//OS
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

    public static final int WIDTH = 640;
    public static final int HEIGHT= 480;
    public static boolean DEBUG = false;
    public static boolean paused = false;
    
    public BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    public double hScale = 1.0f;
    public double vScale = 1.0f;
    public int hOffset = 0;
    public int vOffset = 0;
    public boolean isFullScreen = false;
    public KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
    	public boolean dispatchKeyEvent(KeyEvent e) {
                int code = e.getKeyCode();
                if (code < 256) {
                    if (KeyEvent.KEY_PRESSED == e.getID()) {
                        Keyboard.keys[code] = true;
                    }
                    if (KeyEvent.KEY_RELEASED == e.getID()) {
                        Keyboard.keys[code] = false;
                    }
                }
                return true;
            }
    };

    //loader and opening screen static loading order is important
    public Menu menu = new Menu(this);
    public ArrayList<Entity> openingScreens = menu.getMenus();

    public VD() {
    	VD vd = this;
    	new Thread(new Runnable() {
    		@Override
    		public void run() {
    			vd.initGame();
    		}
    	}).start();
    }

    //Game Initialization - Place something here if you only want it to happen globally when the game is started.
    public void initGame() {
        AudioPlayer.init();
        AudioPlayer.stopAll();
        running = true;
        JFrame frame = this;
        this.frame = this;
        if (os == VD.OS.WIN) {
            frame.setMinimumSize(new Dimension(WIDTH+18 , HEIGHT+30));
        } else {
            frame.setMinimumSize(new Dimension(WIDTH , HEIGHT));
        }
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screen.width/2)-(WIDTH/2), (screen.height/2)-(HEIGHT/2), WIDTH, HEIGHT);
        frame.setBackground(Color.black);
        frame.setTitle("Victory Dispatcher");
        WindowManager.registerWindow(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        g = frame.getContentPane().getGraphics();
        frame.getContentPane().add(gamePanel);
        runningGamePanel = gamePanel;
        frame.pack();
        frame.setVisible(true);
        String fullscreenString = Util.getProperty("is-fullscreen");
        if ("yes".compareTo(fullscreenString)==0) {
        	toggleFullScreen();
        }
        AudioPlayer.OPENER.play();
        //KEYBOARD
        manager.addKeyEventDispatcher(dispatcher);
        while (running) {
            gameLoop();
        }
    }
    
    public void destroyGame() {
    	manager.removeKeyEventDispatcher(dispatcher);
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
        if (Keyboard.keys[KeyEvent.VK_ESCAPE] || Keyboard.keys[KeyEvent.VK_Q]) {
            System.exit(0);
        }
        //TOGGLE KEYS
        if (Keyboard.keys[KeyEvent.VK_F]) {
            toggleFullScreen();
            Keyboard.keys[KeyEvent.VK_F] = false;
        }
        if (Keyboard.keys[KeyEvent.VK_P]) {
            paused = !paused;
            Keyboard.keys[KeyEvent.VK_P] = false;
        }
        if (Keyboard.keys[KeyEvent.VK_H]) {
            DEBUG = !DEBUG;
            Keyboard.keys[KeyEvent.VK_H] = false;
        }
    }
    
    //UPDATE LOOP
    public void update(long dt) {
        try {
            if (openingScreens != null) {
                if (!openingScreens.isEmpty()) {
                    Entity screen = openingScreens.get(0);
                    screen.update(dt);
                    if (screen.isOld() || Keyboard.keys[KeyEvent.VK_SPACE] || Keyboard.keys[KeyEvent.VK_ENTER]) {
                        Keyboard.keys[KeyEvent.VK_SPACE] = false;
                        Keyboard.keys[KeyEvent.VK_ENTER] = false;
                        openingScreens.remove(0);
						if (openingScreens.isEmpty()) {
						    AudioPlayer.OPENER.stop();
						    currentRoom = new Room(this, menu.t1, menu.t2, menu.t3, menu.t4);
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
            g2.scale(hScale, vScale);
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

    public Point getOriginOnScreen() {
        if (runningGamePanel != null) {
            return runningGamePanel.getLocationOnScreen();
        } else {
            return null;
        }
    }

    public void toggleFullScreen() {
        if (frame != null) {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            if (isFullScreen) {
                isFullScreen = !isFullScreen;
                Util.setProperty("is-fullscreen", "no");
                frame.removeNotify();
                hScale = 1.0;
                vScale = 1.0;
                hOffset = 0;
                vOffset = 0;
                if (os == VD.OS.WIN) {
                    frame.setBounds((screen.width/2)-(WIDTH/2), (screen.height/2)-(HEIGHT/2), WIDTH+18, HEIGHT+30);
                } else {
                    frame.setBounds((screen.width/2)-(WIDTH/2), (screen.height/2)-(HEIGHT/2), WIDTH, HEIGHT);
                }
                frame.setUndecorated(false);
                frame.addNotify();
            } else {
                isFullScreen = !isFullScreen;
                Util.setProperty("is-fullscreen", "yes");
                double hRatio = (double)WIDTH/screen.width;
                double vRatio = (double)HEIGHT/screen.height;
                double windowRatio = (double)WIDTH/HEIGHT;
                double screenRatio = (double)screen.width/screen.height;
                if (windowRatio > screenRatio) { //restrict width
                    hScale = hScale/hRatio;
                    vScale = vScale/hRatio;
                    hOffset = (int)((screen.width - (hScale*WIDTH))/2.0);
                    vOffset = (int)((screen.height - (vScale*HEIGHT))/2.0);
                } else { //restrict height
                    hScale = hScale/vRatio;
                    vScale = vScale/vRatio;
                    hOffset = (int)((screen.width - (hScale*WIDTH))/2.0);
                    vOffset = (int)((screen.height - (vScale*HEIGHT))/2.0);
                }
                frame.removeNotify();
                frame.setUndecorated(true);
                frame.setBounds(0, 0, screen.width, screen.height);
                frame.addNotify();
            }
            // Since the focus is frozen all keys will also be frozen.
            // REMOVE FROZEN KEYS
            for (int i = 0; i < 256; i++) {
                Keyboard.keys[i] = false;
            }
            //REQUEST FOCUS
            frame.requestFocus();
        }
    }
    

    public static void main(String[] args) {
    	VD game = new VD();
    }

}
