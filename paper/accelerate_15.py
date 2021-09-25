from stepfiguration import Stepfiguration
            
if __name__=="__main__":
    with open('15-finer.txt') as f:
        lines = f.readlines()
    #for i in range(len(lines)-1):
    #    line = lines[i]
    #    nextline = lines[i+1]
    #    s = Stepfiguration(st=line,machineNum=15)
    #    nexts = Stepfiguration(st=nextline, machineNum=15)
    #    s.accelerate()
    #    areEqual = (s == nexts)
    #    print("Identical? "+str(areEqual))
    stepfigurationlist = []
    for i in range(len(lines)):
        line = lines[i]
        line = line.strip()
        s = Stepfiguration(st=line,machineNum=15)
        #print("line: "+line)
        #print("s is: "+str(s))
        stepfigurationlist.append(s)
    benchmarks = [31167,31203,31267,31407]
    for benchmark_index in range(len(benchmarks) - 1):
        old_bench = benchmarks[benchmark_index]
        new_bench = benchmarks[benchmark_index + 1]
        highestpos = 0
        for s in stepfigurationlist[old_bench-31167:new_bench-31167]:
            #Warning: the machine pushes left at the beginning of each go.
            if s.pos > highestpos:
                rightmost_s = s
                highestpos = s.pos
        print("rightmost step",rightmost_s)
        print("step displacement",rightmost_s.stepnum - old_bench,"out of",new_bench-old_bench)
        print("highestpos",highestpos)
