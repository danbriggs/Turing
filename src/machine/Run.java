package machine;

import java.util.List;
import java.util.StringTokenizer;

public class Run {
	static final char UNIT_SEPARATOR = 31;
	/**mode 0: guaranteed long enough tape.
	 * mode 1: end step divided by 1000.
	 * mode 2: constant million.
	 * initConfig should have two tokens: tape contents and state.
	 * Leave it null or empty for blank tape in state A.*/
	public static void run(List<Machine> _machineList, int num,   long top1, long top2, boolean analytic, boolean leftEdge, boolean rightEdge, boolean allSteps, boolean stepNumbers, int mode, String initConfig) {
		if (num>0 && num<_machineList.size()) {
			Machine m = _machineList.get(num);
			m.reset();
			run(m,top1,top2,analytic,leftEdge,rightEdge,allSteps,stepNumbers, mode, initConfig);
		}
		else if (num==0) {
			Thread t = new Thread(new Runnable() {
				public void run() {
		        	  for (int i=1; i<_machineList.size(); i++) {
		  				Machine m = _machineList.get(i);
		  				m.reset();
		  				System.out.println("For HNR#"+i+":");
		  				Run.run(m,top1,top2,analytic,leftEdge,rightEdge,allSteps,stepNumbers, mode, initConfig);
		  				System.out.println(UNIT_SEPARATOR);
		        	  }
		        }
			});
			t.start();
		}
	}
	public static void run(Machine m, long top1, long top2, boolean analytic, boolean leftEdge, boolean rightEdge, boolean allSteps, boolean stepNumbers, int mode, String initConfig) {
		//Warning: does not automatically reset m to state A
		//Make sure to call m.reset() before invoking this function
		//if you're interested in a clean run from a blank tape.
		//If analytic is false, displays all steps from top1 to top2.
		//If analytic is true, displays initial & final steps,
		//as well as steps whenever the tape head is at the left/right edge, respectively,
		//according to the value of leftEdge/rightEdge.
		String name = "Run";
		System.out.println("\n"+name+" beginning.");
		System.out.println(m);
		//System.out.println(leftEdge);
		//System.out.println(rightEdge);
		int minTapeHead = 0, maxTapeHead = 0;
		long stepForMinTapeHead = 0, stepForMaxTapeHead = 0;
		//Default 0s just to avoid compiler warnings
		try {
			TapeLike t = null;
			if (initConfig!=null && initConfig.length()>0) {
				StringTokenizer st = new StringTokenizer(initConfig);
				if (st.countTokens()!=2) throw new Exception("Could not process Tape field.");
				String tapeContents = st.nextToken();
				tapeContents = tapeContents.replaceAll("I", "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
				String stateIn = st.nextToken();
				if (stateIn.length()!=1) throw new Exception("State must be one character.");
				char c=stateIn.charAt(0);
				if (c<'A'||c>'E') throw new Exception("State must be from A to E.");
				m.setState(c-'A');
				if (analytic) t = new StretchTape(tapeContents);
				else t = new Tape(tapeContents);
			}
			else {
				int idx = (int) Math.min(top2,500000);
				//int idx = (int) Math.min(top2,50000000); //Custom for HNR#14
				if (analytic) {
					t = new StretchTape(idx);
				}
				else {
					t = new Tape(new int[idx*2+1],idx);
					//t = new Tape(new int[idx*2+1],idx/2); //Custom for HNR#14
				}
			}
			long i;
			for (i=0; i<top1; i++) m.act(t);
			minTapeHead = t.getNormalizedIndex();
			maxTapeHead = t.getNormalizedIndex();
			stepForMinTapeHead = top1;
			stepForMaxTapeHead = top1;
			if (analytic) {
				for (i=top1; i<top2; i++) {
					int currState = m.getState();
					boolean shouldPrint = allSteps || i==top1 || i==top2-1|| leftEdge&&t.onLeft() || rightEdge&&t.onRight() || currState<0;
					if (shouldPrint) {
						if (stepNumbers)
							System.out.print(i+" ");
						System.out.print((char)(currState+65)+" ");
						t.printTrim();
					}
					if (currState<0) break;
					//Here we have the code for updating the min and max tape head
					int tapeHead = t.getNormalizedIndex();
					if (tapeHead < minTapeHead) {
						minTapeHead = tapeHead;
						stepForMinTapeHead = i;
					}
					else if (tapeHead > maxTapeHead) {
						maxTapeHead = tapeHead;
						stepForMaxTapeHead = i;
					}
					m.act(t);
				}
				if (stepNumbers)
					System.out.print(i+" ");
				System.out.print((char)(m.getState()+65)+" ");
				t.printTrim();
			}
			else if (allSteps){
				//Normal Tape, show everything
				try {
					for (i=top1; i<top2; i++) {
						int currState = m.getState();
						if (stepNumbers) System.out.print(i+" ");
						System.out.print((char)(currState+65)+" ");
						t.printTrim();
						if (currState < 0) break;
						m.act(t);
					}
				} catch (Exception e) {
					System.out.println("Error 1 in run(): "+e.getMessage());
				}
			}
			else {
				//Normal Tape, show only the first and last configurations
				try {
					if (stepNumbers) System.out.print(i+" ");
					System.out.print((char)(m.getState()+65)+" ");
					t.printTrim();
					for (i=top1; i<top2; i++) {
						m.act(t);
						if (m.getState() < 0) break;
					}
					if (stepNumbers) System.out.print(i+" ");
					System.out.print((char)(m.getState()+65)+" ");
					String s = t.getTrimAsString();
					if (s.length() < 1000) System.out.println(s);
					else System.out.println("SWATH OF LENGTH "+s.length()+" TRUNCATED TO 1000 CHARS AT MIDDLE "+s.substring(0,500)+"..."+s.substring(s.length()-500));
				} catch (Exception e) {
					System.out.println("Error 2 in run() at step " + i + ": " +e.getMessage());
				}
			}
			if (m.getState()>=0) System.out.print((char)(m.getState()+65)+" ");
			else System.out.print("It halted!");
		} catch (Exception e) {
			System.out.println("Error 3 in run(): "+e.getMessage());
			return;
		}
		if (analytic) {
			//Here we output the min and max tape heads
			System.out.println("From start step to end step,\n"
					+ "the min tape head was "+minTapeHead+" at step "+stepForMinTapeHead+";\n"
					+ "the max tape head was "+maxTapeHead+" at step "+stepForMaxTapeHead+".");
		}
		System.out.println(name+" successful.");
		return;
	}
	
	/**Runs m on all the first numTapesToRun tapes
	 * in the enumeration of all tapes
	 * for at most maxNumSteps.
	 * Adds padlen 0s to both sides of the tapes on construction.
	 * Outputs the number of steps it took to halt, or -1 if it did not halt, for each tape.*/
	public static void runOnAllTapes(Machine m, int numTapesToRun, long maxNumSteps, int padlen) {
		int numTapesRun = 0;
		int numForBinary = 0;
		String padding = new String(new char[padlen]).replace("\0", "0");
		boolean haltedOnAllTapesSoFar = true;
		while (numTapesRun < numTapesToRun) {
			String binaryString = Integer.toBinaryString(numForBinary);
			for (int tapeHeadLocation = 0; tapeHeadLocation < binaryString.length(); tapeHeadLocation++) {
				Tape t = null;
				try {
					t = new Tape(padding+binaryString+padding, padlen + tapeHeadLocation);
					//System.out.println("Debug: t is of length "+t.getTape().length);
					System.out.print(t.getTrimAsString()+" ");
				} catch (Exception e) {
					System.out.println("Could not initialize tape: "+e.getMessage());
				}
				m.reset();
				haltedOnAllTapesSoFar &= 
						runSimple(m,t,maxNumSteps,numTapesRun);
				numTapesRun++;
				if (numTapesRun >= numTapesToRun) {
					System.out.println("Halted on all tapes: " + haltedOnAllTapesSoFar);
					return;
				}
			}
			numForBinary++;
		}
	}
	/**Returns whether the machine halted.
	 * Does _not_ automatically reset m before or after the run.*/
	public static boolean runSimple(Machine m, TapeLike t, long maxNumSteps, int numTapesRun) {
		long numSteps = 0;
		for (numSteps = 0; numSteps < maxNumSteps; numSteps++) {
			if (m.getState() < 0) {
				System.out.println("Halted on tape " + numTapesRun + " in "+ numSteps + " steps.");
				return true;
			}
			try {
				m.act(t);
			} catch (Exception e) {
				System.out.println("m.act(t) failed on tape + " + numTapesRun + ": "+e.getMessage()+".");
				return false;
			}
		}
		if (m.getState() >= 0)
			System.out.println("Did not halt on tape " + numTapesRun + " in " + numSteps + "steps.");
		return m.getState() < 0;
	}
}
