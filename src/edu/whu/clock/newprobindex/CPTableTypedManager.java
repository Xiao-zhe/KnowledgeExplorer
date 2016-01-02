package edu.whu.clock.newprobindex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import edu.whu.clock.newgraph.EntityGraphEdgeTyped;
import edu.whu.clock.newgraph.EntityGraphTyped;
import edu.whu.clock.newgraph.GraphManager;

public class CPTableTypedManager {

	private HashMap<String, Double> cpTable;

	public void build(EntityGraphTyped entityGraph) {
		cpTable = new HashMap<String, Double>();
		HashMap<String, HashSet<EntityGraphEdgeTyped>> map1 = new HashMap<String, HashSet<EntityGraphEdgeTyped>>(); // statistics
																							// of
																							// one-hop
																							// paths
		HashMap<String, HashSet<EntityGraphEdgeTyped>> map2 = new HashMap<String, HashSet<EntityGraphEdgeTyped>>(); // statistics
																							// of
																							// two-hop
																							// paths
		for (int i = 0; i < entityGraph.getNodeNum(); i++) {
			EntityGraphEdgeTyped[] neighborEdges = entityGraph.getNeighbors(i);
			if (neighborEdges == null) {
				System.out.println("Notice: the entity node #" + i
						+ " is isolated.");
				continue;      
			}
			short bClass = entityGraph.getInstanceManager().getClassID(i);
			for (EntityGraphEdgeTyped edge1 : neighborEdges) {
				short aClass = entityGraph.getInstanceManager().getClassID(
						edge1.getEnd());
				String id = aClass + "#" + edge1.getType() + "#"
						+ !edge1.isOut() + "#" + bClass;
				if (map1.containsKey(id)) {
					map1.get(id).add(edge1);
					// int count = map1.get(id) + 1;
					// map1.put(id, count);
				} else {
					HashSet<EntityGraphEdgeTyped> set = new HashSet<EntityGraphEdgeTyped>();
					set.add(edge1);
					map1.put(id, set);
					
					
					// map1.put(id, 1);
				}
				for (EntityGraphEdgeTyped edge2 : neighborEdges) {
					if (edge2.getEnd() == edge1.getEnd()) { // edge1 and edge2
															// are the same
															// edge.
						continue;
					}
					short cClass = entityGraph.getInstanceManager().getClassID(
							edge2.getEnd());
					id = aClass + "#" + edge1.getType() + "#" + !edge1.isOut()
							+ "#" + bClass + "@" + edge2.getType() + "#"
							+ edge2.isOut() + "#" + cClass;
					if (map2.containsKey(id)) {
						map2.get(id).add(edge1);
						// double count = cpTable.get(id) + 1;
						// cpTable.put(id, count);
					} else {
						HashSet<EntityGraphEdgeTyped> set = new HashSet<EntityGraphEdgeTyped>();
						set.add(edge1);
						map2.put(id, set);
						// cpTable.put(id, 1.0d);
					}
					if(id.equals("1#5#false#0@4#false#0")){
						String edge11 = edge1.getEnd()+""+edge1.getType()+edge1.isOut()+"";
						System.out.println(edge11);
				}
				}
			}
			if (i % 100 == 0) {
				System.out.print("#" + i + " is done.");
			}
		}
		Iterator<Entry<String, HashSet<EntityGraphEdgeTyped>>> iterator = map2.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, HashSet<EntityGraphEdgeTyped>> entry = iterator.next();
			String id1 = entry.getKey();
			double count1 = entry.getValue().size();
			String id2 = id1.substring(0, id1.indexOf("@"));
			double count2 = map1.get(id2).size();
			double prob = count1 / count2;
			if (prob > 1.0d) {
				System.out.println("Error: the CP of the two-hop path <" + id1
						+ "> is " + prob);
				continue;
			}
			cpTable.put(id1, prob);
		}
	}

	public double getProbability(String key) {
		if (cpTable.containsKey(key))
			return cpTable.get(key);
		else
			return 0.0d;
	}

	public double getProbability(short v1, short t1, boolean d1, short v2,
			short t2, boolean d2, short v3) {
		String key = v1 + "#" + t1 + "#" + d1 + "#" + v2 + "@" + t2 + "#" + d2
				+ "#" + v3;
		return getProbability(key);
	}

	public void writeToFile(String dir) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(dir
					+ "/edge_cptable_file.txt"));
			Iterator<Entry<String, Double>> iterator = cpTable.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, Double> entry = iterator.next();
				bw.write(entry.getKey() + "\t" + entry.getValue());
				bw.newLine();
			}
			bw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void loadFromFile(String dir) {
		cpTable = new HashMap<String, Double>();
		BufferedReader br = null;
		try {
//			br = new BufferedReader(new FileReader(dir + "/cptable_file2.txt"));
			br = new BufferedReader(new FileReader(dir + "/edge_cptable_file.txt"));
			String line = null;
			String[] elements = null;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split("\t");
				cpTable.put(elements[0], Double.parseDouble(elements[1]));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		CPTableTypedManager cpt = new CPTableTypedManager();
//		cpt.loadFromFile(GraphManager.CPTABLE_FILE_DIR);
		
		System.out.println(cpt.getProbability((short) 56, (short) 123, true,
				(short) 69, (short) 63, false, (short) 226));
	}
}
