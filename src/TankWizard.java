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

public class TankWizard extends Tank implements TankInterface {
    
    {
        name = "The Wizard of ECS";
	try {
	    icon = ImageIO.read(new File("./media/wizard.png")); //Frames to animate
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
	
    }
}
