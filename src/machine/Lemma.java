package machine;

public class Lemma {
	static final boolean LOUD = false;
	static final boolean FIVE_LINE = false;
	private boolean _proved;
	private boolean _disproved;
	private int _handedness;
	//-1 for Lemmas concerning a tape head starting at the leftmost bit of a Termfiguration,
	//+1 for Lemmas concerning a tape head starting at the rightmost bit, and 0 otherwise.
	private Machine _m;
	private TermfigurationLike _a; //source
	private TermfigurationLike _b; //target
	private int[] _numSteps;
	private int _minN; //I don't know why I didn't have this before
	
	public boolean isProved() {return _proved;}
	public boolean isDisproved() {return _disproved;}
	public int getHandedness() {return _handedness;}
	/**Important not to return private Object instance variables through the API
	 * to ensure that components of the Lemma cannot be modified using methods
	 * that make them mutable.*/
	public Machine getMachine() {return new Machine(_m);}
	public TermfigurationLike getSource() {return _a.deepCopy();}
	public TermfigurationLike getTarget() {return _b.deepCopy();}
	public int[] getNumSteps() {return _numSteps.clone();}
	
	private Lemma() {
		_proved = false;
		_disproved = false;
		_handedness = 0;
		_minN = 0;
	}
	
	public Lemma(Machine m, VeryTermfigurationLike a, VeryTermfigurationLike b, int[] numSteps) throws Exception {
		//Represents the lemma that the termfiguration a results in the termfiguration b after the polynomial numSteps steps.
		//Note that numSteps is an array consisting of the coefficients of a polynomial in a single variable n.
		this();
		_m = new Machine(m);
		_a = a.deepCopy();
		_b = b.deepCopy();
		_numSteps = numSteps.clone();
		if (Tools.equal(a.getIndex(),new int[] {0})) _handedness = -1;
		if (Tools.equal(a.getIndex(),a.lastBit())) _handedness = 1;
		if (!a.isOrnamented()) throw new Exception("Cannot construct lemma on unornamented VeryTermfigurationLike a="+a.toString());
		if (!b.isOrnamented()) throw new Exception("Cannot construct lemma on unornamented VeryTermfigurationLike b="+b.toString());
		if (a.getState()>=m.numStates())
			throw new Exception("VeryTermfigurationLike a has state "+a.getState()+" out of bounds for Machine with "+m.numStates()+" states");
		if (b.getState()>=m.numStates())
			throw new Exception("VeryTermfigurationLike b has state "+b.getState()+" out of bounds for Machine with "+m.numStates()+" states");
		if (numSteps.length==1) {
			return; //TODO: for consts
		}
		if (numSteps.length==2) {
			try {linearInduction(m, a, b, numSteps);}
			catch (Exception e) {
				System.out.println("Linear induction exception: "+e.getMessage());
				System.out.println("Perhaps the lemma is not true?");
			}
		}
	}
	
	public Lemma(Machine m, TermfigurationSequence a, TermfigurationSequence b, int[] numSteps) throws Exception {
		this();
	}
	
