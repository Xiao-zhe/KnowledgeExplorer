package edu.whu.clock.naivesearch_et;

import edu.whu.clock.generalsearch.SearchPath_ET;
import edu.whu.clock.newgraph.SummaryGraphTyped;
import edu.whu.clock.newprobindex.IndexedEdgeTyped;

public class NaiveSearchPath_ET extends SearchPath_ET implements Comparable<NaiveSearchPath_ET> {
	
	public NaiveSearchPath_ET(short start, short type, boolean out, short end) {
		super(start, type, out, end);
	}
	
	public NaiveSearchPath_ET(IndexedEdgeTyped edge) {
		super(edge);
	}
	
	public NaiveSearchPath_ET(NaiveSearchPath_ET sp, short type, boolean out, short end) {
		super(sp, type, out, end);
	}

	public int getLength() {
		return nodeNum() - 1;
	}

	@Override
	public int compareTo(NaiveSearchPath_ET sp) {
		if (getLength() > sp.getLength()) 
			return 1;
		else if (getLength() == sp.getLength()) 
			return 0;
		else 
			return -1;
	}
	
	public String getString(SummaryGraphTyped graph) {
		String str = getLength() + "[";
		for (int i = 0; i < nodeNum() - 1; i++) {
			str += getNode(i)
					+ "."
					+ graph.classManager.getClassName(getNode(i));
			if (isOut(i)) {
				str += " -- " + getType(i) + "." + graph.etypeManager.getName(getType(i)) + "--> ";
			}
			else {
				str += " <--" + getType(i) + "." + graph.etypeManager.getName(getType(i))  + "-- ";
			}
		}
		str += getNode(nodeNum() - 1) + "."
				+ graph.classManager.getClassName(getNode(nodeNum() - 1)) + "]";
		return str;
	}
	
}
