package machine;

public class Configuration extends Tape {
	private int _state;
	public Configuration(int[] tape) throws Exception {
		this(tape,0);
	}
	public Configuration(int[] tape, int index) throws Exception {
		this(tape, index, 0);
	}
	public Configuration(int[] tape, int index, int state) throws Exception {
		if (index < -1 || index>tape.length) throw new Exception("Index "+index+" out of range for configuration of length "+tape.length);
		setTape(tape);
		setIndex(index);
		setState(state);
	}
	public Configuration(String s) throws Exception {
		//s should have the state as a capital letter (A for 0 etc., and @ for -1 (halt)),
		//followed by a space, then the tape.
		super(s.substring(2));
		if (s.charAt(0)=='@') _state=-1;
		else _state = (int)(s.charAt(0)-'A');
	}
	public String toString() {
		String s = (char)(getState()+65)+" ";
		if (getIndex()==-1) s+='<';
		s+=super.toString();
		if (getIndex()==getTape().length) s+='>';
		return s;
	}
	public int getState() {return _state;}
	public void setState(int state) {_state = state;}
	public boolean isAlive() {
		if (getIndex()==-1 || getIndex()==getTape().length || _state==-1) return false;
		return true;
	}
	public boolean equals(Configuration c) {
		int[] tape1 = getTape();
		int[] tape2 = c.getTape();
		if (tape1.length!=tape2.length)return false;
		if (getIndex() != c.getIndex()) return false;
		if (getState() != c.getState()) return false;
		for (int i=0; i<tape1.length; i++) if (tape1[i]!=tape2[i]) return false;
		return true;
	}
	public Configuration copy() throws Exception {return new Configuration(getTape().clone(),getIndex(),getState());}
}
