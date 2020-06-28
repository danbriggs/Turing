package machine;

import java.util.List;

/**Class for representing a Termfiguration sequence in a single variable n.
 * At most one Termfiguration in the sequence may be ornamented.
 * If a Machine should act on a TermfigurationSequence,
 * it is important to remember to deornament a constituent Termfiguration
 * as the tape head moves across the boundary to an adjacent Termfiguration.*/
public class TermfigurationSequence implements TermfigurationLike {
	List<Termfiguration> _tList;
	boolean _ornamented;
	int _activeTermIndex;
	public TermfigurationSequence(List<Termfiguration> tList) {
		int ornamentedCount = 0;
		for (int i=0; i<tList.size(); i++) {
			if (tList.get(i).isOrnamented()) {
				ornamentedCount++;
				_activeTermIndex = i;
			}
		}
		if (ornamentedCount > 1) {
			System.out.println("Error in TermfigurationSequence(): there are "+ornamentedCount+" > 1 ornamented Termfigurations in the list.");
			return;
		}
		_tList = tList;
		if (ornamentedCount == 1) _ornamented = true;
		else if (ornamentedCount == 0) _ornamented = false;
	}
	public boolean isOrnamented() {return _ornamented;}
	public Termfiguration get(int i) {return _tList.get(i);}
	public int getActiveTermIndex() {
		if (!_ornamented) System.out.println("Error: attempt to find active term index of unornamented TermfigurationList");
		return _activeTermIndex;
	}
	
	/**Gives -1 as the degree of the zero polynomial.*/
	public int[] degrees() {
		int numTerms = _tList.size();
		int[] ret = new int[numTerms];
		for (int i=0; i < numTerms; i++) {
			int[] exponent = _tList.get(i).getExponent();
			int[] trimmedExponent = Tools.trimEnd(exponent);
			ret[i] = trimmedExponent.length - 1;
		}
		return ret;
	}
	
	/**Returns the base of the active term
	 * if this is an ornamented TermfigurationSequence,
	 * and otherwise returns null.*/
	public int[] getBase() {
		if (!_ornamented) {
			System.out.println("Error: attempt to get base of unornamented TermfigurationSequence");
			return null;
		}
		Termfiguration activeTerm = _tList.get(_activeTermIndex);
		return activeTerm.getBase();
	}

	/**Returns the exponent of the active term
	 * if this is an ornamented TermfigurationSequence,
	 * and otherwise returns null.*/
	public int[] getExponent() {
		if (!_ornamented) {
			System.out.println("Error: attempt to get exponent of unornamented TermfigurationSequence");
			return null;
		}
		Termfiguration activeTerm = _tList.get(_activeTermIndex);
		return activeTerm.getExponent();
	}
	
	/**Adds the lengths of the Termfigurations up to the active one
	 * and the index of the tape head in the active term.*/
	public int[] getIndex() {
		if (!_ornamented) {
			System.out.println("Error in getIndex(): unornamented TermfigurationSequence");
			return null;
		}
		Termfiguration activeTerm = _tList.get(_activeTermIndex);
		int[] runningTotal = activeTerm.getIndex();
		for (int i=0; i < _activeTermIndex; i++) {
			runningTotal = Tools.add(runningTotal, _tList.get(i).length());
		}
		return runningTotal;
	}
	
	public int getState() throws Exception {
		if (!_ornamented) {
			System.out.println("Error in getState(): unornamented TermfigurationSequence");
			return -2;
		}
		Termfiguration activeTerm = _tList.get(_activeTermIndex);
		return activeTerm.getState();
	}

	public int[] evalAt(int n) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Term toTermAt(int n) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Configuration toConfigurationAt(int n) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public TermfigurationLike successor() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int[] lastBit() {
		// TODO Auto-generated method stub
		return null;
	}
}
