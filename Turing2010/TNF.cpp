#include <iostream>
#include "structs.cpp"
#include "oldrun.h"
#include "discounts.h"
#include "run.h"
#include "config.h"
#include "TNF.h"
using namespace std;

void makemachines (machine* Ma, int numtries) {
	const bool R=1, L=0;
	const char A=0, B=1, C=2, D=3;// E=4, H=127;	
	halfstate* data=new halfstate[68*2];
	bool* oo=new bool[68*2]; char* qq=new char[68*2];
	for (int i=0; i<68*2; i++) {
		int j=i/2;
		if (i%2==0) {
			oo[i]=0;
			qq[i]=B;
			if(j<8) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=C;
			}
			else if(j<18) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=C;
			}
			else if(j<28) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;				
			}
			else if(j<43) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;				
			}
			else if(j<48) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=A;				
			}
			else if(j<56) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;				
			}
			else if(j<61) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=B;				
			}
			else {
				data[i].p=1;
				data[i].d=L;
				data[i].t=B;				
			}			
		}
		else { //i%2!=0, encoding third halfstate
			if (j<18) {
				oo[i]=0;
				qq[i]=C;
			}
			else if (j<43) {
				oo[i]=1;
				qq[i]=C;
			}
			else if (j<61) {
				oo[i]=1;
				qq[i]=A;
			}
			else {
				oo[i]=1;
				qq[i]=B;
			}
			if (j==0) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==1) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=B;
			}
			else if (j==2) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==3) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==4) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==5) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==6) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==7) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==8) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==9) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=B;
			}
			else if (j==10) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==11) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==12) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=B;
			}
			else if (j==13) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==14) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==15) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==16) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==17) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==18) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==19) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=B;
			}
			else if (j==20) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==21) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==22) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==23) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==24) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==25) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==26) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==27) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==28) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=B;
			}
			else if (j==29) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==30) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==31) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=B;
			}
			else if (j==32) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==33) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==34) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=B;
			}
			else if (j==35) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==36) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=D;
			}
			else if (j==37) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=B;
			}
			else if (j==38) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==39) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=D;
			}
			else if (j==40) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=A;
			}
			else if (j==41) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=A;
			}
			else if (j==42) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==43) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==44) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==45) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==46) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==47) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==48) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=B;
			}
			else if (j==49) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==50) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=B;
			}
			else if (j==51) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==52) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=B;
			}
			else if (j==53) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==54) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=B;
			}
			else if (j==55) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==56) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==57) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==58) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==59) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==60) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==61) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=A;
			}
			else if (j==62) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=A;
			}
			else if (j==63) {
				data[i].p=1;
				data[i].d=L;
				data[i].t=A;
			}
			else if (j==64) {
				data[i].p=0;
				data[i].d=R;
				data[i].t=C;
			}
			else if (j==65) {
				data[i].p=0;
				data[i].d=L;
				data[i].t=C;
			}
			else if (j==66) {
				data[i].p=1;
				data[i].d=R;
				data[i].t=C;
			}
			else {
				data[i].p=1;
				data[i].d=L;
				data[i].t=C;
			}
		}
	}
	for (int h=0; h<numtries; h++) {
		int i=rand()%68;
		int l=rand()%7, g=0;
		for (int j=0; j<5; j++) {
			for (int k=0; k<2; k++) {
				if (j==0 && k==0) {
					Ma[h].s[j].z.p=1;
					Ma[h].s[j].z.d=1;
					Ma[h].s[j].z.t=1;
				}
				else if (j==qq[i*2] && k==oo[i*2]) { //fill in second halfstate
					if (k==0) {
						Ma[h].s[j].z.p=data[i*2].p;
						Ma[h].s[j].z.d=data[i*2].d;
						Ma[h].s[j].z.t=data[i*2].t;
					}
					else {
						Ma[h].s[j].o.p=data[i*2].p;
						Ma[h].s[j].o.d=data[i*2].d;
						Ma[h].s[j].o.t=data[i*2].t;
					}
				}
				else if (j==qq[i*2+1] && k==oo[i*2+1]) { //fill in third halfstate
					if (k==0) {
						Ma[h].s[j].z.p=data[i*2+1].p;
						Ma[h].s[j].z.d=data[i*2+1].d;
						Ma[h].s[j].z.t=data[i*2+1].t;
					}
					else {
						Ma[h].s[j].o.p=data[i*2+1].p;
						Ma[h].s[j].o.d=data[i*2+1].d;
						Ma[h].s[j].o.t=data[i*2+1].t;
					}
				}
				else {
					if (k==0) {
						if (g==l) {
							Ma[h].s[j].z.p=rand()%2;
							Ma[h].s[j].z.d=rand()%2;
							Ma[h].s[j].z.t=127;
							g++;
						}
						else {
							Ma[h].s[j].z.p=rand()%2;
							Ma[h].s[j].z.d=rand()%2;
							Ma[h].s[j].z.t=rand()%5;
							g++;
						}
					}
					else {
						if (g==l) {
							Ma[h].s[j].o.p=rand()%2;
							Ma[h].s[j].o.d=rand()%2;
							Ma[h].s[j].o.t=127;
							g++;
						}
						else {
							Ma[h].s[j].o.p=rand()%2;
							Ma[h].s[j].o.d=rand()%2;
							Ma[h].s[j].o.t=rand()%5;
							g++;
						}
					}
				}
			}
		}
	}
}
