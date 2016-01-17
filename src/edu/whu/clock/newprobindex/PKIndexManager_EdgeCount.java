package edu.whu.clock.newprobindex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import notinuse.PKIndexEntryBinding;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import edu.whu.clock.graphsearch.util.SequentialIntArray;
import edu.whu.clock.graphsearch.util.SequentialIntArrayBinding;
import edu.whu.clock.kgraphsearch.index.IndexedEdge_KG_ET;
import edu.whu.clock.kgraphsearch.index.KeywordIndexEntryBinding_KG_ET;
import edu.whu.clock.kgraphsearch.index.KeywordIndexEntry_KG_ET;
import edu.whu.clock.newgraph.ClassManager;
import edu.whu.clock.newgraph.EntityGraphEdgeTyped;
import edu.whu.clock.newgraph.EntityGraphTyped;
import edu.whu.clock.newgraph.GraphManager;
import edu.whu.clock.newgraph.InstanceManager;

public class PKIndexManager_EdgeCount {

	private Environment env;
	private Database edgeProbIndexOnSG;
	private Database edgeIndexOnKG;
	private Database nodeIndexOnKG;
	private final String edgeProbIndexOnSGDBName = "sg edge prob index";
	private final String edgeIndexOnKGDBName = "kg edge index";
	private final String nodeIndexOnKGDBName = "kg node index";
	private PKIndexEntryBinding ieBinding;
	private PKIndexTypedEntryBinding typedBinding;
	private KeywordIndexEntryBinding_KG_ET kieBinding;
	private SequentialIntArrayBinding siaBinding;