	private boolean linearInduction(Machine m, VeryTermfigurationLike a, VeryTermfigurationLike b, int[] numSteps) throws Exception {
		if (a.getExponent().length!=2)
			throw new Exception("Invalid VeryTermfigurationLike a for linear induction: exponent should be of length 2 but is length "+a.getExponent().length);
		if (b.getExponent().length!=2)
			throw new Exception("Invalid VeryTermfigurationLike b for linear induction: exponent should be of length 2 but is length "+b.getExponent().length);
		if (a.getExponent()[1]<=0)
			throw new Exception("Invalid linear coefficient for VeryTermfigurationLike a's exponent: should be positive but is "+a.getExponent()[1]);
		if (b.getExponent()[1]<=0)
			throw new Exception("Invalid linear coefficient for VeryTermfigurationLike b's exponent: should be positive but is "+b.getExponent()[1]);
		if (!Tools.equal(a.length(),b.length()))
			throw new Exception("VeryTermfigurationLike length mismatch: a="+a+", b="+b);
		if (numSteps.length!=2)
			throw new Exception("Invalid polynomial for linear induction: numSteps should be of length 2 but is length "+numSteps.length);
		//In this case numsteps is a linear expression, and so a straightforward linear induction should suffice.
		//We should really try a few cases first, though.
		if (LOUD) System.out.println("Attempting linear induction.");
		if (LOUD) System.out.println("VeryTermfigurationLike a: "+a.toString());
		if (LOUD) System.out.println("VeryTermfigurationLike b: "+b.toString());
		if (LOUD) System.out.println("int[] numsteps as polynomial: "+Tools.toPolynomialString(numSteps, 'n'));
		int minFora=Tools.minForNonnegative(a.getExponent());
		if (LOUD) System.out.println("minFora: "+minFora);
		int minForb=Tools.minForNonnegative(b.getExponent());
		if (LOUD) System.out.println("minForb: "+minForb);
		int minForNumSteps=Tools.minForNonnegative(numSteps);
		if (LOUD) System.out.println("minForNumSteps: "+minForNumSteps);
		int minForAll = Math.max(Math.max(minFora, minForb),minForNumSteps);
		if (LOUD) System.out.println("minForAll: "+minForAll);
		_minN = minForAll;
		Configuration ca = a.toConfigurationAt(minForAll);
		if (LOUD) System.out.println("Configuration ca: "+ca.toString());
		Configuration cb = b.toConfigurationAt(minForAll);
		if (LOUD) System.out.println("Configuration cb: "+cb.toString());
		int baseNumSteps = Tools.evalAt(numSteps, minForAll);
		if (LOUD) System.out.println("baseNumSteps: "+baseNumSteps);
		boolean baseCase = m.yields(ca, cb, baseNumSteps);
		System.out.println("baseCase: "+baseCase);
		if (!baseCase) {_disproved = true; return false;}
		VeryTermfigurationLike aPrime = a.successor().toVeryTermfigurationLike();
		VeryTermfigurationLike bPrime = b.successor().toVeryTermfigurationLike();
		//TODO: Here's where I left off
		int[] numStepsPrime = Tools.shiftBy(numSteps, 1);
		System.out.println("Next up: assuming "+a+" yields "+b+" in "+Tools.toPolynomialString(numSteps, 'n')+" steps,");
		System.out.println("we prove that "+aPrime+" yields "+bPrime+" in "+Tools.toPolynomialString(numStepsPrime, 'n')+" steps.");
		//At the moment, I'll be addressing only those cases where either
		//(1) the tape head starts at the left and leaves off the right side, or
		//(2) the tape head starts at the right and leaves off the left side.
		if (a instanceof Termfiguration && b instanceof Termfiguration)
			return inductiveStepV1(m,a.toTermfiguration(),b.toTermfiguration(),numSteps);
		if (a instanceof ExtendedTermfiguration && b instanceof ExtendedTermfiguration)
			return inductiveStepV2(m,a.toExtendedTermfiguration(),b.toExtendedTermfiguration(),numSteps);
		System.out.println("In linearInduction(): type mismatch?");
		return false;
	}
	/**Code for the inductive step for Termfigurations proper.
	 * In this case we assume the index is on either the left or the right.
	 * (Code for more possibilities later?)*/
	private boolean inductiveStepV1(Machine m, Termfiguration a, Termfiguration b, int[] numSteps) throws Exception {
		int lca = a.getExponent()[1]; //linear coefficient of a's exponent
		int lcb = b.getExponent()[1]; //linear coefficient of b's exponent
		int lcn = numSteps[1]; //linear coefficient of numSteps' exponent
		if (Tools.equal(a.getIndex(),new int[] {0})) {
			if (!Tools.equal(b.getIndex(), Tools.multiply(b.getBase().length,b.getExponent())))
				throw new Exception("Since a is on the left, I can only test for b leaving from right side. Sorry");
			//Because in this case the Termfigurations' exponents are linear expressions,
			//we may assume that the amount by which aPrime's exponent is greater than a's is the linear coefficient lca of a's exponent.
			//Thus we need only show that m acting on a Configuration cr consisting of a's base repeated lca times with tape head at the start
			//and in the state in which the inductive hypothesis had the machine leaving for a duration of lcn steps
			//results in b's base repeated lcb times with tape head off the right end and in that same ending state (right-split technique),
			//or that m acting on cl equal to cr but with state a's beginning state (left-split technique)
			//results in b's base repeated lcb times with tape head off the right end and in that same beginning state.
			Configuration cr = new Configuration(Tools.iterate(a.getBase(),lca), 0, b.getState());
			Configuration br = new Configuration(Tools.iterate(b.getBase(),lcb),a.getBase().length*lca, b.getState());
			//TODO: Why does this say a.getBase().length*lca for the index?
			//Should it be b.getBase().length*lcb? Does it matter?
			try {
				if (m.yields(cr, br, lcn)) {
					//The induction has passed!
					_proved = true;
					return true;
				}
			}
			catch (ConfigurationBoundsException e) {
				throw e;
				//TODO: code for cl, bl
			}
			//Now, what do we do if the machine had to retrace steps not in the swath of the given iteration?
			//(First, note that a lemma of this form can only be true if n=minFora makes a's exponent strictly *positive*.)
			//In this case, yields() will have thrown a "configuration dead" exception since m was trying to act outside cr's bounds.
			//Hopefully, writing code for both the left-split and the right-split technique
			//will have addressed this here and in the "else if" clause...
		}
		else if (Tools.equal(b.getIndex(),new int[] {-1})) {
			if (!Tools.equal(a.getIndex(),a.lastBit()))
				throw new Exception("Since b leaves off the left, I can only test for a starting on right side. Sorry");
			Configuration cl = new Configuration(Tools.iterate(a.getBase(),lca),a.getBase().length*lca-1, b.getState());
			Configuration bl = new Configuration(Tools.iterate(b.getBase(),lcb),-1, b.getState());
			if (m.yields(cl, bl, lcn)) {
				//The induction has passed!
				_proved = true;
				return true;
			}
		}
		return true;
	}
	
