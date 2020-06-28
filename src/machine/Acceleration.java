package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import graph.DirectedCycle;
import graph.Digraph;
import graph.StdOut;

public class Acceleration {
	//This class is supposed to have the methods for making a machine act on a CondensedConfiguration using a LemmaList.
	public static int act(CondensedConfiguration cc, LemmaList lemlist) throws Exception {
		int[] termNumAndIndex = cc.termNumAndIndex();
		int termNum = termNumAndIndex[0];
		int indexInTerm = termNumAndIndex[1];
		if (termNum==-1) throw new Exception("index "+indexInTerm+" out of bounds for CondensedConfiguration "+cc);
		Term currTerm = cc.getTermList().get(termNum);
		int side=0;//-1 for leftmost bit of term, 1 for rightmost bit, 0 for in between
		if (indexInTerm==0) side=-1;
		if (indexInTerm==currTerm.length()-1) side=1;
		if (side!=0) {
			//Here is where we may have occasion to use a Lemma
			Integer[] pair = lemlist.firstMatchAndValue(currTerm,side,cc.getState());
			if (pair[0]!=-1) {
				int matchIndex = pair[0];
				int nToUse = pair[1];
				Lemma lemmaToUse = lemlist.getLemList().get(matchIndex);
				Termfiguration source = lemmaToUse.getSource();
				Termfiguration target = lemmaToUse.getTarget();
				/*System.out.println("We got one! "+matchIndex+"\n"+
				lemmaToUse+
				"\nworks with n="+nToUse+
				"\nto be "+currTerm);*/ //this is debug code
				cc.replace(termNum, target.toTermAt(nToUse));
				//System.out.println("source.getIndex(): "+Tools.toPolynomialString(source.getIndex(),'n'));
				//System.out.println("target.getIndex(): "+Tools.toPolynomialString(target.getIndex(),'n'));
				int indexAtSource = Tools.evalAt(source.getIndex(),nToUse);
				int indexAtTarget = Tools.evalAt(target.getIndex(),nToUse);
				int toMoveBy = indexAtTarget-indexAtSource;
				//System.out.println("toMoveBy: "+toMoveBy);
				cc.setIndex(cc.getIndex()+toMoveBy);
				cc.setState(target.getState());
				int numSteps = Tools.evalAt(lemmaToUse.getNumSteps(),nToUse);
				System.out.println("numSteps: "+numSteps);
				return numSteps;
			}
		}
		else {
			//Just act for one step
		}
		return 0;
	}
	
	/**Attempts to look for a simple state-loop that m can enter while progressing to the left,
	and a simple state-loop that m can enter while progressing to the right.
	Returns a LemmaList of length 0, 1, or 2 accordingly.*/
	public static LemmaList lookForTwoLoops(Machine m) throws Exception {
		Transition[] t = m.getTransitions();
		int num_states = t.length;
		//First we choose a direction that m goes in in at least half of its transition-halves.
		
		//int lefts = 0;
		//int rights = 0;
		//boolean chooseRight = false;
		
		int num_symbols = t[0].NUM_SYMBOLS;
		//boolean toBreak = false;
		//We assume there is only one transition to Halt
		//int goodHalf = num_states*num_symbols/2;
		Digraph leftGraph = new Digraph(num_states);
		Digraph rightGraph = new Digraph(num_states);
		for (int i=0; i<num_states; i++) {
			for (int b=0; b<num_symbols; b++) {
				int way = t[i].getToGo(b);
				int nextState = t[i].getNextState(b);
				if (way == -1) {
					if (nextState>=0) {
						//lefts++;
						leftGraph.addEdge(i,nextState);
					}
				}
				else if (way == 1) {
					if (nextState>=0) {
						//rights++;
						rightGraph.addEdge(i,nextState);
					}
				}
				else throw new Exception("In lookForTwoLoops("+m+"): Transition "+i+","+b+" is "+way+" not equal to 1 or -1");
				//if (lefts >= goodHalf) {
					//toBreak = true;
					//break;
				//}
				/*else*/ //if (rights >= goodHalf) {
					//chooseRight = true;
					//toBreak = true;
					//break;
				//}
			}
			//if (toBreak) break;
		}
		//Either left or right is guaranteed to have been seen to account for a good half of the non-halting directions in the Transitions,
		//assuming there is only one Halt transition.
		List<Lemma> lemList = new ArrayList<Lemma>();
		System.out.println("Left graph: "+leftGraph);//Debug code
        DirectedCycle leftFinder = new DirectedCycle(leftGraph);
        if (leftFinder.hasCycle()) {
        	StdOut.println("Left cycle:");
        	Lemma leftLemma = cycleHelper(m, leftFinder, -1);
        	lemList.add(leftLemma);
        }
        else {
            StdOut.println("Left graph is acyclic");
        }
        DirectedCycle rightFinder = new DirectedCycle(rightGraph);
        if (rightFinder.hasCycle()) {
        	StdOut.println("Right cycle:");
            Lemma rightLemma = cycleHelper(m, rightFinder, 1);
            lemList.add(rightLemma);
        }
        else {
            StdOut.println("Right graph is acyclic");
        }
		return new LemmaList(lemList);
	}
	
