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
using namespace std;

void copyabstrsymbol (abstrsymbol &a, abstrsymbol &b) { //That is, copy a into b
	b.baselen=a.baselen;
	b.base=new bool[b.baselen];
	for (int i=0; i<b.baselen; i++) b.base[i]=a.base[i];
	b.exp=a.exp;
	return;
};

void appendabstrsymbol (abstrconfig& c, abstrsymbol& s) {
	//WARNING: Will only work when more space than c.len has been reserved for c
	copyabstrsymbol(s,c.a[c.len]);
	c.a[c.rhsindex].next=&c.a[c.len];
	c.a[c.len].prev=&c.a[c.rhsindex];
	c.a[c.len].arrayaddress=c.len;
	c.rhsindex=c.len;
	c.len++;
}

void copyabstrconfig (abstrconfig &c, abstrconfig &d) {
	copyabstrconfig(c,d,1);
}

void copyabstrconfig (abstrconfig &c, abstrconfig &d, int expansionrate) { //That is, copy c into d
	d.s=c.s;
	d.pos=c.pos;
	d.side=c.side;
	d.len=c.len;
	d.a=new abstrsymbol[d.len*expansionrate];
	int cj=c.lhsindex;
	for (int i=0; i<d.len; i++) {
		d.a[i].arrayaddress=i; //might as well clean it up while we're at it
		copyabstrsymbol(c.a[cj],d.a[i]);
		if (i==0) d.a[i].prev=NULL;
		else d.a[i].prev=&d.a[i-1];
		if (i==d.len-1) d.a[i].next=NULL;
		else d.a[i].next=&d.a[i+1];
		if (i<d.len-1) cj=c.a[cj].next->arrayaddress;
	}
	d.lhsindex=0;
	d.rhsindex=d.len-1;
	return;
};

void copyabstrconfig (abstrconfig &c, abstrconfig &d, int expansionrate, int &pointertokeeptrackof) {
	//That is, copy c into d, modifying pointertokeeptrackof
	d.s=c.s;
	d.pos=c.pos;
	d.side=c.side;
	d.len=c.len;
	d.a=new abstrsymbol[d.len*expansionrate];
	int cj=c.lhsindex;
	for (int i=0; i<d.len; i++) {
		d.a[i].arrayaddress=i; //might as well clean it up while we're at it
		copyabstrsymbol(c.a[cj],d.a[i]);
		if (cj==pointertokeeptrackof) pointertokeeptrackof=i;
		if (cj==c.pos) d.pos=i;
		if (i==0) d.a[i].prev=NULL;
		else d.a[i].prev=&d.a[i-1];
		if (i==d.len-1) d.a[i].next=NULL;
		else d.a[i].next=&d.a[i+1];
		if (i<d.len-1) cj=c.a[cj].next->arrayaddress;
	}
	d.lhsindex=0;
	d.rhsindex=d.len-1;
	return;
};

expr readlinexp (char* st, int &i) {
//Cases:b, aN, aN+b.
//So we check to see if there's a + and/or an N there.
	int p=0; //0 for b, 1 for aN, 2 for aN+b
	int a, b; expr exp; //to hold the answer
	for (int j=i; st[j]!='}'; j++) if (st[j]=='+' || st[j]=='N') p++; //So that we don't have to modify i while we're looking
	switch (p) {
		case 0:
			b=chartodigit(st[i]);
			i++;
			while (st[i]!='}') {
				b*=10;
				b+=chartodigit(st[i]);
				i++;
			}
			exp.a=0;
			exp.b=b;
			return exp;			
			break;
		case 1://possibility that it's just N
			if (st[i]=='N')
				a=1;
			else {
				a=chartodigit(st[i]);
				i++;
				while (st[i]!='N') {
					a*=10;
					a+=chartodigit(st[i]);
					i++;
				}
			}
			i++;
			exp.a=a;
			exp.b=0;
			return exp;		
			break;
		case 2://possibility that it's just N+b
			if (st[i]=='N')
				a=1;
			else {
				a=chartodigit(st[i]);
				i++;
				while (st[i]!='N') {
					a*=10;
					a+=chartodigit(st[i]);
					i++;
				}
			}
			i+=2;
			exp.a=a;
			b=chartodigit(st[i]);
			i++;
			while (st[i]!='}') {
				b*=10;
				b+=chartodigit(st[i]);
				i++;
			}
			exp.b=b;
			return exp;			
			break;
	}
	return exp;
}

