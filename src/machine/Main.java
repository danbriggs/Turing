package machine;

import java.util.List;
//import complex.Complex;
//import complex.FFT;

public class Main {
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SkeletPageParser sk = new SkeletPageParser();
		try{
			sk.readPage();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		List<Machine> machineList = sk.getMachineList();
		for (int i=1; i<machineList.size(); i++) {
			System.out.println("Machine #"+i+":");
			System.out.println(machineList.get(i));
		}
		Tests tests = new Tests(machineList);
		
		AllMachines allMachines = new AllMachines();
		try {
			allMachines.makeMachines();
		} catch (Exception e) {
			System.out.println("AllMachines.makeMachines() exited with "+e.getMessage());
		}
		
		MyPanel myPanel = new MyPanel(tests, machineList);
		myPanel.show(args);
		//System.out.println(Acceleration.longestRepeatedSubSeq("AABEBCDD")); ;
		//boolean ok = tests.runTests();
		//System.out.println("All tests passed: "+ok);
	}
}
