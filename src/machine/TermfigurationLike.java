package machine;

/**Termfigurations and TermfigurationSequences.*/
public interface TermfigurationLike {
	public boolean isOrnamented();
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
	will collapse everything together
	like evalAt() and toConfigurationAt().*/
	public Term toTermAt(int n) throws Exception;
	public Configuration toConfigurationAt(int n) throws Exception;
	public String toString();
	public TermfigurationLike successor() throws Exception;
	public int[] lastBit();
}
