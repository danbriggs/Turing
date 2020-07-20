package machine;

import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.*;
//import javax.swing.event.*;

public class MyPanel extends JPanel {
	
	JMenuBar menuBar;
	JMenu menu, menu2, submenu;
	JMenuItem[] menuItems;
	JRadioButtonMenuItem fastRun, analyticRun;
	JCheckBoxMenuItem leftEdge, rightEdge, stepNumbers;
	
	//private JButton normalActionTest;
	private JButton longestRunTest;
	private JButton yieldsTest;
	private JButton termTest;
	private JButton termfigurationTest;
	private JButton configurationReadTest;
	private JButton leftToRightInductionTest;
	private JButton rightToLeftInductionTest;
	private JButton successorTest;
	private JButton lemmaAsStringTest;
	private JButton condensedConfigurationTest;
	private JButton actTest1;
	private JButton actTest2;
	private JButton stretchTapeTest;
	private JButton bigStretchTapeTest1;
	private JButton bigStretchTapeTest2;
	private JButton allProvedTest;
	//private JCheckBox jcomp10;
	//private JCheckBox jcomp11;
    private JLabel machineNoLabel;
    private JTextField machineNoField;
    private JLabel startStepLabel;
    private JTextField startStepField;
    private JLabel endStepLabel;
    private JTextField endStepField;
    private JButton run;
	private JTextArea jcomp12;
	private JScrollPane jcomp13;

	public MyPanel(Tests tests, List<Machine> machineList) {
		//construct components

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("Run");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
		        "Menu items for running machines and tests");
		menuBar.add(menu);

		//a group of JMenuItems
		menuItems = new JMenuItem[10];
		menuItems[0] = new JMenuItem("Run all tests",
		                         KeyEvent.VK_T);
		menuItems[0].setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItems[0].getAccessibleContext().setAccessibleDescription(
		        "Equivalent to clicking all twelve buttons below");
		menu.add(menuItems[0]);
		
