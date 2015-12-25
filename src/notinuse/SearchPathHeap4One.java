package notinuse;

import java.util.NoSuchElementException;

import notinuse.SearchPath;
import edu.whu.clock.graphsearch.util.FibonacciHeapPQ;
import edu.whu.clock.graphsearch.util.UnderflowException;
import edu.whu.clock.probsearch.Traverser;

public class SearchPathHeap4One implements Comparable<SearchPathHeap4One> {

	private final int keywordID;
	private final ProbSchemaGraph graph;
	private FibonacciHeapPQ<SearchPath> queue;
	
	private int numOfPath;
	
	public SearchPathHeap4One(int keywordID, ProbSchemaGraph graph) {
		super();
		this.keywordID = keywordID;
		this.graph = graph;
		this.queue = new FibonacciHeapPQ<SearchPath>();
	}
	
	public int getkeywordID() {
		return keywordID;
	}

	public void insert(SearchPath sp) {
		int indexID = queue.enqueue(sp);
		sp.setIndexID(indexID);
		numOfPath++;
	}
	
	public boolean hasNext() {
		return ! queue.isEmpty();
	}
	
	public SearchPath next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		SearchPath min = null;
		try {
			min = queue.dequeueMin();
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
		}
		
		ProbNeighborhood neighbors = graph.getNeighbors(min.getVertex(min.length() - 1)); // 取路径上终端节点的邻居节点集合
		if(neighbors!=null) {
			for (int i = 0; i < neighbors.size(); i++) {
				SearchPath newsp = new SearchPath(min, (short)neighbors.getVertex(i), neighbors.getProbability(i));
				int indexID = queue.enqueue(newsp);
				newsp.setIndexID(indexID);
				numOfPath++;
			}
		}
		
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
	public int compareTo(SearchPathHeap4One o) {
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
	
	public int getNumOfPath() {
		return numOfPath;
	}
	

}
