package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**A class for a Termfiguration extended by a constant sequence of bits on either side.
 * Important: the Termfiguration may have to have been constructed with an index beyond its bounds.*/
public class ExtendedTermfiguration extends VeryTermfigurationLike {
	static final int L = -1, R = 1;
	static final int STANDARD_PADDING = 12; //How much to add when the tape head went out of bounds
	
	/**What's left of _t*/
	private int[] _l;
	private Termfiguration _t;
	/**What's right of _t*/
	private int[] _r;
	
	public ExtendedTermfiguration(Termfiguration t) {
		_l = new int[0];
		_t = t;
		_r = new int[0];
	}
	
	public ExtendedTermfiguration(int[] l, Termfiguration t) {
		_l = l;
		_t = t;
		_r = new int[0];
	}
	public ExtendedTermfiguration(Termfiguration t, int[] r) {
		_l = new int[0];
		_t = t;
		_r = r;
	}

	public ExtendedTermfiguration(int[] l, Termfiguration t, int[] r) {
		_l = l;
		_t = t;
		_r = r;
	}
	
	public Termfiguration toTermfiguration() {
		return _t;
	}
	
	public TermfigurationSequence toTermfigurationSequence() {
		return new TermfigurationSequence(
				new ArrayList<Termfiguration>(
						Arrays.asList(
								new Termfiguration(_l, new int[] {1}),
								_t,
								new Termfiguration(_r, new int[] {1}))));
	}

	public boolean isOrnamented() {return _t.isOrnamented();}

	public void deOrnament() throws Exception{
		if (isOrnamented() == false) throw new Exception("Error: attempt to deornament unornamented ExtendedTermfiguration");
		_t.deOrnament();
	}
	public int[] getBase() {return _t.getBase();}
	public int[] getExponent() {return _t.getExponent();}
	public int constCoeff() {return _t.constCoeff();}
	/** We use the index of _t shifted by _l's length as proxy.
	 * For this to work, you must allow Termfigurations to report indices beyond their bounds.*/
	public int[] getIndex() {return Tools.add(_t.getIndex(),new int[] {_l.length});}
	public int getState() {return _t.getState();}
	public void setState(int state) {getTerm().setState(state);}
	public int[] getLeft() {return _l;}
	public Termfiguration getTerm() {return _t;}
	public int[] getRight() {return _r;}
	public boolean hasLeft() {return _l.length > 0;}
	public boolean hasRight() {return _r.length > 0;}
	public boolean inBounds() throws Exception {return getTerm().inBounds();}
	public boolean preciseOutOfBounds() throws Exception{return getTerm().preciseOutOfBounds();}
	
	public boolean onLeft() {return getTerm().onLeft();}
	public boolean offLeft() {return getTerm().offLeft();}
	public boolean on(int side) {
		if (side < 0) return onLeft();
		if (side > 0) return onRight();
		System.out.println("Invalid input to ExtendedTermfiguration.on()");
		return false;
	}
	public boolean onRight() {return getTerm().onRight();}
	public boolean offRight() {return getTerm().offRight();}
	
	/**Returns whether the tape head is past the last 1 bit going left.
	 * Useful for SweepTheorems.*/
	public boolean offLeftWing() {
		int[] index = getIndex(); //from the point of view of THIS etf
		for (int i = 1; i < index.length; i++) if (index[i] != 0) return false;
		int spot = 0;
		if (index.length > 0) spot = index[0];
		int i;
		for (i = 0; i <= spot; i++) if (i >= _l.length || _l[i] != 0) return false;
		return true;
	}
	public boolean offRightWing() {
		int[] index = getIndex(); //from the point of view of THIS etf
		for (int i = 2; i < index.length; i++) if (index[i] != 0) {
			System.out.println("ExtendedTermfiguration.offRightWing() only implemented for linear indices so far.");
			return false;
		}
		int[] length = length();
		for (int i = 2; i < length.length; i++) if (index[i] != 0) {
			System.out.println("ExtendedTermfiguration.offRightWing() only implemented for linear lengths so far.");
			return false;
		}
		int[] indexBack = Tools.subtract(length, index);
		if (indexBack.length > 1 && indexBack[1] != 0) return false;
		int spotBack = indexBack[0];
		for (int i = 1; i <= spotBack; i++) {
			int rIndex = _r.length - i;
			if (rIndex < 0 || _r[rIndex] != 0) return false;
		}
		return true;
	}
	public int extremeSide() {
		if (offLeftWing()) return L;
		if (offRightWing()) return R;
		return 0;
	}
	
