package edu.whu.clock.newprobsearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import edu.whu.clock.generalsearch.UnfoldedPatternTree_ET;
import edu.whu.clock.graphsearch.util.UnderflowException;
import edu.whu.clock.newgraph.SummaryGraphTyped;
import edu.whu.clock.newprobindex.CPTableTypedManager;
import edu.whu.clock.newprobindex.FNSetManager;
import edu.whu.clock.newprobindex.IndexedEdgeTyped;
import edu.whu.clock.newprobindex.PKIndexManager;
import edu.whu.clock.newprobindex.PKIndexTypedEntry;

public class JointProbTopkSearchAlgorithm_SG_ET {

	public final String logDir;
	
	private final SummaryGraphTyped graph;
	private final PKIndexManager pkIndex;
	private final CPTableTypedManager cpTable;
	private final FNSetManager fnSet;
	private BufferedWriter logWriter;

	private int keywordNum;
	private int k;
	private SearchPathTypedPQ[] heaps;
	private LinkedList<UnfoldedPatternTree_ET> allResults;
	private SearchPathTypedHub[] records;
	private HashSet<Short> candidates;    //vertices that need to be checked
//	private double[] peeks;  // the peek values of each iterator

	// statistics
	public long visitTimes = 0;
	public long stopTimes = 0;
	
	public JointProbTopkSearchAlgorithm_SG_ET(SummaryGraphTyped graph, PKIndexManager pkIndex, CPTableTypedManager cpTable, FNSetManager fnSet, String logDir) throws IOException {
		this.graph = graph;
		this.pkIndex = pkIndex;
		this.cpTable = cpTable;
		this.fnSet = fnSet;
		this.logDir = logDir;
		// 如果需要日志就初始化logWriter
		logWriter = new BufferedWriter(new FileWriter(logDir + "SG joint prob topk search.txt"));
	}
	
	public JointProbTopkSearchAlgorithm_SG_ET(SummaryGraphTyped graph, PKIndexManager pkIndex, CPTableTypedManager cpTable, FNSetManager fnSet) {
		this.graph = graph;
		this.pkIndex = pkIndex;
		this.cpTable = cpTable;
		this.fnSet = fnSet;
		this.logDir = null;
	}
	
