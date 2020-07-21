struct compsymbol {
	bool* base;
	int baselen;
	int exp;
	compsymbol* next;
	compsymbol* prev;
	int arrayaddress;
};

struct compconfig {
	compsymbol* a;
	int s;
	int pos;
	bool side;
	int len;
	int lhsindex;
	int rhsindex;
};

struct comptransition {
	char* pbase;
	int* pexp;
	int howmanyparts;
	bool d;
	char t;
};

struct compstate {
	comptransition* sl;
	comptransition* sr;
};

struct compmachine {
	compstate s[5];
};

void boolout (bool*, int);
void boolout (bool*, int, int);
void copycompsymbol (compsymbol&, compsymbol&);
void appendcompsymbol (compconfig&, compsymbol&);
void copycompconfig (compconfig&, compconfig&);
void copycompconfig (compconfig&, compconfig&, int);
void copycompconfig (compconfig &, compconfig &, int, int&);
int chartodigit(char);
int readnumber (char*, int &);
void readin (char*, int, int&, compconfig&, int&);
bool compconfigin (char*, int, compconfig&);
void compsymbolout (compsymbol&);
void compconfigout (compconfig&);
void compconfigout (compconfig&, int);
void quietcompconfigout (compconfig&);
bool exponentbreaker(compconfig&, int&, bool&, int&, compconfig&, int&, bool&, int&);
bool basebreaker(compconfig&, int&, bool&, int&, compconfig&, int&, bool&, int&);
bool eql (compconfig&, compconfig&);