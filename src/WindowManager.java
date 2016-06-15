import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class WindowManager implements WindowListener {
	
	public static void registerWindow(JFrame frame) {
		frame.addWindowListener(new WindowManager(frame));
	}
	
	public JFrame window;
	private WindowManager(JFrame frame) {
		super();
		window = frame;
	}
	

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (window instanceof VD) {
			((VD)window).destroyGame();
		}
		Frame[] frames = Frame.getFrames();
		int frameCount = 0;
		for (Frame frame : frames) {
			if (frame.isActive()) {
				frameCount++;
			}
		}
		if (frameCount <= 0) {
			System.exit(0);
		}
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
