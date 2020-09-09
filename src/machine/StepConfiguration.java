package machine;

/**Keeps track of how many steps have passed.*/
public class StepConfiguration extends Configuration {
	private int _numSteps;
	
	public StepConfiguration(int[] tape) throws Exception {
		super(tape);
		_numSteps = 0;
	}

	public StepConfiguration(int[] tape, int index) throws Exception {
		super(tape, index);
		_numSteps = 0;
	}

	public StepConfiguration(int[] tape, int index, int state) throws Exception {
		super(tape, index, state);
		_numSteps = 0;
	}

	public StepConfiguration(int[] tape, int index, int state, int numSteps) throws Exception {
		super(tape, index, state);
		_numSteps = numSteps;
	}

	public StepConfiguration(String s) throws Exception {
		super(s);
		_numSteps = 0;
	}

	public StepConfiguration(Tape t, int state) {
		super(t, state);
		_numSteps = 0;
	}

	public StepConfiguration(Tape t) {
		super(t);
		_numSteps = 0;
	}
	
	public void go(int direction) throws Exception {
		super.go(direction);
		_numSteps+=1;
	}

	public StepConfiguration copy() throws Exception {return new StepConfiguration(getTape().clone(),getIndex(),getState(),_numSteps);}
	
	public int getNumSteps() {return _numSteps;}
	
	public String toString() {return _numSteps + " " + super.toString();}
	
	public String getTrimAsString() {return _numSteps + " " + super.getTrimAsString();}
	
	public StepConfiguration subcon(int begin, int sup) {
		Configuration c = super.subcon(begin, sup);
		try {return new StepConfiguration(c.getTape(), c.getIndex(), c.getState(), _numSteps);}
		catch (Exception e) {return null;}
	}

	public StepConfiguration trimmed() {
		return subcon(left(), right()+1);
	}
	
	public void printTrim() {System.out.println(getTrimAsString());}
	
	public void setData(StepConfiguration sc) {
		_numSteps = sc.getNumSteps();
		setState(sc.getState());
		setIndex(sc.getIndex());
		setTape(sc.getTape());
	}
}
