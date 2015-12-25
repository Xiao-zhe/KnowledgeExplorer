package edu.whu.clock.dbpedia2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.whu.clock.dbpedia.DBPediaLabel;

public class Cleanser {

	public static void run(String dir) {
		OntModel OWLModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		TreeSet<String> allClasses = new TreeSet<String>();
		TreeSet<String> allInstances = new TreeSet<String>();
		TreeSet<String> allRelTypes = new TreeSet<String>();
		TreeSet<String> allAttTypes = new TreeSet<String>();
		HashMap<String, String> instanceClasses = new HashMap<String, String>();
		TreeSet<String> classRelationships = new TreeSet<String>();
		TreeMap<String, ArrayList<String>> instanceProperties = new TreeMap<String, ArrayList<String>>();
//		TreeMap<String, ArrayList<String>> classProperties = new TreeMap<String, ArrayList<String>>();
		
		try {
			OWLModel.read(new FileInputStream(dir + "/dbpedia_3.8.owl"), "");
			BufferedReader br = new BufferedReader(new FileReader(dir + "/instance_types_en.nt"));
			FileWriter fw = new FileWriter(new File(dir + "/clean/instance_class_mapping.txt"));
			String line = null;
			String[] elements = new String[3];
			String last = null;
			long times = 0;
			while (br.ready()) {
				line = br.readLine();
				if (line.startsWith("#"))
					continue;
				line = line.substring(0, line.lastIndexOf(".") - 1);
				elements = line.split(" ", 3);
				if (!elements[2].startsWith(DBPediaLabel.PREFIX_OF_CLASS) || !elements[0].startsWith(DBPediaLabel.PREFIX_OF_INSTANCE)) {
					continue;
				}
				if (last == null || !last.equals(elements[0])) {
//					OntClass oc = OWLModel.getOntClass(elements[2].substring(1, elements[2].length() - 1));
//					if (oc.hasSubClass()) {
//						System.out.println(line);
//					}
					last = elements[0];
					String instance = elements[0].substring(DBPediaLabel.PREFIX_OF_INSTANCE.length(), elements[0].length() - 1);
					String iclass = elements[2].substring(DBPediaLabel.PREFIX_OF_CLASS.length(), elements[2].length() - 1);
					fw.write(instance + " " + iclass + "\n");
					allClasses.add(iclass);
					allInstances.add(instance);
					instanceClasses.put(instance, iclass);
				}
				times++;
				if (times % 100000 == 0)
					System.out.println("已完成" + times + "行");
			}
			fw.close();
			br.close();
			
			fw = new FileWriter(new File(dir + "/clean/class.txt"));
			for (Iterator<String> it = allClasses.iterator(); it.hasNext(); ) {
				String type = it.next();
				fw.write(type + "\n");
			}
			fw.close();
			
			times = 0;
			br = new BufferedReader(new FileReader(dir + "/mappingbased_properties_en.nt"));
			fw = new FileWriter(new File(dir + "/clean/instance_attribute_mapping.txt"));
			FileWriter fw2 = new FileWriter(new File(dir + "/clean/instance_relationship_mapping.txt"));
			while (br.ready()) {
				line = br.readLine();
				if (line.startsWith("#"))
					continue;
				line = line.substring(0, line.lastIndexOf(".") - 1);
				elements = line.split(" ", 3);
				if (elements[0].startsWith(DBPediaLabel.PREFIX_OF_INSTANCE)) {
					elements[0] = elements[0].substring(DBPediaLabel.PREFIX_OF_INSTANCE.length(), elements[0].length() - 1);
				}
				else {
					continue;
				}
				if (allInstances.contains(elements[0])) {
					if (elements[2].startsWith(DBPediaLabel.PREFIX_OF_INSTANCE)) {
						elements[2] = elements[2].substring(DBPediaLabel.PREFIX_OF_INSTANCE.length(), elements[2].length() - 1);
						if (allInstances.contains(elements[2]) && elements[1].startsWith(DBPediaLabel.PREFIX_OF_CLASS)) {
							ObjectProperty op = OWLModel.getObjectProperty(elements[1].substring(1, elements[1].length() - 1));
							if (op == null) {  // 如果object property不存在
								System.out.println(elements[1]);
								continue;
							}
							elements[1] = elements[1].substring(DBPediaLabel.PREFIX_OF_CLASS.length(), elements[1].length() - 1);
							allRelTypes.add(elements[1]);
							fw2.write(elements[0] + " " + elements[1] + " " + elements[2] + "\n");
							// class relationships
							classRelationships.add(instanceClasses.get(elements[0]) + " " + elements[1] + " " + instanceClasses.get(elements[2]));
						}
					}
					else if (elements[2].startsWith("\"")) {
						if (instanceProperties.containsKey(elements[0])) {
							instanceProperties.get(elements[0]).add(elements[0] + " " + elements[1] + " " + elements[2]);
						}
						else {
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(elements[0] + " " + elements[1] + " " + elements[2]);
							instanceProperties.put(elements[0], temp);
						}
						allAttTypes.add(elements[1]);
						// class properties
//						String iclass = instanceClasses.get(elements[0]);
//						if (classProperties.containsKey(iclass)) {
//							classProperties.get(iclass).add(iclass + " " + elements[1] + " " + elements[2]);
//						}
//						else {
//							ArrayList<String> temp = new ArrayList<String>();
//							temp.add(iclass + " " + elements[1] + " " + elements[2]);
//							classProperties.put(iclass, temp);
//						}
					}
					else {
						continue;
					}
				}
				else {
					continue;
				}
				times++;
				if (times % 100000 == 0)
					System.out.println("已完成" + times + "行");
			}
			for (Iterator<String> it = instanceProperties.keySet().iterator(); it.hasNext();) {
				String instance = it.next();
				ArrayList<String> list = instanceProperties.get(instance);
				for (String str : list) {
					fw.write(instanceClasses.get(instance) + " " + str + "\n");
				}
			}
			fw.close();
			fw2.close();
			br.close();
//			instanceProperties.clear();
			
//			fw = new FileWriter(new File(dir + "/clean/class_properties_mapping.txt"));
//			for (Iterator<String> it = classProperties.keySet().iterator(); it.hasNext();) {
//				ArrayList<String> list = classProperties.get(it.next());
//				for (String str : list) {
//					fw.write(str + "\n");
//				}
//			}
//			fw.close();
//			classProperties.clear();
			
			fw = new FileWriter(new File(dir + "/clean/relationship_type.txt"));
			for (Iterator<String> it = allRelTypes.iterator(); it.hasNext(); ) {
				String type = it.next();
				fw.write(type + "\n");
			}		
			fw.close();
//			allRelTypes.clear();
			
			fw = new FileWriter(new File(dir + "/clean/attribute_type.txt"));
			for (Iterator<String> it = allAttTypes.iterator(); it.hasNext(); ) {
				String type = it.next();
				fw.write(type + "\n");
			}	
			fw.close();
//			allAttTypes.clear();
			
			fw = new FileWriter(new File(dir + "/clean/class_relationship_mapping.txt"));
			for (Iterator<String> it = classRelationships.iterator(); it.hasNext(); ) {
				fw.write(it.next() + "\n");
			}
			fw.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static boolean verifyClassRelationships(String dir) {
		OntModel OWLModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		try {
			OWLModel.read(new FileInputStream(dir + "/dbpedia_3.8.owl"), "");
			BufferedReader br = new BufferedReader(new FileReader(dir + "/clean/class_relationship_mapping.txt"));
			String line = null;
			String[] elements = new String[3];
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 3);
				String classA = DBPediaLabel.PREFIX_OF_CLASS.substring(1) + elements[0];
				String relationship = DBPediaLabel.PREFIX_OF_CLASS.substring(1) + elements[1];
				String classB = DBPediaLabel.PREFIX_OF_CLASS.substring(1) + elements[2];
				ObjectProperty op = OWLModel.getObjectProperty(relationship);
				
				if (op == null) {  // 如果object property不存在
					System.out.println(relationship + " does not exist.");
					continue;
				}
				
				OntResource odomain = op.getDomain();
				if (odomain != null) {
					OntClass domain = odomain.asClass();
					OntClass oc = OWLModel.getOntClass(classA);
					if (!oc.equals(domain) && !domain.hasSubClass(oc)) {  // 如果实例不是domain及其子类
						System.out.println("[" + classA + "] does not match the domain [" + domain.getURI() + "] in OWL.");
					}
				}
								
				OntResource orange = op.getRange();
				if (orange != null) {
					OntClass range = orange.asClass();
					OntClass oc = OWLModel.getOntClass(classB);
					if (!oc.equals(range) && !range.hasSubClass(oc)) {  // 如果实例不是domain及其子类
						System.out.println("[" + classB + "] does not match the range [" + range.getURI() + "] in OWL.");
					}
				}
			}
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		Cleanser.run("D:/DBPedia");
//		Cleanser.verifyClassRelationships("D:/DBPedia");
	}

}
