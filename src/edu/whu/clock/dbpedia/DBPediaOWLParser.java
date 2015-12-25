package edu.whu.clock.dbpedia;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DBPediaOWLParser {

	private OntModel model;
	
	public void load(String path) throws FileNotFoundException {
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		model.read(new FileInputStream(path), "");
	}
	
	public String[] getAllClasses() {
		ArrayList<String> list = new ArrayList<String>();
		for (Iterator<OntClass> it = model.listClasses(); it.hasNext(); ) {
			OntClass ontclass = it.next();
			String uri = ontclass.getURI();
			list.add("<" + uri + ">");
			System.out.println(uri);
		}
		System.out.println(list.size());
		return list.toArray(new String[list.size()]);
	}
	
	public String[] getAllObjectProperties() {
		ArrayList<String> list = new ArrayList<String>();
		for (Iterator<ObjectProperty> it = model.listObjectProperties(); it.hasNext(); ) {
			ObjectProperty op = it.next();
			OntResource domain = op.getDomain();
			OntResource range = op.getRange();
//			if (domain == null || range == null) {
//				System.out.println(op);
//				continue;
//			}
			String uri = op.getURI();
			String str = (domain == null ? "" : domain.getURI()) + " <" + uri + "> " + (range == null ? "" : range.getURI());
			list.add(str);
			System.out.println(str);
		}
		System.out.println(list.size());
		return list.toArray(new String[list.size()]);
	}
	
	public static void main(String[] args) {
		DBPediaOWLParser parser = new DBPediaOWLParser();
		try {
			parser.load("D:/DBPedia/dbpedia_3.8.owl");
			String[] properties = parser.getAllObjectProperties();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
