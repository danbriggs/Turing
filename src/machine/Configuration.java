package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Configuration extends Tape {
	private int _state;
	public Configuration(int[] tape) throws Exception {
		this(tape,0);
	}
	public Configuration(int[] tape, int index) throws Exception {
		this(tape, index, 0);
	}
	public Configuration(int[] tape, int index, int state) throws Exception {
		if (index < -1 || index>tape.length) throw new Exception("Index "+index+" out of range for configuration of length "+tape.length);
		setTape(tape);
		setIndex(index);
		setState(state);
	}
	public Configuration(String s) throws Exception {
		//s should have the state as a capital letter (A for 0 etc., and @ for -1 (halt)),
		//followed by a space, then the tape.
		super(s.substring(2));
		if (s.charAt(0)=='@') _state=-1;
		else _state = (int)(s.charAt(0)-'A');
	}
	public Configuration(Tape t, int state) {
		super(t);
		_state = state;
	}
	public Configuration(Tape t) {
		this(t,0);
	}
	public String toString() {
		String s = (char)(getState()+65)+" ";
		if (getIndex()==-1) s+='<';
		s+=super.toString();
		if (getIndex()==getTape().length) s+='>';
		return s;
	}
	public String getTrimAsString() {
		String s = (char)(getState()+65)+" ";
		if (getIndex()==-1) s+='<';
		s+=super.getTrimAsString();
		if (getIndex()==getTape().length) s+='>';
		return s;
	}
	public void printTrim() {
		System.out.println(getTrimAsString());
	}
	public int getState() {return _state;}
	public void setState(int state) {_state = state;}
	public boolean isAlive() {
		if (getIndex()==-1 || getIndex()==getTape().length || _state==-1) return false;
		return true;
	}
	public boolean equals(Configuration c) {
		int[] tape1 = getTape();
		int[] tape2 = c.getTape();
		if (tape1.length!=tape2.length)return false;
		if (getIndex() != c.getIndex()) return false;
		if (getState() != c.getState()) return false;
		for (int i=0; i<tape1.length; i++) if (tape1[i]!=tape2[i]) return false;
		return true;
	}
	public Configuration copy() throws Exception {return new Configuration(getTape().clone(),getIndex(),getState());}
	
	/**Condenses it, splitting it from the index going in direction direction.*/
	public CondensedConfiguration condenseAndSplitUsing(int direction, int[] pattern) {
		if (direction ==0) return null;
		if (direction > 0) return condenseAndSplitUsing(pattern);
		return reverse().condenseAndSplitUsing(Tools.reverse(pattern)).reverse();
	}
	
	public CondensedConfiguration condenseAndSplitUsing(int[] pattern) {
		Configuration c1 = subcon(0,getIndex(),0);
		//0's a dummy value for the index of the first configuration
		Configuration c2 = subcon(getIndex(),length());
		CondensedConfiguration cc1 = c1.condenseUsing(pattern);
		CondensedConfiguration cc2 = c2.condenseUsing(pattern);
		return CondensedConfiguration.combine(cc1,cc2,1);
	}
	
	/**returns a configuration consisting of everything from begin up to but not including sup.*/
	public Configuration subcon(int begin, int sup) {
		Tape t = super.subtape(begin, sup);
		return new Configuration(t,_state);
	}

	/**returns a configuration consisting of everything from begin up to but not including sup
	 * with new index newIndex.*/
	public Configuration subcon(int begin, int sup, int newIndex) {
		Tape t = super.subtape(begin, sup, newIndex);
		return new Configuration(t,_state);
	}
	
	public Configuration reverse() {
		try {
			return new Configuration(Tools.reverse(getTape()), length() - 1 - getIndex(), _state);
		} catch (Exception e) {
			System.out.println("Error in Configuration.reverse(): " + e.getMessage());
			return null;
		}
	}
	
	public CondensedConfiguration condenseUsing (int[] pattern) {
		return condenseUsing(pattern, 2);
	}

	public CondensedConfiguration condenseUsing (int[] pattern, int numtimes) {
		List<int[]> patternList = new ArrayList<int[]>();
		patternList.add(pattern);
		return condenseUsing(patternList, numtimes);
	}
	
	/** Looks through this's tape for patterns matching patternList's first pattern etc.
	 *  and generates a condensed configuration with a Term for each repeating sequence
	 *  and a Term for each stretch between and beyond repeating sequences.*/
	public CondensedConfiguration condenseUsing (List<int[]> patternList) {
		return condenseUsing(patternList, 2);
	}
	
	/** Makes a new term only if the number of repetitions is at least numtimes.*/
	public CondensedConfiguration condenseUsing (List<int[]> patternList, int numtimes) {
		int[] toSkip = new int[length()]; //If the given bit begins a sequence of bits already involved in a pattern,
		                                  //record the number of bits involved
		int[] baselens = new int[length()];
		int[] exponents = new int[length()];
		Iterator<int[]> i = patternList.iterator();
		while (i.hasNext()) {
			int[] currPattern = i.next();
			int lowerThreshold = currPattern.length * numtimes;
			for (int j = 0; j < length(); j++) {
				if (toSkip[j] > 0) {
					j += toSkip[j] - 1; //Because continuing will already add one
					continue;
				}
				int numMatches = numMatches(currPattern, j, toSkip);
				if (numMatches >= lowerThreshold) {
					int numTerms = numMatches / currPattern.length;
					toSkip[j] = numTerms * currPattern.length;
					baselens[j] = currPattern.length;
					exponents[j] = numTerms;
					j += toSkip[j] - 1;
				}
			}
		}
		//Now we go through getTape() and make terms wherever there was an exponent.
		//We put the stuff before, between and after into exponent 1 terms. 
		List<Term> termlist = new ArrayList<Term>();
		int index = 0;
		int nextIndex = 0;
		Term t = null;
		while (index < length()) {
			int baselen = baselens[index];
			int exponent = exponents[index];
			if (exponent > 0) {
				nextIndex = index + baselen * exponent;
				t = new Term(Arrays.copyOfRange(getTape(), index, index + baselen), exponent);
			}
			else {
				//The next index will be wherever a new pattern starts or the end of the tape.
				while (nextIndex < length() && exponents[nextIndex] == 0) nextIndex++;
				t = new Term(Arrays.copyOfRange(getTape(), index, nextIndex), 1);
			}
			termlist.add(t);
			index = nextIndex;
		}
		return new CondensedConfiguration(termlist, getIndex(), getState());
	}
	
	/** Returns the number of bits matching pattern repeated this's tape has starting at index.
	 *  Stops counting if it encounters a nonzero entry in toSkip.*/
	private int numMatches(int[] pattern, int index, int[] toSkip) {
		int total = 0;
		int patlen = pattern.length;
		for (int i = index; i < length(); i++) {
			if (toSkip[i] != 0) break;
			if (getTape()[i] == pattern[total % patlen]) total++;
			else break;
		}
		return total;
	}
	
	/**Returns -1 if there is no good spot.
	 * Only picks up on the spot if pattern is actually repeated!*/
	public int bestSpot(int[] pattern, int handedness) {
		List<int[]> patternList = new ArrayList<int[]>();
		patternList.add(pattern);
		CondensedConfiguration cc = condenseUsing(patternList);
		List<Term> termList = cc.getTermList();
		Iterator<Term> termIterator = termList.iterator();
		int crawlingIndex = 0;
		int bestIndex = -1;
		int maxExponent = 2;
		while (termIterator.hasNext()) {
			Term t = termIterator.next();
			int currExponent = t.getExponent();
			if (currExponent >= maxExponent) {
				//Here we can be confident that the base was pattern,
				//since patternList has only one pattern
				if (currExponent > maxExponent || handedness < 0 || bestIndex == -1) {
					if (handedness > 0) bestIndex = crawlingIndex;
					else bestIndex = crawlingIndex + t.length() - 1;
				}
				maxExponent = currExponent;
			}
			crawlingIndex += t.length();
		}
		return bestIndex;
	}
	
	StepConfiguration toStepConfigurationAt(int numSteps) throws Exception{
		return new StepConfiguration(getTape(), getIndex(), _state, numSteps);
	}
	
}
