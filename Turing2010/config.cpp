#include <iostream>
#include "structs.cpp"
#include "oldrun.h"
#include "discounts.h"
#include "run.h"
#include "config.h"
using namespace std;

void copyconfig (config& c, config& d) {
//copies c to d
	d.s=c.s;
	d.pos=c.pos;
	d.len=c.len;
	for (int i=0; i<c.len; i++) d.t.a[i]=c.t.a[i];
	return;
}

int replace (config* cc, int k, config c0, int pos, bool p, int s) {	
	cc[k].len=c0.len;
	cc[k].pos=pos;
	cc[k].s=s;
	for (int i=0; i<c0.len; i++) {
		if (i!=pos) cc[k].t.a[i]=c0.t.a[i];
		else cc[k].t.a[i]=p;
	}
	return 0;
}

void fronttrim (config* cc, int i, config* cc0, int j) {
	if (cc0[j].pos==0) cc[i]=cc0[j];
	else if (cc0[j].pos==2) {
		cc[i].s=cc0[j].s;
		cc[i].pos=1;
		cc[i].len=cc0[j].len-1;
		cc[i].t.a=new bool[cc0[j].len-1];
		for (int k=0; k<cc0[j].len-1; k++)
			cc[i].t.a[k]=cc0[j].t.a[k+1];
	}
	else cout << "HUH?\n";
	return;
}

void endtrim (config* cc, int i, config* cc0, int j) {
	//cout << "cc0[j].pos=" << cc0[j].pos << " & cc0[j].len=" << cc0[j].len << "\n";
	if (cc0[j].pos==cc0[j].len-1) cc[i]=cc0[j];
	else if (cc0[j].pos==cc0[j].len-3) {
		cc[i].s=cc0[j].s;
		cc[i].pos=cc0[j].len-3;
		cc[i].len=cc0[j].len-1;
		cc[i].t.a=new bool[cc0[j].len-1];
		for (int k=0; k<cc0[j].len-1; k++)
			cc[i].t.a[k]=cc0[j].t.a[k];
	}
	else cout << "WHAT?\n";
	return;
}

int comefrom (config* cc, machine &m, config c) {
	//Parameters: pass along new array cc, needing length possibly up to 20
	//cc=new config[20];
	int k=0;
	if (c.t.a[c.pos-1]==0) {
		for (int i=0; i<5; i++) {
			if (m.s[i].z.t==c.s && m.s[i].z.d==1 && m.s[i].z.p==0) {
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos-1,0,i);
				k++;
			}
			if (m.s[i].o.t==c.s && m.s[i].o.d==1 && m.s[i].o.p==0) {
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos-1,1,i);
				k++;
			}
		}
	}
	else if (c.t.a[c.pos-1]==1) {
		for (int i=0; i<5; i++) {
			if (m.s[i].z.t==c.s && m.s[i].z.d==1 && m.s[i].z.p==1) {
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos-1,0,i);
				k++;
			}
			if (m.s[i].o.t==c.s && m.s[i].o.d==1 && m.s[i].o.p==1) {
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos-1,1,i);
				k++;
			}
		}
	}
	if (c.t.a[c.pos+1]==0) {
		for (int i=0; i<5; i++) {
			/*if (i==4) {
				cout << "OMG IT'S SO HAPPENING\n";
				cout << "m.s[i].z.t=" << m.s[i].z.t << "\n";
				cout << "c.s=" << c.s << "\n";
				cout << "m.s[i].z.d=" << m.s[i].z.d << "\n";
				cout << "m.s[i].z.p=" << m.s[i].z.p << "\n";
			}*/
			//cout << "Here I am\n";
			//configout(c);
			if (m.s[i].z.t==c.s && m.s[i].z.d==0 && m.s[i].z.p==0) {
				//cout << "WELL I GUESS IT HAPPENED\n";
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos+1,0,i);
				k++;
			}
			if (m.s[i].o.t==c.s && m.s[i].o.d==0 && m.s[i].o.p==0) {
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos+1,1,i);
				k++;
			}
		}
	}
	else if (c.t.a[c.pos+1]==1) {
		for (int i=0; i<5; i++) {
			if (m.s[i].z.t==c.s && m.s[i].z.d==0 && m.s[i].z.p==1) {
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos+1,0,i);
				k++;
			}
			if (m.s[i].o.t==c.s && m.s[i].o.d==0 && m.s[i].o.p==1) {
				cc[k].t.a=new bool[c.len];
				replace(cc,k,c,c.pos+1,1,i);
				k++;
			}
		}
	}
	return k;
}

