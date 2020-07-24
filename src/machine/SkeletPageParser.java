package machine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SkeletPageParser {
	private String _filename;
	private boolean _weAreCollectingMachines;
	private int _numHNRs;
	private List<Machine> _machineList;
	public List<Machine> getMachineList(){return _machineList;}
	public SkeletPageParser() {
		_filename = "HNRs.txt";
		_machineList = new ArrayList<Machine>();
		_machineList.add(new Machine());
	}
	public void readPage() throws IOException {
		/*InputStream is = FileIOTest.class.getResourceAsStream("/text.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));*/
		//File file = new File(_filename);
		InputStream is = SkeletPageParser.class.getResourceAsStream(_filename);
		//BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String st;
		_weAreCollectingMachines = false;
		_numHNRs = 0;
		while ((st = br.readLine()) != null) {
			System.out.println(st);
			parse(st);
		}
		br.close();
		System.out.println("_numHNRs: "+_numHNRs);
	} 
	private void parse(String st) {
		//Parses a line of Skelet's HNRs page.
		if (st.length()>=10 && st.substring(0,10).equals("Nonregular")) {
			_weAreCollectingMachines = true;
			return;
		}
		if (st.length()>=18 && st.substring(0,18).equals("Machines writing 0")) {
			_weAreCollectingMachines = false;
			return;
		}
		if (_weAreCollectingMachines && numVerts(st)==4 && !isHeader(st) && (trimAfterNVerts(st,2).equals("----")||trimAfterNVerts(st,2).equals("BL_2"))) {
			//proper line, so collect data as a machine.
			try {_machineList.add(process(st));
			} catch (Exception e) {
				e.printStackTrace();
			}
			_numHNRs++;
		}		
	}
	private int numVerts(String st) {
		int total = 0;
		for (int i=0; i<st.length(); i++)
			if (st.charAt(i)=='|')
				total++;
		return total;
	}
	private boolean isHeader(String st) {
		if (st.length()>=3 && st.substring(0,3).equals("A0 ")) return true;
		return false;
	}
	private String trimAfterNVerts(String st, int nVerts) {
		int currIndex=-1;
		for (int i=0; i<nVerts; i++) {
			currIndex = st.indexOf('|',currIndex+1);
		}
		int nextIndex = st.indexOf('|',currIndex+1);
		if (currIndex>=0 && nextIndex>currIndex)
			return st.substring(currIndex+1,nextIndex).trim();
		return "";
	}
	public static Machine process(String st) throws Exception {
		StringTokenizer st1 = new StringTokenizer(st," ");
		/*HNR1 = {
		{{1,1},{-1,-1},{2,4}},
		{{1,1},{-1,-1},{-1,3}},
		{{1,0},{1,-1},{3,3}},
		{{1,1},{-1,1},{0,4}},
		{{0,0},{-1,1},{1,2}}};*/
		//C1L E1L  H1L D1L  D1R D0L  A1L E1R  B0L C0R
		Transition[] t = new Transition[5];
		for (int i = 0; i<5; i++) {
			int[][] state = new int[3][2];
			String s1=st1.nextToken();
			String s2=st1.nextToken();
			System.out.println("lo "+i+":"+s1);
			System.out.println("hi "+i+":"+s2);
			spill(state,s1,s2);
			System.out.println("lo & hi processed: "+Tools.matrixToString(state));
			t[i]=new Transition(state);
			System.out.println("t["+i+"]: "+t[i]);
		}
		return new Machine(t);
		//later include the IDs
	}
	public static void spill(int[][] state, String s1, String s2) {
		state[0][0] = (int)(s1.charAt(1)-'0');
		state[0][1] = (int)(s2.charAt(1)-'0');
		state[1][0] = decodeDirection(s1.charAt(2));
		state[1][1] = decodeDirection(s2.charAt(2));
		state[2][0] = decodeState(s1.charAt(0));
		state[2][1] = decodeState(s2.charAt(0));
	}
	public static int decodeState(char c) {
		if (c=='H') return -1;
		return (int)(c-'A');
	}
	public static int decodeDirection(char c) {
		if (c=='L') return -1;
		if (c=='R') return 1;
		return 0;
	}
}
