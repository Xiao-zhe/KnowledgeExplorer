package edu.whu.clock.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
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

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DBPediaLoader4RDFSummaryGraph {

	private static BatchInserter inserter;
	private static BatchInserterIndexProvider indexProvider;
	private static BatchInserterIndex uriIndex;
	
	private static DBPediaGraphDB db;
	
	private static OntModel OWLModel;
	
	public static void init(String dbPath, String owlPath) throws FileNotFoundException {
		inserter = BatchInserters.inserter(dbPath);
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		uriIndex = indexProvider.nodeIndex(DBPediaLabel.INDEX_URI, MapUtil.stringMap( "type", "exact" ) );
		
		db = new DBPediaGraphDB("D:/DBpath");
		db.startup();
		
		OWLModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		OWLModel.read(new FileInputStream(owlPath), "");
	}
	
	public static void exit() {
		OWLModel.close();
		db.shutdown();
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
				if (!existClass(line.substring(1, line.length() - 1))) {
					System.out.println(line);
					return;
				}
				properties = new HashMap<String, Object>();
				properties.put(DBPediaLabel.ATTR_URI, line);
				properties.put(DBPediaLabel.ATTR_TYPE, DBPediaLabel.NODE_TYPE_C);
				long id = inserter.createNode(properties);
				properties.remove(DBPediaLabel.ATTR_TYPE);
				uriIndex.add(id, properties);
				count++;
			}
			System.out.println("There are " + count + "  classes.");
			br.close();
			uriIndex.flush();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void loadEERelationships(String dsPath) {
		HashMap<String, String[]> entTypes = new HashMap<String, String[]>();
		HashMap<String, RelationshipType> relTypes = new HashMap<String, RelationshipType>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dsPath));
			FileWriter fw = new FileWriter(new File("D:/unknownRel.txt"));
			String line = null;
			String[] elements = new String[3];
			long count = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				
				ObjectProperty op = OWLModel.getObjectProperty(elements[1].substring(1, elements[1].length() - 1));
				
				if (op == null) {  // 如果object property不存在
					continue;
				}
				
				OntResource domain = op.getDomain();
				long[] domainTypes = extractTypes(elements[0], domain, entTypes);
				if (domainTypes == null) {  // 如果实例不是domain及其子类
//					System.out.println(elements[0] + " is not of the domain type " + type + " in OWL.");
					fw.write("Domain should be: " + domain.getURI() + " for " + line + "\n");
					continue;
				}
				
				OntResource range = op.getRange();
				long[] rangeTypes = extractTypes(elements[2], range, entTypes);;
				if (rangeTypes == null) {
//					System.out.println(elements[2] + " is not of the range type " + type + " in OWL.");
					fw.write("Range should be: " + range.getURI() + " for " + line + "\n");
					continue;
				}
								
//				RelationshipType rtype = null;
//				if (relTypes.containsKey(elements[1])) {
//					rtype = relTypes.get(elements[1]);
//				}
//				else {
//					rtype = DynamicRelationshipType.withName(elements[1]);
//					relTypes.put(elements[1], rtype);
//				}
//				inserter.createRelationship(type1, type2, rtype, null);
				count++;
				if (count % 100000 == 0) {
					System.out.println(count);
					System.gc();
				}
			}
			System.out.println("There are " + count + " relationships.");
			System.out.println("There are " + relTypes.size() + " relationship types.");
			fw.close();
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static long[] extractTypes(String uri, OntResource resource, HashMap<String, String[]> entTypes) {
		String[] types = null;
		if (entTypes.containsKey(uri)) {
			types = entTypes.get(uri);
		}
		else {
			types = db.getTypesOfEntity(uri);
			entTypes.put(uri, types);
		}
		
		if (resource == null) {
			long[] result = new long[types.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = uriIndex.get(DBPediaLabel.ATTR_URI, types[i]).getSingle();
			}
			return result;
		}
		else {
			ArrayList<String> list = new ArrayList<String>();
			OntClass rclass = resource.asClass();
			for (int i = 0; i < types.length; i++) {
				OntClass tclass = OWLModel.getOntClass(types[i].substring(1, types[i].length()-1));
				if (!rclass.isDifferentFrom(tclass) || rclass.hasSubClass(tclass)) {
					list.add(types[i]);
				}
			}
			if (list.isEmpty()) {
				return null;
			}
			else {
				long[] result = new long[list.size()];
				for (int i = 0; i < result.length; i++) {
					result[i] = uriIndex.get(DBPediaLabel.ATTR_URI, list.get(i)).getSingle();
				}
				return result;
			}
		}
	}
	
	private static boolean existClass(String uri) {
		OntClass ontclass = OWLModel.getOntClass(uri);
		if (ontclass == null) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		try {
			DBPediaLoader4RDFSummaryGraph.init("D:/SummaryPath", "D:/DBPedia/dbpedia_3.8.owl");
			DBPediaLoader4RDFSummaryGraph.loadClassVertices("D:/DBpedia/types_en_clean.nt");
			DBPediaLoader4RDFSummaryGraph.loadEERelationships("D:/DBpedia/instance_relationships_en_clean.nt");
			DBPediaLoader4RDFSummaryGraph.exit();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
