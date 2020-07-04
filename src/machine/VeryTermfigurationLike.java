package machine;

/**For being either a Termfiguration or an ExtendedTermfiguration, but not a TermfigurationSequence.
 * Any methods that are shared by the first two but not the third should go here.*/
public abstract class VeryTermfigurationLike implements TermfigurationLike {
	
	public VeryTermfigurationLike toVeryTermfigurationLike() {
		return this;
	}
	
	@Override
	public Termfiguration toTermfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtendedTermfiguration toExtendedTermfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TermfigurationSequence toTermfigurationSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOrnamented() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deOrnament() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getExponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getIndex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getState() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] evalAt(int n) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Term toTermAt(int n) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Configuration toConfigurationAt(int n) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CondensedConfiguration toCondensedConfigurationAt(int n) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TermfigurationLike successor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] lastBit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] length() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TermfigurationLike deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(TermfigurationLike tl) {
		// TODO Auto-generated method stub
		return false;
	}

}
