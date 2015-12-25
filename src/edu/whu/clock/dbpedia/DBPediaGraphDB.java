package edu.whu.clock.dbpedia;

import java.util.ArrayList;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.tooling.GlobalGraphOperations;

public class DBPediaGraphDB {
	private String dbPath;
	public GraphDatabaseService db;
	private Index<Node> uriIndex;
	//private static RelationshipIndex relationshipSubjectIndex
	
	public DBPediaGraphDB() {
		this.dbPath = "D:/DBpath";
	}
	
	public DBPediaGraphDB(String dbPath) {
		this.dbPath = dbPath;
	}
	
	public void startup() {
		db = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		uriIndex = db.index().forNodes(DBPediaLabel.INDEX_URI);
	    //relationshipSubjectIndex = db.index().forRelationships("subject");
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
	
	public Node getNodeByURI(String uri) {
		return uriIndex.get(DBPediaLabel.ATTR_URI, uri).getSingle();
	}
	
	public String[] getTypesOfEntity(String uri) {
		ArrayList<String> list = new ArrayList<String>();
		Node entity = getNodeByURI(uri);
		for (Iterator<Relationship> it = entity.getRelationships(Direction.OUTGOING, DBPediaLabel.INSTANCE_OF).iterator(); it.hasNext();) {
			list.add((String)it.next().getEndNode().getProperty(DBPediaLabel.ATTR_URI));
		}
		return list.toArray(new String[list.size()]);
	}
	
//	public boolean isNodeOfType(Node node, String type) {
//		for (Iterator<Relationship> it = node.getRelationships(DBPediaLabel.INSTANCE_OF).iterator(); it.hasNext();) {
//			Relationship rel = it.next();
//			if (rel.getEndNode().getProperty(DBPediaLabel.ATTR_URI).equals(type)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	public Iterable<Node> getAllNodes() {
		GlobalGraphOperations ggo = GlobalGraphOperations.at(db);
		return ggo.getAllNodes();
	}
	
	public Iterable<Relationship> getAllRelationships() {
		GlobalGraphOperations ggo = GlobalGraphOperations.at(db);
		return ggo.getAllRelationships();
	}
	
	public void builduriIndex() {
		GlobalGraphOperations global = GlobalGraphOperations.at(db);
		Transaction tx = db.beginTx();
		long count = 0;
		try {
			for (Iterator<Node> it = global.getAllNodes().iterator(); it.hasNext();) {
				Node node = it.next();
				if (node.getId() == 0)
					continue;
				uriIndex.putIfAbsent(node, DBPediaLabel.ATTR_URI, node.getProperty(DBPediaLabel.ATTR_URI));
				count++;
				if (count % 100000 == 0) {
					System.out.println(count);
				}
			}
			tx.success();
			System.out.println(count);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			tx.finish();
		}
	}
	
	public long countNodes() {
		long count = 0;
		GlobalGraphOperations global = GlobalGraphOperations.at(db);
		for (Iterator<Node> it = global.getAllNodes().iterator(); it.hasNext();) {
			count++;
			it.next();
		}
		return count;
	}
	
	public void show(Node node) {
		System.out.println("ID:    " + node.getId());
		System.out.println("TYPE:  " + node.getProperty(DBPediaLabel.ATTR_TYPE));
		if (node.getProperty(DBPediaLabel.ATTR_TYPE) == null) {
			System.out.println("Unknown type");
			return;
		}
		else if (node.getProperty(DBPediaLabel.ATTR_TYPE).equals(DBPediaLabel.NODE_TYPE_C)) {
			System.out.println("URI:   " + node.getProperty(DBPediaLabel.ATTR_URI));
		}
		else if (node.getProperty(DBPediaLabel.ATTR_TYPE).equals(DBPediaLabel.NODE_TYPE_E)) {
			System.out.println("URI:   " + node.getProperty(DBPediaLabel.ATTR_URI));
		}
		else if (node.getProperty(DBPediaLabel.ATTR_TYPE).equals(DBPediaLabel.NODE_TYPE_V)) {
			System.out.println("VALUE: " + node.getProperty(DBPediaLabel.ATTR_VALUE));
		}
		System.out.println("Outgoing:");
		Iterator<Relationship> it = node.getRelationships(Direction.OUTGOING).iterator();
		while (it.hasNext()) {
			Relationship rel = it.next();
			System.out.print("       " + rel.getType().name() + " ");
			if (rel.getEndNode().getProperty(DBPediaLabel.ATTR_TYPE).equals(DBPediaLabel.NODE_TYPE_C)) {
				System.out.println(rel.getEndNode().getProperty(DBPediaLabel.ATTR_TYPE) + " " + rel.getEndNode().getProperty(DBPediaLabel.ATTR_URI));
			}
			else if (rel.getEndNode().getProperty(DBPediaLabel.ATTR_TYPE).equals(DBPediaLabel.NODE_TYPE_V)) {
				System.out.println(rel.getEndNode().getProperty(DBPediaLabel.ATTR_TYPE) + " " + rel.getEndNode().getProperty(DBPediaLabel.ATTR_VALUE));
			}
			else {
				System.out.println(rel.getEndNode().getProperty(DBPediaLabel.ATTR_TYPE) + " " + rel.getEndNode().getProperty(DBPediaLabel.ATTR_URI));
			}
		}
		System.out.println("Incoming:");
		it = node.getRelationships(Direction.INCOMING).iterator();
		while (it.hasNext()) {
			Relationship rel = it.next();
			System.out.print("       " + rel.getType().name() + " ");
			if (rel.getStartNode().getProperty(DBPediaLabel.ATTR_TYPE).equals(DBPediaLabel.NODE_TYPE_C)) {
				System.out.println(rel.getStartNode().getProperty(DBPediaLabel.ATTR_TYPE) + " " + rel.getStartNode().getProperty(DBPediaLabel.ATTR_URI));
			}
			else if (rel.getStartNode().getProperty(DBPediaLabel.ATTR_TYPE).equals(DBPediaLabel.NODE_TYPE_V)) {
				System.out.println(rel.getStartNode().getProperty(DBPediaLabel.ATTR_TYPE) + " " + rel.getStartNode().getProperty(DBPediaLabel.ATTR_VALUE));
			}
			else {
				System.out.println(rel.getStartNode().getProperty(DBPediaLabel.ATTR_TYPE) + " " + rel.getStartNode().getProperty(DBPediaLabel.ATTR_URI));
			}
		}
	}
	
	public static void main(String[] args) {
		DBPediaGraphDB dbpedia = new DBPediaGraphDB("D:/DBpath");
		dbpedia.startup();
//		dbpedia.builduriIndex();
		dbpedia.show(dbpedia.getNodeByURI("<http://dbpedia.org/resource/Paul_Linwood>"));
//		dbpedia.show(dbpedia.db.getNodeById(10000));
//		System.out.println(dbpedia.countNodes());
		dbpedia.shutdown();
	}

}
