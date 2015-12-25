package edu.whu.clock.graphsearch;

import java.util.ArrayList;
import java.util.LinkedList;

public class VertexIterator implements Comparable<VertexIterator> {

	private final GraphInMemory graph;
	private final int source;

	private int min;
	private ArrayList<LinkedList<VertexDelegate>> neighborhood;

	public VertexIterator(GraphInMemory graph, int source) {
		this.graph = graph;
		this.source = source;

		min = 0;
		neighborhood = new ArrayList<LinkedList<VertexDelegate>>();
		LinkedList<VertexDelegate> list = new LinkedList<VertexDelegate>();
		VertexDelegate spv = new VertexDelegate(source, source, 0, null);
		list.add(spv);
		neighborhood.add(list);
	}

	public VertexDelegate next() {
		VertexDelegate result = neighborhood.get(min).poll();
		int[] neighbors = graph.getNeighbors(result.getID());
		if (neighbors != null) {
			for (int neighbor : neighbors) {
				VertexDelegate spv = new VertexDelegate(neighbor, source, min + 1, result);
				if (neighborhood.size() == min + 1) {
					LinkedList<VertexDelegate> list = new LinkedList<VertexDelegate>();
					list.add(spv);
					neighborhood.add(list);
				}
				else {
					neighborhood.get(min + 1).add(spv);
				}
			}
		}
		if (neighborhood.get(min).isEmpty() && neighborhood.size() > min + 1) {
			min++;
		}
		return result;
	}
	
	public int peek() {
		return min;
	}
	
	public boolean hasNext() {
		return !neighborhood.get(min).isEmpty();
	}

	public int getSource() {
		return source;
	}

	@Override
	public int compareTo(VertexIterator o) {
		if (peek() > o.peek()) {
			return 1;
		}
		else if (peek() < o.peek()) {
			return -1;
		}
		else {
			return 0;
		}
	}
}
