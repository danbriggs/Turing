package machine;

public interface TapeLike {
	public int[] getTape();
	public int getIndex();
	public boolean onLeft();
	public boolean onRight();
	//WARNING: onLeft() and onRight()
	//will have different meanings for different implementations of TapeLike.
	//It may mean the left (resp. right) of the stretch ever accessed,
	//or it may mean the left (resp. right) of the current nonzero portion.
	public void setIndex(int index) throws Exception;
	public String toString();
	public void printTape();
	public void printTrim();
	public int getSymbol();
	public void replace(int symbol);
	public void go(int direction) throws Exception;
}
