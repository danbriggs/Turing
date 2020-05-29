package machine;

import java.util.ArrayList;
import java.util.List;

public class Tools {
	public static final int[][][] HNR1 = {
			{{1,1},{-1,-1},{2,4}},
			{{1,1},{-1,-1},{-1,3}},
			{{1,0},{1,-1},{3,3}},
			{{1,1},{-1,1},{0,4}},
			{{0,0},{-1,1},{1,2}}};

	public static String signed(int num) {
		if (num<0) return ""+num;
		return "+"+num;
	}
	public static String signedSuppressed(int num) {
		//Leaves out the 1 from -1 and +1. Useful for nonconstant coefficients.
		if (num==-1) return "-";
		if (num==1) return "+";
		if (num<-1) return ""+num;
		if (num>1) return "+"+num;
		return "";
	}
	public static String toString(int[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<arr.length; i++) {
			sb.append(arr[i]);
			sb.append(' ');
		}
		return sb.toString();
	}
	public static String toPolynomialString(int[] coeffs, char var){
		if (coeffs==null) return null;
		StringBuffer sb = new StringBuffer();
		for (int currPower = 0; currPower<coeffs.length; currPower++) {
			if (coeffs[currPower]==0) continue;
			if (currPower==0) sb.append(signed(coeffs[currPower]));
			else sb.append(signedSuppressed(coeffs[currPower]));
			if (currPower>0) sb.append(var);
			if (currPower>1) sb.append("^"+currPower);
		}
		if (!(sb.length()>0)) {sb.append("0"); return sb.toString();}
		if (sb.charAt(0)=='+') return sb.substring(1);
		return sb.toString();
	}
	public static int minForNonnegative(int[] coeffs) throws Exception {
		//Attempts to determine the minimum nonnegative value of the variable n
		//such that the value of the polynomial with coefficients coeffs in rising order
		//is nonnegative.
		if (!(coeffs.length>=0)) throw new Exception("coeffs uninitialized in minForNonnegative()");
		if (coeffs.length==0) return 0;
		if (coeffs.length==1) {
			if (coeffs[0]>=0) return 0;
			else throw new Exception("Negative constant is never nonnegative in minForNonnegative()");
		}
		if (coeffs.length==2) {//linear
			int m = coeffs[1]; int b = coeffs[0]; //Need to find x such that mx+b>=0
			if (b>0) return 0;
			if (m<=0&&b<0) throw new Exception("Negative line never nonnegative for nonnegative n");
			return ceilingDivide(-b,m);
		}
		throw new Exception("I didn't code minForNonnegative() for case coeffs.length="+coeffs.length+">2");
	}
	public static int ceilingDivide(int a, int b) throws Exception {
		if (b==0) throw new Exception(a+"/"+b+" is division by zero in ceilingDivide");
		return (a+b-1)/b;
	}
	public static int evalAt(int[] coeffs, int n) {
		int currSum = 0;
		int nRaisedToTheCurrPower=1;
		for (int currPower = 0; currPower < coeffs.length; currPower++) {
			currSum += coeffs[currPower]*nRaisedToTheCurrPower;
			nRaisedToTheCurrPower*=n;
		}
		return currSum;
	}
	public static int comb(int n , int r) throws Exception {
		if(r<0 || r>n) throw new Exception("Invalid input: comb("+n+","+r+")");
		if(r==0 || r==n) return 1;
		return comb(n-1,r)+comb(n-1,r-1);
	}
	public static int pow(int a, int b) throws Exception {
		if (b<0) throw new Exception("Integer exponentiation not permitted for "+a+"^"+b);
		int result = 1;
		for (int i = 1; i <= b; i++) {
			result *= a;
		}
		return result;
	}
	public static int[] shiftBy(int[] coeffs, int h) throws Exception {
		//Returns an array consisting of the coefficients
		//of the polynomial resulting by replacing the variable n
		//in the polynomial with coefficients coeffs
		//with n+h. Both coefficient lists are in increasing degree order.
		int[] retVal = new int[coeffs.length];
		for (int k=0; k<coeffs.length; k++) {
			for (int j=0; j<=k; j++) {
				retVal[j]+=coeffs[k]*comb(k,j)*pow(h,k-j);
			}
		}
		return retVal;
	}
	public static boolean equal(int[] a, int[] b) {
		//Determines whether the coefficient lists a and b represent identical polynomials.
		//Both are given in rising order of degree.
		int[] aTrim = trimEnd(a);
		int[] bTrim = trimEnd(b);
		if (aTrim==null && bTrim==null) return true;
		if (aTrim==null || bTrim==null) return false;
		if (aTrim.length!=bTrim.length) return false;
		for (int i=0; i<aTrim.length; i++) if (aTrim[i]!=bTrim[i]) return false;
		return true;
	}
	public static boolean areIdentical(int[] a, int[] b) {
		if (a.length!=b.length) return false; 
		for (int i=0; i<a.length; i++) if (a[i]!=b[i]) return false;
		return true;
	}
	public static int[] trimEnd(int[] arr) {
		int lastNonzeroIndex = arr.length-1;
		while (lastNonzeroIndex>=0 && arr[lastNonzeroIndex]==0) lastNonzeroIndex--;
		if (lastNonzeroIndex<0) return null;
		int[] retVal = new int[lastNonzeroIndex+1];
		for (int i=0; i<=lastNonzeroIndex; i++) retVal[i]=arr[i];
		return retVal;
	}
	public static int[] add(int[] a, int[] b) {
		int length = Math.max(a.length, b.length);
		int[] c = new int[length];
		for (int i=0; i<length; i++) {
			if (i>=a.length) c[i]=b[i];
			else if (i>=b.length) c[i]=a[i];
			else c[i]=a[i]+b[i];
		}
		return c;
	}
	public static int[] multiply(int scalar, int[] arr) {
		int[] retVal = new int[arr.length];
		for (int i=0; i<arr.length; i++) retVal[i]=scalar*arr[i];
		return retVal;
	}
	public static int[] iterate(int[] arr, int numTimes) throws Exception {
		if (arr==null) throw new Exception("Cannot iterate null array "+numTimes+" times.");
		if (numTimes<0) throw new Exception("Cannot iterate "+arr.toString()+" "+numTimes+" times.");
		int[] retVal = new int[arr.length*numTimes];
		for (int i=0; i<numTimes; i++) for (int j=0; j<arr.length; j++) retVal[i*arr.length+j]=arr[j];
		return retVal;
	}
	public static char asArrow(int n) {
		if (n==-1) return '<';
		if (n== 1) return '>';
		return '?';
	}
	public static char asLetter(int n) {
		if (-32<n&&n<65000) return (char)(n+65);
		return '?';
	}
	public static String matrixToString(int[][] mat) {
		String s="";
		for (int i=0; i<mat.length; i++) {
			for (int j=0; j<mat[i].length; j++) {
				s+=mat[i][j]+" ";
			}
			s+='\t';
		}
		return s;
	}
	public static List<Integer> differences(List<Integer> a, List<Integer> b){
		//b-a
		List<Integer> retList = new ArrayList<Integer>();
		int len = Math.min(a.size(), b.size());
		for (int i=0; i<len; i++) retList.add(b.get(i)-a.get(i));
		return retList;
	}
	public static List<Integer> differences(List<Integer> a){
		if (a.size()==0) return null;
		List<Integer> b = new ArrayList<Integer>();
		for (int i=0; i<a.size()-1; i++) b.add(a.get(i+1)-a.get(i));
		return b;
		
	}
	public static int[] differences(int[] a) {
		if (a.length==0) return null;
		if (a.length==1) return new int[0];
		int[] b = new int[a.length-1];
		for (int i=0; i<a.length-1; i++) b[i]=a[i+1]-a[i];
		return b;
	}
}
