package machine;

import java.util.ArrayList;
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
	
	/**Important for protection: deepCopy() calls this constructor.*/
	public TermfigurationSequence(TermfigurationSequence ts) {
		_tList = new ArrayList<Termfiguration>();
		for (int i=0; i < ts.asList().size(); i++) {
			_tList.add(ts.get(i).deepCopy());
		}
		_ornamented = ts.isOrnamented();
		if (_ornamented) _activeTermIndex = ts.getActiveTermIndex();
	}
	
	public VeryTermfigurationLike toVeryTermfigurationLike() {
		if (isOrnamented()) return get(_activeTermIndex);
		return null;
	}
	
	public Termfiguration toTermfiguration() {
		if (isOrnamented()) return get(_activeTermIndex);
		return null;
	}
	
	public boolean isOrnamented() {return _ornamented;}
	/**Once this is run, one should not be able to recover the ornamentation.*/
	public void deOrnament() throws Exception{
		if (_ornamented == false) throw new Exception("Error: attempt to deornament unornamented TermfigurationSequence");
		_ornamented = false;
	}

	public Termfiguration get(int i) {return _tList.get(i);}
	public int getActiveTermIndex() {
		if (!_ornamented) System.out.println("Error: attempt to find active term index of unornamented TermfigurationSequence");
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
	
	public List<Termfiguration> asList(){return _tList;}
	
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
	
	public int getState() {
		if (!_ornamented) return -2;
		Termfiguration activeTerm = _tList.get(_activeTermIndex);
		return activeTerm.getState();
	}
	
	//TODO: Change these later!
	public boolean onLeft() {return false;}
	public boolean offLeft() {return false;}
	public boolean onRight() {return false;}
	public boolean offRight() {return false;}
	
	public int[] evalAt(int n) throws Exception {
		int combinedBitSeqLen = 0;
		int[][] bitSeqs = new int[_tList.size()][];
		for (int i = 0; i < _tList.size(); i++) {
			int[] bitSeq = _tList.get(i).evalAt(n);
			combinedBitSeqLen += bitSeq.length;
			bitSeqs[i] = bitSeq;
		}
		int[] combinedBitSeq = new int[combinedBitSeqLen];
		int combinedBitSeqIndex = 0;
		for (int i = 0; i < _tList.size(); i++) {
			int[] bitSeq = bitSeqs[i];
			for (int j = 0; j < bitSeq.length; j++)
				combinedBitSeq[combinedBitSeqIndex + j] = bitSeq[j];
			combinedBitSeqIndex += bitSeq.length;
		}
		return combinedBitSeq;
	}
	
	public Term toTermAt(int n) throws Exception {
		if (!_ornamented) throw new Exception("Error: attempt to call toTermAt("+n+") for unornamented TermfigurationSequence");
		return get(_activeTermIndex).toTermAt(n);
	}
	
	public Configuration toConfigurationAt(int n) throws Exception {
		if (!_ornamented) throw new Exception("In toConfigurationAt("+n+"), cannot convert unornamented TermfigurationSequence to Configuration.");
		int[] tapeContents = evalAt(n);
		int index = Tools.evalAt(getIndex(), n);
		return new Configuration(tapeContents, index, getState());
	}
	
	public TermfigurationLike successor() {
		List<Termfiguration> tPrimeList = new ArrayList<Termfiguration>();
		for (int i = 0; i < _tList.size(); i++)
			tPrimeList.add(_tList.get(i).successor());
		return new TermfigurationSequence(tPrimeList);
	}
	
	public int[] lastBit() {
		return Tools.add(length(),new int[] {-1});
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (_ornamented) {
			try {sb.append(Tools.asLetter(getState())+" ");}
			catch (Exception e) {System.out.println("Error: "+e.getMessage());}
		}
		for (int i=0; i<_tList.size(); i++) {
			sb.append(_tList.get(i).toStringBuffer());
			sb.append(" ");
		}
		if (_ornamented) sb.append(Tools.toPolynomialString(getIndex(), 'n'));
		return sb.toString();
	}
	
	public int[] length() {
		int[] ret = new int[0];
		for (int i = 0; i < _tList.size(); i++) {
			ret = Tools.add(ret, _tList.get(i).length());
		}
		return ret;
	}
	
	public TermfigurationSequence deepCopy() {
		return new TermfigurationSequence(this);
	}
	
	public boolean equals(TermfigurationLike tl) {
		TermfigurationSequence ts = tl.toTermfigurationSequence();
		if (!(ts instanceof TermfigurationSequence)) return false;
		if (_tList.size() != ts.asList().size()) return false;
		if (_ornamented != ts.isOrnamented()) return false;
		for (int i = 0; i < _tList.size(); i++)
			if (!_tList.get(i).equals(ts.get(i))) return false;
		return true;
	}
	
	public ExtendedTermfiguration toExtendedTermfiguration() {
		//TODO: if not ornamented, count how many terms have degree at least one.
		//If more than one, forget about it.
		//If ornamented, if any term other than the active term has degree greater than one, forget about it.
		return null;
	}
	
	public TermfigurationSequence toTermfigurationSequence() {
		return this;
	}
	
	public List<Term> toTermListAt(int n) {
		List<Term> tl = new ArrayList<Term>();
		for (int i = 0; i < _tList.size(); i++) {
			try {tl.add(_tList.get(i).toTermAt(n));}
			catch (Exception e) {System.out.println(e.getMessage()); return null;}
		}
		return tl;
	}
	
	public CondensedConfiguration toCondensedConfigurationAt(int n) throws Exception {
		if (!_ornamented) throw new Exception (
				"In toCondensedConfigurationAt("+n+"), cannot convert unornamented TermfigurationSequence to CondensedConfiguration.");
		return new CondensedConfiguration(toTermListAt(n), Tools.evalAt(getIndex(), n), getState());
	}
}