void readin (char* st, int len, int& i, abstrconfig& c, int& ci) {
	int j;
	switch (st[i]) {
		case '<':
			c.pos=ci-1;
			c.side=1;
			i++;
			break;
		case '>':
			c.pos=ci;
			c.side=0;
			i++;
			break;
		case '{':
			i++;
			j=0;
			while (st[i]=='0' || st[i]=='1') {
				i++;
				j++;
			}
			i-=j;
			c.a[ci].base=new bool[j];
			c.a[ci].baselen=j;
			cout << "i=" << i << ".\n";
			int k=i;
			for (i=i; i<k+j; i++) {
				if (st[i]=='0') c.a[ci].base[i-k]=0;
				else c.a[ci].base[i-k]=1;
			}
			i+=3;
			c.a[ci].exp=readlinexp(st,i);
			c.a[ci].arrayaddress=ci;
			if (ci>0) c.a[ci].prev=&c.a[ci-1];
			else c.a[ci].prev=NULL;
			if (ci<c.len-1) c.a[ci].next=&c.a[ci+1];
			else c.a[ci].next=NULL;
			ci++;
			i++;
			break;
		default: //0 or 1
			if (st[i+1]!='^') {
				c.a[ci].base=new bool[1];
				if (st[i]=='0') c.a[ci].base[0]=0;
				else c.a[ci].base[0]=1;
				c.a[ci].baselen=1;
				c.a[ci].exp.a=0;
				c.a[ci].exp.b=1;
				c.a[ci].arrayaddress=ci;
				if (ci>0) c.a[ci].prev=&c.a[ci-1];
				else c.a[ci].prev=NULL;
				if (ci<c.len-1) c.a[ci].next=&c.a[ci+1];
				else c.a[ci].next=NULL;
				ci++;
				i++;
				break;				
			}
			else {
				c.a[ci].base=new bool[1];
				if (st[i]=='0') c.a[ci].base[0]=0;
				else c.a[ci].base[0]=1;
				c.a[ci].baselen=1;
				i+=3;
				c.a[ci].exp=readlinexp(st,i);
				c.a[ci].arrayaddress=ci;
				if (ci>0) c.a[ci].prev=&c.a[ci-1];
				else c.a[ci].prev=NULL;
				if (ci<c.len-1) c.a[ci].next=&c.a[ci+1];
				else c.a[ci].next=NULL;
				ci++;
				i++;				
			}
			break;
	}
	return;
}

