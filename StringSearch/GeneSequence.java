import java.util.*;

//Find the subsequence within the infinitely long sequence using State Machines 
public class GeneSequence {

	public static void main(String args[]){
		// A = 0,
		// C = 1
		// G = 2
		// T = 3	
		
		if(args.length<2) {
			System.out.println("please enter 2 arguments: <search seq> <string to search through>");
			return;
		}


		//String sequence = "010101";
		//String sequence = "00010001";		
		String sequence = args[0];
		StateMachine sm = new StateMachine(sequence);

		System.out.println(sm.toString());
		sm.reconnectRepeats();
		System.out.println(sm.toString());


		ArrayList<String> testcases = new ArrayList<String>();
		ArrayList<TestResult> testResults = new ArrayList<TestResult>();
		String str = args[1]; // [5,11]
		
		boolean found = false;		
		
		for(int i =0;i<str.length();i++) {
			
			int val = Integer.parseInt(str.charAt(i)+"");
			
			State state = sm.processTransition(val);
			if(sm.isFinishedState()){
				System.out.println("Found sequence at: [" + (i-sequence.length()+1) + ","+
				 i + "]");
				found=true;
				break;
			}			
		}		

		if(!found){
			System.out.println("No match found for Seq: ("+sequence+") within str("+str+")");
		}
	}

}

class TestResult {
	int idxStart;
	int idxEnd;
	boolean pass;

	public TestResult(int s,int e,boolean p){
		this.idxStart=s;
		this.idxEnd = e;
		this.pass=p;
	}

	public TestResult(boolean p){
		this.pass = p;
	}
}

class State {
		int val;
		String stateSequence="";

		HashMap<Integer,State> transitions = new HashMap<Integer,State>();

		public State(int value, String subseq){
			this.val = value;
			this.stateSequence = subseq;
		}
		public void setTransition(State state) {

			transitions.put(state.val,state);
		}

		//should be one element further, null if nothing further
		public State getNextState() {
			State cur=null;

			for(Map.Entry<Integer, State> entry: this.transitions.entrySet()) {
		        //System.out.println(entry.getKey());				        
		        cur = entry.getValue();
		        if(cur.stateSequence.length() > this.stateSequence.length()) {
		        	return cur;
		        }
			}
			return cur;
		}
	};

	class StateMachine {
		State start;
		State last;
		State cursor;

		public StateMachine(String sequence) {
			start=new State(-1,"");
			last = start;
			cursor = start;

			//get happy path of sequence
			for(int i =0;i<sequence.length();i++) {
				
				int val = Integer.parseInt(sequence.charAt(i)+"");
				String subseq =sequence.substring(0,i+1);

				addTransition(new State(val,subseq));
			}

		}

		public void addTransition(State state) {
			last.setTransition(state);
			last = state;		
		}

		public void reconnectRepeats() {
			int maxLen = last.stateSequence.length();
			if(maxLen<=1) {
				return;
			}			

			String sequence = last.stateSequence;
			//iterate through maxLen-variant subseq 1..N-1 
			for(int i=1; i<maxLen-1; i++){
				System.out.println("i:"+i);
				

				String seqA = sequence.substring(0,i);

				//SeqA -> (SeqA+t) -> ... -> SeqA' -> (SeqA'+t')
				//loop and find all instances of similar sub sequences, 
				//for each similar subsequence
					//NextTransition (SeqA' + t) -- previous val can be Next state, null, or to a previous state
					//NTL = NextTransition!=null ? NextTransition.length : 0 ;
					//if NTL < (SeqA+t).len 
						//Set SeqA'.put(t,SeqA + t)

				//Find all instances of similar sub sequences
				for(int k=1;k<maxLen-i;k++) { //skip last element, and sub seq length at edgecase
					String seqAprime = sequence.substring(k,k+i);
					if(seqA.equals(seqAprime)){
										
						System.out.println(seqA+" match at k:"+k);

						//get next character
						String t = sequence.substring(i,i+1);
						String tprime = sequence.substring(k+i,k+i+1);

						int tVal = Integer.parseInt(t);
						int tprimeVal = Integer.parseInt(tprime);

						if(!t.equals(tprime)){ //potential to add another transition (to a previous state or null)
							System.out.println("potential prime:");
							State cur = start; //SeqA + t
							State curPrime = start;//SeqA'

							//loop till you get to the (seqA + t) state
							for(int iSub=0;iSub<=i;iSub++){
								cur = cur.getNextState();
							}

							//loop till you get to the (seqA') state
							for(int iSub=0;iSub<=i+k-1;iSub++){
								curPrime = curPrime.getNextState();
							}

							State backTransitionState = curPrime.transitions.get(tVal);
							if(backTransitionState==null) {
								backTransitionState=start;
							}
							int ntl = backTransitionState !=null ? backTransitionState.stateSequence.length() : 0;
							System.out.println(curPrime.stateSequence + " + " + tVal+ " => " + backTransitionState.stateSequence);
							if(ntl < cur.stateSequence.length()) {
								System.out.println("connect to previous state");
								curPrime.transitions.put(tVal,cur);
							}
						} 	
					}
				}

			}
				
		}
		public String toString() {
			State cur = start;
			StringBuffer sb  = new StringBuffer();

			while(cur!=null) {
				sb.append(cur.stateSequence);

				int countTransitions = 0;
				sb.append(":[");
			    for(Map.Entry<Integer, State> entry: cur.transitions.entrySet()) {
			        countTransitions++;
			         if(entry.getValue().stateSequence.length()>cur.stateSequence.length() ){
			         	continue;
			         }
			         sb.append("("+entry.getKey()+"=>"+entry.getValue().stateSequence+"),");
			        
			    }

				if (countTransitions ==0) {
			    	break;
			    }else {
			    	cur = cur.getNextState();
				}
				sb.append("]");
			    sb.append("\n");


			}

			return sb.toString();
		}


		public boolean isFinishedState() {
			return (cursor == last);
		}


		public State processTransition(int t) {
			if(isFinishedState()){
				return last;
			}

		    for(Map.Entry<Integer, State> entry: cursor.transitions.entrySet()) {		        
		        if(t==entry.getKey()){

		        	cursor=entry.getValue();
		        	return cursor;
		        }
		        
		    }
		    cursor=start; //restart again
		    return cursor;
		}


	}
