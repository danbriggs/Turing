package machine;

import java.util.ArrayList;
import java.util.List;

public class Tests {
	static final char UNIT_SEPARATOR = 31;
	private Machine _m;
	private List<Machine> _machineList;
	private Lemma _lemma1;
	private Lemma _lemma2;
	private Lemma _lemma3;
	private Lemma _lemma4;
	
	public Tests(List<Machine> machineList) {
		this();
		_machineList = machineList;
		_lemma1 = null;
		_lemma2 = null;
		_lemma3 = null;
		_lemma4 = null;
	}
	public Tests() {
		try {_m = new Machine(Tools.HNR1);} 
		catch (Exception e) {System.out.println("ERROR: Failed to initialize Machine HNR1: "+e.getMessage());}
	}
	
	public boolean runTests(int num, int start, int stop) {
		if (num<0 || num>=_machineList.size()) {
			System.out.println("Error in number field: is "+num+" but must be from 0 to "+(_machineList.size()-1));
			return false;
		}
		boolean ok = true;
		ok &= configurationReadTest();
		ok &= condensedConfigurationTest();
		ok &= termTest();
		ok &= termfigurationTest();
		ok &= successorTest();
		ok &= leftToRightInductionTest();
		ok &= rightToLeftInductionTest();
		ok &= lemmaAsStringTest();
		ok &= actTest1();
		ok &= actTest2();
		ok &= stretchTapeTest(num);
		ok &= bigStretchTapeTest(num);
		ok &= bigStretchTapeTest2(num);
		ok &= allProvedTest(num);
		ok &= longestRunTest(num, start, stop);
		ok &= yieldsTest(num, start, stop);
		
		return ok;
	}
	
	public void run(int num,   int top1, int top2, boolean analytic, boolean leftEdge, boolean rightEdge, boolean stepNumbers) {
		if (num>0 && num<_machineList.size()) {
			Machine m = _machineList.get(num);
			m.reset();
			run(m,top1,top2,analytic,leftEdge,rightEdge,stepNumbers);
		}
		else if (num==0) {
			Thread t = new Thread(new Runnable() {
				public void run() {
		        	  for (int i=1; i<_machineList.size(); i++) {
		  				Machine m = _machineList.get(i);
		  				m.reset();
		  				Tests.run(m,top1,top2,analytic,leftEdge,rightEdge,stepNumbers);
		  				System.out.println(UNIT_SEPARATOR);
		        	  }
		        }
			});
			t.start();
		}
	}
	private static void run(Machine m, int top1, int top2, boolean analytic, boolean leftEdge, boolean rightEdge, boolean stepNumbers) {
		//Warning: does not automatically reset m to state A
		//Make sure to call m.reset() before invoking this function
		//if you're interested in a clean run from a blank tape.
		//If analytic is false, displays all steps from top1 to top2.
		//If analytic is true, displays initial & final steps,
		//as well as steps whenever the tape head is at the left/right edge, respectively,
		//according to the value of leftEdge/rightEdge.
		String name = "Run";
		System.out.println("\n"+name+" beginning.");
		System.out.println(m);
		//System.out.println(leftEdge);
		//System.out.println(rightEdge);
		try {
			TapeLike t;
			int idx = Math.min(top2,100000);
			if (analytic)
				t = new StretchTape(new int[idx*2+1],idx);
			else
				t = new Tape(new int[idx*2+1],idx);
			int i;
			for (i=0; i<top1; i++) m.act(t);
			for (i=top1; i<top2; i++) {
				boolean shouldPrint = !analytic || i==top1 || leftEdge&&t.onLeft() || rightEdge&&t.onRight();
				if (shouldPrint) {
					if (stepNumbers)
						System.out.print(i+" ");
					System.out.print((char)(m.getState()+65)+" ");
					t.printTrim();
				}
				m.act(t);
			}
			System.out.print((char)(m.getState()+65)+" ");
		} catch (Exception e) {
			System.out.println("ERROR: run() failed: "+e.getMessage());
			return;
		}
		System.out.println(name+" successful.");
		return;
		
	}
	
