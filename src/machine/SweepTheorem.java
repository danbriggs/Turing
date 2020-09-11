package machine;

public class SweepTheorem {
	static final int L=-1, R=1;
	static final boolean LOUD = false;
	static final boolean FIVE_LINE = false;
	private boolean _proved;
	private boolean _disproved; //No use so far
	private int _initialDirection;
	//R for SweepTheorems beginning going left to right, L for vice versa.
	//Note this is opposite the "handedness" of Lemmas.
	private Machine _m;
	private Lemma _lem1, _lem2;
	private ExtendedTermfiguration _a, _b; //First & second halves
	int _numSweeps;
	private int[] _numSteps;
	
	public boolean isProved() {return _proved;}
	public boolean isDisproved() {return _disproved;}
	public int getInitialDireciton() {return _initialDirection;}
	/**Important not to return private Object instance variables through the API
	 * to ensure that components of the Lemma cannot be modified using methods
	 * that make them mutable.*/
	public Machine getMachine() {return new Machine(_m);}
	public int[] getNumSteps() {return _numSteps.clone();}
	
	/**Takes two Lemmas, two etfs, and a step number.
	* (Checks that both Lemmas are about the same Machine m, and that they were both proved.)
	* Verifies that a blank StepConfiguration sc acted on by m for stepNum steps
	* is equal to a evaluated at 0 when both are trimmed.
	* Attempts to do 1 lap and verify that sc goes from a using lem1 to b,
	* then from b using lem2 to a's successor. 
	* Thus m would go on forever when run on a blank tape.*/
	public SweepTheorem(Lemma lem1, Lemma lem2, ExtendedTermfiguration a, ExtendedTermfiguration b, int stepNum) {
		
	}
	
	

}
