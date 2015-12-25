package edu.whu.clock.probsearch;

import java.util.NoSuchElementException;

import edu.whu.clock.graphsearch.util.FibonacciHeapPQ;
import edu.whu.clock.graphsearch.util.UnderflowException;

public class TraverserHeap {

	private final int keywordID;
	
	private FibonacciHeapPQ<Traverser> queue;

	public TraverserHeap(int keywordID) {
		this.keywordID = keywordID;
		
		this.queue = new FibonacciHeapPQ<Traverser>();
	}
	
	public void insert(Traverser iterator) {
		queue.enqueue(iterator);
	}
	
	public boolean hasNext() {
		return !(queue.isEmpty());
	}
	
	public Traverser next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		Traverser min = null;
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
	
	public int getkeywordID() {
		return keywordID;
	}

}
