package edu.whu.clock.newprobsearch;

import edu.whu.clock.newgraph.SummaryGraphTyped;


public class UnfoldedProbPatternTreeTyped implements Comparable<UnfoldedProbPatternTreeTyped> {
	
	private short root;
	private SearchPathTyped[] paths;
	private double score;
	
	public UnfoldedProbPatternTreeTyped(short root, SearchPathTyped[] paths, double score) {
		super();
		this.root = root;
		this.paths = paths;
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public short getRoot() {
		return root;
	}

	public SearchPathTyped[] getPaths() {
		return paths;
	}
	
	public SearchPathTyped getPath(int index) {
		return paths[index];
	}
	
	public int numOfPaths() {
		return paths.length;
	}

	@Override
	public int compareTo(UnfoldedProbPatternTreeTyped pt) {
		double s = pt.getScore();
		if (score < s) 
			return -1;
		else if (score == s) 
			return 0;
		else 
			return 1;
	}
	
	public String getString(SummaryGraphTyped graph) {
		String str = getScore() + " £º {" + getRoot() + "."
				+ graph.classManager.getClassName(getRoot())
				+ " --- ";
		for (SearchPathTyped path : paths) {
			str += "[";
			for (int i = 0; i < path.nodeNum() - 1; i++) {
				str += path.getNode(i)
						+ "."
						+ graph.classManager.getClassName(path.getNode(i));
				if (path.isOut(i)) {
					str += " -- " + path.getType(i) + "." + graph.etypeManager.getName(path.getType(i)) + "--> ";
				}
				else {
					str += " <--" + path.getType(i) + "." + graph.etypeManager.getName(path.getType(i))  + "-- ";
				}
			}
			str += path.getNode(path.nodeNum() - 1) + "."
					+ graph.classManager.getClassName(path.getNode(path.nodeNum() - 1)) + "]";
		}
		return str;
	}

}
