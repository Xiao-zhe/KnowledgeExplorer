package edu.whu.clock.kgraphsearch.index;

public class KeywordIndexEntry_KG_ET {

	private IndexedEdge_KG_ET[] edgeList;

	public KeywordIndexEntry_KG_ET(IndexedEdge_KG_ET[] edgeList) {
		this.edgeList = edgeList;
	}

	public IndexedEdge_KG_ET[] getEdgeList() {
		return edgeList;
	}

	public IndexedEdge_KG_ET getEdge(int i) {
		return edgeList[i];
	}

	public int length() {
		return edgeList.length;
	}

}
