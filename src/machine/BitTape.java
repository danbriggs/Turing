package machine;

import java.util.Arrays;

/**Class for a tape where each bit takes up only one bit of memory.
 * Primarily for use with KMachine.actOnBitTape(BitTape bt).
 * Please construct only from a Tape.*/
public class BitTape implements TapeLike {
	private byte[] _tape; //Make a char out of two consecutive bytes as follows:
						  //negative bytes count as 128 to 255 in the usual way;
						  //First byte is high byte, second is low byte, for the char.
	private int _byteIndex;
	
	/**Only pass in a Tape whose length is divisible by 8.*/
	public BitTape(Tape t) {
		int[] oldTape = t.getTape();
		if (oldTape.length%8!=0) {
			System.out.println("In BitTape(): length not divisible by 8.");
			return;
		}
		if (t.getIndex()%8!=0) {
			System.out.println("In BitTape(): index not divisible by 8.");
			return;
		}
		_tape = new byte[oldTape.length / 8];
		for (int i=0; i<t.length(); i+=8) {
			//Convert the current 8 bits to a byte
			byte currByte = 0;
			try {currByte = fromBinary(Arrays.copyOfRange(oldTape, i, i+8));}
			catch (Exception e) {
				System.out.println("Could not initialize BitTape: "+e.getMessage());
				return;
			}
			_tape[i/8] = currByte;
		}
		_byteIndex = t.getIndex()/8;
	}
	
	public byte[] getByteTape() {return _tape;}
	public int byteTapeLength() {return _tape.length;}
	
	/**:-( Java sucks at bits*/
	static byte fromBinary(int[] arr) throws Exception {
		if (arr.length!=8) throw new Exception("In fromBinary(): arr.length=="+arr.length+"!=8.");
		for (int i=0; i<8; i++) if (arr[i]!=0 && arr[i]!=1) throw new Exception("In fromBinary(): arr["+i+"]=="+arr[i]+".");
		byte retVal = 0;
		if (arr[0] == 0) {
			for (int i=0; i<7; i++) retVal += arr[7-i] << i;
			return retVal;
		}
		//Now we can be sure arr[0] is 1
		byte posVer = 0;
		for (int i=0; i<7; i++) posVer += (1-arr[7-i]) << i;
		if (posVer==127) return -128;
		posVer += 1;
		retVal = (byte)-posVer;
		return retVal;
	}
	
	/**Converts byte to eight 1/0 integers.*/
	static int[] toBinary(byte b) {
		int[] retVal = new int[8];
		if (b>=0) {
			for (int i=0; i<7; i++) {
				int remainder = b%2;
				retVal[7-i] = remainder;
				b/=2;
			}
			return retVal;
		}
		//Now we can be sure that b<0
		retVal[0]=1;
		byte posVer = (byte)(-b-1);
		//if (posVer==126) System.out.println("Yes, we're working with 126.");
		for (int i=0; i<7; i++) {
			int remainder = posVer%2;
			retVal[7-i] = 1-remainder;
			posVer/=2;
		}
		return retVal;
	}
	//In checking, let's make sure that these two functions are inverse of each other
	//in both ways on all 256 possible inputs.
	
	/** Depend on this for the rest of the TapeLike methods.
	 *  Doesn't need to be fast.*/
	public int[] getTape() {
		//We have to convert them eight by eight
		int[] retVal = new int[_tape.length*8];
		for (int i=0; i<_tape.length; i++) {
			int[] currArray = toBinary(_tape[i]);
			for (int j=0; j<8; j++) retVal[8*i+j]=currArray[j];
		}
		return retVal;
	}
	
	public int length() {return getTape().length;}
	public int getIndex() {return _byteIndex*8;}
	public int getByteIndex() {return _byteIndex;}
	public int getNormalizedIndex() {return getIndex();}
	public boolean onLeft() {
		int[] theTape = getTape();
		int index = _byteIndex*8;
		for (int i=index-1; i>=0; i--) {
			if (theTape[i]!=0) return false;
		}
		return true;
	}
	public boolean onRight() {
		int[] theTape = getTape();
		int index = _byteIndex*8;
		for (int i=index+1; i<theTape.length; i++) {
			if (theTape[i]!=0) return false;
		}
		return true;
	}
	public void setIndex(int index) {
		if (index%8!=0) {
			System.out.println("In BitTape.setIndex(): index "+index+" is not divisible by 8.");
		}
		_byteIndex = index/8;
	}
	public void setByteIndex(int byteIndex) {_byteIndex = byteIndex;}
	
	public String getTrimAsString() {try {
		return new Tape(getTape(),_byteIndex*8).getTrimAsString();
	} catch (Exception e) {
		System.out.println("In BitTape.getTrimAsString(): "+e.getMessage());
		return null;
		}
	}
	
	public void printTape() {System.out.println(toString());}
	
	public String toString() {
		try {
			return new Tape(getTape(), _byteIndex*8).toString();
		} catch (Exception e) {
			System.out.println("In BitTape.toString(): "+e.getMessage());
			return null;
		}
	}
	
	public void printTrim() {System.out.println(getTrimAsString());}
	
	public int getSymbol() {
		return getTape()[_byteIndex*8];
	}

	/**Slow!*/
	public void replace(int symbol) {
		try {
			Tape tempTape = new Tape(getTape(),_byteIndex*8);
			tempTape.replace(symbol);
			_tape = new BitTape(tempTape).getByteTape();
		} catch (Exception e) {
			System.out.println("In BitTape.replace(): "+e.getMessage());
			return;
		}
	}
	
	/**Replaces the bytes before & at index with the bytes encoded in the char twoBytes.*/
	public void replaceBytes(char twoBytes) {
		byte highByte = (byte) (twoBytes >>> 8);
		byte lowByte  = (byte) twoBytes;
		_tape[_byteIndex - 1] = highByte;
		_tape[_byteIndex] = lowByte;
	}
	
	public void replacePrevByte(byte b) {
		_tape[_byteIndex - 1] = b;
	}
	
	public void replaceCurrByte(byte b) {
		_tape[_byteIndex] = b;
	}

	public void go(int direction) throws Exception {
		if (direction==-8) _byteIndex -=1;
		else if (direction==8) _byteIndex +=1;
		else throw new Exception("In BitTape.go(): Direction must be -8 or 8 but is "+direction);
	}

}
