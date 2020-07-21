struct tape {
	int length;
	bool* a;
};

struct config {
	tape t;
	int s;
	int pos;
	int len;
};

struct halfstate {
	bool p;
	bool d;
	char t;
};

struct state {
	halfstate z;
	halfstate o;
};

struct machine {
	int n;
	state s[5];
};
