package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**Represents a term with a fixed sequence of bits as base and polynomial expression in a single variable n as exponent.
This notation represents the base being repeated a given expression number of times.*/
public class Termfiguration extends VeryTermfigurationLike {
	private int[] _base;
	private int[] _exponent; //coefficients of n in rising degree
	private int[] _index; //where the tape head is, as a polynomial expression in n
	private int _state;
	private boolean _ornamented; //Whether it includes a tape head index and state
	public Termfiguration(int[] base, int[] exponent) {
		_base=base;
		_exponent=exponent;
		_ornamented = false;
	}
	public Termfiguration(int[] base, int[] exponent, int[] index, int state) {
		_base=base;
		_exponent=exponent;
		_index = index;
		_state = state;
		_ornamented = true;		
	}
	
	/**Important for Lemma protection.
	 * deepCopy() calls this constructor.*/
	public Termfiguration(Termfiguration t) {
		_base = t.getBase().clone();
		_exponent = t.getExponent().clone();
		_index = t.getIndex().clone();
		try {_state = t.getState();}
		catch (Exception e) {_state = -2;}
		_ornamented = t.isOrnamented();
	}
	
	public Termfiguration(Termfiguration t, int[] index, int state) {
		if (t.isOrnamented()) System.out.println(
				"Warning: Attempt to create Termfiguration by adding index & state to already ornamented Termfiguration");
		_base = t.getBase().clone();
		_exponent = t.getExponent().clone();
		_index = index;
		_state = state;
		_ornamented = true;
	}
	public Termfiguration toTermfiguration() {return this;}
	
	/**Once this is run, one should not be able to recover the ornamentation.*/
	public void deOrnament() throws Exception{
		if (_ornamented == false) throw new Exception("Error: attempt to deornament unornamented Termfiguration");
		_ornamented = false;
	}
	
	public void enterFromSideWithState(int d, int s) {
		if (_ornamented == true) System.out.println("Error: attempt to move into ornamented Termfiguration");
		if (d>0) {
			_ornamented = true;
			_index = lastBit();
			_state = s;
		}
		else if (d<0) {
			_ornamented = true;
			_index = new int[]{0,0};
			_state = s;
		}
		else System.out.println("Error: attempt to enter into Termfiguration using direction 0.");
	}
	public boolean isOrnamented() {return _ornamented;}
	public int[] getBase() {return _base;}
	public int[] getExponent() {return _exponent;}
	public int constCoeff() {return _exponent[0];}
	public int[] getIndex() {return _index;}
	public void setIndex(int[] index) {
		_index = index;
	}
	public int getState(){
		if (!_ornamented) return -2;
		return _state;
	}
	public void setState(int state) {_state = state;}
	public void setBase(int[] base) {_base=base;}
	public void setExponent(int[] exponent) {_exponent=exponent;}
	public boolean onLeft()   {return Tools.equal(_index, new int[] {0, 0});}
	public boolean offLeft()  {return Tools.equal(_index, new int[] {-1, 0});}
	public boolean onRight()  {return Tools.equal(_index, new int[] {-1 + _base.length * _exponent[0], _base.length * _exponent[1]});}
	public boolean offRight() {return Tools.equal(_index, new int[] {_base.length * _exponent[0], _base.length * _exponent[1]});}
	public void setIndexOnLeft()   {setIndex(new int[] {0, 0});}
	public void setIndexOffLeft()  {setIndex(new int[] {-1, 0});}
	public void setIndexOnRight()  {setIndex(new int[] {-1 + _base.length * _exponent[0], _base.length * _exponent[1]});}
	public void setIndexOffRight() {setIndex(new int[] {_base.length * _exponent[0], _base.length * _exponent[1]});}
	
