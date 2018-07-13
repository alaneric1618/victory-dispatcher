import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Loader extends URLClassLoader {

  public ArrayList<Class> classes;

  public static URL[] urls;
  static {
    ArrayList<URL> urlsArray = new ArrayList<URL>();
    String classpath = System.getProperty("java.class.path");
    String[] classpathEntries = classpath.split(File.pathSeparator);
    for (String cp : classpathEntries) {
      if (cp.endsWith(".jar")) {
        continue;
      }
      try {
        File dir = new File(cp);
        urlsArray.add(dir.toURI().toURL());
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Could not load URLS for URLClassLoader: " + cp);
      }
    }
    urls = new URL[urlsArray.size()];
    int i = 0;
    for (URL u : urlsArray) {
      urls[i++] = u;
    }
  }

  public Loader() {
    super(urls, VD.class.getClassLoader());
    //super(urls, Util.class.getClassLoader());
    findClasses();
    getTankClasses();
  }

  public ArrayList<Tank> getTanks() {
    ArrayList<Class> classes = this.getTankClasses();
    ArrayList<Tank> tanks = new ArrayList<Tank>();
    for (Class c : classes) {
      try {
          if (c == null) {
              continue;
          }
        String className = c.getName();
        if (className.contains("$"))
          continue;
        Object o = c.newInstance();// was null
        Tank t = null;
        if (o instanceof Tank) {
          t = (Tank) o;// was null
        } else {
          System.out.println("Could not cast " + className + " to Tank.");
          continue;
        }
        String name = "";
        // t = (Tank)Class.forName(className).newInstance();
        name = t.getName();
        if (name == null) {
          name = className;
        }
        t.setName(name);
        if ("Player".compareTo(name) == 0) {
          tanks.add(0, t);
        } else {
          tanks.add(t);
        }
      } catch (Throwable t) {
        System.out.println(t);
      }
    }
    System.out.println(classes.size() + " classes loaded!");
    return tanks;
  }

  private ArrayList<Class> getTankClasses() {
    return classes;
  }

  private Class getClass(byte[] bytes) {
    Class c = null;
    try {
      c = this.defineClass(null, bytes, 0, bytes.length);
    } catch (Throwable t) {
      System.out.println(t);
    }
    return c;
  }

  private void findClasses() {
    if (Util.getOS() == Util.OS.WIN) {
      classes = new ArrayList<Class>();
      String classpath = System.getProperty("java.class.path");
      String[] classpathEntries = classpath.split(File.pathSeparator);
      for (String cp : classpathEntries) {
        try {
          // Is the class in a jar file
          if (cp.endsWith(".jar")) {
            JarFile jar = new JarFile(cp);
            if (jar.getEntry("VD.class") != null || jar.getEntry("VD.java") != null) {
              Enumeration<JarEntry> entries = jar.entries();
              while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                boolean isClass = entry.getName().contains(".class");
                boolean hasTank = entry.getName().contains("Tank");
                boolean isNotInterface = entry.getName().compareTo("Tank.class") != 0;
                boolean isNotExcluded = !isExcluded(entry.getName());
                if (isClass && hasTank && isNotInterface && isNotExcluded) {
                  InputStream is = jar.getInputStream(entry);
                  byte[] bytes = new byte[(int) entry.getSize()];
                  is.read(bytes);
                  is.close();
                  Class c = this.getClass(bytes);
                  this.resolveClass(c);
                  System.out.println("Loaded Class: "+c.getName() + "    Classloader: " + c.getClassLoader() + "     Super: " + c.getSuperclass() + "    Superloader: " +c.getSuperclass().getClassLoader());
                  classes.add(c);
                }
              }
            }
            // Or a regular directory
          } else {
            File dir = new File(cp);
            File[] files = dir.listFiles();
            for (File file : files) {
              if (file.isFile()) {
                if (file.getName().endsWith(".class") && !isExcluded(file.getName())) {
                  FileInputStream fileInputStream = null;
                  byte[] bytes = new byte[(int) file.length()];
                  fileInputStream = new FileInputStream(file);
                  fileInputStream.read(bytes);
                  fileInputStream.close();
                  Class c = this.getClass(bytes);
                  if (c != null && c.getSuperclass().getName().compareTo("Tank") == 0) {
                    this.resolveClass(c);
                    System.out.println("Loaded Class: "+c.getName() + "    Classloader: " + c.getClassLoader() + "     Super: " + c.getSuperclass() + "    Superloader: " +c.getSuperclass().getClassLoader());
                    classes.add(c);
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("Could not load files in classpath: " + cp);
        }
      }
    } else {
      classes = new ArrayList<Class>();
      String classpath = System.getProperty("java.class.path");
      String[] classpathEntries = classpath.split(";");
      for (String cp : classpathEntries) {
          if (cp.endsWith(".jar")) {
              continue;
          }
          try {
              File dir = new File(cp);
              File[] files = dir.listFiles();
              for (File file : files) {
                  if (file.isFile()) {
                      if (file.getName().endsWith(".class") && !isExcluded(file.getName())) {
                          FileInputStream fileInputStream=null;
                          byte[] bytes = new byte[(int) file.length()];
                          fileInputStream = new FileInputStream(file);
                          fileInputStream.read(bytes);
                          fileInputStream.close();
                          //Class c = this.getClass(bytes);
                          Class c = this.loadClass(file.getName().replace(".class", ""));
                          if (c != null && c.getSuperclass().getName().compareTo("Tank") == 0) {
                              this.resolveClass(c);
                              classes.add(c);
                              System.out.println("CLASS: "+c.getName() + "    SUPER: " +c.getSuperclass().getName());
                          }
                      }
                  }
              }
          } catch (Exception e) {
              e.printStackTrace();
              System.out.println("Could not load files in classpath: "+cp);
          }
      }
    }
  }

  private boolean isExcluded(String s) {
    boolean matched = false;
    String[] excluded =
        new String[] {"VD.class", "Tank.class", "TankInterface.class", "Entity.class",
            "Reloader.class", "Keyboard.class", "VisibleEntity.class", "VisibleEntity$Side.class",
            "VisibleEntity$Type.class"};
    for (String name : excluded) {
      if (name.compareTo(s) == 0) {
        matched = true;
      }
    }
    return matched;
  }
}
