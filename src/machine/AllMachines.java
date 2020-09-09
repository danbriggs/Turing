package machine;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**Class for repeating the work Skelet did by 2003.
 * Start by enumerating machines.*/
public class AllMachines {
	static final int A=0, B=1, C=2, D=3, E=4, H=-1;
	static final int L=-1, R=1;
	static final int TO_WRITE=0, TO_GO=1, NEXT_STATE=2;
	static final boolean ADD_TO_STACK = true;
	static final int MAX_OUTPUT_SIZE = 5; //Was 1000
	static final int SHIFT_NUM_STEPS = 50;
	static final int HOW_FAR_AWAY_ALLOWED = 5;
	//The next three are for isSweepHelper()
	static final int SWEEPCATCH_DURATION = 100; //how many steps to go checking whether the tape head is in bestSpot in bestState
	static final int MAX_NUM_TRIES = 5; //the number of times we'll try to condense and apply a lemma
	static final int MAX_WANDERING = 1000; //while it's precisely out of bounds between lemma use
	int[][][] _states;
	boolean[][] _transitionFilled;
	List<Point> _usedTransitions; //To keep track of the order in which they were filled.
	int _outputSize; //To allow the process to break if the number of partial machines output exceeds MAX_OUTPUT_SIZE.
	long _distanceCovered; //How many machines out of the 40,960,000,000 we've passed.
	
	AllMachines() {
		_states = new int[5][3][2];
		_transitionFilled = new boolean[5][2];
		_usedTransitions = new ArrayList<Point>();
		_outputSize = 0;
		_distanceCovered = 0;
		try {
			FileWriter myWriter = new FileWriter("sweepmachines.txt", false);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Error in AllMachines.AllMachines(): "+e.getMessage());
		}
	}
	
	/**Makes each machine in the list of all machines.
	 * The first 12,800,000,000 have C1L, H1L, and either DR, DL, CR, BR, or AR
	 * in transitions A0, B0, C0, respectively.
	 * The next  7,680,000,000 have B1L/H1L in A, and CR, CL, or BR in B0.
	 * The next 12,800,000,000 have C1L in A0, H1L in B1, and DR, DL, CR, BL, or AR in C0.
	 * The last  7,680,000,000 have B1L in A0, H1L in B1, and CR, CL, or AR  in B0.
	 * We fill them in the order Skelet seems to have. */
	void makeMachines() throws Exception {
		
		//Class B0:H1L
		fill(A,0,C,1,L);
		fill(B,0,H,1,L);
		for (int bit = 1; bit >=0; bit--) {
			fillCompleteUnfill(C,0,D,bit,R);
			fillCompleteUnfill(C,0,D,bit,L);
			fillCompleteUnfill(C,0,C,bit,R);
			fillCompleteUnfill(C,0,B,bit,R);
			fillCompleteUnfill(C,0,A,bit,R);
		}
		unfill(A,0);
		unfill(B,0);
		
		//Class A1:H1L
		fill(A,0,B,1,L);
		fill(A,1,H,1,L);
		for (int bit = 1; bit >=0; bit--) {
			fillCompleteUnfill(B,0,C,bit,R);
			fillCompleteUnfill(B,0,C,bit,L);
			fillCompleteUnfill(B,0,B,bit,R);
		}
		unfill(A,0);
		unfill(A,1);

		//Class B1:H1L, A0:C1L
		fill(A,0,C,1,L);
		fill(B,1,H,1,L);
		for (int bit = 1; bit >=0; bit--) {
			fillCompleteUnfill(C,0,D,bit,R);
			fillCompleteUnfill(C,0,D,bit,L);
			fillCompleteUnfill(C,0,C,bit,R);
			fillCompleteUnfill(C,0,B,bit,L);
			fillCompleteUnfill(C,0,A,bit,R);
		}
		unfill(A,0);
		unfill(B,1);

		//Class B1:H1L, A0:B1L
		fill(A,0,B,1,L);
		fill(B,1,H,1,L);
		for (int bit = 1; bit >=0; bit--) {
			fillCompleteUnfill(B,0,C,bit,R);
			fillCompleteUnfill(B,0,C,bit,L);
			fillCompleteUnfill(B,0,A,bit,R);
		}
		unfill(A,0);
		unfill(B,1);
	}
	
	void fillCompleteUnfill(int currState, int currBit, int nextState, int nextBit, int dir) throws Exception {
		fill(currState, currBit, nextState, nextBit, dir);
		complete();
		unfill(currState, currBit);
	}
	
	void fill(int currState, int currBit, int nextState, int nextBit, int dir) throws Exception {
		if (_transitionFilled[currState][currBit]) throw new Exception("State "+currState+" bit "+currBit+" already filled.");
		_states[currState][TO_WRITE  ][currBit] = nextBit;
		_states[currState][TO_GO     ][currBit] = dir;
		_states[currState][NEXT_STATE][currBit] = nextState;
		_transitionFilled[currState][currBit] = true;
	}
	
	void unfill(int currState, int currBit) throws Exception {
		if (!_transitionFilled[currState][currBit]) throw new Exception("State "+currState+" bit "+currBit+" already empty.");
		_transitionFilled[currState][currBit] = false;
	}
	