	/*public boolean normalActionTest(int num, int top1, int top2){
		String name = "Normal Action Test";
		System.out.println("\n"+name+" beginning.");
		try {
			Machine m = _machineList.get(num);
			m.reset();
			Tape t = new Tape(new int[top2*2+1],top2);
			int i;
			m.reset();
			for (i=0; i<top1; i++) m.act(t);
			for (i=top1; i<top2; i++) {
				m.act(t);
				System.out.print((char)(m.getState()+65)+" ");
				t.printTrim();
			}
		} catch (Exception e) {
			System.out.println("ERROR: normalActionTest() failed: "+e.getMessage());
			return false;
		}
		System.out.println(name+" successful.");
		return true;
	}*/
	
	public boolean yieldsTest() {
		try {
			return yieldsTest(new Machine(Tools.HNR1), 40, 50);
		} catch (Exception e) {
			System.out.println("ERROR: yieldsTest() failed: "+e.getMessage());
			return false;
		}
	}
	public boolean yieldsTest(int i, int first, int second) {
		boolean ok=true;
		if (i>0) {
			System.out.print("For Machine#"+i+", ");
			ok = yieldsTest(_machineList.get(i), first, second);
		}
		else if (i==0) {
			for (int j=1; j<_machineList.size(); j++) {
				ok &= yieldsTest(j,first,second);
			}
		}
		else {
			System.out.println("Invalid machine # for yields test: "+i);
			ok = false;
		}
		return ok;
	}
	public boolean yieldsTest(Machine m, int first, int second){
		boolean ok;
		if ((second-first)%2==0) second++;
		//Difference in parity ensures
		//that m can't yield the same configuration after second steps
		//that it yields after first steps.
		String name = "Yields Test";
		System.out.println(name+" beginning.");
		try {
			Machine m1 = new Machine(m);
			m1.reset();
			Machine m2 = new Machine(m);
			m2.reset();
			int tapelen=second*2+1;
			Configuration c1 = new Configuration(new int[tapelen],second);
			Configuration c2 = new Configuration(new int[tapelen],second);
			Configuration c3 = new Configuration(new int[tapelen],second);
			for (int j=0; j<first; j++) {
				m1.act(c1);
				c1.setState(m1.getState());
				m2.act(c2);
				c2.setState(m2.getState());
			}
			boolean ok1 = c1.equals(c2);
			System.out.println("c1 equals c2: "+ ok1);
			boolean ok2 = m.yields(c3, c2, first);
			System.out.println("c3 yields c2 in "+first+" steps: "+ ok2);
			boolean nok3 = m.yields(c3, c2, second);
			System.out.println("c3 yields c2 in "+second+" steps: "+ nok3);
			boolean ok4 = m.yields(c3, c2, first);
			System.out.println("c3 yields c2 in "+first+" steps: "+ ok4);
			ok = ok1&&ok2&&!nok3&&ok4;
		} catch(Exception e) {
			System.out.println("ERROR: yieldsTest() failed: "+e.getMessage());
			return false;
		}
		System.out.println(name+" successful.");
		return ok;
	}
	
	public boolean termTest() {
		String name = "Term Test";
		System.out.println("\n"+name+" beginning.");
		Term term1 = new Term(new int[] {1,1,0}, 5);
		Term term2 = new Term(new int[] {1,0}, 4);
		CondensedTape ct = new CondensedTape();
		ct.append(term1);
		ct.append(term2);
		System.out.println(ct.toString());
		System.out.println(ct.expandToString());
		System.out.println(name+" successful.");
		return true;
	}
		
	public boolean termfigurationTest() {
		String name = "Termfiguration Test";
		System.out.println("\n"+name+" beginning.");
		try {
		Termfiguration tf = new Termfiguration(new int[] {0,1,1}, new int[] {0,1});
		System.out.println("tf: "+tf.toString());
		System.out.println("tf at 5:"+Tools.toString(tf.evalAt(5)));
		} catch(Exception e) {
			System.out.println("ERROR: termfigurationTest() failed: "+e.getMessage());
			return false;
		}
		System.out.println(name+" successful.");
		return true;
	}

	public boolean configurationReadTest() {
		String name = "Configuration Read Test";
		System.out.println("\n"+name+" beginning.");
		try {
			String s1 = "C o110110110110110110110110110";
			String s2 = "C 110110110110110110110110110o";
			Configuration c1 = new Configuration(s1);
			System.out.println("In configurationReadTest(), c1: "+c1.toString());
			Configuration c2 = new Configuration(s2);
			System.out.println("In configurationReadTest(), c2: "+c2.toString());
			boolean p = _m.yields(c1, c2, 27);
			if (p)	System.out.println(name+" successful.");
			return p;
		} catch(Exception e) {
			System.out.println("ERROR: configurationReadTest() failed: "+e.getMessage());
			return false;
		}
	}

