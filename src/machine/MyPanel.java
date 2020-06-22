package machine;

import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;

import javax.swing.*;
//import javax.swing.event.*;

@SuppressWarnings("serial")
public class MyPanel extends JPanel {
	JMenuBar menuBar;
	JMenu menu, menu2, submenu;
	JMenuItem[] menuItems;
	JRadioButtonMenuItem fastRun, analyticRun;
	JCheckBoxMenuItem leftEdge, rightEdge, stepNumbers;
	boolean analytic;
	
	private JButton normalActionTest;
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
		analytic = false;
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
		menuItems = new JMenuItem[6];
		menuItems[0] = new JMenuItem("Run all tests",
		                         KeyEvent.VK_T);
		menuItems[0].setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItems[0].getAccessibleContext().setAccessibleDescription(
		        "Equivalent to clicking all twelve buttons below");
		menu.add(menuItems[0]);

		menuItems[1] = new JMenuItem("Both text and icon",
		                         new ImageIcon("src/images/middle.gif"));
		menuItems[1].setMnemonic(KeyEvent.VK_B);
		menu.add(menuItems[1]);

		menuItems[2] = new JMenuItem(new ImageIcon("src/images/middle.gif"));
		menuItems[2].setMnemonic(KeyEvent.VK_D);
		menu.add(menuItems[2]);

		//a group of radio button menu items
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		fastRun = new JRadioButtonMenuItem("Fast run");
		//fastRun.setActionCommand("fast");
		fastRun.setSelected(true);
		fastRun.setMnemonic(KeyEvent.VK_R);
		group.add(fastRun);
		menu.add(fastRun);

		analyticRun = new JRadioButtonMenuItem("Analytic run");
		//analyticRun.setActionCommand("analytic");
		analyticRun.setMnemonic(KeyEvent.VK_O);
		group.add(analyticRun);
		menu.add(analyticRun);

		//a group of check box menu items
		menu.addSeparator();
		leftEdge = new JCheckBoxMenuItem("Left edge");
		leftEdge.setMnemonic(KeyEvent.VK_C);
		menu.add(leftEdge);

		rightEdge = new JCheckBoxMenuItem("Right edge");
		rightEdge.setMnemonic(KeyEvent.VK_H);
		menu.add(rightEdge);
		
		stepNumbers = new JCheckBoxMenuItem("Show step numbers");
		stepNumbers.setMnemonic(KeyEvent.VK_I);
		menu.add(stepNumbers);

		//a submenu
		menu.addSeparator();
		submenu = new JMenu("A submenu");
		submenu.setMnemonic(KeyEvent.VK_S);