bool abstrconfigin (char* st, int len, abstrconfig& c) {
	//1 if a valid configuration, 0 otherwise
	int depth=0;
	bool inexp=0;
	int numsymbs=0;
	bool hasonecarat=0;
	bool onenplusperexp=0;
	for (int i=0; i<len-2; i++) {
		cout << "NOW VERIFYING CHARACTER NUMBER " << i << ".\n";
		switch (st[i]) {
			case 'N':
				if (depth==0 || !inexp) {cout << "ERR: Cannot use variable symbol N outside of exponent at position " << i << ".\n"; return 0;}
				if (onenplusperexp) {cout << "ERR: Cannot use second variable symbol N in single exponent at position " << i << ".\n"; return 0;}
				onenplusperexp=1;
				break;
			case '+':
				if (depth==0 || !inexp) {cout << "ERR: Cannot use operator symbol + outside of exponent at position " << i << ".\n"; return 0;}
				if (st[i-1] != 'N') {cout << "ERR: Cannot have + at position " << i << " without N before it.\n"; return 0;}
				break;
			case '<':
				if (depth==1 || inexp || i==0 || hasonecarat) {
					if (depth==1) cout << "ERR: left-pointing tape head in braces at position " << i << ".\n";
					if (inexp) cout << "ERR: left-pointing tape head in exponent at position " << i << ".\n";
					if (i==0) cout << "ERR: left-pointing tape head at left end of tape.\n";
					if (hasonecarat) cout << "ERR: left-pointing tape head at position " << i << " after tape head.\n"; 
					return 0;
				}
				hasonecarat=1;
				break;
			case '>':
				if (depth==1 || inexp || i==len-3 || hasonecarat) {
					if (depth==1) cout << "ERR: right-pointing tape head in braces at position " << i << ".\n";
					if (inexp) cout << "ERR: right-pointing tape head in exponent at position " << i << ".\n";
					if (i==0) cout << "ERR: right-pointing tape head at right end of tape.\n";					
					if (hasonecarat) cout << "ERR: right-pointing tape head at position " << i << " after tape head.\n"; 
					return 0;
				}
				hasonecarat=1;
				break;
			case '{':
				if (depth==1) {cout << "ERR: left brace within braces at position " << i << ".\n"; return 0;}
				depth++;
				if (!inexp) numsymbs++;
				break;
			case '}':
				if (depth==0) {cout << "ERR: right brace outside of braces at position " << i << ".\n"; return 0;}
				if (!inexp && (i==len-3 || st[i+1] != '^')) {cout << "ERR: base with no exponent ending at position" << i << ".\n"; return 0;}
				depth--;
				if (inexp) inexp=0;
				onenplusperexp=0;
				break;
			case '^':
				if (inexp) {cout << "ERR: Exponentiation within exponent at position " << i << ".\n"; return 0;}
				if (depth==1) {cout << "ERR: Exponentiation within base at position " << i << ".\n"; return 0;}
				if (i==0) {cout << "ERR: Exponentiation at beginning of tape."; return 0;}
				if (i==len-3) {cout << "ERR: Exponentiation at end of tape."; return 0;}
				if (st[i+1]!='{') {cout << "ERR: Exponentiation will need a brace at position " << i << ".\n"; return 0;}
				inexp=1;
				break;
			case '0':
				if (inexp && i>=1 && st[i-1]=='{') {cout << "ERR: Cannot begin exponent with a leading zero at position " << i << ".\n"; return 0;}
				if (!inexp && depth==0) numsymbs++;
				break;
			case '1':
				if (!inexp && depth==0) numsymbs++;
				break;
			default:
				if (!inexp) {cout << "ERR: " << st[i] << " is an unrecognizable base character at position " << i << ".\n"; return 0;}
				if (st[i]!='2' && st[i]!='3' && st[i]!='4' && st[i]!='5' && st[i]!='6' && st[i]!='7' && st[i]!='8' && st[i]!='9') {
					cout << "ERR: " << st[i] << " is an unrecognizable exponent character at position " << i << ".\n";
					return 0;
				}
				break;
		}
	}
	if (depth==1) {cout << "ERR: Cannot end tape in braces.\n"; return 0;}
	if (inexp) {cout << "ERR: Cannot end tape in exponent.\n"; return 0;}
	if (!hasonecarat) {cout << "ERR: Tape missing head.\n"; return 0;}
	if (st[len-2]!=' ') {cout << "ERR: Single whitespace needed between tape and state.\n"; return 0;}
	cout << "VALID CONFIGURATION\n";
	switch (st[len-1]) {
		case 'A':
			c.s=0;
			break;
		case 'B':
			c.s=1;
			break;
		case 'C':
			c.s=2;
			break;
		case 'D':
			c.s=3;
			break;
		case 'E':
			c.s=4;
			break;
		case 'H':
			c.s=127;
			break;
		default:
			cout << "ERR: Cannot read state.\n";
			return 0;
	}
	c.a=new abstrsymbol[numsymbs];
	c.len=numsymbs;
	c.lhsindex=0;
	c.rhsindex=c.len-1;
	int i=0, ci=0;
	while (i<len-2) readin(st,len,i,c,ci);
	return 1;
};

void abstrsymbolout (abstrsymbol &cs) {
	if (cs.baselen>1 && !(cs.exp.a==0 && cs.exp.b==1)) {
		cout << "{";
		boolout(cs.base,cs.baselen);
		cout << "}^{"; 
		if (cs.exp.a==0) cout << cs.exp.b;
		else if (cs.exp.a==1) {
			if (cs.exp.b==0) cout << "N";
			else cout << "N+" << cs.exp.b;
		}
		else if (cs.exp.b==0) cout << cs.exp.a << "N";
		else cout << cs.exp.a << "N+" << cs.exp.b;
		cout << "}";
	}
	else if (cs.baselen==1 && !(cs.exp.a==0 && cs.exp.b==1)) {
		boolout(cs.base,cs.baselen);
		cout << "^{"; 
		if (cs.exp.a==0) cout << cs.exp.b;
		else if (cs.exp.a==1) {
			if (cs.exp.b==0) cout << "N";
			else cout << "N+" << cs.exp.b;
		}
		else if (cs.exp.b==0) cout << cs.exp.a << "N";
		else cout << cs.exp.a << "N+" << cs.exp.b;
		cout << "}";
	}
	else boolout(cs.base,cs.baselen);
	return;
}

