package edu.whu.clock.probsearch;

import java.util.NoSuchElementException;

import edu.whu.clock.graphsearch.util.FibonacciHeapPQ;
import edu.whu.clock.graphsearch.util.UnderflowException;

public class Traverser implements Comparable<Traverser> {

	private final int source;
	private final ProbSchemaGraph graph;
	private FibonacciHeapPQ<SearchPathRef> queue;
//	private HashMap<Integer, ArrayList<SearchPathRef>> map;
	
	private int numOfPath;
	
	public Traverser(int source, double prob, ProbSchemaGraph graph) {
		super();
		this.source = source;
		this.graph = graph;
		this.queue = new FibonacciHeapPQ<SearchPathRef>();
//		this.map = new HashMap<Integer, ArrayList<SearchPathRef>>();
		
		SearchPathRef sp = new SearchPathRef(source, prob);
		numOfPath++;
//		ArrayList<SearchPathRef> list = new ArrayList<SearchPathRef>();
//		list.add(sp);
//		map.put(source, list);
		int indexID = queue.enqueue(sp);
		sp.setIndexID(indexID);
	}
	
	public boolean hasNext() {
		return ! queue.isEmpty();
	}
	
	public SearchPathRef next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		SearchPathRef min = null;
		try {
			min = queue.dequeueMin();
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
		}
		
		ProbNeighborhood neighbors = graph.getNeighbors(min.getEnd());
		if(neighbors!=null) {
//			ArrayList<SearchPathRef> list = (ArrayList<SearchPathRef>)map.get(min.getEnd()).clone();
			for (int i = 0; i < neighbors.size(); i++) {
				SearchPathRef newsp = new SearchPathRef(min, neighbors.getVertex(i), neighbors.getProbability(i));
				numOfPath++;
				int indexID = queue.enqueue(newsp);
				newsp.setIndexID(indexID);
//				for (SearchPathRef sp : list) {
//					SearchPathRef newsp = new SearchPathRef(sp, neighbors.getVertex(i), neighbors.getProbability(i));
//					numOfPath++;
//					int indexID = queue.enqueue(newsp);
//					newsp.setIndexID(indexID);
//					if (map.containsKey(neighbors.getVertex(i))) {
//						map.get(neighbors.getVertex(i)).add(newsp);
//					}
//					else {
//						ArrayList<SearchPathRef> newlist = new ArrayList<SearchPathRef>();
//						newlist.add(newsp);
//						map.put(neighbors.getVertex(i), newlist);
//					}
//				}
			}
		}
		
		return min;
	}

	public double peek() {
		try {
			return queue.findMin().getProb();
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
		return Double.MAX_VALUE;
	}
	
	public int getSource() {
		return source;
	}

	@Override
	public int compareTo(Traverser o) {
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