	/**Code for the inductive step for ExtendedTermfigurations.
	 * At the moment, requires that the tape head be in the wings rather than the body.
	 * Perhaps I'll add code for the situation when the tape head is in the body later.
	 * Example from HNR#4:
	 * 98518 E ...0111o...
	 * 98519 B ...011i1...
	 * 98520 E ...01i01...
	 * 98521 D ...0i001...
	 * 98522 A ...01o01...
	 * 98523 C ...0i101...
	 * 98524 C ...o1101...
	 * 98525 D ...1i101...
	 * 98526 A ...11i01...
	 * 98527 D ...110o1...
	 * 98528 E ...11o11...
	 * The Lemma we'd like to prove is
	 * (01)^(N)11o E yields
	 * 11o(11)^(N) E in 10N steps.
	 * So the inductive step is to assume this and use it to show 
	 * (01)^(N+1)11o E yields
	 * 11o(11)^(N+1) E in 10N+10 steps.
	 * So, we can apply the inductive hypothesis by splitting off an 01 to the opposite side
	 * because (imporantly) the opposite wing was empty.
	 * (If the opposite wing had contents, the form would be inappropriate for applying the inductive hypothesis directly.)
	 * So in the following code we'll assume
	 * (1) the tape head is in one wing
	 * (2) the opposite wing is empty.
	 * Note: so far, inductiveStepV2 is only capable of working with
	 * ExtendedTermfigurations with an empty left wing.
	 * We basically have to copy the code over carefully to make them work with the opposite orientation.*/
	private boolean inductiveStepV2(Machine m, ExtendedTermfiguration a, ExtendedTermfiguration b, int[] numSteps) throws Exception {
		int lca = a.getExponent()[1]; //linear coefficient of a's exponent
		int lcb = b.getExponent()[1]; //linear coefficient of b's exponent
		int lcn = numSteps[1]; //linear coefficient of numSteps' exponent
		if (!a.hasLeft()) { //Ex: (01)^(N)11o
			//We should attach lca copies of a's base to the left of b's left wing and run it lcn steps Ex: 01|11o
			int[] bIndex = Tools.trimEnd(b.getIndex());
			if (bIndex.length > 1) throw new Exception ("Because b's index is nonconstant, I can't figure out what to do");
			int bIndex0;
			if (bIndex.length == 1) bIndex0 = bIndex[0];
			else bIndex0 = 0;
			if (bIndex0 >= b.getLeft().length) throw new Exception ("Because b's index puts it off b's left wing, I can't know what to do");
			int[] aBase = Tools.iterate(a.getBase(),lca);
			Configuration cr = new Configuration(Tools.concatenate(aBase, b.getLeft()), aBase.length+bIndex0, b.getState());
			//Now we run m for lcn steps on this configuration and see if it results in b's left wing
			//concatenated by b's base times lcb with the tape head in the same place it would be in b
			int[] bLeft = b.getLeft();
			int[] bBase = Tools.iterate(b.getBase(),lcb);
			Configuration br = new Configuration(Tools.concatenate(bLeft,bBase),bIndex0, b.getState());
			try {
				if (m.yields(cr, br, lcn)) {
					//The induction has passed!
					_proved = true;
					return true;
				}
			}
			catch (ConfigurationBoundsException e) {
				throw e;
				//TODO
			}
		}
		return false;
	}
	
