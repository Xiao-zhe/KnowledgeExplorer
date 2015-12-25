package notinuse;

import java.util.ArrayList;

public class SearchPathHub {

	private ArrayList<ArrayList<SearchPath>> hub;
	private boolean candidate = true;
	
	public SearchPathHub(int keywordNum) {
		hub = new ArrayList<ArrayList<SearchPath>> (keywordNum);
		for (int j = 0; j < keywordNum; j++) {
			hub.add(new ArrayList<SearchPath>());
		}
	}
	
	public boolean isComplete() {
		for (ArrayList<SearchPath> i : hub) {
			if (i.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void add(int keywordID, SearchPath path) {
		hub.get(keywordID).add(path);
	}
	
	public ArrayList<SearchPath> get(int keywordID) {
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
	
	public SearchPathRef getPredecessor(SearchPathRef delegate) {
		return null;
	}
}
