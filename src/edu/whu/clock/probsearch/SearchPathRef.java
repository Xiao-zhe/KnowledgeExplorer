package edu.whu.clock.probsearch;

public class SearchPathRef implements Comparable<SearchPathRef> {

	private SearchPathRef prev;
	private int end;
	private double prob;
	
	private int indexID;
	
	public SearchPathRef(int end, double prob) {
		this.end = end;
		this.prob = prob;
	}

	public SearchPathRef(SearchPathRef prev, int end, double prob) {
		super();
		this.prev = prev;
		this.end = end;
		this.prob = prev.getProb() * prob;
	}

	@Override
	public int compareTo(SearchPathRef sp) {
		double other = sp.getProb();
		if (prob > other) //因为优先队列先出最小的
			return -1;
		else if (prob == other) 
			return 0;
		else 
			return 1;
	}

	public SearchPathRef getPrev() {
		return prev;
	}

	public int getEnd() {
		return end;
	}
	
	public double getProb() {
		return prob;
	}

	public int getIndexID() {
		return indexID;
	}

	public void setIndexID(int indexID) {
		this.indexID = indexID;
	}
	
	
	
}
