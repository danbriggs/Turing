package machine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Backtracking {
	static final double VOLUME = .4;
	public static final int H = -1;
	
	/**Prints all the backtracks from the start step to the stop step.*/
	public static void printBacktracks(Machine m, int start, int stop) {
		Tools.printIfAtLeast(VOLUME, .5, "Doing "+stop+" backtracks for Machine");
		Tools.printIfAtLeast(VOLUME, .5, ""+m);
		List<StepConfiguration> backtracklist = backtrack(m, stop, true);
		Iterator<StepConfiguration> it = backtracklist.iterator();
		while (it.hasNext()) {
			StepConfiguration sc = it.next();
			if (-sc.getNumSteps() >= start) Tools.printIfAtLeast(VOLUME, .5, ""+sc);
		}
	}
	
	/**Returns the number of backtracks from start step to stop step inclusive.*/
	public static int numBacktracks(Machine m, int start, int stop) {
		List<StepConfiguration> backtracklist = backtrack(m, stop, true);
		Iterator<StepConfiguration> it = backtracklist.iterator();
		int tot = 0;
		while (it.hasNext()) {
			StepConfiguration sc = it.next();
			if (-sc.getNumSteps() >= start) tot++;
		}
		return tot;
	}
	
	/**Projects the machine backwards from the halt state for n steps.
	 * Should pass in a machine with only one transition to halt.
	 * Returns all the StepConfigurations from all the steps along the way if all is true.
	 * Uses negative numbers of steps for the StepConfigurations to denote steps until halt.*/
	public static List<StepConfiguration> backtrack(Machine m, int n, boolean all) {
		@SuppressWarnings("unchecked") List<int[]>[] canLeadToes = (ArrayList<int[]>[]) new ArrayList[m.numStates()];
		for (int i = 0; i < m.numStates(); i++) canLeadToes[i] = canLeadTo(m, i);
		int[] pair = quicktrack(m, H);
		int state = pair[0];
		int bit = pair[1];
		List<StepConfiguration> wholeList = new ArrayList<StepConfiguration>();
		List<StepConfiguration> currList  = new ArrayList<StepConfiguration>();
		try {currList.add(new StepConfiguration(new int[] {bit}, 0, state, -1));}
		catch (Exception e) {return null;}
		wholeList.addAll(currList);
		for (int i = 1; i < n; i++) {
			List<StepConfiguration> newList = new ArrayList<StepConfiguration>();
			Iterator<StepConfiguration> it = currList.iterator();
			while (it.hasNext()) {
				StepConfiguration sc = it.next();
				int index = sc.getState();
				if (index == H) index = m.numStates();
				List<int[]> quads = canLeadToes[index];
				Iterator<int[]> subIt = quads.iterator();
				while (subIt.hasNext()) {
					int[] quad = subIt.next();
					int oldState  = quad[0];
					int oldBit    = quad[1];
					int newBit    = quad[2];
					int direction = quad[3];
					/* Now we must determine if sc was capable of coming from the direction opposite direction
					 * and leaving whatever's there behind.
					 * If that's beyond the bounds of sc, the answer is a default yes.*/
					int posWasAt = sc.getIndex() - direction;
					if (!sc.isInBounds(posWasAt)) {
						/* Tack on a new StepConfiguration longer than the current one,
						 * with oldBit in the new spot, in state oldState, with numSteps one less than sc's.*/
						int[] oldArray = sc.getTape();
						int[] newArray = new int[oldArray.length + 1];
						int pos = 0;
						if (posWasAt == -1) pos = 1; 
						System.arraycopy(oldArray, 0, newArray, pos, oldArray.length);
						int tapeHeadAt = 0;
						if (posWasAt == oldArray.length) tapeHeadAt = oldArray.length;
						newArray[tapeHeadAt] = oldBit;
						StepConfiguration scNew = null;
						try {scNew = new StepConfiguration(newArray, tapeHeadAt, oldState, sc.getNumSteps() - 1);}
						catch(Exception e) {return null;}
						newList.add(scNew);
					}
					else if (sc.getSymbol(posWasAt) == newBit) {
						/* Tack on a new StepConfiguration with oldBit in the appropriate spot, in state oldState,
						 * with numSteps one less than sc's. */
						int[] newArray = sc.getTape().clone();
						newArray[posWasAt] = oldBit;
						StepConfiguration scNew = null;
						try {scNew = new StepConfiguration(newArray, posWasAt, oldState, sc.getNumSteps() - 1);}
						catch(Exception e) {return null;}
						newList.add(scNew);
					}
				}
			}
			currList = newList;
			if (!all) wholeList = new ArrayList<StepConfiguration>();
			wholeList.addAll(newList);
		}
		return wholeList;
	}
		
	/**Returns a list of all {state, oldbit, newbit, direction} quadruples
	 * that can lead the Machine to enter state targetState. */
	public static List<int[]> canLeadTo(Machine m, int targetState) {
		List<int[]> ret = new ArrayList<int[]>();
		for (int i = 0; i < m.numStates(); i++) {
			Transition t = m.getTransitions()[i];
			for (int j = 0; j < 2; j++) {
				if (t.getNextState(j) == targetState) {
					int[] quadruple = {i,j,t.getToWrite(j),t.getToGo(j)};
					ret.add(quadruple);
				}
			}
		}
		return ret;
	}
	
	/**Returns a {state, bit} pair for only the first transition found.
	 * Useful e.g. if you know there's only one halt transition.*/
	public static int[] quicktrack(Machine m, int targetState) {
		int[] quad = canLeadTo(m, targetState).get(0);
		int state = quad[0];
		int bit   = quad[1];
		return new int[] {state, bit};
	}
}
