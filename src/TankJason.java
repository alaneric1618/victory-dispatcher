import javax.imageio.ImageIO;

public class TankJason extends Tank implements TankInterface {

  {
    name = "\"Jason\" Burtone";
    try {
      icon = ImageIO.read(VD.class.getResourceAsStream("/media/burtone.png")); // Frames to animate
    } catch (Exception e) {
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
