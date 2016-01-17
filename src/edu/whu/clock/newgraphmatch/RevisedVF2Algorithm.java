package edu.whu.clock.newgraphmatch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.whu.clock.generalsearch.SearchPath_ET;
import edu.whu.clock.generalsearch.UnfoldedPatternTree_ET;
import edu.whu.clock.newgraph.EntityGraphEdgeTyped;
import edu.whu.clock.newgraph.GraphManager;

public class RevisedVF2Algorithm {

	public final String logDir;
	
	private GraphManager graphManager;
	private String[] keywords;
	private BufferedWriter logWriter;
//	private int pathCount; // 算法是从root开始，对每一条path分开调用递归函数进行匹配，pathCount是当前处理的path编号

	public RevisedVF2Algorithm(GraphManager graphManager) {
		this.graphManager = graphManager;
		this.logDir = null;
	}
	
	public RevisedVF2Algorithm(GraphManager graphManager, String logDir) throws IOException {
		this.graphManager = graphManager;
		this.logDir = logDir;
		logWriter = new BufferedWriter(new FileWriter(logDir + "KG subtree matching.txt"));
	}

	public ResultTreeTyped run(String[] keywords, UnfoldedPatternTree_ET queryGraph) throws IOException {
		if (keywords.length != queryGraph.numOfPaths()) {
			System.out.println("Error: the query graph has the wrong number of paths.");
			return null;
		}
		short root = queryGraph.getRoot(); // 取root的实例，经过匹配条件的筛选得到候选的实例
		this.keywords = keywords;
//		this.matched = new int[queryGraph.numOfPaths()][];
//		for (int i = 0; i < matched.length; i++) { // 初始化matched数组为-1
//			int num = queryGraph.getPath(i).nodeNum();
//			matched[i] = new int[num];
//			for (int j = 0; j < num; j++) {
//				matched[i][j] = -1;
//			}
//		}
		int[] rootInstSet = graphManager.instanceManager.getInstanceSet(root);
		for (int rootInst : rootInstSet) {
			ArrayList<ArrayList<Integer>> allCandidates = new ArrayList<ArrayList<Integer>>();
			boolean valid = true;
			for (int i = 0; i < queryGraph.numOfPaths(); i++) {
				SearchPath_ET path = queryGraph.getPath(i);
				ArrayList<Integer> candidates;
				if (path.nodeNum() > 2) {
					candidates = filter(rootInst, path.getType(path.nodeNum() - 2), path.isOut(path.nodeNum() - 2), path.getNode(path.nodeNum() - 2));
				}
				else if (path.nodeNum() == 2) {
					candidates = filter(rootInst, path.getType(path.nodeNum() - 2), path.isOut(path.nodeNum() - 2), path.getNode(path.nodeNum() - 2), keywords[i]);
				}
				else {
					System.out.println("Error: found a path with only one node.");
					return null;
				}
				if (candidates == null || candidates.isEmpty()) {
					valid = false;
					break;
				}
				allCandidates.add(candidates);
			}
			if (!valid) {
				continue;
			}
			ResultTreeTyped result = new ResultTreeTyped(rootInst, queryGraph);
			for (int i = 0; i < queryGraph.numOfPaths(); i++) {
//				if (i > 0 && matched[i - 1][0] == -1) {
//					break;
//				}
				SearchPath_ET path = queryGraph.getPath(i);
				if (path.nodeNum() == 2) {
					result.setNode(i, 0, allCandidates.get(i).get(0));
					result.setMatched(i, true);
					continue;
				}
				for (int nextEntityNode : allCandidates.get(i)) {
					recursive(result, nextEntityNode, i, path, path.nodeNum() - 2);
					if (result.isMatched(i)) {
						break;
					}
				}
				if (!result.isMatched(i)) {
					break;
				}
			}
			if (result.isAllMatched()) {
				if (logWriter != null) {
					logWriter.write(result.getString(graphManager.entityGraphTyped));
					logWriter.newLine();
				}
				return result;
			}
		}
		return null;
	}
	
//	public ResultTreeTyped run(String[] keywords, NaiveUnfoldedPatternTree_ET queryGraph) {
//		if (keywords.length != queryGraph.numOfPaths()) {
//			System.out.println("Error: the query graph has the wrong number of paths.");
//			return null;
//		}
//		short root = queryGraph.getRoot(); // 取root的实例，经过匹配条件的筛选得到候选的实例
//		this.keywords = keywords;
////		this.matched = new int[queryGraph.numOfPaths()][];
////		for (int i = 0; i < matched.length; i++) { // 初始化matched数组为-1
////			int num = queryGraph.getPath(i).nodeNum();
////			matched[i] = new int[num];
////			for (int j = 0; j < num; j++) {
////				matched[i][j] = -1;
////			}
////		}
//		int[] rootInstSet = graphManager.instanceManager.getInstanceSet(root);
//		for (int rootInst : rootInstSet) {
//			ArrayList<ArrayList<Integer>> allCandidates = new ArrayList<ArrayList<Integer>>();
//			boolean valid = true;
//			for (int i = 0; i < queryGraph.numOfPaths(); i++) {
//				NaiveSearchPath_ET path = queryGraph.getPath(i);
//				ArrayList<Integer> candidates;
//				if (path.nodeNum() > 2) {
//					candidates = filter(rootInst, path.getType(path.nodeNum() - 2), path.isOut(path.nodeNum() - 2), path.getNode(path.nodeNum() - 2));
//				}
//				else if (path.nodeNum() == 2) {
//					candidates = filter(rootInst, path.getType(path.nodeNum() - 2), path.isOut(path.nodeNum() - 2), path.getNode(path.nodeNum() - 2), keywords[i]);
//				}
//				else {
//					System.out.println("Error: found a path with only one node.");
//					return null;
//				}
//				if (candidates == null || candidates.isEmpty()) {
//					valid = false;
//					break;
//				}
//				allCandidates.add(candidates);
//			}
//			if (!valid) {
//				continue;
//			}
//			ResultTreeTyped result = new ResultTreeTyped(rootInst, queryGraph);
//			for (int i = 0; i < queryGraph.numOfPaths(); i++) {
////				if (i > 0 && matched[i - 1][0] == -1) {
////					break;
////				}
//				NaiveSearchPath_ET path = queryGraph.getPath(i);
//				if (path.nodeNum() == 2) {
//					result.setNode(i, 0, allCandidates.get(i).get(0));
//					result.setMatched(i, true);
//					continue;
//				}
//				for (int nextEntityNode : allCandidates.get(i)) {
//					recursive(result, nextEntityNode, i, path, path.nodeNum() - 2);
//					if (result.isMatched(i)) {
//						break;
//					}
//				}
//				if (!result.isMatched(i)) {
//					break;
//				}
//			}
//			if (result.isAllMatched()) {
//				return result;
//			}
//		}
//		return null;
//	}
	
