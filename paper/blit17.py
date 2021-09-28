from matplotlib import pyplot as plt
import numpy as np

filename = input('filename:')
#Now right-justify the image for HNR#16,
#left-justify for HNRS #24, 38
start = filename[:2]
if start=='16':
    mode = 'r'
elif start=='24' or start=='38':
    mode = 'l'
else:
    mode = input("l or r flush:")
with open(filename) as f:
    lines = f.readlines()
steps = [] #New
states = [] #New
bitstrings = []
for line in lines:
    li = line.strip().split()
    step = li[0]
    state = li[1]
    bitstring = li[2]
    steps.append(step)
    states.append(state)
    bitstrings.append(bitstring)

#New
indices_to_remove = []
for i in range(len(bitstrings)):
    if states[i] == 'C' and bitstrings[i][-1]=='i':
        indices_to_remove.append(i+1)
        indices_to_remove.append(i+2)
        #They will automatically be implied

for i in indices_to_remove[::-1]:
    del steps[i]
    del states[i]
    del bitstrings[i]

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

def numtenshere(bi, index):
    numtens = 0
    while index + 2 <= len(bi) and bi[index:index+2]=='10':
        numtens += 1
        index += 2
    return numtens, index

def condense(bi):
    #Condenses a bitstring
    index = 0
    st = ""
    while index < len(bi) and bi[index] == '0':
        index += 1
    while index < len(bi):
        numtens, index = numtenshere(bi, index)
        if numtens > 0:
            st += "(10)^{"+str(numtens)+"}"
        else:
            st += bi[index]
            index += 1
    return st

condensed_vers = []
for bitstring in bitstrings:
    condensed = condense(bitstring)
    condensed_vers.append(condensed)

for i in range(len(condensed_vers)):
    print(steps[i]+'&'+states[i]+'&$'+condensed_vers[i]+'$\\\\')

for i in range(len(bitstrings)):
    bitstring = bitstrings[i]
    zeros_to_add = "0"*(linelen-len(bitstring))
    if mode=='r':
        bitstring = zeros_to_add+bitstring
    elif mode=='l':
        bitstring = bitstring + zeros_to_add
    bitstrings[i] = bitstring
    #print(bitstring)

arr = np.zeros((len(bitstrings),linelen,3))

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

for row_index in range(arr.shape[0]):
    arr[row_index,:,0]=from_bitstring(bitstrings[row_index])

plt.imshow(arr, interpolation='nearest')
locs, labels = plt.yticks()            # Get locations and labels
plt.yticks(locs, [steps[i] for i in [0,0,25,50,75,100,125,150,175,200]])
plt.show()
