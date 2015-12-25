package edu.whu.clock.newgraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class SummaryGraphTyped {

	private int edgeNum;

	private SummaryGraphEdgeTyped[][] allEdges;
	public final ClassManager classManager;
	public final EdgeTypeManager etypeManager;
//	private HashMap<String, Short> type2id;
//	private HashMap<Short, String> id2type;

	public SummaryGraphTyped(ClassManager classManager, EdgeTypeManager etypeManager) {
		this.edgeNum = 0;
		this.classManager = classManager;
		this.etypeManager = etypeManager;
		this.allEdges = new SummaryGraphEdgeTyped[classManager.getClassNum()][];
//		this.type2id = new HashMap<String, Short>();
//		this.id2type = new HashMap<Short, String>();
	}

	public int getEdgeNum() {
		return edgeNum;
	}

	public short getNodeNum() {
		return classManager.getClassNum();
	}

	public void addAllEdges(String dir) {
		try {
			ArrayList<TreeSet<SummaryGraphEdgeTyped>> temp = new ArrayList<TreeSet<SummaryGraphEdgeTyped>>(classManager.getClassNum());
			for (int i = 0; i < classManager.getClassNum(); i++) {
				temp.add(new TreeSet<SummaryGraphEdgeTyped>());
			}

			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/class_relationship_mapping.txt")); // begin to import the edges between the class nodes
			String line = null;
			String[] elements = new String[3];
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ");
				temp.get(classManager.getClassID(elements[0])).add(new SummaryGraphEdgeTyped(classManager.getClassID(elements[2]), etypeManager.getID(elements[1]), true));
				temp.get(classManager.getClassID(elements[2])).add(new SummaryGraphEdgeTyped(classManager.getClassID(elements[0]), etypeManager.getID(elements[1]), false));
				edgeNum++;
			}
			br.close();
			
			for (int i = 0; i < classManager.getClassNum(); i++) {
				allEdges[i] = new SummaryGraphEdgeTyped[temp.get(i).size()];
				Iterator<SummaryGraphEdgeTyped> it = temp.get(i).iterator();
				int j = 0;
				while (it.hasNext()) {
					allEdges[i][j] = it.next();
					j++;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Num of nodes: " + getNodeNum());
		System.out.println("Num of edges: " + getEdgeNum());
	}

//	public void setProbability(String dir) {
//		String line = null;
//		String ASplit[] = new String[3];
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(dir
//					+ "/instance_relationship_mapping.txt"));
//			while (br.ready()) {
//				line = br.readLine();
//				ASplit = line.split(" ", 3);
//				put(ASplit[0], ASplit[2]);
//				put(ASplit[2], ASplit[0]);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		///////////////////////////////////////////
//		Iterator<Short> it = allEdges.keySet().iterator();
//		while (it.hasNext()) {
//			short start = it.next();
//			ProbSchemaGraphEdge current = allEdges.get(start).getNextEdge();
//			while (current != null) {
//				if (classManager.getInstanceNum(start) == 0) {
//					current = current.getNextEdge();
//					continue;
//				}
//				if (num_mapping.get(start + "@" + current.getEnd()) == null) {
//					// System.out.println(i+"@"+current.getEnd());
//					// System.out.println("null");
//					current = current.getNextEdge();
//					continue;
//				}
//				double f = (double) num_mapping.get(start + "@" + current.getEnd())
//								.size() / (double) classManager.getInstanceNum(start);
//				// f = f + (float)num_mapping.get(i+"@"+current.getEnd());
//				current.setP(f);
////				System.out.println(100 * f);
//
//				current = current.getNextEdge();
//			}
//		}
//		// System.out.println(dif);
//
//	}

//	private void put(String instance1, String instance2) {
//		String str = instanceManager.instance2class.get(instance1) + "@"
//				+ instanceManager.instance2class.get(instance2);
//		if (num_mapping.get(str) == null) {
//			HashSet<String> set = new HashSet<String>();
//			set.add(instance1);
//			num_mapping.put(str, set);
//		} else {
//			HashSet<String> set = num_mapping.get(str);
//			set.add(instance1);
//		}
//	}

	public SummaryGraphEdgeTyped[] getNeighbors(short start) {
		return allEdges[start];
	}

	public SummaryGraphEdgeTyped[] getNeighbors(String source) {
		return getNeighbors(classManager.getClassID(source));
	}

	public ClassManager getClassManager() {
		return this.classManager;
	}

//	public ProbSchemaGraphEdge findEdge(int start, int end) // 加了一个找边
//	{
//		ProbSchemaGraphEdge head = allEdges[start];
//		ProbSchemaGraphEdge current = head.getNextEdge();
//		while (true) {
//			if (current == null)
//				break;
//			if (current.getEnd() == end)
//				return current;
//			current = current.getNextEdge();
//		}
//		return null;
//
//	}

	public void write2file(String dir)
			{
		try {
	        BufferedWriter bw = new BufferedWriter(new FileWriter(dir));
	        for (int i = 0; i < allEdges.length; i++) {
	        	if(allEdges[i]==null)
	    		continue;
	        	for (int j = 0; j < allEdges[i].length; j++) {
	        		SummaryGraphEdgeTyped current = allEdges[i][j];
	        		String str = i+" "+current.getEnd()+" " + current.getType() + " " + current.isOut();
	    	  bw.write(str);
	    	  bw.newLine();
	        	}
	    		 
	        }
	        bw.close();   
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void load(String dir)
	 {
		try {
			 BufferedReader br = new BufferedReader(new FileReader(dir + "/graph_memory.txt"));
			 String line = null;
				String[] ASplit = null;
				short start = 0,end = 0 , type = 0;
				boolean out = false;
				ArrayList<SummaryGraphEdgeTyped> list = null;
				short last = -1;
				while (br.ready()) {
					line = br.readLine();
					ASplit = line.split(" ");
					start = Short.parseShort(ASplit[0]);
					end = Short.parseShort(ASplit[1]);
					type = Short.parseShort(ASplit[2]);
					out = Boolean.parseBoolean(ASplit[1]);
					if (last != start) {
						if (list != null) {
							allEdges[last] = list.toArray(new SummaryGraphEdgeTyped[list.size()]);
						}
						list =  new ArrayList<SummaryGraphEdgeTyped>();
						last = start;
					}
					list.add(new SummaryGraphEdgeTyped(end, type ,out));
				}
				br.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	 }
}
