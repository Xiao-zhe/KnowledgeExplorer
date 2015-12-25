package edu.whu.clock.graphsearch.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;


public class IteratorHeap {

	private final int groupID;
	
	private HashMap<Integer, SPIterator4Vertex> set;
	private HashSet<Integer> hasOutput;
	private FibonacciHeapPQ<SPIterator4Vertex> queue;

	public IteratorHeap(int groupID) {
		this.groupID = groupID;
		
		this.set = new HashMap<Integer, SPIterator4Vertex>();
		this.hasOutput = new HashSet<Integer>();
		this.queue = new FibonacciHeapPQ<SPIterator4Vertex>();
	}
	
	public void insert(SPIterator4Vertex ni) {
		queue.enqueue(ni);
	}
	
	public boolean hasNext() {
		return !(queue.isEmpty());
	}
	
	public SPIterator4Vertex next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		SPIterator4Vertex min = null;
		try {
			min = queue.dequeueMin();
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
		}
		return min;
	}

	public double peek() throws UnderflowException {
		return queue.findMin().peek();
	}
	
	public int getGroupID() {
		return groupID;
	}

}
