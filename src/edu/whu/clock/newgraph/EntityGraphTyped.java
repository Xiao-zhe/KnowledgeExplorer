package edu.whu.clock.newgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class EntityGraphTyped implements Serializable {

	private static final long serialVersionUID = 5945391136277353744L;

	private int edgeNum;

	private EntityGraphEdgeTyped[][] allEdges;  //allEdges[id] means all endpoints with id as starting point
	private InstanceManager instanceManager;
	private HashMap<String, Short> type2id;
	private HashMap<Short, String> id2type;

	public EntityGraphTyped(InstanceManager instanceManager) {
		this.edgeNum = 0;
		this.instanceManager = instanceManager;
		this.allEdges = new EntityGraphEdgeTyped[instanceManager
				.getInstanceNum()][];
		this.type2id = new HashMap<String, Short>();
		this.id2type = new HashMap<Short, String>();
	}

	public int getEdgeNum() {
		return edgeNum;
	}

	public int getNodeNum() {
		return instanceManager.getInstanceNum();
	}

	public void addAllEdges(String dir) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/relationship_type.txt")); // begin to import the
													// existing edge types
			String line = null;
			short id = 0;
			while (br.ready()) {
				line = br.readLine();
				id++;
				type2id.put(line, id);
				id2type.put(id, line);
			}
			br.close();

			ArrayList<TreeSet<EntityGraphEdgeTyped>> temp = new ArrayList<TreeSet<EntityGraphEdgeTyped>>(
					instanceManager.getInstanceNum());
			for (int i = 0; i < instanceManager.getInstanceNum(); i++) {
				temp.add(new TreeSet<EntityGraphEdgeTyped>());
			}

			br = new BufferedReader(new FileReader(dir
					+ "/instance_relationship_mapping.txt")); // begin to import
																// the edges
																// between the
																// class nodes
			String[] elements = null;
			EntityGraphEdgeTyped edge = null;
			TreeSet<EntityGraphEdgeTyped> set = null;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ");
				edge = new EntityGraphEdgeTyped(
						instanceManager.getInstanceID(elements[2]),
						type2id.get(elements[1]), true);
				set = temp.get(instanceManager.getInstanceID(elements[0]));
				if (set.contains(edge)) { // duplicated lines exist
					continue;
				}
				set.add(edge);
				edge = new EntityGraphEdgeTyped(
						instanceManager.getInstanceID(elements[0]),
						type2id.get(elements[1]), false);
				set = temp.get(instanceManager.getInstanceID(elements[2]));
				set.add(edge);
				edgeNum++;
			}
			br.close();

			for (int i = 0; i < instanceManager.getInstanceNum(); i++) {
				allEdges[i] = new EntityGraphEdgeTyped[temp.get(i).size()];
				Iterator<EntityGraphEdgeTyped> it = temp.get(i).iterator();
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

	public EntityGraphEdgeTyped[] getNeighbors(int start) {
		return allEdges[start];
	}

	public EntityGraphEdgeTyped[] getNeighbors(String source) {
		return getNeighbors(instanceManager.getInstanceID(source));
	}

	public InstanceManager getInstanceManager() {
		return this.instanceManager;
	}
	
	public boolean haveEdgeBetweenInstanceAndClass(int instanceID, int classID) {
		EntityGraphEdgeTyped[] neighboor = allEdges[instanceID];
		for (EntityGraphEdgeTyped edge : neighboor) {
			if (classID == instanceManager.getClassID(edge.getEnd())) {
				return true;
			}
		}
		return false;
	}
	public int edgeNumBetweeTwoClass(short classA,short classB){
		int[] instancesA = instanceManager.getInstanceSet(classA);
		int num = 0;
		for(int insA: instancesA){	
			EntityGraphEdgeTyped[] neighboor = allEdges[insA];
			for (EntityGraphEdgeTyped edge : neighboor) {
				if (classB == instanceManager.getClassID(edge.getEnd())) {
					num++;
				}
			}
		}
		return num;
	}

	// public ProbSchemaGraphEdge findEdge(int start, int end) // ����һ���ұ�
	// {
	// ProbSchemaGraphEdge head = allEdges[start];
	// ProbSchemaGraphEdge current = head.getNextEdge();
	// while (true) {
	// if (current == null)
	// break;
	// if (current.getEnd() == end)
	// return current;
	// current = current.getNextEdge();
	// }
	// return null;
	//
	// }

	// public void write2file(String dir)
	// {
	// try {
	// BufferedWriter bw = new BufferedWriter(new FileWriter(dir));
	// for (int i = 0; i < allEdges.length; i++) {
	// if(allEdges[i]==null)
	// continue;
	// for (int j = 0; j < allEdges[i].length; j++) {
	// EntityGraphEdgeTyped current = allEdges[i][j];
	// String str = i+" "+current.getEnd()+" " + current.getType() + " " +
	// current.isOut();
	// bw.write(str);
	// bw.newLine();
	// }
	//
	// }
	// bw.close();
	// }
	// catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// }
	//
	// public void load(String dir)
	// {
	// try {
	// BufferedReader br = new BufferedReader(new FileReader(dir +
	// "/graph_memory.txt"));
	// String line = null;
	// String[] ASplit = null;
	// short start = 0,end = 0 , type = 0;
	// boolean out = false;
	// ArrayList<EntityGraphEdgeTyped> list = null;
	// short last = -1;
	// while (br.ready()) {
	// line = br.readLine();
	// ASplit = line.split(" ");
	// start = Short.parseShort(ASplit[0]);
	// end = Short.parseShort(ASplit[1]);
	// type = Short.parseShort(ASplit[2]);
	// out = Boolean.parseBoolean(ASplit[1]);
	// if (last != start) {
	// if (list != null) {
	// allEdges[last] = list.toArray(new EntityGraphEdgeTyped[list.size()]);
	// }
	// list = new ArrayList<EntityGraphEdgeTyped>();
	// last = start;
	// }
	// list.add(new EntityGraphEdgeTyped(end, type ,out));
	// }
	// br.close();
	// }
	// catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// }

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		// String dir = "D:/testing example";
		String dir = "D:/dbpedia/clean";
		long t0 = System.currentTimeMillis() / 1000;
		ClassManager classManager = new ClassManager();
		classManager.load(dir);
		InstanceManager instanceManager = new InstanceManager();
		instanceManager.load(dir, classManager);

		EntityGraphTyped graph = new EntityGraphTyped(instanceManager);
		graph.addAllEdges(dir);
		// graph.write2file("D://test3.txt");
		// FileOutputStream fos = new
		// FileOutputStream("D:/SerializedFile/EntityGraphTyped.ser");
		// ObjectOutputStream oos = new ObjectOutputStream(fos);
		// oos.writeObject(graph);
		// oos.close();
		long t1 = System.currentTimeMillis() / 1000;
		System.out.println("addAllEdgesʱ�䣺" + (t1 - t0) + "��");// 19seconds
		// FileInputStream fis = new FileInputStream(
		// "D:/SerializedFile/EntityGraphTyped.ser");
		// ObjectInputStream ois = new ObjectInputStream(fis);
		// EntityGraphTyped graphTyped = (EntityGraphTyped) ois.readObject();
		// ois.close();
		// long t2 = System.currentTimeMillis()/1000;
		// System.out.println("�����л�ʱ�䣺"+(t2-t1)+"��");//57seconds
		EntityGraphEdgeTyped[] set = graph.getNeighbors("Ninewells_Hospital");
		for (EntityGraphEdgeTyped str : set) {
			System.out.println(instanceManager.getInstanceName(str.getEnd()));
		}
	}
}
