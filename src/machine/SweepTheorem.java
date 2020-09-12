package machine;

import java.util.Arrays;

/** Idea for Lemma base pairs that don't divide each other:
 *  make a new Lemma using a base of length their lcm!*/
public class SweepTheorem {
	static final int L=-1, R=1;
	static final int MAX_ITERS = 1000; //For walking around while in the wings
	//static final boolean LOUD = false;
	private boolean _proved;
	//private boolean _disproved; //No use so far
	private int _initialDirection;
	//R for SweepTheorems beginning going left to right, L for vice versa.
	//Note this is opposite the "handedness" of Lemmas.
	private Machine _m;
	private Lemma _lem1, _lem2;
	private ExtendedTermfiguration _a, _b; //First & second halves
	//int _numSweeps; //TODO: Implement soon!
	//private int[] _numSteps; //TODO: implement
	private int _stepNum;
	
	public boolean isProved() {return _proved;}
	//public boolean isDisproved() {return _disproved;}
	public int getInitialDirection() {return _initialDirection;}
	/**Important not to return private Object instance variables through the API
	 * to ensure that components of the Lemma cannot be modified using methods that make them mutable.*/
	public Machine getMachine() {return new Machine(_m);}
	//public int[] getNumSteps() {return _numSteps.clone();}

	/**Checks that both Lemmas are about m, that they were both proved, stepNum is nonnegative,
	 * the etfs' bases pattern after the Lemmas' sources, and the etfs' sides are opposite and match the Lemmas' handedness.
	 * Leaves Object instance variables null if any of these are not the case.
	 * Otherwise assigns the local variables to the instance variables.*/
	private SweepTheorem(Machine m, Lemma lem1, Lemma lem2, ExtendedTermfiguration a, ExtendedTermfiguration b,
			int stepNum) {
		if (!lem1.getMachine().equals(m) || !lem2.getMachine().equals(m)) {
			System.out.println("In SweepTheorem(): Lemma machine mismatch");
			return;
		}
		if (!lem1.isProved() || !lem2.isProved()) {
			System.out.println("In SweepTheorem(): Unproved Lemma");
			return;
		}
		if (stepNum < 0) {
			System.out.println("In SweepTheorem(): negative step number");
			_stepNum = stepNum;
			return;
		}
		if (!Arrays.equals(a.getBase(), lem1.getSource().getBase()) || !Arrays.equals(b.getBase(), lem2.getSource().getBase())) {
			System.out.println("In SweepTheorem(): ETF / Lemma pattern mismatch");
			return;
		}
		int side1 = a.extremeSide(), side2 = b.extremeSide();
		if (side1 * side2 != -1) {
			System.out.println("In SweepTheorem(): ETFs' extreme sides were not opposite");
			return;
		}
		if (lem1.getHandedness() != side1 || lem2.getHandedness() != side2) {
			System.out.println("In SweepTheorem(): Improper Lemma handedness");
			return;
		}
		_m = new Machine(m); _lem1 = lem1; _lem2 = lem2; _a = a.deepCopy(); _b = b.deepCopy(); _stepNum = stepNum; _initialDirection = -a.extremeSide();
	}
	
