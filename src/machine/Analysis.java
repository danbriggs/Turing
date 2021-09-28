package machine;

import java.util.ArrayList;
import java.util.List;

/**Contains static methods for machine-specific analysis.*/
public class Analysis {
	
	/**Analyzes the nth machine.*/
	public static void analyze(List<Machine> machineList, int num, int start, int stop) {
		if (num==3) {
			thirdMachineRightEndRuns(machineList.get(num), 3);
			thirdMachineRuns(machineList.get(num), start, stop);
		}
		else if (num==7) {
			nthMachineLeftEndRun(machineList.get(num), start, stop);
		}
		else if (num==15) {
			fifteenthMachineRun(machineList.get(num), start, stop);
		}
		else if (num==16) {
			sixteenthMachineRightEndRun(machineList.get(num), start, stop);
		}
		else {
			System.out.println("Haven't written analysis for that machine yet.");
		}
	}
	
	private static void sixteenthMachineRightEndRun(Machine m, int start, int stop) {
		int idx = (int) Math.min(stop,500000);
		TapeLike t = new StretchTape(idx);
		int i=0;
		for (i=0; i<start; i++) {
			try {
				m.act(t);
			} catch (Exception e) {
				System.out.println("Error 1 in Analysis.sixteenthMachineRightEndRun(): "+e.getMessage());
				return;
			}
		}
		int delay = 0;
		for (i=start; i<stop; i++) {
			int currState = m.getState();
			boolean shouldPrint = i==start || i==stop-1 || t.onRight() && delay >= 10 || currState<0;
			if (shouldPrint) {
				System.out.print(i+" "+(char)(currState+65)+" ");
				t.printTrim();
				delay = 0;
			}
			else {
				delay++;
			}
			if (currState<0) break;
			try {
				m.act(t);
			} catch (Exception e) {
				System.out.println("Error 2 in Analysis.sixteenthMachineRightEndRun(): "+e.getMessage());
				return;
			}
		}
		System.out.print(i+" "+(char)(m.getState()+65)+" ");
		t.printTrim();

	}

	/**Pass HNR#3 ONLY into this method.
	 * Creates a configuration of the form 1^(2m)01^(2n)i A
	 * for a variety of pairs of positive integers m, n,
	 * and runs m on them until they reach the right end of the tape again
	 * (after having left it for more than 10 steps).*/
	public static void thirdMachineRightEndRuns(Machine ma, int beginState) {
		int leftPadding = 100;
		List<int[]> patternList = new ArrayList<int[]>();
		int[] pattern = new int[] {1};
		patternList.add(pattern);
		int numTrials = 15;
		int[][] numSwaths = new int[numTrials][numTrials];
		int[][] stepNums = new int[numTrials][numTrials];
		int[][] lefts = new int[numTrials][numTrials];
		int[][] rights = new int[numTrials][numTrials];
		int[][] states = new int[numTrials][numTrials];
		for (int m = 0; m < numTrials; m++) {
			for (int n = 0; n < numTrials; n++) {
				//Create a Tape of the form 1^(2m)01^(2n)i
				int[] arr = new int[leftPadding+2*m+1+2*n+2+10];//TODO: change 2 back to 1
				for (int i = leftPadding; i < leftPadding + 2*m; i++) arr[i] = 1;
				for (int i = leftPadding + 2*m + 1; i < leftPadding + 2*m + 1 + 2*n + 2; i++) arr[i] = 1;//TODO: change 2 back to 1
				Tape ta = null;
				try {ta = new Tape(arr, leftPadding + 2*m + 1 + 2*n + 1);}//TODO: delete last +1
				catch (Exception e) {
					System.out.println("Error 1 in Analysis.thirdMachineRightEndRuns(): "+e.getMessage());
					return;
				}
				ma.setState(beginState);
				System.out.println("0 "+ Tools.asLetter(ma.getState()) + " " + ta.getTrimAsString());
				int lastTimeWasOnRight = 0;
				for (int i = 0; i < 10000; i++) {
					try {ma.act(ta);}
					catch (Exception e) {
						System.out.println("Error 2 in Analysis.thirdMachineRightEndRuns(): "+e.getMessage());
						break;
					}
					if (ta.onRight()) {
						if (i - lastTimeWasOnRight >= 10) {
							Configuration c;
							CondensedConfiguration cc;
							try {
								c = new Configuration(Tools.asLetter(ma.getState()) + " " + ta.getTrimAsString());
								cc = c.condenseUsing(patternList);
							} catch (Exception e) {
								System.out.println("Error 3 in Analysis.thirdMachineRightEndRuns(): "+e.getMessage());
								return;
							}
							System.out.println(i + " " + c);
							System.out.println(i + " " + cc );
							System.out.println("Num bases: "+cc.size());
							stepNums[m][n] = i;
							if (cc.size()%2!=1) System.out.println("Warning: even number of bases");
							numSwaths[m][n] = (cc.size()+1)/2;
							lefts[m][n] = cc.get(0).getExponent();
							rights[m][n] = cc.get(cc.size()-1).getExponent();
							states[m][n] = cc.getState();
							break;
						}
						else {
							lastTimeWasOnRight = i;
						}
					}
				}
			}
		}
		System.out.println("stepNums: ");
		System.out.println(Tools.matrixToString(stepNums));
		System.out.println("numSwaths: ");
		System.out.println(Tools.matrixToString(numSwaths));
		System.out.println("lefts: ");
		System.out.println(Tools.matrixToString(lefts));
		System.out.println("rights: ");
		System.out.println(Tools.matrixToString(rights));
		System.out.println("states: ");
		System.out.println(Tools.matrixToString(states));
		return;
	}
	