	private ArrayList<Integer> filter(int entityNode, int type, boolean out, short classNode) {
		EntityGraphEdgeTyped[] edgeList = graphManager.entityGraphTyped.getNeighbors(entityNode);
		ArrayList<Integer> candidates = new ArrayList<Integer>();
		for (EntityGraphEdgeTyped edge : edgeList) {
			if (type == edge.getType() 
					&& out != edge.isOut() 
					&& classNode == graphManager.instanceManager.getClassID(edge.getEnd())) {
				candidates.add(edge.getEnd());
			}
		}
		return candidates;
	}
	
	private ArrayList<Integer> filter(int entityNode, int type, boolean out, short classNode, String keyword) {
		EntityGraphEdgeTyped[] edgeList = graphManager.entityGraphTyped.getNeighbors(entityNode);
		ArrayList<Integer> candidates = new ArrayList<Integer>();
		for (EntityGraphEdgeTyped edge : edgeList) {
			if (type == edge.getType() 
					&& out != edge.isOut() 
					&& classNode == graphManager.instanceManager.getClassID(edge.getEnd())) {
				int[] keyids = graphManager.pkIndex.getNodesOnKG(keyword);
				for (int k : keyids) {
					if (edge.getEnd() == k) {
						candidates.add(edge.getEnd());
						break;
					}
				}
			}
		}
		return candidates;
	}

	/*
	 * 参数说明：id是待检测的实例（也就是上一级recursive得到的candidate中的一个）； index是
	 * 当前path的nodes数组的下标，nodes[index]是参数id对应的classid
	 */
	public void recursive(ResultTreeTyped result, int nextEntityNode, int pathCount, SearchPath_ET path, int index) {
		ArrayList<Integer> candidates = null;
		if (index > 1) {
			candidates = filter(nextEntityNode, path.getType(index - 1), path.isOut(index - 1), path.getNode(index - 1));
		}
		else if (index == 1) {
			candidates = filter(nextEntityNode, path.getType(index - 1), path.isOut(index - 1), path.getNode(index - 1), keywords[pathCount]);
		}
		if (candidates != null && !candidates.isEmpty()) {
			result.setNode(pathCount, index, nextEntityNode);
			index--;
			if (index > 0) {
				for (int cand : candidates) {
					recursive(result, cand, pathCount, path, index);
					if (result.isMatched(pathCount)) {
						return;
					}
				}
			}
			else {
				result.setNode(pathCount, index, candidates.get(0));
				result.setMatched(pathCount, true);
			}
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

	// public static void main(String[] args){
	// ClassManager classManager = new ClassManager();
	// classManager.load("D:/testing example");
	// InstanceManager instanceManager = new InstanceManager();
	// instanceManager.load("D:/testing example", classManager);
	// ClassInstanceIndex Cindex = new ClassInstanceIndex();
	// Cindex.init("D:/testing example", "D:/index file/classInstanceIndex",
	// "index");
	// Cindex.build();
	// EntityIndex index = new EntityIndex();
	// index.init("D:/testing example", "D:/index file/entityindex", "index");
	// index.build();
	// EntityGraphTyped graph = new EntityGraphTyped(instanceManager);
	// graph.addAllEdges("D:/testing example");
	//
	//
	// // SearchPathTyped path00 = new
	// SearchPathTyped((short)2,(short)3,true,(short)4,0);
	// SearchPathTyped path0 = new
	// SearchPathTyped((short)4,(short)6,false,(short)1,0);
	// // SearchPathTyped path1 = new
	// SearchPathTyped((short)3,(short)2,false,(short)0,0);
	// SearchPathTyped path2 = new
	// SearchPathTyped((short)0,(short)5,true,(short)1,0);
	// SearchPathTyped[] path = new SearchPathTyped[2];
	// path[0] = path0;
	// path[1] = path2;
	// UnfoldedProbPatternTreeTyped patternTree = new
	// UnfoldedProbPatternTreeTyped((short)1,path,0);
	//
	// RevisedVF2Algorithm subGraphMatch = new
	// RevisedVF2Algorithm(patternTree,graph,classManager,instanceManager,Cindex,index);
	// subGraphMatch.setKey();
	// subGraphMatch.match();
	// for(int i=0;i<path.length;i++){
	// System.out.print("path"+i+"  ");
	// for(int j=0;j<10;j++){
	// if(matched[i][j]!=-1)
	// System.out.print(matched[i][j]+"  ");
	// }
	// System.out.println();
	//
	// }
	//
	// }
}
