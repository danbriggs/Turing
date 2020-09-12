package machine;

public class Machine {
	static final boolean FIVE_LINE = false;
	static final int L = -1, R = 1;
	private Transition[] _transitions;
	private int _state;
	private int _id;//for keeping track of Skelet's IDs
	private KMachine _speedup; //For 16-bit machine analogous to this
	public Machine() {
		//For creating a null machine. Little purpose.
		_transitions = null;
		_state = 0;
		_id = 0;
	}
	public Machine(Transition[] transitions, int id) throws Exception {
		this(transitions);
		_id=id;
	}
	public Machine (Transition[] transitions) throws Exception {
		for (int i=0; i<transitions.length; i++)
			for (int j=0; j<2; j++)
				if (transitions[i].getNextState(j)>=transitions.length) throw new Exception("_nextState["+i+"] should be at most "
					+ (transitions.length-1) + " but is " + transitions[i].getNextState(j));
		_transitions = transitions;
		_state = 0;
	}
	public Machine(int[][][] all, int id) throws Exception {
		this(all);
		_id=id;
	}
	public Machine (int[][][] all) throws Exception {
		Transition[] transitions = new Transition[all.length];
		for (int i=0; i<all.length; i++) {
			transitions[i] = new Transition(all[i]);
		}
		_transitions = transitions;
		_state = 0;
	}
	public Machine (Machine m) {
		_transitions = m.getTransitions();
		_state = m.getState();
		_id = m.getID();
	}
	public void act(TapeLike tape) throws Exception {
		if (_state==-1) throw new Exception("Cannot act with machine in halt state");
		if (_state<-1||_state>=_transitions.length) throw new Exception("_state is "+_state+" but should be in range [0,"
				+ (_transitions.length-1)+"]");
		int symbol = tape.getSymbol();
		int direction = _transitions[_state].getToGo(symbol);
		int nextState = _transitions[_state].getNextState(symbol);
		tape.replace(_transitions[_state].getToWrite(symbol));
		tape.go(direction);
		_state = nextState;
	}
	public void reset() {_state=0;}
	public void actOnConfig(Configuration c) throws Exception {
		if (!c.isAlive()) {
			if (c.getState()==-1) throw new Exception("Configuration is in halt state");
			else throw new ConfigurationBoundsException ("Index "+c.getIndex()+" makes configuration of length "+c.getTape().length+" dead");
		}
		int stateHolder = _state;
		_state = c.getState();
		act(c);
		c.setState(_state);
		_state = stateHolder; //sorry
	}
		
	/**Returns the direction to go afterwards.
	 * Who knows how that'll need to be used.*/
	public int actOnTerm(Term t, int index) throws Exception {
		if (_state==-1) throw new Exception("Cannot act with machine in halt state");
		if (_state<-1||_state>=_transitions.length) throw new Exception("_state is "+_state+" but should be in range [0,"
				+ (_transitions.length-1)+"]");
		if (t.getExponent() != 1) throw new Exception("Cannot act on Term with exponent other than 1.");
		if (index < 0 || index >= t.getBase().length) throw new Exception("In actOnTerm(): index out of bounds for Term."); 
		int symbol = t.getBase()[index];
		int direction = _transitions[_state].getToGo(symbol);
		int nextState = _transitions[_state].getNextState(symbol);
		t.getBase()[index] = _transitions[_state].getToWrite(symbol);
		_state = nextState;
		return direction;
	}
	
	/**Calls etf.padLeft(), etf.padRight() as necessary.*/
	public void actOnSide(ExtendedTermfiguration etf, int side, int pos) throws Exception {
		if (side != -1 && side != 1) throw new Exception("In Machine.actOnSide(): invalid side");
		int[] array = null;
		if (side == -1) array = etf.getLeft();
		if (side ==  1) array = etf.getRight();
		int symbol = array[pos];
		int state = etf.getState();
		int direction = _transitions[state].getToGo(symbol);
		int nextState = _transitions[state].getNextState(symbol);
		array[pos] = _transitions[state].getToWrite(symbol);
		etf.getTerm().getIndex()[0] += direction;
		etf.setState(nextState);		
	}
	
	/**Gets the number of states of this Machine.*/
	public int numStates() {return _transitions.length;}
	public int getState() {return _state;}
	public void setState(int state) {_state = state;} //Do not overuse!
	/**Returns an array consisting of all the Transitions of this Machine.*/
	public Transition[] getTransitions() {return _transitions;}
	public int getID() {return _id;}
	public boolean yields(Configuration a, Configuration b, int numSteps) throws Exception {
		//Determines whether this machine acting on a results in b after exactly numSteps steps.
		//Note that the two configurations must be of the same length and considered as starting at the same position.
		Configuration c=a.copy();
		//System.out.println(a.getState());
		//System.out.println(c.getState());
		for (int i=0; i<numSteps; i++) {
			//System.out.println(c);
			actOnConfig(c);
		}
		if (c.equals(b)) return true;
		/*System.out.println("The following configurations are not equal:");
		System.out.println(c.toString());
		System.out.println(b.toString());*/
		return false;
	}
	public String toString() {
		if (_transitions==null) return "null machine";
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<_transitions.length; i++) {
			String output;
			if (FIVE_LINE) output =  Tools.asLetter(i)+": "+_transitions[i].toString()+'\n';
			else           output = _transitions[i].toShortString() + " ";
			sb.append(output);
		}
		return sb.toString();
	}
	/**Machines do *not* have to be in the same state to be equal.*/
	public boolean equals(Machine m) {
		int n = numStates();
		if (m.numStates()!=n) return false;
		for (int i=0; i<n; i++) if (!getTransitions()[i].equals(m.getTransitions()[i])) return false;
		return true;
	}
	public boolean hasSpeedUp() {return _speedup!=null;}
	public KMachine getSpeedUp() {return _speedup;}
	public void setSpeedUp(KMachine m16) {
		if (!m16.getMachine().equals(this)) {
			System.out.println("In Machine.setSpeedUp(): wrong machine");
			return;
		}
		_speedup = m16;
	}
	
	/**These only succeed if they bounce it out with no change to the bit.*/
	public void tryActOnFirstBit(Termfiguration t) throws Exception{
		if (!t.onLeft()) throw new Exception ("t is not on left");
		int symbol = t.getBase()[0];
		int state = t.getState();
		int direction = _transitions[state].getToGo(symbol);
		if (direction != L) throw new Exception("Sorry, would advance further into the base.");
		int toWrite = _transitions[state].getToWrite(symbol);
		if (toWrite != symbol) throw new Exception("Sorry, would modify the base.");
		int nextState = _transitions[state].getNextState(symbol);
		t.getIndex()[0] += direction;
		t.setState(nextState);
		//No need to write when they match
	}
	public void tryActOnLastBit(Termfiguration t) throws Exception{
		if (!t.onRight()) throw new Exception ("t is not on right");
		int symbol = t.getBase()[0];
		int state = t.getState();
		int direction = _transitions[state].getToGo(symbol);
		if (direction != R) throw new Exception("Sorry, would advance further into the base.");
		int toWrite = _transitions[state].getToWrite(symbol);
		if (toWrite != symbol) throw new Exception("Sorry, would modify the base.");
		int nextState = _transitions[state].getNextState(symbol);
		t.getIndex()[0] += direction;
		t.setState(nextState);
		//No need to write when they match
	}
}