	public int[] evalAt(int n) throws Exception {
		int[] term = _t.evalAt(n);
		int len = _l.length + term.length + _r.length;
		int[] ret = new int[len];
		int i, j, k;
		for (i = 0; i < _l.length; i++) ret[i] = _l[i];
		for (j = 0; j < term.length; j++) ret[i + j] = term[j];
		for (k = 0; k < _r.length; k++) ret[i + j + k] = _r[k];
		return ret;
	}
	
	public Term toTermAt(int n) throws Exception {
		return _t.toTermAt(n);
	}
	
	public Configuration toConfigurationAt(int n) throws Exception {
		if (!isOrnamented()) throw new Exception("In toConfigurationAt("+n+"), cannot convert unornamented ExtendedTermfiguration to Configuration.");
		int[] tapeContents = evalAt(n);
		int index = Tools.evalAt(getIndex(), n);
		return new Configuration(tapeContents, index, getState());
	}
	
	public ExtendedTermfiguration successor(){
		return new ExtendedTermfiguration(_l.clone(), _t.successor(), _r.clone());
	}
		
	public int[] length() {
		int[] winglen = new int[] {_l.length + _r.length};
		return Tools.add(_t.length(), winglen);
	}
	
	public int[] lastBit() {
		return Tools.add(length(), new int[] {-1});
	}
	
	public ExtendedTermfiguration deepCopy() {
		return new ExtendedTermfiguration(_l.clone(), _t.deepCopy(), _r.clone());
	}
	
	public boolean equals(TermfigurationLike tl) {
		if (!(tl instanceof ExtendedTermfiguration)) return false;
		ExtendedTermfiguration et = tl.toExtendedTermfiguration();
		if (!Arrays.equals(et.getLeft(), _l)) return false;
		if (!Arrays.equals(et.getRight(), _r)) return false;
		return et.getTerm().equals(_t);
	}
	
	/**Here 0s beyond the written tape are allowed.
	 * Revise to check for other ways in which they could be equal!*/
	public boolean essentiallyEquals(ExtendedTermfiguration et) throws Exception{
		if (!(preciseOutOfBounds()||onLeft()||onRight())) throw new Exception("Haven't written code for that yet");
		return toString().equals(et.toString()); //Lazy!!!
		//This is awful code, because it won't be valid whenever the index isn't precisely out of bounds!
	}
	
	public ExtendedTermfiguration toExtendedTermfiguration() {
		return this;
	}
	
	public CondensedConfiguration toCondensedConfigurationAt(int n) throws Exception {
		if (!isOrnamented()) throw new Exception (
				"In toCondensedConfigurationAt("+n+"), cannot convert unornamented ExtendedTermfiguration to CondensedConfiguration.");
		Term t1 = new Term(_l, 1);
		Term t2 = toTermAt(n);
		Term t3 = new Term(_r, 1);
		List<Term> tl = new ArrayList<Term>();
		tl.add(t1);
		tl.add(t2);
		tl.add(t3);
		return new CondensedConfiguration(tl, Tools.evalAt(getIndex(), n), getState());
	}
	
