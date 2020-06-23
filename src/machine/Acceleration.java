package machine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import graph.Cycle;
import graph.Graph;
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
}
