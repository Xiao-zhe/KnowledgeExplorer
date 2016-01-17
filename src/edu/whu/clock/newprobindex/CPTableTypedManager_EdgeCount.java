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
	import edu.whu.clock.newgraph.SummaryGraphEdgeTyped;

	public class CPTableTypedManager_EdgeCount {

		private HashMap<String, Double> cpTable;

		public void build(EntityGraphTyped entityGraph) {
			cpTable = new HashMap<String, Double>();
			HashMap<String, HashSet<Integer>> map1 = new HashMap<String, HashSet<Integer>>(); // statistics
																								// of
																								// one-hop
																								// paths
			HashMap<String, HashSet<Integer>> map2 = new HashMap<String, HashSet<Integer>>(); // statistics
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
						map1.get(id).add(i);
						// int count = map1.get(id) + 1;
						// map1.put(id, count);
					} else {
						HashSet<Integer> set = new HashSet<Integer>();
						set.add(i);
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
							map2.get(id).add(i);
							// double count = cpTable.get(id) + 1;
							// cpTable.put(id, count);
						} else {
							HashSet<Integer> set = new HashSet<Integer>();
							set.add(i);
							map2.put(id, set);
							// cpTable.put(id, 1.0d);
						}
					}
				}
				if (i % 10000 == 0) {
					System.out.print("#" + i + " is done.");
				}
			}
			Iterator<Entry<String, HashSet<Integer>>> iterator = map2.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, HashSet<Integer>> entry = iterator.next();
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

		public void buildFastly(EntityGraphTyped entityGraph) {
			HashMap<String, HashSet<Integer>> map1 = new HashMap<String, HashSet<Integer>>(); // statistics
																								// of
																								// one-hop
																								// paths
			HashMap<String, HashSet<Integer>> map2 = new HashMap<String, HashSet<Integer>>(); // statistics
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
				System.out.print("#" + i + ": " + neighborEdges.length
						+ " entity edges --> ");
				HashSet<SummaryGraphEdgeTyped> edges = new HashSet<SummaryGraphEdgeTyped>();
				short bClass = entityGraph.getInstanceManager().getClassID(i);
				for (EntityGraphEdgeTyped edge : neighborEdges) {
					SummaryGraphEdgeTyped newEdge = new SummaryGraphEdgeTyped(
							entityGraph.getInstanceManager().getClassID(
									edge.getEnd()), edge.getType(), edge.isOut());
					edges.add(newEdge);
				}
				System.out.print(edges.size() + " class edges");
				for (SummaryGraphEdgeTyped edge1 : edges) {
					short aClass = edge1.getEnd();
					String id = aClass + "#" + edge1.getType() + "#"
							+ !edge1.isOut() + "#" + bClass;
					if (map1.containsKey(id)) {
						map1.get(id).add(i);
						// int count = map1.get(id) + 1;
						// map1.put(id, count);
					} else {
						HashSet<Integer> set = new HashSet<Integer>();
						set.add(i);
						map1.put(id, set);
						// map1.put(id, 1);
					}
					for (SummaryGraphEdgeTyped edge2 : edges) {
						if (edge2.getEnd() == edge1.getEnd()) { // edge1 and edge2
																// are the same
																// edge.
							continue;
						}
						short cClass = edge2.getEnd();
						id = aClass + "#" + edge1.getType() + "#" + !edge1.isOut()
								+ "#" + bClass + "@" + edge2.getType() + "#"
								+ edge2.isOut() + "#" + cClass;
						if (map2.containsKey(id)) {
							map2.get(id).add(i);
							// double count = cpTable.get(id) + 1;
							// cpTable.put(id, count);
						} else {
							HashSet<Integer> set = new HashSet<Integer>();
							set.add(i);
							map2.put(id, set);
							// cpTable.put(id, 1.0d);
						}
					}
				}
				System.out.println(" ---- done.");
			}
			Iterator<Entry<String, HashSet<Integer>>> iterator = map2.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, HashSet<Integer>> entry = iterator.next();
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
						+ "/cptable_file.txt"));
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
//				br = new BufferedReader(new FileReader(dir + "/cptable_file.txt"));
				br = new BufferedReader(new FileReader(dir + "/edge_cptable_file.txt"));  //new cptable index
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
			CPTableTypedManager_EdgeCount cpt = new CPTableTypedManager_EdgeCount();
			cpt.loadFromFile(GraphManager.CPTABLE_FILE_DIR);
			System.out.println(cpt.getProbability((short) 56, (short) 123, true,
					(short) 69, (short) 63, false, (short) 226));
		}
	}


