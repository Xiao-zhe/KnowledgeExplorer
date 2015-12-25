package notinuse;

package edu.whu.clock.newprobindex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import notinuse.EntityGraph;
import notinuse.IndexedEdge;
import notinuse.PKIndexEntry;
import notinuse.PKIndexEntryBinding;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import edu.whu.clock.newgraph.ClassManager;
import edu.whu.clock.newgraph.EntityGraphEdgeTyped;
import edu.whu.clock.newgraph.EntityGraphTyped;
import edu.whu.clock.newgraph.GraphManager;
import edu.whu.clock.newgraph.InstanceManager;

public class PKIndexManager {
	
	public static final String KEYS_FILE = "D:/experiment data/knowledge graph explorer/dbpedia-old/keys/";

	private Environment env;
	private Database pkIndex;
	private PKIndexEntryBinding ieBinding;
	private PKIndexTypedEntryBinding typedBinding;
	private final String dbName = "pkindex";

	public PKIndexManager(String dbEnvPath) {
		ieBinding = new PKIndexEntryBinding();
		typedBinding = new PKIndexTypedEntryBinding();
		try {
			File f = new File(dbEnvPath);
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setCachePercent(75);
			env = new Environment(f, envConfig);
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			pkIndex = env.openDatabase(null, dbName, dbConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PKIndexTypedEntry get(String keyword) {
		PKIndexTypedEntry result = null;
		try {
			DatabaseEntry key = new DatabaseEntry(keyword.toLowerCase()
					.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			if (pkIndex.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = typedBinding.entryToObject(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public PKIndexTypedEntry getEntryTyped(String keyword) {
		PKIndexTypedEntry result = null;
		try {
			DatabaseEntry key = new DatabaseEntry(keyword.toLowerCase()
					.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			if (pkIndex.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = typedBinding.entryToObject(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	// public String[] getClasses(String keyword) {
	// int[] temp = get(keyword).getClassIDList();
	// String[] result = new String[temp.length];
	// for (int i = 0; i < result.length; i++) {
	// result[i] = classManager.getClassName(temp[i]);
	// }
	// return result;
	// }

	public void build(String dir, ClassManager classManager,
			InstanceManager instanceManager, EntityGraph entityGraph) {
		HashMap<String, HashMap<IndexedEdge, Integer>> map = new HashMap<String, HashMap<IndexedEdge, Integer>>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/instance_attribute_mapping.txt"));
			String line = null;
			String[] elements = null;
			// int[] neighborEdges;
			int[] neighbor2cla;

			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 4);
				if (elements[3].endsWith("@en")
						&& !elements[2].startsWith("<http://www")) {
					int classID = classManager.getClassID(elements[0]);
					int[] neighborEdges = entityGraph.getNeighbors(elements[1]);
					if (neighborEdges == null) {
						continue;
					}
					neighbor2cla = new int[neighborEdges.length];

					for (int j = 0; j < neighborEdges.length; j++) {
						neighbor2cla[j] = instanceManager
								.getClassID(neighborEdges[j]);
					}
					HashSet<Integer> indexEdges = new HashSet<Integer>();
					for (int q : neighbor2cla) {
						indexEdges.add(q);
					}

					// Arrays.sort(neighbor2cla);

					String[] terms = elements[3].substring(1,
							elements[3].lastIndexOf("\"")).split(" ");
					for (String term : terms) {
						term = term.toLowerCase();
						if (term.length() > 0) {
							if (map.containsKey(term)) {
								HashMap<IndexedEdge, Integer> edgeMap = map
										.get(term);
								for (Integer i : indexEdges) {
									int j = i;
									IndexedEdge e = new IndexedEdge(
											(short) classID, (short) j);
									if (edgeMap.containsKey(e)) {
										int tmp = edgeMap.get(e);
										tmp++;
										edgeMap.put(e, tmp);
									} else {
										edgeMap.put(e, 1);
									}
								}
								map.put(term, edgeMap);
							} else {
								HashMap<IndexedEdge, Integer> te = new HashMap<IndexedEdge, Integer>();
								for (Integer i : indexEdges) {
									int m = i;
									IndexedEdge e = new IndexedEdge(
											(short) classID, (short) m);
									te.put(e, 1);
								}
								map.put(term, te);
							}

						}

					}
				}
			}
			br.close();
			for (String term : map.keySet()) {
				
				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
				HashMap<IndexedEdge, Integer> ei = new HashMap<IndexedEdge, Integer>();
				ei = map.get(term);
				IndexedEdge[] edgeList = new IndexedEdge[ei.size()];
				double[] proList = new double[ei.size()];

				Iterator iter = ei.entrySet().iterator();
				int i = 0;
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					IndexedEdge k = (IndexedEdge) entry.getKey();
					Integer val = (Integer) entry.getValue();
					edgeList[i] = k;
					int g = k.getStart();
					int vnum = classManager.getInstanceNum(k.getStart());
					proList[i++] = (double) val / vnum;
				}
				int m, n, exchange;
				double tmp;
				IndexedEdge edt;
				for (m = 0; m < proList.length - 1; m++) {
					exchange = 0;
					for (n = proList.length - 1; n > m; n--) {
						if (proList[n] < proList[n - 1]) {
							tmp = proList[n];
							proList[n] = proList[n - 1];
							proList[n - 1] = tmp;
							edt = edgeList[n];
							edgeList[n] = edgeList[n - 1];
							edgeList[n - 1] = edt;
							exchange = 1;
						}
					}
					if (exchange == 0) {
						break;
					}
				}
				PKIndexEntry pk = new PKIndexEntry(edgeList, proList);
				DatabaseEntry value = new DatabaseEntry();
				ieBinding.objectToEntry(pk, value);
				pkIndex.put(null, key, value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void build(String dir, ClassManager classManager,
			InstanceManager instanceManager, EntityGraphTyped entityGraph) {
		HashMap<String, TreeMap<IndexedEdgeTyped, Integer>> map = new HashMap<String, TreeMap<IndexedEdgeTyped, Integer>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/instance_attribute_mapping.txt"));
			String line = null;
			String[] elements = null;
			String lastEntity = null;
			HashSet<String> termSet = new HashSet<String>();
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 4);
				if (elements[2].startsWith("<http://www") || !elements[3].endsWith("@en")) {
					continue;
				}
				if (lastEntity != null && !lastEntity.equals(elements[1])) {
					
					short classID = instanceManager.getClassID(lastEntity);
					EntityGraphEdgeTyped[] neighborEdges = entityGraph
							.getNeighbors(lastEntity);
					if (neighborEdges == null) {
						continue;
					}
					HashSet<IndexedEdgeTyped> indexEdges = new HashSet<IndexedEdgeTyped>(); // To
																							// collect
																							// all
																							// unique
																							// index
																							// edges.
					for (EntityGraphEdgeTyped edge : neighborEdges) {
						indexEdges.add(new IndexedEdgeTyped(classID,  /////////////////////////////////////////////////////////
								instanceManager.getClassID(edge.getEnd()), edge
										.getType(), edge.isOut()));
					}

					for (String term : termSet) {////????????????????????????????????????????????????????????????????????????????????????
						if (map.containsKey(term)) {
							TreeMap<IndexedEdgeTyped, Integer> edgeMap = map
									.get(term);
							for (IndexedEdgeTyped edge : indexEdges) {
								if (edgeMap.containsKey(edge)) {
									int count = edgeMap.get(edge);
									edgeMap.put(edge, count + 1);
								} else {
									edgeMap.put(edge, 1);
								}
							}
							// map.put(term, edgeMap);
						} else {
							TreeMap<IndexedEdgeTyped, Integer> edgeMap = new TreeMap<IndexedEdgeTyped, Integer>();
							for (IndexedEdgeTyped i : indexEdges) {
								edgeMap.put(i, 1);
							}
							map.put(term, edgeMap);
						}
					}
					termSet = new HashSet<String>();
				}
				String[] terms = elements[3].substring(1,
						elements[3].lastIndexOf("\"")).split(" ");
				for (String term : terms) {
					term = term.toLowerCase();
					if (term.length() > 0) {
						termSet.add(term);
					}
				}
				lastEntity = elements[1];
			}
			br.close();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(GraphManager.KEYWORD_FILE));
			for (String term : map.keySet()) {
				bw.write(term);//�����йؼ���д���ļ�
				bw.newLine();
				
				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
				TreeMap<IndexedEdgeTyped, Integer> ei = null;
				ei = map.get(term);
				IndexedEdgeTyped[] edgeList = new IndexedEdgeTyped[ei.size()];
				Iterator<Entry<IndexedEdgeTyped, Integer>> iter = ei.entrySet()
						.iterator();
				int i = 0;
				while (iter.hasNext()) {
					Entry<IndexedEdgeTyped, Integer> entry = iter.next();
					IndexedEdgeTyped k = entry.getKey();
					double val = entry.getValue();
					double vnum = classManager.getInstanceNum(k.getStart());
					k.setProb(val / vnum);
					edgeList[i] = k;
					i++;
				}
				PKIndexTypedEntry pk = new PKIndexTypedEntry(edgeList);
				DatabaseEntry value = new DatabaseEntry();
				typedBinding.objectToEntry(pk, value);
				pkIndex.put(null, key, value);
			}
			bw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
    
	
	
	public void close() {
		try {
			if (pkIndex != null) {
				pkIndex.close();
			}
			if (env != null) {
				env.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
