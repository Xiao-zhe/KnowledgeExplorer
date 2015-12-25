package notinuse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import edu.whu.clock.newgraph.ClassManager;
import edu.whu.clock.newgraph.InstanceManager;

public class ProbSchemaGraph {

	private int edgeNum;

	private ProbSchemaGraphEdge[] headEdges;
	private ClassManager classManager;
	private InstanceManager instanceManager;
//	private HashMap<String, Integer> type2id;
//	private HashMap<Integer, String> id2type;

	private HashMap<String, HashSet<String>> num_mapping = new HashMap<String, HashSet<String>>();

	public ProbSchemaGraph(ClassManager classManager,
			InstanceManager instanceManager) {
		this.classManager = classManager;
		this.instanceManager = instanceManager;
		this.headEdges = new ProbSchemaGraphEdge[classManager.getClassNum()];
//		this.type2id = new HashMap<String, Integer>();
//		this.id2type = new HashMap<Integer, String>();
	}

	public int getEdgeNum() {
		return edgeNum;
	}

//	public int getVertexNum() {
//		return headEdges.size();
//	}

//	public ProbSchemaGraphEdge[] getheadEdges() {
//		return headEdges;
//	}

	public void addAllEdges(String dir) {
		try {
//			BufferedReader br = new BufferedReader(new FileReader(dir
//					+ "/relationship_type.txt")); // begin to import the existing edge types
//			String line = null;
//			int id = 0;
//			while (br.ready()) {
//				line = br.readLine();
//				id++;
//				type2id.put(line, id);
//				id2type.put(id, line);
//			}

			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/class_relationship_mapping.txt")); // begin to import the edges between the class nodes
			String[] elements = new String[3];
			while (br.ready()) {
				String line = br.readLine();
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
//		System.out.println("Num of vertices: " + getVertexNum());
		System.out.println("Num of edges:    " + getEdgeNum());
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
//		Iterator<Short> it = headEdges.keySet().iterator();
//		while (it.hasNext()) {
//			short start = it.next();
//			ProbSchemaGraphEdge current = headEdges.get(start).getNextEdge();
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

	public void addEdge(short start, short end, String type) {
		ProbSchemaGraphEdge head = headEdges[start];
//		int t = type2id.get(type);
		if (head != null) {// 表头节点,说明此时已经有一个元素了
			ProbSchemaGraphEdge ahead = head;
			ProbSchemaGraphEdge current = ahead.getNextEdge();

			while (current != null && end > current.getEnd()) {
				ahead = current;
				current = current.getNextEdge();
			}

			if (current == null) {
				ahead.setNextEdge(new ProbSchemaGraphEdge(end, null));
//				ahead.setNextEdge(new ProbSchemaGraphEdge(end, t, null));
			} else {
				if (end == current.getEnd()) {
//					current.addType(t);
					edgeNum--;
				} else {
					ahead.setNextEdge(new ProbSchemaGraphEdge(end, current));
//					ahead.setNextEdge(new ProbSchemaGraphEdge(end, t, current));
				}
			}
		} else {
			head = new ProbSchemaGraphEdge((short)0, new ProbSchemaGraphEdge(end, null));
//			head = new ProbSchemaGraphEdge(0, 0, new ProbSchemaGraphEdge(end,
//					t, null));
			headEdges[start] = head;
		}
		edgeNum++;
	}

	public ProbNeighborhood getNeighbors(short source) {
		ProbSchemaGraphEdge head = headEdges[source];
		if (head == null) {// 这是一个孤立的节点
			return null;
		}
		ProbSchemaGraphEdge re = head.getNextEdge();
		ArrayList<Short> ids = new ArrayList<Short>();
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
		ProbSchemaGraphEdge head = headEdges[start];
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
        for (short itss = 0; itss < headEdges.length; itss++) {
        	if(headEdges[itss]==null)
    		continue;
    	 // String str = Integer.toString(i);
    		 ProbSchemaGraphEdge current = headEdges[itss].getNextEdge();
    		while(current!=null)
    	{
    	  String str = itss+" "+current.getEnd()+" ";
    	  bw.write(str);
    	  bw.newLine();
//    	  String str1 = "";
//          for(int type : current.getTypes())
//       	   str1 = str1 + type + " ";
//    		current = current.getNextEdge();
//    		bw.write(str1);
//    		bw.newLine();
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
			short start = 0,end = 0;
			HashSet<Integer> types = new HashSet<Integer>();
			double p = 0.0;
			while (br.ready()) {
				line = br.readLine();
				ASplit = line.split(" ");
				if(i%2==1)
				{
					start = Short.parseShort(ASplit[0]);
					end = Short.parseShort(ASplit[1]);
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
	
	public void addEdge(short start, short end, double p,HashSet<Integer> types) {
		ProbSchemaGraphEdge head = headEdges.get(start);
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
			headEdges.put(start, head);
		}
		edgeNum++;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
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
