#include <iostream>
#include <fstream>
#include "structs.cpp"
#include "oldrun.h"
#include "discounts.h"
#include "run.h"
#include "config.h"
#include "TNF.h"
#include "skelet.h"
#include "runcomp.h"
using namespace std;

void copycompsymbol (compsymbol &a, compsymbol &b) { //That is, copy a into b
	b.baselen=a.baselen;
	b.base=new bool[b.baselen];
	for (int i=0; i<b.baselen; i++) b.base[i]=a.base[i];
	b.exp=a.exp;
	return;
};

void appendcompsymbol (compconfig& c, compsymbol& s) {
//WARNING: Will only work when more space than c.len has been reserved for c
	copycompsymbol(s,c.a[c.len]);
	if (c.len>=1) {
		c.a[c.rhsindex].next=&c.a[c.len];
		c.a[c.len].prev=&c.a[c.rhsindex];
	}
	else c.lhsindex=0;
	c.a[c.len].arrayaddress=c.len;
	c.rhsindex=c.len;
	c.len++;
	return;
}

void copycompconfig (compconfig &c, compconfig &d) {
	copycompconfig(c,d,1);
}

void copycompconfig (compconfig &c, compconfig &d, int expansionrate) {
//That is, copy c into d, multiplying c's length by expansionrate
//to reserve space, regardless of space reserved for c
	d.s=c.s;
	d.pos=c.pos;
	d.side=c.side;
	d.len=c.len;
	d.a=new compsymbol[d.len*expansionrate];
	//d.freeroom=d.len*(expansionrate-1);
	int cj=c.lhsindex;
	for (int i=0; i<d.len; i++) {
		d.a[i].arrayaddress=i; //might as well clean it up while we're at it
		copycompsymbol(c.a[cj],d.a[i]);
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

void copycompconfig (compconfig &c, compconfig &d, int expansionrate, int &pointertokeeptrackof) {
//That is, copy c into d, modifying pointertokeeptrackof
	d.s=c.s;
	d.pos=c.pos;
	d.side=c.side;
	d.len=c.len;
	d.a=new compsymbol[d.len*expansionrate];
	//d.freeroom=d.len*(expansionrate-1);
	int cj=c.lhsindex;
	for (int i=0; i<d.len; i++) {
		d.a[i].arrayaddress=i; //might as well clean it up while we're at it
		copycompsymbol(c.a[cj],d.a[i]);
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
}

void boolout (bool* b, int len) {
	for (int i=0; i<len; i++) cout << b[i];
	return;
};

void boolout (bool* b, int len, int dir) {
	for (int i=len-1;i>=0;i--) cout << b[i];
	return;
};

int chartodigit(char ch) {
	switch(ch) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
	}
	return 0;
}

int readnumber (char* st, int &i) {
//leaves it at the closed brace
	int ans=chartodigit(st[i]);
	i++;
	while (st[i]!='}') {
		ans*=10;
		ans+=chartodigit(st[i]);
		i++;
	}
	return ans;
}

void readin (char* st, int len, int& i, compconfig& c, int& ci) {
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
			c.a[ci].exp=readnumber(st,i);
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
				c.a[ci].exp=1;
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
				c.a[ci].exp=readnumber(st,i);
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

bool compconfigin (char* st, int len, compconfig& c) {
//1 if a valid configuration, 0 otherwise
	int depth=0;
	bool inexp=0;
	int numsymbs=0;
	bool hasonecarat=0;
	for (int i=0; i<len-2; i++) {
		cout << "NOW VERIFYING CHARACTER NUMBER " << i << ".\n";
		switch (st[i]) {
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
	c.a=new compsymbol[numsymbs];
	c.len=numsymbs;
	c.lhsindex=0;
	c.rhsindex=c.len-1;
	//c.freeroom=0;
	int i=0, ci=0;
	while (i<len-2) readin(st,len,i,c,ci);
	return 1;
};

void compsymbolout (compsymbol &cs) {
	if (cs.baselen>1 && cs.exp!=1) {
		cout << "{";
		boolout(cs.base,cs.baselen);
		cout << "}^{" << cs.exp << "}";
	}
	else if (cs.baselen==1 && cs.exp!=1) {
		boolout(cs.base,cs.baselen);
		cout << "^{" << cs.exp << "}";
	}
	else boolout(cs.base,cs.baselen);
	return;
}

void compconfigout (compconfig &c, int dir) {
	if (dir!=-1) {
		int j=c.lhsindex;
		for (int i=0; i<c.len; i++) {
			if (c.pos==i && c.side==0) cout << ">";
			if (c.a[j].baselen>1 && c.a[j].exp!=1) {
				cout << "{";
				boolout(c.a[j].base,c.a[j].baselen);
				cout << "}^{" << c.a[j].exp << "}";
			}
			else if (c.a[j].baselen==1 && c.a[j].exp!=1) {
				boolout(c.a[j].base,c.a[j].baselen);
				cout << "^{" << c.a[j].exp << "}";
			}
			else boolout(c.a[j].base,c.a[j].baselen);
			if (c.pos==c.a[j].arrayaddress && c.side==1) cout << "<";
			if (i<c.len-1) j=c.a[j].next->arrayaddress;
		}
		cout << " ";
		if (c.s==0) cout << "A";
		else if (c.s==1) cout << "B";
		else if (c.s==2) cout << "C";
		else if (c.s==3) cout << "D";
		else if (c.s==4) cout << "E";
		else if (c.s==127) cout << "H";
		return;
	}
	else{ //dir==-1
		int j=c.rhsindex;
		for (int i=0; i<c.len; i++) {
			if (c.pos==i && c.side==1) cout << ">";
			if (c.a[j].baselen>1 && c.a[j].exp!=1) {
				cout << "{";
				boolout(c.a[j].base,c.a[j].baselen,-1);
				cout << "}^{" << c.a[j].exp << "}";
			}
			else if (c.a[j].baselen==1 && c.a[j].exp!=1) {
				boolout(c.a[j].base,c.a[j].baselen,-1);
				cout << "^{" << c.a[j].exp << "}";
			}
			else boolout(c.a[j].base,c.a[j].baselen,-1);
			if (c.pos==c.a[j].arrayaddress && c.side==0) cout << "<";
			if (i<c.len-1) j=c.a[j].prev->arrayaddress;
		}
		cout << " ";
		if (c.s==0) cout << "A";
		else if (c.s==1) cout << "B";
		else if (c.s==2) cout << "C";
		else if (c.s==3) cout << "D";
		else if (c.s==4) cout << "E";
		else if (c.s==127) cout << "H";
		return;		
	}
}
void compconfigout (compconfig &c) {
	compconfigout(c,1);
};

bool exponentbreaker(compconfig& c, int& cj, bool& cq, int& csum, compconfig& d, int& dj, bool& dq, int& dsum) {
//for when c.a[cj].exp>d.a[dj].exp
	if (d.a[dj].next==NULL) return 0;
	if (!dq) dsum+=d.a[dj].baselen*d.a[dj].exp;
	
	//compsymbol tempy;
	copycompsymbol(c.a[cj],c.a[c.len]);
	c.a[c.len].exp-=d.a[dj].exp;
	compsymbolout(c.a[c.len]);
	c.a[cj].exp=d.a[dj].exp;
	if (c.a[cj].next != NULL) {
		c.a[cj].next->prev=&c.a[c.len];
		c.a[c.len].next=c.a[cj].next;
	}
	else c.a[c.len].next=NULL;
	c.a[cj].next=&c.a[c.len];
	c.a[c.len].prev=&c.a[cj];
	c.a[c.len].arrayaddress=c.len;
	//c.a[c.len]=tempy;
	c.len++; //all in the name of preservation
	if (!cq) csum+=c.a[cj].baselen*c.a[cj].exp;
	if (c.pos==cj && c.side==1) {cout << "WHY ARE WE HERE?"; c.pos=c.a[cj].next->arrayaddress;} //tempy's, of course
	cj=c.a[cj].next->arrayaddress; //tempy's, of course
	dj=d.a[dj].next->arrayaddress;
	cout << "GOT PAST HERE!\n";
	cout << "One is ";
	compconfigout(c);
	cout << "and the other is ";
	compconfigout(d);
	cout << "\n.";
	return 1;
}

bool basebreaker(compconfig& c, int& cj, bool& cq, int& csum, compconfig& d, int& dj, bool& dq, int& dsum) {
//for when c.a[cj].baselen>d.a[dj].baselen
	cout << "We're dealing with ";
	compsymbolout(c.a[cj]);
	cout << ", whose base is longer than ";
	compsymbolout(d.a[dj]);
	cout << "'s.\n";
	//Cases: d.a[dj].exp is or is not 1,
	//       c.a[cj].exp is or is not 1.
	//At the end of the procedure, we should
	//check for 0 exponents and bypass those nodes,
	//redirecting pos as necessary.
	//First, if any of d's don't match up, it's unsalvageable.
	for (int i=0; i<d.a[dj].baselen; i++) if (d.a[dj].base[i] != c.a[cj].base[i]) return 0;
	//tempd will simply hold the rest of the iterations of d.a[dj].base,
	//while d.a[dj] will contain only the first.
	//tempc1 contains the rest of the first iteration of c.a[cj].base,
	//whereas c.a[cj] will contain only the beginning,
	//and tempc2 the rest of the iterations.
	if (d.a[dj].exp==1 && d.a[dj].next==NULL) return 0;
	//compsymbol tempd, tempc1, tempc2;
	copycompsymbol(d.a[dj],d.a[d.len]);
	d.a[d.len].exp--;
	d.a[dj].exp=1;
	if (d.a[dj].next != NULL) {
		d.a[dj].next->prev=&d.a[d.len];
		d.a[d.len].next=d.a[dj].next;
	}
	else d.a[d.len].next=NULL;
	d.a[dj].next=&d.a[d.len];
	d.a[d.len].prev=&d.a[dj];
	d.a[d.len].arrayaddress=d.len;
	//d.a[d.len]=tempd;
	d.len++; //all in the name of preservation
	//now c.a[cj] will contain the first part of the first iteration,
	//tempc1 will contain the second part,
	//and tempc2 will contain the rest of the iterations
	copycompsymbol(c.a[cj],c.a[c.len+1]);
	c.a[c.len+1].exp--;
	c.a[c.len].baselen=c.a[cj].baselen-d.a[dj].baselen;
	c.a[c.len].base=new bool[c.a[c.len].baselen];
	for (int i=0; i<c.a[c.len].baselen; i++) c.a[c.len].base[i]=c.a[cj].base[i+d.a[dj].baselen];
	c.a[c.len].exp=1;
	copycompsymbol(d.a[dj],c.a[cj]);
	
	cout << "c.a[cj] is now ";
	compsymbolout(c.a[cj]);
	cout << ", c.a[c.len] is ";
	compsymbolout(c.a[c.len]);
	cout << ", and c.a[c.len+1] is ";
	compsymbolout(c.a[c.len+1]);
	cout << ".\n";
	
	c.a[c.len+1].next=c.a[cj].next;
	if (c.a[cj].next!=NULL) c.a[cj].next->prev=&c.a[c.len+1];
	c.a[c.len].next=&c.a[c.len+1];
	c.a[c.len+1].prev=&c.a[c.len];
	c.a[cj].next=&c.a[c.len];
	c.a[c.len].prev=&c.a[cj];
	
	c.a[c.len].arrayaddress=c.len;
	c.a[c.len]=c.a[c.len];
	c.a[c.len+1].arrayaddress=c.len+1;
	c.a[c.len+1]=c.a[c.len+1];
	c.len+=2;
	
	if (!dq) dsum+=d.a[dj].baselen; //exp=1
	if (d.pos==dj && d.side==1) d.pos=d.a[dj].next->arrayaddress; //tempd's, of course
	dj=d.a[dj].next->arrayaddress; //tempd's, of course
	if (!cq) csum+=c.a[cj].baselen; //exp=1
	if (c.pos==cj && c.side==1) c.pos=c.a[cj].next->next->arrayaddress; //tempc2's
	cj=c.a[cj].next->arrayaddress; //tempc1's
	cout << "And at this point, c.a[cj] is ";
	compsymbolout(c.a[cj]);
	cout << ".\n";
	
	if (d.a[d.len-1].exp==0) {//d.len has been ++'d
		dj=d.a[dj].next->arrayaddress;
		if (d.pos==d.a[d.len-1].arrayaddress) //then side must be 1
			d.pos=d.a[d.len-1].prev->arrayaddress; //the old dj's
		if (d.a[d.len-1].next!=NULL)
			d.a[d.len-1].next->prev=d.a[d.len-1].prev;
		d.a[d.len-1].prev->next=d.a[d.len-1].next;
		d.len--; //all without a mention of the old dj
	}
	
	if (c.a[c.len-1].exp==0) {//c.len has been +=2'd
		cout << "Now removing ";
		compsymbolout(c.a[c.len-1]);
		cout << " from ";
		compconfigout(c);
		cout << "...\n";
		if (c.pos==c.a[c.len-1].arrayaddress) //then side must be 1
			c.pos=c.a[c.len-1].prev->arrayaddress; //tempc1's address
		if (c.a[c.len-1].next!=NULL)
			c.a[c.len-1].next->prev=&c.a[c.len-2];
		c.a[c.len-1].prev->next=c.a[c.len-1].next;
		c.len--;
		cout << "Done. Config c is now ";
		compconfigout(c);
		cout << ".\n";
	}
	cout << "After splitting across the base for c, we get ";
	compconfigout(c);
	cout << ", focused at ";
	compsymbolout(c.a[cj]);
	cout << ", and ";
	compconfigout(d);
	cout << ", focused at ";
	compsymbolout(d.a[dj]);
	cout << ".\n";
	return 1;
}


bool eql (compconfig &c0, compconfig &d0) {
	if (c0.s != d0.s) return 0;
	compconfig c, d;
	copycompconfig(c0,c,3);
	copycompconfig(d0,d,3);
	int cj=c.lhsindex, dj=d.lhsindex, csum=0, dsum=0, ck=c.len*3, dk=d.len*3;
	
	bool p=1, cq=0, dq=0;
	while (p) {
		cout << "Outset: ";
		compsymbolout(c.a[cj]);
		cout << " vs. ";
		compsymbolout(d.a[dj]);
		cout << "\n";
		if (!cq && cj==c.pos) {
			if (c.side) csum+=c.a[cj].baselen*c.a[cj].exp-1;
			cout << "Not cq, and cj is c.pos. csum=" << csum << "\n";
			if (dq && csum!=dsum) return 0;
			cq=1;
		}
		if (!dq && dj==d.pos) {
			if (d.side) dsum+=d.a[cj].baselen*d.a[dj].exp-1;
			cout << "Not dq, and dj is d.pos. dsum=" << dsum << "\n";
			if (cq && csum!=dsum) return 0;
			dq=1;
		}
		if (c.a[cj].baselen==d.a[dj].baselen) {
			for (int i=0; i<c.a[cj].baselen; i++) if (c.a[cj].base[i] != d.a[dj].base[i]) return 0;
			if (c.a[cj].exp==d.a[dj].exp) {
				cout << "A match: ";
				compsymbolout(c.a[cj]);
				cout << " on each account.\n";
				if (c.a[cj].next==NULL) {
					if (d.a[dj].next==NULL) return 1;
					else return 0;
				}
				else if (d.a[dj].next==NULL) return 0;
				else {
					if (!cq) csum+=c.a[cj].baselen*c.a[cj].exp;
					if (!dq) dsum+=d.a[dj].baselen*d.a[dj].exp;
					cj=c.a[cj].next->arrayaddress;
					dj=d.a[dj].next->arrayaddress;
				}
			}
			else if (c.a[cj].exp<d.a[dj].exp) {
				if (!exponentbreaker(d,dj,dq,dsum,c,cj,cq,csum)) return 0;
			}
			else {//c.a[cj].exp>d.a[dj].exp
				if (!exponentbreaker(c,cj,cq,csum,d,dj,dq,dsum)) return 0;
			}
		}
		else if (c.a[cj].baselen<d.a[dj].baselen){
			if (!basebreaker(d,dj,dq,dsum,c,cj,cq,csum)) return 0;
		}
		else {//d.a[dj].baselen<c.a[cj].baselen
			if (!basebreaker(c,cj,cq,csum,d,dj,dq,dsum)) return 0;
		}
		if (c.len > ck-2) {
			cout << "big thing happening to c, after which it becomes ";
			compconfig cprime;
			copycompconfig(c,cprime,1,cj);
			copycompconfig(cprime,c,3);//already in order
			ck=c.len*3;
			compconfigout(c);
			cout << " (check).\n";
		}
		if (d.len > dk-2) {
			cout << "big thing happening to d, after which it becomes ";
			compconfig dprime;
			copycompconfig(d,dprime,1,dj);
			copycompconfig(dprime,d,3);//already in order
			dk=d.len*3;
			compconfigout(d);
			cout << " (check).\n";
		}		
	}
	return 1;
};