package edu.whu.clock.newprobsearch;

import java.util.NoSuchElementException;

import edu.whu.clock.graphsearch.util.FibonacciHeapPQ;
import edu.whu.clock.graphsearch.util.UnderflowException;
import edu.whu.clock.newgraph.SummaryGraphEdgeTyped;
import edu.whu.clock.newgraph.SummaryGraphTyped;
import edu.whu.clock.newprobindex.CPTableTypedManager_EdgeCount;

public class SearchPathTypedPQ implements Comparable<SearchPathTypedPQ> {

	private final int keywordID;
	private final SummaryGraphTyped graph;
	private final CPTableTypedManager_EdgeCount cpTable;
	private FibonacciHeapPQ<SearchPathTyped> queue;
	
	private int pathNum = 0;
	private boolean limited = false;
	
	public SearchPathTypedPQ(int keywordID, SummaryGraphTyped graph, CPTableTypedManager_EdgeCount cpTable) {
		super();
		this.keywordID = keywordID;
		this.graph = graph;
		this.cpTable = cpTable;
		this.queue = new FibonacciHeapPQ<SearchPathTyped>();
	}
	
	public int getKeywordID() {
		return keywordID;
	}

	public void insert(SearchPathTyped sp) {
		int indexID = queue.enqueue(sp);
		sp.setIndexID(indexID);
		pathNum++;
	}
	
	public boolean hasNext() {
		return ! queue.isEmpty();
	}
	
	public SearchPathTyped next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		SearchPathTyped min = null;
		try {
			min = queue.dequeueMin();
			pathNum--;
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
			return null;
		}
//		if (min.nodeNum()==3 && min.getNode(0)==173 && min.getNode(1)==56 && min.getNode(2)==69){
//			System.out.println();
//		}
		if (min.nodeNum() < 5 && !limited) { // 如果路径长度达到5，则不再扩展该路径。
			short node1 = min.getNode(min.nodeNum() - 2);
			short type1 = min.getType(min.nodeNum() - 2);
			boolean out1 = min.isOut(min.nodeNum() - 2);
			short node2 = min.getNode(min.nodeNum() - 1);
			SummaryGraphEdgeTyped[] neighbors = graph.getNeighbors(node2); // 取路径上终端节点的邻居节点集合
			if(neighbors!=null) {
				for (int i = 0; i < neighbors.length; i++) {
					short type2 = neighbors[i].getType();
					boolean out2 = neighbors[i].isOut();
					short node3 = neighbors[i].getEnd();
					double prob = cpTable.getProbability(node1, type1, out1, node2, type2, out2, node3);
					if (prob == 0.0d) {
						continue;
					}
					SearchPathTyped newsp = new SearchPathTyped(min, type2, out2, node3, prob);
					insert(newsp);
				}
			}
		}
		if (pathNum > 1000000) limited = true;
		return min;
	}

	public double peek() {
		try {
			return queue.findMin().getProbability();
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
		return Double.MAX_VALUE;
	}

	@Override
	public int compareTo(SearchPathTypedPQ o) {
		if (peek() < o.peek()) {
			return 1;
		}
		else if (peek() > o.peek()) {
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