	public static void nthMachineLeftEndRun(Machine m, int start, int stop) {
		List<int[]> patternList = new ArrayList<int[]>();
		int[] pattern0 = new int[] {0};
		int[] pattern1 = new int[] {1};
		patternList.add(pattern0);
		patternList.add(pattern1);
		m.reset();
		TapeLike t = null;
		try {t = new StretchTape(new int[1], 0);}
		catch (Exception e) {
			System.out.println("Error 1 in Analysis.nthMachineLeftEndRun(): "+e.getMessage());
			return;
		}
		int lastTimeWasOnLeft = 0;
		int i;
		for (i = 0; i < start; i++) {
			try {
				m.act(t);
			} catch (Exception e) {
				System.out.println("Error 2 in Analysis.nthMachineLeftEndRun(): "+e.getMessage());
				return;
			}
		}
		for (; i < stop; i++) {
			try {m.act(t);}
			catch (Exception e) {
				System.out.println("Error 3 in Analysis.nthMachineLeftEndRun(): "+e.getMessage());
				return;
			}
			if (t.onLeft()) {
				if (i - lastTimeWasOnLeft >= 10) {
					try {
						Configuration c = new Configuration(t.getTape(), t.getIndex(), m.getState());
						CondensedConfiguration cc = c.condenseUsing(patternList);
						System.out.println(i + " " + cc);
					}
					catch (Exception e) {
						System.out.println("Error 4 in Analysis.nthMachineLeftEndRun(): "+e.getMessage());
						return;
					}
				}
				lastTimeWasOnLeft = i;
			}
		}
	}
	
	public static void thirdMachineRuns(Machine m, int start, int stop) {
		List<int[]> patternList = new ArrayList<int[]>();
		int[] pattern0 = new int[] {0};
		int[] pattern1 = new int[] {1};
		patternList.add(pattern0);
		patternList.add(pattern1);
		m.reset();
		TapeLike t = null;
		try {t = new StretchTape(new int[1], 0);}
		catch (Exception e) {
			System.out.println("Error 1 in Analysis.thirdMachineRuns(): "+e.getMessage());
			return;
		}
		int lastTimeWasOnRight = 0;
		int i;
		for (i = 0; i < start; i++) {
			try {
				m.act(t);
			} catch (Exception e) {
				System.out.println("Error 2 in Analysis.thirdMachineRuns(): "+e.getMessage());
				return;
			}
		}
		for (; i < stop; i++) {
			try {m.act(t);}
			catch (Exception e) {
				System.out.println("Error 3 in Analysis.thirdMachineRuns(): "+e.getMessage());
				return;
			}
			if (t.onRight()) {
				if (i - lastTimeWasOnRight >= 10) {
					try {
						Configuration c = new Configuration(t.getTape(), t.getIndex(), m.getState());
						CondensedConfiguration cc = c.condenseUsing(patternList);
						System.out.println(i + " " + cc);
					}
					catch (Exception e) {
						System.out.println("Error 4 in Analysis.thirdMachineRuns(): "+e.getMessage());
						return;
					}
				}
				lastTimeWasOnRight = i;
			}
		}
	}
	
	public static void fifteenthMachineRun(Machine m, int start, int stop) {
		//Here we explicitly build Lemmas about the fifteenth machine's runs.
		//Below is a reminder of how to build Lemmas from Tests.java.
		/*int[] base1 = {0,1,1};
			int[] exp1 = {9,1};
			int[] index1 = {0};
			int[] base2 = {1,1,0};
			int[] exp2 = {9,1};
			int[] index2 = {27,3};
			int[] numSteps = {27,3};
			Termfiguration t1 = new Termfiguration(base1,exp1,index1,2);
			Termfiguration t2 = new Termfiguration(base2,exp2,index2,2);
			System.out.println(t1.toString());
			System.out.println(t2.toString());
			_lemma1 = new Lemma(_m,t1,t2,numSteps);
			System.out.println(t1+" has successor "+t1.successor());
			System.out.println(t2+" has successor "+t2.successor());
			boolean p = _lemma1.isProved();*/
	}
}
