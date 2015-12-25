package edu.whu.clock.graphsearch.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

import edu.whu.clock.graphsearch.GraphInMemory;

public class SPIterator4Vertex implements Comparable<SPIterator4Vertex> {

	private final GraphInMemory graph;
	private final int vid;
	
	private HashMap<Integer, SPVertex> set;
	private HashSet<Integer> hasOutput;
	private FibonacciHeapPQ<SPVertex> queue;

	public SPIterator4Vertex(GraphInMemory graph, int vid) {
		this.graph = graph;
		this.vid = vid;
		
		this.set = new HashMap<Integer, SPVertex>();
		this.hasOutput = new HashSet<Integer>();
		this.queue = new FibonacciHeapPQ<SPVertex>();
		
		SPVertex spv = new SPVertex(vid, 0);
		spv.setPredecessor(-1);
		int indexID = queue.enqueue(spv);
		spv.setIndexID(indexID);
		set.put(vid, spv);
	}
	
	public boolean hasNext() {
		return ! queue.isEmpty();
	}
	
	public SPVertex next() {
		if (queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		SPVertex min = null;
		try {
			min = queue.dequeueMin();
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
		}
		if (hasOutput.contains(min.getID())) {
			System.out.println("Error: found exsiting shortest path");
		}
		set.remove(min.getID());
		hasOutput.add(min.getID());
		
		int[] neighbors = graph.getNeighbors(min.getID());
		if(neighbors!=null) {
			for (int i = 0; i < neighbors.length; i++) {
				
				if (hasOutput.contains(neighbors[i])) {
					continue;
				}
				SPVertex spv = set.get(neighbors[i]);
				if (spv == null) {
					spv = new SPVertex(neighbors[i], min.getDistance()+1);
					spv.setPredecessor(min.getID());
					int indexID = queue.enqueue(spv);
					spv.setIndexID(indexID);
					set.put(neighbors[i], spv);
				}
				else {
					int dis = min.getDistance() + 1;
					if (dis < spv.getDistance()) {
						spv.setDistance(dis);
						spv.setPredecessor(min.getID());
						relax(spv);
					}
				}
			}
		}
		
		return min;
	}
	
	private void relax(SPVertex spv) {
		try {
			queue.decreaseKey(spv.getIndexID(), spv);
		}
		catch (UnderflowException ex) {
			ex.printStackTrace();
		}
	}

	public double peek() {
		try {
			return queue.findMin().getDistance();
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
		return Double.MAX_VALUE;
	}
	
	public int getVID() {
		return vid;
	}

	@Override
	public int compareTo(SPIterator4Vertex other) {
		if (peek() > other.peek()) {
			return 1;
		}
		else if (peek() < other.peek()) {
			return -1;
		}
		else {
			return 0;
		}
	}

}
