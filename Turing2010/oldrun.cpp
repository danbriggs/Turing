#include <iostream>
#include "structs.cpp"
#include "oldrun.h"
#include "run.h"
#include <ctime>
using namespace std;

bool run (machine m, int l) {
	const int size=2*l+4;
	tape t;
	t.a=new bool[size]; //independent of l
	char s=0;
	int i=size/2;
	for (int k=0; k<l; k++) {
		if (s==127) {
			//std::cout << "HALT!\n";
			//int sigma=0;
			//for (int j=0; j<size; j++) {
			//	if (t.a[j]==1) sigma++;
			//	std::cout << t.a[j];
			//}
			//std::cout << "HALT!\n";
			//std::cout << "CONTAINING " << sigma << " ONES.\n";
			return 1;
		}
		if (t.a[i]==0) {
			t.a[i]=m.s[s].z.p;
			if (m.s[s].z.d==0) i--;
			else i++;
			s=m.s[s].z.t;
		}
		else {
			t.a[i]=m.s[s].o.p;
			if (m.s[s].o.d==0) i--;
			else i++;
			s=m.s[s].o.t;
		}
	}
	// std::cout << "Got to here...\n";
	// for (int j=0; j<size; j++) std::cout << t.a[j];
	return 0;
};

bool runsmart (machine m, int l) {
	bool* beenthere=new bool[5];
	const int size=2*l+4;
	tape t;
	t.a=new bool[size]; //independent of l
	char s=0;
	int i=size/2;
	for (int k=0; k<l; k++) {
		if (s==127) {
			return 1;
		}
		beenthere[s]=1;
		if (t.a[i]==0) {
			t.a[i]=m.s[s].z.p;
			if (m.s[s].z.d==0) i--;
			else i++;
			s=m.s[s].z.t;
		}
		else {
			t.a[i]=m.s[s].o.p;
			if (m.s[s].o.d==0) i--;
			else i++;
			s=m.s[s].o.t;			
		}
	}
	for (int k=0; k<5; k++) {
		if (beenthere[k]==0) return 1;
	}
	return 0;
};

bool runsave (machine m, int l) {
	char* ss=new char[l];
	const int size=2*l+4;
	tape t;
	t.a=new bool[size];
	char s=0;
	int i=size/2;
	tape* tt=new tape[l];
	for (int k=0; k<l; k++) tt[k].a=new bool[size];
	for (int k=0; k<l; k++) {
		//cout << "G";
		if (s==127) {
			return 1;
		}
		ss[k]=s;
		for (int kk=0; kk<size; kk++) tt[k].a[kk]=t.a[kk];
		for (int kk=0; kk<k; kk++) {
			bool matchok=1;
			//if (kk%10==0) cout << "s now is " << (int)s << ", while ss[kk] is " << (int)ss[kk] << ".\n";
			if (ss[kk]!=s) {
				matchok=0;
			}
			if (matchok) {
				//cout << "HI";
				for (int jj=0; jj<size; jj++) {
					if (tt[kk].a[jj] != t.a[jj]) {
						matchok=0;
						break;
					}
				}
			}
			if (matchok) return 1;
		}
		if (t.a[i]==0) {
			t.a[i]=m.s[s].z.p;
			if (m.s[s].z.d==0) i--;
			else i++;
			s=m.s[s].z.t;
		}
		else {
			t.a[i]=m.s[s].o.p;
			if (m.s[s].o.d==0) i--;
			else i++;
			s=m.s[s].o.t;			
		}
	}
	return 0;
};

bool runwithoutput (machine &m, long long l, int size, int i, config &c, int mod) {
	//char* letter=new char[1];
	for (long long k=0; k<l; k++) {
		clock_t start_time, cur_time;
		start_time = clock();
		//while((clock() - start_time) < .03 * CLOCKS_PER_SEC)
		{
		}
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
};

bool runwithoutput (machine &m, long long l, int size, int i, config &c) {
	return runwithoutput (m,l,size,i,c,1);
}

bool runwithoutput (machine &m, long long l, int size, int i, int mod) {
	config c;
	c.t.a=new bool[size];
	c.len=size;
	c.s=0;
	for (int index=0; index<size; index++) {
		c.t.a[index]=0;
	}
	return (runwithoutput (m,l,size,i,c,mod));
}

bool runwithoutput (machine &m, long long l, int size, int i) {
	return (runwithoutput (m,l,size,i,1));
}	
/*The following used to be part of main
	for (int i=0; i<10000; i++) {
		//std::cout << i << " ";
		//std::cout.flush();
		A0.p=1; A0.d=R; A0.t=B;
		//if (i==7) std::cout << "\np=" << 1 << "; d=" << R << "; t=" << (int)B << ";\n";
		bool p=rand()%2, d=rand()%2;
		char t=rand()%5;
		A1.p=p; A1.d=d; A1.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2;
		bool q1=rand()%2;
		if (q1==0) {d=L; t=rand()%2;}
		else {d=rand()%2; t=C;}
		B0.p=p; B0.d=d; B0.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2, d=rand()%2; t=rand()%5;
		B1.p=p; B1.d=d; B1.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2, d=rand()%2; t=rand()%5;
		C0.p=p; C0.d=d; C0.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2, d=rand()%2; t=rand()%5;
		C1.p=p; C1.d=d; C1.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2, d=rand()%2; t=rand()%5;
		D0.p=p; D0.d=d; D0.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2, d=rand()%2; t=rand()%5;
		D1.p=p; D1.d=d; D1.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2, d=rand()%2; t=rand()%5;
		E0.p=p; E0.d=d; E0.t=t;
		//if (i==7) std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n";
		p=rand()%2, d=rand()%2; t=rand()%5;
		E1.p=p; E1.d=d; E1.t=t;
		//if (i==7) {std::cout << "p=" << p << "; d=" << d << "; t=" << (int)t << ";\n"; std::cout.flush();}
		t=rand()%9;
		//if (i==7) std::cout << "That randomish t: " << (int)t << "\n";
		if (t==0) A1.t=H;
		else if (t==1) B0.t=H;
		else if (t==2) B1.t=H;
		else if (t==3) C0.t=H;
		else if (t==4) C1.t=H;
		else if (t==5) D0.t=H;
		else if (t==6) D1.t=H;
		else if (t==7) E0.t=H;
		else if (t==8) E1.t=H;
		M.s[A].z=A0; M.s[A].o=A1;
		M.s[B].z=B0; M.s[B].o=B1;
		M.s[C].z=C0; M.s[C].o=C1;
		M.s[D].z=D0; M.s[D].o=D1;
		M.s[E].z=E0; M.s[E].o=E1;
		int q2=loopcheck3(M);s4+=q2;
		int q3;
		if (!q2) {q3=four_s_m(M);s3+=q3;}
		if (!q2 && !q3) s+=run(M,10);
		//s+=q;
		//int q2, q3;
		//if (!q) {s4+=loopcheck3(M);}
		//if (!q) {q2=loopcheck(M); s2+=q2;}
		//if (!q && !q2) {q3=loopcheck2(M); s3+=q3;}
		//if ((!q) && (!q2) && (!q3)) s4+=loopcheck3(M);
	}*/