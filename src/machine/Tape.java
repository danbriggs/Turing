package machine;

public class Tape implements TapeLike{
	private int[] _tape;
	private int _index;
	public Tape() {
		_tape = new int[1];
		_index = 0;
	}
	public Tape(int[] tape) throws Exception {
		this(tape,0);
	}
	public Tape(Integer[] tape) throws Exception {
		this(tape,0);
	}
	public Tape(int[] tape, int index) throws Exception {
		if (index < -1 || index>tape.length) throw new Exception("Index "+index+" out of range for tape of length "+tape.length);
		this.setTape(tape);
		this.setIndex(index);
	}
	public Tape(Integer[] tape, int index) throws Exception {
		if (index < -1 || index>tape.length) throw new Exception("Index "+index+" out of range for tape of length "+tape.length);
		this.setTape(tape);
		this.setIndex(index);
	}
	public Tape(String s) throws Exception {
		//s should have exactly one o or i in it, denoting tape head at 0 or 1.
		int lengthOfTape = s.length();
		boolean headFound = false;
		for (int i=0; i<lengthOfTape; i++) {
			if (s.charAt(i)=='o'||s.charAt(i)=='i') {
				if (headFound) throw new Exception("In stringToTape(): Multiple tape heads found at indices "+_index+" and "+i+" in string s="+s);
				_index = i;
				headFound = true;
			}
		}
		if (!headFound) throw new Exception("No tape head found in string s="+s);
		_tape = new int[lengthOfTape];
		for (int i=0; i<lengthOfTape; i++) {
			char c=s.charAt(i);
			if (c=='0'||c=='o') _tape[i]=0;
			else if (c=='1'||c=='i') _tape[i]=1;
			else throw new Exception("Unrecognized character "+c+" at position "+i+" in string "+s);
		}
	}
	public int[] getTape() {return _tape;}
	public int getIndex() {return _index;}
	public boolean onLeft() {
		for (int i=_index-1; i>=0; i--) {
			if (_tape[i]!=0) return false;
		}
		return true;
	}
	public boolean onRight() {
		for (int i=_index+1; i<_tape.length; i++) {
			if (_tape[i]!=0) return false;
		}
		return true;
	}
	public void setTape(int[] tape) {this._tape=tape;}
	public void setTape(Integer[] tape) {
		_tape = new int[tape.length];
		for (int i=0;i<tape.length;i++) _tape[i]=tape[i];
	}
	public void setIndex(int index) {_index=index;}
	public String toString() {
		String s="";
		StringBuilder sb = new StringBuilder(_tape.length);
		if (_index==-1) sb.append('<');
		for (int i=0; i<_tape.length; i++) {
			if (i==_index) sb.append(toLetter(_tape[i]));
			else sb.append(_tape[i]);
		}
		if (_index==_tape.length) sb.append('>'); 
		s=sb.toString();
		return s;
	}
	public void printTape() {System.out.println(toString());}
	public String getTrimAsString() {
		String s=toString();
		int i=0;
		while (_tape[i]==0&&i<_index) i++;
		int j=_tape.length-1;
		while (_tape[j]==0&&j>_index) j--;
		return s.substring(i,j+1);
	}
	public void printTrim() {System.out.println(getTrimAsString());}
	private char toLetter(int symbol) {
		if (symbol==0) return 'o';
		if (symbol==1) return 'i';
		return 'E';
	}
	public int getSymbol() {return _tape[_index];}
	public void replace(int symbol) {_tape[_index]=symbol;}
	public void go(int direction) throws Exception {
		if (direction!=-1 && direction!=1) throw new Exception("Direction must be -1 or 1 but is "+direction);
		_index+=direction;
	}
}
