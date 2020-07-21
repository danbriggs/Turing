#include <iostream>
#include <fstream>
#include <ctime>
#include "structs.cpp"
#include "oldrun.h"
#include "discounts.h"
#include "run.h"
#include "config.h"
#include "TNF.h"
#include "skelet.h"
#include "runcomp.h"
#include "induction.h"
#include "runcomp2.h"
using namespace std;

int repeats (config& c, int i, int j, int nnj) {
//Determines if c repeats pattern of length j at least nnj times starting at position i
//Tape head can be at beginning or end, but not strictly in between
	int bound; //to be left of
	int ans=1; //to keep track of
	if (c.pos<=i) bound=c.len;
	else bound=c.pos;
	bool happened=1;
	while (i+2*j<=bound && happened) { //We're not changing the REAL i
		for (int index=0; index<j; index++) {
			if (c.t.a[i+index]!=c.t.a[i+j+index]) {
				happened=0;
				break;
			}
		}
		if (happened) {
			ans++;
			i+=j;
		}
	}
	if (ans >= nnj) return ans;
	else return 0;
}

int repeatswithknown (config& c, int i, int j, int given) {
	//Determines how many times c repeats pattern of length j starting at position i,
	//given that it repeats it at least given times.
	//Tape head can be at very beginning or end, but not strictly in between
	int bound; //to be left of
	int ans=given; //to keep track of
	if (c.pos<=i) bound=c.len;
	else bound=c.pos;
	bool happened=1;
	i+=(given-1)*j; //we start within the last one
	while (i+2*j<=bound && happened) { //We're not changing the REAL i
		for (int index=0; index<j; index++) {
			if (c.t.a[i+index]!=c.t.a[i+j+index]) {
				happened=0;
				break;
			}
		}
		if (happened) {
			ans++;
			i+=j;
		}
	}
	return ans;
}

bool fillsout (config& c, int i, int d, int j) {
	bool p=1;
	for (int k=1; k<j/d; k++) {
		for (int l=0; l<d; l++) {
			if (c.t.a[i+l]!=c.t.a[i+k*d+l]) p=0;
		}
	}
	return p;
}

bool clearafter (config& c, int i) {
//Doesn't affect the REAL i
	if (c.pos>i) return 0;
	while (c.t.a[i]==0 && i<c.len) i++;
	if (i==c.len) return 1;
	return 0;
}

void compress (config& c, compconfig& cc, int* nn, int tryhigh) {
//Compresses the configuration c into the compressed configuration cc
//nn is of length tryhigh+1
//nn[i] is the number of repetitions of a pattern of length i to see before writing them together
//See footnote
	int i,j,r,d,place;
	cc.s=c.s;
	cc.len=0;
	cc.a=new compsymbol[c.len]; //in case there are no repeats
	i=0; place=0;
	while (c.t.a[i]==0 && c.pos>i) i++;
	while (i<c.len) {
		if (clearafter(c,i)) break;
		//Try compressing c at i, longer possibilities first
		j=tryhigh;
		bool whetheritdidit=0;
		while (j>=1) {
		//See if the pattern from c.t.a[i] through c.t.a[i+j-1] repeats at least nn[j] times,
		//remembering not to consume the tape head or go past the end.
		//If it doesn't, simply continue with nn[j-1].
		//If it does, we look for the smallest repeating pattern within the tape from c.t.a[i] through c.t.a[i+j-1].
		//in either case, remember to modify i accordingly.
			if (c.pos==i) {
				cc.pos=place;
				cc.side=0;
			}
			r=repeats(c,i,j,nn[j]);
			if (r) {//#of times if true, 0 if false
				for (d=1; d<=j/2; d++) {//Maybe go back here later sometime and do some sqrt stuff
				//Want d to be a divisor of j, the length
					if (j/d*d==j && fillsout(c,i,d,j)) {
						r=repeatswithknown(c,i,d,r*j/d); //Given that the string of length d repeats at least r*j/d times
						j=d;
						break;
					}
				}
				//So now we should make the compsymbol according to the repeats, append it, modify i accordingly, and break
				compsymbol symb;
				symb.baselen=j;
				symb.base=new bool[j];
				for (int index=0; index<j; index++) symb.base[index]=c.t.a[i+index];
				symb.exp=r;
				symb.arrayaddress=place;
				place++;
				appendcompsymbol(cc,symb);
				i+=j*r;
				//Backtracking a little,
				if (c.pos==i-1) {
					cc.pos=place;
					cc.side=1;
				}
				whetheritdidit=1;
				break;
			}
			else j-=1;
		}
		if (!whetheritdidit) {
		//We should just make a compsymbol of length 1, append it, and add 1 to i. If this is c's position, we should let it be cc's as well.
			compsymbol symb;
			symb.baselen=1;
			symb.base=new bool[1];
			symb.base[0]=c.t.a[i];
			symb.exp=1;
			symb.arrayaddress=place;
			if (c.pos==i) {
				cc.pos=place;
				cc.side=0; //or 1
			}
			place++;
			appendcompsymbol(cc,symb);
			i++;
		}
	}
	return;
}

