package machine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class Run {
	static final char UNIT_SEPARATOR = 31;
	/**mode 0: guaranteed long enough tape.
	 * mode 1: end step divided by 1000.
	 * mode 2: constant million.
	 * initConfig should have two tokens: tape contents and state.
	 * Leave it null or empty for blank tape in state A.*/
	public static void run(List<Machine> _machineList, int num,   long top1, long top2, int typeOption, boolean leftEdge, boolean rightEdge, boolean allSteps, boolean stepNumbers, int mode, String initConfig) {
		if (num>0 && num<_machineList.size()) {
			Machine m = _machineList.get(num);
			m.reset();
			run(m,top1,top2,typeOption,leftEdge,rightEdge,allSteps,stepNumbers, mode, initConfig);
		}
		else if (num==0) {
			Thread t = new Thread(new Runnable() {
				public void run() {
		        	  for (int i=1; i<_machineList.size(); i++) {
		  				Machine m = _machineList.get(i);
		  				m.reset();
		  				System.out.println("For HNR#"+i+":");
		  				Run.run(m,top1,top2,typeOption,leftEdge,rightEdge,allSteps,stepNumbers, mode, initConfig);
		  				System.out.println(UNIT_SEPARATOR);
		        	  }
		        }
			});
			t.start();
		}
	}
	public static void run(Machine m, long top1, long top2, int typeOption, boolean leftEdge, boolean rightEdge, boolean allSteps, boolean stepNumbers, int mode, String initConfig) {
		//TODO: mode hasn't been implemented yet
		//Thousandth of end step, constant million, etc.
		if (typeOption == 2) {
			runAccelerated(m,top1,top2,leftEdge,rightEdge,allSteps,stepNumbers,mode,initConfig);
			return;
		}
		else if (typeOption == 3) {
			runSixteenMacro(m,top1,top2,leftEdge,rightEdge,allSteps,stepNumbers,mode,initConfig);
			return;
		}
		boolean analytic = (typeOption == 1);
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
			if (initConfig!=null && initConfig.length()>0) setTapeLikeAndState(t, m, initConfig, analytic);
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
					if (s.length() < 10000) System.out.println(s);
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
	
	/**Runs the machine normally until top1,
	 * then condenses the tape according to patternList (?)
	 * and applies lemmaList (?) to accelerate the run from then until top2.*/
	public static void runAccelerated(Machine m, long top1, long top2, boolean leftEdge, boolean rightEdge, boolean allSteps, boolean stepNumbers, int mode, String initConfig) {
		//Probably amn't going to implement mode.
		if (top1 > Integer.MAX_VALUE) {
			System.out.println("In runAccelerated(): Error 1: Start step too high.");
			return;
		}
		int top1int = (int) top1;
		List<Lemma> protolemlist = new ArrayList<Lemma>();
		for (int braky = 0; braky < 10; braky ++) { //breaks the run from 0 to top1int into 10 phases, looks for best patterns for each phase
			int[][] patternArray = Acceleration.bestPattern(m, top1int * braky / 10, top1int * (braky + 1) / 10, 30); //30 is max pattern length
			if (patternArray == null) return;
			Lemma lem = Acceleration.guessLemma(m, patternArray);
			if (lem == null) {
				System.out.println("No hypothesis was found.");
				continue;
			}
			if (lem.isProved()) {
				System.out.println("The Lemma was proved!");
				protolemlist.add(lem);
			}
			else System.out.println("The Lemma was not proved.");
		}
		LemmaList lemlist = null;
		try {lemlist = new LemmaList(protolemlist);}
		catch (Exception e) {
			System.out.println("In runAccelerated(): Error 2: " + e.getMessage());
			return;
		}
		lemlist.reduce();
		if (lemlist == null || lemlist.size() == 0) {
			System.out.println("In runAccelerated(): there was no lemma.");
			return;
		}
		System.out.println("Found "+lemlist.size()+" distinct lemmas.");
		//Warning: patternArray in Acceleration and patternList in Configuration are completely different.
		Tape t = null;
		if (initConfig!=null && initConfig.length()>0) {
			try {setTapeLikeAndState(t, m, initConfig, false);}
			catch (Exception e) {
				System.out.println("In runAccelerated(): Error 3: " + e.getMessage());
				return;
			}
		}
		else {
			int idx = (int) Math.min(top2,500000);
			try {
				t = new Tape(new int[idx*2+1],idx);
				m.reset();
			} catch (Exception e) {
				System.out.println("In runAccelerated(): Error 4: " + e.getMessage());
				return;
			}
		}
		Configuration c = new Configuration (t, m.getState());
		System.out.println("Configuration c at step 0: ");
		System.out.println(c.getTrimAsString());
		for (int i = 0; i < top1int; i++) {
			try {m.actOnConfig(c);}
			catch (Exception e) {
				System.out.println("In runAccelerated(): Error 5: " + e.getMessage());
				return;
			}
		}
		System.out.println("Configuration c at step " + top1int + ": ");
		System.out.println(c.getTrimAsString());
		List<int[]> patternList = new ArrayList<int[]>();
		Iterator<Lemma> lemIterator = lemlist.getLemList().iterator();
		while (lemIterator.hasNext()) {
			Lemma lem = lemIterator.next();
			int[] pattern = lem.getSource().getBase();
			Iterator<int[]> patternIterator = patternList.iterator();
			boolean wasFound = false;
			while (patternIterator.hasNext()) {
				int[] patternToCompare = patternIterator.next();
				if (Tools.areIdentical(pattern, patternToCompare)) wasFound = true;
			}
			if (!wasFound) patternList.add(pattern);
		}
		patternList.add(new int[] {0});
		System.out.println("Now condensing the following configuration: ");
		System.out.println(c.getTrimAsString());
		CondensedConfiguration cc = c.condenseUsing(patternList);
		System.out.println("Obtained: ");
		System.out.println(cc.toString());
		int numSteps = top1int;
		try {
			while (numSteps <= top2) {
				int numPassed = Acceleration.act(cc, lemlist);
				if (numPassed == 0) {
					System.out.println("In runAccelerated: numPassed == 0");
					break;
				}
				cc.glueOutward();
				numSteps += numPassed;
				if (numPassed > 1) System.out.println(numPassed + " steps passed.");
				System.out.println(numSteps + " " + cc);
			}
		} catch (Exception e) {
			System.out.println("In runAccelerated(): Error 6: " + e.getMessage());
			return;
		}
	}
	
	/**Approximates a good tape index to start with from a number of steps to run.
	 * Assumes at most square root machine.
	 * Guaranteed to be divisible by 8.*/
	private static int reduceNumber (long n) {
		if (n < 100) return (int)(n+8)/8*8;
		else if (n < 10000) return 96 + (int)(n/10)/8*8;
		else return Math.min(1000000,  10 * (int) (Math.sqrt((double)n))/8*8);
	}
	
	public static void runSixteenMacro(Machine m, long top1, long top2, boolean leftEdge, boolean rightEdge, boolean allSteps, boolean stepNumbers, int mode, String initConfig) {
		if (initConfig!=null && initConfig.length()!=0) {
			System.out.println("Run.runSixteenMacro() does not yet support custom tapes.");
			return;
		}
		KMachine m16 = null;
		if (top1>0) {
			m16 = new KMachine(m);
			m16.loadFreqTable(top1, reduceNumber(top1));
			m.setSpeedUp(m16);
		}
		else {
			if (m.hasSpeedUp()) m16 = m.getSpeedUp();
			else m16 = new KMachine(m);
		}
		runSixteenMacro(m16,top2,leftEdge,rightEdge,allSteps,stepNumbers,mode,initConfig);
	}
	
	public static void runSixteenMacro(KMachine m16, long top2, boolean leftEdge, boolean rightEdge, boolean allSteps, boolean stepNumbers, int mode, String initConfig) {	
		if (m16==null) {
			System.out.println("Null 16-Machine in runSixteenMacro()");
			return;
		}
		m16.reset();
		int index = reduceNumber(top2);
		Tape t = null;
		try {
			t = new Tape(new int[index*2],index);
		} catch (Exception e) {
			System.out.println("Run.runSixteenMacro() failed: "+e.getMessage());
			return;
		}
		BitTape bt = new BitTape(t);
		long numSteps = 0;
		int currSteps = 0;
		if (stepNumbers) System.out.print(numSteps + " ");
		System.out.println((char)(m16.getState()+65) + " " + bt.getTrimAsString());
		while (numSteps < top2) {
			if (allSteps) {
				if (stepNumbers) System.out.print(numSteps + " ");
				System.out.println((char)(m16.getState()+65) + " " + bt.getTrimAsString());
			}
			try {
				currSteps = m16.actOnBitTape(bt);
			} catch (Exception e) {
				System.out.println("In Run.runSixteenMacro(), actOnBitTape() failed: "+e.getMessage());
				return;
			}
			if (currSteps<=0) {
				System.out.println("In Run.runSixteenMacro(), actOnBitTape() returned "+currSteps+" steps.");
				return;
			}
			numSteps+=currSteps;
		}
		if (stepNumbers) System.out.print(numSteps + " ");
		System.out.println((char)(m16.getState()+65) + " " + bt.getTrimAsString());
		m16.doStatistics();
	}
	
	public static void setTapeLikeAndState(TapeLike t, Machine m, String initConfig, boolean analytic) throws Exception {
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
}
