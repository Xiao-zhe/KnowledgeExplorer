package edu.whu.clock.graphsearch.util;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchRecord {
	
	private ArrayList<ArrayList<SourceAndDistance>> distances;
	private HashMap<Integer, Integer> predecessors;
	private boolean status = true;       // true means candidate
	
	public SearchRecord(int groupNum) {
		distances = new ArrayList<ArrayList<SourceAndDistance>> (groupNum);
		for (int j = 0; j < groupNum; j++) {
			distances.add(new ArrayList<SourceAndDistance>());
		}
		predecessors = new HashMap<Integer, Integer>();
	}
	
	public boolean isComplete() {
		for (ArrayList<SourceAndDistance> i : distances) {
			if (i.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void setDistance(int group, int source, double distance) {
		distances.get(group).add(new SourceAndDistance(source, distance));
	}
	
	public void setPredecessor(int source, int predecessor) {
		predecessors.put(source, predecessor);
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public boolean isCandidate() {
		return status;
	}
	
	public ArrayList<SourceAndDistance> getDistances(int group) {
		return distances.get(group);
	}
	
	public double getShortestDistance(int group) {
		return distances.get(group).get(0).getDistance();
	}
	
	public HashMap<Integer, Integer> getPredecessors() {
		return predecessors;
	}
	
	public int getPredecessor(int source) {
		return predecessors.get(source).intValue();
	}

}
