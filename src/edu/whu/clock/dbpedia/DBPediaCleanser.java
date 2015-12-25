package edu.whu.clock.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DBPediaCleanser {

	public static void run(String dir) {
		HashSet<String> allTypes = new HashSet<String>();
		HashSet<String> allInstances = new HashSet<String>();
		
		OntModel OWLModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		
		try {
			OWLModel.read(new FileInputStream(dir + "/dbpedia_3.8.owl"), "");
			BufferedReader br = new BufferedReader(new FileReader(dir + "/instance_types_en_onlyleaf.nt"));
			FileWriter fw1 = new FileWriter(new File(dir + "/instance_types_en_clean.nt"));
			String line = null;
			String[] elements = new String[3];
			long times = 0;
			while (br.ready()) {
				line = br.readLine();
				if (line.startsWith("#"))
					continue;
				line = line.substring(0, line.lastIndexOf(".") - 1);
				elements = line.split(" ", 3);
				if (!elements[2].startsWith("<http://dbpedia.org/ontology/")) {
					continue;
				}
				fw1.write(line + "\n");
				allTypes.add(elements[2]);
				allInstances.add(elements[0]);
				times++;
				if (times % 100000 == 0)
					System.out.println("已完成" + times + "行");
			}
			fw1.close();
			br.close();
			System.gc();
			
			fw1 = new FileWriter(new File(dir + "/types_en_clean_onlyleaf.nt"));
			for (Iterator<String> it = allTypes.iterator(); it.hasNext(); ) {
				String type = it.next();
				fw1.write(type + "\n");
			}
			fw1.close();
			
			times = 0;
			br = new BufferedReader(new FileReader(dir + "/mappingbased_properties_en.nt"));
			fw1 = new FileWriter(new File(dir + "/instance_properties_en_clean_onlyleaf.nt"));
			FileWriter fw2 = new FileWriter(new File(dir + "/instance_relationships_en_clean_onlyleaf.nt"));
			TreeMap<String, ArrayList<String>> lines = new TreeMap<String, ArrayList<String>>();
			while (br.ready()) {
				line = br.readLine();
				if (line.startsWith("#"))
					continue;
				line = line.substring(0, line.lastIndexOf(".") - 1);
				elements = line.split(" ", 3);
				if (allInstances.contains(elements[0])) {
					if (elements[2].startsWith("<") && allInstances.contains(elements[2])) {
						fw2.write(line + "\n");
					}
					else if (elements[2].startsWith("\"")) {
						if (lines.containsKey(elements[0])) {
							lines.get(elements[0]).add(line);
						}
						else {
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(line);
							lines.put(elements[0], temp);
						}
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
			for (Iterator<String> it = lines.keySet().iterator(); it.hasNext();) {
				ArrayList<String> list = lines.get(it.next());
				for (String str : list) {
					fw1.write(str + "\n");
				}
			}
			fw1.close();
			fw2.close();
			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DBPediaCleanser.run("D:/DBPedia");

	}

}
