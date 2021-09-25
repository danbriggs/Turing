f = open('15-2.txt')
r = f.readlines()
linenos=[]
processedlines = []
barestrings = []
for line in r:
    li = line.split()
    linenos.append(int(li[0]))
    st = li[2]
    b = '$i'
    barestring = ''
    counter = 0
    countingOnes = True
    
    for c in st[1:]:
        
        if countingOnes and c=='1':
            counter += 1
        elif countingOnes:
            b+='1^{'+str(counter)+'}'
            countingOnes = False
            b+=c
            barestring+=c
        else:
            b+=c
            barestring+=c
    barestrings.append(barestring)
    processedlines.append(li[0]+' '+li[1]+' '+b+'$')
    print(processedlines[-1])
differences = []
for i in range(1,len(linenos)):
    differences.append(linenos[i]-linenos[i-1])
print(differences)
quotdiffs = []
for i in range(len(differences)-1):
    quotdiffs.append(differences[i+1]-2*differences[i])
print(quotdiffs)

processedlines[0]+=' diff off'

for i in range(1,len(differences)+1):
    processedlines[i]+=' +'+str(differences[i-1])
    processedlines[i]+=' '+str(differences[i-1]-18*2**i)
    #if i>=2:
    #    processedlines[i]+=' =*2+'+str(quotdiffs[i-2])


def first_new_one(cu,ne):
    for i in range(len(cu)):
        if cu[i]=='0' and ne[i]=='1':
            return i

indices=[]
for i in range(len(barestrings)-1):
    currst=barestrings[i]
    nextst=barestrings[i+1]
    index=first_new_one(currst,nextst)
    print(index)
    indices.append(index)

for i in range(len(processedlines)-1):
    nextline=processedlines[i+1]
    nextpair=nextline.split('}')
    remainder = nextpair[1]
    bestindex=indices[i]
    redone = '{\\textcolor{red}{1}}'
    processedlines[i+1]=nextpair[0]+'}'+remainder[:bestindex]+redone
    processedlines[i+1]+=remainder[bestindex+1:].replace('01'*30,'(01)^{30}')

for line in processedlines:
    print(line)

texedver=[]
for line in processedlines:
    texedver=line.replace(' ','&')+'\\\\'
    print(texedver)


for i in range(len(differences)):
    expected = 36*2**i
    actual = differences[i]
    diff = actual - expected
    print(diff,indices[i]-2*i)
