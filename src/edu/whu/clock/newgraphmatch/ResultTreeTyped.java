package edu.whu.clock.newgraphmatch;

import edu.whu.clock.generalsearch.UnfoldedPatternTree_ET;

public class ResultTreeTyped {

	private int root;
	private int[][] paths;
	private boolean[] matched;
	
	public ResultTreeTyped(int root, UnfoldedPatternTree_ET queryTree) {
		this.root = root;
		this.paths = new int[queryTree.numOfPaths()][];
		for (int i = 0; i < paths.length; i++) {
			int num = queryTree.getPath(i).nodeNum();
			paths[i] = new int[num];
			paths[i][num - 1] = root;
		}
		this.matched = new boolean[queryTree.numOfPaths()];
	}

	public int getRoot() {
		return root;
	}

	public int[][] getPaths() {
		return paths;
	}
	
	public int[] getPath(int index) {
		return paths[index];
	}
	
	public int getNode(int pathID, int index) {
		return paths[pathID][index];
	}
	
	public void setNode(int pathID, int index, int node) {
		paths[pathID][index] = node;
	}
	
	public boolean isAllMatched() {
		for (boolean b : matched) {
			if (!b) return false;
		}
		return true;
	}
	
	public boolean isMatched(int index) {
		return matched[index];
	}
	
	public void setMatched(int index, boolean b) {
		matched[index] = b;
	}
}