int smartcomefrom (config* cc, machine m, config c) {
	//cc should have length 80 if c has length 1
	if (c.pos==c.len-1 && c.len-1==0) {
		config c1, c2, c3, c4;
		c1.len=c2.len=c3.len=c4.len=3;
		c1.t.a=new bool[3];
		c2.t.a=new bool[3];
		c3.t.a=new bool[3];
		c4.t.a=new bool[3];
		c1.t.a[0]=c1.t.a[2]=c2.t.a[0]=c3.t.a[2]=0;
		c2.t.a[2]=c3.t.a[0]=c4.t.a[0]=c4.t.a[2]=1;
		c1.t.a[1]=c2.t.a[1]=c3.t.a[1]=c4.t.a[1]=c.t.a[0];
		c1.s=c2.s=c3.s=c4.s=c.s;
		c1.pos=c2.pos=c3.pos=c4.pos=1;

		/*cout << "++++++++++\n";
		configout(c1);
		configout(c2);
		configout(c3);
		configout(c4);
		cout << "----------\n";*/
		config* cc1=new config[20];
		int howmany1=comefrom(cc1, m, c1);
		//for (int indexhere=0; indexhere<howmany1; indexhere++) configout(cc1[indexhere]);
		//cout << "cc1 done\n";
		config* cc2=new config[20];
		int howmany2=comefrom(cc2, m, c2);
		//for (int indexhere=0; indexhere<howmany2; indexhere++) configout(cc2[indexhere]);
		//cout << "cc2 done\n";
		config* cc3=new config[20];
		int howmany3=comefrom(cc3, m, c3);
		//for (int indexhere=0; indexhere<howmany3; indexhere++) configout(cc3[indexhere]);
		//cout << "cc3 done\n";
		config* cc4=new config[20];
		int howmany4=comefrom(cc4, m, c4);
		//for (int indexhere=0; indexhere<howmany4; indexhere++) configout(cc4[indexhere]);
		//cout << "cc4 done\n";
		//cout << "BTW howmany1+howmany2+howmany3+howmany4=" << howmany1+howmany2+howmany3+howmany4 << "\n";
		for (int i=0; i<howmany1+howmany2+howmany3+howmany4; i++) {
			if (i<howmany1) {fronttrim(cc,i,cc1,i); /*cout << "There's "; configout(cc[i]); cout << "\n";*/}
			else if (i<howmany1+howmany2) { /*cout << "YIPPIDEE"; configout(cc[i]); cout << "DOO-DAH,\n";*/ fronttrim(cc,i,cc2,i-howmany1); /*cout << "YABBA"; configout(cc[i]); cout << "DABBADOO.\n";*/ /*cout << "There's"; configout(cc[i]); cout << "\n";*/}
			else if (i<howmany1+howmany2+howmany3) fronttrim(cc,i,cc3,i-howmany1-howmany2);
			else fronttrim(cc,i,cc4,i-howmany1-howmany2-howmany3);
			//cout << "We're doing ";
			//configout(cc[i]);
			//cout<< "\n";
			endtrim(cc,i,cc,i); //cout << "&that's "; configout(cc[i]); cout << "\n";
		}
		/*for (int indexmustworknow=0; indexmustworknow<howmany1+howmany2+howmany3+howmany4;indexmustworknow++) {
			cout << indexmustworknow << ": ";
			configout(cc[indexmustworknow]);
		}*/
		return howmany1+howmany2+howmany3+howmany4;
	}
	else if (c.pos==0) {
		//cout << "Here\n";
		config c1, c2;
		c1.len=c2.len=c.len+1;
		c1.t.a=new bool[c.len+1];
		c2.t.a=new bool[c.len+1];
		c1.t.a[0]=0;
		c2.t.a[0]=1;
		for (int i=0; i<c.len; i++) {
			c1.t.a[i+1]=c2.t.a[i+1]=c.t.a[i];
		}
		c1.s=c2.s=c.s;
		c1.pos=c2.pos=1;
		/*cout << "One of them is ";
		configout(c1);
		cout << ", And the other is ";
		configout(c2);
		cout << ";";*/
		config* cc1=new config[20];
		int howmany1=comefrom(cc1, m, c1);
		config* cc2=new config[20];
		int howmany2=comefrom(cc2, m, c2);
		for (int i=0; i<howmany1+howmany2; i++) {
			if (i<howmany1) fronttrim(cc,i,cc1,i);
			else fronttrim(cc,i,cc2,i-howmany1);
		}
		return howmany1+howmany2;
	}
	else if (c.pos==c.len-1) {
		config c1, c2;
		c1.len=c2.len=c.len+1;
		c1.t.a=new bool[c.len+1];
		c2.t.a=new bool[c.len+1];
		c1.t.a[c1.len-1]=0;
		c2.t.a[c2.len-1]=1;
		for (int i=0; i<c.len; i++) {
			c1.t.a[i]=c2.t.a[i]=c.t.a[i];
		}
		c1.s=c2.s=c.s;
		c1.pos=c2.pos=c.pos;
		config* cc1=new config[20];
		int howmany1=comefrom(cc1, m, c1);
		config* cc2=new config[20];
		int howmany2=comefrom(cc2, m, c2);
		for (int i=0; i<howmany1+howmany2; i++) {
			if (i<howmany1) endtrim(cc,i,cc1,i);
			else endtrim(cc,i,cc2,i-howmany1);
		}
		return howmany1+howmany2;		
	}
	else return comefrom(cc,m,c);
}