		BufferedImage vPic = null;
		try {vPic = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("images/middle.gif"));
		} catch (IOException e2) {e2.printStackTrace();}
				
		menuItems[1] = new JMenuItem("Look for two loops");
		menuItems[1].setMnemonic(KeyEvent.VK_B);
		menu.add(menuItems[1]);

		menuItems[2] = new JMenuItem("Longest run",new ImageIcon(vPic));
		menuItems[2].setMnemonic(KeyEvent.VK_D);
		menu.add(menuItems[2]);
		
		menuItems[7] = new JMenuItem("TermfigurationSequence Test", new ImageIcon(vPic));
		menuItems[7].setMnemonic(KeyEvent.VK_F);
		menu.add(menuItems[7]);
		
		menuItems[8] = new JMenuItem("ExtendedTermfiguration Test", new ImageIcon(vPic));
		menuItems[8].setMnemonic(KeyEvent.VK_G);
		menu.add(menuItems[8]);
		
		//a group of radio button menu items
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		fastRun = new JRadioButtonMenuItem("Fast run");
		fastRun.setSelected(true);
		//fastRun.setActionCommand("fast");
		fastRun.setMnemonic(KeyEvent.VK_R);
		group.add(fastRun);
		menu.add(fastRun);

		analyticRun = new JRadioButtonMenuItem("Analytic run");
		//analyticRun.setActionCommand("analytic");
		//analyticRun.setSelected(true);
		analyticRun.setMnemonic(KeyEvent.VK_O);
		group.add(analyticRun);
		menu.add(analyticRun);

		//a group of check box menu items
		menu.addSeparator();
		leftEdge = new JCheckBoxMenuItem("Left edge");
		leftEdge.setMnemonic(KeyEvent.VK_C);
		//leftEdge.setSelected(true);
		menu.add(leftEdge);

		rightEdge = new JCheckBoxMenuItem("Right edge");
		rightEdge.setMnemonic(KeyEvent.VK_H);
		menu.add(rightEdge);
		
		stepNumbers = new JCheckBoxMenuItem("Show step numbers");
		stepNumbers.setMnemonic(KeyEvent.VK_I);
		stepNumbers.setSelected(true);
		menu.add(stepNumbers);

		//a submenu
		menu.addSeparator();
		submenu = new JMenu("Tape length");
		submenu.setMnemonic(KeyEvent.VK_S);

		ButtonGroup group2 = new ButtonGroup();
		menuItems[9] = new JRadioButtonMenuItem("Guaranteed long enough");
		menuItems[9].setSelected(true);
		group2.add(menuItems[9]);
		submenu.add(menuItems[9]);
		
		menuItems[3] = new JRadioButtonMenuItem("One thousandth of End Step");
		menuItems[3].setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.ALT_MASK));
		group2.add(menuItems[3]);
		submenu.add(menuItems[3]);

		menuItems[4] = new JRadioButtonMenuItem("Constant million");
		group2.add(menuItems[4]);
		submenu.add(menuItems[4]);
		menu.add(submenu);

		//Build second menu in the menu bar.
		menu2 = new JMenu("Documentation");
		menu2.setMnemonic(KeyEvent.VK_N);
		//menu2.getAccessibleContext().setAccessibleDescription(
		//        "This menu does nothing");
		
		menuItems[5] = new JMenuItem("Flowchart");
		menuItems[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,ActionEvent.ALT_MASK));
		menu2.add(menuItems[5]);

		menuItems[6] = new JMenuItem("Tests");
		menuItems[6].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,ActionEvent.ALT_MASK));
		menu2.add(menuItems[6]);
		
		menuBar.add(menu2);

		//normalActionTest = new JButton ("Normal Action Test");
		longestRunTest = new JButton ("Longest Run Test");
		yieldsTest = new JButton ("Yields Test");
		termTest = new JButton ("Term Test");
		termfigurationTest = new JButton ("Termfiguration Test");
		configurationReadTest = new JButton ("<html><center>Configuration Read<br>Test</html>");
		leftToRightInductionTest = new JButton ("<html><center>Left to Right<br>Induction Test</html>");
		rightToLeftInductionTest = new JButton ("<html><center>Right to Left<br>Induction Test</html>");
		successorTest = new JButton ("Successor Test");
		lemmaAsStringTest = new JButton ("<html><center>Lemma As String</html>");
		condensedConfigurationTest = new JButton ("<html><center>Condensed<br>Configuration Test</html>");
		actTest1 = new JButton ("Act Test 1");
		actTest2 = new JButton ("Act Test 2");
		stretchTapeTest = new JButton ("<html><center>Stretch Tape Test</html>");
		bigStretchTapeTest1 = new JButton ("<html><center>Big Stretch Test 1</html>");
		bigStretchTapeTest2 = new JButton ("<html><center>Big Stretch Test 2</html>");
		allProvedTest = new JButton ("<html><center>All Proved Test</html>");

		jcomp12 = new JTextArea (5, 5);
		jcomp13 = new JScrollPane(jcomp12);
		
        machineNoLabel = new JLabel ("Machine #(1-43; 0 for all)");
        machineNoField = new JTextField (5);
        machineNoField.setText("1");
        startStepLabel = new JLabel ("Start Step");
        startStepField = new JTextField (5);
        startStepField.setText("0");
        endStepLabel = new JLabel ("End Step");
        endStepField = new JTextField (5);
        endStepField.setText("1000");
        run = new JButton("Run");

		
		longestRunTest.setToolTipText ("hi");

		//PrintStream standardOut = System.out;
		//PrintStream standardErr = System.err;
		PrintStream printStream = new PrintStream(new CustomOutputStream(jcomp12, this));
		System.setOut(printStream);
		System.setErr(printStream);

		//adjust size and set layout
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int)(screenSize.width*.9);
		int screenHeight = (int)(screenSize.height*.9);
		
		//setPreferredSize (new Dimension (1500, 1000));
		setPreferredSize (new Dimension (screenWidth, screenHeight));
		setLayout (null);		
		
		Font defaultFont = new Font("Arial", Font.PLAIN, (int)(.021*screenSize.height));
		longestRunTest.setFont(defaultFont);
		yieldsTest.setFont(defaultFont);
		termTest.setFont(defaultFont);
		termfigurationTest.setFont(defaultFont);
		configurationReadTest.setFont(defaultFont);
		leftToRightInductionTest.setFont(defaultFont);
		rightToLeftInductionTest.setFont(defaultFont);
		successorTest.setFont(defaultFont);
		lemmaAsStringTest.setFont(defaultFont);
		condensedConfigurationTest.setFont(defaultFont);
		actTest1.setFont(defaultFont);
		actTest2.setFont(defaultFont);
		stretchTapeTest.setFont(defaultFont);
		bigStretchTapeTest1.setFont(defaultFont);
		bigStretchTapeTest2.setFont(defaultFont);
		allProvedTest.setFont(defaultFont);

		run.setFont(defaultFont);
		
		jcomp12.setFont(new Font("Courier New",Font.PLAIN,12));
		
		//set components properties
		jcomp12.setEnabled (true);

		//add components
		add (menuBar);
		add (longestRunTest);
		add (yieldsTest);
		add (termTest);
		add (termfigurationTest);
		add (configurationReadTest);
		add (leftToRightInductionTest);
		add (rightToLeftInductionTest);
		add (successorTest);
		add (lemmaAsStringTest);
		add (condensedConfigurationTest);
		add (actTest1);
		add (actTest2);
		add (stretchTapeTest);
		add (bigStretchTapeTest1);
		add (bigStretchTapeTest2);
		add (allProvedTest);
		add (jcomp13);
		add(machineNoLabel);
		add(machineNoField);
		add(startStepLabel);
		add(startStepField);
		add(endStepLabel);
		add(endStepField);
		add(run);
		
		//think 1512 x 945
		//or 1366*.9 x 768 *.9
		int x1 = (int)(.0462*screenWidth);
		int y1 = (int)(.074*screenHeight);
		int dx = (int)(.19*screenWidth);
		int dy = (int)(.0952*screenHeight);
		int w = (int)(dx*.867);
		int h=(int)(0.0794*screenHeight);
		int mediumh = (int)(h*.7);
		int smallw=(int)(0.1426*screenWidth);
		int smallh=(int)(0.0212*screenHeight);
		int secondRow = (int)(y1+dy-.3*h);
		int thirdRow = (int)(y1+2*dy-.3*h);
		int fourthRow = (int)(y1+3*dy-.6*h);
		int fifthRow = (int)(y1+4*dy-.9*h);
		int scrollPaneWidth = (int)(.903*screenWidth);
		int scrollPaneHeight = screenHeight - fifthRow;
		//set component bounds (only needed by Absolute Positioning)
		longestRunTest.setBounds (x1, y1, w, mediumh);
		yieldsTest.setBounds (x1+dx, y1, w, mediumh);
		termTest.setBounds (x1+2*dx, y1, w, mediumh);
		lemmaAsStringTest.setBounds (x1+3*dx, y1, w, mediumh);
		termfigurationTest.setBounds (x1, thirdRow, w, mediumh);
		configurationReadTest.setBounds (x1+dx, secondRow, w, h);
		leftToRightInductionTest.setBounds (x1+2*dx, secondRow, w, h);
		rightToLeftInductionTest.setBounds (x1+3*dx, secondRow, w, h);
		successorTest.setBounds (x1+dx, thirdRow, w, mediumh);
		condensedConfigurationTest.setBounds (x1, secondRow, w, h);
		actTest1.setBounds (x1+2*dx, thirdRow, w, mediumh);
		actTest2.setBounds (x1+3*dx, thirdRow, w, mediumh);
		stretchTapeTest.setBounds(x1+dx, fourthRow, w, mediumh);
		bigStretchTapeTest1.setBounds(x1+2*dx, fourthRow, w, mediumh);
		bigStretchTapeTest2.setBounds(x1+3*dx, fourthRow, w, mediumh);
		allProvedTest.setBounds(x1, fourthRow, w, mediumh);
		
		jcomp13.setBounds (x1, fifthRow, scrollPaneWidth, scrollPaneHeight);
		machineNoLabel.setBounds(x1+4*dx,y1,smallw,smallh);
		machineNoField.setBounds(x1+4*dx,y1+smallh,smallw,smallh);
		startStepLabel.setBounds(x1+4*dx,y1+2*smallh,smallw,smallh);
		startStepField.setBounds(x1+4*dx,y1+3*smallh,smallw,smallh);
		endStepLabel.setBounds(x1+4*dx,y1+4*smallh,smallw,smallh);
		endStepField.setBounds(x1+4*dx,y1+5*smallh,smallw,smallh);
		int runY = y1+7*smallh;
		run.setBounds(x1+4*dx,runY,smallw,fourthRow+mediumh-runY);
		
		menuItems[0].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			int start = Integer.parseInt(startStepField.getText());
			int stop = Integer.parseInt(endStepField.getText());
			System.out.println("All tests passed: "+tests.runTests(num, start, stop));}});
		menuItems[1].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			boolean ok = tests.allProvedTest(num);
			System.out.println("All proved: "+ok);
			}});
		menuItems[2].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			int start = Integer.parseInt(startStepField.getText());
			int stop = Integer.parseInt(endStepField.getText());
			tests.longestRunTest(num, start, stop);
			}});
		menuItems[7].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.termfigurationSequenceTest();
			}});
		menuItems[8].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.extendedTermfigurationTest();
			}});
		menuItems[5].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			JFrame frame = new JFrame("FrameDemo");
			//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JLabel wIcon=null;
			try {
				BufferedImage wPic = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("images/flowchart.png"));
				wIcon = new JLabel(new ImageIcon(wPic));
			} catch (IOException e1) {e1.printStackTrace();}
			frame.getContentPane().add(wIcon, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			}});
		longestRunTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			int start = Integer.parseInt(startStepField.getText());
			int stop = Integer.parseInt(endStepField.getText());
			tests.longestRunTest(num, start, stop);}});
		yieldsTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			int start = Integer.parseInt(startStepField.getText());
			int stop = Integer.parseInt(endStepField.getText());
			tests.yieldsTest(num, start, stop);}});
		termTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.termTest();}});
		lemmaAsStringTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.lemmaAsStringTest();}});
		termfigurationTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.termfigurationTest();}});
		configurationReadTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.configurationReadTest();}});
		leftToRightInductionTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.leftToRightInductionTest();}});
		rightToLeftInductionTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.rightToLeftInductionTest();}});
		successorTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.successorTest();}});
		condensedConfigurationTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.condensedConfigurationTest();}});
		actTest1.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.actTest1();}});
		actTest2.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.actTest2();}});
		stretchTapeTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			tests.stretchTapeTest(num);}});
		bigStretchTapeTest1.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			tests.bigStretchTapeTest(num);}});
		bigStretchTapeTest2.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			int start = Integer.parseInt(startStepField.getText());
			int stop = Integer.parseInt(endStepField.getText());
			tests.bigStretchTapeTest2(num, start, stop);}});
		allProvedTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			tests.allProvedTest(num);}});
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jcomp12.setText("");
				int num = Integer.parseInt(machineNoField.getText());
				int lo = Integer.parseInt(startStepField.getText());
				int hi = Integer.parseInt(endStepField.getText());
				int mode=0;
				if (menuItems[3].isSelected()) mode=1;
				if (menuItems[4].isSelected()) mode=2;
				boolean analytic = analyticRun.isSelected();
				tests.run(num, lo, hi, analytic, leftEdge.isSelected(), rightEdge.isSelected(), stepNumbers.isSelected(), mode);
			}
		});
	}


	public void show (String[] args) {
		JFrame frame = new JFrame ("MyPanel");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add (this);
		frame.pack();
		frame.setVisible (true);
	}
}