	/** Verifies that a blank StepConfiguration sc acted on by m for stepNum steps is equal to a evaluated at 0 when both are trimmed.
	 * Attempts to do 1 lap and verify that sc goes from a using lem1 to b, then from b using lem2 to a's successor. 
	 * Thus m would go on forever when run on a blank tape.
	 * Uses numStepsPassed1 & numStepsPassed2 as hints for how many steps to try doing.*/
	public SweepTheorem(Lemma lem1, Lemma lem2, ExtendedTermfiguration a, ExtendedTermfiguration b, int stepNum, int numStepsPassed1, int numStepsPassed2) {
		this(lem1.getMachine(), lem1, lem2, a, b, stepNum); //bookkeeping verifications, leaves Object instance variables null if invalid.
		int numStepsPassed = numStepsPassed1 + numStepsPassed2;
		if (_m == null) return;
		StepConfiguration sc = new StepConfiguration (new Tape(stepNum));
		for (int i = 0; i < stepNum; i++) try {_m.actOnConfig(sc);} catch (Exception e) {System.out.println("In SweepTheorem(): "+e.getMessage()); return;}
		Configuration a0 = null;
		try {a0 = a.toConfigurationAt(0);} catch (Exception e) {System.out.println("In SweepTheorem(): "+e.getMessage()); return;}
		if (!a0.trimmed().equals(sc.toConfiguration().trimmed())) {
			System.out.println("In SweepTheorem(): "+stepNum+" steps did not yield a at N=0");
			return;
		}
		ExtendedTermfiguration c = a.deepCopy();
		System.out.println("SweepTheorem() debug 1: c = "+c);
		int[] stepsAcross1 = sendAcross(c, lem1);
		System.out.println("SweepTheorem() debug 2: c = "+ c + " and stepsAcross1 = "+Tools.toPolynomialString(stepsAcross1, 'N'));
		if (stepsAcross1 == null) return;
		//Refactor and tryShiftAway(), tryShiftToward() (not written yet) as necessary
		int currStepsPassed = stepsAcross1[0]; //Assuming N=0
		while (currStepsPassed < numStepsPassed1) {
			try {
				int step = Acceleration.actForOneStep(_m, c);
				if (step != 1) break;
				currStepsPassed ++;
			} catch (Exception e) {break;}
		}
		
		//Now we attempt to repattern c after b.
		System.out.println("Comparing the following two ETFs.");
		boolean wingMatched = c.matchWingSize(b);
		if (!wingMatched) return;
		c.swallowAll(c.side() * -1);
		b.swallowAll(b.side() * -1);
		System.out.println("c = "+c);
		System.out.println("b = "+b);
		boolean essentiallyEqual = false;
		try {
			essentiallyEqual = c.essentiallyEquals(b);
		} catch (Exception e1) {
			return;
		}
		System.out.println("Essentially equal: "+essentiallyEqual);
		
		int[] stepsAcross2 = sendAcross(c, lem2);
		if (stepsAcross2 == null) return;
		currStepsPassed += stepsAcross2[0];
		System.out.println("SweepTheorem() debug 3: c = "+c + " and stepsAcross2 = "+Tools.toPolynomialString(stepsAcross2, 'N'));
		//Now we bop around on the initial side using the hint numStepsPassed
		while (currStepsPassed < numStepsPassed) {
			try {
				int step = Acceleration.actForOneStep(_m, c);
				if (step != 1) break;
				currStepsPassed ++;
			} catch (Exception e) {break;}
		}
		ExtendedTermfiguration successor = a.successor();
		System.out.println("SweepTheorem() debug 4: c = " + c);
		boolean swallowingLeft = true;
		while (swallowingLeft) swallowingLeft = c.trySwallow(L);
		System.out.println("SweepTheorem() debug 5: c = " + c);
		boolean swallowingRight = true;
		while (swallowingRight) swallowingRight = c.trySwallow(R);
		System.out.println("SweepTheorem() debug 6: c = " + c);
		ExtendedTermfiguration d = c.refactored(successor.getBase().length);
		if (d != null) c = d;
		System.out.println("SweepTheorem() debug 7: comparing c = " + c);
		System.out.println("                   to a.successor() = " + successor);
		try {
			_proved = c.essentiallyEquals(successor);
		} catch (Exception e) {}
		System.out.println("SweepTheorem() debug 8: SweepTheorem proved: "+_proved);
	}
	
	/**Returns the number of steps passed as a polynomial if c successfully goes from one extreme side to the other without Exceptions.
	 * Otherwise returns null.*/
	private int[] sendAcross(ExtendedTermfiguration c, Lemma lem) {
		System.out.println("1 In SweepTheorem.sendAcross(): c = "+c);
		int initialSide = c.extremeSide();
		if (initialSide == 0) return null;
		try {
			int[] stepsPassed = new int[2];
			int numIters = 0;
			while ((c.preciseOutOfBounds() || c.on(initialSide) && c.getState() != lem.getSource().getState()) && numIters < MAX_ITERS) {
				int step = Acceleration.actForOneStep(_m, c);
				System.out.println("2 In SweepTheorem.sendAcross(): c = "+c);
				if (step != 1) return null;
				numIters ++;
			}
			if (c.onLeft() || c.onRight()) stepsPassed = Acceleration.act(c, lem); //N.B. left or right of the *Term*
			else throw new Exception("ETF c was not at initial side of Term");
			System.out.println("3 In SweepTheorem.sendAcross(): c = "+c);
			while (c.preciseOutOfBounds() && c.extremeSide() == 0 && numIters < MAX_ITERS) {
				//TODO: make the checking faster
				int step = Acceleration.actForOneStep(_m, c);
				System.out.println("4 In SweepTheorem.sendAcross(): c = "+c);
				if (step != 1) return null;
				numIters ++;
			}
			System.out.println("5 In SweepTheorem.sendAcross(): c = "+c);
			if (c.extremeSide() != initialSide * -1) return null;
			stepsPassed = Tools.add(stepsPassed, new int[] {numIters, 0});
			return stepsPassed;
		} catch(Exception e) {
			System.out.println("in SweepTheorem.sendAcross(): "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