	public Integer valueToUse(Term t, int side, int state) {
		//If this Lemma is applicable to the term t in state state,
		//returns what value of n to use in _a's exponent to obtain t.
		//Otherwise returns null.
		if (side!=_handedness) return null;
		try {if (state!=_a.getState()) return null;}
		catch (Exception e) {
			System.out.println("ERROR: in Lemma.valueToUse(): _a.getState(): "+e.getMessage());
			return null;
		}
		//Now we inspect t to determine whether it is _a.evalAt(n) for some value of n.
		int degreeOfExponent =_a.getExponent().length - 1;
		int leadingCoefficient = _a.getExponent()[degreeOfExponent];
		if (leadingCoefficient<0) {
			System.out.println("In Lemma.valueToUse(): not dealing with polynomials that don't increase without bound right now");
			return null; 
		}
		int n=-1;
		//If t's base and _a's base match, this is easier
		if (Tools.areIdentical(t.getBase(),_a.getBase())) {
			int m=-1; //so the while loop starts
			while (m<t.getExponent()) {
				n++;
				m=Tools.evalAt(_a.getExponent(), n);
			}
			if (m==t.getExponent()) return n;
			return null;
			//Later: make a specialized version for linear and quadratic polynomials (easy);
			//make it continue to check when polynomials are on the decrease
		}
		//TODO!
		return null;
	}
	
	public String toString(){
		if (_proved&&_disproved) return "ERROR: Can't have a Lemma proved & disproved!";
		StringBuffer sb = new StringBuffer();
		sb.append("Lemma: The machine ");
		if (FIVE_LINE) sb.append("\n");
		sb.append(_m);
		sb.append("transforms ");
		sb.append(_a);
		sb.append(" into ");
		sb.append(_b);
		sb.append(" in ");
		sb.append(Tools.toPolynomialString(_numSteps,'n'));
		sb.append(" steps for n >= " + _minN + ": ");
		if (_proved) sb.append("Proved.");
		else if (_disproved) sb.append("Disproved.");
		else sb.append("Undecided.");
		return sb.toString();
	}
	
	public int getMinN() {
		return _minN;
	}
	
	public boolean equals(Lemma lem) {
		//System.out.println("Reached Lemma.equals().");
		if (_handedness!=lem.getHandedness()) {/*System.out.println("Reason 1.");*/ return false;}
		if (_minN != lem.getMinN()) return false;
		if (!_m.equals(lem.getMachine())) {/*System.out.println("Reason 2.");*/ return false;}
		if (!_a.equals(lem.getSource())) {/*System.out.println("Reason 3.");*/ return false;}
		if (!_b.equals(lem.getTarget())) {/*System.out.println("Reason 4.");*/ return false;}
		if (!Tools.equal(_numSteps, lem.getNumSteps())) {/*System.out.println("Reason 5.");*/ return false;}
		return true;
	}
}