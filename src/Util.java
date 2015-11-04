import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Util {
	private static Properties properties = null;
	static {
		Util.loadProperties();
	}
	
	public static String getClassName(String fileString) {
		String name = fileString;
		name = name.substring(name.indexOf("class")+5, name.indexOf("extends")).trim();
		return name;
	}

	public static void restartApplication() throws Exception {
		VD game = new VD();
	}	

	public static void setProperty(String propertyName, String propertyValue) {
		Util.loadProperties();
		Util.getProperties().put(propertyName, propertyValue);
		Util.saveProperties();
	}
	
	public static String getProperty(String propertyName) {
		Util.loadProperties();
		String propertyValue = (String) Util.getProperties().get(propertyName);
		return propertyValue;
	}
	
	public static File getAIDirectory() {
		// CREATE AI DIRECTORY
		String aiDirectoryName = "ai";
		File aiDirectory = new File(aiDirectoryName);
		if (!aiDirectory.exists()) {
			System.out.println("creating directory: " + aiDirectoryName);
			try {
				aiDirectory.mkdir();
			} catch(SecurityException se) {
				se.printStackTrace();
			}
		}
		return aiDirectory;
	}

		private static Properties getProperties() {
		return properties;
	}
	
	private static void setupDefaults() {
		if (!properties.containsKey("upload-key")) {
			properties.put("upload-key", "Not Set");
		}
		if (!properties.containsKey("upload-key")) {
			properties.put("javac-path", "Not Set");
		}
		if (!properties.containsKey("startup-mode")) {
			// game, editor, test
			properties.put("startup-mode", "game");
		}
		if (!properties.containsKey("startup-tank1-name")) {
			properties.put("startup-tank1-name", "TankPlayer");
		}
		if (!properties.containsKey("startup-tank2-name")) {
			properties.put("startup-tank2-name", "TankMajorTom");
		}
		if (!properties.containsKey("recent-save-filename")) {
			properties.put("recent-save-filename", "TankMajorTom");
		}
	}
	
	private static void saveProperties() {
		OutputStream output = null;
		try {
			output = new FileOutputStream("ai\\editor.properties");
			setupDefaults();			
			properties.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void loadProperties() {
		File aiDirectory = Util.getAIDirectory();
		File file = new File(aiDirectory.getAbsolutePath()+"\\editor.properties");
		if(!file.exists() && !file.isDirectory()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(file));
			properties = props;
			setupDefaults();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