	/**How does a polynomial modelling a term's length as a function of a single variable
	 * compare to a polynomial modelling the index of the tape head?
	 * That's not clear in general; we only use the special type of output when it's clear.*/
	public String toString() {
		String lString, mString, rString;
		int[] coeffArray = Tools.trimEnd(getIndex());
		int indexDegree = coeffArray.length;
		if (indexDegree <= 1) {
			int constCoeff = getIndex()[0];
			if (constCoeff < _l.length) {
				try {lString = new Tape(_l, constCoeff).toString();}
				catch (Exception e) {System.out.println("Exception 1 in ExtendedTermfiguration.toString()"); return null;}
				mString = _t.toStringBuffer().toString();
				rString = Tools.toShortString(_r);
				try {return Tools.asLetter(getState()) + " " + Tools.ignore(lString,'0',-1) + mString + Tools.ignore(rString,'0',1);}
				catch (Exception e) {System.out.println("Exception 2 in ExtendedTermfiguration.toString()"); return null;}
			}
			else if (constCoeff == _l.length) {
				lString = Tools.toShortString(_l);
				mString = _t.toStringBuffer().toString();
				rString = Tools.toShortString(_r);
				try {return Tools.asLetter(getState()) + " " + Tools.ignore(lString,'0',-1) + '>' + mString + Tools.ignore(rString,'0',1);}
				catch (Exception e) {System.out.println("Exception 3 in ExtendedTermfiguration.toString()"); return null;}				
			}
		}
		//TODO: array out-of-bounds error handling
		//Next if they differ only in the constant coefficient
		if (Tools.equal(
				Arrays.copyOfRange(getIndex(), 1, getIndex().length),
				Arrays.copyOfRange(_t.length(), 1, _t.length().length))) {
			if (_t.getIndex()[0] >= _t.length()[0]) {
				int rIndex = _t.getIndex()[0] - _t.length()[0];
				lString = Tools.toShortString(_l);
				mString = _t.toStringBuffer().toString();
				try {rString = new Tape(_r, rIndex).toString();}
				catch (Exception e) {System.out.println("Exception 4 in ExtendedTermfiguration.toString()"); return null;}
				try {return Tools.asLetter(getState()) + " " + Tools.ignore(lString,'0',-1) + mString + Tools.ignore(rString,'0',1);}
				catch (Exception e) {System.out.println("Exception 5 in ExtendedTermfiguration.toString()"); return null;}
			}
			else if (_t.getIndex()[0] == _t.length()[0] - 1) {
				lString = Tools.toShortString(_l);
				mString = _t.toStringBuffer().toString();
				rString = Tools.toShortString(_r);
				try {return Tools.asLetter(getState()) + " " + Tools.ignore(lString,'0',-1) + mString + '<' + Tools.ignore(rString,'0',1);}
				catch (Exception e) {System.out.println("Exception 6 in ExtendedTermfiguration.toString()"); return null;}				
			}
		}
		//If you're still here, then we just have a normal kind of output.
		try {
			return Tools.asLetter(getState()) + " " +
			Tools.ignore(Tools.toShortString(_l),'0',-1) +
			_t.toStringBuffer().toString() +
			Tools.ignore(Tools.toShortString(_r),'0',1) + ' ' +
			Tools.toPolynomialString(getIndex(), 'n');
		} catch (Exception e) {
			System.out.println("Exception 7 in ExtendedTermfiguration.toString()");
			return null;
		}
	}
	
	
	/**Tries to rewrite the core with the new base length.
	 * Returns null if it can't do it.*/
	ExtendedTermfiguration refactored(int newBaselen) {return refactored(newBaselen, R);}
	
	ExtendedTermfiguration refactored(int newBaselen, int sidePreference) {
		int currBaselen = getBase().length;
		if (newBaselen > currBaselen) return refactoredLonger(newBaselen, sidePreference);
		if (newBaselen == currBaselen) return deepCopy();
		if (newBaselen > 0 && newBaselen < currBaselen) return refactoredShorter(newBaselen);
		return null;
	}
	
