package edu.whu.clock.dbpedia;

import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class DBPediaSummaryGraphDB {
	private String dbPath;
	public GraphDatabaseService db;
	
	public DBPediaSummaryGraphDB() {
		this.dbPath = "D:/dbpediasummarygraphdb";
	}
	
	public DBPediaSummaryGraphDB(String dbPath) {
		this.dbPath = dbPath;
	}
	
	public void startup() {
		db = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdownHook();
	}
	
	public void shutdown() {
		db.shutdown();
	}
	
	public void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			}
		});
	}
	
	public void generateSummaryGraph(DBPediaGraphDB source) {
		HashMap<String, Long> map = new HashMap<String, Long>();
		
		Iterable<Node> allNodes = source.getAllNodes();
		Transaction tx = db.beginTx();
		try {
			for (Node node : allNodes) {
				Object type = node.getProperty(DBPediaLabel.ATTR_TYPE, null);
				if (type != null && type.equals(DBPediaLabel.NODE_TYPE_C)) {
					Node newNode = db.createNode();
					String uri = (String)node.getProperty(DBPediaLabel.ATTR_URI);
					newNode.setProperty(DBPediaLabel.ATTR_URI, uri);
					map.put(uri, newNode.getId());
					System.out.println(uri);
				}
			}
			tx.success();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			tx.finish();
		}
		System.out.println("Creating vertices finished.");
		
		Iterable<Relationship> allRelationships = source.getAllRelationships();
		tx = db.beginTx();
		try {
			for(Relationship rel : allRelationships) {
				Node start = rel.getStartNode();
				Node end = rel.getEndNode();
				if (start != null && end != null && start.getProperty(DBPediaLabel.ATTR_TYPE).equals("E") && end.getProperty(DBPediaLabel.ATTR_TYPE).equals("E")) {
					for (Relationship rel1 : start.getRelationships(DBPediaLabel.INSTANCE_OF)) {
						Node type1 = rel1.getEndNode();
						for (Relationship rel2 : end.getRelationships(DBPediaLabel.INSTANCE_OF)) {
							Node type2 = rel2.getEndNode();
							long id1 = map.get(type1.getProperty(DBPediaLabel.ATTR_URI));
							long id2 = map.get(type2.getProperty(DBPediaLabel.ATTR_URI));
							db.getNodeById(id1).createRelationshipTo(db.getNodeById(id2), rel.getType());
						}
					}
				}
      		}
			tx.success();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			tx.finish();
		}
		System.out.println("Creating edges finished.");
		
	}
	
	public static void main(String[] args) {
		DBPediaSummaryGraphDB sgdb = new DBPediaSummaryGraphDB();
		sgdb.startup();
		DBPediaGraphDB gdb = new DBPediaGraphDB();
		gdb.startup();
		sgdb.generateSummaryGraph(gdb);
		gdb.shutdown();
		sgdb.shutdown();
	}

}
