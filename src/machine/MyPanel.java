package machine;

import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;

import javax.swing.*;
//import javax.swing.event.*;

@SuppressWarnings("serial")
public class MyPanel extends JPanel {
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
		//construct components
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
		
        machineNoLabel = new JLabel ("Machine #(0-42)");
        machineNoField = new JTextField (5);
        startStepLabel = new JLabel ("Start Step");
        startStepField = new JTextField (5);
        endStepLabel = new JLabel ("End Step");
        endStepField = new JTextField (5);
        run = new JButton("Run");

		
		normalActionTest.setToolTipText ("hi");

		//PrintStream standardOut = System.out;
		//PrintStream standardErr = System.err;
		PrintStream printStream = new PrintStream(new CustomOutputStream(jcomp12));
		System.setOut(printStream);
		System.setErr(printStream);
		
		Font defaultFont = new Font("Arial", Font.PLAIN, 24);
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

		//adjust size and set layout
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int)(screenSize.width*.9);
		int screenHeight = (int)(screenSize.height*.9);
		
		//setPreferredSize (new Dimension (1500, 1000));
		setPreferredSize (new Dimension (screenWidth, screenHeight));
		setLayout (null);

		//add components
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
		int x1 = (int)(.0462*screenWidth);
		int y1 = (int)(.074*screenHeight);
		int dx = (int)(.1984*screenWidth);
		int dy = (int)(.0952*screenHeight);
		int w = (int)(dx*.867);
		int h=(int)(0.0794*screenHeight);
		int smallw=(int)(0.0926*screenWidth);
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
		
		normalActionTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.normalActionTest(4586, 4815);}});
		yieldsTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.yieldsTest();}});
		termTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.termTest();}});
		lemmaAsStringTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.lemmaAsStringTest();}});
		termfigurationTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.termfigurationTest();}});
		configurationReadTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.configurationReadTest();}});
		leftToRightInductionTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.leftToRightInductionTest();}});
		rightToLeftInductionTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.rightToLeftInductionTest();}});
		successorTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.successorTest();}});
		condensedConfigurationTest.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.condensedConfigurationTest();}});
		actTest1.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.actTest1();}});
		actTest2.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {tests.actTest2();}});
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jcomp12.setText("");
				Machine m = machineList.get(Integer.parseInt(machineNoField.getText()));
				int lo = Integer.parseInt(startStepField.getText());
				int hi = Integer.parseInt(endStepField.getText());
				m.reset();
				tests.run(m, lo, hi);
			}
		});
	}


	public void show (String[] args) {
		JFrame frame = new JFrame ("MyPanel");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add (this);
		frame.pack();
		frame.setVisible (true);
	}
}
