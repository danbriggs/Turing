package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//Later: include index for tape head?

public class CondensedTape {
	private List<Term> _termlist;
	public CondensedTape() {_termlist = new ArrayList<Term>();}
	public CondensedTape(List<Term> termlist) {_termlist = termlist;}
	public List<Term> getTermList() {return _termlist;}
	public void setTermList(List<Term> termlist) {_termlist = termlist;}
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
}
