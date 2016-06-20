import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.*;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class Editor extends JFrame {

  public static final String template = "" + "import javax.imageio.ImageIO;" + "\n"
      + "import java.io.File;" + "\n" + "import java.awt.*;" + "\n" + "import java.awt.geom.*;"
      + "\n" + "" + "\n"
      + "public class Tank<INSERT-YOUR-NAME> extends Tank implements TankInterface {" + "\n"
      + "\t// INSTANCE BLOCK - EXECUTES WHEN INSTANCE IS CREATED" + "\n"
      + "\t//    Used to initialize name and icon for dynamic class loading." + "\n" + "\t{" + "\n"
      + "\t\tname = \"<INSERT-YOUT-NAME>\";" + "\n" + "\t\ttry {" + "\n"
      + "\t\t\t//LOAD CUSTOM ICON (46x46 pixels)" + "\n"
      + "\t\t\t//icon = ImageIO.read(new File(\"./ai/<INSERT-YOUR-FILENAME>\"));" + "\n"
      + "\t\t\t// OR" + "\n" + "\t\t\t//DRAW CUSTOM ICON" + "\n"
      + "\t\t\t//Graphics g = icon.getGraphics();" + "\n" + "\t\t} catch(Exception e) {" + "\n"
      + "\t\t\t" + "\n" + "\t\t}" + "\n" + "\t}" + "\n" + "" + "\n" + "\t// ELAPSED TIME" + "\n"
      + "\t//    Can be used to see how long the tank has been alive," + "\n"
      + "\t//    or manage state changes if desired." + "\n" + "\tfloat time = 0.0f;" + "\n" + ""
      + "\n" + "\t// ONCREATION - Called when the tank is created." + "\n"
      + "\t//    Comes from TankInterface" + "\n" + "\tpublic void onCreation() {" + "\n"
      + "\t\t//TODO: Write initialization code." + "\n" + "\t}" + "\n" + "" + "\n"
      + "\t// ONHIT - Bullet Collision Callback." + "\n" + "\t//    Comes from TankInterface"
      + "\n" + "\tpublic void onHit() {" + "\n"
      + "\t\t//TODO: Write callback for collision events." + "\n" + "\t}" + "\n" + "" + "\n"
      + "\t// LOOP - Primary loop code." + "\n" + "\t//    Comes from TankInterface" + "\n"
      + "\tpublic void loop(float dt) {" + "\n" + "\t\ttime += dt;" + "\n"
      + "\t\t//TODO: Write AI loop code." + "\n" + "\t}" + "\n" + "}" + "\n" + "" + "\n";

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

  JLabel statusLabel = new JLabel(" TEST ");
  JPanel statusBar = new JPanel();

  NewAction newAction = new NewAction();
  SaveAction saveAction = new SaveAction();
  OpenAction openAction = new OpenAction();
  CompileAction compileAction = new CompileAction();
  RunAction runAction = new RunAction();
  UploadAction uploadAction = new UploadAction();
  ChooseAction chooseAction = new ChooseAction();
  ZoomInAction zoomInAction = new ZoomInAction();
  ZoomOutAction zoomOutAction = new ZoomOutAction();

  int fontSize = 20;

  TimerTask clearStatusTask = new TimerTask() {
    @Override
    public void run() {
      statusLabel.setText(" ");
    }
  };

  static {
    String classpath = System.getProperty("java.class.path");
    System.out.println("CP:" + classpath);
  }

  public Editor() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        init();
      }
    });
  }

  public void init() {
    UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Monospaced", Font.PLAIN,
        14)));
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
    textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
    RTextScrollPane scrollPane = new RTextScrollPane(textArea);

    this.setupAutoComplete(textArea);

    JPanel temp = new JPanel();
    temp.setBackground(Color.red);
    temp.setMinimumSize(new Dimension(640, 30));
    temp.setBounds(0, 0, 640, 30);
    statusLabel.setBounds(0, 0, 640, 25);
    statusBar.setMinimumSize(new Dimension(640, 30));
    statusBar.setBounds(0, 0, 640, 30);
    statusBar.add(temp);
    temp.setLayout(null);
    // temp.add(statusLabel);
    contentPane.add(controlPanel, BorderLayout.NORTH);
    contentPane.add(scrollPane, BorderLayout.CENTER);
    contentPane.add(statusBar, BorderLayout.SOUTH);

    JMenu fileMenu = new JMenu("File");
    JMenuItem fileNew = new JMenuItem("New");
    fileNew.addActionListener(newAction);
    JMenuItem fileOpen = new JMenuItem("Open");
    fileOpen.addActionListener(openAction);
    JMenuItem fileOpenRecent = new JMenuItem("Open Recent");
    JMenuItem fileSave = new JMenuItem("Save (managed)");
    fileSave.addActionListener(saveAction);
    JMenuItem fileSaveAs = new JMenuItem("Save As...");
    fileMenu.add(fileNew);
    fileMenu.addSeparator();
    fileMenu.add(fileOpen);
    // fileMenu.add(fileOpenRecent);
    fileMenu.addSeparator();
    fileMenu.add(fileSave);
    // fileMenu.add(fileSaveAs);

    JMenu viewMenu = new JMenu("View");
    JMenuItem zoomIn = new JMenuItem("Zoom In");
    JMenuItem zoomOut = new JMenuItem("Zoom Out");
    zoomIn.addActionListener(zoomInAction);
    zoomOut.addActionListener(zoomOutAction);
    viewMenu.add(zoomIn);
    viewMenu.add(zoomOut);

    JMenu javaMenu = new JMenu("Java");
    JMenuItem javaCompile = new JMenuItem("Compile");
    JMenuItem javaTest = new JMenuItem("Run");
    JMenuItem javaUpload = new JMenuItem("Upload");
    javaCompile.addActionListener(compileAction);
    javaTest.addActionListener(runAction);
    javaUpload.addActionListener(uploadAction);
    javaMenu.add(javaCompile);
    javaMenu.add(javaTest);
    javaMenu.add(javaUpload);

    // JMenu helpMenu = new JMenu("Help");
    // JMenuItem helpHelp = new JMenuItem("Documentation");
    // helpMenu.add(helpHelp);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);
    // menuBar.add(editMenu);
    menuBar.add(javaMenu);
    menuBar.add(viewMenu);
    // menuBar.add(helpMenu);

    fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    javaTest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
    javaUpload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
    javaCompile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
    zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.ALT_MASK));
    zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.ALT_MASK));


    this.setJMenuBar(menuBar);
    this.setContentPane(contentPane);
    this.setTitle("Victory Dispatcher - AI Creator");
    WindowManager.registerWindow(this);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setVisible(true);
    this.setMinimumSize(new Dimension(1050, 480));
    this.setLocationRelativeTo(null);

    new Thread(new Runnable() {
      int i = 0;

      @Override
      public void run() {
        System.out.println("STATUS LABEL: " + statusLabel);
        while (statusLabel != null) {
          if (statusLabel.getText().length() > 1) {
            i++;
            try {
              Thread.sleep(66);
            } catch (Exception e) {

            }
            String text = statusLabel.getText();
            text = " " + text;
            statusLabel.setText(text);
          }
        }
        System.out.println("CODE STOPPED");
      }

    }).start();
  }

  public void setupAutoComplete(RSyntaxTextArea area) {
    // Complete Provider
    DefaultCompletionProvider thisProvider = new DefaultCompletionProvider() {
      public boolean isAutoActivateOkay(JTextComponent tc) {
        Document doc = tc.getDocument();
        char ch = 0;
        try {
          String s1 = doc.getText(tc.getCaretPosition() - 4, 5);
          String s2 = doc.getText(tc.getCaretPosition() - 2, 3);
          if (s1.contains("this."))
            return true;
          if (s2.contains("for"))
            return true;
        } catch (BadLocationException ble) { // Never happens
          ble.printStackTrace();
        }
        return false;
      }
    };
    DefaultCompletionProvider entProvider = new DefaultCompletionProvider() {
      public boolean isAutoActivateOkay(JTextComponent tc) {
        Document doc = tc.getDocument();
        char ch = 0;
        try {
          String s1 = doc.getText(tc.getCaretPosition() - 3, 4);
          if (s1.contains("ent."))
            return true;
        } catch (BadLocationException ble) { // Never happens
          ble.printStackTrace();
        }
        return false;
      }
    };
    DefaultCompletionProvider statProvider = new DefaultCompletionProvider() {
      public boolean isAutoActivateOkay(JTextComponent tc) {
        Document doc = tc.getDocument();
        char ch = 0;
        try {
          String s1 = doc.getText(tc.getCaretPosition() - 2, 3);
          boolean is = false;
          if (s1.contains("vis"))
            is = true;
          if (is) {
            return true;
          }
        } catch (BadLocationException ble) { // Never happens
          ble.printStackTrace();
        }
        return false;
      }
    };
    DefaultCompletionProvider iconProvider = new DefaultCompletionProvider() {
      public boolean isAutoActivateOkay(JTextComponent tc) {
        Document doc = tc.getDocument();
        char ch = 0;
        try {
          String s1 = doc.getText(tc.getCaretPosition() - 4, 5);
          boolean is = false;
          if (s1.contains("icon."))
            is = true;
          if (is) {
            return true;
          }
        } catch (BadLocationException ble) { // Never happens
          ble.printStackTrace();
        }
        return false;
      }
    };
    DefaultCompletionProvider gProvider = new DefaultCompletionProvider() {
      public boolean isAutoActivateOkay(JTextComponent tc) {
        Document doc = tc.getDocument();
        char ch = 0;
        try {
          String s1 = doc.getText(tc.getCaretPosition() - 1, 2);
          boolean is = false;
          if (s1.contains("g."))
            is = true;
          if (is) {
            return true;
          }
        } catch (BadLocationException ble) { // Never happens
          ble.printStackTrace();
        }
        return false;
      }
    };
    FunctionCompletion fun = null;
    VariableCompletion var = null;
    ArrayList<Parameter> list = null;
    // g start
    fun = new FunctionCompletion(gProvider, "draw3DRect", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    list.add(new Parameter("boolean", "raised", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "drawArc", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    list.add(new Parameter("double", "startAngle", true));
    list.add(new Parameter("double", "arcAngle", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "drawLine", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x1", false));
    list.add(new Parameter("int", "y1", true));
    list.add(new Parameter("int", "x2", false));
    list.add(new Parameter("int", "y2", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "drawOval", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "drawPolygon", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int[]", "xs", false));
    list.add(new Parameter("int[]", "ys", true));
    list.add(new Parameter("int", "n", false));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "drawPolyline", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int[]", "xs", false));
    list.add(new Parameter("int[]", "ys", true));
    list.add(new Parameter("int", "n", false));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "drawRect", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "drawRoundRect", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    list.add(new Parameter("double", "ArcW", true));
    list.add(new Parameter("double", "ArcH", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "fill3DRect", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    list.add(new Parameter("boolean", "raised", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "fillArc", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    list.add(new Parameter("double", "startAngle", true));
    list.add(new Parameter("double", "arcAngle", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "fillOval", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "fillPolygon", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int[]", "xs", false));
    list.add(new Parameter("int[]", "ys", true));
    list.add(new Parameter("int", "n", false));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    gProvider.addCompletion(new FunctionCompletion(gProvider, "fillRect(x, y, w, h)", "void"));
    // g start
    fun = new FunctionCompletion(gProvider, "fillRect", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "fillRoundRect", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "w", false));
    list.add(new Parameter("int", "h", true));
    list.add(new Parameter("double", "ArcW", true));
    list.add(new Parameter("double", "ArcH", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // g start
    fun = new FunctionCompletion(gProvider, "setColor(new Color", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "r", false));
    list.add(new Parameter("int", "g", true));
    list.add(new Parameter("int", "b", false));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    gProvider.addCompletion(fun);
    // g end
    // icon start
    fun = new FunctionCompletion(iconProvider, "getGraphics", "Graphics");
    list = new ArrayList<Parameter>();
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    iconProvider.addCompletion(fun);
    // icon end
    // icon start
    fun = new FunctionCompletion(iconProvider, "getWidth", "int");
    list = new ArrayList<Parameter>();
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    iconProvider.addCompletion(fun);
    // icon end
    // icon start
    fun = new FunctionCompletion(iconProvider, "getHeight", "int");
    list = new ArrayList<Parameter>();
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    iconProvider.addCompletion(fun);
    // icon end
    // icon start
    fun = new FunctionCompletion(iconProvider, "getRGB", "int");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    iconProvider.addCompletion(fun);
    // icon end
    // icon start
    fun = new FunctionCompletion(iconProvider, "setRGB", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("int", "x", false));
    list.add(new Parameter("int", "y", true));
    list.add(new Parameter("int", "rgb", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription(".");
    iconProvider.addCompletion(fun);
    // icon end
    var = new VariableCompletion(entProvider, "type", "VisibleEntity.Type");
    var.setShortDescription("Can either be BLOCK, BULLET or TANK. Type \"vis\" to auto-complete full type names.");
    entProvider.addCompletion(var);
    var = new VariableCompletion(entProvider, "side", "VisibleEntity.Side");
    var.setShortDescription("Can either be GOOD, BAD or NEUTRAL. Type \"vis\" to auto-complete full type names.");
    entProvider.addCompletion(var);
    var = new VariableCompletion(entProvider, "dir", "double");
    var.setShortDescription("The direction the object is traveling in degrees. The angle starts from the positive x-axis and continues clockwise.");
    entProvider.addCompletion(var);
    var = new VariableCompletion(entProvider, "rect", "Rectangle");
    var.setShortDescription("The bounding box of the object.");
    entProvider.addCompletion(var);
    var = new VariableCompletion(entProvider, "speed", "double");
    var.setShortDescription("The speed at which the object is traveling.");
    entProvider.addCompletion(var);
    var = new VariableCompletion(entProvider, "turretDir", "double");
    var.setShortDescription("Only applicable if type is TANK. The direction of the tanks turret. The angle starts from the positive x-axis and continues clockwise.");
    entProvider.addCompletion(var);
    statProvider.addCompletion(new ShorthandCompletion(statProvider, "VisibleEntity.Side.GOOD","VisibleEntity.Side.GOOD", ""));
    statProvider.addCompletion(new ShorthandCompletion(statProvider, "VisibleEntity.Side.BAD", "VisibleEntity.Side.BAD", ""));
    statProvider.addCompletion(new ShorthandCompletion(statProvider, "VisibleEntity.Side.NEUTRAL", "VisibleEntity.Side.NEUTRAL", ""));
    statProvider.addCompletion(new ShorthandCompletion(statProvider, "VisibleEntity.Type.TANK", "VisibleEntity.Type.TANK"));
    statProvider.addCompletion(new ShorthandCompletion(statProvider, "VisibleEntity.Type.BLOCK", "VisibleEntity.Type.BLOCK", ""));
    statProvider.addCompletion(new ShorthandCompletion(statProvider, "VisibleEntity.Type.BULLET", "VisibleEntity.Type.BULLET", ""));
    fun = new FunctionCompletion(thisProvider, "talk", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("String", "phrase", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription("Use this to print witty dialog above your tank for the next few seconds. Only on phrase will be spoken at a time. Each call overrides the last.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "getDir", "double");
    fun.setReturnValueDescription("Angle in degrees");
    fun.setShortDescription("Gets the current direction of your tank.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "getTurretDir", "double");
    fun.setReturnValueDescription("Angle in degrees");
    fun.setShortDescription("Gets the current direction of your tank's turret.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "getVisibleEntities", "HashSet<VisibleEntity>");
    fun.setReturnValueDescription("A HashSet of Visible Entities. Use the \"for-in\" construct to iterate through. Any variable with the name \"ent\" will auto-complete for VisibleEntity.");
    fun.setShortDescription("This provides a list of every object your tank can currently see in it's line of sight from all vision cones. Press 'h' in game to see vision cones.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "getBoundingBox", "Rectangle");
    fun.setReturnValueDescription("The rectangle representing this objects hitbox.");
    fun.setShortDescription("This is helpful for knowing where your tank is at in the world.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "forward", "void");
    fun.setReturnValueDescription("");
    fun.setShortDescription("Move your tank forward at a fixed speed.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "backward", "void");
    fun.setReturnValueDescription("");
    fun.setShortDescription("Move your tank backward at a fixed speed. Tanks move backwards slower than forwards.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "turnTread", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("double", "deg", false));
    list.add(new Parameter("boolean", "isAbsolute", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription("Turn your tank's treads to a set angle in degrees. Angles start at the positive x-axis and proceed clockwise. Set the absolute flag to false to make the movement relative to its current position.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "lockTurret", "void");
    fun.setReturnValueDescription("");
    fun.setShortDescription("Anchors your turret's movement to a fixed position relative to your tank's treads. Moving the turret again anytime after this call will unlock the turret.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "turnTurret", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("double", "deg", true));
    list.add(new Parameter("boolean", "isAbsolute", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription("Turn your tank's turret to a set angle in degrees. Angles start at the positive x-axis and proceed clockwise. Set the absolute flag to false to make the movement relative to its current position.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "turnTurretTo", "void");
    list = new ArrayList<Parameter>();
    list.add(new Parameter("double", "x", false));
    list.add(new Parameter("double", "y", true));
    fun.setParams(list);
    fun.setReturnValueDescription("");
    fun.setShortDescription("Allows your tank's turret to focus on a particular x/y coordinate.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "isFireAllowed", "boolean");
    fun.setReturnValueDescription("");
    fun.setShortDescription("It takes your tanks cannon around 850 ms to reload. This will tell you if the cannon has reloaded.");
    thisProvider.addCompletion(fun);
    fun = new FunctionCompletion(thisProvider, "fire", "void");
    fun.setReturnValueDescription("Return value description");
    fun.setShortDescription("Fires a bullet if the cannon is reloaded.");
    thisProvider.addCompletion(fun);
    ShorthandCompletion sh = new ShorthandCompletion(thisProvider, "for", "for (VisibleEntity ent : this.getVisibleEntities()) {", " - Code Template");
    // sh.setShortDescription("Code Template - Loop through all the objects your tank can see at this moment in time.");
    sh.setSummary("Loop through all the objects your tank can see at this moment in time.");
    thisProvider.addCompletion(sh);
    AutoCompletion ac1 = new AutoCompletion(thisProvider);
    AutoCompletion ac2 = new AutoCompletion(entProvider);
    AutoCompletion ac3 = new AutoCompletion(statProvider);
    AutoCompletion ac4 = new AutoCompletion(iconProvider);
    AutoCompletion ac5 = new AutoCompletion(gProvider);
    ac1.setShowDescWindow(true);
    ac2.setShowDescWindow(true);
    ac3.setShowDescWindow(true);
    ac4.setShowDescWindow(true);
    ac5.setShowDescWindow(true);
    thisProvider.setParameterizedCompletionParams('(', ", ", ')');
    entProvider.setParameterizedCompletionParams('(', ", ", ')');
    statProvider.setParameterizedCompletionParams('(', ", ", ')');
    iconProvider.setParameterizedCompletionParams('(', ", ", ')');
    gProvider.setParameterizedCompletionParams('(', ", ", ')');
    ac1.setParameterAssistanceEnabled(true);
    ac2.setParameterAssistanceEnabled(true);
    ac3.setParameterAssistanceEnabled(true);
    ac4.setParameterAssistanceEnabled(true);
    ac5.setParameterAssistanceEnabled(true);
    ac1.setAutoActivationEnabled(true);
    ac2.setAutoActivationEnabled(true);
    ac3.setAutoActivationEnabled(true);
    ac4.setAutoActivationEnabled(true);
    ac5.setAutoActivationEnabled(true);
    ac5.install(textArea);
    ac4.install(textArea);
    ac3.install(textArea);
    ac2.install(textArea);
    ac1.install(textArea);
  }

  public void setStatus(String status) {
    statusLabel.setText(status);
    try {
      new Timer().schedule(clearStatusTask, 3000);
    } catch (Exception e) {
      ; // ignore timers already set
    }
  }

  class NewAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      int result =
          JOptionPane.showConfirmDialog(null,
              "Are you sure you want to throw away your current work?", "Create New Template",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        textArea.setText(template);
        textArea.setCaretPosition(0);
      }
    }
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
          System.out.println("Good filename: \"" + name + "\"");
          String filename;
          if (Util.getOS() == Util.OS.WIN) {
            filename = aiDirectory.getAbsolutePath() + "\\" + name + ".java";
          } else {
            filename = aiDirectory.getAbsolutePath() + "/" + name + ".java";
          }
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
          System.out.println("Could not save file with name: \"" + name + "\"");
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
          String matchString;
          if (Util.getOS() == Util.OS.WIN) {
            matchString = ".*\\.java";
          } else {
            matchString = ".*\\.java";
          }
          if (f.getName().matches(matchString)) {
            return true;
          } else {
            return false;
          }

        }
      });
      int returnVal = fileChooser.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try {
          BufferedReader reader = new BufferedReader(new FileReader(file));
          StringBuilder sb = new StringBuilder();
          String line = null;
          while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
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
        JOptionPane.showMessageDialog(null,
            "Tank filename/classname already taken. Please choose another one.", "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      boolean wasRunFromJar = Util.wasRunFromJar();
      String bin;
      if (wasRunFromJar) {
        bin = Util.getPathToRunningJar();
      } else {
        bin = "./bin";
      }
      String classPathString = " -cp " + bin + ";./ai";
      saveAction.actionPerformed(e);
      String javacString = javacField.getText().replaceAll("^C:", "c:");
      if (javacString == null || javacString.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null,
            "Please choose a java compiler (javac) program before attempting to compile.", "Error",
            JOptionPane.ERROR_MESSAGE);
        ChooseAction choice = new ChooseAction();
        choice.actionPerformed(null);
        return;
      }
      String aiPath = Util.getAIDirectory().getAbsolutePath();
      String outputDirectory = " -d ./ai";
      String directorySeperator;
      if (Util.getOS() == Util.OS.WIN) {
        directorySeperator = "\\";
      } else {
        directorySeperator = "/";
      }
      String cmd =
          javacString + outputDirectory + classPathString + " " + aiPath + directorySeperator
              + name + ".java";
      String output = executeCmd(cmd);
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
        JOptionPane.showMessageDialog(null, "Failed to reload the application", "Error",
            JOptionPane.ERROR_MESSAGE);
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
      int returnVal = fileChooser.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        filename = fileChooser.getSelectedFile().getAbsolutePath();
        Util.setProperty("javac-path", filename);
        javacField.setText(filename);
      } else {

      }
    }
  }

  public String executeCmd(String cmd) {
    StringBuilder sb = new StringBuilder();
    try {
      Process p;
      p = Runtime.getRuntime().exec("cmd /C " + cmd);
      p.waitFor();
      BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = "";
      while ((line = out.readLine()) != null) {
        sb.append(line + "\n");
      }
      BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      while ((line = err.readLine()) != null) {
        sb.append(line + "\n");
      }
      if (p.exitValue() == 0) {
        setStatus(" Compilation Success");
        return sb.toString();
      } else {
        JOptionPane.showMessageDialog(null, "Compilation Failed\n" + sb.toString(), "Error",
            JOptionPane.ERROR_MESSAGE);
        return sb.toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public static boolean isNameAllowed(String name) {
    boolean isTaken = false;
    String[] taken =
        new String[] {"Tank", "TankInterface", "TankWizard", "TankJason", "TankMajorTom",
            "TankMinja", "TankPlayer"};
    for (String takenName : taken) {
      if (takenName.compareTo(name) == 0) {
        isTaken = true;
        break;
      }
    }
    return !isTaken;
  }

  public static void main(String[] args) {
    new Editor();
  }

  class ZoomInAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (fontSize < 200) {
        fontSize++;
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
      }
    }
  }

  class ZoomOutAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (fontSize > 2) {
        fontSize--;
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
      }
    }
  }
}
