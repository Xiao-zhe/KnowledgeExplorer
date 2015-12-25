package notinuse;

public class PKIndexEntry {
	private IndexedEdge[] edgeList;
	private double[] probList;

	public PKIndexEntry(IndexedEdge[] ed, double[] d) {
		edgeList = ed;
		probList = d;
	}

	public IndexedEdge[] getEdgeList() {
		return edgeList;
	}
	
	public IndexedEdge getEdge(int i) {
		return edgeList[i];
	}

	public double[] getProbList() {
		return probList;
	}
	
	public double getProb(int i) {
		return probList[i];
	}

	public int length() {
		return edgeList.length;
	}

}