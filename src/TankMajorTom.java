
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
    
    {
        name = "Major Tom";
	try {
	    icon = ImageIO.read(new File("./media/tom.png")); //Frames to animate
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    double time = 0.0;
    
    public void onCreation() {
	
    }
    public void onHit() {
	
    }
    
    public void loop(float dt) {
	time += (double)dt;
	turnTurret(1.0, false);
	if (time > 1000) {
	    fire();
	    time = 0.0;
	}
    }
}
