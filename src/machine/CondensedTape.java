package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CondensedTape {
	private List<Term> _termlist;
	public CondensedTape() {_termlist = new ArrayList<Term>();}
	public CondensedTape(List<Term> termlist) {_termlist = termlist;}
	public List<Term> getTermList() {return _termlist;}
	public Term get(int i) {return _termlist.get(i);}
	public int numTerms() {return _termlist.size();}
	public void setTermList(List<Term> termlist) {_termlist = termlist;}
	/**Returns -1 if it doesn't work out.*/
	public int getBit(int termNum, int indexInTerm) {
		Term term = _termlist.get(termNum);
		int[] base = term.getBase();
		int exponent = term.getExponent();
		int baselen = base.length;
		if (indexInTerm < baselen * exponent)
			return base[indexInTerm % base.length];
		return -1;
	}
	public int getExponent(int termNum) {
		return _termlist.get(termNum).getExponent();
	}
	public void append(Term t) {
		_termlist.add(t);
	}
	public void replace(int i, Term t) throws Exception {
		if (i>=_termlist.size()) {
			throw new Exception("In CondensedTape.replace(): index "+i+" out of bounds for _termlist of size "+_termlist.size());
		}
		_termlist.set(i,t);
	}
	public String toString() {
		StringBuffer bs = new StringBuffer();
		Iterator<Term> i=_termlist.iterator();
		while (i.hasNext()) {
			Term t = i.next();
			bs.append(t.toString());
		}
		return bs.toString();
	}
	public List<Integer> expand() {
		List<Integer> a = new ArrayList<Integer>();
		Iterator<Term> i = _termlist.iterator();
		while (i.hasNext()) {
			Term t=i.next();
			ArrayList<Integer> ab = new ArrayList<Integer> (Arrays.asList(t.toArray()));
			a.addAll(ab);
		}
		return a;
	}
	public int[] expandToArray() {
		Iterator<Term> i=_termlist.iterator();
		int length = 0;
		while (i.hasNext()) {
			Term t = i.next();
			length += t.getBase().length * t.getExponent();
		}
		int[] arr = new int[length];
		
		i=_termlist.iterator();
		int pos = 0;
		while(i.hasNext()) {
			Term t = i.next();
			for (int j = 0; j < t.getExponent(); j++) {
				for (int k = 0; k < t.getBase().length; k++) {
					arr[pos] = t.getBase()[k];
					pos++;
				}
			}
		}
		
		return arr;
	}
	/**Splits an exponent 1 copy of the termNumth term's base off at whatever copy of the base indexInTerm indexes into,
	 * as long as its exponent is greater than 1.
	 * Thus the number of terms increases by 1 if at the first or last copy of the base, and 2 otherwise.
	 * Returns a length 2 array consisting of the new term number and index therein of the pair if successful, null otherwise.
	 * */
	public int[] split(int termNum, int indexInTerm) {
		Term term = _termlist.get(termNum);
		int[] base = term.getBase();
		int baselen = base.length;		
		int whichCopyOfBase = indexInTerm / baselen;
		int exponent = term.getExponent();
		if (whichCopyOfBase >= exponent) {
			System.out.println("indexInTerm too high in split()");
			return null;
		}
		if (exponent == 1) return new int[] {termNum, indexInTerm};
		if (whichCopyOfBase == 0) {
			term.setExponent(exponent-1);
			_termlist.add(termNum, new Term(base, 1));
			//I made sure to add .clone() to the constructor 7/25/20
			return new int[] {termNum, indexInTerm};
		}
		if (whichCopyOfBase == exponent -1) {
			term.setExponent(exponent-1);
			_termlist.add(termNum+1, new Term(base, 1));
			return new int[] {termNum+1, indexInTerm - (exponent - 1)*baselen};		
		}
		_termlist.add(termNum+1, new Term(base, exponent-whichCopyOfBase-1));
		_termlist.add(termNum  , new Term(base, whichCopyOfBase));
		term.setExponent(1);
		return new int[] {termNum+1, indexInTerm - whichCopyOfBase*baselen};
	}
	public String expandToString() {
		List<Integer> a = expand();
		Iterator<Integer>i=a.iterator();
		StringBuffer sb = new StringBuffer();
		while (i.hasNext()) {
			Integer ii = i.next();
			sb.append(ii.intValue());
		}
		return sb.toString();
	}
	protected void glueExcluding(int termNum) {
		//Order is important here!
		glueToward(termNum + 1, 1);
		glueToward(termNum - 1, -1);
	}
	/**Glues termNum together with its neighbor adjacent in the direction dir if possible.*/
	protected void glueToward(int currNum, int dir) {
		int nextNum = currNum + dir;
		if (nextNum >= 0 && nextNum < numTerms()) {
			Term currTerm = get(currNum);
			Term nextTerm = get(nextNum);
			if (Tools.areIdentical(currTerm.getBase(), nextTerm.getBase())) {
				_termlist.set(currNum, new Term(currTerm.getBase(), currTerm.getExponent() + nextTerm.getExponent()));
				_termlist.remove(nextNum);
			}
		}
	}
}