	public PKIndexManager_EdgeCount(String dbEnvPath) {
		ieBinding = new PKIndexEntryBinding();
		typedBinding = new PKIndexTypedEntryBinding();
		kieBinding = new KeywordIndexEntryBinding_KG_ET();
		siaBinding = new SequentialIntArrayBinding();
		try {
			File f = new File(dbEnvPath);
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setCachePercent(75);
			env = new Environment(f, envConfig);
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			edgeProbIndexOnSG = env.openDatabase(null, edgeProbIndexOnSGDBName, dbConfig);
			edgeIndexOnKG = env.openDatabase(null, edgeIndexOnKGDBName, dbConfig);
			nodeIndexOnKG = env.openDatabase(null, nodeIndexOnKGDBName, dbConfig);
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
			if (edgeProbIndexOnSG.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = typedBinding.entryToObject(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public PKIndexTypedEntry getEdgesOnSG(String keyword) {
		PKIndexTypedEntry result = null;
		try {
			DatabaseEntry key = new DatabaseEntry(keyword.toLowerCase()
					.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			if (edgeProbIndexOnSG.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = typedBinding.entryToObject(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public KeywordIndexEntry_KG_ET getEdgesOnKG(String keyword) {
		KeywordIndexEntry_KG_ET result = null;
		try {
			DatabaseEntry key = new DatabaseEntry(keyword.toLowerCase()
					.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			if (edgeIndexOnKG.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = kieBinding.entryToObject(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public int[] getNodesOnKG(String keyword) {  //get the IDs of instance from attribute-instance index 
		int[] result = null;
		try {
			DatabaseEntry key = new DatabaseEntry(keyword.toLowerCase()
					.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			if (nodeIndexOnKG.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = siaBinding.entryToObject(value).getArray();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

//	public void build(String dir, ClassManager classManager,
//			InstanceManager instanceManager, EntityGraph entityGraph) {
//		HashMap<String, HashMap<IndexedEdge, Integer>> map = new HashMap<String, HashMap<IndexedEdge, Integer>>();
//
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(dir
//					+ "/instance_attribute_mapping.txt"));
//			String line = null;
//			String[] elements = null;
//			// int[] neighborEdges;
//			int[] neighbor2cla;
//
//			while (br.ready()) {
//				line = br.readLine();
//				elements = line.split(" ", 4);
//				if (elements[3].endsWith("@en")
//						&& !elements[2].startsWith("<http://www")) {
//					int classID = classManager.getClassID(elements[0]);
//					int[] neighborEdges = entityGraph.getNeighbors(elements[1]);
//					if (neighborEdges == null) {
//						continue;
//					}
//					neighbor2cla = new int[neighborEdges.length];
//
//					for (int j = 0; j < neighborEdges.length; j++) {
//						neighbor2cla[j] = instanceManager
//								.getClassID(neighborEdges[j]);
//					}
//					HashSet<Integer> indexEdges = new HashSet<Integer>();
//					for (int q : neighbor2cla) {
//						indexEdges.add(q);
//					}
//
//					// Arrays.sort(neighbor2cla);
//
//					String[] terms = elements[3].substring(1,
//							elements[3].lastIndexOf("\"")).split(" ");
//					for (String term : terms) {
//						term = term.toLowerCase();
//						if (term.length() > 0) {
//							if (map.containsKey(term)) {
//								HashMap<IndexedEdge, Integer> edgeMap = map
//										.get(term);
//								for (Integer i : indexEdges) {
//									int j = i;
//									IndexedEdge e = new IndexedEdge(
//											(short) classID, (short) j);
//									if (edgeMap.containsKey(e)) {
//										int tmp = edgeMap.get(e);
//										tmp++;
//										edgeMap.put(e, tmp);
//									} else {
//										edgeMap.put(e, 1);
//									}
//								}
//								map.put(term, edgeMap);
//							} else {
//								HashMap<IndexedEdge, Integer> te = new HashMap<IndexedEdge, Integer>();
//								for (Integer i : indexEdges) {
//									int m = i;
//									IndexedEdge e = new IndexedEdge(
//											(short) classID, (short) m);
//									te.put(e, 1);
//								}
//								map.put(term, te);
//							}
//
//						}
//
//					}
//				}
//			}
//			br.close();
//			for (String term : map.keySet()) {
//				
//				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
//				HashMap<IndexedEdge, Integer> ei = new HashMap<IndexedEdge, Integer>();
//				ei = map.get(term);
//				IndexedEdge[] edgeList = new IndexedEdge[ei.size()];
//				double[] proList = new double[ei.size()];
//
//				Iterator iter = ei.entrySet().iterator();
//				int i = 0;
//				while (iter.hasNext()) {
//					Map.Entry entry = (Map.Entry) iter.next();
//					IndexedEdge k = (IndexedEdge) entry.getKey();
//					Integer val = (Integer) entry.getValue();
//					edgeList[i] = k;
//					int g = k.getStart();
//					int vnum = classManager.getInstanceNum(k.getStart());
//					proList[i++] = (double) val / vnum;
//				}
//				int m, n, exchange;
//				double tmp;
//				IndexedEdge edt;
//				for (m = 0; m < proList.length - 1; m++) {
//					exchange = 0;
//					for (n = proList.length - 1; n > m; n--) {
//						if (proList[n] < proList[n - 1]) {
//							tmp = proList[n];
//							proList[n] = proList[n - 1];
//							proList[n - 1] = tmp;
//							edt = edgeList[n];
//							edgeList[n] = edgeList[n - 1];
//							edgeList[n - 1] = edt;
//							exchange = 1;
//						}
//					}
//					if (exchange == 0) {
//						break;
//					}
//				}
//				PKIndexEntry pk = new PKIndexEntry(edgeList, proList);
//				DatabaseEntry value = new DatabaseEntry();
//				ieBinding.objectToEntry(pk, value);
//				edgeProbIndexOnSG.put(null, key, value);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
	
	private void procAnEntity(String lastEntity, 
			InstanceManager instanceManager, 
			EntityGraphTyped entityGraph, 
			HashMap<String, TreeMap<IndexedEdgeTyped, Integer>> sgMap, 
			HashMap<String, TreeSet<IndexedEdge_KG_ET>> kgMap, 
			HashMap<String, SequentialIntArray> map, 
			HashSet<String> termSet) {
		short classID = instanceManager.getClassID(lastEntity);
		int entityID = instanceManager.getInstanceID(lastEntity);
		EntityGraphEdgeTyped[] neighborEdges = entityGraph
				.getNeighbors(entityID);
		if (neighborEdges == null) {
			return;
		}
		// indexEdges收集所有summary graph上与classID节点相连的边及其实例个数
		TreeMap<IndexedEdgeTyped, Integer>  indexEdges = new TreeMap<IndexedEdgeTyped, Integer>();
		// indexedEdgeOnKG收集所有knowledge graph上与entityID节点相连的边
		TreeSet<IndexedEdge_KG_ET> indexedEdgeOnKG = new TreeSet<IndexedEdge_KG_ET>();
		// 下面对与entityID节点相连的边逐一生成索引边
		for (EntityGraphEdgeTyped edge : neighborEdges) {
			// 增加一条summary graph上的索引边
			IndexedEdgeTyped newEdge = new IndexedEdgeTyped(classID,
					instanceManager.getClassID(edge.getEnd()), edge
					.getType(), edge.isOut());
			if (indexEdges.containsKey(newEdge)) {
				indexEdges.put(newEdge, indexEdges.get(newEdge) + 1);
			}
			else {
				indexEdges.put(newEdge, 1);
			}
			// 增加一条knowledge graph上的索引边
			indexedEdgeOnKG.add(new IndexedEdge_KG_ET(entityID, edge.getEnd(), edge.getType(), edge.isOut()));
		}
		// 对lastEntity所包含的所有关键词逐一处理
		for (String term : termSet) {
			if (sgMap.containsKey(term)) {
				// 更新关键词term所对应的summary graph上索引边集合及计数
				TreeMap<IndexedEdgeTyped, Integer> edgeMap = sgMap
						.get(term);
				for (IndexedEdgeTyped edge : indexEdges.keySet()) {
					if (edgeMap.containsKey(edge)) {
						int count1 = edgeMap.get(edge);
						int count2 = indexEdges.get(edge);
						edgeMap.put(edge, count1 + count2);
					} else {
						edgeMap.put(edge, indexEdges.get(edge));
					}
				}
			} else {
				// 为关键词term新建一个summary graph上索引边集合
				TreeMap<IndexedEdgeTyped, Integer> edgeMap = new TreeMap<IndexedEdgeTyped, Integer>();
				for (IndexedEdgeTyped edge : indexEdges.keySet()) {
					edgeMap.put(edge, indexEdges.get(edge));
				}
				sgMap.put(term, edgeMap);
			}
			if (kgMap.containsKey(term)) {
				// 更新关键词term所对应的knowledge graph上索引边集合
				TreeSet<IndexedEdge_KG_ET> edgeSet = kgMap.get(term);
				for (IndexedEdge_KG_ET edge : indexedEdgeOnKG) {
					edgeSet.add(edge);
				}							
			} else {
				// 为关键词term新建一个knowledge graph上索引边集合
				TreeSet<IndexedEdge_KG_ET> edgeSet = new TreeSet<IndexedEdge_KG_ET>();
				for (IndexedEdge_KG_ET edge : indexedEdgeOnKG) {
					edgeSet.add(edge);
				}
				kgMap.put(term, edgeSet);							
			}
			if (map.containsKey(term)) {
				SequentialIntArray si = map.get(term);
				si.insert(entityID);
			} else {
				SequentialIntArray si = new SequentialIntArray();
				si.insert(entityID);
				map.put(term, si);
			}
		}
	}

	public void build(String dir, ClassManager classManager,
			InstanceManager instanceManager, EntityGraphTyped entityGraph) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/instance_attribute_mapping.txt"));
			String line = null;
			String[] elements = null;
			String lastEntity = null;
			HashSet<String> termSet = new HashSet<String>();
			// sgMap是summary graph上的index
			HashMap<String, TreeMap<IndexedEdgeTyped, Integer>> sgMap = new HashMap<String, TreeMap<IndexedEdgeTyped, Integer>>();
			// kgMap是knowledge graph上的index
			HashMap<String, TreeSet<IndexedEdge_KG_ET>> kgMap = new HashMap<String, TreeSet<IndexedEdge_KG_ET>>();
			// map记录每个keyword所匹配的实例节点的集合
			HashMap<String, SequentialIntArray> map = new HashMap<String, SequentialIntArray>();
			// sums记录summary graph上所有的边及其实例个数
//			TreeMap<IndexedEdgeTyped, Integer>  sums = new TreeMap<IndexedEdgeTyped, Integer>();
			int numOfEntities =0; //被解析的实例节点计数
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 4);
				if (elements[2].startsWith("<http://www") || !elements[3].endsWith("@en")) {
					continue;
				}
				if (lastEntity != null && !lastEntity.equals(elements[1])) {
					numOfEntities++;
					procAnEntity(lastEntity, instanceManager, entityGraph, sgMap, kgMap, map, termSet);
					// 为下一个entity新建一个关键词集合
					termSet = new HashSet<String>();
				}
				String[] terms = elements[3].substring(1,
						elements[3].lastIndexOf("\"")).split(" ");
				// 将本行出现的关键词都加入到集合termSet中
				for (String term : terms) {
					term = clean(term);
					if (term.length() > 0) {
						termSet.add(term);
					}
				}
				// 更新lastEntity变量
				lastEntity = elements[1];
			}
			numOfEntities++;
			procAnEntity(lastEntity, instanceManager, entityGraph, sgMap, kgMap, map, termSet);
			br.close();
			System.out.println(numOfEntities + "entities with attributes have been processed.");
			
			// 下面将缓存的index内容写入数据库，并将关键词列表写入文件
			BufferedWriter bw = new BufferedWriter(new FileWriter(GraphManager.KEYWORD_FILE));
			for (String term : sgMap.keySet()) {
				// 将关键词term写入文件
				bw.write(term);
				bw.newLine();
			}
			bw.close();
			for (String term : sgMap.keySet()) {
				// 为关键词term创建数据库记录的key
				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
				// 写入key在数据库edgeProbIndexOnSG中对应记录的value
				TreeMap<IndexedEdgeTyped, Integer> ei = sgMap.get(term);
				IndexedEdgeTyped[] edgeList = new IndexedEdgeTyped[ei.size()];
				Iterator<Entry<IndexedEdgeTyped, Integer>> iter = ei.entrySet()
						.iterator();
				int i = 0;
				while (iter.hasNext()) {
					Entry<IndexedEdgeTyped, Integer> entry = iter.next();
					IndexedEdgeTyped k = entry.getKey();
					double val = entry.getValue();
					double vnum = entityGraph.instNumOfSummaryEdge(k);
					k.setProb(val / vnum);
					edgeList[i] = k;
					i++;
				}
				PKIndexTypedEntry pk = new PKIndexTypedEntry(edgeList);
				DatabaseEntry value = new DatabaseEntry();
				typedBinding.objectToEntry(pk, value);
				edgeProbIndexOnSG.put(null, key, value);
			}
			for (String term : sgMap.keySet()) {
				// 为关键词term创建数据库记录的key
				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
				// 写入key在数据库edgeIndexOnKG中对应记录的value
				TreeSet<IndexedEdge_KG_ET> edgeSet = kgMap.get(term);
				KeywordIndexEntry_KG_ET object2 = new KeywordIndexEntry_KG_ET(edgeSet.toArray(new IndexedEdge_KG_ET[edgeSet.size()]));
				DatabaseEntry value = new DatabaseEntry();
				kieBinding.objectToEntry(object2, value);
				edgeIndexOnKG.put(null, key, value);
			}
			for (String term : map.keySet()) {
				// 为关键词term创建数据库记录的key
				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
				// 写入key在数据库nodeIndexOnKG中对应记录的value
				DatabaseEntry value = new DatabaseEntry();
				siaBinding.objectToEntry(map.get(term), value);
				nodeIndexOnKG.put(null, key, value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	private String clean(String term) {
		if (term.startsWith("\\\""))
			term = term.substring(2);
		if (term.endsWith("\\\""))
			term = term.substring(0, term.length() - 2);
		if (term.startsWith("("))
			term = term.substring(1);
		if (term.endsWith(",") || term.endsWith(":") || term.endsWith(")"))
			term = term.substring(0, term.length() - 1);
		return term.toLowerCase();
	}
	
	public void close() {
		try {
			if (edgeProbIndexOnSG != null)
				edgeProbIndexOnSG.close();
			if (edgeIndexOnKG != null)
				edgeIndexOnKG.close();
			if (nodeIndexOnKG != null)
				nodeIndexOnKG.close();
			if (env != null)
				env.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