	public boolean leftToRightInductionTest() {
		String name = "Left to Right Induction Test";
		System.out.println("\n"+name+" beginning.");
		try {
			int[] base1 = {0,1,1};
			int[] exp1 = {9,1};
			int[] index1 = {0};
			int[] base2 = {1,1,0};
			int[] exp2 = {9,1};
			int[] index2 = {27,3};
			int[] numSteps = {27,3};
			Termfiguration t1 = new Termfiguration(base1,exp1,index1,2);
			Termfiguration t2 = new Termfiguration(base2,exp2,index2,2);
			System.out.println(t1.toString());
			System.out.println(t2.toString());
			_lemma1 = new Lemma(_m,t1,t2,numSteps);
			System.out.println(t1+" has successor "+t1.successor());
			System.out.println(t2+" has successor "+t2.successor());
			boolean p = _lemma1.isProved();
			if (p) System.out.println(name+" successful.");
			return p;
		} catch(Exception e) {
			System.out.println("ERROR: leftToRightInductionTest() failed: "+e.getMessage());
			return false;
		}
	}
	
	public boolean rightToLeftInductionTest() {
		String name = "Right to Left Induction Test";
		System.out.println("\n"+name+" beginning.");
		try {
			int[] base1 = {1,1,0};
			int[] exp1 = {0,2};
			int[] index1 = {-1,6};
			int[] base2 = {1,0,1};
			int[] exp2 = {0,2};
			int[] index2 = {-1};
			int[] numSteps = {0,10};
			Termfiguration t1 = new Termfiguration(base1,exp1,index1,0);
			Termfiguration t2 = new Termfiguration(base2,exp2,index2,0);
			System.out.println(t1.toString());
			System.out.println(t2.toString());
			_lemma2 = new Lemma(_m,t1,t2,numSteps);
			System.out.println(t1+" has successor "+t1.successor());
			System.out.println(t2+" has successor "+t2.successor());
			boolean p = _lemma2.isProved();
			if (p) System.out.println(name+" successful.");
			return p;
		} catch(Exception e) {
			System.out.println("ERROR: rightToLeftInductionTest() failed: "+e.getMessage());
			return false;
		}
	}
	
	public boolean successorTest() {
		String name = "Successor Test";
		System.out.println("\n"+name+" beginning.");
		try {
			Termfiguration t3 = new Termfiguration(new int[] {0,1,1},new int[] {1,3,9,1}, new int[] {2, 1, 2}, 0);
			System.out.println(t3+" has successor "+t3.successor());
		} catch (Exception e) {
			System.out.println("ERROR: successorTest() failed: "+e.getMessage());
			return false;
		}
		System.out.println(name+" successful.");
		return true;
	}
	
	public boolean lemmaAsStringTest() {
		String name = "Lemma as String Test";
		System.out.println("\n"+name+" beginning.");
		if (_lemma1==null || _lemma2==null) {
			System.out.println("Must run leftToRightInductionTest() and rightToLeftInductionTest() first to initialize Lemmas");
			return false;
		}
		System.out.println(_lemma1);
		System.out.println(_lemma2);
		String strLemma1 = _lemma1.toString();
		String strLemma2 = _lemma2.toString();
		int lenLemma1 = strLemma1.length();
		int lenLemma2 = strLemma2.length();
		String endLemma1 = strLemma1.substring(lenLemma1-7);
		String endLemma2 = strLemma2.substring(lenLemma2-7);
		System.out.println("The preceding lemmas end with "+ endLemma1 +" and " + endLemma2);
		if (endLemma1.equals("Proved.") && endLemma2.equals("Proved.")) {
			System.out.println(name+" successful.");
			return true;
		}
		return false;
	}
	