	/**	Begin running this partial machine on a Tape, adding each transition passed to _usedTransitions.
		For error checking, make sure it runs for exactly two steps before coming to a branch.
		When it does, try filling that transition with each of the (up to) 20 possibilities in turn,
		running for one more step, adding it to the List, and recursing.
		(Fewer possibilities if more unused states.)
		Probably best to do this by calling a helper function with a depth corresponding to the size of usedTransitions.
		If the run has gone on for more than 107 steps and there is still an unused state, give up.
		Skip choosing transitions that would lead directly to the halt condition.
		If the machine has reached a configuration loop, give up.
		If the machine is exhibiting shift recurrent behavior already, give up.
		If all 10 transitions get filled and 1000 steps pass, add it to the list of machines.
		If not all 10 transitions get filled, but 1000 steps pass, we will have to come up with
		all arbitrary combinations for the rest of the transitions, and add the resulting
		machines to the list of machines.*/
	void complete() throws Exception {
		StepConfiguration workConfig = new StepConfiguration(new int[220+2*SHIFT_NUM_STEPS], 110+SHIFT_NUM_STEPS);
		for (int i=0; i<2; i++) partialAct(workConfig, ADD_TO_STACK);
		//Analysis by hand shows there should always be branching after two steps.
		if (known(workConfig)) throw new Exception ("No branching after second step");
		if (_usedTransitions.size()!=2) throw new Exception ("_usedTransitions is of size "+_usedTransitions.size()+" after second step");
		for (int i=0; i<20; i++) {
			completeFromHere(workConfig, i, _distanceCovered + i*Tools.longPow(20, 6));
			_distanceCovered += 1280000000;
		}
	}
	
	void completeFromHere(StepConfiguration workConfig, int i, long baseMachineIndex) throws Exception {	
		if (i<0||i>=20) throw new Exception("Invalid i="+i+" for completeFrom()");
		int currState = workConfig.getState();
		int currBit = workConfig.getSymbol();
		
		//The following five lines are to encourage the list of machines
		//to come out in roughly the same order in which they do in Skelet's bbfind.
		int toWrite = 1-(i/10);
		int rem = i%10;
		int nextState = 4-rem/2;
		int remrem = rem%2;
		int toGo = (1-remrem)*2-1;
		
		StepConfiguration tempConfig = workConfig.copy();
		fill(currState, currBit, nextState, toWrite, toGo);
		partialAct(tempConfig, ADD_TO_STACK);
		//If we've already run into another branch, might as well branch.
		if (!known(tempConfig)) {
			long numToBeCoveredByEach = Tools.longPow(20,10-_usedTransitions.size()-1-1);//1 because a halt condition is known too.
			for (int j=0; j<20; j++) {
				long nextBaseMachineIndex = baseMachineIndex + j*numToBeCoveredByEach;
				completeFromHere(tempConfig, j, nextBaseMachineIndex);
			}
		}
		else if (tempConfig.atExtreme())
			throw new IndexOutOfBoundsException("In AllMachines.completeFromHere("+tempConfig+","+i+"), "
					+ "depth "+_usedTransitions.size()+": "
					+ "index "+tempConfig.getIndex()+" is at extreme");
		//Otherwise, use lookAhead to make sure the machine isn't about to halt.
		else if (lookAhead(tempConfig)!=H)
			completeFromHereHelper(tempConfig, baseMachineIndex);
		unfill(currState, currBit);
		_usedTransitions.remove(_usedTransitions.size()-1);
	}
	
