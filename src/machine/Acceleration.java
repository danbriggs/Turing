package machine;

public class Acceleration {
	//This class is supposed to have the methods for making a machine act on a CondensedConfiguration using a LemmaList.
	public static int act(CondensedConfiguration cc, LemmaList lemlist) throws Exception {
		int[] termNumAndIndex = cc.termNumAndIndex();
		int termNum = termNumAndIndex[0];
		int indexInTerm = termNumAndIndex[1];
		if (termNum==-1) throw new Exception("index "+indexInTerm+" out of bounds for CondensedConfiguration "+cc);
		Term currTerm = cc.getTermList().get(termNum);
		int side=0;//-1 for leftmost bit of term, 1 for rightmost bit, 0 for in between
		if (indexInTerm==0) side=-1;
		if (indexInTerm==currTerm.length()-1) side=1;
		if (side!=0) {
			//Here is where we may have occasion to use a Lemma
			Integer[] pair = lemlist.firstMatchAndValue(currTerm,side,cc.getState());
			if (pair[0]!=-1) {
				int matchIndex = pair[0];
				int nToUse = pair[1];
				Lemma lemmaToUse = lemlist.getLemList().get(matchIndex);
				Termfiguration source = lemmaToUse.getSource();
				Termfiguration target = lemmaToUse.getTarget();
				/*System.out.println("We got one! "+matchIndex+"\n"+
				lemmaToUse+
				"\nworks with n="+nToUse+
				"\nto be "+currTerm);*/ //this is debug code
				cc.replace(termNum, target.toTermAt(nToUse));
				//System.out.println("source.getIndex(): "+Tools.toPolynomialString(source.getIndex(),'n'));
				//System.out.println("target.getIndex(): "+Tools.toPolynomialString(target.getIndex(),'n'));
				int indexAtSource = Tools.evalAt(source.getIndex(),nToUse);
				int indexAtTarget = Tools.evalAt(target.getIndex(),nToUse);
				int toMoveBy = indexAtTarget-indexAtSource;
				//System.out.println("toMoveBy: "+toMoveBy);
				cc.setIndex(cc.getIndex()+toMoveBy);
				cc.setState(target.getState());
				int numSteps = Tools.evalAt(lemmaToUse.getNumSteps(),nToUse);
				System.out.println("numSteps: "+numSteps);
				return numSteps;
			}
		}
		else {
			//Just act for one step
		}
		return 0;
	}
}
