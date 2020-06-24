package machine;

import java.util.List;

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
		MyPanel myPanel = new MyPanel(tests, machineList);
		myPanel.show(args);
		System.out.println(Acceleration.lrs("acbdfghybdf"));
		//boolean ok = tests.runTests();
		//System.out.println("All tests passed: "+ok);
	}
}
