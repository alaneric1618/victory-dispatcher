
import javax.imageio.ImageIO;
import java.io.File;

public class TankWizard extends Tank implements TankInterface {
    
    {
        name = "The Wizard of ECS";
	try {
	    icon = ImageIO.read(VD.class.getResourceAsStream("/media/wizard.png")); //Frames to animate
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
