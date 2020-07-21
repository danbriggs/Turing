#include <iostream>
#include "structs.cpp"
#include "oldrun.h"
#include "discounts.h"
#include "run.h"
#include "runcomp.h"
#include "runcomp2.h"
using namespace std;

bool mastatapey[74];
bool mastatapey2[74]; //000111110001^{11}001^{15}001^{9}0001^{9}0001^{9}
bool eighteenth[19]; //01101001111{10}^{4}

bool configin (char* st, int len, config& c) {
	c.len=len-2;
	c.t.a=new bool[c.len];
	for (int i=0; i<len-2; i++) {
		if (st[i]=='0') c.t.a[i]=0;
		else if (st[i]=='1') c.t.a[i]=1;
		else if (st[i]=='o') {
			c.t.a[i]=0;
			c.pos=i;
		}
		else if (st[i]=='i') {
			c.t.a[i]=1;
			c.pos=i;
		}
		else {
			cout << "ERR: Cannot read tape at position " << i << ".\n";
			return 0;
		}
	}
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
	return 1;
}

void configout (config c) {
	for (int i=0; i<c.len; i++) {
		if (i==c.pos) {
			if (c.t.a[i]==0) cout << "o";
			else cout << "i";
		}
		else {
			if (c.t.a[i]==0) cout << "0";
			else cout << "1";
		}
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

void configout2 (config &c) {
	for (int i=0; i<c.len; i++) {
		if (i==c.pos) {
			if (c.t.a[i]==0) cout << "o";
			else cout << "i";
		}
		else if (c.t.a[i]==0 || c.pos==i+1) {
			if (c.t.a[i]==0) cout << "0";
			else cout << "1";
		}
		else {
			if (c.t.a[i+1]==1) cout << "I";
			else cout << "T";
			i++;
		}
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

void configout3 (config &c) {
	int i=0;
	while (c.t.a[i]==0 && i<c.len) i++;
	int j=c.pos-5;
	while (c.t.a[j-2]==1 && c.t.a[j-1]==0 && j>=0) j-=2;
	for (i=i; i<j; i++) {
		if (i==c.pos) {
			if (c.t.a[i]==0) cout << "o";
			else cout << "i";
		}
		else if (c.t.a[i]==0 || c.pos==i+1) {
			if (c.t.a[i]==0) cout << "0";
			else cout << "1";
		}
		else {
			if (c.t.a[i+1]==1) cout << "I";
			else cout << "T";
			i++;
		}
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

void funkyconfigout (config c) {
	bool* farouttapey=new bool[12];
	bool* gnarlytapey=new bool[12];
	bool* funkytapey=new bool[12];
	bool* radtapey=new bool[8];
	farouttapey[0]=1; gnarlytapey[0]=1; funkytapey[0]=1; radtapey[0]=1;
	farouttapey[1]=1; gnarlytapey[1]=1; funkytapey[1]=1; radtapey[1]=1;
	farouttapey[2]=1; gnarlytapey[2]=1; funkytapey[2]=1; radtapey[2]=1;
	farouttapey[3]=1; gnarlytapey[3]=1; funkytapey[3]=1; radtapey[3]=1;
	farouttapey[4]=1; gnarlytapey[4]=1; funkytapey[4]=1; radtapey[4]=1;
	farouttapey[5]=1; gnarlytapey[5]=1; funkytapey[5]=0; radtapey[5]=0;
	farouttapey[6]=1; gnarlytapey[6]=1; funkytapey[6]=1; radtapey[6]=0;
	farouttapey[7]=1; gnarlytapey[7]=0; funkytapey[7]=1; radtapey[7]=0;
	farouttapey[8]=1; gnarlytapey[8]=1; funkytapey[8]=1;
	farouttapey[9]=0; gnarlytapey[9]=1; funkytapey[9]=0;
	farouttapey[10]=0; gnarlytapey[10]=1; funkytapey[10]=0;
	farouttapey[11]=0; gnarlytapey[11]=0; funkytapey[11]=0;
	for (int i=0; i<c.len; i++) {
		if (i==0) {
			while (c.t.a[i]==0) i++;
		}
		if (i==c.pos) {
			if (c.t.a[i]==0) cout << "o";
			else cout << "i";
		}
		else {
			bool fiveonesazerothreeonesthreezeros=1;
			bool fiveonesthreezeros=1;
			bool sevenonesazerothreeonesazero=1;
			bool nineonesthreezeros=1;
			bool ismastastring=1;
			if (i<=c.pos && c.pos<i+12) fiveonesazerothreeonesthreezeros=0;
			else {
				for (int j=0; j<12; j++) {
					if (c.t.a[i+j] != funkytapey[j]) {
						fiveonesazerothreeonesthreezeros=0;
						break;
					}
				}
			}
			if (fiveonesazerothreeonesthreezeros) {
				cout << "a";
				i+=11;
			}
			else {
				if (i<=c.pos && c.pos<i+8) fiveonesthreezeros=0;
				else {
					for (int j=0; j<8; j++) {
						if (c.t.a[i+j] != radtapey[j]) {
							fiveonesthreezeros=0;
							break;
						}
					}
				}
				if (fiveonesthreezeros) {
					cout << "b";
					i+=7;
				}
				else {
					if (i<=c.pos && c.pos<i+12) sevenonesazerothreeonesazero=0;
					else {
						for (int j=0; j<12; j++) {
							if (c.t.a[i+j] != gnarlytapey[j]) {
								sevenonesazerothreeonesazero=0;
								break;
							}
						}
					}
					if (sevenonesazerothreeonesazero) {
						cout << "c";
						i+=11;
					}
					else {
						if (i<=c.pos && c.pos<i+12) nineonesthreezeros=0;
						else {
							for (int j=0; j<12; j++) {
								if (c.t.a[i+j] != farouttapey[j]) {
									nineonesthreezeros=0;
									break;
								}
							}
						}
						if (nineonesthreezeros) {
							cout << "d";
							i+=11;
						}
						else {
							if (i<=c.pos && c.pos<i+74) ismastastring=0;
							else {
								for (int j=0; j<74; j++) {
									if (c.t.a[i+j] != mastatapey[j]) {
										ismastastring=0;
										break;
									}
								}
							}
							if (ismastastring) {
								cout << "e";
								i+=73;
							}
							else {
								if (c.t.a[i]==1) {
									int ones=1;
									bool p;
									do {
										p=c.t.a[i+ones];
										if (p && (i+ones != c.pos)) {
											ones++;
										}
										else if (i+ones==c.pos) {
											p=0;
										}
									} while (p);
									if (ones>=6) {
										cout << "1^{" << ones << "}";
										i+=ones-1;
									}
									else cout << "1";
								}
								else {
									int zeros=1;
									bool p;
									do {
										p=1-c.t.a[i+zeros];
										if (p && (i+zeros != c.pos) && (i+zeros != c.len-1)) {
											zeros++;
										}
										else if (i+zeros == c.pos || i+zeros == c.len-1) {
											p=0;
										}
									} while (p);
									if (i+zeros==c.len-1) break;
									if (zeros>=6) {
										cout << "0^{" << zeros << "}";
										i+=zeros-1;
									}
									else cout << "0";
								}
							}
						}
					}
				}
			}
		}
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

void forthyconfigout (config c) {
	bool* farouttapey=new bool[12];
	bool* gnarlytapey=new bool[12];
	bool* funkytapey=new bool[12];
	bool* radtapey=new bool[8];
	farouttapey[0]=1; gnarlytapey[0]=1; funkytapey[0]=1; radtapey[0]=1;
	farouttapey[1]=1; gnarlytapey[1]=1; funkytapey[1]=1; radtapey[1]=1;
	farouttapey[2]=1; gnarlytapey[2]=1; funkytapey[2]=1; radtapey[2]=1;
	farouttapey[3]=1; gnarlytapey[3]=1; funkytapey[3]=1; radtapey[3]=1;
	farouttapey[4]=1; gnarlytapey[4]=1; funkytapey[4]=1; radtapey[4]=1;
	farouttapey[5]=1; gnarlytapey[5]=1; funkytapey[5]=0; radtapey[5]=0;
	farouttapey[6]=1; gnarlytapey[6]=1; funkytapey[6]=1; radtapey[6]=0;
	farouttapey[7]=1; gnarlytapey[7]=0; funkytapey[7]=1; radtapey[7]=0;
	farouttapey[8]=1; gnarlytapey[8]=1; funkytapey[8]=1;
	farouttapey[9]=0; gnarlytapey[9]=1; funkytapey[9]=0;
	farouttapey[10]=0; gnarlytapey[10]=1; funkytapey[10]=0;
	farouttapey[11]=0; gnarlytapey[11]=0; funkytapey[11]=0;
	for (int i=0; i<c.len; i++) {
		if (i==0) {
			while (c.t.a[i]==0) i++;
		}
		if (i==c.pos) {
			if (c.t.a[i]==0) cout << "o";
			else cout << "i";
		}
		else {
			bool fiveonesazerothreeonesthreezeros=1;
			bool fiveonesthreezeros=1;
			bool sevenonesazerothreeonesazero=1;
			bool nineonesthreezeros=1;
			bool ismastastring=1;
			if (i<=c.pos && c.pos<i+12) fiveonesazerothreeonesthreezeros=0;
			else {
				for (int j=0; j<12; j++) {
					if (c.t.a[i+j] != funkytapey[j]) {
						fiveonesazerothreeonesthreezeros=0;
						break;
					}
				}
			}
			if (fiveonesazerothreeonesthreezeros) {
				cout << "a";
				i+=11;
			}
			else {
				if (i<=c.pos && c.pos<i+8) fiveonesthreezeros=0;
				else {
					for (int j=0; j<8; j++) {
						if (c.t.a[i+j] != radtapey[j]) {
							fiveonesthreezeros=0;
							break;
						}
					}
				}
				if (fiveonesthreezeros) {
					cout << "b";
					i+=7;
				}
				else {
					if (i<=c.pos && c.pos<i+12) sevenonesazerothreeonesazero=0;
					else {
						for (int j=0; j<12; j++) {
							if (c.t.a[i+j] != gnarlytapey[j]) {
								sevenonesazerothreeonesazero=0;
								break;
							}
						}
					}
					if (sevenonesazerothreeonesazero) {
						cout << "c";
						i+=11;
					}
					else {
						if (i<=c.pos && c.pos<i+12) nineonesthreezeros=0;
						else {
							for (int j=0; j<12; j++) {
								if (c.t.a[i+j] != farouttapey[j]) {
									nineonesthreezeros=0;
									break;
								}
							}
						}
						if (nineonesthreezeros) {
							cout << "d";
							i+=11;
						}
						else {
							if (i<=c.pos && c.pos<i+74) ismastastring=0;
							else {
								for (int j=0; j<74; j++) {
									if (c.t.a[i+j] != mastatapey[j]) {
										ismastastring=0;
										break;
									}
								}
							}
							if (ismastastring) {
								cout << "e";
								i+=73;
							}
							else {
								if (c.t.a[i]==1) {
									if (c.t.a[i+1]==1) {
										int ones=1;
										bool p;
										do {
											p=c.t.a[i+ones];
											if (p && (i+ones != c.pos)) {
												ones++;
											}
											else if (i+ones==c.pos) {
												p=0;
											}
										} while (p);
										if (ones>=6) {
											cout << "1^{" << ones << "}";
											i+=ones-1;
										}
										else cout << "1";
									}
									else {
										int onezeros=1;
										bool p, q;
										do {
											p=c.t.a[i+2*onezeros];
											q=1-c.t.a[i+2*onezeros+1];
											if (p && q && (i+2*onezeros != c.pos) && (i+2*onezeros+1 != c.pos)) {
												onezeros++;
											}
											else if ((i+2*onezeros != c.pos) || (i+2*onezeros+1 != c.pos)) {
												p=0; q=0;
											}
										} while (p && q);
										if (onezeros>=4) {
											cout << "{10}^{" << onezeros << "}";
											i+=2*onezeros-1;
										}
										else cout << "1";
									}
								}
								else {
									int zeros=1;
									bool p;
									do {
										p=1-c.t.a[i+zeros];
										if (p && (i+zeros != c.pos) && (i+zeros != c.len-1)) {
											zeros++;
										}
										else if (i+zeros == c.pos || i+zeros == c.len-1) {
											p=0;
										}
									} while (p);
									if (i+zeros==c.len-1) break;
									if (zeros>=6) {
										cout << "0^{" << zeros << "}";
										i+=zeros-1;
									}
									else cout << "0";
								}
							}
						}
					}
				}
			}
		}
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

void firstyconfigout (config &c) {
	//1110111111111000
	bool* farouttapey=new bool[16];
	bool* gnarlytapey=new bool[12];
	bool* funkytapey=new bool[12];
	bool* radtapey=new bool[12];
	farouttapey[0]=1; gnarlytapey[0]=1; funkytapey[0]=1; radtapey[0]=1;
	farouttapey[1]=1; gnarlytapey[1]=1; funkytapey[1]=1; radtapey[1]=1;
	farouttapey[2]=1; gnarlytapey[2]=1; funkytapey[2]=1; radtapey[2]=1;
	farouttapey[3]=0; gnarlytapey[3]=1; funkytapey[3]=1; radtapey[3]=0;
	farouttapey[4]=1; gnarlytapey[4]=1; funkytapey[4]=1; radtapey[4]=1;
	farouttapey[5]=1; gnarlytapey[5]=1; funkytapey[5]=0; radtapey[5]=1;
	farouttapey[6]=1; gnarlytapey[6]=1; funkytapey[6]=1; radtapey[6]=1;
	farouttapey[7]=1; gnarlytapey[7]=0; funkytapey[7]=1; radtapey[7]=1;
	farouttapey[8]=1; gnarlytapey[8]=1; funkytapey[8]=1; radtapey[8]=1;
	farouttapey[9]=1; gnarlytapey[9]=1; funkytapey[9]=0; radtapey[9]=0;
	farouttapey[10]=1; gnarlytapey[10]=1; funkytapey[10]=0; radtapey[10]=0;
	farouttapey[11]=1; gnarlytapey[11]=0; funkytapey[11]=0; radtapey[11]=0;
	farouttapey[12]=1;
	farouttapey[13]=0;
	farouttapey[14]=0;
	farouttapey[15]=0;
	
	for (int i=0; i<c.len; i++) {
		if (i==0) {
			while (c.t.a[i]==0 && i!=c.pos) i++;
		}
		if (i==c.pos) {
			if (c.t.a[i]==0) cout << "o";
			else cout << "i";
		}
		else {
			bool fiveonesazerothreeonesthreezeros=1;
			bool threeonesazerofiveonesthreezeros=1;
			bool sevenonesazerothreeonesazero=1;
			bool threeonesazeronineonesthreezeros=1;
			bool ismastastring=1;
			if (i<=c.pos && c.pos<i+12) fiveonesazerothreeonesthreezeros=0;
			else {
				for (int j=0; j<12; j++) {
					if (c.t.a[i+j] != funkytapey[j]) {
						fiveonesazerothreeonesthreezeros=0;
						break;
					}
				}
			}
			if (fiveonesazerothreeonesthreezeros) {
				cout << "a";
				i+=11;
			}
			else {
				if (i<=c.pos && c.pos<i+16) threeonesazeronineonesthreezeros=0;
				else {
					for (int j=0; j<16; j++) {
						if (c.t.a[i+j] != farouttapey[j]) {
							threeonesazeronineonesthreezeros=0;
							break;
						}
					}
				}
				if (threeonesazeronineonesthreezeros) {
					cout << "d";
					i+=15;
				}
				else {
					if (i<=c.pos && c.pos<i+12) sevenonesazerothreeonesazero=0;
					else {
						for (int j=0; j<12; j++) {
							if (c.t.a[i+j] != gnarlytapey[j]) {
								sevenonesazerothreeonesazero=0;
								break;
							}
						}
					}
					if (sevenonesazerothreeonesazero) {
						cout << "c";
						i+=11;
					}
					else {
						if (i<=c.pos && c.pos<i+12) threeonesazerofiveonesthreezeros=0;
						else {
							for (int j=0; j<12; j++) {
								if (c.t.a[i+j] != radtapey[j]) {
									threeonesazerofiveonesthreezeros=0;
									break;
								}
							}
						}
						if (threeonesazerofiveonesthreezeros) {
							cout << "b";
							i+=11;
						}
						else {
							if (i<=c.pos && c.pos<i+74) ismastastring=0;
							else {
								for (int j=0; j<74; j++) {
									if (c.t.a[i+j] != mastatapey2[j]) {
										ismastastring=0;
										break;
									}
								}
							}
							if (ismastastring) {
								cout << "e";
								i+=73;
							}
							else {
								if (c.t.a[i]==1) {
									if (c.t.a[i+1]==1 && c.t.a[i+2]==1) {
										if (c.t.a[i+3]==1) {
											int ones=1;
											bool p;
											do {
												p=c.t.a[i+ones];
												if (p && (i+ones != c.pos)) {
													ones++;
												}
												else if (i+ones==c.pos) {
													p=0;
												}
											} while (p);
											if (ones>=6) {
												cout << "1^{" << ones << "}";
												i+=ones-1;
											}
											else cout << "1";
										}
										else { //case 1110x
											int oneoneonezerozeros=1;
											bool p, q, r, s, t;
											do {
												p=c.t.a[i+5*oneoneonezerozeros];
												q=c.t.a[i+5*oneoneonezerozeros+1];
												r=c.t.a[i+5*oneoneonezerozeros+2];
												s=1-c.t.a[i+5*oneoneonezerozeros+3];
												t=1-c.t.a[i+5*oneoneonezerozeros+4];
												if (p && q && r && s && t && (i+5*oneoneonezerozeros != c.pos) && (i+5*oneoneonezerozeros+1 != c.pos) && (i+5*oneoneonezerozeros+2 != c.pos) && (i+5*oneoneonezerozeros+3 != c.pos) && (i+5*oneoneonezerozeros+4 != c.pos)) {
													oneoneonezerozeros++;
												}
												else if ((i+5*oneoneonezerozeros == c.pos) || (i+5*oneoneonezerozeros+1 == c.pos) || (i+5*oneoneonezerozeros+2 == c.pos) || (i+5*oneoneonezerozeros+3 == c.pos) || (i+5*oneoneonezerozeros+4 == c.pos)) {
													p=0; q=0; r=0; s=0; t=0; //all bets are off
												}
											} while (p && q && r && s && t);
											if (oneoneonezerozeros>=3 && i+1 != c.pos && i+2 != c.pos && i+3 != c.pos && i+4 != c.pos) {
												cout << "{11100}^{" << oneoneonezerozeros << "}";
												i+=5*oneoneonezerozeros-1;
											}
											else cout << "1";											
										}
									}
									else if (c.t.a[i+1]==1) {//then c.t.a[i+2]=0
										int oneonezeros=1;
										bool p, q, r;
										do {
											p=c.t.a[i+3*oneonezeros];
											q=c.t.a[i+3*oneonezeros+1];
											r=1-c.t.a[i+3*oneonezeros+2];
											if (p && q && r && (i+3*oneonezeros != c.pos) && (i+3*oneonezeros+1 != c.pos) && (i+3*oneonezeros+2 != c.pos)) {
												oneonezeros++;
											}
											else if ((i+3*oneonezeros == c.pos) || (i+3*oneonezeros+1 == c.pos) || (i+3*oneonezeros+2 == c.pos)) {
												p=0; q=0; r=0; //all bets are off
											}
										} while (p && q && r);
										if (oneonezeros>=4 && i+1 != c.pos && i+2 != c.pos) {
											cout << "{110}^{" << oneonezeros << "}";
											i+=3*oneonezeros-1;
										}
										else cout << "1";
									}
									else {
										int onezeros=1;
										bool p, q;
										do {
											p=c.t.a[i+2*onezeros];
											q=1-c.t.a[i+2*onezeros+1];
											if (p && q && (i+2*onezeros != c.pos) && (i+2*onezeros+1 != c.pos)) {
												onezeros++;
											}
											else if ((i+2*onezeros != c.pos) || (i+2*onezeros+1 != c.pos)) {
												p=0; q=0;
											}
										} while (p && q);
										if (onezeros>=4 && i+1 != c.pos) {
											cout << "{10}^{" << onezeros << "}";
											i+=2*onezeros-1;
										}
										else cout << "1";
									}
								}
								else {
									int zeros=1;
									bool p;
									do {
										p=1-c.t.a[i+zeros];
										if (p && (i+zeros != c.pos) && (i+zeros != c.len-1)) {
											zeros++;
										}
										else if (i+zeros == c.pos || i+zeros == c.len-1) {
											p=0;
										}
									} while (p);
									if (i+zeros==c.len-1) break;
									if (zeros>=6) {
										cout << "0^{" << zeros << "}";
										i+=zeros-1;
									}
									else cout << "0";
								}
							}
						}
					}
				}
			}
		}
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

bool runwithfunkyoutput (machine m, int l, int size, int i) {
	//Tailor-made to prove Marxen's 2nd HNR loops
	//1^{15}001^{11}000bdd1^{9}00 length:74
	mastatapey[0]=1; mastatapey[10]=1; mastatapey[20]=1; mastatapey[30]=0; mastatapey[40]=1; mastatapey[50]=0; mastatapey[60]=0; mastatapey[70]=1;
	mastatapey[1]=1; mastatapey[11]=1; mastatapey[21]=1; mastatapey[31]=1; mastatapey[41]=1; mastatapey[51]=1; mastatapey[61]=0; mastatapey[71]=1;
	mastatapey[2]=1; mastatapey[12]=1; mastatapey[22]=1; mastatapey[32]=1; mastatapey[42]=1; mastatapey[52]=1; mastatapey[62]=0; mastatapey[72]=0;
	mastatapey[3]=1; mastatapey[13]=1; mastatapey[23]=1; mastatapey[33]=1; mastatapey[43]=1; mastatapey[53]=1; mastatapey[63]=1; mastatapey[73]=0;
	mastatapey[4]=1; mastatapey[14]=1; mastatapey[24]=1; mastatapey[34]=1; mastatapey[44]=1; mastatapey[54]=1; mastatapey[64]=1;
	mastatapey[5]=1; mastatapey[15]=0; mastatapey[25]=1; mastatapey[35]=1; mastatapey[45]=1; mastatapey[55]=1; mastatapey[65]=1;
	mastatapey[6]=1; mastatapey[16]=0; mastatapey[26]=1; mastatapey[36]=0; mastatapey[46]=1; mastatapey[56]=1; mastatapey[66]=1;
	mastatapey[7]=1; mastatapey[17]=1; mastatapey[27]=1; mastatapey[37]=0; mastatapey[47]=1; mastatapey[57]=1; mastatapey[67]=1;
	mastatapey[8]=1; mastatapey[18]=1; mastatapey[28]=0; mastatapey[38]=0; mastatapey[48]=0; mastatapey[58]=1; mastatapey[68]=1;
	mastatapey[9]=1; mastatapey[19]=1; mastatapey[29]=0; mastatapey[39]=1; mastatapey[49]=0; mastatapey[59]=1; mastatapey[69]=1;	
	//const int size=100;
	tape t;
	config c;
	c.len=size;
	t.a=new bool[size];
	c.t.a=new bool[size];
	c.t=t;
	char s=0;
	//int i=size/2;
	//char* letter=new char[1];
	for (int k=0; k<l; k++) {
		if (s==127) {
			//std::cout << "HALT!\n";
			//int sigma=0;
			//for (int j=0; j<size; j++) {
			//if (t.a[j]==1) sigma++;
			//	std::cout << t.a[j];
			//}
			//std::cout << "HALT!\n";
			//std::cout << "CONTAINING " << sigma << " ONES.\n";
			return 1;
		}
		if (i==0 || i==size-1) {cout << "STOP IT NOW!!!!!!\n"; return 0;}
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
		c.t=t;
		c.s=s;
		c.pos=i;
		funkyconfigout(c);
		/*if (s==0) letter="A";
		 else if (s==1) letter="B";
		 else if (s==2) letter="C";
		 else if (s==3) letter="D";
		 else if (s==4) letter="E";
		 else if (s==127) letter="H";*/
		cout <<  k << "\n";
	}
	// std::cout << "Got to here...\n";
	// for (int j=0; j<size; j++) std::cout << t.a[j];
	return 0;
};

bool runwithforthyoutput (machine m, long long l, int size, long long i) {
	// Tailor-made for Marxen's fourth machine
	// C1L D0R  H1L E0L  D1R C1L  E1L A1R  B1L D0L  |  3911891 | ---- |1942 |  0  9  0  8  0 22  0 33  0 59  0 57  0 57  0
	mastatapey[0]=1; mastatapey[10]=1; mastatapey[20]=1; mastatapey[30]=0; mastatapey[40]=1; mastatapey[50]=0; mastatapey[60]=0; mastatapey[70]=1;
	mastatapey[1]=1; mastatapey[11]=1; mastatapey[21]=1; mastatapey[31]=1; mastatapey[41]=1; mastatapey[51]=1; mastatapey[61]=0; mastatapey[71]=1;
	mastatapey[2]=1; mastatapey[12]=1; mastatapey[22]=1; mastatapey[32]=1; mastatapey[42]=1; mastatapey[52]=1; mastatapey[62]=0; mastatapey[72]=0;
	mastatapey[3]=1; mastatapey[13]=1; mastatapey[23]=1; mastatapey[33]=1; mastatapey[43]=1; mastatapey[53]=1; mastatapey[63]=1; mastatapey[73]=0;
	mastatapey[4]=1; mastatapey[14]=1; mastatapey[24]=1; mastatapey[34]=1; mastatapey[44]=1; mastatapey[54]=1; mastatapey[64]=1;
	mastatapey[5]=1; mastatapey[15]=0; mastatapey[25]=1; mastatapey[35]=1; mastatapey[45]=1; mastatapey[55]=1; mastatapey[65]=1;
	mastatapey[6]=1; mastatapey[16]=0; mastatapey[26]=1; mastatapey[36]=0; mastatapey[46]=1; mastatapey[56]=1; mastatapey[66]=1;
	mastatapey[7]=1; mastatapey[17]=1; mastatapey[27]=1; mastatapey[37]=0; mastatapey[47]=1; mastatapey[57]=1; mastatapey[67]=1;
	mastatapey[8]=1; mastatapey[18]=1; mastatapey[28]=0; mastatapey[38]=0; mastatapey[48]=0; mastatapey[58]=1; mastatapey[68]=1;
	mastatapey[9]=1; mastatapey[19]=1; mastatapey[29]=0; mastatapey[39]=1; mastatapey[49]=0; mastatapey[59]=1; mastatapey[69]=1;	
	//const int size=100;
	tape t;
	config c;
	c.len=size;
	t.a=new bool[size];
	c.t.a=new bool[size];
	c.t=t;
	char s=0;
	//int i=size/2;
	//char* letter=new char[1];
	for (long long k=0; k<l; k++) {
		if (s==127) {
			std::cout << "HALT!\n";
			int sigma=0;
			for (int j=0; j<size; j++) {
				if (t.a[j]==1) sigma++;
				std::cout << t.a[j];
			}
			std::cout << "YES, HALT!\n";
			std::cout << "CONTAINING " << sigma << " ONES.\n";
			return 1;
		}
		if (i==0 || i==size-1) {cout << "STOP IT NOW!!!!!!\n"; return 0;}
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
		c.t=t;
		c.s=s;
		c.pos=i;
		if (c.t.a[i]==1 && c.t.a[i-1]==1 && c.t.a[i-2]==1 && c.t.a[i-3]==1 && c.t.a[i-4]==1 && c.t.a[i-5]==1 && c.t.a[i-6]==1 && c.t.a[i-7]==1 && c.t.a[i-8]==1 && c.t.a[i+1]==0 && c.t.a[i+2]==1 && c.t.a[i+3]==0) {
			bool p=1;
			int index;
			for (index=i-9; index>=0; index--) {
				if (c.t.a[index]==1) {
					p=0;
					break;
				}
			}
			if (p) {
				forthyconfigout(c);
				cout <<  k << "\n";
			}
		}
	}
	return 0;
};

bool runwithfirstyoutput (machine m, long long l, int size, long long i, int which, int mod) {
	tape t;
	config c;
	c.len=size;
	cout << "size=" << size << "\n";
	c.t.a=new bool[size];
	t.a=new bool[size];
	//c.t=t;
	for (int bleh=0; bleh<size; bleh++) {
		c.t.a[bleh]=0;
		t.a[bleh]=0;
	}
	cout << "\n";
	return runwithfirstyoutput (m, l, size, i, which, mod, c, t);
}

bool runwithfirstyoutput (machine m, long long l, int size, long long i, int which, int mod, config& c, tape &t) {
	// which=5: tailor-made for Marxen's fifth HNR
	// mastatapey2:000111110001^{11}001^{15}001^{9}0001^{9}0001^{9}
	if (which==5) {
		mastatapey2[0]=0; mastatapey2[10]=0; mastatapey2[20]=1; mastatapey2[30]=1; mastatapey2[40]=0; mastatapey2[50]=0; mastatapey2[60]=1; mastatapey2[70]=1;
		mastatapey2[1]=0; mastatapey2[11]=1; mastatapey2[21]=1; mastatapey2[31]=1; mastatapey2[41]=1; mastatapey2[51]=0; mastatapey2[61]=1; mastatapey2[71]=1;
		mastatapey2[2]=0; mastatapey2[12]=1; mastatapey2[22]=0; mastatapey2[32]=1; mastatapey2[42]=1; mastatapey2[52]=0; mastatapey2[62]=0; mastatapey2[72]=1;
		mastatapey2[3]=1; mastatapey2[13]=1; mastatapey2[23]=0; mastatapey2[33]=1; mastatapey2[43]=1; mastatapey2[53]=1; mastatapey2[63]=0; mastatapey2[73]=1;
		mastatapey2[4]=1; mastatapey2[14]=1; mastatapey2[24]=1; mastatapey2[34]=1; mastatapey2[44]=1; mastatapey2[54]=1; mastatapey2[64]=0;
		mastatapey2[5]=1; mastatapey2[15]=1; mastatapey2[25]=1; mastatapey2[35]=1; mastatapey2[45]=1; mastatapey2[55]=1; mastatapey2[65]=1;
		mastatapey2[6]=1; mastatapey2[16]=1; mastatapey2[26]=1; mastatapey2[36]=1; mastatapey2[46]=1; mastatapey2[56]=1; mastatapey2[66]=1;
		mastatapey2[7]=1; mastatapey2[17]=1; mastatapey2[27]=1; mastatapey2[37]=1; mastatapey2[47]=1; mastatapey2[57]=1; mastatapey2[67]=1;
		mastatapey2[8]=0; mastatapey2[18]=1; mastatapey2[28]=1; mastatapey2[38]=1; mastatapey2[48]=1; mastatapey2[58]=1; mastatapey2[68]=1;
	mastatapey2[9]=0; mastatapey2[19]=1; mastatapey2[29]=1; mastatapey2[39]=0; mastatapey2[49]=1; mastatapey2[59]=1; mastatapey2[69]=1;}
	
	// which=6: tailor-made for Marxen's sixth HNR
	// mastatapey2:000111110001^{9}0001^{9}0001^{9}001^{15}001^{11}; for use with the sixth HNR
	else {
		mastatapey2[0]=0; mastatapey2[10]=0; mastatapey2[20]=0; mastatapey2[30]=1; mastatapey2[40]=1; mastatapey2[50]=1; mastatapey2[60]=1; mastatapey2[70]=1;
		mastatapey2[1]=0; mastatapey2[11]=1; mastatapey2[21]=0; mastatapey2[31]=1; mastatapey2[41]=1; mastatapey2[51]=1; mastatapey2[61]=0; mastatapey2[71]=1;
		mastatapey2[2]=0; mastatapey2[12]=1; mastatapey2[22]=0; mastatapey2[32]=0; mastatapey2[42]=1; mastatapey2[52]=1; mastatapey2[62]=0; mastatapey2[72]=1;
		mastatapey2[3]=1; mastatapey2[13]=1; mastatapey2[23]=1; mastatapey2[33]=0; mastatapey2[43]=1; mastatapey2[53]=1; mastatapey2[63]=1; mastatapey2[73]=1;
		mastatapey2[4]=1; mastatapey2[14]=1; mastatapey2[24]=1; mastatapey2[34]=0; mastatapey2[44]=0; mastatapey2[54]=1; mastatapey2[64]=1;
		mastatapey2[5]=1; mastatapey2[15]=1; mastatapey2[25]=1; mastatapey2[35]=1; mastatapey2[45]=0; mastatapey2[55]=1; mastatapey2[65]=1;
		mastatapey2[6]=1; mastatapey2[16]=1; mastatapey2[26]=1; mastatapey2[36]=1; mastatapey2[46]=1; mastatapey2[56]=1; mastatapey2[66]=1;
		mastatapey2[7]=1; mastatapey2[17]=1; mastatapey2[27]=1; mastatapey2[37]=1; mastatapey2[47]=1; mastatapey2[57]=1; mastatapey2[67]=1;
		mastatapey2[8]=0; mastatapey2[18]=1; mastatapey2[28]=1; mastatapey2[38]=1; mastatapey2[48]=1; mastatapey2[58]=1; mastatapey2[68]=1;
	mastatapey2[9]=0; mastatapey2[19]=1; mastatapey2[29]=1; mastatapey2[39]=1; mastatapey2[49]=1; mastatapey2[59]=1; mastatapey2[69]=1;}
	
	//const int size=100;
	char s=0;
	//int i=size/2;
	//char* letter=new char[1];
	for (long long k=0; k<l; k++) {
		if (s==127) {
			std::cout << "HALT!\n";
			int sigma=0;
			for (int j=0; j<size; j++) {
				if (t.a[j]==1) sigma++;
				std::cout << t.a[j];
			}
			std::cout << "YES, HALT!\n";
			std::cout << "CONTAINING " << sigma << " ONES.\n";
			return 1;
		}
		if (i==0 || i==size-1) {cout << "STOP IT NOW!!!!!!\n"; return 0;}
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
		c.t=t;
		c.s=s;
		c.pos=i;
		if (k%mod == 0) {
			firstyconfigout(c);
			cout <<  k << "\n";
		}
		/*if (c.t.a[i]==1 && c.t.a[i-1]==1 && c.t.a[i-2]==1 && c.t.a[i-3]==1 && c.t.a[i-4]==1 && c.t.a[i-5]==1 && c.t.a[i-6]==1 && c.t.a[i-7]==1 && c.t.a[i-8]==1 && c.t.a[i+1]==0 && c.t.a[i+2]==1 && c.t.a[i+3]==0) {
		 bool p=1;
		 int index;
		 for (index=i-9; index>=0; index--) {
		 if (c.t.a[index]==1) {
		 p=0;
		 break;
		 }
		 }
		 if (p) {
		 forthyconfigout(c);
		 cout <<  k << "\n";
		 }
		 }*/
	}
	return 0;
};

//01101001111{10}^{4}

void teenthyconfigout (config &c) {
	bool* farouttapey=new bool[16];
	bool* gnarlytapey=new bool[12];
	bool* funkytapey=new bool[12];
	bool* radtapey=new bool[12];
	farouttapey[0]=1; gnarlytapey[0]=1; funkytapey[0]=1; radtapey[0]=1;
	farouttapey[1]=1; gnarlytapey[1]=1; funkytapey[1]=1; radtapey[1]=1;
	farouttapey[2]=1; gnarlytapey[2]=1; funkytapey[2]=1; radtapey[2]=1;
	farouttapey[3]=0; gnarlytapey[3]=1; funkytapey[3]=1; radtapey[3]=0;
	farouttapey[4]=1; gnarlytapey[4]=1; funkytapey[4]=1; radtapey[4]=1;
	farouttapey[5]=1; gnarlytapey[5]=1; funkytapey[5]=0; radtapey[5]=1;
	farouttapey[6]=1; gnarlytapey[6]=1; funkytapey[6]=1; radtapey[6]=1;
	farouttapey[7]=1; gnarlytapey[7]=0; funkytapey[7]=1; radtapey[7]=1;
	farouttapey[8]=1; gnarlytapey[8]=1; funkytapey[8]=1; radtapey[8]=1;
	farouttapey[9]=1; gnarlytapey[9]=1; funkytapey[9]=0; radtapey[9]=0;
	farouttapey[10]=1; gnarlytapey[10]=1; funkytapey[10]=0; radtapey[10]=0;
	farouttapey[11]=1; gnarlytapey[11]=0; funkytapey[11]=0; radtapey[11]=0;
	farouttapey[12]=1;
	farouttapey[13]=0;
	farouttapey[14]=0;
	farouttapey[15]=0;
	
	for (int i=0; i<c.len; i++) {
		if (i==0) {
			while (c.t.a[i]==0 && i!=c.pos) i++;
		}
		if (i==c.pos) {
			if (c.t.a[i]==0) cout << "o";
			else cout << "i";
		}
		else {
			bool eighteenthism=1;
			bool threeonesazerofiveonesthreezeros=1;
			bool sevenonesazerothreeonesazero=1;
			bool threeonesazeronineonesthreezeros=1;
			bool ismastastring=1;
			if (i<=c.pos && c.pos<i+19) eighteenthism=0;
			else {
				for (int j=0; j<19; j++) {
					if (c.t.a[i+j] != eighteenth[j]) {
						eighteenthism=0;
						break;
					}
				}
			}
			if (eighteenthism) {
				cout << "a";
				i+=18;
			}
			else {
				if (i<=c.pos && c.pos<i+16) threeonesazeronineonesthreezeros=0;
				else {
					for (int j=0; j<16; j++) {
						if (c.t.a[i+j] != farouttapey[j]) {
							threeonesazeronineonesthreezeros=0;
							break;
						}
					}
				}
				if (threeonesazeronineonesthreezeros) {
					cout << "d";
					i+=15;
				}
				else {
					if (i<=c.pos && c.pos<i+12) sevenonesazerothreeonesazero=0;
					else {
						for (int j=0; j<12; j++) {
							if (c.t.a[i+j] != gnarlytapey[j]) {
								sevenonesazerothreeonesazero=0;
								break;
							}
						}
					}
					if (sevenonesazerothreeonesazero) {
						cout << "c";
						i+=11;
					}
					else {
						if (i<=c.pos && c.pos<i+12) threeonesazerofiveonesthreezeros=0;
						else {
							for (int j=0; j<12; j++) {
								if (c.t.a[i+j] != radtapey[j]) {
									threeonesazerofiveonesthreezeros=0;
									break;
								}
							}
						}
						if (threeonesazerofiveonesthreezeros) {
							cout << "b";
							i+=11;
						}
						else {
							if (i<=c.pos && c.pos<i+74) ismastastring=0;
							else {
								for (int j=0; j<74; j++) {
									if (c.t.a[i+j] != mastatapey2[j]) {
										ismastastring=0;
										break;
									}
								}
							}
							if (ismastastring) {
								cout << "e";
								i+=73;
							}
							else {
								if (c.t.a[i]==1) {
									if (c.t.a[i+1]==1 && c.t.a[i+2]==1) {
										if (c.t.a[i+3]==1) {
											int ones=1;
											bool p;
											do {
												p=c.t.a[i+ones];
												if (p && (i+ones != c.pos)) {
													ones++;
												}
												else if (i+ones==c.pos) {
													p=0;
												}
											} while (p);
											if (ones>=6) {
												cout << "1^{" << ones << "}";
												i+=ones-1;
											}
											else cout << "1";
										}
										else { //case 1110x
											int oneoneonezerozeros=1;
											bool p, q, r, s, t;
											do {
												p=c.t.a[i+5*oneoneonezerozeros];
												q=c.t.a[i+5*oneoneonezerozeros+1];
												r=c.t.a[i+5*oneoneonezerozeros+2];
												s=1-c.t.a[i+5*oneoneonezerozeros+3];
												t=1-c.t.a[i+5*oneoneonezerozeros+4];
												if (p && q && r && s && t && (i+5*oneoneonezerozeros != c.pos) && (i+5*oneoneonezerozeros+1 != c.pos) && (i+5*oneoneonezerozeros+2 != c.pos) && (i+5*oneoneonezerozeros+3 != c.pos) && (i+5*oneoneonezerozeros+4 != c.pos)) {
													oneoneonezerozeros++;
												}
												else if ((i+5*oneoneonezerozeros == c.pos) || (i+5*oneoneonezerozeros+1 == c.pos) || (i+5*oneoneonezerozeros+2 == c.pos) || (i+5*oneoneonezerozeros+3 == c.pos) || (i+5*oneoneonezerozeros+4 == c.pos)) {
													p=0; q=0; r=0; s=0; t=0; //all bets are off
												}
											} while (p && q && r && s && t);
											if (oneoneonezerozeros>=3 && i+1 != c.pos && i+2 != c.pos && i+3 != c.pos && i+4 != c.pos) {
												cout << "{11100}^{" << oneoneonezerozeros << "}";
												i+=5*oneoneonezerozeros-1;
											}
											else cout << "1";											
										}
									}
									else if (c.t.a[i+1]==1) {//then c.t.a[i+2]=0
										int oneonezeros=1;
										bool p, q, r;
										do {
											p=c.t.a[i+3*oneonezeros];
											q=c.t.a[i+3*oneonezeros+1];
											r=1-c.t.a[i+3*oneonezeros+2];
											if (p && q && r && (i+3*oneonezeros != c.pos) && (i+3*oneonezeros+1 != c.pos) && (i+3*oneonezeros+2 != c.pos)) {
												oneonezeros++;
											}
											else if ((i+3*oneonezeros == c.pos) || (i+3*oneonezeros+1 == c.pos) || (i+3*oneonezeros+2 == c.pos)) {
												p=0; q=0; r=0; //all bets are off
											}
										} while (p && q && r);
										if (oneonezeros>=4 && i+1 != c.pos && i+2 != c.pos) {
											cout << "{110}^{" << oneonezeros << "}";
											i+=3*oneonezeros-1;
										}
										else cout << "1";
									}
									else {
										int onezeros=1;
										bool p, q;
										do {
											p=c.t.a[i+2*onezeros];
											q=1-c.t.a[i+2*onezeros+1];
											if (p && q && (i+2*onezeros != c.pos) && (i+2*onezeros+1 != c.pos)) {
												onezeros++;
											}
											else if ((i+2*onezeros != c.pos) || (i+2*onezeros+1 != c.pos)) {
												p=0; q=0;
											}
										} while (p && q);
										if (onezeros>=4 && i+1 != c.pos) {
											cout << "{10}^{" << onezeros << "}";
											i+=2*onezeros-1;
										}
										else cout << "1";
									}
								}
								else {
									int zeros=1;
									bool p;
									do {
										p=1-c.t.a[i+zeros];
										if (p && (i+zeros != c.pos) && (i+zeros != c.len-1)) {
											zeros++;
										}
										else if (i+zeros == c.pos || i+zeros == c.len-1) {
											p=0;
										}
									} while (p);
									if (i+zeros==c.len-1) break;
									if (zeros>=6) {
										cout << "0^{" << zeros << "}";
										i+=zeros-1;
									}
									else cout << "0";
								}
							}
						}
					}
				}
			}
		}
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

bool runwithteenthyoutput (machine m, long long l, int size, long long i, int which, int mod) {
	tape t;
	config c;
	c.len=size;
	cout << "size=" << size << "\n";
	c.t.a=new bool[size];
	t.a=new bool[size];
	//c.t=t;
	for (int bleh=0; bleh<size; bleh++) {
		c.t.a[bleh]=0;
		t.a[bleh]=0;
	}
	cout << "\n";
	return runwithteenthyoutput (m, l, size, i, which, mod, c, t);
}

bool runwithteenthyoutput (machine m, long long l, int size, long long i, int which, int mod, config& c, tape &t) {
	// which=5: tailor-made for Marxen's fifth HNR
	// mastatapey2:000111110001^{11}001^{15}001^{9}0001^{9}0001^{9}
	if (which==5) {
		mastatapey2[0]=0; mastatapey2[10]=0; mastatapey2[20]=1; mastatapey2[30]=1; mastatapey2[40]=0; mastatapey2[50]=0; mastatapey2[60]=1; mastatapey2[70]=1;
		mastatapey2[1]=0; mastatapey2[11]=1; mastatapey2[21]=1; mastatapey2[31]=1; mastatapey2[41]=1; mastatapey2[51]=0; mastatapey2[61]=1; mastatapey2[71]=1;
		mastatapey2[2]=0; mastatapey2[12]=1; mastatapey2[22]=0; mastatapey2[32]=1; mastatapey2[42]=1; mastatapey2[52]=0; mastatapey2[62]=0; mastatapey2[72]=1;
		mastatapey2[3]=1; mastatapey2[13]=1; mastatapey2[23]=0; mastatapey2[33]=1; mastatapey2[43]=1; mastatapey2[53]=1; mastatapey2[63]=0; mastatapey2[73]=1;
		mastatapey2[4]=1; mastatapey2[14]=1; mastatapey2[24]=1; mastatapey2[34]=1; mastatapey2[44]=1; mastatapey2[54]=1; mastatapey2[64]=0;
		mastatapey2[5]=1; mastatapey2[15]=1; mastatapey2[25]=1; mastatapey2[35]=1; mastatapey2[45]=1; mastatapey2[55]=1; mastatapey2[65]=1;
		mastatapey2[6]=1; mastatapey2[16]=1; mastatapey2[26]=1; mastatapey2[36]=1; mastatapey2[46]=1; mastatapey2[56]=1; mastatapey2[66]=1;
		mastatapey2[7]=1; mastatapey2[17]=1; mastatapey2[27]=1; mastatapey2[37]=1; mastatapey2[47]=1; mastatapey2[57]=1; mastatapey2[67]=1;
		mastatapey2[8]=0; mastatapey2[18]=1; mastatapey2[28]=1; mastatapey2[38]=1; mastatapey2[48]=1; mastatapey2[58]=1; mastatapey2[68]=1;
	mastatapey2[9]=0; mastatapey2[19]=1; mastatapey2[29]=1; mastatapey2[39]=0; mastatapey2[49]=1; mastatapey2[59]=1; mastatapey2[69]=1;}
	
	// which=6: tailor-made for Marxen's sixth HNR
	// mastatapey2:000111110001^{9}0001^{9}0001^{9}001^{15}001^{11}; for use with the sixth HNR
	else {
		mastatapey2[0]=0; mastatapey2[10]=0; mastatapey2[20]=0; mastatapey2[30]=1; mastatapey2[40]=1; mastatapey2[50]=1; mastatapey2[60]=1; mastatapey2[70]=1;
		mastatapey2[1]=0; mastatapey2[11]=1; mastatapey2[21]=0; mastatapey2[31]=1; mastatapey2[41]=1; mastatapey2[51]=1; mastatapey2[61]=0; mastatapey2[71]=1;
		mastatapey2[2]=0; mastatapey2[12]=1; mastatapey2[22]=0; mastatapey2[32]=0; mastatapey2[42]=1; mastatapey2[52]=1; mastatapey2[62]=0; mastatapey2[72]=1;
		mastatapey2[3]=1; mastatapey2[13]=1; mastatapey2[23]=1; mastatapey2[33]=0; mastatapey2[43]=1; mastatapey2[53]=1; mastatapey2[63]=1; mastatapey2[73]=1;
		mastatapey2[4]=1; mastatapey2[14]=1; mastatapey2[24]=1; mastatapey2[34]=0; mastatapey2[44]=0; mastatapey2[54]=1; mastatapey2[64]=1;
		mastatapey2[5]=1; mastatapey2[15]=1; mastatapey2[25]=1; mastatapey2[35]=1; mastatapey2[45]=0; mastatapey2[55]=1; mastatapey2[65]=1;
		mastatapey2[6]=1; mastatapey2[16]=1; mastatapey2[26]=1; mastatapey2[36]=1; mastatapey2[46]=1; mastatapey2[56]=1; mastatapey2[66]=1;
		mastatapey2[7]=1; mastatapey2[17]=1; mastatapey2[27]=1; mastatapey2[37]=1; mastatapey2[47]=1; mastatapey2[57]=1; mastatapey2[67]=1;
		mastatapey2[8]=0; mastatapey2[18]=1; mastatapey2[28]=1; mastatapey2[38]=1; mastatapey2[48]=1; mastatapey2[58]=1; mastatapey2[68]=1;
	mastatapey2[9]=0; mastatapey2[19]=1; mastatapey2[29]=1; mastatapey2[39]=1; mastatapey2[49]=1; mastatapey2[59]=1; mastatapey2[69]=1;}
	eighteenth[0]=0; eighteenth[10]=1;
	eighteenth[1]=1; eighteenth[10]=1;
	eighteenth[2]=1; eighteenth[10]=0;
	eighteenth[3]=0; eighteenth[10]=1;
	eighteenth[4]=1; eighteenth[10]=0;
	eighteenth[5]=0; eighteenth[10]=1;
	eighteenth[6]=0; eighteenth[10]=0;
	eighteenth[7]=1; eighteenth[10]=1;
	eighteenth[8]=1; eighteenth[10]=0;
	eighteenth[9]=1;
	//01101001111{10}^{4}
	//const int size=100;
	char s=0;
	//int i=size/2;
	//char* letter=new char[1];
	for (long long k=0; k<l; k++) {
		if (s==127) {
			std::cout << "HALT!\n";
			int sigma=0;
			for (int j=0; j<size; j++) {
				if (t.a[j]==1) sigma++;
				std::cout << t.a[j];
			}
			std::cout << "YES, HALT!\n";
			std::cout << "CONTAINING " << sigma << " ONES.\n";
			return 1;
		}
		if (i==0 || i==size-1) {cout << "STOP IT NOW!!!!!!\n"; return 0;}
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
		c.t=t;
		c.s=s;
		c.pos=i;
		if (k%mod == 0) {
			teenthyconfigout(c);
			cout <<  k << "\n";
		}
		/*if (c.t.a[i]==1 && c.t.a[i-1]==1 && c.t.a[i-2]==1 && c.t.a[i-3]==1 && c.t.a[i-4]==1 && c.t.a[i-5]==1 && c.t.a[i-6]==1 && c.t.a[i-7]==1 && c.t.a[i-8]==1 && c.t.a[i+1]==0 && c.t.a[i+2]==1 && c.t.a[i+3]==0) {
		 bool p=1;
		 int index;
		 for (index=i-9; index>=0; index--) {
		 if (c.t.a[index]==1) {
		 p=0;
		 break;
		 }
		 }
		 if (p) {
		 forthyconfigout(c);
		 cout <<  k << "\n";
		 }
		 }*/
	}
	return 0;
};

bool runwithawesomeoutput (machine m, long long l, long long size, long long i, int mod) {
	tape t;
	config c;
	c.len=size;
	cout << "size=" << size << "\n";
	c.t.a=new bool[size];
	t.a=new bool[size];
	//c.t=t;
	for (int bleh=0; bleh<size; bleh++) {
		c.t.a[bleh]=0;
		t.a[bleh]=0;
	}
	cout << "\n";
	return runwithawesomeoutput (m, l, size, i, mod, c, t);
}

bool runwithawesomeoutput (machine m, long long l, long long size, long long i, int mod, config& c, tape &t) {
	compconfig d;
	char s=0;
	for (long long k=0; k<l; k++) {
		if (s==127) {
			std::cout << "HALT!\n";
			int sigma=0;
			for (int j=0; j<size; j++) {
				if (t.a[j]==1) sigma++;
				std::cout << t.a[j];
			}
			std::cout << "YES, HALT!\n";
			std::cout << "CONTAINING " << sigma << " ONES.\n";
			return 1;
		}
		if (i==0) {cout << "STOP IT NOW!!!!!! (Reached beginning of tape.)\n"; return 0;}
		if (i==size-1) {cout << "STOP IT NOW!!!!!! (Reached end of tape.)\n"; return 0;}
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
		c.t=t;
		c.s=s;
		c.pos=i;
		if (k%mod == 0) {
			compress(c,d);
			compconfigout(d);
			cout <<  k+1 << "\n";
		}
	}
	return 0;
};

bool runwithhackneyedoutput (machine m, long long l, long long size, long long i, int which) {
//Feel free to mess around with conditions here
	tape t;
	config c;
	c.len=size;
	cout << "size=" << size << "\n";
	c.t.a=new bool[size];
	t.a=new bool[size];
	//c.t=t;
	for (int bleh=0; bleh<size; bleh++) {
		c.t.a[bleh]=0;
		t.a[bleh]=0;
	}
	cout << "\n";
	return runwithhackneyedoutput (m, l, size, i, which, c, t);
}

bool runwithhackneyedoutput (machine m, long long l, long long size, long long i, int which, config& c, tape &t) {
	compconfig d;
	char s=0;
	int* ss=new int[6];
	for (long long k=0; k<l; k++) {
		if (s==127) {
			std::cout << "HALT!\n";
			int sigma=0;
			for (int j=0; j<size; j++) {
				if (t.a[j]==1) sigma++;
				std::cout << t.a[j];
			}
			std::cout << "YES, HALT!\n";
			std::cout << "CONTAINING " << sigma << " ONES.\n";
			return 1;
		}
		if (i==0) {cout << "STOP IT NOW!!!!!! (Reached beginning of tape.)\n"; return 0;}
		if (i==size-1) {cout << "STOP IT NOW!!!!!! (Reached end of tape.)\n"; return 0;}
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
		c.t=t;
		c.s=s;
		c.pos=i;
		for (int index=0;index<5;index++)ss[index]=ss[index+1];
		ss[5]=s;
		if (conditionc(c,which,ss)) {
			if (which==1) {
				compress2(c,d);
				compconfigout(d,-1);
			}
			else configout3(c);
			cout <<  k+1 << "\n";
		}
	}
	return 0;
};
