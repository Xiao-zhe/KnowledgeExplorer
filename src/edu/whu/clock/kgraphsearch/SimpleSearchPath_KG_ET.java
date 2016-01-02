package edu.whu.clock.kgraphsearch;

import edu.whu.clock.kgraphsearch.index.IndexedEdge_KG_ET;
import edu.whu.clock.newgraph.EntityGraphTyped;

public class SimpleSearchPath_KG_ET implements Comparable<SimpleSearchPath_KG_ET> {

	private final int[] nodes;
	private final short[] types;
	private final boolean[] outs;
	private int indexID; // Fibonacci¶ÑËùÐèÒª
	
	public SimpleSearchPath_KG_ET(int start, short type, boolean out, int end) {
		nodes = new int[2];
		nodes[0] = start;
		nodes[1] = end;
		types = new short[1];
		types[0] = type;
		outs = new boolean[1];
		outs[0] = out;
	}
	
	public SimpleSearchPath_KG_ET(IndexedEdge_KG_ET edge) {
		nodes = new int[2];
		nodes[0] = edge.getStart();
		nodes[1] = edge.getEnd();
		types = new short[1];
		types[0] = edge.getType();
		outs = new boolean[1];
		outs[0] = edge.isOut();
	}
	
	public SimpleSearchPath_KG_ET(SimpleSearchPath_KG_ET sp, short type, boolean out, int end) {
		nodes = new int[sp.nodeNum() + 1];
		types = new short[sp.nodeNum()];
		outs = new boolean[sp.nodeNum()];
		for (int i = 0; i < sp.nodeNum() - 1; i++) {
			nodes[i] = sp.getNode(i);
			types[i] = sp.getType(i);
			outs[i] = sp.isOut(i);
		}
		nodes[sp.nodeNum() - 1] = sp.getNode(sp.nodeNum() - 1);
		types[sp.nodeNum() - 1] = type;
		outs[sp.nodeNum() - 1] = out;
		nodes[sp.nodeNum()] = end;
	}
	
	public int nodeNum() {
		return nodes.length;
	}
	
	public int getNode(int i) {
		return nodes[i];
	}
	
	public short getType(int i) {
		return types[i];
	}
	
	public boolean isOut(int i) {
		return outs[i];
	}

	public int getIndexID() {
		return indexID;
	}

	public void setIndexID(int indexID) {
		this.indexID = indexID;
	}

	public int getLength() {
		return nodeNum() - 1;
	}
	
	public boolean contains(int node) {
		for (int i : nodes) {
			if (i == node) return true;
		}
		return false;
	}

	@Override
	public int compareTo(SimpleSearchPath_KG_ET sp) {
		if (getLength() > sp.getLength()) 
			return 1;
		else if (getLength() == sp.getLength()) 
			return 0;
		else 
			return -1;
	}
	
	public String getString(EntityGraphTyped graph) {
		String str = getLength() + "[";
		for (int i = 0; i < nodeNum() - 1; i++) {
			str += getNode(i)
					+ "."
					+ graph.getInstanceManager().getInstanceName(getNode(i));
			if (isOut(i)) {
				str += " -- " + getType(i) + "." + graph.getEdgeTypeManager().getName(getType(i)) + "--> ";
			}
			else {
				str += " <--" + getType(i) + "." + graph.getEdgeTypeManager().getName(getType(i))  + "-- ";
			}
		}
		str += getNode(nodeNum() - 1) + "."
				+ graph.getInstanceManager().getInstanceName(getNode(nodeNum() - 1)) + "]";
		return str;
	}
}
