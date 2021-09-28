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
    while index < len(bi) and bi[index] == '0':
        index += 1
    while index < len(bi):
        numTH, index = numrepshere(bi,'1001000', index)
        if numTH > 0:
            st += "(1001000)^{"+str(numTH)+"}"
            continue
        numhundreds, index = numrepshere(bi, '100', index)
        if numhundreds > 0:
            st += "(100)^{"+str(numhundreds)+"}"
            continue
        numtens, index = numrepshere(bi,'10',index)
        if numtens > 0:
            st += "(10)^{"+str(numtens)+"}"
            continue
        st += bi[index]
        index += 1
    return st

condensed_vers = []
for bitstring in bitstrings:
    condensed = condense(bitstring)
    condensed_vers.append(condensed)

bigli = []
for i in range(len(condensed_vers)):
    stepnum = int(steps[i])
    if len(condensed_vers[i]) <= 30 and stepnum >= 888:
        print(steps[i]+'&'+states[i]+'&$'+condensed_vers[i]+'$\\\\')
        bigli.append(stepnum)

while len(bigli) > 0:
    print("step numbers or differences",bigli)
    bigli = [bigli[i+1]-bigli[i] for i in range(len(bigli)-1)]

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
