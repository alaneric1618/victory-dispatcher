import java.lang.*;
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
                Tank t = (Tank)Class.forName(className).newInstance();
                String name = t.getName();
                if (name == null) {
                    name = className;
                }
                t.setName(name);
                tanks.add(t);
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
	classes = new ArrayList<Class>();
	String classpath = System.getProperty("java.class.path");
	String[] classpathEntries = classpath.split(File.pathSeparator);
	for (String cp : classpathEntries) {
	    System.out.println("CP: "+cp);
	    File dir = new File(cp);
	    File[] files = dir.listFiles();
	    for (File file : files) {
		if (file.isFile()) {
		    if (file.getName().endsWith(".class")) {
			FileInputStream fileInputStream=null;
			byte[] bytes = new byte[(int) file.length()];
			try {
			    fileInputStream = new FileInputStream(file);
			    fileInputStream.read(bytes);
			    fileInputStream.close();
			    Class c = this.getClass(bytes);
			    if (c != null && c.getSuperclass().getName().compareTo("Tank") == 0) {
				this.resolveClass(c);
				classes.add(c);
				System.out.println("CLASS: "+c.getName() + "    SUPER: " +c.getSuperclass().getName());
			    }
			} catch (Exception e) {
			    System.out.println(e);
			}
		    }
		}
	    }
	}
    }
}

