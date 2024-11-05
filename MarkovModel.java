

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarkovModel {
	
	private Map<String, Map<String, Integer>> markov = new HashMap<>();
	private static final int BASE_NGRAM = 2;
	
	public MarkovModel(ArrayList<String> cleanTokens) {
		this(cleanTokens, BASE_NGRAM);
	}

	public MarkovModel(ArrayList<String> cleanTokens, int nGram) {
	    for(int i = 0; i <= cleanTokens.size()-nGram; i++) {
	        //Current and next node in graph
	        String current = "";
	        String next = "";
	        
	        //Get nGram words and put them into node. 
	        for(int j = 0; j < nGram; j++) {
	            current += cleanTokens.get(i+j) + " ";
	        }
	        
	        //Remove last " "
	        current = current.substring(0, current.length()-1);
	        
	        //Get the next nGram words, after previous set
	        for(int j = 1; j <= nGram; j++) {
	            if(i+j+nGram-1 < cleanTokens.size()) {
	                next += cleanTokens.get(i+j+nGram-1)+ " ";
	            }
	        }
	        
	        //Remove last " "
	        if(next.length() != 0) 
		        next = next.substring(0, next.length()-1);
	        
	        
	        add(current, next);
	    }
	}
	
	
    private void add(String state, String nextState) {
    	//If state is not present, insert state and set count to 1
    	if(markov.get(state)==null) {
    		markov.put(state, new HashMap<>());
    		markov.get(state).put(nextState, 1);
    	}
		//If nextState is not present, insert nextState and set count to 1
    	else if(markov.get(state).get(nextState)==null){
    			markov.get(state).put(nextState, 1);
    		}
    	//Increase count of nextState
		else {
    		int i = markov.get(state).get(nextState);
    		markov.get(state).put(nextState, i+1);
		}
    }
    
    public String getNextState(String currentState) {
        Map<String, Integer> transitions = markov.get(currentState);
        if(transitions == null) {
            return null;
        }
        int total = 0;
        for (Integer value : transitions.values()) {
            total += value;
        }
        double p = Math.random();
        double cumulativeProbability = 0.0;
        for (Map.Entry<String, Integer> entry : transitions.entrySet()) {
            cumulativeProbability += (double) entry.getValue() / total;
            if (p <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        return null; // if no state can be returned
    }
    
    public int size() {
    	return markov.size();
    }
    
    @Override
    public String toString() {
    	return markov.toString();
    	
    }
}
