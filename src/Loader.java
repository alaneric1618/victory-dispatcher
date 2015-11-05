
import java.io.*;
import java.util.*;

public class Loader extends ClassLoader {

    public ArrayList<Class> classes;

    public Loader() {
        findClasses();
    }

    public ArrayList<Tank> getTanks() {
        ArrayList<Class> classes = this.getTankClasses();
        ArrayList<Tank> tanks = new ArrayList<Tank>();
        for (Class c : classes) {
            try {
                String className = c.getName();
                Tank t = null;
                String name = "";
                t = (Tank)Class.forName(className).newInstance();
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
        System.out.println(classes.size()+" classes loaded!");
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
		if (cp.endsWith(".jar")) {
                    continue;
                }
                try {
                    File dir = new File(cp);
                    File[] files = dir.listFiles();
                    for (File file : files) {
                        if (file.isFile()) {
                            if (file.getName().endsWith(".class") && !isExcluded(file)) {
                                FileInputStream fileInputStream=null;
                                byte[] bytes = new byte[(int) file.length()];
                                fileInputStream = new FileInputStream(file);
                                fileInputStream.read(bytes);
                                fileInputStream.close();
                                Class c = this.getClass(bytes);
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
                            if (file.getName().endsWith(".class") && !isExcluded(file)) {
                                FileInputStream fileInputStream=null;
                                byte[] bytes = new byte[(int) file.length()];
                                fileInputStream = new FileInputStream(file);
                                fileInputStream.read(bytes);
                                fileInputStream.close();
                                Class c = this.getClass(bytes);
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
    
    private boolean isExcluded(File file) {
    	boolean matched = false;
    	String[] excluded = new String[] {
    		"Tank.class",
    		"TankInterface.class",
    		"Entity.class"
    	};
    	for (String name : excluded) {
    		if (name.compareTo(file.getName()) == 0) {
    			matched = true;
    		}
    	}
    	return matched;
    }
}

