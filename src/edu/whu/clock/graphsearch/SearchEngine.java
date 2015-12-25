package edu.whu.clock.graphsearch;

import edu.whu.clock.database.ClassDB;
import edu.whu.clock.graphsearch.util.UnderflowException;

public class SearchEngine {
	
	private SearchAlgorithm algorithm;
	private Index index;
	private ClassDB classDB;
	
	public void init(String dir, String indexPath) {
		classDB = new ClassDB();
		classDB.load(dir);
		GraphInMemory graph = new GraphInMemory(classDB);
		graph.load(dir);
		algorithm = new SearchAlgorithm(graph);
		index = new Index();
		index.init(dir, indexPath, "index");
	}
	
	public void shutdown() {
		index.close();
	}

	public AnswerTree[] search(String[] keywords, int k) {
		if (keywords == null || keywords.length < 2 || k < 1) {
			return null;
		}
		try {
			int[][] matchedVertices = getMatchedVertices(keywords);
			if (matchedVertices == null) {  // At least one keyword does not exist in the graph.
				return null;
			}
			
			System.out.println("The number of matched vertices for each keyword");
			for (int i = 0; i < keywords.length; i++) {
				System.out.println(keywords[i] + " : " + matchedVertices[i].length);
			}
			
			algorithm.init(matchedVertices);
			return algorithm.execute(k);
		} catch (UnderflowException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int[][] getMatchedVertices(String[] keywords) {
		int[][] matchedVertices = new int[keywords.length][];
		for (int i = 0; i < keywords.length; i++) {
			matchedVertices[i] = lookupIndex(keywords[i]);
			if (matchedVertices[i] == null) {
				System.out.println("Keyword [" + keywords[i] + "] does not exist in the graph.");
				return null;
			}
		}
		return matchedVertices;
	}
	
	private int[] lookupIndex(String keyword) {
		return index.get(keyword);
	}
	
	public void print(AnswerTree[] result) {
		for (AnswerTree answer : result) {
			answer.transform(classDB);
			System.out.println(answer);
		}
	}
	
	public static void main(String[] args) {
		SearchEngine se = new SearchEngine();
		se.init("D:/DBpedia", "D:/dbpedia index");
		AnswerTree[] result = se.search(new String[]{"inter", "zanetti", "ronaldo"}, 10);
		se.print(result);
		se.shutdown();
	}

}
