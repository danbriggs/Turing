# Using The Program

When you run it, you will likely see [this](display.jpg).  
Make sure that the number in the "End Step" field is not much greater than 1000, and click "Run."  
This will run the hardly non-regular machine #"Machine" in Skelet's list for "End Step" steps,  
and display the tape starting at step number "Start Step."

If you want a longer run, the limiting factor will be how many characters need to be displayed.  
Showing a run e.g. from step 1000000 to step 1000500 is quick for machine #9,  
since the amount of tape it accesses grows only logarithmically with the number of steps,  
but it wouldn't be quick for other machines, since they have so many symbols on the tape by then.

To show only a subset of the steps along the way, click "Analytic run" in the "Run" menu.  
Then click "Left edge" in the same menu, so that it is checked.  
Now you can easily run a machine from step 0 to step 10000,  
and only the steps where the tape head is on the left will be output.

## Big Stretch Tape Test 2

With "Machine" set to 1, "Start Step" 0 and "End Step" 1000, click the lower-right button, "Big Stretch Test 2."  
You should see the following output:  

    Big Stretch Tape Test 2 beginning from step #0 to step #1000.
    HNR#1: [2, 5, 4, 11, 20, 26, 18, 5, 31, 11, 20, 26, 68, 66, 11, 20, 26, 48, 5, 151, 36, 42, 122, 58, 68, 74]
    Big Stretch Tape Test 2 successful.

The list, which I call the __spectrum__ of the machine, is the differences in start steps of __pushing phases__,  
meaning when the machine is increasing the extent of tape it has ever accessed.  
Compare this to the results of an "Analytic run" of machine #1 from step 0 to step 1000  
with both "Left edge" and "Right edge" checked; you should see that the length of the output is increasing  
at steps 1, 3, 8, 12, 23, etc., and the differences in consecutive terms of this sequence are 2, 5, 4, 11, etc.  
(It's also increasing at step 13, but that's part of the same "pushing phase" as step 12.)  
Big Stretch Test 2 really showcases how many steps can be run when the tape doesn't have to be output.  
Set the start step to 10000000 (ten million), the end step to 100000000 (one hundred million),  
and the machine number to anything from 1 to 43.  
Then click on "Big Stretch Test 2" (don't click on "Run" or you'll have to force quit the program!).  
It will output up to 432 differences between consecutive "pushing phases" starting at or after step 10000000.  
Examining spectra can be a good way to determine if machines are likely equivalent to each other.  

## [Next Page: Understanding Lemmas](lemmas.md)