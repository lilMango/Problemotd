import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class WordLadder{

    private static boolean DEBUG=false;
    static ArrayList<String> dict;
    static HashMap<String, Boolean> visited;
    static Stack<String> pathTrace;
    static Map<String,String> bfs_prev;
    static Map<String,Integer> bfs_distTo;
    
    public static ArrayList<String> getShortenedDict(int length) throws IOException
    {
	ArrayList<String> data=new ArrayList<String>();
	FileInputStream instream = new FileInputStream("wordsEn.txt");
	if(instream==null)
	    System.out.println("instream is null@");
	BufferedReader in = new BufferedReader(new InputStreamReader(instream));

	String input = "";
	while((input=in.readLine())!=null){
	    if(length == input.length()){
		data.add(input);
	    }
	}
	System.out.println("data size:="+data.size());
	return data;
    }

    public static void main(String args[]) throws IOException
    {
	if(args.length>=2){
	    System.out.println(args[0] + "->" + args[1]);
	    if(args.length>2)DEBUG=true;
	}else{
	    System.out.println("Insert 2 strings");
	    return;
	}

	//get dictionary with only n words
	dict = getShortenedDict(args[0].length());

	//check if input is correct
	int idx0 = Collections.binarySearch(dict,args[0]);
	int idx1 = Collections.binarySearch(dict,args[1]);
	
	System.out.println("input 0 @ index: "+ idx0);
	System.out.println("input 1 @ index: "+ idx1);
	
	if(idx0<0 || idx1<0){
	    System.out.println("input arguments aren't valid words!");
	    return;
	}
	
	if(DEBUG) {
	    for(String i:dict){
		System.out.println(i);
	    }
	    Word word1 = new Word("power");
	    Word word2 = new Word("tower");
	    Word word3 = new Word("beast");
	    Word word4 = new Word("WTF");
	    Word word5 = new Word("lower");
	    
	    System.out.println("power, tower:"+ word1.isValidLink("tower"));
	    System.out.println("tower, power:"+ word2.isValidLink("power"));
	    System.out.println("power, beastt:"+ word1.isValidLink("beast"));
	    System.out.println("power, WTF:" + word1.isValidLink("WTF"));
	    System.out.println("power, power:"+ word1.isValidLink("power"));
	    System.out.println("tower, toner:"+ word2.isValidLink("toner"));
	    
	    word1.addEdge(word2); 
	    word1.addEdge(word1);
	    word1.addEdge(word5);
	    System.out.println(word1.toString());
	}

	Map<String, Word> graph = new HashMap<String, Word>();

	for(String w: dict) {
	    graph.put(w, new Word(w));
	}
	
	//create links to all possible words that are 1 letter off
	for (Map.Entry entry : graph.entrySet()) {
	    String keyWord = (String) entry.getKey();
	    Word curWord = (Word)entry.getValue();

	    for(String w:dict) {
		if(!w.equals(keyWord)) {
		    curWord.addEdge(graph.get(w));
		    graph.put(keyWord, curWord);
		}
	    }
	    
	}

	System.out.println(graph.get("power"));
	System.out.println(graph.get("toned"));
	System.out.println("Does path exist? " + findPath(graph,args[0],args[1]));
    }


    //use DepthFirstSearch
    public static void dfs(Map<String,Word> graph, String cur, String dst) {
	//System.out.println("dfs: " +cur);
	visited.put(cur,true);
	pathTrace.push(cur);
	if(cur.equals(dst)){
	    pathTrace.push("9000");
	    for(String e:pathTrace) {
		System.out.print(e+" <- ");
	    }

	    System.out.println("found destination");
	}
	if("9000".equals(pathTrace.peek())){
	    return;
	}
	for(Word child: graph.get(cur).getAdj()) {
	    if(!visited.get(child.getValue())) {
		dfs(graph, child.getValue(), dst);
	    }
	}
	
	//detect flag that we found the object (added "9000" to mark end stack)
	pathTrace.pop();
    }

    //BreadthFirstSearch -- Finds Shortest path
    public static void bfs(Map<String,Word> graph, String cur, String dst) {
	bfs_prev = new HashMap<String,String>();
	bfs_distTo = new HashMap<String,Integer>();

	for(Map.Entry entry : graph.entrySet()) {
	    String keyWord = (String)entry.getKey();
	    bfs_distTo.put(keyWord,1000000);
	}
	bfs_distTo.put(cur,0);
	visited.put(cur,true);

	ArrayList<String> pathQueue = new ArrayList<String>();
	pathQueue.add(cur);

	while(!pathQueue.isEmpty()) {
	    String qCur = pathQueue.remove(0); 

	    for(Word child:graph.get(qCur).getAdj()) {
		String childString = child.getValue();

		if(!visited.get(childString)) {
		    visited.put(childString, true);
		    bfs_prev.put(childString, qCur);
		    bfs_distTo.put(childString, bfs_distTo.get(qCur)+1);

		    if(childString.equals(dst)){
			System.out.println("found destination using Breadth First Search:");
			System.out.print(dst + " -> ");
			String retrace = childString;;
			while(!retrace.equals(cur)) {
			    retrace = bfs_prev.get(retrace);
			    System.out.print(retrace + " -> ");
			}
			System.out.println("END\n");

			return;
		    }

		    pathQueue.add(childString);
		}
	    }
	}
    }


    public static boolean findPath(Map<String,Word> graph, String start, String end) {
	
	//use some path finding graph algorithm to find shortest path
	visited = new HashMap<String, Boolean>();
	pathTrace = new Stack<String>();

	for(String w:dict) {
	    visited.put(w,false);
	}

	System.out.println("Before:" +visited.get(end));

	//dfs(graph, start,end);
	bfs(graph,start,end);

	System.out.println("After:" +visited.get(end));
	return visited.get(end);

    }
    


}