	public boolean condensedConfigurationTest() {
		String name = "Condensed Configuration Test";
		System.out.println("\n"+name+" beginning.");
		// C o11011011011011011011011011\
		//   01\
		//   011011011011011011011\
		//   010101\
		//   011011011\
		//   010101\
		//   011010011101101\
		//   011011011011011011011011011011011\
		//   01011010011
		final int[] OII= {0,1,1};
		final int[] OI = {0,1};
		List<Term> termlist = new ArrayList<Term>();
		termlist.add(new Term(OII,9));
		termlist.add(new Term(OI, 1));
		termlist.add(new Term(OII,7));
		termlist.add(new Term(OI, 3));
		termlist.add(new Term(OII,3));
		termlist.add(new Term(OI, 3));
		termlist.add(new Term(new int[] {0,1,1,0,1,0,0,1,1,1,0,1,1,0,1},1));
		termlist.add(new Term(OII,11));
		termlist.add(new Term(new int[] {0,1,0,1,1,0,1,0,0,1,1},1));
		CondensedTape ct = new CondensedTape(termlist);
		System.out.println("ct.toString(): "+ct.toString());
		System.out.println("ct.expandToString(): "+ct.expandToString());
		CondensedConfiguration cc = new CondensedConfiguration(ct, 0, 2);
		System.out.println("cc.toString(): "+cc.toString());
		System.out.println("cc.expandToString(): "+cc.expandToString());
		//Now we compare this CondensedConfiguration to a Configuration
		String s="C o110110110110110110110110110101101101101101101101101010101101101101010101101001110110101101101101101101101101101101101101011010011";
		Configuration c;
		try {
			c = new Configuration(s);
		} catch (Exception e) {
			System.out.println("ERROR: condensedConfigurationTest() failed: "+e.getMessage());
			return false;
		}
		System.out.println("c as a String:       "+c);
		System.out.println("cc equals c: "+cc.equals(c));
		System.out.println(name+" successful.");
		return true;
	}
	
	public boolean actTest1() {
		String name = "Act Test 1";
		System.out.println("\n"+name+" beginning.");
		if (_lemma1==null || _lemma2==null) {
			System.out.println("Must run leftToRightInductionTest() and rightToLeftInductionTest() first to initialize Lemmas");
			return false;
		}
		final int[] OII= {0,1,1};
		List<Term> termlist = new ArrayList<Term>();
		termlist.add(new Term(OII,12));
		CondensedTape ct = new CondensedTape(termlist);
		CondensedConfiguration cc = new CondensedConfiguration(ct, 0, 2);
		List<Lemma> protolemlist = new ArrayList<Lemma>();
		protolemlist.add(_lemma1);
		protolemlist.add(_lemma2);
		LemmaList lemlist;
		try {
			lemlist = new LemmaList(protolemlist);
			Acceleration.act(cc, lemlist);
			System.out.println("cc after Acceleration.act(): "+cc);
		} catch (Exception e) {
			System.out.println("ERROR: actTest1() failed: "+e.getMessage());
			return false;
		}
		System.out.println(name+" successful.");
		return true;
	}
	
	public boolean actTest2() {
		String name = "Act Test 2";
		System.out.println("\n"+name+" beginning.");
		final int[] OII= {0,1,1};
		final int[] OI = {0,1};
		
		List<Term> termlist = new ArrayList<Term>();
		termlist.add(new Term(OII,12));
		CondensedTape ct = new CondensedTape(termlist);
		CondensedConfiguration cc = new CondensedConfiguration(ct, 0, 2);
		int[] base1 = {0,1,1};
		int[] exp1 = {0,1};
		int[] index1 = {0};
		int[] base2 = {1,1,0};
		int[] exp2 = {0,1};
		int[] index2 = {0,3};
		int[] numSteps = {0,3};
		Termfiguration t1 = new Termfiguration(base1,exp1,index1,2);
		Termfiguration t2 = new Termfiguration(base2,exp2,index2,2);
		System.out.println(t1.toString());
		System.out.println(t2.toString());
		
		int[] base3 = {1,1,0};
		int[] exp3 = {0,2};
		int[] index3 = {-1,6};
		int[] base4 = {1,0,1};
		int[] exp4 = {0,2};
		int[] index4 = {-1};
		int[] numSteps2 = {0,10};
		Termfiguration t3 = new Termfiguration(base3,exp3,index3,0);
		Termfiguration t4 = new Termfiguration(base4,exp4,index4,0);
		System.out.println(t1.toString());
		System.out.println(t2.toString());	


		List<Term> termlist2 = new ArrayList<Term>();
		termlist2.add(new Term(OII,9));
		termlist2.add(new Term(OI, 1));
		termlist2.add(new Term(OII,7));
		termlist2.add(new Term(OI, 3));
		termlist2.add(new Term(OII,3));
		termlist2.add(new Term(OI, 3));
		termlist2.add(new Term(new int[] {0,1,1,0,1,0,0,1,1,1,0,1,1,0,1},1));
		termlist2.add(new Term(OII,11));
		termlist2.add(new Term(new int[] {0,1,0,1,1,0,1,0,0,1,1},1));
		CondensedTape ct2 = new CondensedTape(termlist2);
		CondensedConfiguration cc2 = new CondensedConfiguration(ct2, 0, 2);

		
		try {
			_lemma3 = new Lemma(_m,t1,t2,numSteps);
			_lemma4 = new Lemma(_m,t3,t4,numSteps2);
			System.out.println("_lemma4: "+_lemma4);
			List<Lemma> protolemlist = new ArrayList<Lemma>();
			protolemlist.add(_lemma3);
			LemmaList lemlist = new LemmaList(protolemlist);
			Acceleration.act(cc, lemlist);
			System.out.println("cc2.toString(): "+cc2.toString());
			Acceleration.act(cc2, lemlist);
			System.out.println("cc2.toString(): "+cc2.toString());
		} catch (Exception e) {
			System.out.println("ERROR: actTest2() failed: "+e.getMessage());
			return false;
		}
		System.out.println(name+" successful.");
		return true;
	}
	