	ExtendedTermfiguration refactoredShorter(int newBaselen) {
		return new ExtendedTermfiguration(_l.clone(), _t.refactoredShorter(newBaselen), _r.clone());
	}
	
	/** Leaks extra partial repetitions onto the sidePreference side.*/
	ExtendedTermfiguration refactoredLonger(int newBaselen, int sidePreference) {
		int[] currBase = getBase();
		int currBaselen = currBase.length;
		if (newBaselen < currBaselen) return null;
		if (newBaselen == currBaselen) return deepCopy();
		if (newBaselen % currBaselen != 0) return null; //TODO: write code for trickier cases later
		int factor = newBaselen / currBaselen;
		int[] currExponent = getExponent();
		int numTerms = currExponent.length; //Number of terms in the exponent
		int[] newExponent = new int[numTerms];
		for (int deg = 1; deg < numTerms; deg++) {
			if (currExponent[deg] % factor != 0) return null; //Because it would be too complicated
			newExponent[deg] = currExponent[deg] / factor;
		}
		//Still have to do the degree 0 one
		int constCoeff = currExponent[0];
		int q = constCoeff / factor;
		int r = constCoeff % factor;
		newExponent[0] = q;
		int[] newBase = Tools.iterate(currBase, factor);
		int[] newL, newR;
		int toShiftIndexLeft = 0;
		if (sidePreference == L) {
			newL = Tools.concatenate(_l, Tools.iterate(currBase, r));
			toShiftIndexLeft = currBaselen * r;
			newR = _r.clone();
		}
		else { //So I guess no side preference defaults to R
			newL = _l.clone();
			newR = Tools.concatenate(Tools.iterate(currBase, r), _r);
		}
		//Careful of how Termfigurations view their index vs. how ExtendedTermfigurations do!
		int[] newTIndex = Tools.add(_t.getIndex(), new int[] {-toShiftIndexLeft});
		Termfiguration newT = new Termfiguration(newBase, newExponent, newTIndex, getState());
		return new ExtendedTermfiguration(newL, newT, newR);
	}
	
	/**Splits off one copy of the Term to the given side if the constant coefficient of its exponent is positive.
	 * Otherwise returns false.*/
	public boolean split(int direction) {
		if (direction != L && direction != R) return false;
		if (constCoeff()<=0) return false;
		getExponent()[0] -= 1;
		if (direction == R) _r = Tools.concatenate(getBase(), _r);
		if (direction == L) _l = Tools.concatenate(_l, getBase());
		return true;
	}
	
