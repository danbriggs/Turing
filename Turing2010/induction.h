struct expr {
//linear expression aN+b
//hope to include polynomial, exponential capability soon
	int a;
	int b;
};

struct abstrsymbol {
	bool* base;
	int baselen;
	expr exp;
	abstrsymbol* next;
	abstrsymbol* prev;
	int arrayaddress;
};

struct abstrconfig {
	abstrsymbol* a;
	int s;
	int pos;
	bool side;
	int len;
	int lhsindex;
	int rhsindex;
};

struct abstrtransition {
	char* pbase;
	int* pexp;
	int howmanyparts;
	bool d;
	char t;
};

struct abstrstate {
	abstrtransition* sl;
	abstrtransition* sr;
};

struct abstrmachine {
	abstrstate s[5];
};

struct assumption {
//The assumption that input yields output in num steps
	abstrconfig* input;
	abstrconfig* output;
	expr num;
};

struct assumptionlist {
//A list of len assumptions.
	assumption* a;
	int len;
};

void copyabstrsymbol (abstrsymbol&, abstrsymbol&);
void copyabstrconfig (abstrconfig&, abstrconfig&);
void copyabstrconfig (abstrconfig&, abstrconfig&, int);
void copyabstrconfig (abstrconfig &, abstrconfig &, int, int&);
int chartodigit(char);
expr readlinexp (char*, int &);
void readin (char*, int, int&, abstrconfig&, int&);
bool abstrconfigin (char*, int, abstrconfig&);
//here so far
void abstrsymbolout (abstrsymbol&);
void abstrconfigout (abstrconfig&);
bool exponentbreaker(abstrconfig&, int&, bool&, int&, abstrconfig&, int&, bool&, int&);
bool basebreaker(abstrconfig&, int&, bool&, int&, abstrconfig&, int&, bool&, int&);
bool eql (abstrconfig&, abstrconfig&);
bool eqlexactsymb(abstrsymbol& s, abstrsymbol& t);
bool eqlexact (abstrconfig&, abstrconfig&);