void compress (config& c, compconfig& cc) {
	int* nn=new int[101];
	int tryhigh=100;
	int i;
	for (i=100; i>=7; i--) nn[i]=2;
	nn[6]=nn[5]=nn[4]=3;
	nn[3]=4;
	nn[2]=nn[1]=5;
	compress (c, cc, nn, tryhigh);
	return;
}

void compress2 (config& c, compconfig& cc) {
//short-base compression
	int* nn=new int[5];
	int tryhigh=4;
	nn[1]=6;
	nn[2]=5;
	nn[3]=4;
	nn[4]=3;
	compress (c, cc, nn, tryhigh);
	return;
}


bool conditionc (config& c, int which, int* ss) {
	if (which==0) return conditionc(c);
	else if (which == 1){
		if (c.s!=3) return 0;
		if (ss[0]==0 && ss[1]==4 && ss[2]==2 && ss[3]==3 && ss[4]==2 && ss[5]==3) return 1;
		if (ss[4]==3 && ss[5]==0) return 1;
		if (ss[2]==2 && ss[3]==0 && ss[4]==4 && ss[5]==1) return 1;
		if (ss[3]==0 && ss[4]==2 && ss[5]==3) return 1;
		return 0;
	}
	else {
		if (c.t.a[c.pos]!=1) return 0;
		for (int i=c.pos+1;i<c.len;i++) if (c.t.a[i]!=0) return 0;
		return 1;
	}
	return 0;
}

bool conditionc (config& c) {
//change this condition as you like.
//Currently, the condition that c contains no '1010's.
	if (c.s!=4) return 0;
	if (c.t.a[c.pos]!=0 || c.t.a[c.pos+1]!=0) return 0;
	for (int i=c.pos-1; i>=0; i--)
		if (c.t.a[i]==1) return 0;
	for (int i=0; i<c.len-4; i++)
		if (c.t.a[i]==1 && c.t.a[i+1]==0 && c.t.a[i+2]==1 && c.t.a[i+3]==0) return 0;
	return 1;
}

/* If we have a pattern such as
 00001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111,
 we would almost certainly prefer to write it as {00001111}^{13}.
 However, if we compress according to patterns of length 1 first, accepting 4 in a row, it quickly becomes
 0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4}0^{4}1^{4},
 which is generally less useful.
 On the other hand, if we compress according to largest patterns first, accepting 4 in a row, we get
 {000011110000111100001111}^{4}0^{4}1^{4}, which is also less useful.
 Quantitatively, {00001111}^{13} wins out in that it minimizes the length of the resulting expression, or, the sum of the lengths of the bases.
 This is 8 in this case, and 26 in the others.
 So, we should take in a candidate for the compression, from largest to smallest, see what we can do with one base,
 and then see what the smallest base covering that is, and then cover as much more as we can.
 
*/