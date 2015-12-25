package edu.whu.clock.naivesearch_et;

import java.util.NoSuchElementException;

import edu.whu.clock.graphsearch.util.FibonacciHeapPQ;
import edu.whu.clock.graphsearch.util.UnderflowException;
import edu.whu.clock.newgraph.SummaryGraphEdgeTyped;
import edu.whu.clock.newgraph.SummaryGraphTyped;

public class NaiveSearchPathPQ_ET implements Comparable<NaiveSearchPathPQ_ET> {

	private final int keywordID;
	private final SummaryGraphTyped graph;
	private FibonacciHeapPQ<NaiveSearchPath_ET> queue;
	
	private int pathNum;
	private boolean limited = false;
	
	public NaiveSearchPathPQ_ET(int keywordID, SummaryGraphTyped graph) {
		super();
		this.keywordID = keywordID;
		this.graph = graph;
		this.queue = new FibonacciHeapPQ<NaiveSearchPath_ET>();
	}
	
	public int getKeywordID() {
		return keywordID;
	}

	public void insert(NaiveSearchPath_ET sp) {
		int indexID = queue.enqueue(sp);
		sp.setIndexID(indexID);
		pathNum++;
	}
	
	public boolean hasNext() {
		return ! queue.isEmpty();
	}
	
	public NaiveSearchPath_ET next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		NaiveSearchPath_ET min = null;
		try {
			min = queue.dequeueMin();
			pathNum--;
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
			return null;
		}
		if (min.getLength() < 5 && !limited) { // 如果路径长度达到5，则不再扩展该路径。
			short node2 = min.getNode(min.nodeNum() - 1);
			SummaryGraphEdgeTyped[] neighbors = graph.getNeighbors(node2); // 取路径上终端节点的邻居节点集合
			if(neighbors!=null) {
				for (int i = 0; i < neighbors.length; i++) {
					short type2 = neighbors[i].getType();
					boolean out2 = neighbors[i].isOut();
					short node3 = neighbors[i].getEnd();
					NaiveSearchPath_ET newsp = new NaiveSearchPath_ET(min, type2, out2, node3);
					insert(newsp);
				}
			}
		}
		if (pathNum > 1000000) limited = true;
		return min;
	}

	public int peek() {
		try {
			return queue.findMin().getLength();
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
		return Integer.MAX_VALUE; // 有点疑问
	}

	@Override
	public int compareTo(NaiveSearchPathPQ_ET o) {
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
	
	public int getPathNum() {
		return pathNum;
	}

}