	public int[] evalAt(int n) throws Exception {
		int numReps = Tools.evalAt(_exponent, n);
		if (numReps<0) throw new Exception("Termfiguration "+toString()+" has exponent "+numReps+"<0 at n="+n);
		int[] retVal = new int[_base.length*numReps];
		for (int i=0; i<numReps; i++)
			for (int j=0; j<_base.length; j++) 
				retVal[i*_base.length+j]=_base[j];
		return retVal;
	}
	public Term toTermAt(int n) throws Exception {
		return new Term(getBase(), Tools.evalAt(getExponent(), n));
	}
	public Configuration toConfigurationAt(int n) throws Exception {
		if (!_ornamented) throw new Exception("In toConfigurationAt("+n+"), cannot convert unornamented Termfiguration to Configuration.");
		int[] tapeContents = evalAt(n);
		int index = Tools.evalAt(getIndex(), n);
		return new Configuration(tapeContents, index, getState());
	}
	public CondensedConfiguration toCondensedConfigurationAt (int n) throws Exception {
		if (!_ornamented) throw new Exception (
				"In toCondensedConfigurationAt("+n+"), cannot convert unornamented Termfiguration to CondensedConfiguration.");
		Term t = toTermAt(n);
		List<Term> tl = new ArrayList<Term>();
		tl.add(t);
		return new CondensedConfiguration(tl, Tools.evalAt(_index, n), _state);
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (_ornamented) sb.append(Tools.asLetter(_state)+" ");
		sb.append(toStringBuffer());
		if (_ornamented) sb.append(" "+Tools.toPolynomialString(_index, 'n'));
		return sb.toString();
	}
	
