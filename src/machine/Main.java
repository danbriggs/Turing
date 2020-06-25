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
		MyPanel myPanel = new MyPanel(tests, machineList);
		myPanel.show(args);
		/*int length = 128;
		Complex[] complexes = new Complex[length];
		complexes[0]=new Complex(1,0);
		int currNum = 1;
		for (int i=1; i<length; i++)
			try {
				currNum*=3;
				currNum%=7;
				complexes[i]= new Complex((Math.pow(-1, currNum)+1)/2,0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		FFT.show(complexes, "Original");
		FFT.show(FFT.convolve(complexes, complexes), "Autocorrelation");
		FFT.show(FFT.cconvolve(complexes, complexes), "Cyclic Autocorrelation");*/
		//System.out.println(Acceleration.longestRepeatedSubSeq("AABEBCDD")); ;
		//boolean ok = tests.runTests();
		//System.out.println("All tests passed: "+ok);
	}
}