	public UnfoldedPatternTree_ET[] run(String[] keywords, int k, boolean log) throws IOException {
		if (keywords == null || keywords.length < 2 || k < 1) {
			System.out.println("Error: invalid inputs.");
			return null;
		}
		try {
			PKIndexTypedEntry[] entries = new PKIndexTypedEntry[keywords.length];
			for (int i = 0; i < keywords.length; i++) {
				entries[i] = pkIndex.get(keywords[i]);
			}
			
			init(entries);
			return execute(k);
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void init(PKIndexTypedEntry[] entries) throws IOException {
		keywordNum = entries.length;
		heaps = new SearchPathTypedPQ[keywordNum];
		records = new SearchPathTypedHub[graph.classManager.getClassNum()]; //为每个节点保管所有已知到达该节点的searchpath
//		peeks = new double[keywordNum];
//		for (int i = 0; i < peeks.length; i++) {
//			peeks[i] = 1.1d;
//		}
		candidates = new HashSet<Short>();
		allResults = new LinkedList<UnfoldedPatternTree_ET>();
		for (int i = 0; i < keywordNum; i++) {
			PKIndexTypedEntry entry = entries[i];
			if (entry == null) {
				System.out.println("Error: The entry of the keyword #" + i + " is null.");
				System.exit(1);
			}
//			if (logWriter != null) {
//				logWriter.write("The initial search paths of the keyword #" + i + ": ");
//				logWriter.newLine();
//			}
			heaps[i] = new SearchPathTypedPQ(i, graph, cpTable);
			for (IndexedEdgeTyped edge : entry.getEdgeList()) {
				SearchPathTyped sp = new SearchPathTyped(edge);
				heaps[i].insert(sp);
//				if (logWriter != null) {
//					logWriter.write(sp.getString(graph));
//					logWriter.newLine();
//					logWriter.write("*****************************************************");
//					logWriter.newLine();
//				}
			}
		}
		for (short nodeID = 0; nodeID < graph.getNodeNum(); nodeID++)
			candidates.add(nodeID);
	}

	public UnfoldedPatternTree_ET[] execute(int k) throws UnderflowException, IOException {
		this.k = k;
		int keywordID = -1;                 // the id of current iterator
		boolean[] isEmpty = new boolean[keywordNum];
		
		// begin to search
		while (isNotAllHeapEmpty(isEmpty)) {  // at least one heap has paths
			// choose the next heap by round-robin scheduling
			keywordID = (keywordID + 1) % keywordNum;
			if (!heaps[keywordID].hasNext()) {  // current iterator is empty    循环不出
				isEmpty[keywordID] = true;
				continue;
			}
			
			// get the current most possible search path connected to keyword #i
			SearchPathTyped path = heaps[keywordID].next();
			
			// visit this vertex
//			if (logWriter != null) {
//				logWriter.write("[" + keywordID + "] ");
//				logWriter.write(path.getString(graph));
//				logWriter.newLine();
//			}
			visit(keywordID, path);
			
			if (allResults.size() >= k) {
				stop();
//				peeks[keywordID] = heaps[keywordID].peek();
			}
			
			// check if algorithm can be stopped
			if (candidates.isEmpty()) {
				System.out.println("Successfully stopped");
				UnfoldedPatternTree_ET[] topk = new UnfoldedPatternTree_ET[k];
				for (int j = 0; j < k; j++) {
					topk[j] = allResults.remove(0);
					if (logWriter != null) {
						logWriter.write(topk[j].getString(graph));
						logWriter.newLine();
					}
				}
				logWriter.write("*****************************************");
				logWriter.newLine();
				return topk;
			}
		}
		System.out.println("All heaps are empty now.");
		UnfoldedPatternTree_ET[] topk = new UnfoldedPatternTree_ET[k];
		int num = Math.min(allResults.size(), k);
		for (int j = 0; j < num; j++) {
			topk[j] = allResults.remove(0);
			if (logWriter != null) {
				logWriter.write(topk[j].getString(graph));
				logWriter.newLine();
			}
		}
		logWriter.write("*****************************************");
		logWriter.newLine();
		return topk;
	}
	
	private boolean isNotAllHeapEmpty(boolean[] isEmpty) {
		for (boolean temp : isEmpty) {
			if (!temp) return true;
		}
		return false;
	}
	
	private void visit(int keywordID, SearchPathTyped path) throws UnderflowException, IOException {
		short root = path.getNode(path.nodeNum() - 1);
		if (records[root] == null) {
			// produce a new record for the vertex
			SearchPathTypedHub hub = new SearchPathTypedHub(keywordNum);
			hub.add(keywordID, path);
//			if (allResults.size() >= k) { // R cannot be the root of an answer better than current topk
//				hub.disqualify();
//			}
//			else {
//				candidates.add(root);
//			}
			records[root] = hub;
		}
		else {  // if R has been visited before
			SearchPathTypedHub hub = records[root];
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
	
	private void crossProduct(SearchPathTypedHub record, int keywordID, SearchPathTyped path) throws IOException {
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
		
		for (int i = 0; i < numOfCombinations; i++) {
			SearchPathTyped[] sources = new SearchPathTyped[keywordNum];
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
			// 乘以联合分布概率
			int count = fnSet.countNodeSet(path.getNode(path.nodeNum() - 1), sources);
			if (count == 0)
				score *= 0.025;
			else
				score *= getJointProb(count, graph.classManager.getInstanceNum(path.getNode(path.nodeNum() - 1)));
			if (allResults.size() >= k && score <= allResults.get(k-1).getScore()) {
				continue;
			}
			else {
				UnfoldedPatternTree_ET at = new UnfoldedPatternTree_ET(path.getNode(path.nodeNum() - 1), sources, score);
				addAnswer(at);
			}
		}
	}
	
	private void stop() throws UnderflowException, IOException {
		Iterator<Short> vIt = candidates.iterator();
		while (vIt.hasNext()) {
			short candidate = vIt.next();
			SearchPathTypedHub hub = records[candidate];
			double upperBound = 0.0d;
			if (hub == null) {
				upperBound = upperBound(candidate);
			}
			else {
				if (hub.isComplete()) {
					for (int j = 0; j< keywordNum; j++) {
						double temp = upperBound(candidate, hub, j);
						if (upperBound < temp) {
							upperBound = temp;
						}
					}
				}
				else {
					upperBound = upperBound(candidate, hub);
				}
			}
			if (upperBound <= allResults.get(k - 1).getScore()) {
				if (hub != null) hub.disqualify();
				vIt.remove();
//				if (logWriter != null) {
//					logWriter.write("Disqualified: " + candidate + ", upperBound: " + upperBound);
//					logWriter.newLine();
//				}
			}
			stopTimes++;
		}
	}
	
	private double upperBound(short root) throws UnderflowException {
		double bound = 1.0d;
		for (int j = 0; j < keywordNum; j++) {
			if (heaps[j].hasNext()) {
				bound *= heaps[j].peek();
			}
			else {
				return 0.0d;
			}
		}
		bound *= getJointProb(fnSet.getMaxCount(root), graph.classManager.getInstanceNum(root));
		return bound;
	}

	private double upperBound(short root, SearchPathTypedHub record) throws UnderflowException {
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
		bound *= getJointProb(fnSet.getMaxCount(root), graph.classManager.getInstanceNum(root));
		return bound;
	}

	private double upperBound(short root, SearchPathTypedHub hub, int index) throws UnderflowException {
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
		bound *= getJointProb(fnSet.getMaxCount(root), graph.classManager.getInstanceNum(root));
		return bound;
	}
	
	private void addAnswer(UnfoldedPatternTree_ET answer) throws IOException {
		ListIterator<UnfoldedPatternTree_ET> it = allResults.listIterator();
		int pos = 0;
		while (it.hasNext()) {
			UnfoldedPatternTree_ET other = it.next();
				if (other.getScore() >= answer.getScore()) {
					pos++;
				}
		}
			
		allResults.add(pos, answer);
//		if (logWriter != null) {
//			logWriter.write("add " + answer.getString(graph));
//			logWriter.newLine();
//		}
	}
	
	private double getJointProb(double x, double y) {
		return Math.sqrt(x / y);
	}
	
	public void closeLogWriter() {
		if (logWriter != null) {
			try {
				logWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
