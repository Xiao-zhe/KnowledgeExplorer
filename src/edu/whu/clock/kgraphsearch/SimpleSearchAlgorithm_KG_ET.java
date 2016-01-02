package edu.whu.clock.kgraphsearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import edu.whu.clock.graphsearch.util.UnderflowException;
import edu.whu.clock.kgraphsearch.index.IndexedEdge_KG_ET;
import edu.whu.clock.kgraphsearch.index.KeywordIndexEntry_KG_ET;
import edu.whu.clock.newgraph.EntityGraphTyped;
import edu.whu.clock.newprobindex.PKIndexManager;

public class SimpleSearchAlgorithm_KG_ET {

	public final String logDir;
	
	private final EntityGraphTyped graph;
	private final PKIndexManager pkIndex;
	private BufferedWriter logWriter;

	private int keywordNum;
	private SimpleSearchPathPQ_KG_ET[] heaps;
	private LinkedList<UnfoldedEntityTree_ET> allResults;
	private SimpleSearchPathHub_KG_ET[] records;

	// statistics
	public long visitTimes = 0;
	public long stopTimes = 0; 

	public SimpleSearchAlgorithm_KG_ET(EntityGraphTyped graph, PKIndexManager pkIndex, String logDir) throws IOException {
		this.graph = graph;
		this.pkIndex = pkIndex;
		this.logDir = logDir;
		// 如果需要日志就初始化logWriter
		logWriter = new BufferedWriter(new FileWriter(logDir + "KG simple search.txt"));
	}

	public SimpleSearchAlgorithm_KG_ET(EntityGraphTyped graph, PKIndexManager pkIndex) {
		this.graph = graph;
		this.pkIndex = pkIndex;
		this.logDir = null;
	}
	
	public int numOfResult() {
		return allResults.size();
	}
	
	public void run(String[] keywords, int k) throws IOException {
		if (keywords == null || keywords.length < 2 || k < 1) {
			System.out.println("Error: invalid inputs.");
			return;
		}
		try {
			KeywordIndexEntry_KG_ET[] entries = new KeywordIndexEntry_KG_ET[keywords.length];
			for (int i = 0; i < keywords.length; i++) {
				entries[i] = pkIndex.getEdgesOnKG(keywords[i]);
			}
			
			init(entries);
			execute(k);
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
	}

	public void init(KeywordIndexEntry_KG_ET[] entries) throws IOException {
		keywordNum = entries.length;
		heaps = new SimpleSearchPathPQ_KG_ET[keywordNum];
		records = new SimpleSearchPathHub_KG_ET[graph.getNodeNum()]; //为每个节点保管所有已知到达该节点的searchpath
		allResults = new LinkedList<UnfoldedEntityTree_ET>();
		for (int i = 0; i < keywordNum; i++) {
			KeywordIndexEntry_KG_ET entry = entries[i];
			if (entry == null) {
				System.out.println("Error: The entry of the keyword #" + i + " is null.");
				System.exit(1);
			}
//			if (logWriter != null) {
//				logWriter.write("The initial search paths of the keyword #" + i + ": ");
//				logWriter.newLine();
//			}
			heaps[i] = new SimpleSearchPathPQ_KG_ET(i, graph);
			for (IndexedEdge_KG_ET edge : entry.getEdgeList()) {
				if (edge.getStart() >= 2350906 || edge.getEnd() >= 2350906)
					System.out.println();
				SimpleSearchPath_KG_ET sp = new SimpleSearchPath_KG_ET(edge);
				heaps[i].insert(sp);
//				if (logWriter != null) {
//					logWriter.write(sp.getString(graph));
//					logWriter.newLine();
//					logWriter.write("*****************************************************");
//					logWriter.newLine();
//				}
			}
		}
	}

	public void execute(int k) throws UnderflowException, IOException {
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
			SimpleSearchPath_KG_ET path = heaps[keywordID].next();
			
			// visit this vertex
//			if (logWriter != null) {
//				logWriter.write("[" + keywordID + "] ");
//				logWriter.write(path.getString(graph));
//				logWriter.newLine();
//			}
			visit(keywordID, path);
			
			if (allResults.size() >= k) {
				System.out.println("Successfully stopped");
//				System.out.println("visit times: " + visitTimes);
				for (int j = 0; j < k; j++) {
					if (logWriter != null) {
						logWriter.write(allResults.get(j).getString(graph));
						logWriter.newLine();
					}
				}
				logWriter.write("*****************************************");
				logWriter.newLine();
				return;
			}
		}
		System.out.println("All heaps are empty now.");
		int num = Math.min(allResults.size(), k);
		for (int j = 0; j < num; j++) {
			if (logWriter != null) {
				logWriter.write(allResults.get(j).getString(graph));
				logWriter.newLine();
			}
		}
		logWriter.write("*****************************************");
		logWriter.newLine();
	}
	
	private boolean isNotAllHeapEmpty(boolean[] isEmpty) {
		for (boolean temp : isEmpty) {
			if (!temp) return true;
		}
		return false;
	}
	
	private void visit(int keywordID, SimpleSearchPath_KG_ET path) throws UnderflowException, IOException {
		int root = path.getNode(path.nodeNum() - 1);
		if (records[root] == null) {
			// produce a new record for the vertex
			SimpleSearchPathHub_KG_ET hub = new SimpleSearchPathHub_KG_ET(keywordNum);
			hub.add(keywordID, path);
			records[root] = hub;
		}
		else {  // if R has been visited before
			SimpleSearchPathHub_KG_ET hub = records[root];
			hub.add(keywordID, path);
			
			if (hub.isComplete()) {
				crossProduct(hub, keywordID, path);
			}
		}
		visitTimes++;
	}
	
	private void crossProduct(SimpleSearchPathHub_KG_ET record, int keywordID, SimpleSearchPath_KG_ET path) throws IOException {
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
			SimpleSearchPath_KG_ET[] sources = new SimpleSearchPath_KG_ET[keywordNum];
			int score = 0;
			for (int j = 0; j < keywordNum; j++) {
				if (j == keywordID) {
					sources[j] = path;
					score += path.getLength();
				}
				else {
					int pos = (i / temp[j]) % record.get(j).size();
					sources[j] = record.get(j).get(pos);
					score += sources[j].getLength();
				}
			}
			UnfoldedEntityTree_ET at = new UnfoldedEntityTree_ET(path.getNode(path.nodeNum() - 1), sources, score);
			allResults.add(at);
//			if (logWriter != null) {
//				logWriter.write("add " + at.getString(graph));
//				logWriter.newLine();
//			}
		}
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
