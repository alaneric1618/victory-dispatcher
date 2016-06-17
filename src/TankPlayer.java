import javax.imageio.ImageIO;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class TankPlayer extends Tank implements TankInterface {
  {
    name = "Player";
    try {
      icon = ImageIO.read(VD.class.getResourceAsStream("/media/player.png")); // Frames to animate
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean[] keys = new boolean[256];

  public void onCreation() {
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      public void eventDispatched(AWTEvent event) {
        // Point o = VD.getOriginOnScreen();
        // Point s = MouseInfo.getPointerInfo().getLocation();
        // int x = (int)(((s.x-o.x)-VD.hOffset)/VD.hScale);
        // int y = (int)(((s.y-o.y)-VD.vOffset)/VD.vScale);
        // Point p = new Point(x, y);
        // turnTurretTo(p.x, p.y);
      }
    }, AWTEvent.MOUSE_MOTION_EVENT_MASK);
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      public void eventDispatched(AWTEvent event) {
        if (event.getID() == MouseEvent.MOUSE_CLICKED) {
          // fire();
        }
      }
    }, AWTEvent.MOUSE_EVENT_MASK);
  }

  public void onHit() {

  }

  public void loop(float dt) {
    HashSet<VisibleEntity> ents = getVisibleEntities();
    // for (VisibleEntity ent : ents) {
    // System.out.print(String.format("[SIDE:%4s TYPE:%4s  X:%4s Y:%4s]  ", ent.side, ent.type,
    // ent.rect.x, ent.rect.y));
    // }
    // System.out.println();
    handleUserControl();
  }

  final private void handleUserControl() {
    boolean[] keys = Keyboard.keys;
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
    if (keys[KeyEvent.VK_L]) {
      lockTurret();
    }
  }

}
