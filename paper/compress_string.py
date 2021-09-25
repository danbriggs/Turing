def compress_string(st):
    temp = st.split('0')
    lennies = [len(swath) for swath in temp]
    temp2 = ['1^{'+str(lenny)+'}' for lenny in lennies]
    collected = 'i'
    for term in temp2:
        collected += term
        collected += '0'
    return collected[:-1]

def compress_full(st):
    li = st.split(' ') #should have three elements
    return li[0]+'& '+li[1]+'& $'+compress_string(li[2])+'$\\'

def pare_and_tex(fn):
    with open(fn) as file:
        lines = file.readlines()
        lines = [line.rstrip() for line in lines]
        for line in lines:
            line_as_list = line.split(' ')
            line_as_list[0]+='&'
            line_as_list[1]+='&'
            line_as_list[2]='$'+line_as_list[2][8:-8]+'}$\\\\'
            modified_version = ''
            for t in line_as_list[2]:
                if t=='(':
                    modified_version+= '}'
                elif t==')':
                    continue
                elif t=='^':
                    modified_version += '^{'
                else:
                    modified_version += t
            line_as_list[2]=modified_version[0]+modified_version[2:]
            del line_as_list[3]
            print(''.join(line_as_list))
