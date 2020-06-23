package machine;

public class Transition {
	/**The number of symbols of this Transition.*/
	final int NUM_SYMBOLS = 2;
	final int HALT = -1;
	private int[] _toWrite;
	private int[] _toGo;
	private int[] _nextState;
	public Transition(int[] toWrite, int[] toGo, int[] nextState) throws Exception {
		if (toWrite.length!=NUM_SYMBOLS) throw new Exception("toWrite must be "+NUM_SYMBOLS+" symbols long"
				+ " but is "+toWrite.length+" symbols long");
		if (toGo.length!=NUM_SYMBOLS) throw new Exception("toGo must be "+NUM_SYMBOLS+" symbols long"
				+ " but is "+toGo.length+" symbols long");
		if (nextState.length!=NUM_SYMBOLS) throw new Exception("nextState must be "+NUM_SYMBOLS+" symbols long"
				+ " but is "+nextState.length+" symbols long");
		for (int i=0; i<NUM_SYMBOLS; i++) {
			if (toWrite[i]<0 || toWrite[i]>=NUM_SYMBOLS) throw new Exception("Invalid data in toWrite["+i+"]: "
					+ toWrite[i] + " is out of range [0,"+(NUM_SYMBOLS-1)+"]");
			if (toGo[i]!=-1 && toGo[i]!=1) throw new Exception("Invalid data in toGo["+i+"]: "
				+ toGo[i] + " should be either -1 or 1");
			if (nextState[i]<-1) throw new Exception("Invalid data in nextState["+i+"]: "
					+ nextState[i] + " should be greater than or equal to -1");
		}
		_toWrite=toWrite;
		_toGo=toGo;
		_nextState=nextState;
	}
	public Transition(int[][] all) throws Exception {
		/*if (all.length!=3) throw new Exception("2D array must be of length 3 but is of length "
				+all.length+" in initialization of Transition");*/
		this(all[0],all[1],all[2]);
	}
	public int getToWrite(int i) {return _toWrite[i];}
	/**Returns -1 or 1 according as this Transition is to the left or right upon encountering bit b.*/
	public int getToGo(int b) {return _toGo[b];}
	public int getNextState(int i) {return _nextState[i];}
	public String toString() {
		StringBuffer sb=new StringBuffer();
		for (int i=0; i<NUM_SYMBOLS; i++)
			sb.append("/"+_toWrite[i]+","+Tools.asArrow(_toGo[i])+","+Tools.asLetter(_nextState[i]));
		return sb.substring(1);
	}
	public boolean equals(Transition t) {
		if (NUM_SYMBOLS!=t.NUM_SYMBOLS) return false; //Yikes
		for (int i=0; i<NUM_SYMBOLS; i++) {
			if (getToWrite(i)!=t.getToWrite(i)) return false;
			if (getToGo(i)!=t.getToGo(i)) return false;
			if (getNextState(i)!=t.getNextState(i)) return false;
		}
		return true;
	}
}