	private static Lemma cycleHelper(Machine m, DirectedCycle c, int direction) throws Exception {
		Transition[] t = m.getTransitions();
		int num_symbols = t[0].NUM_SYMBOLS;
    	List<Integer> stateList = new ArrayList<Integer>();
    	List<Integer> bitsRead = new ArrayList<Integer>();
    	List<Integer> bitsWritten = new ArrayList<Integer>();
        for (int v : c.cycle()) {
            StdOut.print(v + " ");
            stateList.add(v);
        }
        StdOut.println();
        //Now that we know the state list,
        //we should recover what bits created the cycle on being read
        //and what bits were left in their place.
        for (int i=0; i<stateList.size()-1; i++) {
        	int curr_state = stateList.get(i);
        	int next_state = stateList.get(i+1);
        	int bit_read = -1; //if still -1 after we're done, there's an error
        	int bit_written = -1;
        	for (int b=0; b<num_symbols; b++) {
        		if (t[curr_state].getNextState(b)==next_state && 
        				t[curr_state].getToGo(b)==direction) {
        			bit_read = b;
        			bit_written = t[curr_state].getToWrite(b);
        			//Note: this will choose the highest bit if more than one work
        		}
        	}
        	if (bit_read==-1) throw new Exception("Could not find bit to have been read");
        	bitsRead.add(bit_read);
        	bitsWritten.add(bit_written);
        }
        System.out.println("Bits read: "+bitsRead);
        if (direction==-1) {
        	Collections.reverse(bitsRead);
        	System.out.println("Left to right: "+bitsRead);
        }
        System.out.println("Bits written: "+bitsWritten);
        if (direction==-1) {
        	Collections.reverse(bitsWritten);
        	System.out.println("Left to right: "+bitsWritten);
        }
        int[] bitsReadAsArray = bitsRead.stream().mapToInt(i->i).toArray();
        int[] bitsWrittenAsArray = bitsWritten.stream().mapToInt(i->i).toArray();
        int[] zeroOneArray = {0,1};
        int[] leftArray = {0};
        int[] offLeftArray = {-1};
        int[] rightArray = {-1,bitsRead.size()};
        int[] offRightArray = {0,bitsRead.size()};
        int[] startIndexArray = {0};
        if (direction==1) startIndexArray = leftArray;
        else if (direction==-1) startIndexArray = rightArray;
        int[] endIndexArray = {0,bitsRead.size()};
        if (direction==1) endIndexArray = offRightArray;
        else if (direction==-1) endIndexArray = offLeftArray;
        Termfiguration a = new Termfiguration(bitsReadAsArray, zeroOneArray, startIndexArray, stateList.get(0));
        Termfiguration b = new Termfiguration(bitsWrittenAsArray, zeroOneArray, endIndexArray, stateList.get(0));
        int[] numSteps = {0,bitsRead.size()};
        Lemma lem = new Lemma(m, a, b, numSteps);
        return lem;
	}
	
