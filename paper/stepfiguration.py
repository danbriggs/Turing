class Stepfiguration():
    """Represents one of Skelet's HNRs after a certain number of steps
    in a certain state."""
    def __init__(self, stepnum=0, state='A', tape='0', pos = 0, machineNum=0, st=None):
        if machineNum < 1 or machineNum > 43:
            raise Exception("machineNum is "+str(machineNum)+" but should be 1-43")
        self.machineNum=machineNum
        if st:
            self.initFromString(st)
        else:
            self.initFromVals(stepnum, state, tape, pos, machineNum)
    def initFromString(self, st):
        li = st.split()
        l = len(li)
        if l != 3:
            raise Exception("Tape string must be of form 'stepnum state tape'")
        stepnum = int(li[0])
        state = li[1]
        tape = li[2]
        if 'o' in tape:
            pos = tape.index('o')
            tape = tape.replace('o','0')
        elif 'i' in tape:
            pos = tape.index('i')
            tape = tape.replace('i','1')
        else:
            raise Exception("Tape string must have i or o for tape head")
        self.initFromVals(stepnum, state, tape, pos)
    def initFromVals(self, stepnum, state, tape, pos):
        if stepnum < 0:
            raise Exception("stepnum is "+str(stepnum)+" < 0")
        self.stepnum = stepnum
        if (state < 'A' or state > 'E') and state != 'H':
            raise Exception("state is "+state+". Use A-E or H")
        self.state = state
        for i in range(len(tape)):
            c = tape[i]
            if c != '0' and c != '1':
                raise Exception("Bit at tape position "+str(i)+" is "+c+" but should be 0 or 1")
        self.tape=tape
        if pos < 0 or pos >= len(tape):
            raise Exception("pos is "+str(pos)+" but tape is of length "+str(len(tape)))
        self.pos=pos
    def __str__(self):
        stepnum = self.stepnum
        state = self.state
        tape = self.tape
        pos = self.pos
        if tape[pos]=='0':
            ch = 'o'
        elif tape[pos]=='1':
            ch = 'i'
        oiTape = tape[:pos]+ch+tape[pos+1:]
        return str(stepnum)+' '+state+' '+oiTape
    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        if self.machineNum != other.machineNum:
            return False
        if self.stepnum != other.stepnum:
            return False
        if self.state != other.state:
            return False
        if self.tape != other.tape:
            return False
        if self.pos != other.pos:
            return False
        return True
    def accelerate(self):
        """Determines whether this is of a format that can be accelerated
        using known accelerations, and accelerates and returns True if so.
        Does nothing and returns False otherwise. Currently for machine 15."""
        if self.machineNum == 15:
            if self.state != 'C':
                return False
            #Check if it begins i1^(4n)011
            if self.pos != 0:
                return False
            if len(self.tape) < 6:
                return False
            if self.tape[0]!="1":
                return False
            index = 1
            while index < len(self.tape) and self.tape[index]=='1':
                index += 1
            if index + 3 > len(self.tape) or index % 4 != 1:
                return False
            if self.tape[index:index+3] != "011":
                return False
            n = (index - 1) // 4
            index += 3
            #Check if next comes a string of 10s and 11s
            #with a 10 as the at-least-nth pair
            j = 0
            while index + 2 <= len(self.tape) and j < n:
                pair = self.tape[index:index+2]
                if pair != '10' and pair != '11':
                    return False
                index += 2
                j += 1
            savedIndex = index
            if index + 2 > len(self.tape):
                return False
            while index + 2 <= len(self.tape):
                pair = self.tape[index:index+2]
                if pair != '10' and pair != '11':
                    return False
                if pair == '10':
                    break
                index += 2
                j += 1
            if index + 2 > len(self.tape):
                return False
            #Now we have our values of n, j, savedIndex, and index.
            #The index + 1st bit should be turned into a 1,
            #and going back from index,
            #every other bit should be turned into a 0
            #for a total of j - n bits turned into 0s.
            #Next, an additional four 1s should be added to the beginning,
            #and the number of steps should be increased by 36*2**n-8+4*(j-n).
            midsection = self.tape[savedIndex:index]
            midsection = midsection.replace('11','10')
            newt = '1111'+self.tape[0:savedIndex]+midsection+'11'+self.tape[index+2:]
            newsteps = self.stepnum + 36*2**n-8+4*(j-n)
            self.tape = newt
            self.stepnum = newsteps
            return True
