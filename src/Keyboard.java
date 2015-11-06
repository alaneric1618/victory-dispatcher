
public class Keyboard {

	public static boolean[] keys = new boolean[256];
	static {
		resetKeyboard();
	}

	public static void resetKeyboard() {
		for (int i = 0; i < 256; i++) {
			Keyboard.keys[i] = false;
		}
	}
	
}
