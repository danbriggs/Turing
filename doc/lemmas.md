# Understanding Lemmas

Click the following three buttons in the following order:  
"Left to Right Induction Test," "Right to Left Induction Test," "Lemma as String."

Let's look at the first lemma printed by the Lemma as String Test first.

    Lemma: The machine
    A: 1,<,C/1,<,E
    B: 1,<,@/1,<,D
    C: 1,>,D/0,<,D
    D: 1,<,A/1,>,E
    E: 0,<,B/0,>,C
    transforms C (011)^(9+n) 0 into C (110)^(9+n) 27+3n in 27+3n steps: Proved.

This is a lemma about machine #1.  
First, the source, "C (011)^(9+n) 0," means given:  
the machine is in state C and encounters the sequence 011 repeated 9+n times.  
The 0 at the end means that the tape head is at the leftmost bit of the string.  
Similarly, the target, "C (110)^(9+n) 27+3n," has the tape head at the 27+3nth bit.  
Since the string consists of a sequence of length 3 repeated 9+n times,  
this puts the tape head just off the right end of the string.  
Finally, "in 27+3n steps" means that's how long it takes to turn the source into the target.  
You'll notice that the tape head moved 27+3n spots during the transformation,  
so the machine must have been moving to the right the whole time.

The lemma was proved when we clicked Left to Right Induction Test. Let's look at its output:

    Left to Right Induction Test beginning.
    C (011)^(9+n) 0
    C (110)^(9+n) 27+3n
    Attempting linear induction.
    VeryTermfigurationLike a: C (011)^(9+n) 0
    VeryTermfigurationLike b: C (110)^(9+n) 27+3n
    int[] numsteps as polynomial: 27+3n
    minFora: 0
    minForb: 0
    minForNumSteps: 0
    minForAll: 0
    Configuration ca: C o11011011011011011011011011
    Configuration cb: C 110110110110110110110110110>>
    baseNumSteps: 27
    baseCase: true
    Next up: assuming C (011)^(9+n) 0 yields C (110)^(9+n) 27+3n in 27+3n steps,
    we prove that C (011)^(10+n) 0 yields C (110)^(10+n) 30+3n in 30+3n steps.
    C (011)^(9+n) 0 has successor C (011)^(10+n) 0
    C (110)^(9+n) 27+3n has successor C (110)^(10+n) 30+3n
    Left to Right Induction Test successful.

VeryTermfigurationLike is a type that will be explained later.  
The four "min..." values are there just to determine how small a nonnegative integer n can be and have the lemma still make sense.  
In this case, 0 is a value of n for which a's exponent, b's exponent, and numsteps will all be nonnegative,  
so minForAll=0 will be used for the base case of the lemma.  
The lemma was verified for the base case simply by running the machine on an actual tape.  
How the induction hypothesis is used to prove the n+1 case from the n case will be explained later.  
But since it was successful, the lemma is considered proved, and can be used to accelerate the action of machine #1  
whenever it is in state C at the leftmost bit of a sequence of 011s repeated at least 9 times.  
(We could have just as easily written a lemma about (011)^(n) instead, and it could be used  
when the machine encounters 011 repeated __any__ number of times.)


Please see [src/images/flowchart.png](src/images/flowchart.png) to learn the similarities and differences among the different classes.

The tests are in place to verify that extensions to the project aren't breaking its older functionality; read [tests.md](tests.md) to learn the purposes of the tests and what needs to be added to them.