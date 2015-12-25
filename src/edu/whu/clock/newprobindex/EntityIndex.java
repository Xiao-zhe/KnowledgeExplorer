
//包含关键词key的有哪些实例
package edu.whu.clock.newprobindex;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

//import edu.whu.clock.database.ClassDB;
import edu.whu.clock.graphsearch.util.SequentialIntArray;
import edu.whu.clock.graphsearch.util.SequentialIntArrayBinding;
import edu.whu.clock.newgraph.InstanceManager;

public class EntityIndex {

	private Environment env;
	private Database indexDB;
	private String dbName = "ei-index";
	private SequentialIntArrayBinding siaBinding;
	
	public EntityIndex(String dbEnvPath) {
		siaBinding = new SequentialIntArrayBinding();
		try {
			File f = new File(dbEnvPath);
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setCachePercent(75);
			env = new Environment(f, envConfig);
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			indexDB = env.openDatabase(null, dbName, dbConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int[] getAinstID(String keyword) {  //get the IDs of instance from attribute-instance index 
		int[] result = null;
		try {
			DatabaseEntry key = new DatabaseEntry(keyword.toLowerCase()
					.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			if (indexDB.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = siaBinding.entryToObject(value).getArray();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
//	public String[] getAinstName(String keyword) {//get the names of instance from attribute-instance index 
//		int[] temp = getAinstID(keyword);
//		String[] result = new String[temp.length];
//		for (int i = 0; i < result.length; i++) {
//			result[i] = instanceManager.getInstanceName(temp[i]);
//		}
//		return result;
//	}

	public void build(String dir, InstanceManager instanceManager) {
		HashMap<String, SequentialIntArray> map = new HashMap<String, SequentialIntArray>();
		
		try {

			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/instance_attribute_mapping.txt"));
			String line = null;
			String[] elements = null;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 4);
				if (elements[3].endsWith("@en")
						&& !elements[2].startsWith("<http://www")) {
					int instanceID = instanceManager.getInstanceID(elements[1]);
					String[] terms = elements[3].substring(1,
							elements[3].lastIndexOf("\"")).split(" ");//????????????????????????????????????
					for (String term : terms) {
						term = term.toLowerCase();
						SequentialIntArray si = null;
						if (term.length() > 0) {
							if (map.containsKey(term)) {
								si = map.get(term);
							} else {
								si = new SequentialIntArray();
							}
							si.insert(instanceID);
							map.put(term, si);
						}
					}
				}
			}
			br.close();

			for (String term : map.keySet()) {
				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
				DatabaseEntry value = new DatabaseEntry();
				siaBinding.objectToEntry(map.get(term), value);
				indexDB.put(null, key, value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		try {
			if (indexDB != null) {
				indexDB.close();
			}
			if (env != null) {
				env.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		EntityIndex index = new EntityIndex();
//		index.init("D:/dbpedia/clean", "D:/entityindex", "index");
////		index.init("D:/testing example", "D:/entityindex", "index");
//		index.build();
//		String[] instances = index.getAinstName("Disease");
//		for (String str : instances) {
//			System.out.println(str);
//		}
//		index.close();
//	}

}

