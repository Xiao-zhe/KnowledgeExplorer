package edu.whu.clock.newprobsearch;

import java.util.ArrayList;
/**
 * This class represents a container that holds the search paths of each keyword linking to a specified node. 
 * @author clock
 * @since 2015/03/17
 * @version 0.1
 */
public class SearchPathTypedHub {

	private ArrayList<ArrayList<SearchPathTyped>> hub;
	private boolean candidate = true;
	
	public SearchPathTypedHub(int keywordNum) {
		hub = new ArrayList<ArrayList<SearchPathTyped>>(keywordNum * 2);
		for (int j = 0; j < keywordNum; j++) {
			hub.add(new ArrayList<SearchPathTyped>());
		}
		hub.trimToSize();
	}
	
	public boolean isComplete() {
		for (ArrayList<SearchPathTyped> i : hub) {
			if (i.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void add(int keywordID, SearchPathTyped path) {
		hub.get(keywordID).add(path);
	}
	
	public ArrayList<SearchPathTyped> get(int keywordID) {
		return hub.get(keywordID);
	}
	
	public boolean isCandidate() {
		return candidate;
	}
	
	public void disqualify() {
		candidate = false;
	}
	
	public double getMaxProb(int keywordID) {
		return hub.get(keywordID).get(0).getProbability();
	}
}
