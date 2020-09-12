package machine;

/**Methods for sweep machines, with focus on step numbers at right and left edge.
 * Revised from the original methods, which can be found in AllMachines.*/
public class Sweep {
	static final int L = -1, R = 1;
	static final int REQ_DELAY = 12; //Disregard the "setup" phase:
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
		int minIndex = sc.getIndex(), maxIndex = sc.getIndex();
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
					if (nextTarget == L) minIndex = sc.getIndex();
					if (nextTarget == R) maxIndex = sc.getIndex();
					nextTarget *= -1;
					prevStepNum = sc.getNumSteps();
				}
				else if (retIndex > 0 && nextTarget == R && sc.getIndex() < minIndex) {
					//we found a new lower bound, so use it
					ret[retIndex - 1] = sc.getNumSteps();
					minIndex = sc.getIndex();
				}
				else if (retIndex > 0 && nextTarget == L && sc.getIndex() > maxIndex) {
					//we found a new lower bound, so use it
					ret[retIndex - 1] = sc.getNumSteps();
					maxIndex = sc.getIndex();
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
	
	public static boolean isSweepUsing(Machine m, Lemma lem, Lemma otherLem, int bestStartStep1, int bestStartStep2) {
		//TODO: Add checking: unproved lemmas, wrong handedness
		//TODO: Test for compatibility of lem's target's base with otherLem's source's base
		//TODO: Add code dealing with numbers of steps here and in CondensedConfiguration and/or ExtendedTermfiguration
		
		int[] arr = Sweep.lrStepNums(m, 30);
		System.out.println("Sweep.lrStepNums says: "+Tools.toString(arr));
		System.out.println("     with differences: "+Tools.toString(Tools.differences(arr)));
		System.out.println("Alternate second differences:");
		int[][] secondDifferences = Tools.secondDifferenceArraysWithSkip(arr, 2);
		System.out.print(Tools.matrixToString(secondDifferences));
		System.out.println("And the StepConfiguration at the first two steps:");
		StepConfiguration[] scArray = Sweep.at(m, arr);
		CondensedConfiguration[] ccArray = new CondensedConfiguration[scArray.length];
		int direction;
		int[] pattern;
		int bestFirstIndex = -2, bestSecondIndex = -2;
		boolean firstIndexFound = false, secondIndexFound = false;
		for (int i=0; i<scArray.length; i++) {
			if (i%2 == 0) {direction = R; pattern = lem.getSource().getBase();}
			else {direction = L; pattern = otherLem.getSource().getBase();}
			StepConfiguration trimmedVersion = scArray[i].trimmed();
			if (i < 10) System.out.println(i+" "+trimmedVersion);
			ccArray[i] = trimmedVersion.condenseAndSplitUsing(direction, pattern);
			if (i < 10) System.out.println(i+" "+ccArray[i]);
			if (!firstIndexFound && scArray[i].getNumSteps() > bestStartStep1) {
				bestFirstIndex = i - 1;
				firstIndexFound = true;
			}
			if (!secondIndexFound && scArray[i].getNumSteps() > bestStartStep2) {
				bestSecondIndex = i - 1;
				secondIndexFound = true;
			}
		}
		if (firstIndexFound) {
			System.out.println("bestFirstIndex: "+bestFirstIndex);
		}
		if (secondIndexFound) {
			System.out.println("bestSecondIndex: "+bestSecondIndex);
		}
		if (Tools.areConstant(secondDifferences)) {
			//We're likely in the easiest case for sweeps; let's check the condensed configurations to see
			int[] increases = CondensedConfiguration.simpleIncreasingPatterns(ccArray, 2);
			System.out.println("increases: "+Tools.toString(increases));
			if (Tools.arePositive(increases)) {
				ExtendedTermfiguration[] etfs = new ExtendedTermfiguration[2];
				/*TODO: Instead of generalizing ccArray[i],
				 *      construct new SCs and run them for bestStartStep_i steps;
				 *      look in scArray for the step number just before that.
				 *      Measure the displacement; now you know where to apply the pattern.*/
				if (bestFirstIndex >=0 && bestSecondIndex == bestFirstIndex + 1) {
					//Here we can just run a StepConfiguration for scArray[whatever].getNumSteps()
					//and however many more it takes to get to bestStartStep1,
					//measure the head displacement, and use that to compress & generalize.
					try {
						int[] targetNumSteps = new int[2];
						int numStepsPassed1 = scArray[bestFirstIndex + 1].getNumSteps() - scArray[bestFirstIndex    ].getNumSteps();
						int numStepsPassed2 = scArray[bestFirstIndex + 2].getNumSteps() - scArray[bestFirstIndex + 1].getNumSteps();
						int numStepsPassed  = scArray[bestFirstIndex + 2].getNumSteps() - scArray[bestFirstIndex    ].getNumSteps();
						System.out.println("In Sweep.isSweepUsing(): Total number of steps passed for sweep: "+numStepsPassed);
						int numStepsPassedPrime = scArray[bestFirstIndex + 4].getNumSteps() - scArray[bestFirstIndex + 2].getNumSteps();
						System.out.println("In Sweep.isSweepUsing(): Successor total steps passed for sweep: "+numStepsPassedPrime);
						int[] lemNumStepsPassed = Tools.add(lem.getNumSteps(), otherLem.getNumSteps());
						System.out.println("In Sweep.isSweepUsing(): Total number of steps passed by lemmas: "+Tools.toPolynomialString(lemNumStepsPassed,'N'));
						int length1 = scArray[bestFirstIndex ].length();
						int length2 = scArray[bestSecondIndex].length();
						StepConfiguration scForTheorem1 = new StepConfiguration(new Tape(new int[length1], length1/2));
						StepConfiguration scForTheorem2 = new StepConfiguration(new Tape(new int[length2], length2/2)); //:(
						for (int steps = 0; steps < scArray[bestFirstIndex ].getNumSteps(); steps++) m.actOnConfig(scForTheorem1);
						for (int steps = 0; steps < scArray[bestSecondIndex].getNumSteps(); steps++) m.actOnConfig(scForTheorem2);
						int tapeHeadIndex1Prev = scForTheorem1.getIndex();
						int tapeHeadIndex2Prev = scForTheorem2.getIndex();
						while (scForTheorem1.getNumSteps() < bestStartStep1) m.actOnConfig(scForTheorem1);
						System.out.println("In Sweep.isSweepUsing(): scForTheorem1.getNumSteps() = "+ scForTheorem1.getNumSteps() +", "
								+ "scForTheorem1.getState() = "+Tools.asLetter(scForTheorem1.getState()));
						while (scForTheorem2.getNumSteps() < bestStartStep2) m.actOnConfig(scForTheorem2);
						System.out.println("In Sweep.isSweepUsing(): scForTheorem2.getNumSteps() = "+ scForTheorem2.getNumSteps() +", "
								+ "scForTheorem2.getState() = "+Tools.asLetter(scForTheorem1.getState()));
						int tapeHeadIndex1After = scForTheorem1.getIndex();
						int tapeHeadIndex2After = scForTheorem2.getIndex();
						
						int tapeHeadIndex1Displacement = tapeHeadIndex1After - tapeHeadIndex1Prev;
						System.out.println("In Sweep.isSweepUsing(): tapeHeadIndex1Displacement = "+tapeHeadIndex1Displacement);
						//We got displacement 0! Try fixing the loop at the beginning of this method to use step numbers
						//when the tape head is at extremes per loop, rather than just beyond the 1 bits.
						int tapeHeadIndex2Displacement = tapeHeadIndex2After - tapeHeadIndex2Prev;
						System.out.println("In Sweep.isSweepUsing(): tapeHeadIndex2Displacement = "+tapeHeadIndex2Displacement);
						StepConfiguration trimmed1 = scArray[bestFirstIndex ].trimmed();
						StepConfiguration trimmed2 = scArray[bestSecondIndex].trimmed();
						int trimmedIndex1 = tapeHeadIndex1Displacement;
						int trimmedIndex2 = trimmed2.length() - 1 + tapeHeadIndex2Displacement; //This last term is generally negative
						System.out.println("trimmed1 = "+trimmed1);
						CondensedConfiguration cc1 = trimmed1.condenseAndSplitUsing(R, lem     .getSource().getBase(), trimmedIndex1);
						System.out.println("trimmed2 = "+trimmed2);
						CondensedConfiguration cc2 = trimmed2.condenseAndSplitUsing(L, otherLem.getSource().getBase(), trimmedIndex2);
						System.out.println("Generalizing cc1 = " + cc1 +": ");
						ExtendedTermfiguration etf1 = cc1.generalizeFromEnd(lem     .getSource().getBase(), R, increases[0]);
						ExtendedTermfiguration etf2 = cc2.generalizeFromEnd(otherLem.getSource().getBase(), L, increases[1]);
						System.out.println("etf1 & etf2: ");
						System.out.println(etf1);
						System.out.println(etf2);
						if (etf1 != null && etf2 != null) {
							//TODO: Guide construction of the SweepTheorem as to how many steps it should take to get to the successor.
							SweepTheorem st = new SweepTheorem(lem, otherLem, etf1, etf2, scArray[bestFirstIndex].getNumSteps(), numStepsPassed1, numStepsPassed2);
							if (st.isProved()) return true;
						}
					} catch (Exception e) {
						System.out.println("In Sweep.isSweepUsing(): "+e.getMessage());
						StackTraceElement[] stes = e.getStackTrace();
						for (int i=0; i<stes.length; i++) System.out.println(stes[i]);
						return false;
					}
				}
				for (int i=0; i<2; i++) {
					System.out.println("Generalizing ccArray["+i+"]="+ccArray[i]+": ");
					if (i%2 == 0) {direction = R; pattern = lem.getSource().getBase();}
					else {direction = L; pattern = otherLem.getSource().getBase();}
					ExtendedTermfiguration etf = ccArray[i].generalizeFromEnd(pattern, direction, increases[i]);
					etfs[i] = etf;
					System.out.println(etf);
				}
				if (etfs[0] != null && etfs[1] != null) {
					SweepTheorem st = new SweepTheorem(lem, otherLem, etfs[0], etfs[1], scArray[0].getNumSteps(), 0, 0);
					if (st.isProved()) return true;
				}
			}
		}
		return false;
	}
	
	
}
