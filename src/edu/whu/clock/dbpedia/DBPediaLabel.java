package edu.whu.clock.dbpedia;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;

public abstract class DBPediaLabel {
	public static final RelationshipType INSTANCE_OF = DynamicRelationshipType.withName("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
	public static final RelationshipType HAS_NAME = DynamicRelationshipType.withName("<http://clock.whu.edu.cn/rdf-gdb/name");
	
	public static final String ATTR_URI = "uri";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_VALUE = "value";
	
	public static final String INDEX_URI = "uri";
	
	public static final String NODE_TYPE_E = "E";
	public static final String NODE_TYPE_V = "V";
	public static final String NODE_TYPE_C = "C";
	
	public static final String PREFIX_OF_CLASS = "<http://dbpedia.org/ontology/";
	public static final String PREFIX_OF_INSTANCE = "<http://dbpedia.org/resource/";

}
