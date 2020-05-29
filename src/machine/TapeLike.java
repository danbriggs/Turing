package machine;

public interface TapeLike {
	public int[] getTape();
	public int getIndex();
	public void setIndex(int index) throws Exception;
	public String toString();
	public void printTape();
	public int getSymbol();
	public void replace(int symbol);
	public void go(int direction) throws Exception;
}
