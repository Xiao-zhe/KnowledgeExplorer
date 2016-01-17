package edu.whu.clock.newgraph;

import java.util.Date;

import notinuse.EntityGraph;
import edu.whu.clock.newprobindex.CPTableTypedManager_EdgeCount;
import edu.whu.clock.newprobindex.FNSetManager;
import edu.whu.clock.newprobindex.PKIndexManager_EdgeCount;

public class GraphManager {

	public final static String GRAPH_FILE_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/clean";
//	public final static String INDEX_DB_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/keyword index db";
//	public final static String INDEX_DB_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/pk-indexdb";
	public final static String INDEX_DB_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/pk-indexdb-ec";
//	public final static String INDEX_DB_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/pk-indexdb-ectest";
	public final static String EI_INDEX_DB_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/ei-indexdb";
	public final static String CPTABLE_FILE_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/cptable";
	public final static String FNSET_FILE_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/fnset/minsupport5%_N=4";
	public final static String KEYWORD_FILE = INDEX_DB_DIR + "/keywords.txt";
	
	public ClassManager classManager;
	public EdgeTypeManager etypeManager;
	public InstanceManager instanceManager;
	public SummaryGraphTyped summaryGraphTyped;
	public EntityGraph entityGraph;
	public EntityGraphTyped entityGraphTyped;
	public CPTableTypedManager_EdgeCount cpTableTyped;
	public PKIndexManager_EdgeCount pkIndex;
	public FNSetManager fnSet;
//	public EntityIndex eiIndex;
	

