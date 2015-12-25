package edu.whu.clock.graphsearch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.whu.clock.database.ClassDB;

public class GraphInMemory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2482986411021037196L;

	private int edgeNum;
    private int classNum;
	private HashMap<Integer, GraphEdge> rowEdgeHead;
	private ClassDB classDB;
	private HashMap<String, Integer> type2id;
	private HashMap<Integer, String> id2type;

	public GraphInMemory(ClassDB classDB) {
		this.classDB = classDB;
		this.rowEdgeHead = new HashMap<Integer, GraphEdge>();
		this.type2id = new HashMap<String, Integer>();
		this.id2type = new HashMap<Integer, String>();
	}

	public int getEdgeNum() {
		return edgeNum;
	}
	public int getClassNum(){
		return ++classNum;
	}
	public HashMap<String, Integer>  gettype2id(){
		return type2id;
	}
	
	
	@SuppressWarnings("resource")
	public void load(String dir) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/relationship_type.txt"));
			String line = null;
			int id = 0;
			while (br.ready()) {
				line = br.readLine();
				id++;
				type2id.put(line, id);
				id2type.put(id, line);
			}

			br = new BufferedReader(new FileReader(dir
					+ "/class_relationship_mapping.txt"));
			String[] elements = new String[3];
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ");
				addEdge(classDB.getClassID(elements[0]), classDB.getClassID(elements[2]), elements[1]);
//				addEdge(classDB.getClassID(elements[2]), classDB.getClassID(elements[0]), elements[1]);
			}
			
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		System.out.println("Num of edges:    " + getEdgeNum());
	}
	@SuppressWarnings("resource")
	public void loadUndirectedGraph(String dir) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/relationship_type.txt"));
			String line = null;
			int id = 0;
			while (br.ready()) {
				line = br.readLine();
				id++;
				type2id.put(line, id);
				id2type.put(id, line);
			}


			br = new BufferedReader(new FileReader(dir
					+ "/class_relationship_mapping.txt"));
			String[] elements = new String[3];
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ");
				addEdge(classDB.getClassID(elements[0]), classDB.getClassID(elements[2]), elements[1]);
				addEdge(classDB.getClassID(elements[2]), classDB.getClassID(elements[0]), elements[1]);
			}
			
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		classNum = rowEdgeHead.size();
		System.out.println("Num of class:    "+getClassNum());
		System.out.println("Num of edges:    " + getEdgeNum());
	}

	public void addEdge(int start, int end, String type) {
		GraphEdge head = rowEdgeHead.get(start);
		int t = type2id.get(type);
		if (head != null) {// 表头节点,说明此时已经有一个元素了
			GraphEdge ahead = head;
			GraphEdge current = ahead.getNextEdge();

			while (current != null && end > current.getEnd()) {
				ahead = current;
				current = current.getNextEdge();
			}

			if (current == null) {
				ahead.setNextEdge(new GraphEdge(end, t, null));
			}
			else {
				if (end == current.getEnd()) {
					current.addType(t);
					edgeNum--;
				}
				else {
					ahead.setNextEdge(new GraphEdge(end, t, current));
				}
			}
		}
		else {
			head = new GraphEdge(0, 0, new GraphEdge(end, t, null));
			rowEdgeHead.put(start, head);
		}
		edgeNum++;
	}
	
	public HashMap getRowEdgeHead(){
		return rowEdgeHead;
	}
//	public HashMap<Integer,Integer> neiHash(int source){
//		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
//		int a[] = GraphInMemory.getNeighbors(source);
//		for(int i =0;i<a.length;i++){
//			map.put(a[i], i);
//		}
//	
//		return map;
//	}
	public int[] getNeighbors(int source) {
		GraphEdge head = rowEdgeHead.get(source);
		if (head == null) {// 这是一个孤立的节点
			return null;
		}
		GraphEdge re = head.getNextEdge();

		ArrayList<Integer> temp = new ArrayList<Integer>();

		while (re != null) {
			temp.add(re.getEnd());
			re = re.getNextEdge();
		}
		
		int[] result = new int[temp.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = temp.get(i);
		}

		return result;
	}

	public int[] getNeighbors(String source) {
		return getNeighbors(classDB.getClassID(source));
	}
	
	public String[] getNeighborNames(String source) {
		int[] temp = getNeighbors(source);
		String[] result = new String[temp.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = classDB.getClassName(temp[i]);
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		ClassDB classDB = new ClassDB();
		classDB.load("D:/dbpedia/clean");
//		classDB.load("D:/testing example");
		GraphInMemory graph = new GraphInMemory(classDB);
		graph.loadUndirectedGraph("D:/dbpedia/clean");
//		graph.loadUndirectedGraph("D:/testing example");
		FileOutputStream fos = new FileOutputStream("D:/SerializedFile/GraphInMemory.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(graph);
		oos.close();
//		String[] set = graph.getNeighborNames("Judge");
//		String[] set = graph.getNeighborNames("Song");
//		int[] set = graph.getNeighbors(0);
//		for (int str : set) {
//			System.out.println(str);
//		}
//		HashMap map = graph.neiHash(0);
//		java.util.Iterator it =  map.entrySet().iterator();
//		while(it.hasNext()){
//			Map.Entry entry = (Entry) it.next();
//			int a = (Integer) entry.getKey();
//			int b = (Integer) entry.getValue();
//			System.out.println(a+"-"+b);
//		}
	}
}
