package machine;

import java.util.Iterator;
import java.util.List;

public class LemmaList {
	//This class is (1) to square away having checked
	//that the Lemmas of a given list all use the same machine,
	//and (2) for locating the first Lemma applicable to a given term. 
	private List<Lemma> _lemlist;
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
	}
	public List<Lemma> getLemList() {return _lemlist;}
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
}
