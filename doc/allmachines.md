# All Machines

A secondary goal of this project is to replicate the results that Skelet got by 2003. Having two completely separate algorithms arrive at the same, or nearly the same, list of holdouts seems to be the best achievable way to verify results at this time.

The set of symmetries [Marxen & Buntrock found in 1990](http://turbotm.de/~heiner/BB/mabu90.html) allowed them to consider there to exist approximately 88,000,000 5-state Turing machines total. The complete enumeration [Skelet used](http://skelet.ludost.net/bb/index.html) while simulating machines rounded out at approximately 150,000,000.

The enumeration we will define identifies exactly 40,960,000,000 machines. This is admittedly much higher than either of the two above enumeration schemes, but it is much more transparent, and fairly easy to go from a number to a machine and vice versa. In the course of observing machines to halt and constructing proofs of non-halting, too, most often we will be able to discount thousands or millions of consecutively-numbered machines in one step.

The broadest distinction will be whether the machine halts on a 0 or on a 1. Note that it is not necessary to consider any machine with more than one halt transition, since when run on a blank tape, at most one halt transition is reached, so upon replacement of all but one halt transition with another arbitrary transition, the machine will have the exact same behavior (for at least one choice of halt transition to keep). Without loss of generality, the halt transition is written H1L.

In the first class—halting on a 0—the state with the transition to halt will be known as B (if it were A, the machine would halt immediately; all other states are at this point symmetric). Given this, the transition from A upon encountering a 0 is WLOG A0:C1L. This is because transitioning to B would halt the machine immediately; transitioning to A would loop; the last three states are still symmetric; left and right are at this point symmetric; and if it wrote a 0 in its initial step, it would be equivalent to another machine that hasn't started yet.

Thus, machines of the first class will first transition to state C, and encounter a 0 in that state. The state and direction from C0 will be either DR, DL, CR, BR, or AR: E can be discounted, being symmetric to D; CL loops, BL halts, and AL loops with a period of 2.

Thus machines of the first class have two transitions (A0:C1L and B0:H1L) completely filled out, and another (C0) with only half its initial set of options. So the first class has 20^7 × 10 = 12,800,000,000 machines. Within this class, we will consider those where C0 writes a 1 to come *before* those where C0 writes a 0; within each possibility, transitioning to D earliest in the enumeration, followed by C, B, then A; and in the case of D, R before L. This seemingly backwards way of enumerating machines is intended to make them come out in roughly the same order in which they show up in [Skelet's list](http://skelet.ludost.net/bb/nreg.html). 

For machines of the second class, the transition H1L will WLOG show up in either A1 or B1; if A1, then A0 is WLOG B1L; and if B1, then A0 is WLOG either C1L or B1L. An argument parallel to the above shows that the transition out of the second configuration (which is B0 in the first and third subclasses, and C0 in the second) may be taken to be one of only CR, CL, BR in the first subclass; DR, DL, CR, BL, or AR in the second; and CR, CL, or AR in the third. Thus the second class has 7,680,000,000 + 12,800,000,000 + 7,680,000,000 machines; each subclass is enumerated in the same order used above; the two classes have a sum total of 40,960,000,000 machines.

## Enumeration within each slice of each of the four categories

By now we have identified that there are exactly 1,280,000,000 machines in each of 32 slices, where a slice is given by complete determination of exactly three transitions, including the initial transition and the halt transition. We have also set the order in which the slices come; now let's enumerate each slice.

The machine is currently experiencing a new transition; enumerate the possibilities for that transition using the same order we used above. Run the machine until any of the following happens:

1. It arrives at a new undetermined transition.
2. It goes for more than 107 steps (from beginning) without arriving at an undetermined transition.
3. It halts.

In cases (2) and (3), we can discount the machine, since the Busy Beaver steps function of 4 is 107. (For definiteness of the enumeration, fill in the unused transitions from seeing a 1, then those from seeing a 0, within each filling in transitions from states E, D, C, B, A in that order, using the order we defined above for each transition; this is only important for establishing a bijective correspondence, not for the algorithm).

In case (1), the machine is currently experiencing a new transition; recursively do the following.

Enumerate the possibilities for the current transition using the same order we used above. Run the machine until any of the following happens:

1. It arrives at a new undetermined transition.
2. It has gone on for a total of more than 107 steps without arriving at an undetermined transition, and only at most four states have been used.
3. It halts.
4. A configuration repeats.
5. It has gone on for a total of more than 1000 steps.

Condition (1) provides the recursion. In the other cases, definiteness of the enumeration may be achieved by filling in the unused transitions in the same way we did above.

## [Next page: How to Recognize Sweep Machines](sweepmachines.md)