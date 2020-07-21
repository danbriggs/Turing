#include <iostream>
#include <fstream>
#include "structs.cpp"
#include "oldrun.h"
#include "discounts.h"
#include "run.h"
#include "config.h"
#include "TNF.h"
#include "skelet.h"
using namespace std;

void skeletfill (machine& M, int which) {
	skeletfill (M, which, 1);
}

void skeletfill (machine& M, int which, bool loud) {
	if (loud && which <=43) cout << "Now loading HNR #" << which << ".\n";
	const bool R=1, L=0;
	const char A=0, B=1, C=2, D=3, E=4, H=127;
	/*halfstate A0, A1;
	halfstate B0, B1;
	halfstate C0, C1;
	halfstate D0, D1;
	halfstate E0, E1;*/
	halfstate zs[5];
	halfstate os[5];
	
	string line;
	ifstream infile ("HNRs.txt");
	if (infile.is_open())
	{
		int find=0;
		while (! infile.eof() )
		{
			getline (infile,line);
			if (line[58]==(int)'-') {
				//cout << line << endl;
				find++;
				if (find==which) {
					for (int counter=0; counter<5; counter++) {
						char p=line[9*counter+1];
						if (p=='0') zs[counter].p=0;
						else if (p=='1') zs[counter].p=1;
						//cout << "counter=" << counter << " and line[9*counter+1]=" << line[9*counter+1] << " ";
						char d=line[9*counter+2];
						//cout << "line[9*counter+2]=" << line[9*counter+2] << " ";						
						if (d=='L') zs[counter].d=L;
						else if (d=='R') zs[counter].d=R;
						char s=line[9*counter];
						//cout << "line[9*counter]=" << line[9*counter] << "\n";						
						if (s==(int)'A') zs[counter].t=A;
						else if (s==(int)'B') zs[counter].t=B;
						else if (s==(int)'C') zs[counter].t=C;
						else if (s==(int)'D') zs[counter].t=D;
						else if (s==(int)'E') zs[counter].t=E;
						else if (s==(int)'H') zs[counter].t=H;
						
						p=line[9*counter+5];
						if (p=='0') os[counter].p=0;
						else if (p=='1') os[counter].p=1;
						//cout << "line[9*counter+5]=" << line[9*counter+5] << " ";
						d=line[9*counter+6];
						//cout << "line[9*counter+6]=" << line[9*counter+6] << " ";
						if (d=='L') os[counter].d=L;
						else if (d=='R') os[counter].d=R;
						s=line[9*counter+4];
						//cout << "line[9*counter+4]=" << line[9*counter+4] << "\n\n";
						if (s==(int)'A') os[counter].t=A;
						else if (s==(int)'B') os[counter].t=B;
						else if (s==(int)'C') os[counter].t=C;
						else if (s==(int)'D') os[counter].t=D;
						else if (s==(int)'E') os[counter].t=E;
						else if (s==(int)'H') os[counter].t=H;
					}
				}
			}
		}
		infile.close();
	}
	
	if (which==43) {
		zs[0].p=1; zs[1].p=0; zs[2].p=1; zs[3].p=1; zs[4].p=0;
		zs[0].d=L; zs[1].d=L; zs[2].d=L; zs[3].d=R; zs[4].d=R;
		zs[0].t=C; zs[1].t=E; zs[2].t=D; zs[3].t=A; zs[4].t=A;

		os[0].p=0; os[1].p=1; os[2].p=0; os[3].p=0; os[4].p=1;
		os[0].d=R; os[1].d=L; os[2].d=L; os[3].d=L; os[4].d=R;
		os[0].t=E; os[1].t=H; os[2].t=B; os[3].t=A; os[4].t=E;
	}
	
	if (which==127) {
		zs[0].p=1; zs[1].p=1; zs[2].p=1; zs[3].p=1; zs[4].p=1;
		zs[0].d=R; zs[1].d=R; zs[2].d=R; zs[3].d=L; zs[4].d=R;
		zs[0].t=B; zs[1].t=C; zs[2].t=D; zs[3].t=A; zs[4].t=H;
		
		os[0].p=1; os[1].p=1; os[2].p=0; os[3].p=1; os[4].p=0;
		os[0].d=L; os[1].d=R; os[2].d=L; os[3].d=L; os[4].d=L;
		os[0].t=C; os[1].t=B; os[2].t=E; os[3].t=D; os[4].t=A;
	}
	
	for (int index=0; index<5; index++) {
		if (loud) cout << zs[index].p << " "<< zs[index].d << " " << (int)zs[index].t%121 << " | " << os[index].p << " " << os[index].d << " " << (int)os[index].t%121 << "\n";
		M.s[index].z=zs[index];
		M.s[index].o=os[index];
	}
}