package edu.whu.clock.probsearch;


public class RawProbAnswerTree {
	
	private int root;
	private SearchPathRef[] paths;
	private double score;
	
	public RawProbAnswerTree(int root, SearchPathRef[] paths, double score) {
		super();
		this.root = root;
		this.paths = paths;
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public int getRoot() {
		return root;
	}

	public SearchPathRef[] getPaths() {
		return paths;
	}

}
