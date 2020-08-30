package machine;

import java.util.Arrays;

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
	public Tape(int runLength) {
		if (runLength<0) {
			System.out.println("Cannot create Tape for run of length "+runLength+"<0");
			return;
		}
		this.setTape(new int[2*runLength+1]);
		this.setIndex(runLength);
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
	/**s should have exactly one o or i in it, denoting tape head at 0 or 1.*/
	public Tape(String s) throws Exception {
		
		int lengthOfString  = s.length();
		
		boolean headFound = false;
		for (int i=0; i<lengthOfString; i++) {
			if (s.charAt(i)=='o'||s.charAt(i)=='i') {
				if (headFound) throw new Exception("In stringToTape(): Multiple tape heads found at indices "+_index+" and "+i+" in string s="+s);
				headFound = true;
			}
		}
		if (!headFound) throw new Exception("No tape head found in string s="+s);
		
		int countOs       = s.length() - s.replace("O", "").length();
		int countPercents = s.length() - s.replace("%", "").length();
		int countDollars  = s.length() - s.replace("$", "").length();
		int totalShift = countOs*99 + countPercents*9999 + countDollars*999999;
		int lengthOfTape  = lengthOfString + totalShift;
		//Special characters used to represent 100, 10000, and 1000000 0s, respectively
		
		_tape = new int[lengthOfTape];
		for (int i=0, j=0; i<lengthOfString; i++) {
			char c=s.charAt(i);
			if (c=='0') j+=1;
			else if (c=='o') {_index=j; j+=1;}
			else if (c=='1') {_tape[j]=1; j+=1;}
			else if (c=='i') {_tape[j]=1; _index = j; j+=1;}
			else if (c=='O') j += 100;
			else if (c=='%') j += 10000;
			else if (c=='$') j += 1000000;
			else throw new Exception("Unrecognized character "+c+" at position "+i+" in string "+s);
		}
	}
	/**For this one use just 0s and 1s in the string.*/
	public Tape(String s, int index) throws Exception {
		this(
				s.substring(0,index) +
				s.substring(index,index+1)
				.replace('0', 'o')
				.replace('1', 'i')
				+s.substring(index+1));
	}
	public Tape(Tape t) {
		_tape = t.getTape().clone();
		_index = t.getIndex();
	}
	public int[] getTape() {return _tape;}
	public int length() {return _tape.length;}
	public int getIndex() {return _index;}
	public int getNormalizedIndex() {return _index;}
	public boolean onLeft() {return nearLeft(0);}
	public boolean onRight() {return nearRight(0);}
	/**Returns whether the tape head is within dist of the left  edge of the 1 bits.*/
	public boolean nearLeft(int dist) {
		for (int i=_index-1-dist; i>=0; i--) {
			if (_tape[i]!=0) return false;
		}
		return true;
	}
	/**Returns whether the tape head is within dist of the right edge of the 1 bits.*/
	public boolean nearRight(int dist) {
		for (int i=_index+1+dist; i<_tape.length; i++) {
			if (_tape[i]!=0) return false;
		}
		return true;
	}
	/**Returns whether _index is at an extreme possible value.*/
	public boolean atExtreme() {
		return (_index==0||_index==_tape.length-1);
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
		int i=0;
		while (_tape[i]==0&&i<_index) i++;
		int j=_tape.length-1;
		while (_tape[j]==0&&j>_index) j--;
		StringBuilder sb = new StringBuilder(_tape.length);
		if (_index==-1) sb.append('<');
		for (int k=i; k<=j; k++) {
			if (k==_index) sb.append(toLetter(_tape[k]));
			else sb.append(_tape[k]);
		}
		if (_index==_tape.length) sb.append('>'); 
		return sb.toString();
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
	public boolean equals(Tape t) {
		if (_index != t.getIndex()) return false;
		if (_tape.length != t.length()) return false;
		int[] otherTape = t.getTape();
		for (int i=0; i<_tape.length; i++) {
			if (_tape[i]!=otherTape[i]) return false;
		}
		return true;
	}
	
	/**Returns the subarray from the index to the outermost 1 bit out from there, inclusive.*/
	public int[] tail(int dir) {
		if (dir == 1) {
			int j =_tape.length-1;
			while (_tape[j]==0&&j>_index) j--;
			return Arrays.copyOfRange(_tape, _index, j + 1);
		}
		else if (dir == -1) {
			int i = 0;
			while (_tape[i]==0&&i<_index) i++;
			return Arrays.copyOfRange(_tape, i, _index + 1);			
		}
		return null;
	}
	
	/**Returns a tape consisting of everything from begin up to but not including sup.*/
	public Tape subtape(int begin, int sup) {
		int[] arr = Arrays.copyOfRange(getTape(), begin, sup);
		try {
			return new Tape(arr, _index - begin);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**Returns a tape consisting of everything from begin up to but not including sup
	 * with new index newIndex.*/
	public Tape subtape(int begin, int sup, int newIndex) {
		int[] arr = Arrays.copyOfRange(getTape(), begin, sup);
		try {
			return new Tape(arr, newIndex);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**Returns whether the tape matches the array pattern
	   going in the direction direction from the tape head.
	   Returns false if it goes out of bounds.*/
	public boolean matches(int[] pattern, int direction) {
		if (direction!=1 && direction!=-1) return false;
		int beginForPattern = 0;
		if (direction==-1) beginForPattern = pattern.length - 1;
		try {
			for (int i=0; i<pattern.length; i++) {
				if (_tape[_index+i*direction]!=pattern[beginForPattern+i*direction]) return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
