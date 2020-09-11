package machine;

/**Methods for sweep machines, with focus on step numbers at right and left edge.
 * Revised from the original methods, which can be found in AllMachines.*/
public class Sweep {
	static final int L = -1, R = 1;
	static final int REQ_DELAY = 20; //Disregard the "setup" phase:
    //when the head is going both ways in under REQ_DELAY steps.
	static final int MAX_NUM_STEPS = 1000000;
	
	//TODO: Rewrite using StretchTape for speedup
	/**Will always put them in left, right, ... format.*/
	public static int[] lrStepNums(Machine m, int seqlen) {
		if (seqlen < 0) return null;
		if (seqlen == 0) return new int[0];
		StepConfiguration sc = new StepConfiguration(new Tape(100 * (int)Math.floor(Math.sqrt(seqlen)))); //Argument is the center index
		int prevStepNum = 0;
		int[] ret = new int[seqlen];
		int retIndex = 0;
		int nextTarget = L;
		while(retIndex < seqlen) {
			try {
				m.actOnConfig(sc);
				if (sc.getNumSteps() >= MAX_NUM_STEPS) {
					System.out.println("Error 1 in Sweep.lrStepNums(): took too long");
					return null;
				}
				if (nextTarget == L && sc.offLeft() || nextTarget == R && sc.offRight()) {
					if (retIndex > 0 || retIndex == 0 && nextTarget == L && sc.getNumSteps() - prevStepNum >= REQ_DELAY) {
						ret[retIndex] = sc.getNumSteps();
						prevStepNum = sc.getNumSteps();
						retIndex++;
					}
					nextTarget *= -1;
					prevStepNum = sc.getNumSteps();
				}				
			} catch (Exception e) {
				System.out.println("Error 2 in Sweep.lrStepNums(): "+e.getMessage());
				return null;
			}
		}
		return ret;
	}
	
	/**Returns an array of StepConfigurations consisting of what the tape looks like looks like at each of those stepNums.*/
	public static StepConfiguration[] at(Machine m, int[] stepNums) {
		if (!Tools.areNonnegative(stepNums)) {
			System.out.println("In Sweep.at(): "+Tools.toString(stepNums)+" are not nonnegative.");
			return null;
		}
		if (!Tools.isIncreasing(stepNums)) {
			System.out.println("In Sweep.at(): "+Tools.toString(stepNums)+" is not increasing.");
			return null;
		}
		//StringBuffer sb = new StringBuffer();
		StepConfiguration[] ret = new StepConfiguration[stepNums.length];
		if (stepNums.length == 0) return ret;
		StepConfiguration sc = new StepConfiguration(new Tape(stepNums[stepNums.length - 1]));
		int index = 0;
		while (index < stepNums.length) {
			while (sc.getNumSteps() < stepNums[index]) {
				try {
					m.actOnConfig(sc);
				} catch (Exception e) {
					System.out.println("Error 1 in Sweep.at(): "+e.getMessage());
					return null;
				}
			}
			try {
				//sb.append(sc.getTrimAsString()+'\n');
				ret[index] = sc.copy();
			} catch (Exception e) {
				System.out.println("Error 2 in Sweep.at(): "+e.getMessage());
				return null;
			}
			index ++;
		}
		return ret; //sb.toString();
	}
	
	public static boolean isSweepUsing(Machine m, Lemma lem, Lemma otherLem) {
		//TODO: Add checking: unproved lemmas, wrong handedness
		//TODO: Test for compatibility of lem's target's base with otherLem's source's base
		//TODO: Add code dealing with numbers of steps here and in CondensedConfiguration and/or ExtendedTermfiguration
		int[] arr = Sweep.lrStepNums(m, 30);
		System.out.println("Sweep.lrStepNums says: "+Tools.toString(arr));
		System.out.println("     with differences: "+Tools.toString(Tools.differences(arr)));
		System.out.println("Alternate second differences:");
		int[][] secondDifferences = Tools.secondDifferenceArraysWithSkip(arr, 2);
		System.out.print(Tools.matrixToString(secondDifferences));
		System.out.println("And the StepConfiguration at those steps:");
		StepConfiguration[] scArray = Sweep.at(m, arr);
		CondensedConfiguration[] ccArray = new CondensedConfiguration[scArray.length];
		int direction;
		int[] pattern;
		for (int i=0; i<scArray.length; i++) {
			if (i%2 == 0) {direction = R; pattern = lem.getSource().getBase();}
			else {direction = L; pattern = otherLem.getSource().getBase();}
			StepConfiguration trimmedVersion = scArray[i].trimmed();
			System.out.println(i+" "+trimmedVersion);
			ccArray[i] = trimmedVersion.condenseAndSplitUsing(direction, pattern);
			System.out.println(i+" "+ccArray[i]);
		}
		if (Tools.areConstant(secondDifferences)) {
			//We're likely in the easiest case for sweeps; let's check the condensed configurations to see
			int[] increases = CondensedConfiguration.simpleIncreasingPatterns(ccArray, 2);
			System.out.println("increases: "+Tools.toString(increases));
			if (Tools.arePositive(increases)) {
				ExtendedTermfiguration[] etfs = new ExtendedTermfiguration[2];
				for (int i=0; i<2; i++) {
					System.out.println("Generalizing ccArray["+i+"]="+ccArray[i]+": ");
					if (i%2 == 0) {direction = R; pattern = lem.getSource().getBase();}
					else {direction = L; pattern = otherLem.getSource().getBase();}
					ExtendedTermfiguration etf = ccArray[i].generalizeFromEnd(pattern, direction, increases[i]);
					etfs[i] = etf;
					System.out.println(etf);
				}
				if (etfs[0] != null && etfs[1] != null) {
					//Here we should write the algorithm for proving that etfs[0] becomes etfs[1] and vice versa with the increase increases[i].
					//Now make the SweepTheorem
				}
			}
		}
		return false;
	}
	
	
}