	/**Returns an array of length 2 consisting of
	* • the start step of the longest run of m in direction dir_desired
	*   on a blank tape that starts at at least lower_bound steps
	*   and finishes by upper_bound steps (either by ending or by upper_bound being reached), and
	* • how long that run is.
	* Use dir_desired=-1 for longest left run, dir_desired=1 for longest right run, dir_desired=0 for longest unidirectional run.*/
	public static int[] longestRun(Machine m, int lower_bound, int upper_bound, int dir_desired) {
		m.reset();
		try {
			if (lower_bound < 0 || upper_bound < lower_bound)
				throw new Exception("Invalid bounds "+lower_bound+", "+upper_bound+" for longestRun");
			int index = upper_bound;
			int direction = 0;
			int oldDirection = 0;
			int runLength = 0;
			int maxRunLength = 0;
			int runStart = 0;
			int maxRunStart = 0;
			int oldIndex;
			Tape t = new Tape(new int[2*upper_bound+1], index);
			int i;
			for (i=0; i<lower_bound; i++)
				m.act(t);
			index = t.getIndex();
			for (i=lower_bound; i<upper_bound; i++) {
				oldIndex = index;
				if (i>lower_bound) oldDirection = direction;
				m.act(t);
				index = t.getIndex();
				direction = index - oldIndex;
				if (direction != 1 && direction != -1) throw new Exception("Tape jumped by " + direction);
				if (i==lower_bound || oldDirection == direction) {
					runLength++;
					if (runLength > maxRunLength) {
						if (direction==dir_desired || dir_desired==0) {
							maxRunLength = runLength;
							maxRunStart = runStart;
						}
					}
				}
				else {
					runStart = i;
					runLength = 0;
				}
			}
			return new int[] {maxRunStart, maxRunLength};
		} catch (Exception e) {e.printStackTrace(); return new int[] {0,0};}
	}
	
	/**Meant to be called on a Machine m that goes in one direction from step lower_bound to step upper_bound.
	 * Finds the lowest positive number n <= 10 such that m has a run of length
	 * a multiple of n that is at least half of (upper_bound - lower_bound)
	 * in which state & bit repeat perfectly every n steps.
	 * Returns an array of triples: state, bit, direction.
	 * Returns null if no such pattern is found. * */
	public static int[][] runPattern(Machine m, int lower_bound, int upper_bound) {
		m.reset();
		int swath = upper_bound-lower_bound;
		int[] theStates = new int[swath];
		int[] theBits = new int[swath];
		int[] theDirections = new int[swath];
		Tape t = new Tape(upper_bound);
		try {
			for (int i=0; i<lower_bound; i++) m.act(t);
			for (int i=0; i<swath; i++) {
				theStates[i] = m.getState();
				theBits[i] = t.getSymbol();
				theDirections[i] = m.getTransitions()[theStates[i]].getToGo(theBits[i]);
				int theWay = theDirections[0];
				if (theDirections[i]!=theWay) {
					System.out.println("Error in runPattern: Machine not in direction "+theWay
							+ "at step "+(lower_bound+i)+" in range ["+lower_bound+", "+upper_bound+"]");
					return null;
				}
				m.act(t);
			}
			for (int skip = 10; skip >= 1; skip--) {
				System.out.print("Calculating the number of perfect matches with skip "+skip+": ");
				int numMatches = 0;
				for (int i=0; i<swath-skip; i++) {
					if (theStates[i]==theStates[i+skip]&&theBits[i]==theBits[i+skip]&&theDirections[i]==theDirections[i+skip])
						numMatches++;
				}
				System.out.println("there are "+numMatches+" matches.");
			}
		}
		catch(Exception e) {System.out.println("Error in runPattern: "+e.getMessage()); return null;}
		return null;
	}

