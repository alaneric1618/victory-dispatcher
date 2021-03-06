import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.jar.JarFile;

public class Util {

  public enum OS {
    WIN, MAC, UNIX, UNKNOWN;

    public String toString() {
      String s = "Unknown Operating System";
      switch (this) {
        case WIN:
          s = "Windows";
          break;
        case MAC:
          s = "OSX";
          break;
        case UNIX:
          s = "Unix Based";
          break;
      }
      return s;
    }
  }
  

  private static Util.OS os = Util.OS.UNKNOWN;
  static {
    // OS
    String osString = System.getProperty("os.name");
    if (osString.toUpperCase().indexOf("WIN") >= 0) {
      os = Util.OS.WIN;
    } else if (osString.toUpperCase().indexOf("MAC") >= 0) {
      os = Util.OS.MAC;
    } else if (osString.toUpperCase().indexOf("NIX") >= 0) {
      os = Util.OS.UNIX;
    } else {
      os = Util.OS.UNKNOWN;
    }
  }

  public static Loader getNewLoader() {
    return new Loader();
  }

  public static Util.OS getOS() {
    return os;
  }

  private static Properties properties = null;
  static {
    Util.loadProperties();
  }

  public static String getClassName(String fileString) {
    String name = fileString;
    name = name.substring(name.indexOf("class") + 5, name.indexOf("extends")).trim();
    return name;
  }

  public static void restartApplication() throws Exception {
    try {
      System.out.println("Original DynamicClassLoader Closed Successfully");
    } catch (Throwable t) {
      t.printStackTrace();
    }
    VD game = new VD();
    game.setFocusable(true);
  }
  
  public static BufferedImage convertImageToNative(BufferedImage image) {
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice device = env.getDefaultScreenDevice();
    GraphicsConfiguration config = device.getDefaultConfiguration();
    BufferedImage buffy = config.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        buffy.setRGB(x, y, image.getRGB(x, y));
      }
    }
    return buffy;
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
      } catch (SecurityException se) {
        se.printStackTrace();
      }
    }
    return aiDirectory;
  }

  private static Properties getProperties() {
    return properties;
  }

  private static void setupDefaults() {
    if (!properties.containsKey("is-fullscreen")) {
      properties.put("is-fullscreen", "no");
    }
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
      if (Util.getOS() == Util.OS.WIN) {
        output = new FileOutputStream("ai\\editor.properties");
        setupDefaults();
        properties.store(output, null);
      } else {
        output = new FileOutputStream("./ai/editor.properties");
        setupDefaults();
        properties.store(output, null);
      }
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
    File file;
    if (Util.getOS() == Util.OS.WIN) {
      file = new File(aiDirectory.getAbsolutePath() + "\\editor.properties");
    } else {
      file = new File(aiDirectory.getAbsolutePath() + "/editor.properties");
    }
    if (!file.exists() && !file.isDirectory()) {
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

  public static boolean wasRunFromJar() {
    String classpath = System.getProperty("java.class.path");
    String[] classpathEntries = classpath.split(File.pathSeparator);
    for (String cp : classpathEntries) {
      try {
        // Is the class in a jar file
        if (cp.endsWith(".jar")) {
          JarFile jar = new JarFile(cp);
          if (jar.getEntry("VD.class") != null || jar.getEntry("VD.java") != null) {
            return true;
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
    return false;
  }

  public static String getPathToRunningJar() {
    String classpath = System.getProperty("java.class.path");
    String[] classpathEntries = classpath.split(File.pathSeparator);
    for (String cp : classpathEntries) {
      try {
        // Is the class in a jar file
        if (cp.endsWith(".jar")) {
          JarFile jar = new JarFile(cp);
          if (jar.getEntry("VD.class") != null || jar.getEntry("VD.java") != null) {
            return cp;
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
    return "";
  }


}
