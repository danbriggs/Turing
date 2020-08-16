import csv

def load_data(filename):
    stepnums = []
    states = []
    numzeross = []
    stringies = []
    lenlists = []
    with open(filename) as file:
        reader = csv.reader(file, delimiter=" ")
        header_row = next(reader)
        stepnum = 0
        rownum = 0
        for row in reader:
            rownum += 1
            old_stepnum = stepnum
            stepnum = int(row[0])
            tape = row[2]
            split_tape = tape.split("(")
            numzeros = (len(split_tape)-4)//2
            stepnums.append(stepnum)
            states.append(row[1])
            numzeross.append(numzeros)
            stringy = ""
            lenlist = []
            for i in range(2,len(split_tape)-1):
                if i%2==1:
                    continue
                exponent = int(split_tape[i].split("^")[1])
                lenlist.append(exponent)
            stringies.append(stringy)
            lenlists.append(lenlist)
    return stepnums, states, numzeross, stringies, lenlists

if __name__=="__main__":
    stepnums, states, numzeross, stringies, lenlists = load_data("HNR3uptoabillion.txt")
    for i in range(len(stringies)):
        print(stepnums[i],states[i],numzeross[i],sum(lenlists[i]),stringies[i])
            