	/** If the state providing halt is boxed out of occurrence by _usedTransitions,
	 * then we might as well do nothing.
	 * Otherwise, we should run the partial machine until one of the following things happens:
	 * (a) We come to another branching point;
	 * (b) tempConfig is atExtreme(), meaning the tape head has reached the end of its allocated array;
	 * (c) more than 107 steps have passed and there is still an unaccessed state;
	 * (d) more than 1000 steps have passed;
	 * (e) the machine repeats a configuration;
	 * (f) it's doing something shift recurrent;
	 * (g) the configuration is empty again (thus the machine is equivalent to another machine);
	 * (h) the machine halts.*/
	void completeFromHereHelper(StepConfiguration workConfig, long baseMachineIndex) throws Exception {
		//Here we should be guaranteed that the partial machine can work on workConfig
		//for at least one step. workConfig has space for 300 symbols,
		//and the tape head started off at the center,
		//so one may reasonably estimate that any time there are at least 107 steps before another branching,
		//it's likely not to have to run off the tape before determining that.
		//Hopefully, most of those times, at most four states were ever accessed,
		//so we can confidently give up at that point.
		//But we'll see.
		while(workConfig.getNumSteps() <= 108) {
			if (known(workConfig)) {
				partialAct(workConfig);
				if (workConfig.getState()==H)
					return;
				/*if (workConfig.isEmpty())
					return; //We may underestimate S this way, but not Sigma*/
				//TODO: Restore above when you have a way of making it not be slow.
				if (workConfig.atExtreme()) {
					throw new IndexOutOfBoundsException("In AllMachines.completeFromHereHelper("+workConfig+"), "
							+ "depth "+_usedTransitions.size()+": "
							+ "index "+workConfig.getIndex()+" is at extreme");
				}
			}
			else {
				long numToBeCoveredByEach = Tools.longPow(20,10-_usedTransitions.size()-1-1);
				for (int j=0; j<20; j++) {
					long nextBaseMachineIndex = baseMachineIndex + j*numToBeCoveredByEach;
					completeFromHere(workConfig, j, nextBaseMachineIndex);
				}
				return;
			}
		}
		//Here we can be confident that 107 steps passed since the beginning,
		//so we scan _usedTransitions for the number of states used.
		//If it's 4 or fewer, we can confidently throw this machine out.
		if (numStatesUsed() <= 4) return;
		
		if (workConfig.getState() == H) return;
		
		//Note that control flow will make it right here on later calls to completeFromHere()/completeFromHereHelper().

		//If the tape head is near the right, try to find out if the machine is shift right recurrent;
		//if the tape head is near the left,  try to find out if the machine is shift left  recurrent.
		
		if (workConfig.nearRight(HOW_FAR_AWAY_ALLOWED)) {
			if (isShiftRecurrent(workConfig, 1)) return;
		}
		
		if (workConfig.nearLeft(HOW_FAR_AWAY_ALLOWED)) {
			if (isShiftRecurrent(workConfig, -1)) return;
		}
		
		//Next, do a sweep recognition algorithm.
		//start by checking for the first step number when it will be on the left—if one exists—
		//and then when it will be on the right after that,
		//and then when it will be on the left  again.
		
		StepConfiguration tempConfig = workConfig.copy();
		
		boolean leftStepFound = false;
		while (!tempConfig.atExtreme() && tempConfig.getNumSteps() < 1000) {
			if (tempConfig.getState() == H) return;
			if (!known(tempConfig)) {
				long numToBeCoveredByEach = Tools.longPow(20,10-_usedTransitions.size()-1-1);
				for (int j=0; j<20; j++) {
					long nextBaseMachineIndex = baseMachineIndex + j*numToBeCoveredByEach;
					completeFromHere(tempConfig, j, nextBaseMachineIndex);
				}
				return;
			}
			partialAct(tempConfig);
			if(tempConfig.onLeft()) {
				leftStepFound = true;
				break;
			}
		}
		int leftStep = -1;
		if (leftStepFound) leftStep = tempConfig.getNumSteps();
		else {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;
		}
		
		boolean rightStepFound = false;
		while (!tempConfig.atExtreme() && tempConfig.getNumSteps() < 1500) {
			if (tempConfig.getState() == H) return;
			if (!known(tempConfig)) {
				long numToBeCoveredByEach = Tools.longPow(20,10-_usedTransitions.size()-1-1);
				for (int j=0; j<20; j++) {
					long nextBaseMachineIndex = baseMachineIndex + j*numToBeCoveredByEach;
					completeFromHere(tempConfig, j, nextBaseMachineIndex);
				}
				return;
			}
			partialAct(tempConfig);
			if(tempConfig.onRight()) {
				rightStepFound = true;
				break;
			}
		}
		int rightStep = -1;
		if (rightStepFound) rightStep = tempConfig.getNumSteps();
		else {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;
		}
		
		boolean leftStep2Found = false;
		while (!tempConfig.atExtreme() && tempConfig.getNumSteps() < 2000) {
			if (tempConfig.getState() == H) return;
			if (!known(tempConfig)) {
				long numToBeCoveredByEach = Tools.longPow(20,10-_usedTransitions.size()-1-1);
				for (int j=0; j<20; j++) {
					long nextBaseMachineIndex = baseMachineIndex + j*numToBeCoveredByEach;
					completeFromHere(tempConfig, j, nextBaseMachineIndex);
				}
				return;
			}
			partialAct(tempConfig);
			if(tempConfig.onLeft()) {
				leftStep2Found = true;
				break;
			}
		}
		int leftStep2 = -1;
		if (leftStep2Found) leftStep2 = tempConfig.getNumSteps();
		else {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;
		}
		
		//Here we can be confident that a left step, a right step, and a second left step were all found.
		//So let's use Acceleration.bestPattern() and Acceleration.guessLemma()
		//between the left and the right step, and between the right and the second left step.
		//In order to do that, we'll need to make a good old Machine;
		//be careful to copy the state diagram rather than passing it in.
		//Skip Machine creation if _usedTransitions.size() < 9.
		
		if (_usedTransitions.size() < 9) {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;			
		}
		
		System.out.print("Now trying Acceleration.bestPattern for machine "+baseMachineIndex+": ");
		outputPartialMachine(false);
		
		Machine m = new Machine(Tools.threeDeepCopy(_states));
		
		int[][] patternArray1 = Acceleration.bestPattern(m, leftStep, rightStep, 30);
		if (patternArray1 == null) {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;			
		}
		
		int[][] patternArray2 = Acceleration.bestPattern(m, rightStep, leftStep2, 30);
		if (patternArray2 == null) {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;			
		}
		
		Lemma lem1 = Acceleration.guessLemma(m, patternArray1);
		if (lem1 == null || !lem1.isProved()) {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;			
		}

		Lemma lem2 = Acceleration.guessLemma(m, patternArray2);
		if (lem2 == null || !lem2.isProved()) {
			System.out.print(baseMachineIndex + " ");
			outputPartialMachine(true);
			tempConfig.printTrim();
			return;			
		}

		if (lem1.isProved() && lem2.isProved()) {
			System.out.println("The Lemmas were proved:");
			System.out.println(lem1);
			System.out.println(lem2);
			System.out.println("Can we use them? Machine, workConfig, tempConfig:");
			outputPartialMachine(false);
			System.out.println(workConfig);
			System.out.println(tempConfig);
			//Try to use lem1 and lem2 to show the machine never halts
			if (isSweep(m, leftStep, rightStep, leftStep2, lem1, lem2)) return;
			//TODO: What happens if not all transitions of m have been filled?
		}
		
		//Other algorithms here
		
		System.out.print(baseMachineIndex + " ");
		outputPartialMachine(true);
		tempConfig.printTrim();
		return;
	}
	
