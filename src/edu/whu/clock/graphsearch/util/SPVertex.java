package edu.whu.clock.graphsearch.util;



public class SPVertex extends GraphVertex implements Comparable<SPVertex> {

	private int distance;   // current distance from source
	private int predecessor;   //
	private int indexID;       // index id in fibonacci heap
	
	public SPVertex(int id, int distance) {
		this(id, distance, 0);
	}
	
	public SPVertex(int id, int distance, int predecessor) {
		super(id);
		this.distance = distance;
		this.predecessor = predecessor;
	}
	
	public int compareTo(SPVertex x) {
		double xd = ((SPVertex)x).getDistance();
		if (distance < xd) 
			return -1;
		else if (distance == xd) 
			return 0;
		else 
			return 1;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(int predecessor) {
		this.predecessor = predecessor;
	}

	public void setIndexID(int indexID) {
		this.indexID = indexID;
	}

	public int getIndexID() {
		return indexID;
	}

}
