package machine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LemmaList {
	//This class is (1) to square away having checked
	//that the Lemmas of a given list all use the same machine,
	//and (2) for locating the first Lemma applicable to a given term. 
	private List<Lemma> _lemlist;
	private Machine _m;
	public LemmaList(List<Lemma> lemlist) throws Exception {
		if (lemlist.size()==0) {_lemlist=lemlist; return;}
		Machine firstMachine = lemlist.get(0).getMachine();
		Iterator<Lemma> lemIterator = lemlist.iterator();
		while (lemIterator.hasNext()) {
			Lemma currLem=lemIterator.next();
			if (currLem.getMachine()!=firstMachine&&!currLem.getMachine().equals(firstMachine))
				throw new Exception("Can't make a LemmaList using Lemmas from distinct machines");
		}
		_lemlist=lemlist;
		_m = firstMachine;
	}
	public int size() {return _lemlist.size();}
	public List<Lemma> getLemList() {return _lemlist;}
	public Machine getMachine() {return _m;}
	public Integer[] firstMatchAndValue(Term term, int side, int state) {
		//Returns a pair consisting of the index of the first Lemma applicable to term, if one exists (-1 otherwise),
		//and the value of n to use in that Lemma's source's evalAt(n) to make it applicable.
		for (int i=0; i<_lemlist.size(); i++) {
			Lemma currLem = _lemlist.get(i);
			Integer n = currLem.valueToUse(term,side,state);
			if (n!=null) return new Integer[] {i,n};
		}
		return new Integer[] {-1,0};
	}
	public boolean allProved() {
		for (int i=0; i<_lemlist.size(); i++) {
			if (!_lemlist.get(i).isProved()) return false;
		}
		return true;
	}
	public void reduce() {
		List<Lemma> newLemlist = new ArrayList<Lemma>();
		Iterator<Lemma> lemIterator = _lemlist.iterator();
		while (lemIterator.hasNext()) {
			Lemma currLem=lemIterator.next();
			Iterator<Lemma> newLemIterator = newLemlist.iterator();
			boolean wasFound = false;
			while (newLemIterator.hasNext()) {
				Lemma currNewLem = newLemIterator.next();
				if (currNewLem.equals(currLem)) {
					wasFound = true;
					break;
				}
			}
			if (!wasFound) newLemlist.add(currLem);
		}
		_lemlist = newLemlist;
	}
}