	public void genClassManager() {
		Date start = new Date();
		classManager = new ClassManager();
		classManager.load(GraphManager.GRAPH_FILE_DIR);
		Date end = new Date();
		System.out.println("Generating class manager finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
//		try {
//			ObjectOutputStream os = new ObjectOutputStream(
//					new FileOutputStream(GraphManager.SERIALIZED_FILE_DIR));
//			os.writeObject(cm);
//			os.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Serializing finished.");
	}
	
	public void genEdgeTypeManager() {
		Date start = new Date();
		etypeManager = new EdgeTypeManager();
		etypeManager.build(GRAPH_FILE_DIR);;
		Date end = new Date();
		System.out.println("Generating edge type manager finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}
	
	public void genInstanceManager() {
		Date start = new Date();
		instanceManager = new InstanceManager();
		instanceManager.load(GraphManager.GRAPH_FILE_DIR, classManager);
		Date end = new Date();
		System.out.println("Generating instance manager finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}

	public void genSummaryGraphTyped() {
		if (classManager == null) {
			System.out.println("ClassManager is not initialized.");
			return;
		}
		if (etypeManager == null) {
			System.out.println("EdgeTypeManager is not initialized.");
			return;
		}
		Date start = new Date();
		summaryGraphTyped = new SummaryGraphTyped(classManager, etypeManager);
		summaryGraphTyped.addAllEdges(GraphManager.GRAPH_FILE_DIR);
		Date end = new Date();
		System.out.println("Generating typed summary graph finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
//		summaryGraphTyped.write2file(GraphManager.SERIALIZED_FILE_DIR);
	}
	
	public void genEntityGraph(){
		if (instanceManager == null) {
			System.out.println("InstanceManager is not initialized.");
			return;
		}
		Date start = new Date();
		EntityGraph graph = new EntityGraph(instanceManager);
		graph.loadUndirectedGraph(GraphManager.GRAPH_FILE_DIR);
		Date end = new Date();
		System.out.println("Generating entity graph finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}
	
	public void genEntityGraphTyped(){
		if (instanceManager == null) {
			System.out.println("InstanceManager is not initialized.");
			return;
		}
		if (etypeManager == null) {
			System.out.println("EdgeTypeManager is not initialized.");
			return;
		}
		Date start = new Date();
		entityGraphTyped = new EntityGraphTyped(instanceManager, etypeManager);
		entityGraphTyped.addAllEdges(GraphManager.GRAPH_FILE_DIR);
		Date end = new Date();
		System.out.println("Generating the typed entity graph is finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}
	
	public void genCPTableTypedManager_EdgeCount(){
		if (entityGraphTyped == null) {
			System.out.println("EntityGraphTyped is not initialized.");
			return;
		}
		Date start = new Date();
		cpTableTyped = new CPTableTypedManager_EdgeCount();
		cpTableTyped.build(entityGraphTyped);
		Date end = new Date();
		System.out.println("Generating the typed CP-Table is finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}
	

	public void genPKIndexManager_EdgeCount(boolean typed) {
		Date start = new Date();
		pkIndex = new PKIndexManager_EdgeCount(GraphManager.INDEX_DB_DIR);
		if (typed) {
			pkIndex.build(GraphManager.GRAPH_FILE_DIR, classManager, instanceManager, entityGraphTyped);
		}
		else {
//			pkIndex_ec.build(GraphManager.GRAPH_FILE_DIR, classManager, instanceManager, entityGraph);
		}
		Date end = new Date();
		System.out.println("Generating the typed PK-Index-ec is finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
		if (pkIndex != null) pkIndex.close();
	}

//	public void genEntityInvertedIndexManager(){
//		if (instanceManager == null) {
//			System.out.println("InstanceManager is not initialized.");
//			return;
//		}
//		Date start = new Date();
//		eiIndex = new EntityIndex(EI_INDEX_DB_DIR);
//		eiIndex.build(GRAPH_FILE_DIR, instanceManager);
//		Date end = new Date();
//		eiIndex.close();
//		System.out.println("Generating the entity inverted index is finished.");
//		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
//	}
	
	public void loadCPTableTypedManager() {
		Date start = new Date();
		cpTableTyped = new CPTableTypedManager_EdgeCount();
		cpTableTyped.loadFromFile(GraphManager.CPTABLE_FILE_DIR);
		Date end = new Date();
		System.out.println("Loading the CP-Table is finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}
	
	public void loadFNSetManager(){
		if (classManager == null) {
			System.out.println("ClassManager is not initialized.");
			return;
		}
		Date start = new Date();
		fnSet = new FNSetManager(classManager.getClassNum());
		fnSet.loadFromFile(GraphManager.FNSET_FILE_DIR);;
		Date end = new Date();
		System.out.println("Loading the FN-Set is finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}
	
	
	public void initPKIndexManager_EdgeCount() {
		Date start = new Date();
		pkIndex = new PKIndexManager_EdgeCount(GraphManager.INDEX_DB_DIR);
		Date end = new Date();
		System.out.println("Initializing the PK-Index-ec is finished.");
		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
	}
	
//	public void initEntityInvertedIndexManager() {
//		Date start = new Date();
//		eiIndex = new EntityIndex(GraphManager.EI_INDEX_DB_DIR);
//		Date end = new Date();
//		System.out.println("Initializing the EI-Index is finished.");
//		System.out.println("Time exhausted: " + (end.getTime()-start.getTime()) + " msec");
//	}

	public static void main(String[] args) {
		GraphManager gm = new GraphManager();
		gm.genClassManager();
		gm.genEdgeTypeManager();
		gm.genInstanceManager();
		
		
//		System.out.println(gm.instanceManager.getInstanceName(0));
//		System.out.println(gm.instanceManager.getClassID("Green_Bay_Packers"));
		gm.genSummaryGraphTyped();
//		SummaryGraphEdgeTyped[] list = gm.summaryGraphTyped.getNeighbors("AmericanFootballLeague");
//		for (SummaryGraphEdgeTyped edge : list) {
//			System.out.println(edge.getEnd()+" "+edge.getType()+" "+edge.isOut());
//		}
//		gm.genEntityInvertedIndexManager();
		gm.genEntityGraphTyped();
//		gm.genCPTableTypedManager();
//		gm.cpTableTyped.writeToFile(GraphManager.CPTABLE_FILE_DIR);
		gm.genPKIndexManager_EdgeCount(true);
//		gm.entityGraphTyped.numOfSummaryEdge((short)7,(short)7,(short)550, true);
//		gm.initPKIndexManager();
	}

}