	/**Attempts to unfurl by a fractional number of copies of the base if possible.
	 * Fractional numbers of copies are possible only when the base is a repeated sequence.
	 * Returns false if the constant coefficient of the exponent is not positive
	 * and it couldn't unfurl by a fractional number of copies of the base.
	 * See tryShiftAway() & tryShiftTowards() instead if you'd like to allow the base to change.*/
	public boolean unfurl(int side) {
		int baselen = getBase().length;
		//A valid bit shift is one where base is a mere repetition of that sequence.
		if (side == R) {
			for (int i=1; i<=baselen/2; i++) {
				if (!Tools.isRepeatOfLast(getBase(), i)) continue;
				//Now we try to shift it out if either
				// (1) terms can be collected from the opposite wing or
				// (2) the constant coefficient of the exponent is positive.
				try {
					int[] lastiOfBase = Arrays.copyOfRange(getBase(),baselen - i,baselen);
					int[] lastiOfl    = Arrays.copyOfRange(_l, _l.length - i, _l.length);
					if (Arrays.equals(lastiOfBase, lastiOfl)) {
						int[] piece = Tools.shiftAllByAndReturnFallen(_l, i);
						_r = Tools.concatenate(piece, _r);
						_t.getIndex()[0] += i;
						//Because from _t's point of view, 0 has shifted
						return true;
					}
				} catch (Exception e) {}
				//_l doesn't have the same pattern (possibly because the piece was longer than _l).
				//So we decrease the constant coefficient of the exponent,
				//tack on the end to _r, the beginning to _l, and adjust the
				//internal representation of the index accordingly.
				if (getExponent()[0] <= 0) return false;
				getExponent()[0] -= 1;
				int[] lPiece = Arrays.copyOfRange(getBase(), 0, baselen - i);
				int[] rPiece = Arrays.copyOfRange(getBase(), baselen - i, baselen);
				_l = Tools.concatenate(_l, lPiece);
				_r = Tools.concatenate(rPiece, _r);
				getIndex()[0] -= baselen - i;
				return true;
			}
		}
		else if (side == L) {
			for (int i=1; i<=baselen/2; i++) {
				if (!Tools.isRepeatOfFirst(getBase(), i)) continue;
				try {
					int[] firstiOfBase = Arrays.copyOfRange(getBase(),0,i);
					int[] firstiOfr    = Arrays.copyOfRange(_r, 0, i);
					if (Arrays.equals(firstiOfBase, firstiOfr)) {
						int[] piece = Tools.shiftAllByAndReturnFallen(_r, -i);
						_l = Tools.concatenate(_l, piece);
						_t.getIndex()[0] -= i;
						//Because from _t's point of view, 0 has shifted
						return true;
					}
				} catch (Exception e) {}
				//_l doesn't have the same pattern (possibly because the piece was longer than _l).
				//So we decrease the constant coefficient of the exponent,
				//tack on the end to _r, the beginning to _l, and adjust the
				//internal representation of the index accordingly.
				if (getExponent()[0] <= 0) return false;
				getExponent()[0] -= 1;
				int[] lPiece = Arrays.copyOfRange(getBase(), 0, i);
				int[] rPiece = Arrays.copyOfRange(getBase(), i, baselen);
				_l = Tools.concatenate(_l, lPiece);
				_r = Tools.concatenate(rPiece, _r);
				getIndex()[0] -= i;
				return true;
			}
		}
		else {
			System.out.println("Invalid side for unfurl");
			return false;
		}
		return false;
	}
	
	public void condense() {_t.condense();}
	
	/**Returns how many steps from the vast expanse of blank tape the tape head is.
	 * Returns -1 if the tape head is not in a wing.
	 * So far only a simplistic implementation.*/
	public int stepsFromSide() {
		if (!isLinear()) {
			System.out.println("In ExtendedTermfiguration.stepsFromSide(): haven't made code for nonlinear Termfigurations yet");
			return -1;
		}
		try {
			if (!preciseOutOfBounds()) return -1;
		} catch (Exception e) {
			System.out.println("In ExtendedTermfiguration.stepsFromSide(): "+e.getMessage());
			return -1;
		}
		if (getIndex().length<2 || getIndex()[1]==0) return getIndex()[0] - blanksOnSide(L);
		else if (getExponent().length < 2) return -1;
		else if (getIndex()[1] == getExponent()[1] * getBase().length &&
				getIndex()[0] >= _l.length + getExponent()[0] * getBase().length)
			return length()[0] - 1 - (getIndex()[0] - (_l.length + getExponent()[0] * getBase().length)) - blanksOnSide(R);
		else {
			System.out.println("In ExtendedTermfiguration.stepsFromSide(): couldn't figure it out");
			return -1;
		}
	}
	
