package machine;

/**Class for being identical to a Tape, but with an awareness of the min & max indices ("stretch") accessed.
Tape needs to not have to update values like this, in order to run as fast as possible;
StretchTape needs to keep track of them, in order not to have to check every bit in order to be able to report them.
Warning: getIndex() will jump after the edge of the allocated region is reached.
     Use getNormalizedIndex() to make this not happen.*/
public class StretchTape implements TapeLike{
	//TODO: Consolidate the padding elements from pushing and from the constructor from a string
	//      Consider making getIndex() ignore padding
	private static int pushBlock = 10000;
	private int _totalPadding;
	private int[] _tape;
	private int _index;
	private int _min; //new
	private int _max; //new
	private boolean _justPushed; //new; about whether the tape just pushed itself beyond its bounds on the previous step
	private boolean _borked;
	public StretchTape() {
		this(500);
	}
	/**getNormalizedIndex() will report 0 at outset no matter what.*/
	public StretchTape(int index) {
		_tape = new int[2*index+1];
		_index = index;
		_min = index; //new
		_max = index; //new
		_totalPadding = index;
		_borked = false;
	}
	public StretchTape(StretchTape t) {
		_tape = t.getTape().clone();
		_index = t.getIndex();
		_min = t.getMin();
		_max = t.getMax();
		_justPushed = t.getJustPushed();
		_totalPadding = t.getTotalPadding();
		_borked = t.getBorked();
	}
	public StretchTape(int[] tape) throws Exception {
		this(tape,0);
	}
	public StretchTape(Integer[] tape) throws Exception {
		this(tape,0);
	}
	public StretchTape(int[] tape, int index) throws Exception {
		if (index < -1 || index>tape.length) throw new Exception("Index "+index+" out of range for stretch-tape of length "+tape.length);
		this.setTape(tape);
		int min;
		for (min=0; min<tape.length; min++)
			if (tape[min]!=0 || min==index) {
				_min=min;
				break;
			}
		int max;
		for (max=tape.length-1; max>=0; max--)
			if (tape[max]!=0 || max==index) {
				_max=max;
				break;
			}
		_index = index;
		_totalPadding = 0;
		_borked = false;
	}
	public StretchTape(Integer[] tape, int index) throws Exception {
		if (index < -1 || index>tape.length) throw new Exception("Index "+index+" out of range for stretch-tape of length "+tape.length);
		this.setTape(tape);
		_index = index;
		int min;
		for (min=0; min<tape.length; min++)
			if (tape[min]!=0 || min==index) {
				_min=min;
				break;
			}
		int max;
		for (max=tape.length-1; max>=0; max--)
			if (tape[max]!=0 || max==index) {
				_max=max;
				break;
			}
		_totalPadding = 0;
		_borked = false;
	}
	public StretchTape(String s) throws Exception {
		this(s,50);
	}
	public StretchTape(String s, int padding) throws Exception {
		int lengthOfString = s.length();
		if (!(lengthOfString>0)) throw new Exception("In stringToTape(): string length must be greater than zero");
		if (s.charAt(0)=='0'||s.charAt(lengthOfString-1)=='0') throw new Exception("A StretchTape initialized from a string should not have leading or trailing zeros.");
		_min=padding+0;
		_max=padding+lengthOfString-1;
		boolean headFound = false;
		for (int i=0; i<lengthOfString; i++) {
			if (s.charAt(i)=='o'||s.charAt(i)=='i') {
				if (headFound) throw new Exception("In stringToTape(): Multiple tape heads found at indices "+_index+" and "+i+" in string s="+s);
				_index = padding + i;
				headFound = true;
			}
		}
		if (!headFound) throw new Exception("No tape head found in string s="+s);
		_tape = new int[padding + lengthOfString + padding];
		for (int i=0; i<lengthOfString; i++) {
			char c=s.charAt(i);
			if (c=='0'||c=='o') _tape[padding + i]=0;
			else if (c=='1'||c=='i') _tape[padding + i]=1;
			else throw new Exception("Unrecognized character "+c+" at position "+i+" in string "+s);
		}
		_totalPadding = padding;
		_borked = false;
	}
	public int[] getTape() {return _tape;}
	public int length() {return _tape.length;}
	public int getIndex() {return _index;}
	public int getNormalizedIndex() {return _index-_totalPadding;}
	public int getMin() {return _min;}
	public int getMax() {return _max;}
	public int getRange() {return _max-_min;}
	public int getTotalPadding() {return _totalPadding;}
	public boolean onLeft() {return _index==_min;}
	public boolean onRight() {return _index==_max;}
	private void setTape(int[] tape) {this._tape=tape; _borked = true;}
	private void setTape(Integer[] tape) {
		_tape = new int[tape.length];
		for (int i=0;i<tape.length;i++) _tape[i]=tape[i];
		_borked = true;
	}
	public void setIndex(int index) throws Exception {
		if (index<_min) throw new Exception("In StretchTape.setIndex(): index "+index+" < _min "+_min);
		if (index>_max) throw new Exception("In StretchTape.setIndex(): index "+index+" > _max "+_max);
		_index=index;
		_borked = true;
	}
	public String toString() {
		String s="";
		StringBuilder sb = new StringBuilder(_tape.length);
		if (_index==-1) sb.append('<');
		for (int i=_min; i<=_max; i++) {
			if (i==_index) sb.append(toLetter(_tape[i]));
			else sb.append(_tape[i]);
		}
		if (_index==_tape.length) sb.append('>'); 
		s=sb.toString();
		return s;
	}
	public void printTape() {System.out.println(toString());}
	public void printTrim() {System.out.println(toString());}
	public String getTrimAsString() {return toString();}
	public char toLetter(int symbol) {
		if (symbol==0) return 'o';
		if (symbol==1) return 'i';
		return 'E';
	}
	public int getSymbol() {return _tape[_index];}
	public void replace(int symbol) {_tape[_index]=symbol;}
	public boolean getJustPushed() {return _justPushed;}
	public boolean getBorked() {return _borked;}
	public void go(int direction) throws Exception {
		if (direction<0) {
			_index--;
			if (_index<_min) {_min--; _justPushed = true;}
			else _justPushed = false;
			if (_index<=0) pushLeft();
			return;
		}
		if (direction>0) {
			_index++;
			if (_index>_max) {_max++; _justPushed = true;}
			else _justPushed = false;
			if (_index>=_tape.length) pushRight();
			return;
		}
		throw new Exception("In StretchTape.go(): Direction must be -1 or 1 but is "+direction);
	}
	
	private void pushLeft() {
		int[] tape2 = new int[_tape.length+pushBlock];
		for (int i=pushBlock; i<tape2.length; i++)
			tape2[i]=_tape[i-pushBlock];
		_tape=tape2;
		_index+=pushBlock;
		_min+=pushBlock;
		_max+=pushBlock;
		_totalPadding+=pushBlock;
		_borked=true;
	}
	
	private void pushRight() {
		int[] tape2 = new int[_tape.length+pushBlock];
		for (int i=0; i<_tape.length; i++)
			tape2[i]=_tape[i];
		_tape=tape2;
	}
}
