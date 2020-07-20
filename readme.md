The goal of this project is to write accelerations for hardly non-regular five-state Turing machines,  
with the aim of proving they don't halt or running them until they do.

# Early History of the Busy Beaver Problem

Tibor Radó published [On Non-Computable Functions](https://archive.org/details/bstj41-3-877/mode/2up), in which he defined the Busy Beaver game, in 1962.  
He defined S(n) as the largest number of steps an n-state Turing machine can run for on a blank tape and then halt;  
he defined Σ(n) as the largest number of symbols an n-state Turing machine can leave on a blank tape and then halt.  
(Turing Machines by definition use just 0 & 1, and 0s are blanks, so Σ(n) is the largest number of 1s it can leave.)  
He showed that both functions are non-computable.

Shen Lin and Tibor Radó published [Computer Studies of Turing Machine Problems](https://dl.acm.org/doi/10.1145/321264.321270) in 1965,  
based on Lin's [1963 Ohio State Ph.D. thesis of the same name](https://etd.ohiolink.edu/!etd.send_file?accession=osu1486554418657614&disposition=inline) accepted by Radó.  
In it, Lin demonstrated by computer program that Σ(3)=6 and S(3)=21. The values came from two different machines.  
(Appel and Haken are famous for using a computer for a proof in 1977, but Lin's paper did this 14 years earlier.)

Allen H. Brady presented "Solution of the non-computable “Busy Beaver” game for k=4."  
at the ACM Computer Science Conference in Washington, D.C. in 1975 and wrote  
[The Determination of the Value of Rado's Noncomputable Function Σ(k) for Four-State Turing Machines](https://www.ams.org/journals/mcom/1983-40-162/S0025-5718-1983-0689479-6/S0025-5718-1983-0689479-6.pdf) in 1983.  
In it, a computer program showed that Σ(4)=13 and S(4)=107, both records achieved by the same machine.  
He also defined __Christmas trees__ based on personal communication from Lin:  
machines whose action simply sweeps over the tape both ways, increasing the swath by a constant each pass;  
and __counters__, whose action is equivalent to counting up in binary or ternary etc.  
This left n=5 as the lowest number for which Σ(n) and S(n) were unknown.

# History of the Busy Beaver of 5

Heiner Marxen and Jürgen Buntrock published [Attacking the Busy Beaver 5](http://web.archive.org/web/20170620034704/http://www.drb.insel.de/~heiner/BB/mabu90.html) in 1990.  
In it, they showed how by using symmetry, their computer program reduced the naïve estimate of  
(((5+1)×2×2)^2)^5=24^10≈6.34×10^13 5-state machines to only about 8.8×10^7 machines necessary to check.  
They used __exponential notation__ to indicate sequences repeating an arbitrary number of times.  
They also found a machine establishing that Σ(5)≥4098 and S(5)≥47176870.  
Their champion reigns to this day.

Skelet (Georgi Georgiev) uploaded a [webpage](https://skelet.ludost.net/bb/nreg.html) in 2003  
alleging that his [program](https://skelet.ludost.net/bb/index.html) left only 43 Turing machines to be checked to finish the Busy Beaver of 5.  
He calls these machines __HNR__, meaning "HardlyNonRegular."

univerz (Pavel Kropitz) found automated proofs for twelve of these machines in April 2010,  
and [uploaded eleven of these proofs](https://web.archive.org/web/20130521181342/http://fu-solution.com/univerz/projects/unibb/machines/skelet/).

By July 24, 2010, I found proofs taking care of eleven of Skelet's 43 machines.  
These proofs were done by hand, and some (especially the one for HNR#12) are very hard to read.  
I didn't know about univerz's proofs, so we ended up writing proofs for seven of the same machines.  
Thus, 16 machines had been taken care of between us, and 27 remained.

On July 19, 2020, I found some equivalences, so now 18 machines are done and there remain 25.  
There are also some very near equivalences, so at most 21 essentially different proofs are needed.

# How Can I Help?

If you have git and a Java IDE, you can fork and/or clone this project [(how to clone repositories)](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository);  
if you don't have git, but you have a Java IDE, you can download this project as a zip file using the green Code button above.  
If you only have a Java runtime, you can download Turing.jar from the list of files above and run it.

## [Next Page: Using the Program](doc/program.md)