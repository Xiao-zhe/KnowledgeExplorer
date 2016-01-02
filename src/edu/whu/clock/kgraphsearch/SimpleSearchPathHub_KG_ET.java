package edu.whu.clock.kgraphsearch;

import java.util.ArrayList;

public class SimpleSearchPathHub_KG_ET {

	private ArrayList<ArrayList<SimpleSearchPath_KG_ET>> hub;
	private boolean candidate = true;
	
	public SimpleSearchPathHub_KG_ET(int keywordNum) {
		hub = new ArrayList<ArrayList<SimpleSearchPath_KG_ET>>(keywordNum * 2);
		for (int j = 0; j < keywordNum; j++) {
			hub.add(new ArrayList<SimpleSearchPath_KG_ET>());
		}
		hub.trimToSize();
	}
	
	public boolean isComplete() {
		for (ArrayList<SimpleSearchPath_KG_ET> i : hub) {
			if (i.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void add(int keywordID, SimpleSearchPath_KG_ET path) {
		hub.get(keywordID).add(path);
	}
	
	public ArrayList<SimpleSearchPath_KG_ET> get(int keywordID) {
		return hub.get(keywordID);
	}
	
	public boolean isCandidate() {
		return candidate;
	}
	
	public void disqualify() {
		candidate = false;
	}
	
	public double getMinLength(int keywordID) {
		return hub.get(keywordID).get(0).getLength();
	}
}