void reduce (config* cc, int k, int len) {
	for (int i=k; i<len-1; i++) {
		cc[i]=cc[i+1];
	}
	return;
}

bool eql (config c1, config c2) {
	if (c1.s==c2.s && c1.pos==c2.pos && c1.len==c2.len) {
		for (int i=0; i<c1.len; i++) {
			if (c1.t.a[i] != c2.t.a[i]) return 0;
		}
		return 1;
	}
	return 0;
}

int boom (config* cc, machine m, config* cc0, int len) {
	//parameters: cc to put new ones in
	//cc0 containing the original nexters
	int i=0;
	for (int k=0; k<len; k++) {
		config* dd=new config[80];
		int n=smartcomefrom(dd,m,cc0[k]);
		for (int j=0; j<n; j++) {
			cc[i]=dd[j];
			i++;
		}
	}
	for (int k=0; k<i; k++) {
		for (int kk=0; kk<k; kk++) {
			if (eql(cc[k],cc[kk])) {
				reduce(cc,k,i);
				k--;
				i--;
				break;
			}
		}
	}
	/*cout << "And for the boom, goes: \n";
	for (int indexmustworknow=0; indexmustworknow<i; indexmustworknow++) {
		cout << indexmustworknow << ": ";
		configout(cc[indexmustworknow]);
	}
	cout << "\n";*/
	return i;
}

int multicomefrom (config* cc, machine m, config c, int l, int room) {
	config* cc2=new config[room*80];
	int howmany=1;
	cc[0]=c;
	//cout << "WATCH ME NOW: cc[0] is ";
	//configout(cc[0]);
	for (int i=0; i<l; i++) {
		howmany=boom(cc2,m,cc,howmany);
		for (int j=0; j<howmany; j++) {
			//cout << "I'MA DO THIS: j=" << j << " and cc2[j] is: ";
			//configout(cc2[j]);
			cc[j]=cc2[j];
			//cout << "NOW IT'S DONE: j=" << j << " and cc[j] is: ";
			//configout(cc[j]);
		}
	}
	return howmany;
}

int allcomefrom (config* ff, machine m, config c, int l, int room) {
	config* cc=new config[room];
	config* cc2=new config[room];
	int howmany=1;
	cc[0]=c;
	//cout << "WATCH ME NOW: cc[0] is ";
	//configout(cc[0]);
	int gotpastit=0;
	ff[0]=c;
	gotpastit++;
	for (int i=0; i<l; i++) {
		howmany=boom(cc2,m,cc,howmany);
		for (int j=0; j<howmany; j++) {
			//cout << "I'MA DO THIS: j=" << j << " and cc2[j] is: ";
			//configout(cc2[j]);
			cc[j]=cc2[j];
			//cout << "NOW IT'S DONE: j=" << j << " and cc[j] is: ";
			//configout(cc[j]);
			ff[gotpastit]=cc[j];
			gotpastit++;
		}
	}
	return gotpastit;
}
