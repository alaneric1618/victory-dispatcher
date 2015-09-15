import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.util.*;

public class Text extends Entity {

    public enum Align {
	LEFT,
	RIGHT,
	CENTER;
    }
    
    public static BufferedImage textImage;
    public static BufferedImage textImageSelected;
    static {
	try {
	    textImage = ImageIO.read(new File("./media/text.png"));
	    textImageSelected = ImageIO.read(new File("./media/text.png"));
	} catch(Exception e) {
	    e.printStackTrace();
	}
	for (int x = 0; x < textImage.getWidth(); x++) {
            for (int y = 0; y < textImage.getHeight(); y++) {
                int alpha = (textImage.getRGB(x, y) >> 24);
		int lum = (textImage.getRGB(x, y) & 0x00ffffff);
		lum = ((lum>>16)&0xff)+((lum>>8)&0xff)+((lum>>0)&0xff);
                if (alpha != 0) {
		    if (lum > 255) {
			textImage.setRGB(x, y, 0xffeeeeee);
		    } else {
			textImage.setRGB(x, y, 0xff222222);
		    }
                }
            }
        }
	for (int x = 0; x < textImageSelected.getWidth(); x++) {
            for (int y = 0; y < textImageSelected.getHeight(); y++) {
                int alpha = (textImageSelected.getRGB(x, y) >> 24);
		int lum = (textImageSelected.getRGB(x, y) & 0x00ffffff);
		lum = ((lum>>16)&0xff)+((lum>>8)&0xff)+((lum>>0)&0xff);
                if (alpha != 0) {
		    if (lum > 255) {
			textImageSelected.setRGB(x, y, 0xff00ffff);
		    } else {
			textImageSelected.setRGB(x, y, 0xff002020);
		    }
                }
            }
        }
    }

    private final String text;
    private final Text.Align align;
    private final int width;
    private final boolean selected;
    int[][] ijs;
    private int h = 0;

    public Text(final String text) {
	this(text, 1.0);
    }
    
    public Text(final String text, double size) {
	this(text, size, Text.Align.LEFT);
    }

    public Text(final String text, double size, Text.Align align) {
	this(text, size, align, false);
    }

    public Text(final String text, double size, Text.Align align, boolean selected) {
	super();
	this.text = text;
	this.align = align;
	this.selected = selected;
	spriteSize = (float)size;
	initIJS();
	int w = 0;
	for (int i = 0; i < ijs.length; i++) {
	    w += ijs[i][2];
	}
	width = (int)(spriteSize*w);
    }

    private void initIJS() {
	ijs = new int[text.length()][4];
	for (int i = 0; i < text.length(); i++) {
	    char c = text.charAt(i);
	    ijs[i] = getIJFromChar(c);
	}
    }

