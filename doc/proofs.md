# Proofs

There are two ways to prove that an HNR (or any other Turing machine) doesn't halt:  
- by hand,
- automatically.

The most effective methods at the moment, however, seem to be somewhere in between the two.

Back in 2010, I would go through the output of my C-subset C++ program and analyze it to write proofs by hand.  
But [they](https://github.com/danbriggs/Turing2010/tree/master/proofs) were nearly unreadable, and so was [the program code](https://github.com/danbriggs/Turing2010).

## Linear machines

I have recently rewritten a proof of non-halting for machines 2, 5, and 6,  
and hope that you would try reading it instead:

[Proof of non-halting for machines 2, 5, and 6.](../proofs/proof2and5and6.txt)

Machines 2, 5, and 6 have __precisely linear growth__,  
meaning that the amount of tape ever accessed by them eventually increases by a constant in a constant number of steps, forever  
(to wit, by 74 every 4328 steps for each of these three; they are almost the same machine).  
Machines with precisely linear growth are the easiest to prove don't halt;  
one need only exhibit that they do the same thing over and over again.  
Unfortunately, the only machines with precisely linear growth on Skelet's list of HNRs seem to be these three, machine 18, and machine 25.

[Proof of non-halting for machine 18.](../proofs/proof18.txt)  
[Proof of non-halting for machine 25.](../proofs/proof25.txt)

The gist of it is that  
for machine 18, the tape head moves 57 spaces left every 3429 steps starting with step 6218 forever;  
for machine 25, the tape head moves 23 spaces right every 617 steps starting with step 6076 forever.

## Square root machines

Machines 8, 11, 31, and 36 have __precisely square root growth__,  
meaning that the number of steps taken to increase the amount of tape accessed by a constant  
grows as a linear function of how many steps have transpired.  
I.e., the total number of steps transpired grows as a quadratic in the length of the tape.  
(The "precise" part means that one can write a quadratic function y = f(x)
for which points (L,N) will be on the curve infinitely often,  
L being the actual length of tape accessed and N being the number of steps passed.)

Generally, this occurs when there is some increasing-length sequence among constant sequences as the tape grows.  
For example, HNR#8 turns

    (0)^(∞)o11101100(111)^(2+N)0001100000110000     &x C into
    (0)^(∞)o11101100(111)^(3+N)000110000011000000111&x C
	
in 596+80N steps.  
But depending on how many times the machine has to pass over the variable-length portion,  
a proof by hand of a statement like this gets longer and longer.

[Proof of non-halting for machine 8, using old program, rtf format, 2010](https://github.com/danbriggs/Turing2010/tree/master/proofs/8/proof%20for%208th.rtf)

There may be more machines in the list that have precisely square-root growth;  
then there are machines like 12&13 that have square-root growth, but it may not be precise.

One of the more immediate goals of the new program is to fully automate proofs for precisely square root growth machines.

## Cube root, fourth root, ..., and log machines

These concepts are defined analogously to the linear and square root growth outlined above.  
When their growth rate is precise, a proof is generally arduous but manageable;  
when imprecise, the sky's the limit on the difficulty.  
See [record7-19-20.txt](record7-19-20.txt) for the growth rates of several of the 43 machines.  
However, the growth rate may be misidentified in several cases based on local behavior.  
For example, there are several machines (such as #32 and probably #15) that exhibit counting behavior  
at least hundreds of millions of steps, and then go into a different phase.  
It will be necessary to add capability for writing and automatically proving lemmas about "counting up" in a custom numeral system,  
even just to be able to observe the action of these machines for longer.  
This will be significantly more involved than the precisely square-root growth lemmas mentioned above, but not extraordinarily difficult.

## Machine equivalences and near equivalences

Several of the 43 machines are equivalent to each other, just as in the proof of 2, 5, and 6.  
In [record7-19-20.txt](record7-19-20.txt) I use the symbol = when their actions match from the same step on;  
== when their actions match with an offset in step number;  
~~ when they exhibit matching action with slightly different tape contents in a region that seems never to be accessed, and neither has yet been proved to never halt;  
and ~ when they seem to be conducting exactly the same process but with different "start" conditions, such that their contents never match exactly.

## Working back from halt conditions

If all else fails, it might be a good idea to generate the set of all configurations that could lead to a halt state in 1, 2, 3,... steps.  
So far, this method has not yielded results for any of the HNRs. But it has been an invaluable tool working with larger sets of simpler machines!

## I just want the state diagrams for the 43 machines

They're at the bottom of [record7-19-20.txt](record7-19-20.txt).  
They were taken from [Skelet's nonregular machines page](https://skelet.ludost.net/bb/nreg.html).

## [Next page: How to knock out all 40,960,000,000 machines](allmachines.md)