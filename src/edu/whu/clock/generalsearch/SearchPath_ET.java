package edu.whu.clock.generalsearch;

import edu.whu.clock.newprobindex.IndexedEdgeTyped;

public class SearchPath_ET {

	private final short[] nodes;
	private final short[] types;
	private final boolean[] outs;
	private int indexID; // Fibonacci¶ÑËùÐèÒª
	
	public SearchPath_ET(short start, short type, boolean out, short end) {
		nodes = new short[2];
		nodes[0] = start;
		nodes[1] = end;
		types = new short[1];
		types[0] = type;
		outs = new boolean[1];
		outs[0] = out;
	}
	
	public SearchPath_ET(IndexedEdgeTyped edge) {
		nodes = new short[2];
		nodes[0] = edge.getStart();
		nodes[1] = edge.getEnd();
		types = new short[1];
		types[0] = edge.getType();
		outs = new boolean[1];
		outs[0] = edge.isOut();
	}
	
	public SearchPath_ET(SearchPath_ET sp, short type, boolean out, short end) {
		nodes = new short[sp.nodeNum() + 1];
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
	
	public short getNode(int i) {
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
	
}
