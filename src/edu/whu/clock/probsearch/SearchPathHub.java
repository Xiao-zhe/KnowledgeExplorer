package edu.whu.clock.probsearch;

import java.util.ArrayList;

public class SearchPathHub {

	private ArrayList<ArrayList<SearchPathRef>> hub;
	private boolean candidate = true;
	
	public SearchPathHub(int keywordNum) {
		hub = new ArrayList<ArrayList<SearchPathRef>> (keywordNum);
		for (int j = 0; j < keywordNum; j++) {
			hub.add(new ArrayList<SearchPathRef>());
		}
	}
	
	public boolean isComplete() {
		for (ArrayList<SearchPathRef> i : hub) {
			if (i.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void add(int keywordID, SearchPathRef path) {
		hub.get(keywordID).add(path);
	}
	
	public ArrayList<SearchPathRef> get(int keywordID) {
		return hub.get(keywordID);
	}
	
	public boolean isCandidate() {
		return candidate;
	}
	
	public void disqualify() {
		candidate = false;
	}
	
	public double getMaxProb(int keywordID) {
		return hub.get(keywordID).get(0).getProb();
	}
	
	public SearchPathRef getPredecessor(SearchPathRef delegate) {
		return null;
	}
}
