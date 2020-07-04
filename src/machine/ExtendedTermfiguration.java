package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**A class for a Termfiguration extended by a constant sequence of bits on either side.
 * Important: the Termfiguration may have to have been constructed with an index beyond its bounds.*/
public class ExtendedTermfiguration extends VeryTermfigurationLike {
	
	/**What's left of _t*/
	private int[] _l;
	private Termfiguration _t;
	/**What's right of _t*/
	private int[] _r;
	
	public ExtendedTermfiguration(Termfiguration t) {
		_l = new int[0];
		_t = t;
		_r = new int[0];
	}
	
	public ExtendedTermfiguration(int[] l, Termfiguration t) {
		_l = l;
		_t = t;
		_r = new int[0];
	}
	public ExtendedTermfiguration(Termfiguration t, int[] r) {
		_l = new int[0];
		_t = t;
		_r = r;
	}

	public ExtendedTermfiguration(int[] l, Termfiguration t, int[] r) {
		_l = l;
		_t = t;
		_r = r;
	}
	
	public Termfiguration toTermfiguration() {
		return _t;
	}
	
	public TermfigurationSequence toTermfigurationSequence() {
		return new TermfigurationSequence(
				new ArrayList<Termfiguration>(
						Arrays.asList(
								new Termfiguration(_l, new int[] {1}),
								_t,
								new Termfiguration(_r, new int[] {1}))));
	}

	public boolean isOrnamented() {return _t.isOrnamented();}

	public void deOrnament() throws Exception{
		if (isOrnamented() == false) throw new Exception("Error: attempt to deornament unornamented ExtendedTermfiguration");
		_t.deOrnament();
	}
	public int[] getBase() {return _t.getBase();}
	public int[] getExponent() {return _t.getExponent();}
	/** We use the index of _t shifted by _l's length as proxy.
	 * For this to work, you must allow Termfigurations to report indices beyond their bounds.*/
	public int[] getIndex() {return Tools.add(_t.getIndex(),new int[] {_l.length});}
	public int getState() throws Exception {return _t.getState();}
	public int[] getLeft() {return _l;}
	public Termfiguration getTerm() {return _t;}
	public int[] getRight() {return _r;}
	public boolean hasLeft() {return _l.length > 0;}
	public boolean hasRight() {return _r.length > 0;}
	
	public int[] evalAt(int n) throws Exception {
		int[] term = _t.evalAt(n);
		int len = _l.length + term.length + _r.length;
		int[] ret = new int[len];
		int i, j, k;
		for (i = 0; i < _l.length; i++) ret[i] = _l[i];
		for (j = 0; j < term.length; j++) ret[i + j] = term[j];
		for (k = 0; k < _r.length; k++) ret[i + j + k] = _r[k];
		return ret;
	}
	
	public Term toTermAt(int n) throws Exception {
		return _t.toTermAt(n);
	}
	
	public Configuration toConfigurationAt(int n) throws Exception {
		if (!isOrnamented()) throw new Exception("In toConfigurationAt("+n+"), cannot convert unornamented ExtendedTermfiguration to Configuration.");
		int[] tapeContents = evalAt(n);
		int index = Tools.evalAt(getIndex(), n);
		return new Configuration(tapeContents, index, getState());
	}
	
	public ExtendedTermfiguration successor(){
		return new ExtendedTermfiguration(_l, _t.successor(), _r);
	}
		
	public int[] length() {
		int[] winglen = new int[] {_l.length + _r.length};
		return Tools.add(_t.length(), winglen);
	}
	
	public int[] lastBit() {
		return Tools.add(length(), new int[] {-1});
	}
	
	public ExtendedTermfiguration deepCopy() {
		return new ExtendedTermfiguration(_l.clone(), _t.deepCopy(), _r.clone());
	}
	
	public boolean equals(TermfigurationLike tl) {
		if (!(tl instanceof ExtendedTermfiguration)) return false;
		ExtendedTermfiguration et = tl.toExtendedTermfiguration();
		if (!Arrays.equals(et.getLeft(), _l)) return false;
		if (!Arrays.equals(et.getRight(), _r)) return false;
		return et.getTerm().equals(_t);
	}
	
	public ExtendedTermfiguration toExtendedTermfiguration() {
		return this;
	}
	
	public CondensedConfiguration toCondensedConfigurationAt(int n) throws Exception {
		if (!isOrnamented()) throw new Exception (
				"In toCondensedConfigurationAt("+n+"), cannot convert unornamented ExtendedTermfiguration to CondensedConfiguration.");
		Term t1 = new Term(_l, 1);
		Term t2 = toTermAt(n);
		Term t3 = new Term(_r, 1);
		List<Term> tl = new ArrayList<Term>();
		tl.add(t1);
		tl.add(t2);
		tl.add(t3);
		return new CondensedConfiguration(tl, Tools.evalAt(getIndex(), n), getState());
	}
	
	/**How does a polynomial modelling a term's length as a function of a single variable
	 * compare to a polynomial modelling the index of the tape head?
	 * That's not clear in general; we only use the special type of output when it's clear.*/
	public String toString() {
		String lString, mString, rString;
		int[] coeffArray = Tools.trimEnd(getIndex());
		int indexDegree = coeffArray.length;
		if (indexDegree <= 1) {
			int constCoeff = getIndex()[0];
			if (constCoeff < _l.length) {
				try {lString = new Tape(_l, constCoeff).toString();}
				catch (Exception e) {System.out.println("Exception 1 in ExtendedTermfiguration.toString()"); return null;}
				mString = _t.toStringBuffer().toString();
				rString = Tools.toShortString(_r);
				try {return getState() + " " + lString + mString + rString;}
				catch (Exception e) {System.out.println("Exception 2 in ExtendedTermfiguration.toString()"); return null;}
			}
		}
		//TODO: array out-of-bounds error handling
		//Next if they differ only in the constant coefficient
		if (Tools.equal(
				Arrays.copyOfRange(getIndex(), 1, getIndex().length),
				Arrays.copyOfRange(_t.length(), 1, _t.length().length))) {
			if (_t.getIndex()[0] >= _t.length()[0]) {
				int rIndex = _t.getIndex()[0] - _t.length()[0];
				lString = Tools.toShortString(_l);
				mString = _t.toStringBuffer().toString();
				try {rString = new Tape(_r, rIndex).toString();}
				catch (Exception e) {System.out.println("Exception 3 in ExtendedTermfiguration.toString()"); return null;}
				try {return getState() + " " + lString + mString + rString;}
				catch (Exception e) {System.out.println("Exception 4 in ExtendedTermfiguration.toString()"); return null;}
			}
		}
		//If you're still here, then we just have a normal kind of output.
		try {
			return getState() + " " +
			Tools.toShortString(_l) +
			_t.toStringBuffer().toString() +
			Tools.toShortString(_r) +
			Tools.toPolynomialString(getIndex(), 'n');
		} catch (Exception e) {
			System.out.println("Exception 5 in ExtendedTermfiguration.toString()");
			return null;
		}
	}

}
