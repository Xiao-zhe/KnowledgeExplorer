package edu.whu.clock.graphsearch;

import java.util.ArrayList;

public class VertexVisitRecord {

	private ArrayList<ArrayList<VertexDelegate>> content;
	private boolean candidate = true;
	
	public VertexVisitRecord(int keywordNum) {
		content = new ArrayList<ArrayList<VertexDelegate>> (keywordNum);
		for (int j = 0; j < keywordNum; j++) {
			content.add(new ArrayList<VertexDelegate>());
		}
	}
	
	public boolean isComplete() {
		for (ArrayList<VertexDelegate> i : content) {
			if (i.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void add(int keywordID, VertexDelegate delegate) {
		content.get(keywordID).add(delegate);
	}
	
	public ArrayList<VertexDelegate> get(int keywordID) {
		return content.get(keywordID);
	}
	
	public boolean isCandidate() {
		return candidate;
	}
	
	public void disqualify() {
		candidate = false;
	}
	
	public int getShortestDistance(int keywordID) {
		return content.get(keywordID).get(0).getDistance();
	}
	
	public VertexDelegate getPredecessor(VertexDelegate delegate) {
		return null;
	}
}
