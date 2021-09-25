# Automatic Sweep Machine Recognition

When the enumeration algorithm of the [previous page](allmachines.md) is carried out, and one takes a look at the machines of case (5) "has gone on for more than 1000 steps" that cannot be ruled out by a low-period shift recurrence check, the overwhelming majority of the remaining machines are what I will dub "sweep machines," meaning they run over a homogeneous swath of increasing size to the left and back to the right again, performing a few actions of another nature when at each end. Generally, the interior of the tape will not look the same once the tape head has reached the opposite side; but it will look the same again, simply with more repetitions, once the tape head returns (or in some cases, after n returns, where n > 1).

These are the machines of the **k-linear sequence** at [Skelet's page](http://skelet.ludost.net/bb/index.html) for k = 1, in his notation ".i0(e1|n1)i1" where i stands for "*introne*" (or *intron*) and e for "*exone*" (or *exon*). (These words seem to perhaps be opposite to what they should mean for k = 1, but with k > 1 the reason for these labels becomes clear.)

What the algorithm should do in this case is clear: run a few passes, use [Acceleration.bestPattern()](../src/machine/Acceleration.java#L407) to find candidates for the rightward and leftward patterns, then [Acceleration.guessLemma()](../src/machine/Acceleration.java#L483) twice to attempt to prove those patterns; if those Lemmas be proved, make an ExtendedTermfiguration representing a generalization using the first Lemma's source of the tape as it is at one moment when the tape head is fully to the left, and see if [Acceleration.act(etf,lem)](../src/machine/Acceleration.java#L88) can be used to advance to a situation identical to what the tape actually becomes after one (or more than one) full sweep.

Two attempts at implementing this procedure can be seen at [Allmachines.isSweepHelper()](../src/machine/AllMachines.java#L456) and more recently [Sweep.isSweepUsing()](../src/machine/Sweep.java#L94), which constructs an object of a new immutable class called [SweepTheorem](../src/machine/SweepTheorem.java).

The algorithm is currently under active development; so far, it has succeeded in proving only the most basic types of sweep machines.

## [Next page: extending the program code](extend.md)