	public boolean stretchTapeTest(int num) {
		if (num==0) {
			boolean ok = true;
			for (int i=1; i<_machineList.size(); i++)
				ok &= stretchTapeTest(i);
			return ok;
		}
		String name = "Stretch Tape Test";
		System.out.println("\n"+name+" beginning.");
		StretchTape t1 = new StretchTape();
		Machine m = _machineList.get(num);
		m.reset();
		for (int i=0; i<50; i++)
			try {
				m.act(t1);
				System.out.println((char)(m.getState()+65)+" "+t1);
			} catch (Exception e) {
				System.out.println("ERROR: stretchTapeTest() failed: "+e.getMessage());
				return false;
			}
		System.out.println(name+" successful.");
		return true;
	}
	
	public boolean bigStretchTapeTest(int num) {
		String name = "Big Stretch Tape Test";
		System.out.println("\n"+name+" beginning.");

		boolean ok = true;
		if (num==0) {
			for (int i=1; i<_machineList.size(); i++)
				ok &= bigStretchTapeTestHelper(i);
		}
		else ok = bigStretchTapeTestHelper(num);
		System.out.println(name+" successful.");
		return ok;
	}
	
	/**Sets the machine in motion, then looks at how much the stretch increases beyond a certain point.*/
	private boolean bigStretchTapeTestHelper(int num) {
		int numDataPts = 20;
		int startingPoint = 1000000;
		int increment = 100000;
		int[] results = new int[numDataPts];
		Machine m=_machineList.get(num);
		m.reset();
		StretchTape t = new StretchTape(startingPoint+numDataPts*increment);
		try {
			int j;
			for (j=0; j<startingPoint; j++) m.act(t);
			int k;
			for (k=0; k<numDataPts; k++) {
				results[k]=t.getRange();
				for (j=startingPoint+increment*k; j<startingPoint+increment*(k+1); j++) m.act(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: Big Stretch Tape Test failed: "+e.getMessage());
			return false;
		}
		System.out.println("Machine #"+num+": "+Tools.toString(results));
		System.out.println("Differences: "+Tools.toString(Tools.differences(results)));
		return true;
	}
	
	/**Tests for the functionality of tracking the periods when the machines are increasing the ranges of the StretchTapes.*/
	public boolean bigStretchTapeTest2(int num) {
		String name = "Big Stretch Tape Test 2";
		System.out.println("\n"+name+" beginning.");
		boolean ok = true;
		if (num==0) {
			for (int i=1; i<_machineList.size(); i++)
				ok &= bigStretchTapeTest2Helper(i);
		}
		else {
			ok = bigStretchTapeTest2Helper(num);
		}
		if (ok) System.out.println(name+" successful.");		
		return ok;
	}
	
	private boolean bigStretchTapeTest2Helper(int num) {
		int startingPoint = 1000000;
		int numSteps = 10000000;
		int maxNumDataPts = 30;
		Machine m=_machineList.get(num);
		m.reset();
		int tapeLen = numSteps/1650;
		if (num==5||num==18) tapeLen*=3;
		StretchTape t = new StretchTape(tapeLen);//risky
		//old: /250, risky
		List<Integer> starts = new ArrayList<Integer>();
		List<Integer> stops = new ArrayList<Integer>();
		int j=0;
		try {
			//We disregard what happens on the first few steps.
			for (j=0; j<startingPoint; j++) m.act(t);
			//Continue until either numsteps or maxNumDataPts is reached.
			boolean isPushing = t.getJustPushed();
			for (; j<numSteps; j++) {
				m.act(t);
				if (!isPushing && t.getJustPushed()) {
					starts.add(j);
					isPushing=true;
				}
				else if (isPushing && !t.getJustPushed()) {
					stops.add(j);
					isPushing = false;
				}
				if (starts.size()>=maxNumDataPts) break;
			}
			System.out.print("HNR#"+num+": ");
			//System.out.println("Starts: "+starts);
			//System.out.println("Stops: "+stops);
			//System.out.println("Differences: "+Tools.differences(stops,starts));
			System.out.println(Tools.differences(starts));
			if (t.getBorked()) System.out.println(""
					+ "...but it is borked because it had to push left beyond index 0. "
					+ "Try allocating more tape at the outset.");
			
		}
		catch (Exception e) {
			System.out.println("ERROR: Big Stretch Tape Test 2 failed: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean allProvedTest(int num) {
		boolean ok = true;
		String name = "All proved test";
		System.out.println("\n"+name+" beginning for num="+num);
		
		if (num>0&&num<_machineList.size()) {
			System.out.println("Looking for a loop for machine #"+num+":");
			Machine m = _machineList.get(num);
			try {ok &= Acceleration.lookForTwoLoops(m).allProved();}
			catch (Exception e1) {e1.printStackTrace();return false;}
		}
		else if (num==0) {
			System.out.println("Looking for a loop for all machines.");
			for (int i=1; i<_machineList.size(); i++) {
				System.out.println("Machine #"+i+":");
				Machine m = _machineList.get(i);
				try {ok &= Acceleration.lookForTwoLoops(m).allProved();}
				catch (Exception e1) {e1.printStackTrace(); return false;}
			}
		}
		else {
			System.out.println("Invalid number "+num+" passed to allProvedTest(); should be from 0 to "+(_machineList.size()-1));
			return false;
		}
		if (ok) System.out.println(name+" successful.");
		return ok;
	}
	
	public boolean longestRunTest(int num, int start, int stop) {
		String name = "Longest run test";
		System.out.println("\n"+name+" beginning for num="+num);
		boolean ok = true;
		if (num==0) for (int i=1; i<_machineList.size(); i++) ok &= longestRunTestHelper (i, start, stop);
		else if (num>0&&num<_machineList.size()) ok = longestRunTestHelper (num, start, stop);
		else {
			System.out.println("Invalid number "+num+" passed to allProvedTest(); should be from 0 to "+(_machineList.size()-1));
			ok = false;
		}
		if (ok) System.out.println(name+" successful.");
		else System.out.println(name+" failed.");
		return ok;
	}
	
	public boolean longestRunTestHelper(int num, int start, int stop) {
		System.out.println("Looking for the longest run from "+start+" to "+stop+" for machine #"+num+":");
		Machine m = _machineList.get(num);
		int[] ret;
		ret = Acceleration.longestRun(m, start, stop, -1);
		System.out.println("The longest left run started at "+ret[0]+" and was of length "+ret[1]);
		Acceleration.runPattern(m,ret[0],ret[0]+ret[1]);
		ret = Acceleration.longestRun(m, start, stop, 1);
		System.out.println("The longest right run started at "+ret[0]+" and was of length "+ret[1]);
		Acceleration.runPattern(m,ret[0],ret[0]+ret[1]);
		ret = Acceleration.longestRun(m, start, stop, 0);
		System.out.println("The longest run started at "+ret[0]+" and was of length "+ret[1]);
		int[][] patternArray = Acceleration.bestPattern(m, start, stop, 30);
		if (patternArray == null) return false;
		Lemma lem = Acceleration.guessLemma(m, patternArray);
		if (lem == null) return false;
		if (lem.isProved()) System.out.println("The Lemma was proved!");
		else System.out.println("The Lemma was not proved.");
		return true;
	}
}














