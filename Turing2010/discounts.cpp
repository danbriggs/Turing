#include "structs.cpp"
#include "oldrun.h"
#include "discounts.h"

bool loopcheck (machine m) {
	char h, i;
	//identify halting state:
	for (i=0; i<5; i++)
		if (m.s[i].z.t==127 || m.s[i].o.t==127) h=i;
	//Loops if no other state transitions to halting state:
	for (i=0; i<5; i++)
		if (i!=h)
			if (m.s[i].z.t==h ||m.s[i].o.t==h) return 0;
	return 1;
};

bool loopcheck2 (machine m) {
	/*char* incl=new char[5];
	 int h,i;
	 for (i=0; i<5)
	 if (m.s[i].z.t==127) {incl[i]=127; h=i;}
	 bool p=1;
	 while (p==1) {
	 p=0;
	 for (i=0; i<5; i++)
	 }
	 for (i=0; i<5) {
	 if (m.s[i].z.t==h%5) incl[i]=1;
	 if (m.s[i].o.t==h%5) incl[i+5]=1;
	 }*/
	char h, i;
	//identify halting state:
	for (i=0; i<5; i++) {
		if (m.s[i].z.t==127 || m.s[i].o.t==127) h=i;
	}
	//Loops if no state transitions to set of states transitioning to halting state:
	char* friends=new char[5];
	for (i=0; i<5; i++) {
		if (i==h) friends[i]=127;
		else if (m.s[i].z.t==h ||m.s[i].o.t==h) friends[i]=1;
		else friends[i]=0;
	}
	if (friends[0]==1) return 0;
	else for (i=1; i<5; i++)
		if (friends[i]==1) {
			if (m.s[0].z.t==i||m.s[0].o.t==i) return 0;
			for (int j=1; j<5; j++)
				if (friends[j]==0 && (m.s[j].z.t==i||m.s[j].o.t==i)) return 0;
		}
	return 1;
};

bool loopcheck3 (machine m) {
	char* incl=new char[5];
	int h,i;
	bool u;
	for (i=0; i<5; i++) {
		if (m.s[i].z.t==127) {incl[i]=127; h=i; u=0;}
		else if (m.s[i].o.t==127) {incl[i]=127; h=i; u=1;}
	}
	bool p=1;
	while (p==1) {
		p=0;
		for (i=0; i<5; i++) {
			if (incl[i]==0) {
				if (incl[m.s[i].z.t]==1 || incl[m.s[i].o.t]==1 || incl[m.s[i].z.t]==127 || incl[m.s[i].o.t]==127) {
					incl[i]=1;
					p=1;
					if (i==0) return 0;
				}
			}
			else if (incl[i]==127) {
				if (incl[m.s[i].z.t]==1 || incl[m.s[i].o.t]==1) {
					incl[i]=1;
					p=1;
					if (i==0) return 0;
				}
			}
		}
	}
	return 1;
	// for (i=0; i<5) {
	// if (m.s[i].z.t==h%5) incl[i]=1;
	// if (m.s[i].o.t==h%5) incl[i+5]=1;
}

int four_s_m (machine m) {
	for (int i=2; i<=4; i++) {
		bool p=0;
		for (int j=0; j<=4; j++)
			if (m.s[j].z.t==i || m.s[j].o.t==i)
				p=1;
		if (p==0) return 1;
	}
	return 0;
}