	/**Attempts to prove that m is a sweep machine:
	 * goes from the left to the right and back again, increasing the length of the swath.
	 * This is going to be a hard one to write.
	 * leftStep, rightStep, and leftStep2 are hints for the method to use:
	 * they are steps when the tape head is on the left, then right, then left again, respectively.
	 * Ideally, there should be found a step lem1begin after leftStep when lem1 can be used,
	 * then a few steps later, a step after rightStep when lem2 can be used.
	 * But the onus of isSweep() is to prove that even when the exponent in
	 * the condensedConfiguration constructed at lem1begin is replaced with a variable N,
	 * a number of steps—depending on N—later, we are left with the exact same termfigurationSequence,
	 * with N replaced by N+c for some positive number c.*/
	boolean isSweep(Machine m, int leftStep, int rightStep, int leftStep2, Lemma lem1, Lemma lem2) {
		//We have to determine when is the best moment to use lem1.
		//bestSpot() will tell us where the tape head should be;
		//lem1.getSource().getState() what state m should be in.
		
		Tape t = new Tape(100); //201 bits long, at center.
		StepConfiguration sc = new StepConfiguration(t, 0);
		for (int i=0; i<leftStep; i++) {
			try {
				m.actOnConfig(sc);
			} catch (Exception e) {
				System.out.println("Error in isSweep: "+e.getMessage());
				return false;
			}
		}
		
		Sweep.isSweepUsing(m, lem1, lem2); //New revision
		
		if (!isSweepHelper(m, sc, lem1, lem2, R, false)) return false;
		if (!isSweepHelper(m, sc, lem2, lem1, L, false)) return false;
		//if (!isSweepHelper(m, sc, lem1, lem2, R, false)) return false; //Testing
		//if (!isSweepHelper(m, sc, lem2, lem1, L, false)) return false; //Testing
		if (!isSweepHelper(m, sc, lem1, lem2, R, true )) return false;
		//More code goes here for other types of machines
		return true;
	}
	
	/**Rewrites in terms of a variable N if generalize is true.*/
	boolean isSweepHelper(Machine m, StepConfiguration sc, Lemma lem, Lemma otherLem, int direction, boolean generalize) {
		//TODO: fix for multiple possible entry points.
		int[] pattern = lem.getSource().getBase();
		int bestState = lem.getSource().getState();
		int bestSpot = sc.bestSpot(pattern, direction);
		//TODO: Address the case when lem's source is actually repeating 0s
		System.out.print("1. ");printEverything(sc, bestState, bestSpot, pattern, direction); //For debugging
		//Next, we go at most SWEEPCATCH_DURATION steps checking whether the tape head links up with bestSpot in bestState.
		if (!advanceTo(m, sc, bestState, bestSpot, pattern, direction, SWEEPCATCH_DURATION)) {
			System.out.print("Next, ");
			printEverything(sc, bestState, bestSpot, pattern, direction);
			return false;
		}
		//Now, we can mark the best step number at what sc's at now, accelerate using the lemma,
		//and putz around on the right until it links up with the best spot for going backwards
		int bestStepNumber = sc.getNumSteps();
		int currStepNumber = bestStepNumber; //Will have to update manually from here, because we'll be using cc
		StepConfiguration sc2 = sc;
		CondensedConfiguration cc = sc2.condenseAndSplitUsing(direction, pattern);
		if (generalize) {
			int linCoeff = 1;
			int a = lem.getSource().getBase().length;
			int b = otherLem.getSource().getBase().length;
			int lcm = Tools.lcm(a, b);
			int aBumpUp = lcm / a, bBumpUp = lcm / b;
			if (a > 1 && b > 1) linCoeff = aBumpUp;
			int numTimesWorthTrying = Math.max(aBumpUp, bBumpUp);
			ExtendedTermfiguration etf = cc.generalize(pattern, direction, linCoeff);
			try {
				return generalIsSweepHelper(etf, lem, otherLem, numTimesWorthTrying);
			} catch (Exception e) {
				System.out.println("Error 1 in isSweepHelper: "+e.getMessage());
				return false;
			}
		}
		int numStepsPassed = 0;
		try {
			System.out.println("Debug code: cc = " + cc);
			numStepsPassed = Acceleration.act(cc, new LemmaList(lem));
			currStepNumber += numStepsPassed;
			if (numStepsPassed < 2) throw new Exception("Only " + numStepsPassed + " steps passed");
		} catch (Exception e) {
			System.out.println("Error 2 in isSweepHelper: "+e.getMessage());
			return false;
		}
		try {
			Configuration c2 = cc.toConfiguration();
			sc2 = c2.toStepConfigurationAt(currStepNumber);
		} catch (Exception e) {
			System.out.println("Error 3 in isSweepHelper: "+e.getMessage());
			return false;
		}
		System.out.println("Debug code: in isSweepHelper():"
				+ " StepConfiguration sc2 = " + sc2.getTrimAsString());
		sc.setData(sc2); //To update appropriately!
		return true;
	}
	
