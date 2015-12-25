package edu.whu.clock.dbpedia2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.TreeSet;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DBPediaCleanser {

	public static void run(String dir) {
		TreeSet<String> allClasses = new TreeSet<String>();
		TreeSet<String> allInstances = new TreeSet<String>();
		TreeSet<String> allRelTypes = new TreeSet<String>();
		TreeSet<String> allAttTypes = new TreeSet<String>();
		
		OntModel OWLModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		
		try {
			OWLModel.read(new FileInputStream(dir + "/dbpedia_3.8.owl"), "");
			BufferedReader br = new BufferedReader(new FileReader(dir + "/instance_types_en.nt"));
//			FileWriter fw1 = new FileWriter(new File(dir + "/clean/instance_class_mapping.txt"));
			HashMap<String, String> icMappings = new HashMap<String, String>();
			String line = null;
			String[] elements = new String[3];
			long times = 0;
			while (br.ready()) {
				line = br.readLine();
				if (line.startsWith("#"))
					continue;
				line = line.substring(0, line.lastIndexOf(".") - 1);
				elements = line.split(" ", 3);
				if (!elements[2].startsWith("<http://dbpedia.org/ontology/") || !elements[0].startsWith("<http://dbpedia.org/resource/")) {
					continue;
				}
//				elements[0] = elements[0].substring("<http://dbpedia.org/resource/".length(), elements[0].length() - 1);
//				elements[2] = elements[2].substring("<http://dbpedia.org/ontology/".length(), elements[2].length() - 1);
//				if (elements[0].equals("<http://dbpedia.org/resource/Dieselboy>")) {
//					System.out.println();
//				}
				OntClass oc = OWLModel.getOntClass(elements[2].substring(1, elements[2].length() - 1));
				OntClass current = null;
				if (icMappings.containsKey(elements[0])) {
					current = OWLModel.getOntClass(icMappings.get(elements[0]));
					if (oc.hasSubClass(current)) {
						continue;
					}
					else if (current.hasSubClass(oc)) {
						icMappings.put(elements[0], elements[2].substring(1, elements[2].length() - 1));
					}
					else {
						System.out.println(elements[0]);
					}
				}
				else {
					icMappings.put(elements[0], elements[2].substring(1, elements[2].length() - 1));
				}
				
//				if (oc.hasSubClass()) {
//					continue;
//				}
//				allInstances.add(elements[0]);
//				allClasses.add(elements[2]);
//				fw1.write(elements[0] + " " + elements[2] + "\n");
				times++;
				if (times % 100000 == 0)
					System.out.println("已完成" + times + "行");
			}
//			fw1.close();
			br.close();
//			System.gc();
			
//			fw1 = new FileWriter(new File(dir + "/clean/class.txt"));
//			for (Iterator<String> it = allClasses.iterator(); it.hasNext(); ) {
//				String type = it.next();
//				fw1.write(type + "\n");
//			}
//			fw1.close();
//			
//			times = 0;
//			br = new BufferedReader(new FileReader(dir + "/mappingbased_properties_en.nt"));
//			fw1 = new FileWriter(new File(dir + "/clean/instance_attribute_mapping.txt"));
//			FileWriter fw2 = new FileWriter(new File(dir + "/clean/instance_relationship_mapping.txt"));
//			TreeMap<String, ArrayList<String>> lines = new TreeMap<String, ArrayList<String>>();
//			while (br.ready()) {
//				line = br.readLine();
//				if (line.startsWith("#"))
//					continue;
//				line = line.substring(0, line.lastIndexOf(".") - 1);
//				elements = line.split(" ", 3);
//				if (elements[0].startsWith("<http://dbpedia.org/resource/")) {
//					elements[0] = elements[0].substring("<http://dbpedia.org/resource/".length(), elements[0].length() - 1);
//				}
//				else {
//					continue;
//				}
//				if (allInstances.contains(elements[0])) {
//					if (elements[2].startsWith("<http://dbpedia.org/resource/")) {
//						elements[2] = elements[2].substring("<http://dbpedia.org/resource/".length(), elements[2].length() - 1);
//						if (allInstances.contains(elements[2]) && elements[1].startsWith("<http://dbpedia.org/ontology/")) {
//							ObjectProperty op = OWLModel.getObjectProperty(elements[1].substring(1, elements[1].length() - 1));
//							if (op == null) {  // 如果object property不存在
//								continue;
//							}
//							elements[1] = elements[1].substring("<http://dbpedia.org/ontology/".length(), elements[1].length() - 1);
//							allRelTypes.add(elements[1]);
//							fw2.write(elements[0] + " " + elements[1] + " " + elements[2] + "\n");
//						}
//					}
//					else if (elements[2].startsWith("\"")) {
//						if (lines.containsKey(elements[0])) {
//							lines.get(elements[0]).add(elements[0] + " " + elements[1] + " " + elements[2]);
//						}
//						else {
//							ArrayList<String> temp = new ArrayList<String>();
//							temp.add(elements[0] + " " + elements[1] + " " + elements[2]);
//							lines.put(elements[0], temp);
//						}
//						allAttTypes.add(elements[1]);
//					}
//					else {
//						continue;
//					}
//				}
//				else {
//					continue;
//				}
//				times++;
//				if (times % 100000 == 0)
//					System.out.println("已完成" + times + "行");
//			}
//			for (Iterator<String> it = lines.keySet().iterator(); it.hasNext();) {
//				ArrayList<String> list = lines.get(it.next());
//				for (String str : list) {
//					fw1.write(str + "\n");
//				}
//			}
//			fw1.close();
//			fw2.close();
//			
//			fw1 = new FileWriter(new File(dir + "/clean/relationship_type.txt"));
//			fw2 = new FileWriter(new File(dir + "/clean/attribute_type.txt"));
//			for (Iterator<String> it = allRelTypes.iterator(); it.hasNext(); ) {
//				String type = it.next();
//				fw1.write(type + "\n");
//			}
//			for (Iterator<String> it = allAttTypes.iterator(); it.hasNext(); ) {
//				String type = it.next();
//				fw2.write(type + "\n");
//			}			
//			fw1.close();
//			fw2.close();
//			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DBPediaCleanser.run("D:/DBPedia");

	}

}
