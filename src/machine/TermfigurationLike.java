package machine;

/**Termfigurations and TermfigurationSequences.*/
public interface TermfigurationLike {
	public Termfiguration toTermfiguration();
	public TermfigurationSequence toTermfigurationSequence();
	public boolean isOrnamented();
	public void deOrnament() throws Exception;
	/**For ornamented TermfigurationSequences,
	means the base of the active term. */
	public int[] getBase();
	/**For ornamented TermfigurationSequences,
	means the exponent of the active term. */
	public int[] getExponent();
	public int[] getIndex();
	public int getState() throws Exception;
	public int[] evalAt(int n) throws Exception;
	/**For TermfigurationSequences,
	just does the current term if ornamented,
	throws exception otherwise.*/
	public Term toTermAt(int n) throws Exception;
	public Configuration toConfigurationAt(int n) throws Exception;
	public String toString();
	public TermfigurationLike successor() throws Exception;
	public int[] lastBit();
	public int[] length();
	public TermfigurationLike deepCopy();
	public boolean equals(TermfigurationLike tl);
}
