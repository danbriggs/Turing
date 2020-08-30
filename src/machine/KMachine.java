package machine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**To implement Marxen & Buntrock's idea of a "k-macro-machine,"
 * meaning the transition table is composed of what the machine does to bitstrings of length k.
 * The transition table generated should have size 10 × 2^k, where each entry consists of bitstring, side, state, numsteps.
 * So approximately (k+3) × 10 × 2^k × size of int, or 40(k+3)2^k bytes.
 * For k from 1 to 20, that size will be:
 * 320,     800,     1920,    4480,     10240,    23040,    51200,     112640,    245760,    532480,
 * 1146880, 2457600, 5242880, 11141120, 23592960, 49807360, 104857600, 220200960, 461373440, 964689920. 
 * This is largely a silly idea, since half the time the tape head just runs off after one step,
 * and even when it doesn't, the size of tape to rewrite tends to be on the order of the number of steps accelerated.
 * 
 * For the fast version, for each pair of state and 16-bit window, we record state, direction, and 16-bit window.
 * So as long as no bitsequence caused a loop (which may be the case for some HNRs),
 * we should need at most 2^16 * 5 cases, each of which consists of 3 + 1 + 16 bits.
 * Realistically, we'll use 1 byte for the state + direction combo in addition to the 2 for the new sequence,
 * and the key will take 2 + 1  bytes of storage too.
 * Since this adds up to 6 bytes per key-value pair, for fast addressing it is probably better to use 4+4=8 bytes;
 * thus the hashmap should be <Integer, Integer>, and with 65536 * 5 entries, 8 bytes each,
 * we're looking at using 2621440 bytes of memory. Not too bad!
 * In fact, there's no reason not to use an array instead of a Hashmap. This halves the size, not having to store the key.
 * (Well it halves the size from my estimate. Not too sure whether Hashmaps store their keys.)
 * 
 * Forgot about the numsteps! We can probably use shorts, but we might as well use ints, and now the total
 * storage is back up to 2621440.
 * */
