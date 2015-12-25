package edu.whu.clock.probsearch;

import java.util.HashSet;

public class ProbSchemaGraphEdge {

	private int end;
	private HashSet<Integer> types;
	private ProbSchemaGraphEdge nextEdge;
	private double probability;
	

//	public GraphEdge(String end, GraphEdge nextEdge) {
//		this.end = end;
//		this.types = new HashSet<String>();
//		this.nextEdge = nextEdge;
//	}
	
	public ProbSchemaGraphEdge(int end, int type, ProbSchemaGraphEdge nextEdge) {
		this.end = end;
		this.probability = 0d;
		this.types = new HashSet<Integer>();
		types.add(type);
		this.nextEdge = nextEdge;
		
	}
	
	public ProbSchemaGraphEdge(int end, double probability, HashSet<Integer> types, ProbSchemaGraphEdge nextEdge) {
		this.end = end;
		this.probability = probability;
		this.types = types;
		this.nextEdge = nextEdge;
		
	}

	public int getEnd() {
		return end;
	}

	public ProbSchemaGraphEdge getNextEdge() {
		return nextEdge;
	}

	public void setNextEdge(ProbSchemaGraphEdge nextEdge) {
		this.nextEdge = nextEdge;
	}
	
	public void addType(int type) {
		this.types.add(type);
	}
	
	public void setP(double f)
	{
		this.probability = f;
	}
	
	public double getP()
	{
		return probability;
	}
	
	public HashSet<Integer> getTypes()
	{
		return types;
	}
	
}
