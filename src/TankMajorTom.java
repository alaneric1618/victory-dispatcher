import javax.imageio.ImageIO;


public class TankMajorTom extends Tank implements TankInterface {

  {
    name = "Major Tom";
    try {
      icon = ImageIO.read(VD.class.getResourceAsStream("/media/tom.png")); // Frames to animate
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
    time += (double) dt;
    turnTurret(1.0, false);
    if (time > 1000) {
      fire();
      time = 0.0;
    }
  }
}
