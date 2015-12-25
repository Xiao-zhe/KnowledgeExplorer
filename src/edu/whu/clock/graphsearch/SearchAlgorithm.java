package edu.whu.clock.graphsearch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import edu.whu.clock.graphsearch.util.ExpandableIntArray;
import edu.whu.clock.graphsearch.util.MyMath;
import edu.whu.clock.graphsearch.util.UnderflowException;

public class SearchAlgorithm {

	private int keywordNum;
	private int k;
	
	private GraphInMemory graph;

	private VertexIteratorHeap[] heap;		
	private LinkedList<AnswerTree> allResults;
	private HashMap<Integer, VertexVisitRecord> records;
	private HashSet<Integer> candidates;    //vertices that need to be checked
	private double[] peeks;  // the peek values of each iterator

	// statistics
	public long visitTimes = 0;
	public long stopTimes = 0;

	public SearchAlgorithm(GraphInMemory graph) {
		this.graph = graph;
	}

	public void init(int[][] matchedVertices) {
		keywordNum = matchedVertices.length;
		records = new HashMap<Integer, VertexVisitRecord>();
		peeks = new double[keywordNum];
		candidates = new HashSet<Integer>();
		allResults = new LinkedList<AnswerTree>();
		heap = new VertexIteratorHeap[keywordNum];
		try {
			for (int i = 0; i < keywordNum; i++) {
				heap[i] = new VertexIteratorHeap(i);
				for (int j : matchedVertices[i]) {
					VertexIterator iterator = new VertexIterator(graph, j);
					heap[i].insert(iterator);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public AnswerTree[] execute(int k) throws UnderflowException {
		this.k = k;
		int keywordID = -1;                 // the id of current iterator
		
		// begin to search
		while (true) {  // at least one iterator remains
			// choose the next iterator by round-robin scheduling
			keywordID = (keywordID + 1) % keywordNum;
			if (!heap[keywordID].hasNext()) {  // current iterator is empty
				continue;
			}
			
			// get the current nearest vertex connected to keyword #i
			VertexIterator iterator = heap[keywordID].next();
			VertexDelegate delegate = iterator.next();
			
			// visit this vertex
			visit(keywordID, delegate);
			
			if (iterator.hasNext()) {
				heap[keywordID].insert(iterator);
			}
			
			if (allResults.size() >= k) {
				stop(keywordID);
			}
			
			// check if algorithm can be stopped
			if (candidates.isEmpty()) {
				System.out.println("Successfully stopped");
				AnswerTree[] topk = new AnswerTree[k];
				for (int j = 0; j < k; j++) {
					AnswerTree sa = allResults.remove(0);
//					knownTopK.add(sa);
					topk[j] = sa;
				}
				return topk;
			}
		}
//		System.out.println("All processed");
//		SimpleAnswer[] topk = new SimpleAnswer[k];
//		int num = Math.min(allResults.size(), k);
//		for (int j = 0; j < num; j++) {
////			knownTopK.add(sa);
//			topk[j] = allResults.get(j);
//		}
//		return topk;
	}
	
	private void visit(int i, VertexDelegate delegate) throws UnderflowException {
//		if (delegate.getID() == 144)
//			System.out.println();
		
		if (!records.containsKey(delegate.getID())) {  // if R has never been visited
			// produce a new record for R
			VertexVisitRecord sr = new VertexVisitRecord(keywordNum);
			sr.add(i, delegate);
			if (allResults.size() >= k) { // R cannot be the root of an answer better than current topk
				sr.disqualify();
			}
			else {
				candidates.add(delegate.getID());
			}
			records.put(delegate.getID(), sr);
		}
		else {  // if R has been visited before
			VertexVisitRecord sr = records.get(delegate.getID());
			sr.add(i, delegate);
			
			if (sr.isCandidate()) {  // if R is a candidate
				// check if the tree rooted at R is complete
				if (sr.isComplete()) {
					crossProduct(sr, i, delegate);
				}
			}
		}
		visitTimes++;
	}
	
	private void crossProduct(VertexVisitRecord record, int keywordID, VertexDelegate delegate) {
		int[] temp = new int[keywordNum];
		for (int i = 0; i < keywordNum; i++) {
			temp[i] = 1;
		}
		int numOfCombinations = 1;
		for (int i = keywordNum - 1; i >= 0; i--) {
			if (i != keywordID) {
			numOfCombinations *= record.get(i).size();
				for (int j = 0; j < i; j++) {
					if (j!= keywordID) {
						temp[j] *= record.get(i).size();
					}
				}
			}
		}
//		int num = 0;
//		if (keywordID == 0) {
//			num = nums[1] * sr.get(1).size();
//		}
//		else {
//			num = nums[0] * sr.get(0).size();
//		}
		
		for (int i = 0; i < numOfCombinations; i++) {
			VertexDelegate[] sources = new VertexDelegate[keywordNum];
			int score = 0;
			for (int j = 0; j < keywordNum; j++) {
				if (j == keywordID) {
					sources[j] = delegate;
					score += delegate.getDistance();
				}
				else {
					int pos = (i / temp[j]) % record.get(j).size();
					sources[j] = record.get(j).get(pos);
					score += sources[j].getDistance();
				}
			}
			if (allResults.size() >= k && score >= allResults.get(k-1).getScore()) {
//				System.out.println("directly abandon root(" + R + ") score: " + score);
				continue;
			}
			else {
				AnswerTree at = genAnswer(delegate.getID(), sources, score);
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
		if (!heap[i].hasNext() || heap[i].peek() > peeks[i]) {
			Iterator<Integer> vIt = candidates.iterator();
			while (vIt.hasNext()) {
				Integer V = vIt.next();
//				if (V.intValue() == 3) {
//					System.out.println();
//				}
				VertexVisitRecord sr = records.get(V);
				int lb = Integer.MAX_VALUE;
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
					sr.disqualify();
					vIt.remove();
//					System.out.println((line++) + ": " + V + " stopped, lb: " + lb);
				}
				stopTimes++;
			}
			peeks[i] = heap[i].hasNext() ? heap[i].peek() : Double.MAX_VALUE;
		}
	}

	private int lowestBound(VertexVisitRecord record) throws UnderflowException {
		int lbound = 0;
		for (int j = 0; j < keywordNum; j++) {
			if (record.get(j).isEmpty()) {
				if (heap[j].hasNext()) {
					lbound += heap[j].peek();
				}
				else {
					return Integer.MAX_VALUE;
				}
			}
			else {
				lbound += record.getShortestDistance(j);
			}
		}
		return lbound;
	}

	private int lowestBound(VertexVisitRecord record, int index) throws UnderflowException {
		int lbound = 0;
		for (int j = 0; j < keywordNum; j++) {
			if (j == index) {
				if (heap[j].hasNext()) {
					lbound += heap[j].peek();
				}
				else {
					return Integer.MAX_VALUE;
				}
			}
			else {
				lbound += record.getShortestDistance(j);
			}
		}
		return lbound;
	}
	
	private AnswerTree genAnswer(int root, VertexDelegate[] sources, int score) {
		int[][] paths = new int[keywordNum][];
		
		for (int i = 0; i < keywordNum; i++) {
			paths[i] = getPath(sources[i]);
		}
		
		int[] leaves = new int[sources.length];
		for (int i = 0; i < leaves.length; i++) {
			leaves[i] = sources[i].getSource();
		}
		
		return new AnswerTree(root, leaves, paths, score);
	}

	private int[] getPath(VertexDelegate source) {
		ExpandableIntArray eia = new ExpandableIntArray();
		VertexDelegate current = source;
		
		do {
			current = current.getPredecessor();
			if (current == null) {
				break;
			}
			else {
				eia.add(current.getID());
			}
		} while (true);
		
		return eia.toArray();
	}
	
	private void addAnswer(AnswerTree answer) {
		ListIterator<AnswerTree> it = allResults.listIterator();
		int pos = 0;
		while (it.hasNext()) {
			AnswerTree other = it.next();
//			int x = answer.compare(other);
//			if (x == MyMath.ARR1_CONTAIN_ARR2) {
////				System.out.println((line++) + ": " + "abandon " + answer);
//				return;
//			}
//			else if (x == MyMath.ARR2_CONTAIN_ARR1) {
//				it.remove();
////				System.out.println((line++) + ": " + "remove " + other);
//			}
//			else if (x == MyMath.ARR1_EQUAL_ARR2) {
//				if (answer.getScore() < other.getScore()) {
//					it.remove();
////					System.out.println((line++) + ": " + "remove " + other);
//					allResults.add(pos, answer);
////					System.out.println((line++) + ": " + "add " + answer);
//				}
//				return;
//			}
//			else {
				if (other.getScore() < answer.getScore()) {
					pos++;
				}
//			}
		}
			
		allResults.add(pos, answer);
//		System.out.println((line++) + ": " + "add " + answer);
	}

}