	/**Finds for each n less than maxPatternLength
	 * the longest subsequence from lower_bound to upper_bound steps
	 * in which state & bit repeat perfectly every n steps.
	 * Returns an array of arrays of length 3
	 * which have at index n >= 1:
	 * • max number of repetitions,
	 * • last step at which the situation is still identical n steps later,
	 * • tape head displacement.* */
	public static int[][] bestPattern(Machine m, int lower_bound, int upper_bound, int maxPatternLength) {
		m.reset();
		int swath = upper_bound-lower_bound;
		int[] theStates = new int[swath];
		int[] theBits = new int[swath];	
		int[] theTapeHead = new int[swath];
		Tape t = new Tape(upper_bound);
		try {
			for (int i=0; i<lower_bound; i++) m.act(t);
			for (int i=0; i<swath; i++) {
				theStates[i] = m.getState();
				theBits[i] = t.getSymbol();
				theTapeHead[i] = t.getIndex();
				m.act(t);
			}
			int[][] results = new int[maxPatternLength][3];
			//will have at each index other than 0
			//an array consisting of bestNumMatches and at.
			for (int skip = 1; skip < maxPatternLength; skip++) {
				System.out.print("Finding the best run of perfect matches with skip "+skip+": ");
				int numMatches = 0;
				int bestNumMatches = 0;
				int at = -1;
				for (int i=0; i<swath-skip; i++) {
					if (theStates[i]==theStates[i+skip]&&theBits[i]==theBits[i+skip]) {
						numMatches++;
						if (numMatches>bestNumMatches) {
							bestNumMatches = numMatches;
							at = lower_bound+i;
						}
					}
					else numMatches = 0;
				}
				results[skip][0] = bestNumMatches;
				results[skip][1] = at;
				if (at>=bestNumMatches) results[skip][2] = theTapeHead[at]-theTapeHead[at-bestNumMatches];
				System.out.println("there are "+bestNumMatches+" matches at " + at);
			}
			System.out.println(Tools.matrixToString(results, true));
			return results;
		}
		catch(Exception e) {System.out.println("Error in bestPattern: "+e.getMessage()); return null;}
	}
	/**Attempts to formulate and prove a Lemma about the action of m based on the patternArray passed in.
	 * See the return value of bestPattern() for the format and contents that patternArray should have.*/
	public static Lemma guessLemma(Machine m, int[][] patternArray) {
		int bestSkip = 0;
		int bestSwath = 0;
		for (int i=1; i<patternArray.length; i++) {
			int currSwath = patternArray[i][0];
			if (currSwath > bestSwath) {
				bestSkip = i;
				bestSwath = currSwath;
			}
		}
		int endStep = patternArray[bestSkip][1];
		int displacement = patternArray[bestSkip][2];
		System.out.println("The best skip was "+bestSkip+" with "+bestSwath+" repetitions ending at step "+endStep+" after a displacement of "+displacement+".");
		float approxTermLength = (float)displacement/bestSwath*bestSkip;
		int termLength = Math.round(approxTermLength);
		System.out.println("The signed term length seems to be "+approxTermLength+", so I'm going to guess it's "+termLength+".");
		StretchTape t1 = new StretchTape(endStep);
		int startStep = endStep - bestSwath + 1;
		m.reset();
		for (int i=0; i<startStep; i++) {
			try {m.act(t1);}
			catch (Exception e) {System.out.println("Error 1 in guessLemma()."); return null;}
		}
		System.out.println("Tape at start step, then at end step: ");
		System.out.println(startStep+" "+Tools.asLetter(m.getState())+" "+t1);
		int minIndex = t1.getIndex();
		int maxIndex = t1.getIndex();
		TapeLike t2 = new StretchTape(t1);
		Machine mm = new Machine(m); //For remembering what state m was in
		int[] numVisits = new int[t2.getTape().length];
		//We'll keep track of how many times a given bit was visited.
		//This could clearly be done with less memory, but I don't want to generate indexing errors.
		//A given index will be considered useful to start with only if it and its equivalent skip steps ahead are visited exactly once.
		//Later, I'll incorporate preamble/epilogue bit sequences in order to be able to generate lemmas when this condition cannot be met.
		for (int i=startStep; i<endStep; i++) {
			numVisits[t2.getIndex()]++;
			try {m.act(t2);}
			catch (Exception e) {System.out.println("Error 2 in guessLemma()."); return null;}
			int index = t2.getIndex();
			if (index > maxIndex) maxIndex = index;
			if (index < minIndex) minIndex = index;
		}
		System.out.println(endStep+" "+Tools.asLetter(m.getState())+" "+t2);
		//Now, we need to be careful to write the Lemma in such a way that
		//the tape head does not exceed the bounds of the Term
		//as it goes from one end to the other.
		//
		//For leftward displacements, we should compare the last |termLength| bits of t1 and t2, ending at maxIndex;
		//for rightward displacements, we should compare the first |termLength| bits of t1 and t2, beginning at minIndex.
		//for both, we use the state of the machine when it got there and the state it was in after skip steps.
		//That's how we write the Lemma.
		int targetIndex = minIndex;
		if (displacement < 0) targetIndex = maxIndex;
		int beginState = -2; //so that errors can be thrown if beginState is never set
		Termfiguration a = null;
		Termfiguration b = null;
		boolean foundIndex = false;
		boolean foundSingletons = false;
		//Whether it found two tape head positions that only occur once each, spaced termLength away from each other
		for (int i=startStep; i<endStep; i++) {
			if (t1.getIndex()==targetIndex) foundIndex = true;
			if (foundIndex && numVisits[t1.getIndex()]==1 && numVisits[t1.getIndex()+termLength]==1){
				targetIndex = t1.getIndex();
				beginState = mm.getState();
				int[] bitSeq = null;
				if (displacement > 0) bitSeq = Arrays.copyOfRange(t1.getTape(), targetIndex, targetIndex+termLength);
				if (displacement < 0) bitSeq = Arrays.copyOfRange(t1.getTape(), targetIndex+termLength+1, targetIndex+1);
				//Remember termLength is signed, with same sign as displacement
				int[] beginLeftIndexArr = {0, 0};
				int[] beginRightIndexArr =  {-1, Math.abs(termLength)};
				int[] beginIndexArr = beginLeftIndexArr;
				if (displacement < 0) beginIndexArr = beginRightIndexArr;
				a = new Termfiguration(bitSeq, new int[] {0,1}, beginIndexArr, beginState);
				System.out.println("Begin Termfiguration as string:");
				System.out.println(a);
				foundSingletons = true;
				break;
			}
			try {mm.act(t1);}
			catch (Exception e) {System.out.println("Error 3 in guessLemma()."); return null;}
		}
		if (!foundIndex) {System.out.println("Could not find index in guessLemma()"); return null;}
		if (!foundSingletons) {
			System.out.println("Could not find two positions with a displacement of "+termLength+" visited only once in the swath");
			return null;}
		for (int i = 0; i < bestSkip; i++) {
			try {mm.act(t1);}
			catch (Exception e) {System.out.println("Error 4 in guessLemma()."); return null;}
		}
		//Now we should verify that the tape head did indeed move by the signed termLength in that number of steps.
		if (targetIndex+termLength!=t1.getIndex()) {
			System.out.println("In guessLemma(), the tape head did not move the expected number of steps:");
			System.out.println("targetIndex = "+targetIndex+", termLength = "+termLength+", t1.getIndex() = "+t1.getIndex());
			return null;
		}
		int endState = mm.getState();
		int[] bitSeq2 = null;
		if (displacement > 0) bitSeq2 = Arrays.copyOfRange(t1.getTape(), targetIndex, targetIndex+termLength);
		if (displacement < 0) bitSeq2 = Arrays.copyOfRange(t1.getTape(), targetIndex+termLength+1, targetIndex+1);
		//Remember termLength is signed, with same sign as displacement
		int[] endForwardIndexArr = {0, termLength};
		int[] endBackwardIndexArr =  {-1, 0};
		int[] endIndexArr = endForwardIndexArr;
		if (displacement < 0) endIndexArr = endBackwardIndexArr;
		b = new Termfiguration(bitSeq2, new int[] {0,1}, endIndexArr, endState);
		System.out.println("End Termfiguration as string:");
		System.out.println(b);
		Lemma lem = null;
		try {lem = new Lemma(m, a, b, new int[] {0, bestSkip});}
		catch (Exception e) {System.out.println("In guessLemma(), error initializing Lemma: "+e.getMessage());}
		return lem;
	}
}
