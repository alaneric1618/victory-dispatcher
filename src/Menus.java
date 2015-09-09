import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Menus {

    public static ArrayList<Entity> getMenus() {
        ArrayList<Entity> menus = new ArrayList<Entity>();
        menus.add(black);
        menus.add(mcleod);
        menus.add(opener);
        menus.add(mainMenu);
        return menus;
    }

    private static Entity black = new Entity() {
            {this.setMaxAge(1000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, 640, 480);
            }
    };

    private static Entity mcleod = new Entity() {
            {this.setMaxAge(3000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.yellow);
                g.fillRect(0, 0, 640, 480);
            }
    };

    private static Entity opener = new Entity() {
            {this.setMaxAge(3000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.blue);
                g.fillRect(0, 0, 640, 480);
            }
    };

    private static Entity mainMenu = new Entity() {
            {this.setMaxAge(3000L);}
            public void update(float dt) {
                super.update(dt);
            }
            public void draw(Graphics2D g) {
                g.setColor(Color.red);
                g.fillRect(0, 0, 640, 480);
            }
    };

    





}