	private boolean generalIsSweepHelper(ExtendedTermfiguration etf, Lemma lem0, Lemma lem1, int numTimesWorthTrying)
			throws Exception {
		if (etf == null) {
			System.out.println("null etf. Maybe it didn't generalize.");
			return false;
		}
		ExtendedTermfiguration successorEtf = etf.successor(); //saving to compare for later
		successorEtf.condense(); //Easier to compare
		System.out.println("Goal: successorEtf = "+successorEtf);
		System.out.println("Debug code: etf = " + etf);
		
		System.out.println("More debug code: etf = " + etf);

		int[] stepsPassed1 =  Acceleration.act(etf, lem0);
		
		Lemma lem, otherLem;
		for (int numTimes = 0; numTimes < numTimesWorthTrying * 2; numTimes++) {
			if (numTimes % 2 == 0) {lem = lem0; otherLem = lem1;}
			else {lem = lem1; otherLem = lem0;}
			/**Here we should refactor —change base length as needed—
			 * then we should reanalyze— shift base as needed.
			 * But coeff. of N high enough to be divisible should be present first.*/
			etf = etf.refactored(otherLem.getSource().getBase().length, lem.getHandedness()); //who knows which side's better?
			if (etf == null) return false;
			
			System.out.println("Yet more debug: etf = " + etf);
			
			//Now we continue to act until the tape head runs into the core again.
			//TODO: Consider splitting a copy of the term off, maybe here, maybe later.
			int[] stepsPassed2 = walkAroundWhileOutOfBounds(lem.getMachine(), etf, MAX_WANDERING);
			if (stepsPassed2 == null) return false;
			int[] stepsPassed = Tools.add(stepsPassed1, stepsPassed2);
			//Now we gotta see if what we've landed on is of the correct form for the other lemma.
			//If not, some major reanalysis is due.
			System.out.println("Now etf = "+etf);
			int otherBestState = otherLem.getSource().getState();
			int[] otherBestPattern = otherLem.getSource().getBase();
			
			int side;
			if (etf.onRight()) side = R;
			else if (etf.onLeft()) side = L;
			else throw new Exception("Tape head not at boundary of term!");
			
			if (Tools.areIdentical(etf.getTerm().getBase(), otherBestPattern)) {
				int numIters = 0;
				while (etf.getState() != otherBestState) {
					numIters++;
					if (numIters > 5) break;
					
					//TODO: put code here about "will strike again."
					//      meaning the bit will be left as it is, and the tape head moved off the term,
					//      so we can just act for one step and wait till that happens.
					//OR: Add code to the walking around methods to have them continue overtime
					//    if a given target state isn't reached.
									
					//TODO: for now, we only unfurl at most once;
					//      in the future, consider unfurling more than once
					
					try {
						System.out.println("Debug code: Commencing unfurl for etf = "+etf+" with index "+
					                       Tools.toPolynomialString(etf.getIndex(),'n') +
					                       " and term with index " +
					                       Tools.toPolynomialString(etf.getTerm().getIndex(),'n'));
						boolean didUnfurl =	etf.unfurl(side);
						if (!didUnfurl) etf.split(side);
						System.out.println("Debug code: after "+didUnfurl+", etf = "+etf+" with index "+
								           Tools.toPolynomialString(etf.getIndex(),'n') +
					                       " and term with index " +
					                       Tools.toPolynomialString(etf.getTerm().getIndex(),'n'));
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					
					//if (etf.constCoeff() <= 0) return false;
					
					//Split off a term from the core on etf's current side;
					//run until it reaches the core again.
					
					//if (etf.onRight()) etf.split(R); //This should always happen the first time this method is run
					//else if (etf.onLeft()) etf.split(L);
					
					int[] stepsPassedNow = walkAroundWhileOutOfBounds(lem.getMachine(), etf, MAX_WANDERING);
					stepsPassed = Tools.add(stepsPassed, stepsPassedNow);
					if (etf.nonzeroSwathLengthAt(0) > successorEtf.nonzeroSwathLengthAt(0)) //maybe change to if greater by 2 or more
						successorEtf = successorEtf.successor();
				}
				
				stepsPassed = Acceleration.act(etf, otherLem); //The key moment!
								
				//Now if the opposite wing sizes differ but not by a multiple of the base length,
				//it would be too hard to try to reconcile;
				//but if they differ by a multiple of the base length, we might as well attempt to swallow,
				//returning false if the patterns don't match.
				int etfOppositeWingSize, successorEtfOppositeWingSize, oppositeWingSizeDifference, currSide;
				if (etf.getIndex()[1]==0) {
					etfOppositeWingSize = Tools.trimEnd(etf.getRight()).length;
					successorEtfOppositeWingSize = Tools.trimEnd(successorEtf.getRight()).length;
					currSide = L;
				}	
				else {
					etfOppositeWingSize = Tools.trimBeginning(etf.getLeft()).length;
					successorEtfOppositeWingSize = Tools.trimBeginning(successorEtf.getLeft()).length;
					currSide = R;
				}
				oppositeWingSizeDifference = etfOppositeWingSize - successorEtfOppositeWingSize;
				System.out.println("Here etf = "+etf+" and oppositeWingSizeDifference = " + oppositeWingSizeDifference);
				
				/*Now if etf's base doesn't match lem's source's base,
				 *hopefully that's because it just needs to be shifted over.
				 *Try shifting in the opposite direction of the tape head.*/
				if (etf.getBase().length == lem.getSource().getBase().length &&
						!Arrays.equals(etf.getBase(), lem.getSource().getBase())) {
					if(oppositeWingSizeDifference > 0)
						etf.tryShiftAway(lem.getSource().getBase(), oppositeWingSizeDifference);
					else if (oppositeWingSizeDifference == -1)
						etf.tryShiftToward(lem.getSource().getBase(), 1);	
				}
				
				/*if (oppositeWingSizeDifference % etf.getBase().length != 0)
					return false;*/
				boolean wereSwallowed = true; //Default is it's OK
				if (oppositeWingSizeDifference > 0) {
					int numTermsToSwallow = oppositeWingSizeDifference / etf.getBase().length;
					wereSwallowed = etf.trySwallow(-currSide, numTermsToSwallow);
				}
				else if (oppositeWingSizeDifference < 0) {
					int numTermsToSwallow = -oppositeWingSizeDifference / etf.getBase().length;
					wereSwallowed = successorEtf.trySwallow(-currSide, numTermsToSwallow);
				}
				if (!wereSwallowed)
					return false;
				System.out.println("After 2nd Lem used: etf = " + etf);
								
				int[] stepsPassed3 = walkAroundSwallowingWhileOutOfBounds(lem.getMachine(), etf, MAX_WANDERING,
						successorEtf.getExponent()[0] - etf.getExponent()[0]);
				if (stepsPassed3 == null) return false;
				stepsPassed = Tools.add(stepsPassed, stepsPassed3);
				System.out.println("successorEtf= " + successorEtf);
				System.out.println("compare etf = " + etf);
				if (etf.essentiallyEquals(successorEtf)) {
					System.out.println("Sweep proved!!");
					return true;
				}
				if (etf.nonzeroSwathLengthAt(0) >= successorEtf.nonzeroSwathLengthAt(0))
					successorEtf = successorEtf.successor();
			}
		}
		return false;//change this / add code
	}
	
	/**Returns the array representing T+0n where T is the number of steps it took to get back to the core,
	 * or null if it doesn't get there in maxIterations iterations or the tape head goes out of bounds.*/
	private int[] walkAroundWhileOutOfBounds(Machine m, ExtendedTermfiguration etf, int maxIterations) {
		return walkAroundSwallowingWhileOutOfBounds(m, etf, maxIterations, 0);
	}
	
	/**Just like the above function, but attempts to swallow towards the tape head at most maxNumTermsToSwallow times.
	 * A negative value of maxNumTermsToSwallow will be treated like zero.
	 * Depending on the choice of moment to swallow, this function may have better or worse performance for the matching.*/
	private int[] walkAroundSwallowingWhileOutOfBounds(Machine m, ExtendedTermfiguration etf, int maxIterations,
			int maxNumTermsToSwallow) {
		int side = R;
		if (etf.getIndex()[1]==0) side = L;
		int iterations = 0;
		int numTermsSwallowed = 0;
		try {
			while (etf.preciseOutOfBounds()) {
				if (numTermsSwallowed < maxNumTermsToSwallow)
					etf.trySwallow(side, 1);
				//I've experimented with unlimited swallowing. It's not that great.
				System.out.println("In walkAroundSwallowingWhileOutOfBounds "+Tools.asLR(side)+" "+maxNumTermsToSwallow+": was etf = "+etf);
				iterations += Acceleration.actForOneStep(m, etf);
				System.out.println("In walkAroundSwallowingWhileOutOfBounds "+Tools.asLR(side)+" "+maxNumTermsToSwallow+": now etf = "+etf);
				if (iterations>=maxIterations) {
					System.out.println(maxIterations+" iterations passed.");
					return null;
				}
			}
		} catch (Exception e) {
			System.out.println("In walkAroundSwallowingWhileOutOfBounds(): "+e.getMessage() + " with etf = "+ etf +
					", getIndex() = "+Tools.toPolynomialString(etf.getIndex(),'n')+
					", and term's index = "+Tools.toPolynomialString(etf.getTerm().getIndex(),'n'));
			return null;
		}
		return new int[] {iterations, 0};
	}
	
	/**Good for helping debug*/
	private void printEverything(StepConfiguration sc, int bestState, int bestSpot, int[] pattern, int direction) {
		System.out.println("Debug code: in printEverything():"
				+ " StepConfiguration sc = " + sc.getTrimAsString()
				+ " with index " + sc.getIndex()
				+ " has best spot " + bestSpot
				+ " for pattern = " + Tools.toString(pattern)
				+ " in direction " + Tools.asLR(direction)
				+ " targeting state " + Tools.asLetter(bestState));
	}

	/**Go at most SWEEPCATCH_DURATION steps checking whether the tape head links up with bestSpot in bestState.
	 * This method modifies sc, and returns whether a spot like this was reached.*/
	private boolean advanceTo(Machine m, StepConfiguration sc, int bestState, int bestSpot, int[] pattern, int direction, int numSteps) {
		boolean found = false;
		for (int i=0; i<numSteps; i++) {
			try {
				if (sc.getState()==bestState) {
					System.out.println("Debug code: checking to see if sc at symbol "+sc.getSymbol()+" matches pattern "+Tools.toString(pattern));
					//Insanely, the following clause works better with || than &&. ??
					if (sc.getIndex()==bestSpot || sc.matches(pattern, direction)) {
						System.out.println("Yes it does.");
						found = true;
						break;
					}
				}
				m.actOnConfig(sc);
			} catch (Exception e) {
				System.out.println("In AllMachines.advanceTo(): "+e.getMessage());
				return false;
			}
		}
		return found;
	}

	/**Note: Also returns true if the machine just halts!*/
	boolean isShiftRecurrent(StepConfiguration workConfig, int dir) throws Exception {
		return isShiftRecurrent(workConfig, dir, SHIFT_NUM_STEPS);
	}
	boolean isShiftRecurrent(StepConfiguration workConfig, int dir, int numSteps) throws Exception {
		if (dir!=1 && dir!=-1)
			throw new Exception("In isShiftRecurrent(): dir is "+dir+"!=1 or -1");
		StepConfiguration tempConfig = workConfig.copy();
		//Now we run the machine on tempConfig for numSteps steps,
		//recording the state-bit pairs we see as we go.
		//if the pattern lines up with itself perfectly with any offset from 1 to numSteps/2,
		//then we might be able to prove the machine shift recurrent.
		List<Point> statesAndBits = new ArrayList<Point>();
		for (int i=0; i<numSteps; i++) {
			if (tempConfig.getState() == H) return true; //Might as well; halting is as good as shift recurrent
			statesAndBits.add(new Point(tempConfig.getState(),tempConfig.getSymbol()));
			if (known(tempConfig))
				partialAct(tempConfig);
			else
				return false;
		}
		int bestOffset = 0;
		for (int offset = numSteps/2; offset >= 1; offset--) {
			if (linesUp(statesAndBits, offset))
				bestOffset = offset;
		}
		//If bestOffset is still 0, that means we didn't find a good offset
		if (bestOffset==0) return false;
		//System.out.println("Best offset: "+bestOffset);
		
		//Now we find a step number within the first bestOffset steps when the tape head was innermost from the edge.
		//At that step, we read everything from the tape head to the edge.
		//We verify that the tape head was going in the correct direction on average by bestOffset steps later.
		//At that step, we read everything from the tape head to the edge.
		//If the swaths match, the machine has been proved shift recurrent.
		
		StepConfiguration tempConfig2 = workConfig.copy();
		int innermostIndex = tempConfig2.getIndex();
		int innermostNumSteps = tempConfig2.getNumSteps();
		for (int i=0; i<bestOffset; i++) {
			partialAct(tempConfig2);
			int currIndex = tempConfig2.getIndex();
			int currNumSteps = tempConfig2.getNumSteps();
			if (dir == 1) {
				if (currIndex < innermostIndex) {
					innermostIndex = currIndex;
					innermostNumSteps = currNumSteps;
				}
			}
			else if (dir == -1) {
				if (currIndex > innermostIndex) {
					innermostIndex = currIndex;
					innermostNumSteps = currNumSteps;
				}				
			}
		}
		
		//Now we repeat the process for another bestOffset steps,
		//making sure the answers we get are bestOffset steps apart
		//and the tape head wasn't displaced in the direction opposite the desired direction.
		
		int innermostIndex2 = tempConfig2.getIndex();
		int innermostNumSteps2 = tempConfig2.getNumSteps();
		for (int i=0; i<bestOffset; i++) {
			partialAct(tempConfig2);
			int currIndex = tempConfig2.getIndex();
			int currNumSteps = tempConfig2.getNumSteps();
			if (dir == 1) {
				if (currIndex < innermostIndex2) {
					innermostIndex2 = currIndex;
					innermostNumSteps2 = currNumSteps;
				}
			}
			else if (dir == -1) {
				if (currIndex > innermostIndex2) {
					innermostIndex2 = currIndex;
					innermostNumSteps2 = currNumSteps;
				}				
			}
		}
		
		if (innermostNumSteps2 - innermostNumSteps != bestOffset) return false;
		if (dir == 1  && innermostIndex2 < innermostIndex) return false;
		if (dir == -1 && innermostIndex2 > innermostIndex) return false;
		
		//Now we are guaranteed that the machine doesn't read anything further in from
		//innermostIndex for bestOffset steps. So let's read the tail of the tape
		//at innermostNumSteps and innermostNumSteps2 steps.
		//If the tails match, the machine is shift recurrent.
		
		StepConfiguration tempConfig3 = workConfig.copy();
		while (tempConfig3.getNumSteps() < innermostNumSteps) partialAct(tempConfig3);
		int[] tail1 = tempConfig3.tail(dir);
		while (tempConfig3.getNumSteps() < innermostNumSteps2) partialAct(tempConfig3);
		int[] tail2 = tempConfig3.tail(dir);
		if (Tools.areIdentical(tail1, tail2)) {
			//System.out.println("Success!");
			return true;
		}
		
		return false;
	}
	
	boolean linesUp(List<Point> statesAndBits, int offset) {
		for (int i=0; i<statesAndBits.size()-offset; i++) {
			Point p = statesAndBits.get(i);
			Point q = statesAndBits.get(i+offset);
			if (p.x!=q.x||p.y!=q.y)
				return false;
		}
		return true;
	}
	
	void outputPartialMachine(boolean alsoWriteToFile) throws Exception {
		if (_outputSize >= MAX_OUTPUT_SIZE)
			throw new Exception("Total number of machines exceeded.");
		StringBuffer sb = new StringBuffer("");
		for (int i=0; i<5; i++) {
			for (int j=0; j<2; j++) {
				if (_transitionFilled[i][j])
					sb.append( "" +
							Tools.asLetter(_states[i][NEXT_STATE][j]) +
							_states[i][TO_WRITE][j] +
							Tools.asLR(_states[i][TO_GO][j])
					);
				else
					sb.append("XXX");
				sb.append(' ');
			}
			sb.append(' ');
		}
		sb.append('\n');
		System.out.print(sb);
		if (alsoWriteToFile) {
			try {
			    FileWriter myWriter = new FileWriter("sweepmachines.txt", true);
			    myWriter.write(sb.toString());
			    myWriter.close();
			} catch (Exception e) {
			    System.out.println("Error in AllMachines.outputPartialMachine(): "+e.getMessage());
			}
			_outputSize++;
		}
	}
	
	int numStatesUsed() {
		boolean[] used = new boolean[5];
		Iterator<Point> usedTransitionIterator = _usedTransitions.iterator();
		while (usedTransitionIterator.hasNext()) {
			Point currUsedTransition = usedTransitionIterator.next();
			used[currUsedTransition.x] = true; 
		}
		int numStatesUsed = 0;
		for (int i=0; i<5; i++)
			if (used[i]) numStatesUsed++;
		return numStatesUsed;
	}
	
	/**Returns the state the machine is about to transition to,
	 * or -2 if the current transition is unfilled.*/
	int lookAhead(StepConfiguration workConfig) {
		int currState = workConfig.getState();
		if (currState == H) return H;
		int currBit = workConfig.getSymbol();
		if (!_transitionFilled[currState][currBit]) return -2;
		return _states[currState][NEXT_STATE][currBit];
	}
	
	void partialAct(StepConfiguration workConfig) throws Exception {
		partialAct(workConfig, false);
	}
	
	/**add denotes whether to add currState, currBit to the List.*/
	void partialAct(StepConfiguration workConfig, boolean add) throws Exception {
		partialAct(workConfig, add, false);
	}
	
	void partialAct(StepConfiguration workConfig, boolean add, boolean loud) throws Exception {
		int currState = workConfig.getState();
		int currBit = workConfig.getSymbol();
		if (!_transitionFilled[currState][currBit]) throw new Exception("Transition from state "+currState+", bit "+currBit+" unfilled");
		int nextState = _states[currState][NEXT_STATE][currBit];
		int nextBit   = _states[currState][TO_WRITE  ][currBit];
		int dir       = _states[currState][TO_GO     ][currBit];
		if (loud) {
			System.out.println("Debug code: "
					+ "currState = " + Tools.asLetter(currState) + ", "
					+ "currBit = "   + currBit + ", "
					+ "nextState = " + Tools.asLetter(nextState) + ", "
					+ "nextBit = "   + nextBit + ", "
					+ "dir = "       + Tools.asLR(dir)
			);
		}
		workConfig.replace(nextBit);
		workConfig.go(dir);
		workConfig.setState(nextState);
		if (add) _usedTransitions.add(new Point(currState, currBit));
	}
	
	/**Whether it's determined what would occur to the current StepConfiguration.*/
	boolean known(StepConfiguration workConfig) {
		return _transitionFilled[workConfig.getState()][workConfig.getSymbol()];
	}
	
	static Machine fromMachineIndex(long machineIndex) {
		int[][][] states = new int[5][3][2];
		for (int i=0; i<5; i++) for (int j=0; j<3; j++) for (int k=0; k<2; k++) states[i][j][k] = -2; //Intentionally invalid
		/*	 * The first 12,800,000,000 have C1L, H1L, and either DR, DL, CR, BR, or AR in transitions A0, B0, C0, respectively.
	         * The next   7,680,000,000 have B1L/H1L in A, and CR, CL, or BR in B0.
	         * The next  12,800,000,000 have C1L in A0, H1L in B1, and DR, DL, CR, BL, or AR in C0.
	         * The last   7,680,000,000 have B1L in A0, H1L in B1, and CR, CL, or AR  in B0. 
	         * The gcf is 2,560,000,000.*/
		long q = machineIndex / 2560000000L;
		long r;
		
		long q2, r2, q3, r3; //r3 will indicate the rest of the instructions in every case
		
		if (0 <= q && q < 5) {
			localFill(states, A, 0, C, 1, L);
			localFill(states, B, 0, H, 1, L);
			r = machineIndex;
			q2 = r  / 6400000000L; //1-q2 is the bit to write on seeing C0
			r2 = r  % 6400000000L;
			q3 = r2 / 1280000000L; //q3 indicates the state to leave in after C0
			r3 = r2 % 1280000000L;
			if (q3 == 0)      localFill(states, C, 0, D, 1-(int)q2, R);
			else if (q3 == 1) localFill(states, C, 0, D, 1-(int)q2, L);
			else if (q3 == 2) localFill(states, C, 0, C, 1-(int)q2, R);
			else if (q3 == 3) localFill(states, C, 0, B, 1-(int)q2, R);
			else if (q3 == 4) localFill(states, C, 0, A, 1-(int)q2, R);
		}
		else if (5 <= q && q < 8) {
			localFill(states, A, 0, B, 1, L);
			localFill(states, A, 1, H, 1, L);
			r = machineIndex - 12800000000L;
			q2 = r  / 3840000000L; //1-q2 is the bit to write on seeing C0
			r2 = r  % 3840000000L;
			q3 = r2 / 1248000000L; //q3 indicates the state to leave in after C0
			r3 = r2 % 1248000000L;
			if (q3 == 0)      localFill(states, B, 0, C, 1-(int)q2, R);
			else if (q3 == 1) localFill(states, B, 0, C, 1-(int)q2, L);
			else if (q3 == 2) localFill(states, B, 0, B, 1-(int)q2, R);
		}
		else if (8 <= q && q < 13) {
			localFill(states, A, 0, C, 1, L);
			localFill(states, B, 1, H, 1, L);
			r = machineIndex - 12800000000L - 7680000000L;
			q2 = r  / 6400000000L; //1-q2 is the bit to write on seeing C0
			r2 = r  % 6400000000L;
			q3 = r2 / 1280000000L; //q3 indicates the state to leave in after C0
			r3 = r2 % 1280000000L;
			if (q3 == 0)      localFill(states, C, 0, D, 1-(int)q2, R);
			else if (q3 == 1) localFill(states, C, 0, D, 1-(int)q2, L);
			else if (q3 == 2) localFill(states, C, 0, C, 1-(int)q2, R);
			else if (q3 == 3) localFill(states, C, 0, B, 1-(int)q2, L);
			else if (q3 == 4) localFill(states, C, 0, A, 1-(int)q2, R);
		}
		else if (13 <= q && q < 16) {
			localFill(states, A, 0, B, 1, L);
			localFill(states, B, 1, H, 1, L);
			r = machineIndex - 12800000000L - 7680000000L - 12800000000L;
			q2 = r  / 3840000000L; //1-q2 is the bit to write on seeing C0
			r2 = r  % 3840000000L;
			q3 = r2 / 1248000000L; //q3 indicates the state to leave in after C0
			r3 = r2 % 1248000000L;
			if (q3 == 0)      localFill(states, B, 0, C, 1-(int)q2, R);
			else if (q3 == 1) localFill(states, B, 0, C, 1-(int)q2, L);
			else if (q3 == 2) localFill(states, B, 0, A, 1-(int)q2, R);
		}
		else {
			System.out.println("machineIndex "+machineIndex+" out of range [0,40960000000)");
			return null;
		}
		//the rest of the code goes here
		return null;
	}
	
	/**Just like fill, but for a local variable.*/
	static void localFill(int[][][] states, int currState, int currBit, int nextState, int nextBit, int dir){
		states[currState][TO_WRITE  ][currBit] = nextBit;
		states[currState][TO_GO     ][currBit] = dir;
		states[currState][NEXT_STATE][currBit] = nextState;
	}
}