	/**Returns how many blanks there are in a trimmed version of this,
	 * up to but not including the tape head, on the given side.
	 * Returns -1 if it can't figure it out.*/
	public int blanksOnSide(int side) {
		if (!isLinear()) {
			System.out.println("In ExtendedTermfiguration.blanksOnSide(): haven't made code for nonlinear Termfigurations yet");
			return -1;
		}
		try {
			if (!preciseOutOfBounds()) return -1;
		} catch (Exception e) {
			System.out.println("In ExtendedTermfiguration.blanksOnSide(): "+e.getMessage());
			return -1;
		}
		if (Tools.degree(getIndex()) <= 0) {
			int i;
			for (i=0; i<getIndex()[0] && i<_l.length; i++) {
				if (_l[i]==1) return i;
			}
			if (i == getIndex()[0]) return i;
			return -1;
		}
		else if (getExponent().length == 2 && getIndex().length ==2 &&
				 getIndex()[1] == getExponent()[1] * getBase().length) {
			int backIndex = length()[0] - getIndex()[0] - 1;
			int i;
			for (i=0; i<backIndex && i<_r.length; i++) {
				if (_r[i]==1) return i;
			}
			if (i == backIndex) return i;
			return -1;
		}
		else return -1;
	}
	
	public boolean isLinear() {
		return _t.isLinear();
	}
	
	/**Returns the number of times swallowing was successful.*/
	public int swallowAll() {
		int numTimes = 0;
		while (trySwallow(L)) numTimes++;
		while (trySwallow(R)) numTimes++;
		return numTimes;
	}
	
	public int swallowAll(int side) {
		int numTimes = 0;
		while (trySwallow(side)) numTimes++;
		return numTimes;		
	}
	
	public boolean trySwallow(int side) {return trySwallow(side, 1);}
	
	/**Attempts to swallow numTerms copies of the base from the wing on side side,
	 * and does so only if the pattern matches the base's pattern that number of times,
	 * and this sequence does not include the tape head.
	 * Otherwise does nothing and returns false.
	 * Only suited for linear exponents & indices.*/
	public boolean trySwallow(int side, int numTerms) {
		int baselen = _t.getBase().length;
		if (side == R) {
			int tapeHeadRindex;
			if (getIndex()[1] == getExponent()[1] * getBase().length)
				tapeHeadRindex = getIndex()[0] - _l.length - getBase().length * getExponent()[0];
			else
				tapeHeadRindex = -1; //In other words, don't use it
			for (int i=0; i<numTerms; i++) {
				for (int j=0; j<baselen; j++) {
					int rindex = baselen * i + j;
					System.out.println("Debug code: tapeHeadRindex = " + tapeHeadRindex + " and rindex = "+rindex);
					if (rindex >= _r.length || _r[rindex] != getBase()[j] || rindex == tapeHeadRindex)
						return false;
				}
			}
			getExponent()[0] += numTerms;
			_r = Arrays.copyOfRange(_r, baselen * numTerms, _r.length);
			return true;
		}
		else if (side == L) {
			int tapeHeadLindex;
			if (getIndex()[1] == 0)
				tapeHeadLindex = getIndex()[0];
			else
				tapeHeadLindex = -1;
			int llen = _l.length;
			for (int i=0; i<numTerms; i++) {
				for (int j=0; j<baselen; j++) {
					int lindex = llen - 1 - (baselen * i + j);
					System.out.println("Debug code: tapeHeadLindex = " + tapeHeadLindex + " and lindex = "+lindex);
					if (lindex < 0 || _l[lindex] != getBase()[baselen - 1 - j] || lindex == tapeHeadLindex)
						return false;
				}
			}
			getExponent()[0] += numTerms;
			_l = Arrays.copyOfRange(_l, 0, llen - baselen * numTerms);
			_t.getIndex()[0] += baselen * numTerms; //Because _t is who is *really* keeping track of the index,
			                                        //and from _t's point of view (index 0 being the beginning of the base), that's changed
			return true;
		}
		return false;
	}
	
	public int nonzeroSwathLengthAt(int n) {
		int llen = Tools.trimBeginning(_l).length;
		int rlen = Tools.trimEnd(_r).length;
		int blen = getBase().length * Tools.evalAt(getExponent(), n);
		return llen + rlen + blen;
	}
	
