package machine;

import java.util.ArrayList;
import java.util.Arrays;
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
	public static String toShortString(int[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<arr.length; i++) {
			sb.append(arr[i]);
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
	public static int comb(int n , int r) {
		if(r<0 || r>n) {
			System.out.println("Warning: comb("+n+","+r+")");
			return 0;
		}
		if(r==0 || r==n) return 1;
		return comb(n-1,r)+comb(n-1,r-1);
	}
	public static int pow(int a, int b) {
		if (b<0) {
			System.out.println("Warning: Integer exponentiation for "+a+"^"+b+"?");
			return 0;
		}
		int result = 1;
		for (int i = 1; i <= b; i++) {
			result *= a;
		}
		return result;
	}
	public static long longPow(long a, int b) {
		if (b<0) {
			System.out.println("Warning: Integer exponentiation for "+a+"^"+b+"?");
			return 0;
		}
		long result = 1;
		for (int i = 1; i <= b; i++) {
			result *= a;
		}
		return result;
	}
	public static int[] shiftBy(int[] coeffs, int h) {
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
		if (aTrim.length!=bTrim.length) return false;
		for (int i=0; i<aTrim.length; i++) if (aTrim[i]!=bTrim[i]) return false;
		return true;
	}
	public static boolean areIdentical(int[] a, int[] b) {
		if (a.length!=b.length) return false; 
		for (int i=0; i<a.length; i++) if (a[i]!=b[i]) return false;
		return true;
	}
	
	/**Returns an equivalent array with 0s trimmed off the end.
	 * Does not mutate the array passed in.*/
	public static int[] trimEnd(int[] arr) {
		int lastNonzeroIndex = arr.length-1;
		while (lastNonzeroIndex>=0 && arr[lastNonzeroIndex]==0) lastNonzeroIndex--;
		int[] retVal = new int[lastNonzeroIndex+1];
		//retVal is thus length 0 when arr is any zero array
		for (int i=0; i<=lastNonzeroIndex; i++) retVal[i]=arr[i];
		return retVal;
	}
	
	/**Returns an equivalent array with 0s trimmed off the beginning.
	 * Does not mutate the array passed in.*/
	public static int[] trimBeginning(int[] arr) {
		int firstNonzeroIndex = 0;
		while (firstNonzeroIndex<arr.length && arr[firstNonzeroIndex]==0) firstNonzeroIndex++;
		int[] retVal = new int[arr.length - firstNonzeroIndex];
		//retVal is thus length 0 when arr is any zero array
		for (int i=firstNonzeroIndex; i<arr.length; i++) retVal[i-firstNonzeroIndex]=arr[i];
		return retVal;
	}
	
	public static int[] add(int[] a, int[] b) {
		if (a == null || b == null) return null;
		int length = Math.max(a.length, b.length);
		int[] c = new int[length];
		for (int i=0; i<length; i++) {
			if (i>=a.length) c[i]=b[i];
			else if (i>=b.length) c[i]=a[i];
			else c[i]=a[i]+b[i];
		}
		return c;
	}
	
	public static int[] subtract(int[] a, int[] b) {
		if (a == null || b == null) return null;
		int length = Math.max(a.length, b.length);
		int[] c = new int[length];
		for (int i=0; i<length; i++) {
			if (i>=a.length) c[i]=-b[i];
			else if (i>=b.length) c[i]=a[i];
			else c[i]=a[i]-b[i];
		}
		return c;
	}
	
	public static int[] negative(int[] a) {
		if (a == null) return null;
		int length = a.length;
		int[] c = new int[length];
		for (int i=0; i<length; i++) {
			c[i] = -a[i];
		}
		return c;
	}
	
	public static int[] multiply(int scalar, int[] arr) {
		int[] retVal = new int[arr.length];
		for (int i=0; i<arr.length; i++) retVal[i]=scalar*arr[i];
		return retVal;
	}
	public static int[] iterate(int[] arr, int numTimes) {
		if (arr==null) {
			System.out.println("Cannot iterate null array "+numTimes+" times.");
			return null;
		}
		if (numTimes<0) {
			System.out.println("Cannot iterate "+arr.toString()+" "+numTimes+" times.");
			return null;
		}
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
	public static char asLR(int n) {
		if (n==-1) return 'L';
		if (n== 1) return 'R';
		return '?';
	}
	public static String matrixToString(int[][] mat) {
		return matrixToString(mat, false);
	}
	public static String matrixToString(int[][] mat, boolean inverted) {
		String s="";
		if (inverted)
			for (int j=0; j<mat[0].length; j++) {
				for (int i=0; i<mat.length; i++) {
					s+=mat[i][j]+"\t";
				}
				s+='\n';
			}			
		else
		for (int i=0; i<mat.length; i++) {
			for (int j=0; j<mat[i].length; j++) {
				s+=mat[i][j]+"\t";
			}
			s+='\n';
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
	
	public static CondensedConfiguration[] skipArray(CondensedConfiguration[] arr, int skip, int start) {
		if (arr == null) {System.out.println("In Tools.skipArray(): arr is null"); return null;}
		if (skip <= 0) {System.out.println("In Tools.skipArray(): skip "+skip+" <= 0"); return null;}
		if (start < 0) {System.out.println("In Tools.skipArray(): start "+start+" < 0"); return null;}
		if (start >= arr.length) {System.out.println("In Tools.skipArray(): start "+start+" >= arr.length = "+arr.length); return null;}
		int retlen;
		try {retlen = Tools.ceilingDivide(arr.length - start, skip);} catch (Exception e) {return null;}
		CondensedConfiguration[] ret = new CondensedConfiguration[retlen];
		for (int i=0; i<retlen; i++) ret[i] = arr[start + i*skip];
		return ret;
	}


	public static int[] skipArray(int[] arr, int skip, int start) {
		if (arr == null) {System.out.println("In Tools.skipArray(): arr is null"); return null;}
		if (skip <= 0) {System.out.println("In Tools.skipArray(): skip "+skip+" <= 0"); return null;}
		if (start < 0) {System.out.println("In Tools.skipArray(): start "+start+" < 0"); return null;}
		if (start >= arr.length) {System.out.println("In Tools.skipArray(): start "+start+" >= arr.length = "+arr.length); return null;}
		int retlen;
		try {retlen = Tools.ceilingDivide(arr.length - start, skip);} catch (Exception e) {return null;}
		int[] ret = new int[retlen];
		for (int i=0; i<retlen; i++) ret[i] = arr[start + i*skip];
		return ret;
	}
	
	/**Returns a (possibly jagged) 2D array consisting of the first differences of a with skip skip.
	 * Its first array will have it for the sequence starting with index 0, and so on.*/
	public static int[][] firstDifferenceArraysWithSkip(int[] a, int skip) {
		if (a == null) {System.out.println("In Tools.firstDifferenceArraysWithSkip(): a is null"); return null;}
		if (skip <= 0) {System.out.println("In Tools.firstDifferenceArraysWithSkip(): skip "+skip+" <= 0"); return null;}
		int[][] ret = new int[skip][];
		for (int res = 0; res < skip; res++) {
			ret[res] = Tools.differences(Tools.skipArray(a, skip, res));
		}
		return ret;
	}
	
	/**Returns a (possibly jagged) 2D array consisting of the degth differences of a with skip skip.
	 * Its first array will have it for the sequence starting with index 0, and so on.*/
	public static int[][] secondDifferenceArraysWithSkip(int[] a, int skip) {
		if (a == null) {System.out.println("In Tools.secondDifferenceArraysWithSkip(): a is null"); return null;}
		if (skip <= 0) {System.out.println("In Tools.secondDifferenceArraysWithSkip(): skip "+skip+" <= 0"); return null;}
		int[][] firstDifferenceArrays = firstDifferenceArraysWithSkip(a, skip);
		int[][] ret = new int[skip][];
		for (int i=0; i<skip; i++) {
			ret[i] = Tools.differences(firstDifferenceArrays[i]);
			if (ret[i] == null) System.out.println("Warning: in Tools.secondDifferenceArraysWithSkip(): "
					+ "ret["+i+"] is null (a = "+Tools.toString(a)+" too short)");
		}
		return ret;
	}
	
	public static int[] concatenate(int[] first, int[] second) {
		int[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	/**The array should be rectangular (not jagged),
	 * and none of the array's dimensions should be 0.*/
	public static int[][][] threeDeepCopy(int[][][] array) {
		int m = array.length, n = array[0].length, r = array[0][0].length;
		int[][][] ret = new int[m][n][r];
		for (int i=0; i<m; i++)
			for (int j=0; j<n; j++)
				for (int k=0; k<r; k++)
					ret[i][j][k] = array[i][j][k];
		return ret;
	}
	
	public static int[] reverse(int[] array) {
		int[] ret = new int[array.length];
		for (int i=0; i<array.length; i++) {
			ret[i] = array[array.length - 1 - i];
		}
		return ret;
	}
	
	public static String ignore(String s, char c, int side) {
		if (side != -1 && side != 1) return null;
		int i=0;
		if (side == -1) {
			for (i=0; i<s.length(); i++) {
				if (s.charAt(i)!=c) break;
			}
			if (i>=s.length()) return "";
			return s.substring(i);
		}
		if (side == 1) {
			for (i=s.length()-1; i>=0; i--) {
				if (s.charAt(i)!=c) break;
			}
			if (i<0) return "";
			return s.substring(0,i+1);
		}
		return null;
	}
	
	/**Determines whether base is a repetition of its first n terms.*/
	public static boolean isRepeatOfFirst(int[] base, int n) {
		int len = base.length;
		if (n <= 0 || n > len) return false;
		if (len % n != 0) return false;
		int numChunks = len / n;
		for (int i=0; i<n; i++) {
			int bit = base[i];
			for (int j=1; j<numChunks; j++) {
				if (base[i + n * j] != bit) return false;
			}
		}
		return true;
	}
	
	/**Determines whether base is a repetition of its last n terms.*/
	public static boolean isRepeatOfLast(int[] base, int n) {
		int len = base.length;
		if (n <= 0 || n > len) return false;
		if (len % n != 0) return false;
		int numChunks = len / n;
		for (int i=0; i<n; i++) {
			int bit = base[len - 1 - i];
			for (int j=1; j<numChunks; j++) {
				if (base[len - 1 - i - n * j] != bit) return false;
			}
		}
		return true;		
	}
	
	/**CoeffList should be given in increasing order of power of the variable.*/
	public static int degree (int[] coeffList) {
		if (coeffList == null) return -1;
		int degree = coeffList.length - 1;
		while (degree >= 0 && coeffList[degree] == 0) degree --;
		return degree;
	}
	
	/**Naive implementation*/
	public static int gcf(int a, int b) {
		int aa = Math.abs(a), bb = Math.abs(b);
		if (aa == 0 && bb == 0) {
			System.out.println("Warning: gcf(0,0)");
			return 0;
		}
		if (aa == 0) return bb;
		if (bb == 0) return aa;
		int max = 1;
		int cc = Math.min(aa, bb);
		for (int i = 1; i <= cc; i++) {
			if (aa % i == 0 && bb % i == 0) max = i;
		}
		return max;
	}
	
	public static int lcm(int a, int b) {
		if (a == 0 || b == 0) {
			System.out.println("Warning: lcm with 0");
			return 0;
		}
		return a * b / gcf(a, b);
	}
	
	/**Shifts all elements of arr by disp.
	 * Puts 0s in the new places,
	 * and returns an array consisting of what fell off the edge.*/
	public static int[] shiftAllByAndReturnFallen(int[] arr, int disp) {
		if (disp == 0) return new int[0];
		else if (disp > arr.length || disp < -arr.length) {
			System.out.println("In Tools.shiftAllByAndReturnFallen(): |disp| too large");
			return null; 
		}
		else if (disp > 0) {
			int[] ret = new int[disp];
			System.arraycopy(arr, arr.length - disp, ret, 0, disp);
			int i;
			for (i = arr.length - 1; i>=disp; i--) 
				arr[i] = arr[i - disp];
			for (; i>=0; i--)
				arr[i] = 0;
			return ret;
		}
		else { //disp < 0
			int[] ret = new int[-disp];
			System.arraycopy(arr, 0, ret, 0, -disp);
			int i;
			for (i = 0; i<arr.length + disp; i++) 
				arr[i] = arr[i - disp];
			for (; i<arr.length; i++)
				arr[i] = 0;
			return ret;
		}
	}
	
	public static int[] rotated(int[] arr, int disp) {
		disp %= arr.length;
		int[] arrClone = arr.clone();
		if (disp == 0) return arrClone;
		int[] fragment = shiftAllByAndReturnFallen(arrClone, disp);
		if (disp > 0)
			System.arraycopy(fragment, 0, arrClone, 0, disp);
		else
			System.arraycopy(fragment, 0, arrClone, arrClone.length + disp, -disp);
		return arrClone;
	}
	
	public static boolean areNonnegative(int[] arr) {
		if (arr==null) {
			System.out.println("In Tools.areNonnegative(): arr is null");
			return false;
		}
		for (int i=0; i<arr.length; i++) {
			if (arr[i]<0) return false;
		}
		return true;
	}
	
	public static boolean arePositive(int[] arr) {
		if (arr==null) {
			System.out.println("In Tools.arePositive(): arr is null");
			return false;
		}
		for (int i=0; i<arr.length; i++) {
			if (arr[i]<=0) return false;
		}
		return true;
	}
	
	public static boolean isIncreasing(int[] arr) {
		if (arr==null) {
			System.out.println("In Tools.isIncreasing(): arr is null");
			return false;
		}
		int[] diff = Tools.differences(arr);
		return arePositive(diff);
	}
	
	public static boolean isConstant(int[] arr) {
		if (arr == null) {
			System.out.println("In Tools.isConstant(): arr is null");
			return false;
		}
		if (arr.length == 0) return true;
		int val = arr[0];
		for (int i = 1; i < arr.length; i++) if (arr[i] != val) return false;
		return true;
	}
	
	public static boolean areConstant(int[][] mat) {
		if (mat == null) {
			System.out.println("In Tools.areConstant(): mat is null");
			return false;
		}
		for (int i = 0; i < mat.length; i++) {
			if (mat[i] == null) {
				System.out.println("In Tools.areConstant(): mat[" + i + "] is null");
				return false;				
			}
			if (!isConstant(mat[i])) return false;
		}
		return true;
	}
}
