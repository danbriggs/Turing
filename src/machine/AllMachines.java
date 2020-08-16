package machine;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**Class for repeating the work Skelet did by 2003.
 * Start by enumerating machines.*/
public class AllMachines {
	static final int A=0, B=1, C=2, D=3, E=4, H=-1;
	static final int L=-1, R=1;
	static final int TO_WRITE=0, TO_GO=1, NEXT_STATE=2;
	static final boolean ADD_TO_STACK = true;
	int[][][] _states;
	boolean[][] _transitionFilled;
	List<Point> _usedTransitions; //To keep track of the order in which they were filled.
	final int MAX_OUTPUT_SIZE = 1000;
	int _outputSize; //To allow the process to break if the number of partial machines output exceeds MAX_OUTPUT_SIZE.
	long _distanceCovered; //How many machines out of the 40,960,000,000 we've passed.
	
	AllMachines() {
		_states = new int[5][3][2];
		_transitionFilled = new boolean[5][2];
		_usedTransitions = new ArrayList<Point>();
		_outputSize = 0;
		_distanceCovered = 0;
	}
	
	/**Makes each machine in the list of all machines.
	 * The first 12,800,000,000 have C1L, H1L, and either DL, AR, BR, CR, or DR
	 * in transitions A0, B0, C0, respectively.
	 * The next  7,680,000,000 have B1L/H1L in A, and BR, CL, or CR in B0.
	 * The next  7,680,000,000 have B1L in A0, H1L in B1, and AR, CL, or CR in B0.
	 * The last 12,800,000,000 have C1L in A0, H1L in B1, and AR, BL, CR, DL, or DR in C0.
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
		StepConfiguration workConfig = new StepConfiguration(new int[300], 150);
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
		
		//If the tape head is near the right, try to find out if the machine is shift right recurrent;
		//if the tape head is near the left,  try to find out if the machine is shift left  recurrent.
		
		int howFarAwayAllowed = 3;
		if (workConfig.nearRight(howFarAwayAllowed)) {
			if (isShiftRecurrent(workConfig, 1)) return;
		}
		
		if (workConfig.nearLeft(howFarAwayAllowed)) {
			if (isShiftRecurrent(workConfig, -1)) return;
		}
		
		//Find out how much padding may be necessary to run for 500 more steps,
		//pad it by twice as much as necessary if any is necessary,
		//run for up to 500 steps, watching for halt, branch, loop, and shift recurrent.
		//Note that control flow will make it right here on later calls to completeFromHere()/completeFromHereHelper().
		
		
		System.out.print(baseMachineIndex + " ");
		outputPartialMachine();
		workConfig.printTrim();
	}
	
	boolean isShiftRecurrent(StepConfiguration workConfig, int dir) throws Exception {
		return isShiftRecurrent(workConfig, dir, 30);
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
		//and the tape head was displaced in the desired direction.
		
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
	
	void outputPartialMachine() throws Exception {
		if (_outputSize >= MAX_OUTPUT_SIZE)
			throw new Exception("Total number of machines exceeded.");
		for (int i=0; i<5; i++) {
			for (int j=0; j<2; j++) {
				if (_transitionFilled[i][j])
					System.out.print( "" +
							Tools.asLetter(_states[i][NEXT_STATE][j]) +
							_states[i][TO_WRITE][j] +
							Tools.asLR(_states[i][TO_GO][j])
					);
				else
					System.out.print("XXX");
				System.out.print(" ");
			}
			System.out.print(" ");
		}
		System.out.println();
		_outputSize++;
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
	
}
