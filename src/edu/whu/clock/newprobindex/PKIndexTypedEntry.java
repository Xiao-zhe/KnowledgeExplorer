package edu.whu.clock.newprobindex;


public class PKIndexTypedEntry {
	private IndexedEdgeTyped[] edgeList;

	public PKIndexTypedEntry(IndexedEdgeTyped[] edgeList) {
		this.edgeList = edgeList;
	}

	public IndexedEdgeTyped[] getEdgeList() {
		return edgeList;
	}

	public IndexedEdgeTyped getEdge(int i) {
		return edgeList[i];
	}

	public int length() {
		return edgeList.length;
	}

}
