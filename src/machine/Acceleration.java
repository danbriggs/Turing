package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import graph.DirectedCycle;
import graph.Digraph;
import graph.StdOut;

public class Acceleration {
	static final boolean FIVE_LINE = false;
	static final double VOLUME = .4; // set to .6 to get many more print statements
	// This class is supposed to have the methods for making a machine act on a
	// CondensedConfiguration using a LemmaList.

	/**
	 * Returns the number of steps done by acting using lemlist. Should be 1
	 * whenever none of the Lemmas in lemlist were applicable.
	 */
	public static int act(CondensedConfiguration cc, LemmaList lemlist) throws Exception {
		int[] termNumAndIndex = cc.termNumAndIndex();
		int termNum = termNumAndIndex[0];
		int indexInTerm = termNumAndIndex[1];
		if (termNum == -1)
			throw new Exception("index " + indexInTerm + " out of bounds for CondensedConfiguration " + cc);
		Term currTerm = cc.getTermList().get(termNum);
		int side = 0;// -1 for leftmost bit of term, 1 for rightmost bit, 0 for in between
		if (indexInTerm == 0)
			side = -1;
		if (indexInTerm == currTerm.length() - 1)
			side = 1;
		if (side != 0) {
			// Here is where we may have occasion to use a Lemma
			Integer[] pair = lemlist.firstMatchAndValue(currTerm, side, cc.getState());
			if (pair[0] != -1) {
				int matchIndex = pair[0];
				int nToUse = pair[1];
				Lemma lemmaToUse = lemlist.getLemList().get(matchIndex);
				TermfigurationLike source = lemmaToUse.getSource();
				TermfigurationLike target = lemmaToUse.getTarget();
				/*
				 * System.out.println("We got one! "+matchIndex+"\n"+ lemmaToUse+
				 * "\nworks with n="+nToUse+ "\nto be "+currTerm);
				 */ // this is debug code
				cc.replace(termNum, target.toTermAt(nToUse));
				// TODO: make this work for Lemmas on TermfigurationSequences
				// System.out.println("source.getIndex():
				// "+Tools.toPolynomialString(source.getIndex(),'n'));
				// System.out.println("target.getIndex():
				// "+Tools.toPolynomialString(target.getIndex(),'n'));
				int indexAtSource = Tools.evalAt(source.getIndex(), nToUse);
				int indexAtTarget = Tools.evalAt(target.getIndex(), nToUse);
				int toMoveBy = indexAtTarget - indexAtSource;
				// System.out.println("toMoveBy: "+toMoveBy);
				cc.setIndex(cc.getIndex() + toMoveBy);
				cc.setState(target.getState());
				int numSteps = Tools.evalAt(lemmaToUse.getNumSteps(), nToUse);
				Tools.printIfAtLeast(VOLUME, .5, "numSteps: " + numSteps);
				return numSteps;
			}
		}
		// Just act for one step
		return actForOneStep(lemlist.getMachine(), cc, termNum, indexInTerm);
	}

	/**
	 * Have m act on cc for one step. Returns 1 if successful, 0 otherwise.
	 */
	public static int actForOneStep(Machine m, CondensedConfiguration cc, int termNum, int indexInTerm) {
		// int bit = cc.getBit(termNum, indexInTerm);
		// if (bit < 0) System.out.println("getBit() failed");
		int exp = cc.getExponent(termNum);
		// If the exponent is greater than 1, then we have to split the term
		int[] retval;
		if (exp > 1) {
			retval = cc.split(termNum, indexInTerm);
			if (retval == null || retval.length != 2) {
				Tools.printIfAtLeast(VOLUME, .3, "actForOneStep() didn't work");
				return 0;
			}
			termNum = retval[0];
			indexInTerm = retval[1];
		}
		// Now the exponent is guarantted to be 1, so we can just act on the term
		// directly
		m.setState(cc.getState());
		int toGo = 0;
		try {
			toGo = m.actOnTerm(cc.getTermList().get(termNum), indexInTerm);
		} catch (Exception e) {
			Tools.printIfAtLeast(VOLUME, .1, "Error in actOnTerm(): " + e.getMessage());
		}
		cc.setState(m.getState()); // sorry
		cc.setIndex(cc.getIndex() + toGo);
		return toGo * toGo; // The number of steps it acted for.
	}

