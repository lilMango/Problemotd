import java.util.ArrayList;


public class Word{
    boolean visited=false;
    String wordVal="";
    ArrayList<Word> adj;
    
    public Word(String val) {
	this.wordVal = val;
	adj = new ArrayList<Word>();
    }
	
    public String getValue() {
	return wordVal;
    }
    public ArrayList<Word> getAdj() {
	return adj;
    }
    
    public boolean addEdge(Word werrd) {
	if(isValidLink(werrd.getValue())) {
	    adj.add(werrd);
	    return true;
	}
	return false;
    }
    
    public boolean isValidLink(String word) {
	if(word ==null) return false;
	    if(this.wordVal.equals(word)) return false;
	    if(this.wordVal.length() != word.length()) return false;
	    
	    int i=0;
	    
	    while(i < getValue().length()) {
		if( word.charAt(i) == wordVal.charAt(i)) {
		    i++;
		}else { //mismatched character
		    if(word.substring(i+1).equals( wordVal.substring(i+1))) {
			return true;
		    }
		    return false;
		}
	    }
	    return false;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(wordVal); 
	sb.append("\t: \n\t{\t");
	for(Word w:adj) {
	    sb.append(w.getValue()+", ");
	}
	sb.append("\n\t}");

	return sb.toString();
    }
}
