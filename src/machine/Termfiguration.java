package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Termfiguration implements TermfigurationLike {
	//Represents a term with a fixed sequence of bits as base and polynomial expression in a single variable n as exponent.
	//This notation represents the base being repeated a given expression number of times.
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
	public int[] getIndex() {return _index;}
	public int getState() throws Exception {
		if (!_ornamented) throw new Exception("Cannot get state of unornamented Termfiguration");
		return _state;
	}
	public void setBase(int[] base) {_base=base;}
	public void setExponent(int[] exponent) {_exponent=exponent;}
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
	
	public Termfiguration successor() throws Exception {
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
	
	public TermfigurationSequence toTermfigurationSequence() {
		List<Termfiguration> ts = new ArrayList<Termfiguration>();
		ts.add(this);
		return new TermfigurationSequence(ts);
	}
}
