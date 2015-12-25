package edu.whu.clock.probsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ProbSchemaGraph {

	private int edgeNum;

	private HashMap<Integer, ProbSchemaGraphEdge> rowEdgeHead;
	private ClassManager classManager;
	private InstanceManager instanceManager;
	private HashMap<String, Integer> type2id;
	private HashMap<Integer, String> id2type;

	private HashMap<String, HashSet<String>> num_mapping = new HashMap<String, HashSet<String>>();

	public ProbSchemaGraph(ClassManager classManager,
			InstanceManager instanceManager) {
		this.classManager = classManager;
		this.instanceManager = instanceManager;
		this.rowEdgeHead = new HashMap<Integer, ProbSchemaGraphEdge>();
		this.type2id = new HashMap<String, Integer>();
		this.id2type = new HashMap<Integer, String>();
	}

	public int getEdgeNum() {
		return edgeNum;
	}

	public int getVertexNum() {
		return rowEdgeHead.size();
	}

	public HashMap<Integer, ProbSchemaGraphEdge> getRowEdgeHead() {
		return rowEdgeHead;
	}

	public void addAllEdges(String dir) {
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
				addEdge(classManager.getClassID(elements[0]),
						classManager.getClassID(elements[2]), elements[1]);
				addEdge(classManager.getClassID(elements[2]),
						classManager.getClassID(elements[0]), elements[1]);
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Num of vertices: " + getVertexNum());
		System.out.println("Num of edges:    " + getEdgeNum());
	}

	public void setProbability(String dir) {
		String line = null;
		String ASplit[] = new String[3];
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/instance_relationship_mapping.txt"));
			while (br.ready()) {
				line = br.readLine();
				ASplit = line.split(" ", 3);
				put(ASplit[0], ASplit[2]);
				put(ASplit[2], ASplit[0]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		///////////////////////////////////////////
		Iterator<Integer> it = rowEdgeHead.keySet().iterator();
		while (it.hasNext()) {
			int start = it.next();
			ProbSchemaGraphEdge current = rowEdgeHead.get(start).getNextEdge();
			while (current != null) {
				if (classManager.getInstanceNum(start) == 0) {
					current = current.getNextEdge();
					continue;
				}
				if (num_mapping.get(start + "@" + current.getEnd()) == null) {
					// System.out.println(i+"@"+current.getEnd());
					// System.out.println("null");
					current = current.getNextEdge();
					continue;
				}
				double f = (double) num_mapping.get(start + "@" + current.getEnd())
								.size() / (double) classManager.getInstanceNum(start);
				// f = f + (float)num_mapping.get(i+"@"+current.getEnd());
				current.setP(f);
//				System.out.println(100 * f);

				current = current.getNextEdge();
			}
		}
		// System.out.println(dif);

	}

	private void put(String instance1, String instance2) {
		String str = instanceManager.instance2class.get(instance1) + "@"
				+ instanceManager.instance2class.get(instance2);
		if (num_mapping.get(str) == null) {
			HashSet<String> set = new HashSet<String>();
			set.add(instance1);
			num_mapping.put(str, set);
		} else {
			HashSet<String> set = num_mapping.get(str);
			set.add(instance1);
		}
	}

	public void addEdge(int start, int end, String type) {
		ProbSchemaGraphEdge head = rowEdgeHead.get(start);
		int t = type2id.get(type);
		if (head != null) {// 表头节点,说明此时已经有一个元素了
			ProbSchemaGraphEdge ahead = head;
			ProbSchemaGraphEdge current = ahead.getNextEdge();

			while (current != null && end > current.getEnd()) {
				ahead = current;
				current = current.getNextEdge();
			}

			if (current == null) {
				ahead.setNextEdge(new ProbSchemaGraphEdge(end, t, null));
			} else {
				if (end == current.getEnd()) {
					current.addType(t);
					edgeNum--;
				} else {
					ahead.setNextEdge(new ProbSchemaGraphEdge(end, t, current));
				}
			}
		} else {
			head = new ProbSchemaGraphEdge(0, 0, new ProbSchemaGraphEdge(end,
					t, null));
			rowEdgeHead.put(start, head);
		}
		edgeNum++;
	}

	public ProbNeighborhood getNeighbors(int source) {
		ProbSchemaGraphEdge head = rowEdgeHead.get(source);
		if (head == null) {// 这是一个孤立的节点
			return null;
		}
		ProbSchemaGraphEdge re = head.getNextEdge();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ArrayList<Double> ps = new ArrayList<Double>();
		while (re != null) {
			ids.add(re.getEnd());
			ps.add(re.getP());
			re = re.getNextEdge();
		}

		int[] IDs = new int[ids.size()];
		double[] Ps = new double[ps.size()];
		for (int i = 0; i < ids.size(); i++) {
			IDs[i] = ids.get(i);
			Ps[i] = ps.get(i);
		}

		return new ProbNeighborhood(IDs, Ps);
	}

	public ProbNeighborhood getNeighbors(String source) {
		return getNeighbors(classManager.getClassID(source));
	}

	// public String[] getNeighborNames(String source) {
	// int[] temp = getNeighbors(source).getIDList();
	// String[] result = new String[temp.length];
	// for (int i = 0; i < result.length; i++) {
	// result[i] = classDB.getClassName(temp[i]);
	// }
	// return result;
	// }

	public ClassManager getClassManager() {
		return this.classManager;
	}

	public ProbSchemaGraphEdge findEdge(int start, int end) // 加了一个找边
	{
		ProbSchemaGraphEdge head = rowEdgeHead.get(start);
		ProbSchemaGraphEdge current = head.getNextEdge();
		while (true) {
			if (current == null)
				break;
			if (current.getEnd() == end)
				return current;
			current = current.getNextEdge();
		}
		return null;

	}

	public void write2file(String path)
			throws IOException {
		FileWriter writer = new FileWriter(path); 
        BufferedWriter bw = new BufferedWriter(writer);
        Iterator<Integer> itss=rowEdgeHead.keySet().iterator();
        while (itss.hasNext()){ 
        	int i = itss.next(); 
     if(rowEdgeHead.get(i)==null)
    		continue;
    	 // String str = Integer.toString(i);
    		 ProbSchemaGraphEdge current = rowEdgeHead.get(i).getNextEdge();
    		while(current!=null)
    	{
    	  String str = i+" "+current.getEnd()+" "+current.getP();
    	  bw.write(str);
    	  bw.newLine();
    	  String str1 = "";
          for(int type : current.getTypes())
       	   str1 = str1 + type + " ";
    		current = current.getNextEdge();
    		bw.write(str1);
    		bw.newLine();
    	}
  
        }
        bw.close();    
        writer.close();
	}
	
	public void load(String dir) throws IOException
	 {
		 BufferedReader br = new BufferedReader(new FileReader(dir + "/graph_memory.txt"));
		 String line = null;
			String[] ASplit = null;
			int i =1;
			int start = 0,end = 0;
			HashSet<Integer> types = new HashSet<Integer>();
			double p = 0.0;
			while (br.ready()) {
				line = br.readLine();
				ASplit = line.split(" ");
				if(i%2==1)
				{
					start = Integer.parseInt(ASplit[0]);
					end = Integer.parseInt(ASplit[1]);
					p = Double.parseDouble(ASplit[2]);
				}
				else
				{
					for(String str : ASplit)
						types.add(Integer.parseInt(str));
//							if(i==2)
//							{
//							System.out.println(start);
//							System.out.println(end);
//							System.out.println(p);
//							for(int type : types)
//							System.out.println(type);
//							}
							this.addEdge(start,end,p,types);
							types.clear();
				}
				i++;
			}
	 }
	
	public void addEdge(int start, int end, double p,HashSet<Integer> types) {
		ProbSchemaGraphEdge head = rowEdgeHead.get(start);
		if (head != null) {// 表头节点,说明此时已经有一个元素了
			ProbSchemaGraphEdge ahead = head;
			ProbSchemaGraphEdge current = ahead.getNextEdge();

			while (current != null && end > current.getEnd()) {
				ahead = current;
				current = current.getNextEdge();
			}

			if (current == null) {
				ahead.setNextEdge(new ProbSchemaGraphEdge(end, p, types, null));
			}
			else {
				if (end == current.getEnd()) {
					return;					
				}
				else {
					ahead.setNextEdge(new ProbSchemaGraphEdge(end, p, types, current));
				}
			}
		}
		else {
			head = new ProbSchemaGraphEdge(0, 0, new ProbSchemaGraphEdge(end, p, types, null));
			rowEdgeHead.put(start, head);
		}
		edgeNum++;
	}

	public static void main(String[] args) throws IOException {
		String dir = "D:/testing example";
		ClassManager classManager = new ClassManager();
		classManager.load(dir);
		InstanceManager instanceManager = new InstanceManager();
		instanceManager.load(dir, classManager);
		ProbSchemaGraph graph = new ProbSchemaGraph(classManager, instanceManager);
		graph.addAllEdges(dir);
		graph.setProbability(dir);
		graph.write2file("D://test3.txt");

		// String[] set = graph.getNeighborNames("Judge");
		// for (String str : set) {
		// System.out.println(str);
		// }
	}
}
