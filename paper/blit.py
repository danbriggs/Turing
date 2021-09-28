from matplotlib import pyplot as plt
import numpy as np

filename = input('filename:')
#Now right-justify the image for HNR#16,
#left-justify for HNRS #24, 38
start = filename[:2]
if start=='16' or start=='38':
    mode = 'r'
elif start=='24':
    mode = 'l'
else:
    mode = input("l or r flush:")
with open(filename) as f:
    lines = f.readlines()
bitstrings = []
for line in lines:
    li = line.strip().split()
    bitstring = li[2]
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
plt.show()