	/** Quick hack; only works for linear Termfiguration bases.
	 *  Assumes preciseOutOfBounds() works correctly.*/
	int side() {
		try {
			if (preciseOutOfBounds()) {
				if (getIndex()[1] == 0) return L;
				if (getIndex()[1] > 0) return R;
			}
			return 0;
		} catch (Exception e) {
			System.out.println("In ExtendedTermfiguration.side(): "+e.getMessage());
			return 0;
		}
	}
	
	/**Attempts to shift the Term over so that it matches target.
	 * TODO: write the code for when side() > 0.*/
	public void tryShiftAway(int[] target, int maxShift) {
		if (getBase().length != target.length) return;
		if (Arrays.equals(getBase(), target)) return;
		if (side() < 0) {
			System.out.println("Got here 1");
			for (int i = 1; i < getBase().length && i <= maxShift && i < _r.length; i++) {
				int[] rotatedBase = Tools.rotated(getBase(), -i);
				if (Arrays.equals(rotatedBase, target)) {
					System.out.println("Got here 2");
					boolean moved = move(i);
				}
			}
		}
	}
	
	public boolean move(int i) {
		if (i > 0) {
			int[] rotatedBase = Tools.rotated(getBase(), -i);
			//We check if the opposite wing's nearest i bits match
			int[] oppBits = Arrays.copyOfRange(_r, 0, i);
			int[] baseBits = Arrays.copyOfRange(getBase(), 0, i);
			if (Arrays.equals(oppBits, baseBits)) {
				int[] ret = Tools.shiftAllByAndReturnFallen(_r, -i);
				_l = Tools.concatenate(_l, ret);
				_t.setBase(rotatedBase);
				_t.getIndex()[0] -= i;
				return true;
			}
			return false;
		}
		else if (i < 0) {
			i *= -1; //Now it's a positive left shift
			int[] rotatedBase = Tools.rotated(getBase(), i);
			//We check if the opposite wing's nearest i bits match
			int[] oppBits = Arrays.copyOfRange(_l, _l.length - i, _l.length);
			int[] baseBits = Arrays.copyOfRange(getBase(), getBase().length - 1, getBase().length);
			if (Arrays.equals(oppBits, baseBits)) {
				int[] ret = Tools.shiftAllByAndReturnFallen(_l, i);
				_r = Tools.concatenate(ret, _r);
				_t.setBase(rotatedBase);
				_t.getIndex()[0] += i;
				return true;
			}
			return false;
		}
		else return true;
	}

	public void tryShiftToward(int[] target, int maxShift) {
		// TODO Auto-generated method stub
		
	}
	
	/**Don't need to worry about the index for these,
	 * since the only instance variable is in Termfiguration,
	 * and from its point of view, nothing's changed!*/
	public void padLeft()  {_l = Tools.concatenate(new int[STANDARD_PADDING], _l);}
	public void padRight() {_r = Tools.concatenate(_r, new int[STANDARD_PADDING]);}
	public void padBothSides() {padLeft(); padRight();}
	
	int wingSize(int side) {
		if (side == L) {
			int[] lReduced = Tools.trimBeginning(_l);
			int firstIndex = _l.length - lReduced.length;
			if (side() == L) firstIndex = Math.min(firstIndex, getIndex()[0]);
			return _l.length - firstIndex;
		}
		else if (side == R) {
			int[] rReduced = Tools.trimEnd(_r);
			int length = rReduced.length;
			if (side() == R) length = Math.max(length, Tools.subtract(getIndex(), _t.length())[0] - _l.length + 1);
			return length;
		}
		else return -1;
	}
	
	public boolean matchWingSize(ExtendedTermfiguration other) {
		if (side() != L && side() != R) return false;
		if (other.side() != side()) return false;
		int myWingSize    = wingSize(side());
		int otherWingSize = other.wingSize(other.side());
		if (side() == L) {
			return move(otherWingSize - myWingSize);
		}
		else if (side() == R) {
			return move(myWingSize - otherWingSize);
		}
		return false;
	}
}
