package machine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CondensedConfiguration extends CondensedTape {
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
		if (direction < 0) abstractIndex = new int[] {-1 + exponent * base.length, base.length};
		Termfiguration tf = new Termfiguration(base, new int[] {exponent, 1}, abstractIndex, _state);
		int[] l = termsAsArray(0,termNum);
		int[] r = termsAsArray(termNum+1,size());
		return new ExtendedTermfiguration(l, tf, r);
	}
	
	int[] termsAsArray(int begin, int sup) {
		int length = 0;
		for (int i=begin; i<sup; i++) length += get(i).length();
		int[] ret = new int[length];
		int baseIndex = 0;
		for (int i=begin; i<sup; i++) {
			Term t = get(i);
			int[] base = t.getBase();
			int baselen = base.length;
			for (int it = 0; it < t.getExponent(); it++) {
				for (int j = 0; j < baselen; j++) {
					ret[baseIndex + it * baselen + j] = base[j];
				}
			}
			baseIndex += t.length();
		}
		return ret;
	}
}