		menuItems[3] = new JMenuItem("An item in the submenu");
		menuItems[3].setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.ALT_MASK));
		submenu.add(menuItems[3]);

		menuItems[4] = new JMenuItem("Another item");
		submenu.add(menuItems[4]);
		menu.add(submenu);

		//Build second menu in the menu bar.
		menu2 = new JMenu("Help");
		menu2.setMnemonic(KeyEvent.VK_N);
		//menu2.getAccessibleContext().setAccessibleDescription(
		//        "This menu does nothing");
		
		menuItems[5] = new JMenuItem("Documentation");
		menuItems[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,ActionEvent.ALT_MASK));
		
		menu2.add(menuItems[5]);
		menuBar.add(menu2);

		normalActionTest = new JButton ("Normal Action Test");
		yieldsTest = new JButton ("Yields Test");
		termTest = new JButton ("Term Test");
		termfigurationTest = new JButton ("Termfiguration Test");
		configurationReadTest = new JButton ("<html><center>Configuration Read<br>Test</html>");
		leftToRightInductionTest = new JButton ("<html><center>Left to Right<br>Induction Test</html>");
		rightToLeftInductionTest = new JButton ("<html><center>Right to Left<br>Induction Test</html>");
		successorTest = new JButton ("Successor Test");
		lemmaAsStringTest = new JButton ("<html><center>Lemma as String<br>Test</html>");
		condensedConfigurationTest = new JButton ("<html><center>Condensed<br>Configuration Test</html>");
		actTest1 = new JButton ("Act Test 1");
		actTest2 = new JButton ("Act Test 2");
		//jcomp10 = new JCheckBox ("newCheckBox");
		//jcomp11 = new JCheckBox ("newCheckBox");
		jcomp12 = new JTextArea (5, 5);
		jcomp13 = new JScrollPane(jcomp12);
		
        machineNoLabel = new JLabel ("Machine #(1-43; 0 for all)");
        machineNoField = new JTextField (5);
        machineNoField.setText("1");
        startStepLabel = new JLabel ("Start Step");
        startStepField = new JTextField (5);
        startStepField.setText("4586");
        endStepLabel = new JLabel ("End Step");
        endStepField = new JTextField (5);
        endStepField.setText("4815");
        run = new JButton("Run");

		
		normalActionTest.setToolTipText ("hi");

		//PrintStream standardOut = System.out;
		//PrintStream standardErr = System.err;
		PrintStream printStream = new PrintStream(new CustomOutputStream(jcomp12));
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
		normalActionTest.setFont(defaultFont);
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
		run.setFont(defaultFont);
		
		jcomp12.setFont(new Font("Courier New",Font.PLAIN,12));
		
		//set components properties
		jcomp12.setEnabled (true);

		//add components
		add (menuBar);
		add (normalActionTest);
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
		//add (jcomp10);
		//add (jcomp11);
		//add (jcomp12);
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
		int smallw=(int)(0.1426*screenWidth);
		int smallh=(int)(0.0212*screenHeight);
		int scrollPaneWidth = (int)(.903*screenWidth);
		int scrollPaneHeight =(int)(.63*screenHeight);
		//set component bounds (only needed by Absolute Positioning)
		normalActionTest.setBounds (x1, y1, w, h);
		yieldsTest.setBounds (x1+dx, y1, w, h);
		termTest.setBounds (x1+2*dx, y1, w, h);
		lemmaAsStringTest.setBounds (x1+3*dx, y1, w, h);
		termfigurationTest.setBounds (x1, y1+dy, w, h);
		configurationReadTest.setBounds (x1+dx, y1+dy, w, h);
		leftToRightInductionTest.setBounds (x1+2*dx, y1+dy, w, h);
		rightToLeftInductionTest.setBounds (x1+dx, y1+2*dy, w, h);
		successorTest.setBounds (x1+3*dx, y1+dy, w, h);
		condensedConfigurationTest.setBounds (x1, y1+2*dy, w, h);
		actTest1.setBounds (x1+2*dx, y1+2*dy, w, h);
		actTest2.setBounds (x1+3*dx, y1+2*dy, w, h);
		//jcomp10.setBounds (x1+4*dx, y1, w, h);
		//jcomp11.setBounds (x1+4*dx, y1+dy, w, h);
		
		jcomp13.setBounds (x1, y1+3*dy, scrollPaneWidth, scrollPaneHeight);
		machineNoLabel.setBounds(x1+4*dx,y1,smallw,smallh);
		machineNoField.setBounds(x1+4*dx,y1+smallh,smallw,smallh);
		startStepLabel.setBounds(x1+4*dx,y1+2*smallh,smallw,smallh);
		startStepField.setBounds(x1+4*dx,y1+3*smallh,smallw,smallh);
		endStepLabel.setBounds(x1+4*dx,y1+4*smallh,smallw,smallh);
		endStepField.setBounds(x1+4*dx,y1+5*smallh,smallw,smallh);
		run.setBounds(x1+4*dx,y1+7*smallh,smallw,(int)(1.5*h));
		
		menuItems[0].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			int num = Integer.parseInt(machineNoField.getText());
			int start = Integer.parseInt(startStepField.getText());
			int stop = Integer.parseInt(endStepField.getText());
			System.out.println("All tests passed: "+tests.runTests(num, start, stop));}});
		fastRun.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			analytic = false;}});
		analyticRun.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			analytic = true;}});
		normalActionTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.normalActionTest(4586, 4815);}});
		yieldsTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			tests.yieldsTest();}});
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
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jcomp12.setText("");
				Machine m = machineList.get(Integer.parseInt(machineNoField.getText()));
				int lo = Integer.parseInt(startStepField.getText());
				int hi = Integer.parseInt(endStepField.getText());
				m.reset();
				tests.run(m, lo, hi, analytic, leftEdge.isSelected(), rightEdge.isSelected(), stepNumbers.isSelected());
			}
		});
		menuItems[5].addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			System.out.println("Here!");
		}});
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
