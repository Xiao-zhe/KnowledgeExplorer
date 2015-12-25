package edu.whu.clock.dbpedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.LuceneBatchInserterIndexProvider;

public class DBPediaLoader4PropertyGraph {
	
	private static BatchInserter inserter;
	private static BatchInserterIndexProvider indexProvider;
	
	private static HashMap<String, Long> uriIndex;
	
	public static void init(String dbPath) {
		inserter = BatchInserters.inserter(dbPath);
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		uriIndex = new HashMap<String, Long>();
	}
	
	public static void exit() {
		indexProvider.shutdown();
		inserter.shutdown();
	}
	
	public static void loadTypes(String dsPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			Map<String, Object> properties = null;
			while (br.ready()) {
				line = br.readLine().trim();
				properties = new HashMap<String, Object>();
				properties.put(DBPediaLabel.ATTR_URI, line);
				properties.put(DBPediaLabel.ATTR_TYPE, DBPediaLabel.NODE_TYPE_C);
				long id = inserter.createNode(properties);
				uriIndex.put(line, id);
			}
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void loadEntities(String dsPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			String[] elements = new String[3];
			String lastSubject = null;
			Map<String, Object> properties = null;
			long times = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				if (lastSubject == null || !elements[0].equals(lastSubject)) {
					if (properties != null) {
						if (uriIndex.containsKey(lastSubject)) {
							System.out.println(lastSubject);
						}
						else {
							long entity = inserter.createNode(properties);
							uriIndex.put(lastSubject, entity);
						}
					}
					lastSubject = elements[0];
					properties = new HashMap<String, Object>();
					properties.put(DBPediaLabel.ATTR_URI, elements[0]);
					properties.put(DBPediaLabel.ATTR_TYPE, DBPediaLabel.NODE_TYPE_E);
				}
				properties.put(elements[1], elements[2]);
				
				times++;
				if (times % 100000 == 0)
					System.out.println("已完成" + times + "行");
			}
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void loadECRelationships(String dsPath) {		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			String[] elements = new String[3];
			Map<String, Object> properties = null;
			long times = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				long entity = 0;
				if (uriIndex.containsKey(elements[0])) {
					entity = uriIndex.get(elements[0]);
				}
				else {  // 这个实例没有任何属性,所以之前没有被插入.
					properties = new HashMap<String, Object>();
					properties.put(DBPediaLabel.ATTR_URI, elements[0]);
					properties.put(DBPediaLabel.ATTR_TYPE, DBPediaLabel.NODE_TYPE_E);
					entity = inserter.createNode(properties);
					uriIndex.put(elements[0], entity);
				}
				long type = uriIndex.get(elements[2]);
				inserter.createRelationship(entity, type, DBPediaLabel.INSTANCE_OF, null);
				
				times++;
				if (times % 100000 == 0)
					System.out.println("已完成" + times + "行");
			}
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void loadEERelationships(String dsPath) {		
		HashMap<String, RelationshipType> allRTypes = new HashMap<String, RelationshipType>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			String[] elements = new String[3];
			long times = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				if (uriIndex.containsKey(elements[0]) && uriIndex.containsKey(elements[2])) {
					long entity = uriIndex.get(elements[0]);
					long other = uriIndex.get(elements[2]);
					RelationshipType rtype = null;
					if (allRTypes.containsKey(elements[1])) {
						rtype = allRTypes.get(elements[1]);
					}
					else {
						rtype = DynamicRelationshipType.withName(elements[1]);
						allRTypes.put(elements[1], rtype);
					}
					inserter.createRelationship(entity, other, rtype, null);
				}
				else {
					System.out.println(line);
				}
				
				times++;
				if (times % 100000 == 0)
					System.out.println("已完成" + times + "行");
			}
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DBPediaLoader4PropertyGraph.init("D:/DBpath3");
		DBPediaLoader4PropertyGraph.loadTypes("D:/DBpedia/types_en_clean.nt");
		DBPediaLoader4PropertyGraph.loadEntities("D:/DBpedia/instance_properties_en_clean.nt");
		DBPediaLoader4PropertyGraph.loadECRelationships("D:/DBpedia/instance_types_en_clean.nt");
		DBPediaLoader4PropertyGraph.loadEERelationships("D:/DBpedia/instance_relationships_en_clean.nt");
		DBPediaLoader4PropertyGraph.exit();
	}
}
