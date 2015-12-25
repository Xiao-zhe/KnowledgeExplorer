package edu.whu.clock.graphsearch.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import edu.whu.clock.graphsearch.GraphInMemory;

public class SearchAlgorithmOld {

	private int keywordNum;
	private GraphInMemory graph;
	private int[][] matchedVertices;

	private IteratorHeap[] heap;
	private int k;
		
	private LinkedList<SimpleAnswer> allResults;
	private HashMap<Integer, SearchRecord> records;
	private HashSet<Integer> candidates;    //vertices that need to be checked
	private double[] peeks;  // the peek values of each iterator

	// statistics
	public int[] initVertexNums;
	public long visitTimes = 0;
	public long line = 1;
	public long stopTimes = 0;

	public SearchAlgorithmOld(GraphInMemory graph) {
		this.graph = graph;
	}

	public void init(int[][] matchedVertices) {
		this.matchedVertices = matchedVertices;
		keywordNum = matchedVertices.length;
		records = new HashMap<Integer, SearchRecord>();
		peeks = new double[keywordNum];
		candidates = new HashSet<Integer>();
		allResults = new LinkedList<SimpleAnswer>();
		heap = new IteratorHeap[keywordNum];
		try {
			for (int i = 0; i < keywordNum; i++) {
				heap[i] = new IteratorHeap(i);
				for (int j : matchedVertices[i]) {
					SPIterator4Vertex iterator = new SPIterator4Vertex(graph, j);
					heap[i].insert(iterator);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public SimpleAnswer[] execute(int k) throws UnderflowException  {
		this.k = k;
		byte stopped = 0;  // how many heap have been stopped
		int i = -1;                 // the id of current iterator
		
		// begin to search
		while (stopped < keywordNum) {  // at least one iterator remains
			// choose the next iterator by round-robin scheduling
			i = (i + 1) % (keywordNum - stopped);
			if (!heap[i].hasNext()) {  // current iterator is empty
//				for (int j = i; j < (keywordNum - stopped - 1); j++) {
//					heap[i] = heap[j + 1];
//				}
				stopped++;
				continue;
			}
			
			// get the current nearest vertex connected to keyword #i
			SPIterator4Vertex ni = heap[i].next();
			int source = ni.getVID();
			SPVertex spv = ni.next();
			
			// get the information of this vertex
			int root = spv.getID();
			Integer R = new Integer(root);
			int distance = spv.getDistance();
			int predecessor = spv.getPredecessor();
			
			// visit this vertex
			visit(i, R, distance, predecessor, source);
			
			if (ni.hasNext()) {
				heap[i].insert(ni);
			}
			
			if (allResults.size() >= k) {
//				stop(sr, R);
				stop(i);
			}
			
			// check if algorithm can be stopped
			if (candidates.isEmpty()) {
				System.out.println("Successfully stopped");
				SimpleAnswer[] topk = new SimpleAnswer[k];
				for (int j = 0; j < k; j++) {
					SimpleAnswer sa = allResults.remove(0);
//					knownTopK.add(sa);
					topk[j] = sa;
				}
				return topk;
			}
		}
		System.out.println("All processed");
		SimpleAnswer[] topk = new SimpleAnswer[k];
		int num = Math.min(allResults.size(), k);
		for (int j = 0; j < num; j++) {
//			knownTopK.add(sa);
			topk[j] = allResults.get(j);
		}
		return topk;
	}
	
	private void visit(int i, Integer R, double distance, int predecessor, int source) throws UnderflowException {

//		if (i == 0 && R.intValue() == 2 && source ==6) {
//			System.out.println();
//		}
		
//		System.out.println((line++) + ": " + i + ", " + R + ", " + distance + ", " + predecessor + ", " + source);
		if (!records.containsKey(R)) {  // if R has never been visited
			// produce a new record for R
			SearchRecord sr = new SearchRecord(keywordNum);
			sr.setDistance(i, source, distance);
			sr.setPredecessor(source, predecessor);	
			if (allResults.size() >= k) { // R cannot be the root of an answer better than current topk
				sr.setStatus(false);
			}
			else {
				candidates.add(R);
			}
			records.put(R, sr);
		}
		else {  // if R has been visited before
			SearchRecord sr = records.get(R);
			sr.setDistance(i, source, distance);
			sr.setPredecessor(source, predecessor);	
			
			if (sr.isCandidate()) {  // if R is a candidate
				// check if the tree rooted at R is complete
				if (sr.isComplete()) {
					crossProduct(sr, i, R, distance, source);
				}
			}
		}
		visitTimes++;
	}
	
	private void crossProduct(SearchRecord sr, int i, Integer R, double distance, int source) {
		int[] nums = new int[keywordNum];
		for (int j = 0; j < keywordNum; j++) {
			nums[j] = 1;
		}
		for (int j = keywordNum - 1; j >= 0; j--) {
			if (j == i) {
				continue;
			}
			for (int k = 0; k < j; k++) {
				if (k == i) {
					continue;
				}
				nums[k] *= sr.getDistances(j).size();
			}
		}
		int num = 0;
		if (i == 0) {
			num = nums[1] * sr.getDistances(1).size();
		}
		else {
			num = nums[0] * sr.getDistances(0).size();
		}
		
		for (int j = 0; j < num; j++) {
			int[] sources = new int[keywordNum];
			double score = 0.0d;
			for (int k = 0; k < keywordNum; k++) {
				if (k == i) {
					sources[k] = source;
					score += distance;
					continue;
				}
				int pos = (j / nums[k]) % sr.getDistances(k).size();
				SourceAndDistance sad = sr.getDistances(k).get(pos);
				sources[k] = sad.getSource();
				score += sad.getDistance();
			}
			if (allResults.size() >= k && score >= allResults.get(k-1).getScore()) {
//				System.out.println("directly abandon root(" + R + ") score: " + score);
				continue;
			}
			else {
				SimpleAnswer at = genAnswer(R.intValue(), sources, score);
				addAnswer(at);
			}
		}
	}
	
//	private void stop(SearchRecord sr, Integer R) throws UnderflowException {
//		if (sr.isCandidate()) {
//			if (sr.isComplete()) {
//				for (int j = 0; j< keywordNum; j++) {
//					double lb = lowestBound(sr, j);
//					if (lb < allResults.get(k - 1).getScore()) {
//						return;
//					}
//				}
//				sr.setStatus(false);
//				candidates.remove(R);
//				System.out.println((line++) + ": " + R + " stopped");
//			}
//			else {
//				double lb = lowestBound(sr);
//				if (lb >= allResults.get(k - 1).getScore()) {
//					sr.setStatus(false);
//					candidates.remove(R);
//					System.out.println((line++) + ": " + R + " stopped, lb: " + lb);
//				}
//			}
//		}
//	}
	
	private void stop(int i) throws UnderflowException {
		if (allResults.size() >= k) {
			if (!heap[i].hasNext() || heap[i].peek() > peeks[i]) {
				Iterator<Integer> vIt = candidates.iterator();
				while (vIt.hasNext()) {
					Integer V = vIt.next();
//					if (V.intValue() == 3) {
//						System.out.println();
//					}
					SearchRecord sr = records.get(V);
					double lb = Double.MAX_VALUE;
					if (sr.isComplete()) {
						for (int j = 0; j< keywordNum; j++) {
							if (lb > lowestBound(sr, j)) {
								lb = lowestBound(sr, j);
							}
						}
					}
					else {
						lb = lowestBound(sr);
					}
					if (lb >= allResults.get(k - 1).getScore()) { // > or >=
						sr.setStatus(false);
						vIt.remove();
//						System.out.println((line++) + ": " + V + " stopped, lb: " + lb);
					}
					stopTimes++;
				}
				peeks[i] = heap[i].hasNext() ? heap[i].peek() : Double.MAX_VALUE;
			}
		}
	}

	private double lowestBound(SearchRecord record) throws UnderflowException {
		double lbound = 0.0d;
		for (int j = 0; j < keywordNum; j++) {
			if (record.getDistances(j).isEmpty()) {
				if (heap[j].hasNext()) {
					lbound += heap[j].peek();
				}
				else {
					return Double.MAX_VALUE;
				}
			}
			else {
				lbound += record.getShortestDistance(j);
			}
		}
		return lbound;
	}

	private double lowestBound(SearchRecord record, int index) throws UnderflowException {
		double lbound = 0.0d;
		for (int j = 0; j < keywordNum; j++) {
			if (j == index) {
				if (heap[j].hasNext()) {
					lbound += heap[j].peek();
				}
				else {
					return Double.MAX_VALUE;
				}
			}
			else {
				lbound += record.getShortestDistance(j);
			}
		}
		return lbound;
	}
	
	private SimpleAnswer genAnswer(int root, int[] sources, double score) {
		int[][] paths = new int[keywordNum][];
		
		for (int i = 0; i < keywordNum; i++) {
			paths[i] = getPath(root, sources[i]);
		}
		
		return new SimpleAnswer(root, sources, paths, score);
	}

	private int[] getPath(int root, int source) {
		ExpandableIntArray eia = new ExpandableIntArray();
		int current = root;
		
		do {
			current = records.get(current).getPredecessor(source);
			if (current == 0) {
				System.err.println("Error: predecessor not found");
				System.exit(1);
			}
			else if (current != -1) {
				eia.add(current);
			}
			else {
				break;
			}
		} while (true);
		
		return eia.toArray();
	}
	
	private void addAnswer(SimpleAnswer answer) {
		ListIterator<SimpleAnswer> it = allResults.listIterator();
		int pos = 0;
		while (it.hasNext()) {
			SimpleAnswer other = it.next();
			int x = answer.compare(other);
			if (x == MyMath.ARR1_CONTAIN_ARR2) {
//				System.out.println((line++) + ": " + "abandon " + answer);
				return;
			}
			else if (x == MyMath.ARR2_CONTAIN_ARR1) {
				it.remove();
//				System.out.println((line++) + ": " + "remove " + other);
			}
			else if (x == MyMath.ARR1_EQUAL_ARR2) {
				if (answer.getScore() < other.getScore()) {
					it.remove();
//					System.out.println((line++) + ": " + "remove " + other);
					allResults.add(pos, answer);
//					System.out.println((line++) + ": " + "add " + answer);
				}
				return;
			}
			else {
				if (other.getScore() < answer.getScore()) {
					pos++;
				}
			}
		}
			
		allResults.add(pos, answer);
//		System.out.println((line++) + ": " + "add " + answer);
	}

}