	/**Does not include state or index.*/
	public StringBuffer toStringBuffer() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		for (int i=0; i<_base.length; i++) sb.append(_base[i]);
		sb.append(")^(");
		try {sb.append(Tools.toPolynomialString(_exponent, 'n'));}
		catch (Exception e) {sb.append("ERROR");}
		sb.append(")");
		return sb;		
	}
	
	public Termfiguration successor() {
		//Returns the Termfiguration obtained by replacing n with n+1.
		int[] exponent = Tools.shiftBy(_exponent,1);
		int[] index = Tools.shiftBy(_index, 1);
		if (!_ornamented) return new Termfiguration(_base, exponent);
		return new Termfiguration(_base,exponent,index,_state);
	}
	public int[] length() {
		return Tools.multiply(getBase().length, getExponent());
	}
	
	public int[] lastBit() {
		//Returns the index of the last bit
		//as an array consisting of
		//the coefficients of a polynomial in n
		//in rising order of degree.
		return Tools.add(length(),new int[] {-1});
	}
	
	public Termfiguration deepCopy() {
		return new Termfiguration(this);
	}
	
	/**Arrays.equals for the bitstring,
	 * Tools.equal for the coefficient arrays,
	 * where leading zeros shouldn't matter.
	 * Doesn't do any fancier equality detection.*/
	public boolean equals(TermfigurationLike t) {
		if (!(t instanceof Termfiguration)) return false;
		if (_ornamented!=t.isOrnamented()) return false;
		if (!Arrays.equals(_base,t.getBase())) return false;
		if (!Tools.equal(_exponent, t.getExponent())) return false;
		if (!_ornamented) return true;
		if (!Tools.equal(_index, t.getIndex())) return false;
		try {if (_state!=t.getState()) return false;}
		catch (Exception e) {System.out.println("In Termfiguration.equals: "+e); return false;}
		return true;
	}
	
	public ExtendedTermfiguration toExtendedTermfiguration() {
		return new ExtendedTermfiguration(this);
	}
	
	public TermfigurationSequence toTermfigurationSequence() {
		List<Termfiguration> ts = new ArrayList<Termfiguration>();
		ts.add(this);
		return new TermfigurationSequence(ts);
	}
	
	/**Returns whether the array _index is within the bounds of
	 * the length as indicated by the arrays _base and _exponent.
	 * Throws an exception if it can't figure it out,
	 * it's indeterminate, or it's ambiguous which side it's on.
	 * Useful only when part of an ExtendedTermfiguration.
	 * We may have to take successors before using this function
	 * so that indices are never negative for a nonnegative n.*/
	public boolean inBounds() throws Exception{
		if (_base.length < 1) throw new Exception("in inBounds(): _base.length < 1");
		if (_exponent.length > 2) throw new Exception("In inBounds(): Haven't written code for nonlinear Termfiguration exponents yet.");
		if (_index.length    > 2) throw new Exception("In inBounds(): Haven't written code for nonlinear Termfiguration indices yet.");
		int[] compExponent = new int[2];
		int[] compIndex = new int[2];
		for (int i=0; i<_exponent.length; i++) compExponent[i]=_exponent[i];
		for (int i=0; i<_index.length; i++) compIndex[i] = _index[i];
		if (compExponent[1] < 0) throw new Exception("In inBounds(): Haven't written code for exponents of decreasing size yet.");
		if (compExponent[0] < 0) throw new Exception("In inBounds(): Haven't written code for exponents that start negative yet.");
		int a = compIndex[1], b = compIndex[0], c = compExponent[1] * _base.length, d = compExponent[0] * _base.length;
		//We want precisely to see if 0<=ax+b<cx+d for all x>=0.
		if (a <= 0 && b < 0) return false;
		if (a > 0 && b < 0) throw new Exception("In inBounds(): ambiguous side");
		if (b < d && b >= 0) {
			if (a <= c && a >= 0) return true;
			throw new Exception("In inBounds(): ambiguous");
		}
		if (b>=d) {
			if (a>=c) return false;
			throw new Exception("In inBounds(): ambiguous");
		}
		throw new Exception("Unreachable code");
	}
	
	/**Returns true only if the bit where the tape head is
	 * would be known precisely if this Termfiguration were incorporated in an ExtendedTermfiguration,
	 * and this place is out of bounds of the Term.
	 * We should really assume that the linear coefficient of _exponent is positive.*/
	public boolean preciseOutOfBounds() throws Exception{
		if (_base.length < 1) throw new Exception("in preciseOutOfBounds(): _base.length < 1");
		if (_exponent.length > 2) throw new Exception("In preciseOutOfBounds(): Haven't written code for nonlinear Termfiguration exponents yet.");
		if (_index.length    > 2) throw new Exception("In preciseOutOfBounds(): Haven't written code for nonlinear Termfiguration indices yet.");
		int[] compExponent = new int[2];
		int[] compIndex = new int[2];
		for (int i=0; i<_exponent.length; i++) compExponent[i]=_exponent[i];
		for (int i=0; i<_index.length; i++) compIndex[i] = _index[i];
		if (compExponent[1] < 0) throw new Exception("In preciseOutOfBounds(): Haven't written code for exponents of decreasing size yet.");
		if (compExponent[0] < 0) throw new Exception("In preciseOutOfBounds(): Haven't written code for exponents that start negative yet.");
		int a = compIndex[1], b = compIndex[0], c = compExponent[1] * _base.length, d = compExponent[0] * _base.length;
		//We want precisely to see if ax+b<0 and constant or (ax+b)-(cx+d)>=0 and constant.
		if (a==0 && b < 0) return true;
		if (a==c && b >= d) return true;
		return false;
	}
	
	/**If the term's base is something repeated,
	 * replace the base with that, and multiply the exponent by the factor.*/
	public void condense() {
		int[] base = this.getBase();
		int len = base.length;
		//Shortest to longest, for the most condensation possible
		for (int i=1; i<=len/2; i++) {
			if (len % i != 0) continue;
			if (Tools.isRepeatOfFirst(base, i)) {
				_base = Arrays.copyOfRange(_base, 0, i);
				int numRepeats = len/i;
				for (int k=0; k<_exponent.length; k++)
					_exponent[k] *= numRepeats;
				return;
			}
		}
	}
	
	Termfiguration refactoredShorter(int newBaselen) {
		int[] currBase = getBase();
		int currBaselen = currBase.length;
		if (newBaselen > currBaselen) return null;
		if (newBaselen == currBaselen) return deepCopy();
		if (currBaselen % newBaselen != 0) return null;
		int factor = currBaselen / newBaselen;
		if (!Tools.isRepeatOfFirst(currBase, newBaselen)) return null;
		int[] currExponent = getExponent();
		int explen = currExponent.length;
		int[] newExponent = new int[explen];
		for (int i=0; i<explen; i++) newExponent[i] = currExponent[i] * factor;
		int[] newBase = Arrays.copyOfRange(currBase, 0, newBaselen);
		return new Termfiguration(newBase, newExponent, getIndex(), getState());
	}
	
	public boolean isLinear() {
		if (Tools.degree(_index) > 1) return false;
		if (Tools.degree(_exponent) > 1) return false;
		return true;
	}
}
