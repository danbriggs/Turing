package machine;

import java.io.PrintStream;
import java.util.List;
//import complex.Complex;
//import complex.FFT;

public class Main {
	static final boolean LOUD = false;
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SkeletPageParser sk = new SkeletPageParser();
		try{
			sk.readPage();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		List<Machine> machineList = sk.getMachineList();
		if (LOUD)  {
			for (int i=1; i<machineList.size(); i++) {
				System.out.println("Machine #"+i+":");
				System.out.println(machineList.get(i));
			}
		}
		Tests tests = new Tests(machineList);
		
		AllMachines allMachines = new AllMachines();
		PrintStream stdout = System.out;
		try {
			allMachines.makeMachines(); 
		} catch (Exception e) {
			System.setOut(stdout); 
			System.out.println("AllMachines.makeMachines() exited with "+e.getMessage());
			if (!e.getMessage().equals("Total number of machines exceeded.")) e.printStackTrace();
		}
		System.setOut(stdout); 
		MyPanel myPanel = new MyPanel(tests, machineList);
		myPanel.show(args);
		//System.out.println(Acceleration.longestRepeatedSubSeq("AABEBCDD")); ;
		//boolean ok = tests.runTests();
		//System.out.println("All tests passed: "+ok);
	}
}
