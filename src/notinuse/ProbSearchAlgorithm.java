package notinuse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import edu.whu.clock.graphsearch.VertexDelegate;
import edu.whu.clock.graphsearch.util.ExpandableIntArray;
import edu.whu.clock.graphsearch.util.UnderflowException;

public class ProbSearchAlgorithm {

	private int keywordNum;
	private int k;
	
	private ProbSchemaGraph graph;

//	private TraverserHeap[] heap;
	private SearchPathHeap4One[] heaps;
	private LinkedList<RawProbAnswerTree> allResults;
	private HashMap<Short, SearchPathHub> records;
	private HashSet<Short> candidates;    //vertices that need to be checked
	private double[] peeks;  // the peek values of each iterator

	// statistics
	public long visitTimes = 0;
	public long stopTimes = 0; 

	public ProbSearchAlgorithm(ProbSchemaGraph graph) {
		this.graph = graph;
	}

	public void init(PKIndexEntry[] entries) {
		keywordNum = entries.length;
		records = new HashMap<Short, SearchPathHub>(); // 为每个潜在根节点保管所有已遍历的通往该节点的searchpath
		peeks = new double[keywordNum];
		for (int i = 0; i < peeks.length; i++) {
			peeks[i] = 1.1d;
		}
		candidates = new HashSet<Short>();
		allResults = new LinkedList<RawProbAnswerTree>();
//		heap = new TraverserHeap[keywordNum];
		try {
			for (int i = 0; i < keywordNum; i++) {
				if (entries[i] == null) {
					System.out.println("Error: The entry of the keyword #" + i + " is null.");
					System.exit(1);
				}
				heaps[i] = new SearchPathHeap4One(i, graph);
				for (int j = 0; j < entries[i].length(); j++) {
//					Traverser traverser = new Traverser(entries[i].getClassID(j), entries[i].getProb(j), graph);
					SearchPath sp = new SearchPath(entries[i].getEdge(j), entries[i].getProb(j));
					heaps[i].insert(sp);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public RawProbAnswerTree[] execute(int k) throws UnderflowException {
		this.k = k;
		int keywordID = -1;                 // the id of current iterator
		
		// begin to search
		while (true) {  // at least one iterator remains
			// choose the next iterator by round-robin scheduling
			keywordID = (keywordID + 1) % keywordNum;
			if (!heaps[keywordID].hasNext()) {  // current iterator is empty
				continue;
			}
			
			// get the current nearest vertex connected to keyword #i
//			Traverser traverser = heap[keywordID].next();
//			SearchPathRef path = traverser.next();
			SearchPath path = heaps[keywordID].next();
//			System.out.println(traverser.getNumOfPath());
			
			// visit this vertex
//			System.out.println(traverser.getSource() + " --> " + path.getEnd() + " : " + path.getProb());
			System.out.print("[" + keywordID + "] ");
			printSearchPath(path);
			visit(keywordID, path);
			
//			if (traverser.hasNext()) {
//				heap[keywordID].insert(traverser);
//			}
			
			if (allResults.size() >= k) {
				stop(keywordID);
			}
			
			// check if algorithm can be stopped
			if (candidates.isEmpty()) {
				System.out.println("Successfully stopped");
				RawProbAnswerTree[] topk = new RawProbAnswerTree[k];
				for (int j = 0; j < k; j++) {
					RawProbAnswerTree sa = allResults.remove(0);
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
	
	private void visit(int keywordID, SearchPath path) throws UnderflowException {
		if (!records.containsKey(path.getVertex(path.length()))) {  // if R has never been visited
			// produce a new record for the vertex
			SearchPathHub hub = new SearchPathHub(keywordNum);
			hub.add(keywordID, path);
			if (allResults.size() >= k) { // R cannot be the root of an answer better than current topk
				hub.disqualify();
			}
			else {
				candidates.add(path.getVertex(path.length()));
			}
			records.put(path.getVertex(path.length()), hub);
		}
		else {  // if R has been visited before
			SearchPathHub hub = records.get(path.getVertex(path.length()));
			hub.add(keywordID, path);
			
			if (hub.isCandidate()) {  // if R is a candidate
				// check if the tree rooted at R is complete
				if (hub.isComplete()) {
					crossProduct(hub, keywordID, path);
				}
			}
		}
		visitTimes++;
	}
	
	private void crossProduct(SearchPathHub record, int keywordID, SearchPath path) {
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
			SearchPath[] sources = new SearchPath[keywordNum];
			double score = 1.0d;
			for (int j = 0; j < keywordNum; j++) {
				if (j == keywordID) {
					sources[j] = path;
					score *= path.getProbability();
				}
				else {
					int pos = (i / temp[j]) % record.get(j).size();
					sources[j] = record.get(j).get(pos);
					score *= sources[j].getProbability();
				}
			}
			if (allResults.size() >= k && score < allResults.get(k-1).getScore()) {
//				System.out.println("directly abandon root(" + R + ") score: " + score);
				continue;
			}
			else {
				RawProbAnswerTree at = new RawProbAnswerTree(path.getVertex(path.length() - 1), sources, score);
				addAnswer(at);
			}
		}
	}
	
	private void stop(int keywordID) throws UnderflowException {
		if (!heaps[keywordID].hasNext() || heaps[keywordID].peek() < peeks[keywordID]) {
			Iterator<Short> vIt = candidates.iterator();
			while (vIt.hasNext()) {
				short candidate = vIt.next();
				SearchPathHub hub = records.get(candidate);
				double upperBound = 0.0d;
				if (hub.isComplete()) {
					for (int j = 0; j< keywordNum; j++) {
						if (upperBound < upperBound(hub, j)) {
							upperBound = upperBound(hub, j);
						}
					}
				}
				else {
					upperBound = upperBound(hub);
				}
				if (upperBound <= allResults.get(k - 1).getScore()) {
					hub.disqualify();
					vIt.remove();
					System.out.println("Stopped: " + candidate + ", upperBound: " + upperBound);
				}
				stopTimes++;
			}
			peeks[keywordID] = heaps[keywordID].hasNext() ? heaps[keywordID].peek() : 0.0d;
		}
	}

	private double upperBound(SearchPathHub record) throws UnderflowException {
		double bound = 1.0d;
		for (int j = 0; j < keywordNum; j++) {
			if (record.get(j).isEmpty()) {
				if (heaps[j].hasNext()) {
					bound *= heaps[j].peek();
				}
				else {
					return 0.0d;
				}
			}
			else {
				bound *= record.getMaxProb(j);
			}
		}
		return bound;
	}

	private double upperBound(SearchPathHub hub, int index) throws UnderflowException {
		double bound = 1.0d;
		for (int j = 0; j < keywordNum; j++) {
			if (j == index) {
				if (heaps[j].hasNext()) {
					bound *= heaps[j].peek();
				}
				else {
					return 0.0d;
				}
			}
			else {
				bound *= hub.getMaxProb(j);
			}
		}
		return bound;
	}
	
//	private AnswerTree genAnswer(int root, VertexDelegate[] sources, int score) {
//		int[][] paths = new int[keywordNum][];
//		
//		for (int i = 0; i < keywordNum; i++) {
//			paths[i] = getPath(sources[i]);
//		}
//		
//		int[] leaves = new int[sources.length];
//		for (int i = 0; i < leaves.length; i++) {
//			leaves[i] = sources[i].getSource();
//		}
//		
//		return new AnswerTree(root, leaves, paths, score);
//	}

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
	
	private void addAnswer(RawProbAnswerTree answer) {
		ListIterator<RawProbAnswerTree> it = allResults.listIterator();
		int pos = 0;
		while (it.hasNext()) {
			RawProbAnswerTree other = it.next();
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
				if (other.getScore() >= answer.getScore()) {
					pos++;
				}
//			}
		}
			
		allResults.add(pos, answer);
//		System.out.println((line++) + ": " + "add " + answer);
	}
	
//	private void printSearchPath(SearchPathRef path) {
//		String str = path.getEnd() + " : " + path.getProb();
//		while (path.getPrev() != null) {
//			path = path.getPrev();
//			str = path.getEnd() + " --> " + str;
//		}
//		System.out.println(str);
//	}
	
	private void printSearchPath(SearchPath path) {
		String str = "";
		for (short i = 0; i < path.length(); i++) {
			str += path.getVertex(i) + "-->";
		}
		str += ": " + path.getProbability();
		System.out.println(str);
	}

}
