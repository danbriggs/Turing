from matplotlib import pyplot as plt
import numpy as np

filename = input('filename:')
start = filename[:2]
mode = input("l or r flush:")
with open(filename) as f:
    lines = f.readlines()
steps = []
states = []
bitstrings = []
for i in range(len(lines)):
    if i%4 != 0:
        continue
    line = lines[i]
    li = line.strip().split()
    step = li[0]
    state = li[1]
    bitstring = li[2]
    steps.append(step)
    states.append(state)
    bitstrings.append(bitstring)

lengths = []
chars_used = []
for bitstring in bitstrings:
    length = len(bitstring)
    lengths.append(length)
    for c in bitstring:
        if c not in chars_used:
            chars_used.append(c)

print("lengths",lengths)
print("chars_used",chars_used)
linelen = max(lengths)
print("linelen",linelen)

def numrepshere(bi, st, index):
    numreps = 0
    while index + len(st) <= len(bi) and bi[index:index+len(st)]==st:
        numreps += 1
        index += len(st)
    return numreps, index

def condense(bi):
    #Condenses a bitstring
    index = 0
    st = ""
    while index < len(bi):
        numTH, index = numrepshere(bi,'110', index)
        if numTH > 0:
            st += "(110)^{"+str(numTH)+"}"
            continue
        numTH, index = numrepshere(bi,'10', index)
        if numTH > 0:
            st += "(10)^{"+str(numTH)+"}"
            continue
        st += bi[index]
        index += 1
    return st

def simple_condense(bi):
    retval = bi[0]
    index = 1
    numzeros = 0
    while index < len(bi) and bi[index]=='0':
        numzeros += 1
        index += 1
    retval += "0^{"+str(numzeros)+"}"
    retval += bi[index:]
    return retval

condensed_vers = []
for bitstring in bitstrings:
    condensed = condense(bitstring)
    condensed_vers.append(condensed)

bigli = []
for i in range(len(condensed_vers)):
    stepnum = int(steps[i])
    print(steps[i]+'&'+states[i]+'&$'+condensed_vers[i]+'$\\\\')
    bigli.append(stepnum)

for i in range(10):
    print("step numbers or differences",bigli)
    bigli = [bigli[i+1]-bigli[i] for i in range(len(bigli)-1)]

def from_bitstring(bi):
    #print("len(bi)",len(bi))
    retval = np.zeros(len(bi))
    for i in range(len(bi)):
        c = bi[i]
        if c=='0' or c=='o':
            retval[i] = 0
        elif c=='1' or c=='i':
            retval[i] = 1
    #print("retval",retval)
    return retval

def past11110(bi):
    """returns the index past the first swath of 11110s in bi."""
    for i in range(len(bi)//5):
        index = 5*i
        if bi[index]=='0':
            #print('index',index)
            return index
    #print('got to None')
    return None

def pad_blist(blist, linelen, mode):
    for i in range(len(blist)):
        bitstring = blist[i]
        zeros_to_add = "0"*(linelen-len(bitstring))
        if mode=='r':
            bitstring = zeros_to_add+bitstring
        elif mode=='l':
            bitstring = bitstring + zeros_to_add
        blist[i] = bitstring
        #print(bitstring)

def pad_and_show(blist, mode):
    #print("The blist I will show is",blist)
    maxlen = max([len(b) for b in blist])
    pad_blist(blist, maxlen, mode)
    arr = np.zeros((len(blist),maxlen,3))

    for row_index in range(arr.shape[0]):
        arr[row_index,:,0]=from_bitstring(blist[row_index])

    #plt.imshow(arr, interpolation='nearest')
    #locs, labels = plt.yticks()            # Get locations and labels
    #plt.yticks(locs, [steps[i] for i in [0,0,25,50,75,100,125,150,175,200]])
    #plt.show()

#print(len(bitstrings), "bitstrings at end:")
#for b in bitstrings:
#    print(b)
#print("")

#bitstring_indices = [past11110(bitstring) for bitstring in bitstrings]
#sub_bitstrings = [bitstrings[i][bitstring_indices[i]:] for i in range(len(bitstrings))]
#pad_and_show(bitstrings, mode)
#pad_and_show(sub_bitstrings, mode)
