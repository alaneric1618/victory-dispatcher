import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class Editor extends JFrame {
	
	public static final String template = ""
			+ "import javax.imageio.ImageIO;" + "\n"
			+ "import java.io.File;" + "\n"
			+ "" + "\n"
			+ "public class Tank<INSERT-YOUR-NAME> extends Tank implements TankInterface {" + "\n"
			+ "\t// INSTANCE BLOCK - EXECUTES WHEN INSTANCE IS CREATED" + "\n"
			+ "\t//    Used to initialize name and icon for dynamic class loading." + "\n"
			+ "\t{" + "\n"
			+ "\t\tname = \"<INSERT-YOUT-NAME>\";" + "\n"
			+ "\t\ttry {" + "\n"
			+ "\t\t\ticon = ImageIO.read(new File(\"<INSERT-YOUR-FILENAME>\"));" + "\n"
			+ "\t\t} catch(Exception e) {" + "\n"
			+ "\t\t\t" + "\n"
			+ "\t\t}" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t// ELAPSED TIME" + "\n"
			+ "\t//    Can be used to see how long the tank has been alive," + "\n"
			+ "\t//    or manage state changes if desired." + "\n"
			+ "\tfloat time = 0.0f;" + "\n"
			+ ""  + "\n"
			+ "\t// ONCREATION - Called when the tank is created." + "\n"
			+ "\t//    Comes from TankInterface" + "\n"
			+ "\tpublic void onCreation() {" + "\n"
			+ "\t\t//TODO: Write initialization code." + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t// ONHIT - Bullet Collision Callback." + "\n"
			+ "\t//    Comes from TankInterface" + "\n"
			+ "\tpublic void onHit() {" + "\n"
			+ "\t\t//TODO: Write callback for collision events." + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t// LOOP - Primary loop code." + "\n"
			+ "\t//    Comes from TankInterface" + "\n"
			+ "\tpublic void loop(float dt) {" + "\n"
			+ "\t\ttime += dt;" + "\n"
			+ "\t\t//TODO: Write AI loop code." + "\n"
			+ "\t}" + "\n"
			+ "}" + "\n"
			+ "" + "\n";

	RSyntaxTextArea textArea = new RSyntaxTextArea(20, 80);
	JLabel compileLabel = new JLabel("", SwingConstants.CENTER);
	JButton compileButton = new JButton("Save/Compile");
	JButton runButton = new JButton("Run");
	JLabel uploadLabel = new JLabel("Upload Key:", SwingConstants.RIGHT);
	JTextField uploadField = new JTextField();
	JButton uploadButton = new JButton("Upload");
	JLabel javacLabel = new JLabel("javac:", SwingConstants.RIGHT);
	JTextField javacField = new JTextField();
	JButton javacButton = new JButton("Choose");
	
	JLabel statusLabel = new JLabel(" ");
	JPanel statusBar = new JPanel();
	ArrayList<String> queuedStatuses = new ArrayList<String>();
	
	SaveAction saveAction = new SaveAction();
	OpenAction openAction = new OpenAction();
	CompileAction compileAction = new CompileAction();
	RunAction runAction = new RunAction();
	UploadAction uploadAction = new UploadAction();
	ChooseAction chooseAction = new ChooseAction();
	
	public Editor() {
		init();
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
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
			
		});
	}
	
	public void init() {
		
		UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Monospaced", Font.PLAIN, 14)));
		
		JPanel contentPane = new JPanel(new BorderLayout());
		
		JPanel controlPanel = new JPanel();
		controlPanel.setBounds(0, 0, 640, 200);
		controlPanel.setMinimumSize(new Dimension(640, 200));
		controlPanel.setLayout(new GridLayout(0, 9));
		
		compileButton.addActionListener(compileAction);
		runButton.addActionListener(runAction);
		javacButton.addActionListener(chooseAction);
		
		javacField.setEditable(false);
		javacField.setText(Util.getProperty("javac-path"));
		uploadField.setEditable(false);
		uploadField.setText(Util.getProperty("upload-key"));
		
		controlPanel.add(compileButton);
		controlPanel.add(runButton);
		controlPanel.add(compileLabel);
		controlPanel.add(uploadLabel);
		controlPanel.add(uploadField);
		controlPanel.add(uploadButton);
		controlPanel.add(javacLabel);
		controlPanel.add(javacField);
		controlPanel.add(javacButton);
		
		textArea.setBackground(new Color(249, 249, 249));
		textArea.setText(template);
		textArea.setCaretPosition(0);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(false);
		RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		
		statusBar.setMinimumSize(new Dimension(640, 24));
		statusBar.setBounds(0, 0, 640, 25);
		statusBar.add(statusLabel);
		contentPane.add(controlPanel, BorderLayout.NORTH);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(statusBar, BorderLayout.SOUTH);
		
		Menu fileMenu = new Menu("File");
		MenuItem fileNew = new MenuItem("New");
		MenuItem fileOpen = new MenuItem("Open");
		fileOpen.addActionListener(openAction);
		MenuItem fileOpenRecent = new MenuItem("Open Recent");
		MenuItem fileSave = new MenuItem("Save (managed)");
		fileSave.addActionListener(saveAction);
		MenuItem fileSaveAs = new MenuItem("Save As...");
		fileMenu.add(fileNew);
		fileMenu.addSeparator();
		fileMenu.add(fileOpen);
		//fileMenu.add(fileOpenRecent);
		fileMenu.addSeparator();
		fileMenu.add(fileSave);
		//fileMenu.add(fileSaveAs);
		
		Menu editMenu = new Menu("Edit");
		MenuItem editCopy = new MenuItem("Copy");
		MenuItem editCut = new MenuItem("Cut");
		MenuItem editPaste = new MenuItem("Paste");
		MenuItem editClear = new MenuItem("Clear");
		editMenu.add(editCopy);
		editMenu.add(editCut);
		editMenu.add(editPaste);
		editMenu.add(editClear);
		
		Menu javaMenu = new Menu("Java");
		MenuItem javaCompile = new MenuItem("Compile");
		MenuItem javaTest = new MenuItem("Run");
		MenuItem javaUpload = new MenuItem("Upload");
		javaMenu.add(javaCompile);
		javaMenu.add(javaTest);
		javaMenu.add(javaUpload);
		
		Menu helpMenu = new Menu("Help");
		MenuItem helpHelp = new MenuItem("Documentation");
		helpMenu.add(helpHelp);
		
		MenuBar menuBar = new MenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(javaMenu);
		menuBar.add(helpMenu);
		
		this.setMenuBar(menuBar);
		this.setContentPane(contentPane);
		this.setTitle("Victory Dispatcher - AI Creator");
		WindowManager.registerWindow(this);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.setMinimumSize(new Dimension(1050, 480));
		this.setLocationRelativeTo(null);	
	}
	
	class SaveAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			File aiDirectory = Util.getAIDirectory();
			// SAVE FILE
			if (aiDirectory.exists() && aiDirectory.isDirectory()) {
				String name = Util.getClassName(textArea.getText());
				if (!Editor.isNameAllowed(name)) {
					System.out.println("Tank filename already taken. Please choose another one.");
					return;
				}
				if (name.toUpperCase().matches("^[\\u0041-\\u005A]*$")) {
					System.out.println("Good filename: \""+name+"\"");
					String filename = aiDirectory.getAbsolutePath()+"\\"+name+".java";
					System.out.println(filename);
					File file = new File(filename);
					PrintWriter writer;
					try {
						writer = new PrintWriter(filename, "UTF-8");
						String bufferString = textArea.getText();
						String[] lines = bufferString.split("\n");
						for (String line : lines) {
							writer.println(line);
						}
						Util.setProperty("recent-save-filename", name);
						writer.close();
					} catch (FileNotFoundException | UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("Could not save file with name: \""+name+"\"");
					System.out.println("\t\tPlease use only ascii alpha characters.");
				}
				
			}
		}
	}
	
	class OpenAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			File aiDirectory = Util.getAIDirectory();
			String filename = "";
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(aiDirectory);
			fileChooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return null;
				}
				
				@Override
				public boolean accept(File f) {
					if (f.getName().matches(".*\\.java")) {
						return true;
					} else {
						return false;
					}
					
				}
			});
			int returnVal=fileChooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line+"\n");
					}
					textArea.setText(sb.toString());
					textArea.setCaretPosition(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				
			}
		}
	}
	
	class CompileAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			String name = Util.getClassName(textArea.getText());
			if (!Editor.isNameAllowed(name)) {
				JOptionPane.showMessageDialog(null, "Tank filename/classname already taken. Please choose another one.", "Error", JOptionPane.ERROR_MESSAGE);
				System.out.println();
				return;
			}
			String classPathString = " -cp ./bin";
			saveAction.actionPerformed(e);
			String javacString = javacField.getText().replaceAll("^C:", "c:");
			String aiPath = Util.getAIDirectory().getAbsolutePath();			
			String outputDirectory = " -d ./bin ";
			String cmd = javacString + outputDirectory + classPathString + " " + aiPath + "\\" + name + ".java";
			String output = Editor.executeCmd(cmd);
			System.out.println(cmd);
			System.out.println(output);
		}
	}
	
	class RunAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String recentFilename = Util.getProperty("recent-save-filename");
				Util.setProperty("startup-tank1-name", "TankPlayer");
				Util.setProperty("startup-tank2-name", recentFilename);
				Util.setProperty("startup-mode", "test");
				Util.restartApplication();
				Util.setProperty("startup-mode", "game");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Failed to reload the application", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class UploadAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveAction.actionPerformed(e);
		}
	}

	class ChooseAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			String filename = "";
			File home = Util.getAIDirectory();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(home);
			int returnVal=fileChooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				filename = fileChooser.getSelectedFile().getAbsolutePath();
				Util.setProperty("javac-path", filename);
				javacField.setText(filename);
			} else {
				
			}
		}
	}
	
	public static String executeCmd(String cmd) {
		StringBuilder sb = new StringBuilder();
		try {
			Process p;
			p = Runtime.getRuntime().exec("cmd /C "+cmd);
			p.waitFor();
			BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String line = "";
		    while ( (line = out.readLine()) != null) {
		    	sb.append(line + "\n");
		    }
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		    while ( (line = err.readLine()) != null) {
		    	sb.append(line + "\n");
		    }
		    if (p.exitValue() == 0) {
		    	JOptionPane.showMessageDialog(null, "Compilation Success", "Success", JOptionPane.PLAIN_MESSAGE);
		    	return sb.toString();
		    } else {
		    	JOptionPane.showMessageDialog(null, "Compilation Failed\n"+sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		    	return sb.toString();
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static boolean isNameAllowed(String name) {
		boolean isTaken = false;
		String[] taken = new String[] {
				"Tank",
				"TankInterface",
				"TankWizard",
				"TankJason",
				"TankMajorTom",
				"TankMinja",
				"TankPlayer"
		};
		for (String takenName : taken) {
			if (takenName.compareTo(name) == 0) {
				isTaken = true;
				break;
			}
		}
		return !isTaken;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Editor();
			}
		});
	}	
}

