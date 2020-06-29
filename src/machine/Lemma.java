package machine;

public class Lemma {
	private boolean _proved;
	private boolean _disproved;
	private int _handedness;
	//-1 for Lemmas concerning a tape head starting at the leftmost bit of a Termfiguration,
	//+1 for Lemmas concerning a tape head starting at the rightmost bit, and 0 otherwise.
	private Machine _m;
	private TermfigurationLike _a; //source
	private TermfigurationLike _b; //target
	private int[] _numSteps;
	
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
	}
	public Lemma(Machine m, Termfiguration a, Termfiguration b, int[] numSteps) throws Exception {
		//Represents the lemma that the termfiguration a results in the termfiguration b after the polynomial numSteps steps.
		//Note that numSteps is an array consisting of the coefficients of a polynomial in a single variable n.
		this();
		_m = new Machine(m);
		_a = new Termfiguration(a);
		_b = new Termfiguration(b);
		_numSteps = numSteps.clone();
		if (Tools.equal(a.getIndex(),new int[] {0})) _handedness = -1;
		if (Tools.equal(a.getIndex(),a.lastBit())) _handedness = 1;
		if (!a.isOrnamented()) throw new Exception("Cannot construct lemma on unornamented Termfiguration a="+a.toString());
		if (!b.isOrnamented()) throw new Exception("Cannot construct lemma on unornamented Termfiguration b="+b.toString());
		if (a.getState()>=m.numStates())
			throw new Exception("Termfiguration a has state "+a.getState()+" out of bounds for Machine with "+m.numStates()+" states");
		if (b.getState()>=m.numStates())
			throw new Exception("Termfiguration b has state "+b.getState()+" out of bounds for Machine with "+m.numStates()+" states");
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
	private boolean linearInduction(Machine m, Termfiguration a, Termfiguration b, int[] numSteps) throws Exception {
		if (a.getExponent().length!=2)
			throw new Exception("Invalid Termfiguration a for linear induction: exponent should be of length 2 but is length "+a.getExponent().length);
		if (b.getExponent().length!=2)
			throw new Exception("Invalid Termfiguration b for linear induction: exponent should be of length 2 but is length "+b.getExponent().length);
		if (a.getExponent()[1]<=0)
			throw new Exception("Invalid linear coefficient for Termfiguration a's exponent: should be positive but is "+a.getExponent()[1]);
		if (b.getExponent()[1]<=0)
			throw new Exception("Invalid linear coefficient for Termfiguration b's exponent: should be positive but is "+b.getExponent()[1]);
		if (!Tools.equal(Tools.multiply(a.getBase().length, a.getExponent()),Tools.multiply(b.getBase().length, b.getExponent())))
			throw new Exception("Termfiguration length mismatch: a="+a+", b="+b);
		if (numSteps.length!=2)
			throw new Exception("Invalid polynomial for linear induction: numSteps should be of length 2 but is length "+numSteps.length);
		//In this case numsteps is a linear expression, and so a straightforward linear induction should suffice.
		//We should really try a few cases first, though.
		System.out.println("Attempting linear induction.");
		System.out.println("Termfiguration a: "+a.toString());
		System.out.println("Termfiguration b: "+b.toString());
		System.out.println("int[] numsteps as polynomial: "+Tools.toPolynomialString(numSteps, 'n'));
		int minFora=Tools.minForNonnegative(a.getExponent());
		System.out.println("minFora: "+minFora);
		int minForb=Tools.minForNonnegative(b.getExponent());
		System.out.println("minForb: "+minForb);
		int minForNumSteps=Tools.minForNonnegative(numSteps);
		System.out.println("minForNumSteps: "+minForNumSteps);
		int minForAll = Math.max(Math.max(minFora, minForb),minForNumSteps);
		System.out.println("minForAll: "+minForAll);
		Configuration ca = a.toConfigurationAt(minForAll);
		System.out.println("Configuration ca: "+ca.toString());
		Configuration cb = b.toConfigurationAt(minForAll);
		System.out.println("Configuration cb: "+cb.toString());
		int baseNumSteps = Tools.evalAt(numSteps, minForAll);
		System.out.println("baseNumSteps: "+baseNumSteps);
		boolean baseCase = m.yields(ca, cb, baseNumSteps);
		System.out.println("baseCase: "+baseCase);
		if (!baseCase) {_disproved = true; return false;}
		Termfiguration aPrime = a.successor();
		Termfiguration bPrime = b.successor();
		int[] numStepsPrime = Tools.shiftBy(numSteps, 1);
		System.out.println("Next up: assuming "+a+" yields "+b+" in "+Tools.toPolynomialString(numSteps, 'n')+" steps,");
		System.out.println("we prove that "+aPrime+" yields "+bPrime+" in "+Tools.toPolynomialString(numStepsPrime, 'n')+" steps.");
		//At the moment, I'll be addressing only those cases where either
		//(1) the tape head starts at the left and leaves off the right side, or
		//(2) the tape head starts at the right and leaves off the left side.
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
		sb.append("Lemma: The machine\n");
		sb.append(_m);
		sb.append("transforms ");
		sb.append(_a);
		sb.append(" into ");
		sb.append(_b);
		sb.append(" in ");
		sb.append(Tools.toPolynomialString(_numSteps,'n'));
		sb.append(" steps: ");
		if (_proved) sb.append("Proved.");
		else if (_disproved) sb.append("Disproved.");
		else sb.append("Undecided.");
		return sb.toString();
	}
}