public class KMachine {
	//static final int[] STATE_INDICES = {0, 65536, 65536*2, 65536*3, 65536*4}; 
	static final boolean LOUD = false;
	static final boolean DOING_STATISTICS = true;
	/* HashMap<String, String> capitalCities = new HashMap<String, String>();
       capitalCities.put("England", "London");
       get, remove, clear, size, keySet, values */
	private HashMap<int[], int[]> _whatToDo;
	private KTransition[] _kt; //For 16-bit K-machines only.
	/*private byte[] _whatToWrite1; //For 16-bit K-machines ONLY.
	private byte[] _whatToWrite2; //For 16-bit K-machines ONLY.
	private byte[] _stateToLeaveIn; //For 16-bit K-machines ONLY.
	private byte[] _sideToLeaveIn; //For 16-bit K-machines ONLY.
	private int[] _numStepsTaken; //For 16-bit K-machines ONLY.*/
	private int[] _patternFrequencies; //For 16-bit K-machines only.
	private boolean _freqTableLoaded; //For 16-bit K-machines only.
	private boolean _loadingFreqTable; //For 16-bit K-machines only.
	private HashMap<Integer, KTransition> _freqKT; //For 16-bit K-machines only.
	/*private HashMap<Integer, Byte> _freqWhatToWrite1; //For 16-bit K-machines only.
	private HashMap<Integer, Byte> _freqWhatToWrite2; //For 16-bit K-machines only.
	private HashMap<Integer, Byte> _freqStateToLeaveIn; //For 16-bit K-machines only.
	private HashMap<Integer, Byte> _freqSideToLeaveIn; //For 16-bit K-machines only.
	private HashMap<Integer, Integer> _freqNumStepsTaken; //For 16-bit K-machines only.*/
	//The lower halves of the key and value contain the information about the 16 bits on the tape.
	//The upper halves contain the information about the state (and for the value, the direction).
	private Machine _m;
	private int _k;
	private boolean _hasLoop;
	private boolean _fast;
	private int _state;
	//The keys should be of length k+2, and the values should be of length k+3.
	//The last two   entries in keys   should be the side it leaves from and the state.
	//The last three entries in values should be the side it leaves from,    the state, and the number of steps.
	/**This constructor can take a while to complete, depending on k.*/
	public KMachine(Machine m, int k) {
		if (k%2 != 0) {
			System.out.println("In KMachine(): Sorry, odd k not supported yet.");
			return;
		}
		if (k < 2 || k > 16) {
			System.out.println("In KMachine(): Sorry, k must be from 2 to 16.");
			return;
		}
		_whatToDo = new HashMap<int[], int[]>();
		_m = new Machine(m);
		_k = k;
		int numCeiling = Tools.pow(2, k);
		int stepCeiling = Tools.pow(2, k) * k * m.numStates();
		int maxNumSteps = 0; //For statistics only. Not to be confused with stepCeiling!
		int totNumSteps = 0; //For statistics only.
		for (int state = 0; state < m.numStates(); state++) {
			//for (int side = 0; side < k; side += k-1) { //Only two iterations
			for (int num=0; num < numCeiling; num++) {
				int[] bitstring = toBinary(num, k);
				//Here we create a configuration out of bitstring, side, and state,
				//and run m on it until it either leaves the bounds, halts, or
				//runs for more than 2^k × k × numStates steps.
				//If it runs that long, it'll loop forever.
				Configuration c = null;
				int beginSpot = k/2; //Was side instead of k/2
				try {c = new Configuration(bitstring.clone(), beginSpot, state);}
				catch (Exception e) {
					System.out.println("Error 1 in KMachine(): " + e.getMessage());
					return;
				}
				int numSteps = 0;
				while (c.getIndex() > 0 && c.getIndex() < c.length() && c.getState() >= 0 && numSteps <= stepCeiling) {
					try {m.actOnConfig(c);} 
					catch (Exception e) {
						System.out.println("Error 2 in KMachine(): " + e.getMessage());
						return;
					}
					numSteps++;
				}
				totNumSteps += numSteps;
				if (numSteps > maxNumSteps) maxNumSteps = numSteps;
				if (c.getState() < 0) if (LOUD) System.out.print("m halted ");
				if (c.getIndex() == 0) if (LOUD) System.out.print("m got to the left  ");
				if (c.getIndex() >= c.length()) if (LOUD) System.out.print("m ran off the right ");
				if (numSteps > stepCeiling) {
					System.out.print("m took too many steps ");
					_hasLoop = true;
				}
				
				if (LOUD) System.out.print("acting on bit string " + Tools.toString(bitstring) + " from pos. " + beginSpot + " in state " + state + ", ");
				if (LOUD) System.out.print("transforming it into " + c.toString() + " in " + numSteps + " steps.\n");
				//Now we add the results to the HashMap whatToDo.
				int[] before = new int[k+2];
				int[] after  = new int[k+3];
				for (int i = 0; i < k; i++) before[i] = bitstring[i];
				//before[k] = side; //I guess we'll leave a hiatus
				before[k+1] = state;
				for (int i = 0; i < k; i++) after[i] = c.getTape()[i];
				after[k] = c.getIndex(); //Will be 0 or k unless the machine halted or looped
				after[k+1] = c.getState();
				after[k+2] = numSteps;
				_whatToDo.put(before, after);
			}
			if (LOUD) System.out.println("Finished for state "+Tools.asLetter(state));
			//}
		}
		if (LOUD) {
			System.out.println("_hasLoop: "+_hasLoop);
			System.out.println("Max numSteps: "+maxNumSteps);
			System.out.println("Avg numSteps: "+totNumSteps*1.0/m.numStates()/numCeiling);
		}
		_state = 0;
	}

