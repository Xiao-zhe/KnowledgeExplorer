package edu.whu.clock.probsearch;

import java.io.IOException;
import java.util.ArrayList;

import edu.whu.clock.graphsearch.util.UnderflowException;

public class ProbSearchEngine {
	
	private ProbSearchAlgorithm algorithm;
	private ProbIndex index;
	private ClassManager classManager;
	
	public void init(String dir, String indexPath) {
		try {
			classManager = new ClassManager();
			classManager.load(dir);
			InstanceManager instanceManager = new InstanceManager();
			instanceManager.load(dir, classManager);
			ProbSchemaGraph graph = new ProbSchemaGraph(classManager, instanceManager);
			graph.load(dir);
			algorithm = new ProbSearchAlgorithm(graph);
			index = new ProbIndex();
			index.init(dir, indexPath, "index");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		index.close();
	}

	public RawProbAnswerTree[] search(String[] keywords, int k) {
		if (keywords == null || keywords.length < 2 || k < 1) {
			return null;
		}
		try {
			ProbIndexEntry[] entries = new ProbIndexEntry[keywords.length];
			for (int i = 0; i < keywords.length; i++) {
				entries[i] = getEntry(keywords[i]);
			}
			
			algorithm.init(entries);
			return algorithm.execute(k);
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ProbIndexEntry getEntry(String keyword) {
		String[] words = keyword.split(" ");
		if (words.length == 1) {
			return index.get(words[0]);
		}
		else if (words.length == 2) {
			return combineTwoEntries(words[0], words[1]);
		}
		else {
			System.out.println(keyword + " contains too many words!");
			return null;
		}
	}
	
	private ProbIndexEntry combineTwoEntries(String a, String b) {
		ProbIndexEntry ea = index.get(a);
		ProbIndexEntry eb = index.get(b);
		if (ea == null || eb == null) {
			return null;
		}
		int pos = 0;
		ArrayList<Integer> newClassIDList = new ArrayList<Integer>();
		ArrayList<Double> newProbList = new ArrayList<Double>();
		for (int i = 0; i < ea.getClassIDList().length; i++) {
			for (int j = pos; j < eb.getClassIDList().length; j++) {
				if (ea.getClassID(i) == eb.getClassID(j)) {
					newClassIDList.add(ea.getClassID(i));
					newProbList.add(ea.getProb(i) * eb.getProb(j));
					pos = j + 1;
					break;
				}
				else if (ea.getClassID(i) < eb.getClassID(j)) {
					pos = j;
					break;
				}
				pos++;
			}
		}
		int[] classIDList = new int[newClassIDList.size()];
		double[] probList = new double[newClassIDList.size()];
		for (int i = 0; i < classIDList.length; i++) {
			classIDList[i] = newClassIDList.get(i);
			probList[i] = newProbList.get(i);
		}
		return new ProbIndexEntry(classIDList, probList);
	}
	
//	private int[][] getMatchedVertices(String[] keywords) {
//		int[][] matchedVertices = new int[keywords.length][];
//		for (int i = 0; i < keywords.length; i++) {
//			matchedVertices[i] = lookupIndex(keywords[i]);
//			if (matchedVertices[i] == null) {
//				System.out.println("Keyword [" + keywords[i] + "] does not exist in the graph.");
//				return null;
//			}
//		}
//		return matchedVertices;
//	}
	
//	private ProbIndexEntry lookupIndex(String keyword) {
//		return index.get(keyword);
//	}
	
	public void print(RawProbAnswerTree[] result) {
		int i = 1;
		for (RawProbAnswerTree answer : result) {
			System.out.print("Top-" + i + "  ");
			printRawAnswerTree(answer);
			i++;
		}
	}
	
	public void printRawAnswerTree(RawProbAnswerTree tree) {
		System.out.print("{" + tree.getRoot() + "." + classManager.getClassName(tree.getRoot()) + " --- ");
		SearchPathRef[] paths = tree.getPaths();
		for (SearchPathRef path : paths) {
			String str = "(";
			while (path.getPrev() != null) {
				path = path.getPrev();
				str += ", " + path.getEnd() + "." + classManager.getClassName(path.getEnd());
			}
			System.out.print(str + ")");
		}
		System.out.println("} : " + tree.getScore());
	}
	
	public static void main(String[] args) {
		ProbSearchEngine se = new ProbSearchEngine();
//		se.init("D:/testing example", "D:/dbpedia probindex");
		se.init("D:/DBpedia/clean", "D:/dbpedia index");
		RawProbAnswerTree[] result = se.search(new String[]{"inter", "ronaldo", "zanetti"}, 10);
//		RawProbAnswerTree[] result = se.search(new String[]{"brooklyn bridge", "river"}, 10);
//		RawProbAnswerTree[] result = se.search(new String[]{"honeymoon", "marry"}, 1);
		se.print(result);
		se.shutdown();
	}

}
