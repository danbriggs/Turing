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
            if stepnum - old_stepnum < 10:
                continue
            tape = row[2]
            split_tape = tape.split("0")
            numzeros = len(split_tape)-1
            stepnums.append(stepnum)
            states.append(row[1])
            numzeross.append(numzeros)
            stringy = ""
            lenlist = []
            for i in range(len(split_tape)):
                if i > 0:
                    stringy += "0"
                if i < len(split_tape) - 1:
                    swath = split_tape[i]
                else:
                    swath = split_tape[i][:-1]
                #Make sure they're all 1s
                all_ones = True
                for char in swath:
                    if char != "1":
                        all_ones = False
                if not all_ones:
                    print("Not all 1s, rownum =",rownum,"and i=",i)
                else:
                    lengthy = len(swath)
                    stringy += "1^(" + str(lengthy) + ")"
                    lenlist.append(lengthy)
                if i == len(split_tape) - 1:
                    stringy += split_tape[i][-1]
            stringies.append(stringy)
            lenlists.append(lenlist)
    return stepnums, states, numzeross, stringies, lenlists

if __name__=="__main__":
    stepnums, states, numzeross, stringies, lenlists = load_data("analysis8-11-20.txt")
    for i in range(len(stringies)):
        print(stepnums[i],states[i],numzeross[i],sum(lenlists[i]),stringies[i])
            
