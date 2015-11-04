import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class WindowManager implements WindowListener {
	
	private static int windowCount = 0;
	public static void registerWindow(JFrame frame) {
		frame.addWindowListener(new WindowManager(frame));
		windowCount++;
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
		windowCount--;
		if (window instanceof VD) {
			((VD)window).destroyGame();
		}
		if (windowCount <= 0) {
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