    private int[] getIJFromChar(final char c) {
	int[] ij = new int[4];
	switch (c) {
	case '\n':ij[0] = 1;   ij[1] = 1;       ij[2] = 1;  ij[3] = 1;  break;
	case ' ': ij[0] = 700; ij[1] = 2*70+0;  ij[2] = 40; ij[3] = 70; break;
	case 'A': ij[0] = 0;   ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'B': ij[0] = 68;  ij[1] = 3*70+0;  ij[2] = 60; ij[3] = 70; break;
	case 'C': ij[0] = 133; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'D': ij[0] = 195; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'E': ij[0] = 259; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'F': ij[0] = 325; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'G': ij[0] = 390; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'H': ij[0] = 452; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'I': ij[0] = 515; ij[1] = 3*70+0;  ij[2] = 32; ij[3] = 70; break;
	case 'J': ij[0] = 555; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'K': ij[0] = 620; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'L': ij[0] = 683; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'M': ij[0] = 746; ij[1] = 3*70+0;  ij[2] = 62; ij[3] = 70; break;
	case 'N': ij[0] = 0;   ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'O': ij[0] = 75;  ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'P': ij[0] = 140; ij[1] = 4*70+2;  ij[2] = 56; ij[3] = 70; break;
	case 'Q': ij[0] = 204; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'R': ij[0] = 277; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'S': ij[0] = 340; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'T': ij[0] = 404; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'U': ij[0] = 475; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'V': ij[0] = 538; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'W': ij[0] = 603; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'X': ij[0] = 683; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'Y': ij[0] = 747; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'Z': ij[0] = 819; ij[1] = 4*70+2;  ij[2] = 62; ij[3] = 70; break;
	case 'a': ij[0] = 0;   ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'b': ij[0] = 68;  ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'c': ij[0] = 131; ij[1] = 6*70+22; ij[2] = 52; ij[3] = 70; break;
	case 'd': ij[0] = 186; ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'e': ij[0] = 249; ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'f': ij[0] = 314; ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'g': ij[0] = 373; ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'h': ij[0] = 435; ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'i': ij[0] = 501; ij[1] = 6*70+22; ij[2] = 40; ij[3] = 70; break;
	case 'j': ij[0] = 548; ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'k': ij[0] = 602; ij[1] = 6*70+22; ij[2] = 60; ij[3] = 70; break;
	case 'l': ij[0] = 667; ij[1] = 6*70+22; ij[2] = 40; ij[3] = 70; break;
	case 'm': ij[0] = 715; ij[1] = 6*70+22; ij[2] = 70; ij[3] = 70; break;
	case 'n': ij[0] = 796; ij[1] = 6*70+22; ij[2] = 56; ij[3] = 70; break;
	case 'o': ij[0] = 0;   ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'p': ij[0] = 68;  ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'q': ij[0] = 131; ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'r': ij[0] = 194; ij[1] = 7*70+24; ij[2] = 54; ij[3] = 70; break;
	case 's': ij[0] = 260; ij[1] = 7*70+24; ij[2] = 56; ij[3] = 70; break;
	case 't': ij[0] = 322; ij[1] = 7*70+24; ij[2] = 52; ij[3] = 70; break;
	case 'u': ij[0] = 378; ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'v': ij[0] = 442; ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'w': ij[0] = 508; ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'x': ij[0] = 588; ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'y': ij[0] = 652; ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	case 'z': ij[0] = 715; ij[1] = 7*70+24; ij[2] = 60; ij[3] = 70; break;
	}
	return ij;
    }
    
    @Override
    public void update(float dt) {
	super.update(dt);
    }

    @Override
    public void draw(Graphics2D g) {
	this.draw(g, 0, 0);
    }

    public void draw(Graphics2D g, int x, int y) {
	super.draw(g);
	switch (align) {
	case LEFT:
	    this.boundingSprite = new Rectangle(x, y, 70, 70);
	    break;
	case RIGHT:
	    this.boundingSprite = new Rectangle(x-(width), y, 70, 70);
	    break;
	case CENTER:
	    this.boundingSprite = new Rectangle(x-(width/2), y, 70, 70);
	    break;
	}
	for (int i = 0; i < ijs.length; i++) {
	    Rectangle r = boundingSprite;
	    int dx = (int)(spriteSize*ijs[i][2]);
	    drawSprite(g, ijs[i][0], ijs[i][1], ijs[i][2], ijs[i][3]);
	    this.boundingSprite = new Rectangle(r.x+dx, r.y, r.width, r.height);
	}
    }

    @Override
    public void drawSprite(Graphics2D g, int i, int j, int w, int h) {
	int offset = 4;
	double x = boundingSprite.getX();
	double y = boundingSprite.getY();
	g.clipRect((int)x, (int)y, (int)(w*spriteSize), (int)(h*spriteSize));
	if (selected) {
	    g.drawImage(textImageSelected, new AffineTransform(spriteSize, 0f , 0f , spriteSize, (x-(i*spriteSize)), (y-(j*spriteSize))), null);
	} else {
	    g.drawImage(textImage, new AffineTransform(spriteSize, 0f , 0f , spriteSize, (x-(i*spriteSize)), (y-(j*spriteSize))), null);
	}
	g.setClip(new Rectangle(0, 0, VD.WIDTH, VD.HEIGHT));
    }

}
