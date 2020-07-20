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

## Using Lemmas to Accelerate Machines

As long as you've run both induction tests, click on Act Test 1, and you should get the following output:

    Act Test 1 beginning.
    CondensedConfiguration cc before Acceleration.act(): C (011)^12 0
    numSteps: 36
    CondensedConfiguration cc after Acceleration.act():  C (110)^12 36
    Act Test 1 successful.

This means that the machine was able to pick out which lemma to use from a list of lemmas,  
and it rightly chose to use the lemma we described above.  
It applied that lemma to the CondensedConfiguration cc with an exponent of twelve on the active sequence,  
and circumvented having to execute each of 36 steps explicitly.

## Automatically Formulating Hypotheses

If you set the machine number to 1, start step to 0, and end step to 1000, and click "Longest Run" (either on the panel or in the "Run" menu),  
you should get output like this:  

    Longest run test beginning for num=1
    Looking for the longest run from 0 to 1000 for machine #1:
    ...
    The longest run started at 249 and was of length 23
    ...
    Finding the best run of perfect matches with skip 10: there are 31 matches at 304
    ...
    0	0	0	21	4	0	18	8	2	15	31	11	12	3	14	11	7	18	17	3	21	19	3	10	19	7	17	12	3	8	
    0	-1	-1	269	95	-1	266	112	229	263	304	228	260	2	750	664	118	728	692	63	294	673	298	111	679	611	52	723	214	51	
    0	0	0	19	-2	0	16	-4	-2	13	-19	-7	10	0	-8	5	-5	-10	11	-1	-13	15	-1	0	13	-5	1	-8	1	6	
    
    The best skip was 10 with 31 repetitions ending at step 304 after a displacement of -19.
    The signed term length seems to be -6.129032, so I'm going to guess it's -6.
    Tape at start step, then at end step: 
    274 D 11011011011011011011011o01
    304 D 11011o10110110110110110101
    ...
    Attempting linear induction.
    VeryTermfigurationLike a: D (110110)^(n) -1+6n
    VeryTermfigurationLike b: D (101101)^(n) -1
    int[] numsteps as polynomial: 10n
    ...
    The Lemma was proved!
    Longest run test successful.

The output is fairly long, so I have truncated it in a few places.  
The "longest run" portion of the Longest Run test is unimportant;  
what's important is instead the "best run of perfect matches," in this case "with skip 10," where there were "31 matches (ending) at 304."  
What this means is that the machine was in the same state looking at the same symbol 10 steps apart, 31 times in a row.  
Since we can see that the machine was in state D looking at 0 on step 274, it was in state D looking at 0 on step 284.  
Since we can see that the machine was in state D looking at 0 on step 304, it was in state D looking at 0 on step 314.  
We have equivalences like that as well for __every__ pair n and n+10, where n is between 274 and 304  
(although the state isn't necessarily D, and the bit isn't necessarily 0).  
This is a clue that the machine might be doing something very repetitive from step 274 to step 314,  
and so the program tries to generate a lemma about it.  
First, it guesses the length of a repeating bit sequence to use based on the fact that the tape head moves about 6 spaces left every 10 steps.  
The source a is thus constructed based on what's on the nearest six bits going left from and including where the machine started at step #274,  
and the target b based on what's on the same six bits after ten steps.

## See the [flowchart](../src/images/flowchart.png)

to learn the similarities and differences among the different classes.

The tests are in place to verify that extensions to the project aren't breaking its older functionality;

## Read [tests](tests.md)

to learn the purposes of the tests and what needs to be added to them.