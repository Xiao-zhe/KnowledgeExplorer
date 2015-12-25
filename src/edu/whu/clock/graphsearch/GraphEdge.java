package edu.whu.clock.graphsearch;

import java.io.Serializable;
import java.util.HashSet;

public class GraphEdge implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 330485379433422624L;
	private int end;
	private HashSet<Integer> types;
	private GraphEdge nextEdge;

//	public GraphEdge(String end, GraphEdge nextEdge) {
//		this.end = end;
//		this.types = new HashSet<String>();
//		this.nextEdge = nextEdge;
//	}
	
	public GraphEdge(int end, int type, GraphEdge nextEdge) {
		this.end = end;
		this.types = new HashSet<Integer>();
		types.add(type);
		this.nextEdge = nextEdge;
	}

	public int getEnd() {
		return end;
	}

	public GraphEdge getNextEdge() {
		return nextEdge;
	}

	public void setNextEdge(GraphEdge nextEdge) {
		this.nextEdge = nextEdge;
	}
	
	public void addType(int type) {
		this.types.add(type);
	}
	
}