void abstrconfigout (abstrconfig &c) {
	int j=c.lhsindex;
	for (int i=0; i<c.len; i++) {
		if (c.pos==i && c.side==0) cout << ">";
		abstrsymbolout (c.a[j]);
		if (i<c.len-1) j=c.a[j].next->arrayaddress;
		if (c.pos==i && c.side==1) cout << "<";
	}
	cout << " ";
	if (c.s==0) cout << "A";
	else if (c.s==1) cout << "B";
	else if (c.s==2) cout << "C";
	else if (c.s==3) cout << "D";
	else if (c.s==4) cout << "E";
	else if (c.s==127) cout << "H";
	return;
};

bool yields (machine m, abstrconfig &c, abstrconfig &d, expr num) { //WARNING: not finished yet
//Does config c yield config d in num steps according to the rules of m?
//Will try a simple induction: let N=0; see if c yields d.
// Then assume it's true when N=K and see if it's true when N=K+1.
//For instance, according to HNR#9, >1^{2N+1} C yields 1{10}^{N}> A in 2N+1 steps.
// So, we'll check whether >1^{3} C yields 1{10}^{1}> A in 3 steps, and then
// assuming >1^{2K+1} C yields 1{10}^{K}> A in 2K+1 steps, we'll prove that
// >1^{2K+3} C yields 1{10}^{K+1}> A in 2K+3 steps.
// The assumption is used wherever felicitous; hence, at the beginning,
// we change to 1{10}^{K}>11 A in 2K+1 steps, and then get
// 1{10}^{K+1}> A in two more.
	abstrconfig c0, d0;
	//Copy s, pos, side, len;
	c0.s=c.s; d0.s=d.s;
	c0.pos=c.pos; d0.pos=d.pos;
	c0.side=c.side; d0.side=d.side;
	c0.len=c.len; d0.len=d.len;
	return 0;
}

bool eqlexactsymb (abstrsymbol& s, abstrsymbol& t) {
	if (s.baselen == t.baselen && s.exp.a == t.exp.a && s.exp.b == t.exp.b) {
		for (int i=0; i<s.baselen; i++)
			if (s.base[i]!=t.base[i])
				return 0;
		return 1;
	}
	return 0;
}

bool eqlexact (abstrconfig &c, abstrconfig &d) {
//Must check abstrsymbol* a; bool side; int s, pos, len;
//The particular indices for the memory are not considered to matter
	if (c.side == d.side && c.s == d.s && c.pos == d.pos && c.len == d.len) {
		int i=c.lhsindex, j=d.lhsindex;
		while (c.a[i].next!=NULL&&d.a[j].next!=NULL) {
			if (!eqlexactsymb(c.a[i],d.a[j])) {
				return 0;
			}
			i=c.a[i].next->arrayaddress;
			j=d.a[j].next->arrayaddress;
		}
		if (c.a[i].next!=NULL||d.a[j].next!=NULL) //If either abstrconfig has more to it
			return 0;
	}
	else return 0;
	return 1;
}

bool spill (abstrconfig &c, int where, bool reorganize) {
	if (where>=c.len || where < 0) {
		cout << "ERR: Symbol # " << where << " is past the end of the configuration.\n";
		return 0;
	}
	else {
		if (reorganize) {
		}
		else {
			
		}
	}
	return 1;
}

bool runonassumptions (machine m, abstrconfig &c, assumptionlist a, int numbigsteps) {
//Runs m on c for numbigsteps big steps, or until m hits a side of c, assuming a
	//bool runwithoutput (machine &m, long long l, int size, int i, config &c, int mod) {
		//char* letter=new char[1];
/*	int i=c.a[pos].arrayaddress;
		for (int k=0; k<numbigsteps; k++) {
			//clock_t start_time, cur_time;
			//start_time = clock();
			//while((clock() - start_time) < .03 * CLOCKS_PER_SEC)
			//{
			//}
			if (i==-1) {
				cout << "GOT TO THE LEFT END!\n";
				return 0;
			}
			if(i==size) {
				cout << "GOT TO THE RIGHT END!\n";
				return 0;
			}
			if (c.s==127) {
				cout << "HALT!\n";
				return 1;
			}
			if (c.t.a[i]==0) {
				c.t.a[i]=m.s[c.s].z.p;
				if (m.s[c.s].z.d==0) i--;
				else i++;
				c.s=m.s[c.s].z.t;
				c.pos=i;
			}
			else {
				c.t.a[i]=m.s[c.s].o.p;
				if (m.s[c.s].o.d==0) i--;
				else i++;
				c.s=m.s[c.s].o.t;
				c.pos=i;
			}
			if ((k+1)%mod==0) {
				configout(c);
				cout << k+1 << "\n";
			}
		}
		return 0;
	};*/
	return 0;
}