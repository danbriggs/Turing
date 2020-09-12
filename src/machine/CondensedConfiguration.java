package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CondensedConfiguration extends CondensedTape {
	static final int L = -1, R = 1;
	private int _index;
	private int _state;
	public CondensedConfiguration(List<Term> termlist, int index, int state) {
		setTermList(termlist);
		_index = index;
		_state = state;
	}
	public CondensedConfiguration(CondensedTape ct, int index, int state) {
		setTermList(ct.getTermList());
		_index = index;
		_state = state;
	}
	public int getIndex() {return _index;}
	public int getState() {return _state;}
	public void setIndex(int index) {_index=index;}
	public void setState(int state) {_state=state;}
	public String toString() {
		return Tools.asLetter(_state)+" "+super.toString()+" "+_index;
	}
	public String expandToString() {
		return Tools.asLetter(_state)+" "+super.expandToString()+" "+_index;
	}
	public Configuration toConfiguration() throws Exception {
		return new Configuration(expandToArray(), _index, _state);
	}
	public boolean equals(Configuration c) {
		if (_index!=c.getIndex()) return false;
		if (_state!=c.getState()) return false;
		List<Integer> asList = expand();
		Integer[] asArray = asList.toArray(new Integer[0]);
		int[] csTape = c.getTape();
		if (asArray.length!=csTape.length) return false;
		for (int i=0; i<asArray.length; i++) if (asArray[i]!=csTape[i]) return false;
		return true;
	}
	public void glueOutward() {
		glueExcluding(termNumAndIndex()[0]);
	}
	public CondensedConfiguration reverse() {
		CondensedTape ct = super.reverse();
		return new CondensedConfiguration(ct, length() - 1 - _index, _state);
	}
	
	/**Combines cc1 and cc2. which indicates which one should have the tape head & state.*/
	public static CondensedConfiguration combine(CondensedConfiguration cc1, CondensedConfiguration cc2, int which) {
		if (which != 0 && which != 1) {
			System.out.println("Invalid which = "+which+" in CondensedConfiguration.combine().");
			return null;
		}
		List<Term> termlist = new ArrayList<Term>();
		
		Iterator<Term> it1 = cc1.getTermList().iterator();
		while (it1.hasNext()) {
			Term t1 = it1.next();
			termlist.add(t1);
		}
		
		Iterator<Term> it2 = cc2.getTermList().iterator();
		while (it2.hasNext()) {
			Term t2 = it2.next();
			termlist.add(t2);
		}
		
		if (which == 0) return new CondensedConfiguration(termlist,cc1.getIndex(),cc1.getState());
		return new CondensedConfiguration(termlist,cc1.length()+cc2.getIndex(),cc2.getState());
	}
	
	/**Generalizes this CondensedConfiguration to an ExtendedTermfiguration
	 * only if the following conditions are met:
	 * index is at the left-hand  end of a term if direction is 1,
	 *       resp. the right-hand end of a term if direction is -1;
	 * the base of the Term containing the index matches pattern.
	 * In this case it replaces the exponent c of the Term containing the index with c + N.
	 * Otherwise, it returns null.*/
	ExtendedTermfiguration generalize(int[] pattern, int direction) {
		return generalize(pattern, direction, 1);
	}
	
	/**As above, but gives N a coefficient of linCoeff.*/
	ExtendedTermfiguration generalize(int[] pattern, int direction, int linCoeff) {
		System.out.println("Debug code. In cc.generalize(): cc="+this);
		if (direction == 0) return null;
		int[] termNumAndIndex = termNumAndIndex();
		int termNum = termNumAndIndex[0];
		int indexInTerm = termNumAndIndex[1];
		Term currTerm = get(termNum);
		int[] base = currTerm.getBase();
		int exponent = currTerm.getExponent();
		if (!Tools.areIdentical(base, pattern)) return null;
		if (direction > 0 && indexInTerm != 0) return null;
		if (direction < 0 && indexInTerm != currTerm.length() - 1) return null;
		//Make the exponent c + N;
		//need to make the index correspond.
		int[] abstractIndex = new int[] {0, 0};
		if (direction < 0) abstractIndex = new int[] {-1 + exponent * base.length, linCoeff * base.length};
		Termfiguration tf = new Termfiguration(base, new int[] {exponent, linCoeff}, abstractIndex, _state);
		int[] l = termsAsArray(0,termNum);
		int[] r = termsAsArray(termNum+1,size());
		return new ExtendedTermfiguration(l, tf, r);
	}
	
	/**As above, but tape head must instead be all the way at the end opposite direction.
	 * The Term with the highest exponent is used for matching pattern.*/
	ExtendedTermfiguration generalizeFromEnd(int[] pattern, int direction) {
		return generalizeFromEnd(pattern, direction, 1);
	}
	/**As above, but gives N a coefficient of linCoeff.*/
	ExtendedTermfiguration generalizeFromEnd(int[] pattern, int direction, int linCoeff) {
		if (direction == 0) {
			System.out.println("CondensedConfiguration.generalizeFromEnd() failed at 1");
			return null;
		}
		int termNum = termNumWithMaxExponent();
		Term term = get(termNum);
		int[] base = term.getBase();
		int exponent = term.getExponent();
		if (!Tools.areIdentical(base, pattern)) {
			System.out.println("CondensedConfiguration.generalizeFromEnd() failed at 2");
			return null;
		}
		if (direction > 0 && !onLeft()) {
			System.out.println("CondensedConfiguration.generalizeFromEnd() failed at 3");
			return null;
		}
		if (direction < 0 && !onRight()) {
			System.out.println("CondensedConfiguration.generalizeFromEnd() failed at 4");
			return null;
		}
		int[] l = termsAsArray(0,termNum);
		int[] r = termsAsArray(termNum+1,size());
		int[] abstractIndex = null;
		if (direction > 0) abstractIndex = new int[] {-l.length, 0};
		else if (direction < 0) abstractIndex = new int[] {-1 + exponent * base.length + r.length, linCoeff * base.length};
		Termfiguration tf = new Termfiguration(base, new int[] {exponent, linCoeff}, abstractIndex, _state);
		ExtendedTermfiguration etf = new ExtendedTermfiguration(l, tf, r);
		etf.padBothSides(); //In case we're going to use it later, easier to avoid OutOfBoundsExceptions
		return etf;
	}
	
	/**Returns a positive int difference in exponent only if the array of Condensed Configurations has the very simplest of increasing patterns.
	 * Also returns the difference if it's a constant difference in exponent less than or equal to 0.
	 * Returns -1 if the array is null or there is no constant difference, or bases or wings change, and 0 if the array is of length less than 2.
	 * The only reanalysis done is flattening the wings into arrays.*/
	public static int simpleIncreasingPattern(CondensedConfiguration[] arr) {
		if (arr == null) {
			System.out.println("In CondensedConfiguration.simpleIncreasingPattern: arr is null");
			return -1;
		}
		int len = arr.length;
		if (len == 0 || len == 1) return 0;
		CondensedConfiguration cc = arr[0];
		int termNum = cc.termNumWithMaxExponent();
		int[] lWing = cc.expandToArray(0, termNum);
		int[] rWing = cc.expandToArray(termNum + 1, cc.numTerms());
		Term term = cc.get(termNum);
		int[] base = term.getBase();
		int initialExponent = term.getExponent();
		int prevExponent = initialExponent;
		int state = cc.getState();
		int diff = -1;
		int side = 0;
		if (cc.onLeft()) side = L;
		else if (cc.onRight()) side = R;
		else return -1;
		for (int i = 1; i < len; i++) {
			CondensedConfiguration cc2 = arr[i];
			int currTermNum = cc2.termNumWithMaxExponent();
			int[] currLWing = cc2.expandToArray(0, currTermNum);
			int[] currRWing = cc2.expandToArray(currTermNum + 1, cc2.numTerms());
			Term currTerm = cc2.get(currTermNum);
			int[] currBase = currTerm.getBase();
			int currExponent = currTerm.getExponent();
			int currState = cc2.getState();
			if (i == 1) diff = currExponent - initialExponent;
			int currDiff = currExponent - prevExponent;
			if (i < 3) {
			System.out.println("In CC.simpleIncreasingPattern(): left wings / bases / right wings:");
			System.out.println(Tools.toString(currLWing)+','+Tools.toString(lWing)+'/'
					          +Tools.toString(currBase) +','+Tools.toString(base) +'/'
					          +Tools.toString(currRWing)+','+Tools.toString(rWing));
			}
			if (!Arrays.equals(currLWing, lWing) || !Arrays.equals(currRWing, rWing) || !Arrays.equals(currBase, base))
				return -1;
			if (i < 3) {
			System.out.println("states / exponents / diffs:");
			System.out.println(currState + "," + state + "/"
					          + currExponent + "," + prevExponent + "/"
			                  + currDiff + "," + diff);
			}
			if (currState != state || currDiff != diff)
				return -1;
			if (side == L && !cc2.onLeft() || side == R && !cc2.onRight()) {
				System.out.println("In simpleIncreasingPattern(): side alternation did not occur");
				return -1;
			}
			prevExponent = currExponent;
		}
		return diff;
	}
	
	public static int[] simpleIncreasingPatterns(CondensedConfiguration[] arr, int skip) {
		if (arr == null) return null;
		if (skip <= 0) return null;
		int[] ret = new int[skip];
		for (int i = 0; i < skip; i++) {
			CondensedConfiguration[] ccs = Tools.skipArray(arr, skip, i);
			ret[i] = simpleIncreasingPattern(ccs);
		}
		return ret;
	}
	
	public int[] termNumAndIndex() {
		if (_index<0) return new int[] {-1,_index};
		int lengthOfTermsSoFar = 0;
		Iterator<Term> i = getTermList().iterator();
		int ii=0;
		while (i.hasNext()) {
			Term currTerm = i.next();
			int lengthOfCurrTerm = currTerm.length(); 
			if (lengthOfTermsSoFar<=_index&&_index<lengthOfTermsSoFar+lengthOfCurrTerm)
				return new int[] {ii,_index-lengthOfTermsSoFar};
			lengthOfTermsSoFar+=lengthOfCurrTerm;
			ii++;
		}
		return new int[] {-1,_index};
	}

	public boolean onLeft() {
		return Arrays.equals(termNumAndIndex(), new int[] {0,0});
	}
	
	public boolean onRight() {
		return Arrays.equals(termNumAndIndex(), new int[] {size() - 1, get(size() - 1).length() - 1});
	}
}
