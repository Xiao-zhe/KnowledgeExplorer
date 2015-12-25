package edu.whu.clock.nprobsearch;

public class Traversal {

	private final int source;
	private final int target;
	private final double probability;
	private final Traversal predecessor;
	
	public Traversal(int source, int target, double probability,
			Traversal predecessor) {
		super();
		this.source = source;
		this.target = target;
		this.probability = probability;
		this.predecessor = predecessor;
	}

	public int getSource() {
		return source;
	}

	public int getTarget() {
		return target;
	}

	public double getProbability() {
		return probability;
	}

	public Traversal getPredecessor() {
		return predecessor;
	}
	
}
