package edu.whu.clock.probsearch;


public class SearchPath implements Comparable<SearchPath> {

	private final short[] vertices;
	private final double probability;
	private int indexID; // Fibonacci¶ÑËùÐèÒª
	
	public SearchPath(short vertex, double prob) {
		vertices = new short[1];
		vertices[0] = vertex;
		probability = prob;
	}
	
	public SearchPath(SearchPath sp, short vertex, double prob) {
		vertices = new short[sp.length() + 1];
		for (int i = 0; i < sp.length(); i++) {
			vertices[i] = sp.getVertex(i);
		}
		vertices[sp.length()] = vertex;
		probability = sp.getProbability() * prob;
	}
	
	public int length() {
		return vertices.length;
	}
	
	public short getVertex(int i) {
		return vertices[i];
	}

	public double getProbability() {
		return probability;
	}

	@Override
	public int compareTo(SearchPath sp) {
		double prob = sp.getProbability();
		if (probability < prob) 
			return -1;
		else if (probability == prob) 
			return 0;
		else 
			return 1;
	}
	
	public int getIndexID() {
		return indexID;
	}

	public void setIndexID(int indexID) {
		this.indexID = indexID;
	}
	
}
