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

public class TankPlayer extends Tank implements TankInterface {
    private boolean[] keys = new boolean[256];
    
    public void onCreation() {
	KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
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
    }
    
    public void onHit() {

    }
    
    public void loop(float dt) {
	HashSet<VisibleEntity> ents = getVisibleEntities();
	// for (VisibleEntity ent : ents) {
	//     System.out.print(String.format("[SIDE:%4s TYPE:%4s  X:%4s Y:%4s]  ", ent.side, ent.type, ent.rect.x, ent.rect.y));
	// }
	// System.out.println();

	handleUserControl();
    }

    final private void handleUserControl() {
	if (keys[KeyEvent.VK_A]) {
	    turnTread(-5.0, false);
	}
	if (keys[KeyEvent.VK_D]) {
	    turnTread(5.0, false);
	}
	if (keys[KeyEvent.VK_W]) {
	    forward();
	}
	if (keys[KeyEvent.VK_S]) {
	    backward();
	}
	if (keys[KeyEvent.VK_J]) {
	    turnTurret(-5.0, false);
	}
	if (keys[KeyEvent.VK_K]) {
	    turnTurret(5.0, false);
	}
        if (keys[KeyEvent.VK_SPACE]) {
            fire();
        }
        if (keys[KeyEvent.VK_1]) {
            VD.DEBUG = false;
        }
        if (keys[KeyEvent.VK_2]) {
            VD.DEBUG = true;
        }
        if (keys[KeyEvent.VK_L]) {
            lockTurret();
        }
    }

}

