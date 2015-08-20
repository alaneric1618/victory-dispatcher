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

public class TankMajorTom extends Tank implements TankInterface {
    public void onCreation() {

    }
    public void onHit() {

    }
    
    public void loop(float dt) {
	HashSet<VisibleEntity> ents = getVisibleEntities();
	turnTurret(1.0, false);
    }
}
