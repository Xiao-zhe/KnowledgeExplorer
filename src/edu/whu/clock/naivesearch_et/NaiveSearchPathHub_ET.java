package edu.whu.clock.naivesearch_et;

import java.util.ArrayList;

public class NaiveSearchPathHub_ET {

	private ArrayList<ArrayList<NaiveSearchPath_ET>> hub;
	private boolean candidate = true;
	
	public NaiveSearchPathHub_ET(int keywordNum) {
		hub = new ArrayList<ArrayList<NaiveSearchPath_ET>>(keywordNum * 2);
		for (int j = 0; j < keywordNum; j++) {
			hub.add(new ArrayList<NaiveSearchPath_ET>());
		}
		hub.trimToSize();
	}
	
	public boolean isComplete() {
		for (ArrayList<NaiveSearchPath_ET> i : hub) {
			if (i.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void add(int keywordID, NaiveSearchPath_ET path) {
		hub.get(keywordID).add(path);
	}
	
	public ArrayList<NaiveSearchPath_ET> get(int keywordID) {
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
