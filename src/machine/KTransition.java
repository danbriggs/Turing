package machine;

public class KTransition {
	byte _whatToWrite1;
	byte _whatToWrite2;
	byte _stateToLeaveIn;
	byte _sideToLeaveIn;
	int _numStepsTaken;
	KTransition(byte whatToWrite1, byte whatToWrite2, byte stateToLeaveIn, byte sideToLeaveIn, int numStepsTaken) {
		_whatToWrite1 = whatToWrite1;
		_whatToWrite2 = whatToWrite2;
		_stateToLeaveIn = stateToLeaveIn;
		_sideToLeaveIn = sideToLeaveIn;
		_numStepsTaken = numStepsTaken;
	}
}
