package machine;

public class Term {
	private int[] _base;
	private int _exponent;
	//New code 7/25/20: .clone()
	public Term(int[] base, int exponent) {_base=base.clone();_exponent=exponent;}
	public int[] getBase() {return _base;}
	public int getExponent() {return _exponent;}
	public int length() {return _base.length*_exponent;}
	public void setBase(int[] base) {_base=base;}
	public void setExponent(int exponent) {_exponent=exponent;}
	public Integer[] toArray() {
		Integer[] ret = new Integer[_base.length*_exponent];
		int i=0;
		for (int j=0; j<_exponent; j++)
			for (int k=0; k<_base.length; k++) {
				ret[i]=_base[k];
				i++;
			}
		return ret;
	}
	/**As above, but using the primitive.*/
	public int[] toIntArray() {
		int[] ret = new int[_base.length*_exponent];
		int i=0;
		for (int j=0; j<_exponent; j++)
			for (int k=0; k<_base.length; k++) {
				ret[i]=_base[k];
				i++;
			}
		return ret;
	}
	public Tape toTape() throws Exception {
		return new Tape(toArray());//Automatically puts head at 0
	}
	public String toString() {
		StringBuffer bs = new StringBuffer();
		bs.append('(');
		for (int i=0; i<_base.length; i++) bs.append(_base[i]);
		bs.append(")^");
		bs.append(_exponent);
		return bs.toString();
	}
	public boolean equals(Term t) {
		return Tools.areIdentical(_base, t.getBase()) && _exponent==t.getExponent();
	}
	public Term reverse() {
		return new Term(Tools.reverse(_base), _exponent);
	}
}