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
	/** Returns a 2-tuple consisting of
	    the index of the term _index is in
	    and the index of _index in that term.
	    Returns {-1,_index} if _index is out of bounds.*/
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
	
	/**Generalizes this CondensedConfiguration to a Termfiguration
	 * only if the following conditions are met:
	 * index is at the left- hand end of a term if direction is 1,
	 *       resp. the right-hand end of a term if direction is -1;
	 * the base of the Term containing the index matches pattern.
	 * In this case it replaces the exponent of the Term containing the index with N.
	 * Otherwise, it returns null.*/
	Termfiguration generalize(int[] pattern, int direction) {
		return null;
	}
}