	/**
	 * Aiming for something rather simpler than Act(cc,lemlist). Acts only if it's
	 * in perfect position to act on the core, or if it's in the wings. Otherwise
	 * throws an exception. Returns a polynomial consisting of the number of steps
	 * acted for.
	 */
	public static int[] act(ExtendedTermfiguration etf, Lemma lem) throws Exception {
		if (etf.preciseOutOfBounds()) {
			actForOneStep(lem.getMachine(), etf);
			return new int[] { 1, 0 };
		}
		if (!lem.isProved())
			throw new Exception(
					"What are you doing, trying to act on an ExtendedTermfiguration with an unproved Lemma");
		if (lem.getSource().getExponent().length > 2)
			throw new Exception("Haven't written code for nonlinear lem sources yet");
		if (lem.getTarget().getExponent().length > 2)
			throw new Exception("Haven't written code for nonlinear lem targets yet");
		if (etf.getTerm().getExponent().length > 2)
			throw new Exception("Haven't written code for nonlinear ExtendedTermfigurations' Terms yet");
		// For now, we'll just make it work in the case where lem's source's baselen
		// equals etf's Term's baselen.
		int baselen = etf.getTerm().getBase().length;
		if (lem.getSource().getBase().length != baselen) {
			// Here we split the term that shows up on the tape if it's a mere repetition of
			// the lemma source's term.
			if (false)
				;// TODO: Haven't written the code for that yet.
			// Thinking of reanalyzing bases upon production instead, to make them as short
			// as possible.
			else
				throw new Exception(
						"We've only made it work in the case where etf's Term's baselen equals lem's source's baselen.");
		}
		if (lem.getSource().getState() != etf.getState())
			throw new Exception("State mismatch");
		// Warning: handedness in Lemmas is opposite of direction in AllMachines!
		// First, we have to make sure that lem is applicable to etf for all values of
		// etf's Term's variable >= 0.
		Tools.printIfAtLeast(VOLUME, .7, "Another line of debug code: etf.getIndex() = " + Tools.toString(etf.getIndex()));
		// Remember that a Termfiguration's index may be negative,
		// but the corresponding ExtendedTermfiguration's index will have the length of
		// the left wing added.
		if (etf.onLeft() && lem.getHandedness() == -1 || etf.onRight() && lem.getHandedness() == 1) {
			/*
			 * System.out.println("Debug code in Acceleration.act(etf,lem): " + "etf = " +
			 * etf + "\n" + "lem.getSource() = " + lem.getSource() + "\n" +
			 * "lem.getMinN() = " + lem.getMinN());
			 */
			// Make sure that the etf with N=0 is valid for lem's minN.
			if (etf.getExponent()[0] < lem.getMinN())
				throw new Exception("Exponent " + Tools.toPolynomialString(etf.getExponent(), 'n')
						+ " too small with n=0 for Lemma with source " + lem.getSource());
			if (!Tools.equal(lem.getTarget().getExponent(), lem.getSource().getExponent()))
				throw new Exception("We've only made it work when lem's source and target have the same exponent");
			if (lem.getTarget().getBase().length != lem.getSource().getBase().length)
				throw new Exception(
						"We've only made it work when lem's source's baselen equals lem's target's baselen");
			if (!Tools.areIdentical(lem.getSource().getBase(), etf.getBase()))
				throw new Exception("We've only made it work when lem's source and etf have the same base");
			if (lem.getHandedness() == -1 && !lem.getTarget().offRight())
				throw new Exception("We've only made it work when lem's target is off the right");
			if (lem.getHandedness() == 1 && !lem.getTarget().offLeft())
				throw new Exception("We've only made it work when lem's target is off the left");
			if (!Tools.equal(lem.getSource().getExponent(), new int[] { 0, 1 }))
				throw new Exception("We've only made it work when lem's source's exponent is n");
			/*
			 * if (etf.getExponent()[1]!=1) throw new Exception("Using "+lem+" on "
			 * +etf+": We've only made it work when etf's exponent's linear coefficient is 1"
			 * ); //Consider restoring this when you make it work for source & target with
			 * different exponents!
			 */

			etf.getTerm().setBase(lem.getTarget().getBase());
			etf.setState(lem.getTarget().getState());
			if (lem.getHandedness() == -1)
				etf.getTerm().setIndexOffRight();
			if (lem.getHandedness() == 1)
				etf.getTerm().setIndexOffLeft();
			int[] lemNumSteps = lem.getNumSteps();
			int[] ret = new int[] { lemNumSteps[0] + lemNumSteps[1] * etf.getExponent()[0],
					lemNumSteps[1] * etf.getExponent()[1] };
			// added *etf.getExponent()[1], 9/11/20. Also switched place with the line
			// below. Important!
			etf.getTerm().condense();
			return ret;
		}
		return new int[] { 0, 0 };
	}