	/**With no argument for size, one means to construct the fast (?) 16-machine, using centered 16-bit windows.*/
	public KMachine(Machine m) {
		this(m, 16);
		_kt = new KTransition[_m.numStates()*65536];
		/*_whatToWrite1 = new byte[_m.numStates()*65536]; //What to write in the prevByte in the case of each 16-bit array, in each of the 5 states.
		_whatToWrite2 = new byte[_m.numStates()*65536]; //What to write in the currByte in the case of each 16-bit array, in each of the 5 states.
		_stateToLeaveIn = new byte[_m.numStates()*65536];
		_sideToLeaveIn = new byte[_m.numStates()*65536];
		_numStepsTaken = new int[_m.numStates()*65536];*/
		_patternFrequencies = new int[_m.numStates()*65536];
		
		//Now we dump all the HashMap contents into _kt.
		
	    Iterator<Entry<int[],int[]>> it = _whatToDo.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<int[],int[]> pair = it.next();
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        int[] before = pair.getKey();
	        int[] beforeSeq = Arrays.copyOfRange(before, 0, 16);
	        char charBeforeSeq = toChar(beforeSeq);
	        int beforeState = before[17];
	        int[] after = pair.getValue();
	        int[] afterSeq = Arrays.copyOfRange(after, 0, 16);
	        char charAfterSeq = toChar(afterSeq);
	        byte afterSeq1 = hiByte(charAfterSeq);
	        byte afterSeq2 = loByte(charAfterSeq);
	        int afterIndex = after[16];
	        int afterState = after[17];
	        int afterSteps = after[18];
	        _kt[beforeState*65536+charBeforeSeq] = new KTransition(afterSeq1, afterSeq2, (byte)afterState, (byte)afterIndex, afterSteps);
	        /*_whatToWrite1[beforeState*65536+charBeforeSeq] = afterSeq1;
	        _whatToWrite2[beforeState*65536+charBeforeSeq] = afterSeq2;	        
	        _sideToLeaveIn[beforeState*65536+charBeforeSeq] = (byte)afterIndex;
	        _stateToLeaveIn[beforeState*65536+charBeforeSeq] = (byte)afterState;
	        _numStepsTaken[beforeState*65536+charBeforeSeq] = afterSteps;*/
	    }
	    if (LOUD) System.out.println("Speedup finished.");

