package edu.whu.clock.graphsearch;

import java.util.NoSuchElementException;

import edu.whu.clock.graphsearch.util.FibonacciHeapPQ;
import edu.whu.clock.graphsearch.util.UnderflowException;

public class VertexIteratorHeap {

	private final int groupID;
	
	private FibonacciHeapPQ<VertexIterator> queue;

	public VertexIteratorHeap(int groupID) {
		this.groupID = groupID;
		
		this.queue = new FibonacciHeapPQ<VertexIterator>();
	}
	
	public void insert(VertexIterator iterator) {
		queue.enqueue(iterator);
	}
	
	public boolean hasNext() {
		return !(queue.isEmpty());
	}
	
	public VertexIterator next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		VertexIterator min = null;
		try {
			min = queue.dequeueMin();
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
		}
		return min;
	}

	public int peek() throws UnderflowException {
		return queue.findMin().peek();
	}
	
	public int getGroupID() {
		return groupID;
	}

}
