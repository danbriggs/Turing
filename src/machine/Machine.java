package machine;

public class Machine {
	private Transition[] _transitions;
	private int _state;
	private int _id;//for keeping track of Skelet's IDs
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
		for (int i=0; i<_transitions.length; i++) sb.append(Tools.asLetter(i)+": "+_transitions[i].toString()+'\n');
		return sb.toString();
	}
	/**Machines do *not* have to be in the same state to be equal.*/
	public boolean equals(Machine m) {
		int n = numStates();
		if (m.numStates()!=n) return false;
		for (int i=0; i<n; i++) if (!getTransitions()[i].equals(m.getTransitions()[i])) return false;
		return true;
	}
}
