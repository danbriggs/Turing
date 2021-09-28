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

def numhundoshere(bi, index):
    """Counts the number of 1001001000s in a row."""
    numhundos = 0
    while index + 10 <= len(bi) and bi[index:index+10]=='1001001000':
        numhundos += 1
        index += 10
    return numhundos, index

def numtousoshere(bi, index):
    """Counts the number of 11011001001001010100s in a row."""
    numtousos = 0
    while index + 20 <= len(bi) and bi[index:index+20]=='11011001001001010100':
        numtousos += 1
        index += 20
    return numtousos, index

#

def condense(bi):
    #Condenses a bitstring
    index = 0
    st = ""
    while index < len(bi):
        numhundos, index = numhundoshere(bi, index)
        if numhundos > 1:
            st += "T^{"+str(numhundos)+"}"
        else:
            numtousos, index = numtousoshere(bi,index)
            if numtousos > 0:
                st += "U^{"+str(numtousos)+"}"
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

def center(st):
    """Portion between the first } and the first U."""
    if '}' in st and 'U' in st:
        i1 = st.index('}')
        i2 = st.index('U')
        return st[i1:i2+1]
    else:
        return None

new_bitstrings = []
for c in condensed_vers:
    middle = center(c)
    if middle is not None:
        new_bitstrings.append(middle)

lenmax = 0
for bitstring in new_bitstrings:
    lenny = len(bitstring)
    if lenny > lenmax:
        lenmax = lenny

for i in range(len(new_bitstrings)):
    bitstring = new_bitstrings[i]
    pad = '0'*(lenmax-len(bitstring))
    if mode=='l':
        bitstring = pad + bitstring
    elif mode=='r':
        bitstring = bitstring + pad
    new_bitstrings[i] = bitstring

arr = np.zeros((len(new_bitstrings),lenmax,3))

for row_index in range(arr.shape[0]):
    arr[row_index,:,0]=from_bitstring(new_bitstrings[row_index])

plt.imshow(arr, interpolation='nearest')
locs, labels = plt.yticks()            # Get locations and labels
plt.yticks(locs, [steps[i] for i in [0,0,50,100,150,200]])
plt.show()
