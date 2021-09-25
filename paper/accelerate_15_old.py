from stepfiguration import Stepfiguration

filename = '15.txt' #can also use 15-2.txt, 15-3.txt
#15.txt runs in the 31 thousand to 38 billion range
#15-2.txt runs in the 76 billion to 79 billion range
#15-3.txt assumes the conjecture and goes from 76 billion to 356 octillion
with open(filename) as f:
    lines = f.readlines()
for i in range(len(lines)-1):
    line = lines[i]
    nextline = lines[i+1]
    s = Stepfiguration(st=line,machineNum=15)
    nexts = Stepfiguration(st=nextline, machineNum=15)
    s.accelerate()
    areEqual = (s == nexts)
    print("Identical? "+str(areEqual))
