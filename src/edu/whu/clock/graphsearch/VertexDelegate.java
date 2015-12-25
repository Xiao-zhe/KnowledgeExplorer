package edu.whu.clock.graphsearch;

public class VertexDelegate {

	private final int id;
	private final int source;
	private int distance;   // current distance from source
	private VertexDelegate predecessor;
	
	public VertexDelegate(int id, int source, int distance, VertexDelegate predecessor) {
		this.id = id;
		this.source = source;
		this.distance = distance;
		this.predecessor = predecessor;
	}

	public int getID() {
		return id;
	}

	public int getSource() {
		return source;
	}

	public int getDistance() {
		return distance;
	}

	public VertexDelegate getPredecessor() {
		return predecessor;
	}
	
	
}
