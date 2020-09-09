package machine;

public interface TapeLike {
	public int[] getTape();
	public int length();
	public int getIndex();
	public int getNormalizedIndex(); //To discount left padding if it is present
	public int left();
	public int right();
	//WARNING: left() and right()
	//has different meanings for different implementations of TapeLike.
	//For StretchTape, it means the left (resp. right) of the stretch ever accessed;
	//for Tape, it means the left (resp. right) of the current nonzero portion.
	//Similarly for the four methods below.
	public boolean onLeft();
	public boolean onRight();
	public boolean offLeft();
	public boolean offRight();
	public void setIndex(int index) throws Exception;
	public String toString();
	public String getTrimAsString();
	public void printTape();
	public void printTrim();
	public int getSymbol();
	public void replace(int symbol);
	public void go(int direction) throws Exception;
}
