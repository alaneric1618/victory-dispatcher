

import javax.imageio.ImageIO;

public class TankMinja extends Tank implements TankInterface {
    
    {
        name = "Minja";
	try {
	    icon = ImageIO.read(VD.class.getResourceAsStream("/media/minja.png")); //Frames to animate
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