	/**
	 * It's expected that the calling function's checked that the bit to adapt is
	 * precise. To discover where to act, we can just set n=0.
	 */
	public static int actForOneStep(Machine m, ExtendedTermfiguration etf) throws Exception {
		// Thus we can use the sign of the constant coefficient of etf's index to
		// determine which side of the Term the tape head's on.
		int[] index = etf.getTerm().getIndex();
		int b = index[0];
		int[] array = null;
		int pos = 0;
		if (b < 0) {
			array = etf.getLeft();
			pos = array.length + b;
			m.actOnSide(etf, -1, pos);
			return 1;
		}
		Termfiguration t = etf.getTerm();
		int d = t.getBase().length * t.getExponent()[0];
		if (b >= d) {
			array = etf.getRight();
			pos = b - d;
			m.actOnSide(etf, 1, pos);
			return 1;
		}
		if (b == 0) {
			m.tryActOnFirstBit(t);
			return 1;
		}
		if (b == d - 1) {
			m.tryActOnLastBit(t);
			return 1;
		}
		return 0;
	}

	/**
	 * Attempts to look for a simple state-loop that m can enter while progressing
	 * to the left, and a simple state-loop that m can enter while progressing to
	 * the right. Returns a LemmaList of length 0, 1, or 2 accordingly.
	 */
	public static LemmaList lookForTwoLoops(Machine m) throws Exception {
		Transition[] t = m.getTransitions();
		int num_states = t.length;
		// First we choose a direction that m goes in in at least half of its
		// transition-halves.

		// int lefts = 0;
		// int rights = 0;
		// boolean chooseRight = false;

		int num_symbols = t[0].NUM_SYMBOLS;
		// boolean toBreak = false;
		// We assume there is only one transition to Halt
		// int goodHalf = num_states*num_symbols/2;
		Digraph leftGraph = new Digraph(num_states);
		Digraph rightGraph = new Digraph(num_states);
		for (int i = 0; i < num_states; i++) {
			for (int b = 0; b < num_symbols; b++) {
				int way = t[i].getToGo(b);
				int nextState = t[i].getNextState(b);
				if (way == -1) {
					if (nextState >= 0) {
						// lefts++;
						leftGraph.addEdge(i, nextState);
					}
				} else if (way == 1) {
					if (nextState >= 0) {
						// rights++;
						rightGraph.addEdge(i, nextState);
					}
				} else
					throw new Exception("In lookForTwoLoops(" + m + "): Transition " + i + "," + b + " is " + way
							+ " not equal to 1 or -1");
				// if (lefts >= goodHalf) {
				// toBreak = true;
				// break;
				// }
				/* else */ // if (rights >= goodHalf) {
				// chooseRight = true;
				// toBreak = true;
				// break;
				// }
			}
			// if (toBreak) break;
		}
		// Either left or right is guaranteed to have been seen to account for a good
		// half of the non-halting directions in the Transitions,
		// assuming there is only one Halt transition.
		List<Lemma> lemList = new ArrayList<Lemma>();
		System.out.println("Left graph: " + leftGraph);// Debug code
		DirectedCycle leftFinder = new DirectedCycle(leftGraph);
		if (leftFinder.hasCycle()) {
			StdOut.println("Left cycle:");
			Lemma leftLemma = cycleHelper(m, leftFinder, -1);
			lemList.add(leftLemma);
		} else {
			StdOut.println("Left graph is acyclic");
		}
		DirectedCycle rightFinder = new DirectedCycle(rightGraph);
		if (rightFinder.hasCycle()) {
			StdOut.println("Right cycle:");
			Lemma rightLemma = cycleHelper(m, rightFinder, 1);
			lemList.add(rightLemma);
		} else {
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
		// Now that we know the state list,
		// we should recover what bits created the cycle on being read
		// and what bits were left in their place.
		for (int i = 0; i < stateList.size() - 1; i++) {
			int curr_state = stateList.get(i);
			int next_state = stateList.get(i + 1);
			int bit_read = -1; // if still -1 after we're done, there's an error
			int bit_written = -1;
			for (int b = 0; b < num_symbols; b++) {
				if (t[curr_state].getNextState(b) == next_state && t[curr_state].getToGo(b) == direction) {
					bit_read = b;
					bit_written = t[curr_state].getToWrite(b);
					// Note: this will choose the highest bit if more than one work
				}
			}
			if (bit_read == -1)
				throw new Exception("Could not find bit to have been read");
			bitsRead.add(bit_read);
			bitsWritten.add(bit_written);
		}
		System.out.println("Bits read: " + bitsRead);
		if (direction == -1) {
			Collections.reverse(bitsRead);
			System.out.println("Left to right: " + bitsRead);
		}
		System.out.println("Bits written: " + bitsWritten);
		if (direction == -1) {
			Collections.reverse(bitsWritten);
			System.out.println("Left to right: " + bitsWritten);
		}
		int[] bitsReadAsArray = bitsRead.stream().mapToInt(i -> i).toArray();
		int[] bitsWrittenAsArray = bitsWritten.stream().mapToInt(i -> i).toArray();
		int[] zeroOneArray = { 0, 1 };
		int[] leftArray = { 0 };
		int[] offLeftArray = { -1 };
		int[] rightArray = { -1, bitsRead.size() };
		int[] offRightArray = { 0, bitsRead.size() };
		int[] startIndexArray = { 0 };
		if (direction == 1)
			startIndexArray = leftArray;
		else if (direction == -1)
			startIndexArray = rightArray;
		int[] endIndexArray = { 0, bitsRead.size() };
		if (direction == 1)
			endIndexArray = offRightArray;
		else if (direction == -1)
			endIndexArray = offLeftArray;
		Termfiguration a = new Termfiguration(bitsReadAsArray, zeroOneArray, startIndexArray, stateList.get(0));
		Termfiguration b = new Termfiguration(bitsWrittenAsArray, zeroOneArray, endIndexArray, stateList.get(0));
		int[] numSteps = { 0, bitsRead.size() };
		Lemma lem = new Lemma(m, a, b, numSteps);
		return lem;
	}

	/**
	 * Returns an array of length 2 consisting of � the start step of the longest
	 * run of m in direction dir_desired on a blank tape that starts at at least
	 * lower_bound steps and finishes by upper_bound steps (either by ending or by
	 * upper_bound being reached), and � how long that run is. Use dir_desired=-1
	 * for longest left run, dir_desired=1 for longest right run, dir_desired=0 for
	 * longest unidirectional run.
	 */
	public static int[] longestRun(Machine m, int lower_bound, int upper_bound, int dir_desired) {
		m.reset();
		try {
			if (lower_bound < 0 || upper_bound < lower_bound)
				throw new Exception("Invalid bounds " + lower_bound + ", " + upper_bound + " for longestRun");
			int index = upper_bound;
			int direction = 0;
			int oldDirection = 0;
			int runLength = 0;
			int maxRunLength = 0;
			int runStart = 0;
			int maxRunStart = 0;
			int oldIndex;
			Tape t = new Tape(new int[2 * upper_bound + 1], index);
			int i;
			for (i = 0; i < lower_bound; i++)
				m.act(t);
			index = t.getIndex();
			for (i = lower_bound; i < upper_bound; i++) {
				oldIndex = index;
				if (i > lower_bound)
					oldDirection = direction;
				m.act(t);
				index = t.getIndex();
				direction = index - oldIndex;
				if (direction != 1 && direction != -1)
					throw new Exception("Tape jumped by " + direction);
				if (i == lower_bound || oldDirection == direction) {
					runLength++;
					if (runLength > maxRunLength) {
						if (direction == dir_desired || dir_desired == 0) {
							maxRunLength = runLength;
							maxRunStart = runStart;
						}
					}
				} else {
					runStart = i;
					runLength = 0;
				}
			}
			return new int[] { maxRunStart, maxRunLength };
		} catch (Exception e) {
			e.printStackTrace();
			return new int[] { 0, 0 };
		}
	}

	/**
	 * Meant to be called on a Machine m that goes in one direction from step
	 * lower_bound to step upper_bound. Finds the lowest positive number n <= 10
	 * such that m has a run of length a multiple of n that is at least half of
	 * (upper_bound - lower_bound) in which state & bit repeat perfectly every n
	 * steps. Returns an array of triples: state, bit, direction. Returns null if no
	 * such pattern is found. *
	 */
	public static int[][] runPattern(Machine m, int lower_bound, int upper_bound) {
		m.reset();
		int swath = upper_bound - lower_bound;
		int[] theStates = new int[swath];
		int[] theBits = new int[swath];
		int[] theDirections = new int[swath];
		Tape t = new Tape(upper_bound);
		try {
			for (int i = 0; i < lower_bound; i++)
				m.act(t);
			for (int i = 0; i < swath; i++) {
				theStates[i] = m.getState();
				theBits[i] = t.getSymbol();
				theDirections[i] = m.getTransitions()[theStates[i]].getToGo(theBits[i]);
				int theWay = theDirections[0];
				if (theDirections[i] != theWay) {
					Tools.printIfAtLeast(VOLUME, .2, "Error in runPattern: Machine not in direction " + theWay + "at step "
							+ (lower_bound + i) + " in range [" + lower_bound + ", " + upper_bound + "]");
					return null;
				}
				m.act(t);
			}
			for (int skip = 10; skip >= 1; skip--) {
				Tools.printIfAtLeast(VOLUME, .5, "Calculating the number of perfect matches with skip " + skip + ": ");
				int numMatches = 0;
				for (int i = 0; i < swath - skip; i++) {
					if (theStates[i] == theStates[i + skip] && theBits[i] == theBits[i + skip]
							&& theDirections[i] == theDirections[i + skip])
						numMatches++;
				}
				Tools.printIfAtLeast(VOLUME, .5, "there are " + numMatches + " matches.");
			}
		} catch (Exception e) {
			Tools.printIfAtLeast(VOLUME, .1, "Error in runPattern: " + e.getMessage());
			return null;
		}
		return null;
	}

	/**
	 * Finds for each n less than maxPatternLength the longest subsequence from
	 * lower_bound to upper_bound steps in which state & bit repeat perfectly every
	 * n steps. Returns an array of arrays of length 3 which have at index n >= 1: �
	 * max number of repetitions, � last step at which the situation is still
	 * identical n steps later, � tape head displacement.*
	 */
	public static int[][] bestPattern(Machine m, int lower_bound, int upper_bound, int maxPatternLength) {
		m.reset();
		int swath = upper_bound - lower_bound;
		int[] theStates = new int[swath];
		int[] theBits = new int[swath];
		int[] theTapeHead = new int[swath];
		Tape t = new Tape(upper_bound);
		try {
			for (int i = 0; i < lower_bound; i++)
				m.act(t);
			for (int i = 0; i < swath; i++) {
				theStates[i] = m.getState();
				theBits[i] = t.getSymbol();
				theTapeHead[i] = t.getIndex();
				m.act(t);
			}
			int[][] results = new int[maxPatternLength][3];
			// will have at each index other than 0
			// an array consisting of bestNumMatches and at.
			for (int skip = 1; skip < maxPatternLength; skip++) {
				// System.out.print("Finding the best run of perfect matches with skip "+skip+":
				// ");
				int numMatches = 0;
				int bestNumMatches = 0;
				int at = -1;
				for (int i = 0; i < swath - skip; i++) {
					if (theStates[i] == theStates[i + skip] && theBits[i] == theBits[i + skip]) {
						numMatches++;
						if (numMatches > bestNumMatches) {
							bestNumMatches = numMatches;
							at = lower_bound + i;
						}
					} else
						numMatches = 0;
				}
				results[skip][0] = bestNumMatches;
				results[skip][1] = at;
				if (at - lower_bound >= bestNumMatches) {
					/*
					 * System.out.println("Debug code:" + " skip = " + skip + " at = " + at +
					 * " bestNumMatches = " + bestNumMatches + " theTapeHead.length = swath = " +
					 * swath);
					 */
					results[skip][2] = theTapeHead[at - lower_bound] - theTapeHead[at - lower_bound - bestNumMatches];
				}
				// System.out.println("there are "+bestNumMatches+" matches at " + at);
			}
			Tools.printIfAtLeast(VOLUME, .3, Tools.matrixToString(results, true));
			return results;
		} catch (Exception e) {
			Tools.printIfAtLeast(VOLUME, .1, "Error in bestPattern: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static int bestStartStep(int[][] patternArray) {
		int bestSkip = 0;
		int bestSwath = 0;
		for (int i = 1; i < patternArray.length; i++) {
			int currSwath = patternArray[i][0];
			if (currSwath > bestSwath) {
				bestSkip = i;
				bestSwath = currSwath;
			}
		}
		int endStep = patternArray[bestSkip][1];
		int startStep = endStep - bestSwath + 1;
		return startStep;
	}

	/**
	 * Attempts to formulate and prove a Lemma about the action of m based on the
	 * patternArray passed in. See the return value of bestPattern() for the format
	 * and contents that patternArray should have.
	 */
	public static Lemma guessLemma(Machine m, int[][] patternArray) {
		int bestSkip = 0;
		int bestSwath = 0;
		for (int i = 1; i < patternArray.length; i++) {
			int currSwath = patternArray[i][0];
			if (currSwath > bestSwath) {
				bestSkip = i;
				bestSwath = currSwath;
			}
		}
		int endStep = patternArray[bestSkip][1];
		int displacement = patternArray[bestSkip][2];
		Tools.printIfAtLeast(VOLUME, .5, "The best skip was " + bestSkip + " with " + bestSwath + " repetitions ending at step "
				+ endStep + " after a displacement of " + displacement + ".");
		float approxTermLength = (float) displacement / bestSwath * bestSkip;
		int termLength;
		if (bestSkip % 2 == 0)
			termLength = Math.round(approxTermLength);
		else {
			// round to the nearest odd integer;
			// odd skip can't traverse even length term.
			// So subtract 1; round to the nearest even integer by dividing by 2, rounding,
			// and multiplying by 2; then add 1.
			termLength = Math.round((approxTermLength - 1) / 2) * 2 + 1;
		}
		float error = termLength - approxTermLength;
		/*
		 * if (error > .15 || error < -.15) {
		 * System.out.println("The signed term length "
		 * +approxTermLength+" was not close to an integer, so I'm not even going to bother."
		 * ); return null; }
		 */
		if (termLength == 0) {
			Tools.printIfAtLeast(VOLUME, .2, "The term length was zero, so I'm not even going to bother.");
			return null;
		}
		Tools.printIfAtLeast(VOLUME, .5, "The signed term length seems to be " + approxTermLength + ", so I'm going to guess it's "
				+ termLength + ".");
		StretchTape t1 = new StretchTape(endStep);
		int startStep = endStep - bestSwath + 1;
		m.reset();
		for (int i = 0; i < startStep; i++) {
			try {
				m.act(t1);
			} catch (Exception e) {
				Tools.printIfAtLeast(VOLUME, .1, "Error 1 in guessLemma().");
				return null;
			}
		}
		Tools.printIfAtLeast(VOLUME, .5, "Tape at start step, then at end step: ");
		Tools.printIfAtLeast(VOLUME, .5, startStep + " " + Tools.asLetter(m.getState()) + " " + t1);
		int minIndex = t1.getIndex();
		int maxIndex = t1.getIndex();
		StretchTape t2 = new StretchTape(t1); // t2 for using now, t1 for using later in the code
		StretchTape t3 = new StretchTape(t1); // t3 for using much later in the code
		Machine mm = new Machine(m); // For remembering what state m was in
		Machine m3 = new Machine(m); // Idem.
		int[] numVisits = new int[t2.getTape().length];
		// We'll keep track of how many times a given bit was visited.
		// This could clearly be done with less memory, but I don't want to generate
		// indexing errors.
		// A given index will be considered useful to start with only if it and its
		// equivalent skip steps ahead are visited exactly once.
		// Later, I'll incorporate preamble/epilogue bit sequences in order to be able
		// to generate lemmas when this condition cannot be met.
		for (int i = startStep; i < endStep; i++) {
			numVisits[t2.getIndex()]++;
			try {
				m.act(t2);
			} catch (Exception e) {
				Tools.printIfAtLeast(VOLUME, .1, "Error 2 in guessLemma().");
				return null;
			}
			int index = t2.getIndex();
			if (index > maxIndex)
				maxIndex = index;
			if (index < minIndex)
				minIndex = index;
		}
		Tools.printIfAtLeast(VOLUME, .5, endStep + " " + Tools.asLetter(m.getState()) + " " + t2);
		// Now, we need to be careful to write the Lemma in such a way that
		// the tape head does not exceed the bounds of the Term
		// as it goes from one end to the other.
		//
		// For leftward displacements, we should compare the last |termLength| bits of
		// t1 and t2, ending at maxIndex;
		// for rightward displacements, we should compare the first |termLength| bits of
		// t1 and t2, beginning at minIndex.
		// for both, we use the state of the machine when it got there and the state it
		// was in after skip steps.
		// That's how we write the Lemma.
		int targetIndex = minIndex;
		if (displacement < 0)
			targetIndex = maxIndex;
		boolean foundIndex = false;
		boolean foundSingletons = false;
		// Whether it found two tape head positions that only occur once each, spaced
		// termLength away from each other
		for (int i = startStep; i < endStep; i++) {
			if (t1.getIndex() == targetIndex)
				foundIndex = true;
			if (foundIndex && numVisits[t1.getIndex()] == 1 && numVisits[t1.getIndex() + termLength] == 1) {
				foundSingletons = true;
				break;
			}
			try {
				mm.act(t1);
			} catch (Exception e) {
				Tools.printIfAtLeast(VOLUME, .1, "Error 3 in guessLemma().");
				return null;
			}
		}
		if (!foundIndex) {
			Tools.printIfAtLeast(VOLUME, .2, "Could not find index in guessLemma()");
			return null;
		}
		if (!foundSingletons) {
			Tools.printIfAtLeast(VOLUME, .3, "Could not find two positions with a displacement of " + termLength
					+ " visited only once in the swath");
			// In this case, we will try using every position over the course of skip steps
			// as the starting point for the Lemma.
			// We can use m3 and t3, since they are still in the original state.
			// TODO: make this method waste less memory copying tapes
			StretchTape t4 = new StretchTape(t3); // We'll use t3 for saving, t4 for using
			Machine m4 = new Machine(m3); // same
			for (int i = 0; i < bestSkip; i++) {
				Lemma trialLem = guessLemmaHelper(m4, t4, termLength, bestSkip);
				if (trialLem != null)
					return trialLem;
				try {
					m3.act(t3);
				} catch (Exception e) {
					Tools.printIfAtLeast(VOLUME, .1, "Error 4 in guessLemma().");
					return null;
				}
				t4 = new StretchTape(t3);
				m4 = new Machine(m3);
			}
			return null;
		}
		return guessLemmaHelper(mm, t1, termLength, bestSkip);
	}

	public static Lemma guessLemmaHelper(Machine mm, TapeLike t1, int termLength, int bestSkip) {
		int targetIndex = t1.getIndex();
		int beginState = mm.getState();
		int[] bitSeq = null;
		if (termLength > 0)
			bitSeq = Arrays.copyOfRange(t1.getTape(), targetIndex, targetIndex + termLength);
		if (termLength < 0)
			bitSeq = Arrays.copyOfRange(t1.getTape(), targetIndex + termLength + 1, targetIndex + 1);
		// Remember termLength is signed, with same sign as displacement
		int[] beginLeftIndexArr = { 0, 0 };
		int[] beginRightIndexArr = { -1, Math.abs(termLength) };
		int[] beginIndexArr = beginLeftIndexArr;
		if (termLength < 0)
			beginIndexArr = beginRightIndexArr;
		Termfiguration a = new Termfiguration(bitSeq, new int[] { 0, 1 }, beginIndexArr, beginState);
		Tools.printIfAtLeast(VOLUME, .5, "Begin Termfiguration as string: ");
		if (FIVE_LINE)
			Tools.printIfAtLeast(VOLUME, .3, "");
		Tools.printIfAtLeast(VOLUME, .4, a.toString());
		for (int i = 0; i < bestSkip; i++) {
			try {
				mm.act(t1);
			} catch (Exception e) {
				Tools.printIfAtLeast(VOLUME, .1, "Error in guessLemmaHelper().");
				return null;
			}
		}
		// Now we should verify that the tape head did indeed move by the signed
		// termLength in that number of steps.
		if (targetIndex + termLength != t1.getIndex()) {
			Tools.printIfAtLeast(VOLUME, .3, "In guessLemmaHelper(), the tape head did not move the expected number of steps:");
			Tools.printIfAtLeast(VOLUME, .3, "targetIndex = " + targetIndex + ", termLength = " + termLength + ", t1.getIndex() = "
					+ t1.getIndex());
			return null;
		}
		int endState = mm.getState();
		int[] bitSeq2 = null;
		if (termLength > 0)
			bitSeq2 = Arrays.copyOfRange(t1.getTape(), targetIndex, targetIndex + termLength);
		if (termLength < 0)
			bitSeq2 = Arrays.copyOfRange(t1.getTape(), targetIndex + termLength + 1, targetIndex + 1);
		// Remember termLength is signed, with same sign as displacement
		int[] endForwardIndexArr = { 0, termLength };
		int[] endBackwardIndexArr = { -1, 0 };
		int[] endIndexArr = endForwardIndexArr;
		if (termLength < 0)
			endIndexArr = endBackwardIndexArr;
		Termfiguration b = new Termfiguration(bitSeq2, new int[] { 0, 1 }, endIndexArr, endState);
		Tools.printIfAtLeast(VOLUME, .5, "End Termfiguration as string: ");
		if (FIVE_LINE)
			Tools.printIfAtLeast(VOLUME, .3, "");
		Tools.printIfAtLeast(VOLUME, .4, b.toString());
		Lemma lem = null;
		try {
			lem = new Lemma(mm, a, b, new int[] { 0, bestSkip });
		} catch (Exception e) {
			Tools.printIfAtLeast(VOLUME, .1, "In guessLemmaHelper(), error initializing Lemma: " + e.getMessage());
		}
		if (lem.isProved())
			return lem;
		return null;
	}
}