		_whatToDo.clear(); //Get rid of all that memory overhead!
		_whatToDo = null;
		_fast = true;
	}
	
	private static byte hiByte (char c) {
		return (byte) (c>>>8);
	}
	
	private static byte loByte (char c) {
		return (byte) c;
	}
	
	public boolean hasLoop() {return _hasLoop;}
	public boolean isFast() {return _fast;}
	public int getK() {return _k;}
	public Machine getMachine() {return new Machine(_m);}
	public int getState() {return _state;}
	
	/**Returns how many steps passed.*/
	public int actOnBitTape(BitTape bt) throws Exception{
		if (!_fast) throw new Exception("Cannot act on BitTape without fast machine");
		final int byteIndex = bt.getByteIndex();
		if (byteIndex < 1) throw new Exception("Byte index "+byteIndex+" too low for actOnBitTape.");
		if (byteIndex>=bt.byteTapeLength()) throw new Exception("Index "+byteIndex+" too high for actOnBitTape.");
		byte[] byteTape = bt.getByteTape();
		final byte prevByte = byteTape[byteIndex-1];
		final byte currByte = byteTape[byteIndex];
		final char twoBytes = toChar(prevByte, currByte);
		int stateTimes65536 = _state << 16;
		int arrayIndex = stateTimes65536+twoBytes;
		byte whatToWrite1, whatToWrite2, sideToLeaveIn, stateToLeaveIn;
		int numStepsTaken;
		if (!_freqTableLoaded || !_freqKT.containsKey(arrayIndex)) {
			KTransition kt = _kt[arrayIndex];
			whatToWrite1   = kt._whatToWrite1;
			whatToWrite2   = kt._whatToWrite2;
			sideToLeaveIn  = kt._sideToLeaveIn;
			stateToLeaveIn = kt._stateToLeaveIn;
			numStepsTaken  = kt._numStepsTaken;
			if (_loadingFreqTable) {
				_freqKT.put(arrayIndex, new KTransition(whatToWrite1, whatToWrite2, stateToLeaveIn, sideToLeaveIn, numStepsTaken));
				if (DOING_STATISTICS) _patternFrequencies[arrayIndex]++;
				/*_freqWhatToWrite1  .put(arrayIndex, whatToWrite1);
				_freqWhatToWrite2  .put(arrayIndex, whatToWrite2);
				_freqSideToLeaveIn .put(arrayIndex, sideToLeaveIn);
				_freqStateToLeaveIn.put(arrayIndex, stateToLeaveIn);
				_freqNumStepsTaken .put(arrayIndex, numStepsTaken);*/				
			}
		}
		else {
			KTransition kt = _freqKT.get(arrayIndex);
			whatToWrite1 = kt._whatToWrite1;
			whatToWrite2 = kt._whatToWrite2;
			sideToLeaveIn = kt._sideToLeaveIn;
			stateToLeaveIn = kt._stateToLeaveIn;
			numStepsTaken = kt._numStepsTaken;
			/*whatToWrite1   = _freqWhatToWrite1  .get(arrayIndex);
			whatToWrite2   = _freqWhatToWrite2  .get(arrayIndex);
			sideToLeaveIn  = _freqSideToLeaveIn .get(arrayIndex);
			stateToLeaveIn = _freqStateToLeaveIn.get(arrayIndex);
			numStepsTaken  = _freqNumStepsTaken .get(arrayIndex);*/		
		}
		bt.replacePrevByte(whatToWrite1);
		bt.replaceCurrByte(whatToWrite2);
		if (sideToLeaveIn == 0) bt.setByteIndex(byteIndex - 1);
		else if (sideToLeaveIn == 16) bt.setByteIndex(byteIndex + 1);
		else throw new Exception("In actOnBitTape(): direction is not 0 or 16");
		_state = stateToLeaveIn;
		return numStepsTaken;
	}
	
	/**Returns the precise number of steps passed.*/
	public long loadFreqTable(long maxNumSteps, int index) {
		if (index%8!=0) {
			System.out.println("In loadFreqTable(): index "+index+" not divisible by 8.");
		}
		Tape t = null;
		try {t = new Tape(new int[index*2], index);}
		catch (Exception e) {
			System.out.println("In KMachine.loadFreqTable(), Tape() failed to initialize: "+e.getMessage());
			return 0;
		}
		reset();
		_loadingFreqTable = true;
		_freqKT = new HashMap<Integer, KTransition>();
		/*_freqWhatToWrite1 = new HashMap<Integer, Byte>();
		_freqWhatToWrite2 = new HashMap<Integer, Byte>();
		_freqSideToLeaveIn= new HashMap<Integer, Byte>();
		_freqStateToLeaveIn=new HashMap<Integer, Byte>();
		_freqNumStepsTaken= new HashMap<Integer, Integer>();*/
		BitTape bt = new BitTape(t);
		long numSteps = 0;
		int currSteps = 0;
		while (numSteps < maxNumSteps) {
			try {
				currSteps = actOnBitTape(bt);
			} catch (Exception e) {
				System.out.println("In KMachine.loadFreqTable(), actOnBitTape() failed: "+e.getMessage());
				e.printStackTrace();
				return numSteps;
			}
			if (currSteps<=0) {
				System.out.println("In KMachine.loadFreqTable(), actOnBitTape() returned "+currSteps+" steps.");
				return numSteps;
			}
			numSteps+=currSteps;
		}
		_loadingFreqTable = false;
		doStatistics();
		_freqTableLoaded = true;
		reset();
		return numSteps;
	}
	
	/**Returns the char represented by the two bytes.
	 * */
	static char toChar(byte prevByte, byte currByte) {
		char highHalf = (char) (prevByte & 0xFF);
	    char lowHalf  = (char) (currByte & 0xFF);
		return (char) ((highHalf<<8)+lowHalf);
	}
	
	private static int[] toBinary(int number, int length) {
	    final int[] ret = new int[length];
	    for (int i = 0; i < length; i++) {
	        ret[length - 1 - i] = ((1 << i & number) != 0) ? 1 : 0;
	    }
	    return ret;
	}
	
	static char toChar(int[] array) {
		if (array == null || array.length != 16) {
			System.out.println("In toChar: array must be length 16");
			return 0;
		}
		char ret = 0;
		for (int i=0; i<16; i++) {
			ret |= array[i] << (15-i);
		}
		return ret;
	}
	
	public void doStatistics() {
		if (!DOING_STATISTICS) {
			System.out.println("You have to set DOING_STATISTICS to true to collect data.");
			return;
		}
		int totalUses = 0;
		int maxNumUses = 0;
		int nonzeroEntries = 0;
		for (int i=0; i<65536*5; i++) {
			int numUses = _patternFrequencies[i];
			totalUses += numUses;
			if (numUses > maxNumUses) maxNumUses = numUses;
			if (numUses > 0) nonzeroEntries++;
		}
		System.out.println("There were "+nonzeroEntries+" distinct patterns used among "+totalUses+" uses, "
				         + "so each pattern used was used an average of "+1.0*totalUses/nonzeroEntries+" times."
				         + "The maximum number of uses was "+maxNumUses+".");
	}
	
	void reset() {_state = 0;}
	
}
