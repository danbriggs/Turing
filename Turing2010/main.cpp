//Turing machine emulator for Busy Beaver problem
//Daniel Briggs, 2010

#include <iostream>
#include <fstream>
#include <ctime>
#include <string>
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

int main (int argc, char * const argv[]) {
	machine M;
	M.n=5;
	clock_t start = clock();
	int s=0, s3=0, s4=0; //s2=0;
	time_t seconds;
	time(&seconds);
	srand((unsigned int) seconds);
	const int numtries=10000;
	machine* Ma = new machine[numtries];
	makemachines(Ma,numtries);
	std::cout << "s4 (no transition to halt) WAS, " << s4 << '\n';
	std::cout << "s3 (4-state machine, of rest) WAS, " << s3 << '\n';
	std::cout << "s (halts, of rest) WAS, " << s << '\n';
	std::cout << 10000-s4-s3-s;
	//std::cout << "s2 (obvious loops) WAS, " << s2 << '\n';
	//std::cout << "s3 (less obvious) WAS, " << s3 << '\n';
	std::cout << "TIME WAS, " << ( ( std::clock() - start ) / (double)CLOCKS_PER_SEC ) <<'\n';
	s4=0; s3=0; s=0;
	int sbam=0, s5=0;
	const int savetime=60;
	for (int i=0; i<numtries; i++) {
		int q2=loopcheck3(Ma[i]);s4+=q2;
		int q3;
		if (!q2) {q3=four_s_m(Ma[i]);s3+=q3;}
		int q3half=0, q4=0;
		if (!q2 && !q3) {q3half=run(Ma[i],100); s+=q3half;}
		if (!q2 && !q3 && !q3half) {q4=runsmart(Ma[i],110); sbam+=q4;}
		if (!q2 && !q3 && !q3half && !q4) s5+=runsave(Ma[i],savetime);
	}
	
	std::cout << "UTILIZING TREE.\n";
	std::cout << "s4 (no transition to halt) WAS, " << s4 << '\n';
	std::cout << "s3 (4-state machine, of rest) WAS, " << s3 << '\n';
	std::cout << "s (halts, of rest) WAS, " << s << '\n';
	std::cout << "sbam (fails the S(4) test) WAS, " << s << '\n';
	std::cout << "s5 (repeat configs in first " << savetime << ") WAS, " << s5 << '\n';
	std::cout << numtries-s4-s3-s-sbam-s5 << " machines left out of " << numtries << ".\n";
	//std::cout << "s2 (obvious loops) WAS, " << s2 << '\n';
	//std::cout << "s3 (less obvious) WAS, " << s3 << '\n';
	std::cout << "TIME WAS, " << ( ( std::clock() - start ) / (double)CLOCKS_PER_SEC ) <<'\n';
	
	int whichone, runlen /*sometimes long long*/, tapelen, spot, mod;
	string line;
	ifstream infile ("instr.txt");
	if (infile.is_open()) {
		if (!infile.eof()) getline(infile,line);
		whichone = atoi(line.c_str());
		if (!infile.eof()) getline(infile,line);
		runlen = atoi(line.c_str());
		if (!infile.eof()) getline(infile,line);
		tapelen = atoi(line.c_str());
		if (!infile.eof()) getline(infile,line);
		spot = atoi(line.c_str());
		if (!infile.eof()) getline(infile,line);
		mod = atoi(line.c_str());
	}
	skeletfill(M,whichone);
	start = clock();
	
	runwithoutput(M,/*(long long)2000000000*(long long)2000000000*/runlen,tapelen,spot,mod);
	
	/*for (int whichone=1; whichone<=42; whichone++) {
		skeletfill(M,whichone,0);
		runwithoutput(M,1005,170,85,1000);
	}*/
	
	
	//runwithhackneyedoutput(M,100000000,8000,7500,1);
	//runwithawesomeoutput (M, 5, 180, 90, 1);	
	//runwithawesomeoutput (M,100,50,25,1);
	std::cout << "TIME WAS, " << (clock()-start)/(double)CLOCKS_PER_SEC <<'\n';	
		
	/*int whichone=1;
	skeletfill(M,whichone);*/
	config c;
	c.len=1;
	c.t.a=new bool[1];
	c.t.a[0]=0;
	int B=1;
	c.s=B;
	c.pos=0;
	configout(c);
	cout << "\n";
	
	start = clock();
	
	/*int room=1000;
	config* cc=new config[room];
	int howmany=allcomefrom(cc, M, c, 10, room);
	for (int index=0; index<howmany; index++)
		configout(cc[index]);
	cout << "There were " << howmany << " of them\n";
	
	 for (int k=0; k<howmany; k++) {
	 for (int kk=0; kk<k; kk++) {
	 if (eql(cc[k],cc[kk])) {
	 reduce(cc,k,howmany);
	 k--;
	 howmany--;
	 break;
	 }
	 }
	 }*/
	
	int howmany=0;
	for (int funindex=1; funindex<=3; funindex++) {
		int room=1000;
		config* cc=new config[room];
		int howmanynow=multicomefrom(cc, M, c, funindex, room);
		for (int index=0; index<howmanynow; index++) {
			configout(cc[index]);
			cout << "\n";
		}
		cout << "There were " << howmanynow << " of them at level " << funindex<< ".\n\n";
		howmany+=howmanynow;
	}
	
	cout << "Which comes down to " << howmany << " of them\n";
	
	std::cout << "TIME WAS, " << (clock()-start)/(double)CLOCKS_PER_SEC <<'\n';
	
	
	compsymbol ss[5];
	ss[0].baselen=1;
	ss[1].baselen=1;
	ss[2].baselen=2;
	ss[3].baselen=3;
	ss[4].baselen=4;
	for (int icky=0; icky<5; icky++) ss[icky].base=new bool[ss[icky].baselen];
	ss[0].base[0]=0;													ss[0].exp=2;
	ss[1].base[0]=1;													ss[1].exp=9;
	ss[2].base[0]=0; ss[2].base[1]=0;									ss[2].exp=11;
	ss[3].base[0]=0; ss[3].base[1]=1; ss[3].base[2]=1;					ss[3].exp=1;
	ss[4].base[0]=1; ss[4].base[1]=0; ss[4].base[2]=0; ss[4].base[3]=0;	ss[4].exp=1;
	
	compconfig conf;
	
	conf.len=5;
	conf.s=3;
	conf.pos=2;
	conf.side=0;
	conf.lhsindex=0;
	
	conf.a=new compsymbol[5];
    
	for (int i=conf.lhsindex; i<conf.len; i++) {
		copycompsymbol(ss[i],conf.a[i]);
		if (i<4) conf.a[i].next=&conf.a[i+1];
		else conf.a[i].next=NULL;
		if (i>0) conf.a[i].prev=&conf.a[i-1];
		else conf.a[i].prev=NULL;
		conf.a[i].arrayaddress=i;
	}
	
	compconfigout(conf);
	
	cout << "\n";
	
	/*abstrsymbol sss[5];
	sss[0].base='a'; sss[0].exp.a=0;  sss[0].exp.b=1;
	sss[1].base='b'; sss[1].exp.a=1;  sss[1].exp.b=0;
	sss[2].base='c'; sss[2].exp.a=1; sss[2].exp.b=1;
	sss[3].base='d'; sss[3].exp.a=2;  sss[3].exp.b=0;
	sss[4].base='e'; sss[4].exp.a=5;  sss[4].exp.b=3;
	
	abstrconfig aconf;
	
	aconf.len=5;
	aconf.s=3;
	aconf.pos=2;
	aconf.side=0;
	
	aconf.a=new abstrsymbol[5];
    
	for (int i=0; i<5; i++) {
		copyabstrsymbol(sss[i],aconf.a[i]);
		if (i<4) aconf.a[i].next=&aconf.a[i+1];
		if (i>0) aconf.a[i].prev=&aconf.a[i-1];
		aconf.a[i].arrayaddress=i;
	}
	
	abstrconfigout(aconf);
	
	cout << "\n\n";*/
	
	cout << "Type 0 for concrete, 1 for abstract, 2 for compression, 3 for run.\n";
	int pq;
	cin>>pq;
	if (pq==0) {
		char* somestring=new char[100];
		char* stateforit=new char[1];
		cin >> somestring;
		cin >> stateforit;
		strcat(somestring, " ");
		strcat(somestring, stateforit);
		cout << strlen(somestring) << " is its length\n";
		compconfig someconfig;
		if (compconfigin(somestring,strlen(somestring),someconfig)) {
			cout << "It came in!\n";
			cout << "It has " << someconfig.len << " symbols. The tape head attached on the " << someconfig.side << " side of symbol number " << someconfig.pos << ".";
			cout << "It is in state " << someconfig.s << ". Its lhsindex is of course " << someconfig.lhsindex << "\n";
			compconfigout(someconfig);
		}
		else return 0;
		
		cout << "\n\n";
		
		char* somestring2=new char[100];
		char* stateforit2=new char[1];
		cin >> somestring2;
		cin >> stateforit2;
		strcat(somestring2, " ");
		strcat(somestring2, stateforit2);
		cout << strlen(somestring2) << " is its length\n";
		compconfig someconfig2;
		if (compconfigin(somestring2,strlen(somestring2),someconfig2)) {
			cout << "It came in!\n";
			cout << "It has " << someconfig2.len << " symbols. The tape head attached on the " << someconfig2.side << " side of symbol number " << someconfig2.pos << ".";
			cout << "It is in state " << someconfig2.s << ". Its lhsindex is of course " << someconfig2.lhsindex << "\n";
			compconfigout(someconfig2);
			
			if (eql(someconfig,someconfig2)) cout << "WHOA.\n";
			else cout << "OH WELL...\n";	
		}
		else return 0;
	}
	else if (pq==1) {
		char* somestring=new char[100];
		char* stateforit=new char[1];
		cin >> somestring;
		cin >> stateforit;
		strcat(somestring, " ");
		strcat(somestring, stateforit);
		cout << strlen(somestring) << " is its length\n";
		abstrconfig someconfig;
		if (abstrconfigin(somestring,strlen(somestring),someconfig)) {
			cout << "It came in!\n";
			cout << "It has " << someconfig.len << " symbols. The tape head attached on the " << someconfig.side << " side of symbol number " << someconfig.pos << ".";
			cout << "It is in state " << someconfig.s << ". Its lhsindex is of course " << someconfig.lhsindex << "\n";
			abstrconfigout(someconfig);
		}
		else return 0;
		
		cout << "\n\n";
		
		char* somestring2=new char[100];
		char* stateforit2=new char[1];
		cin >> somestring2;
		cin >> stateforit2;
		strcat(somestring2, " ");
		strcat(somestring2, stateforit2);
		cout << strlen(somestring2) << " is its length\n";
		abstrconfig someconfig2;
		if (abstrconfigin(somestring2,strlen(somestring2),someconfig2)) {
			cout << "It came in!\n";
			cout << "It has " << someconfig2.len << " symbols. The tape head attached on the " << someconfig2.side << " side of symbol number " << someconfig2.pos << ".";
			cout << "It is in state " << someconfig2.s << ". Its lhsindex is of course " << someconfig2.lhsindex << "\n";
			abstrconfigout(someconfig2);
			cout << "\n";
		}
		else return 0;
		
		if (eqlexact(someconfig,someconfig2)) cout << "They are exactly equal.\n";
		else cout << "They are not exactly equal.";
	}
	else if (pq==2) {
		char* somestring=new char[1000];
		char* stateforit=new char[1];
		cin >> somestring;
		cin >> stateforit;
		strcat(somestring, " ");
		strcat(somestring, stateforit);
		config someconfig;
		if (configin(somestring,strlen(somestring),someconfig)) {
			cout << "It came in!\n";
			cout << "It has " << someconfig.len << " symbols. The tape head is at symbol number " << someconfig.pos << ". ";
			cout << "It is in state " << someconfig.s << ".\n";
			compconfig someconfig2;
			compress(someconfig,someconfig2);
			cout << "Configuration compressed!\n";
			compconfigout(someconfig2);
		}
		else return 0;
	}
	else if (pq==3) {
		char* somestring=new char[1000];
		char* stateforit=new char[1];
		cin >> somestring;
		cin >> stateforit;
		strcat(somestring, " ");
		strcat(somestring, stateforit);
		config someconfig;
		if (configin(somestring,strlen(somestring),someconfig)) {
			runwithoutput(M,1000000,someconfig.len,someconfig.pos,someconfig);
		}
		else return 0;
	}
	return 0;
}


