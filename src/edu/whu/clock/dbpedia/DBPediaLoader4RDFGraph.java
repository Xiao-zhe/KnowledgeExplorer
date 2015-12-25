package edu.whu.clock.dbpedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.LuceneBatchInserterIndexProvider;

public class DBPediaLoader4RDFGraph {
	
	private static BatchInserter inserter;
	private static BatchInserterIndexProvider indexProvider;
	private static BatchInserterIndex uriIndex;
	
	private static HashMap<String, Long> uriMap;
	
	public static void init(String dbPath) {
		inserter = BatchInserters.inserter(dbPath);
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		uriIndex = indexProvider.nodeIndex(DBPediaLabel.INDEX_URI, MapUtil.stringMap( "type", "exact" ) );
		
		uriMap = new HashMap<String, Long>();
	}
	
	public static void exit() {
		uriIndex.flush();
		indexProvider.shutdown();
		inserter.shutdown();
	}
	
	public static void loadClassVertices(String dsPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			Map<String, Object> properties = null;
			long count = 0;
			while (br.ready()) {
				line = br.readLine().trim();
				properties = new HashMap<String, Object>();
				properties.put(DBPediaLabel.ATTR_URI, line);
				properties.put(DBPediaLabel.ATTR_TYPE, DBPediaLabel.NODE_TYPE_C);
				long id = inserter.createNode(properties);
				properties.remove(DBPediaLabel.ATTR_TYPE);
				uriIndex.add(id, properties);
				uriMap.put(line, id);
				count++;
			}
			System.out.println("There are " + count + "  classes.");
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void loadEntityVertices(String dsPath) {		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			String[] elements = new String[3];
			Map<String, Object> properties = null;
			long count1 = 0, count2 = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				long entity = 0;
				if (uriMap.containsKey(elements[0])) {
					entity = uriMap.get(elements[0]);
				}
				else {  // 这个实例之前没有被插入.
					properties = new HashMap<String, Object>();
					properties.put(DBPediaLabel.ATTR_URI, elements[0]);
					properties.put(DBPediaLabel.ATTR_TYPE, DBPediaLabel.NODE_TYPE_E);
					entity = inserter.createNode(properties);
					properties.remove(DBPediaLabel.ATTR_TYPE);
					uriIndex.add(entity, properties);
					uriMap.put(elements[0], entity);
					count1++;
				}
				long type = uriMap.get(elements[2]);
				inserter.createRelationship(entity, type, DBPediaLabel.INSTANCE_OF, null);
				count2++;
			}
			System.out.println("There are " + count1 + "  entities.");
			System.out.println("There are " + count2 + "  instanceOf.");
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void loadValueVertices(String dsPath) {
		HashMap<String, RelationshipType> attTypes = new HashMap<String, RelationshipType>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			String[] elements = new String[3];
			Map<String, Object> properties = null;
			long count = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				long entity = 0;
				if (uriMap.containsKey(elements[0])) {
					entity = uriMap.get(elements[0]);
				}
				else {  // 这个实例之前没有被插入.
					System.out.println(line);
					continue;
				}
				properties = new HashMap<String, Object>();
				properties.put(DBPediaLabel.ATTR_VALUE, elements[2]);
				properties.put(DBPediaLabel.ATTR_TYPE, DBPediaLabel.NODE_TYPE_V);
				long value = inserter.createNode(properties);
				count++;
				RelationshipType atype = null;
				if (attTypes.containsKey(elements[1])) {
					atype = attTypes.get(elements[1]);
				}
				else {
					atype = DynamicRelationshipType.withName(elements[1]);
					attTypes.put(elements[1], atype);
				}
				inserter.createRelationship(entity, value, atype, null);
			}
			System.out.println("There are " + count + " values.");
			System.out.println("There are " + attTypes.size() + " attribute types.");
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void loadEERelationships(String dsPath) {		
		HashMap<String, RelationshipType> relTypes = new HashMap<String, RelationshipType>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			String line = null;
			String[] elements = new String[3];
			long count = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				if (uriMap.containsKey(elements[0]) && uriMap.containsKey(elements[2])) {
					long entity = uriMap.get(elements[0]);
					long other = uriMap.get(elements[2]);
					RelationshipType rtype = null;
					if (relTypes.containsKey(elements[1])) {
						rtype = relTypes.get(elements[1]);
					}
					else {
						rtype = DynamicRelationshipType.withName(elements[1]);
						relTypes.put(elements[1], rtype);
					}
					inserter.createRelationship(entity, other, rtype, null);
					count++;
				}
				else {
					System.out.println(line);
				}
			}
			System.out.println("There are " + count + " relationships.");
			System.out.println("There are " + relTypes.size() + " relationship types.");
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DBPediaLoader4RDFGraph.init("D:/DBPath");
		DBPediaLoader4RDFGraph.loadClassVertices("D:/DBpedia/types_en_clean.nt");
		DBPediaLoader4RDFGraph.loadEntityVertices("D:/DBpedia/instance_types_en_clean.nt");
		DBPediaLoader4RDFGraph.loadValueVertices("D:/DBpedia/instance_properties_en_clean.nt");
		DBPediaLoader4RDFGraph.loadEERelationships("D:/DBpedia/instance_relationships_en_clean.nt");
		DBPediaLoader4RDFGraph.exit();
	}
}
