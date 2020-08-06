package machine;

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
}
