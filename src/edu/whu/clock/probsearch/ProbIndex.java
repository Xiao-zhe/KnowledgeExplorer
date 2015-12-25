//keyword包含在哪些class中

package edu.whu.clock.probsearch;

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

import edu.whu.clock.graphsearch.util.SequentialIntArray;

public class ProbIndex {

//	private ClassDB classDB;
	private ClassManager classManager;
	private Environment env;
	private Database indexDB;

	private String dir;
	private ProbIndexEntryBinding ieBinding;

	public ProbIndex() {
//		classDB = new ClassDB();
		classManager = new ClassManager();
		ieBinding = new ProbIndexEntryBinding();
	}

	public ProbIndexEntry get(String keyword) {
		ProbIndexEntry result = null;
		try {
			DatabaseEntry key = new DatabaseEntry(keyword.toLowerCase()
					.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			if (indexDB.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				if (value != null) {
					result = ieBinding.entryToObject(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public String[] getClasses(String keyword) {
		int[] temp = get(keyword).getClassIDList();
		String[] result = new String[temp.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = classManager.getClassName(temp[i]);
		}
		return result;
	}

	public void init(String dir, String dbEnvPath, String dbName) {
		this.dir = dir;
//		classDB.load(dir);
		classManager.load(dir);
		openDB(dbEnvPath, dbName);
	}

	public void build() {
		HashMap<String, HashMap<Integer, Integer>> map = new HashMap<String, HashMap<Integer, Integer>>();
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
					int classID = classManager.getClassID(elements[0]);
					String[] terms = elements[3].substring(1, elements[3].lastIndexOf("\"")).split(" ");
					for (String term : terms) {
						term = term.toLowerCase();
						HashMap<Integer, Integer> temp = null;
						if (term.length() > 0) {
							if (map.containsKey(term)) {
								temp = map.get(term);
								if(temp.get(classID)==null)
									temp.put(classID, 1);
								else
								{
									int i = temp.get(classID);
									i++;
									temp.put(classID, i);
								}
							} else {
								temp = new HashMap<Integer, Integer>();
								temp.put(classID, 1);
								map.put(term, temp);
							}
						}
					}
				}

			}
			br.close();

			for (String term : map.keySet()) {
				HashMap<Integer, Integer> temp = map.get(term);
				DatabaseEntry key = new DatabaseEntry(term.getBytes("UTF-8"));
				SequentialIntArray si = new SequentialIntArray();
				for (int i : temp.keySet()) {
					si.insert(i);
				}
				ProbIndexEntry ie = new ProbIndexEntry(si.getArray());
				ie.computeProb(classManager, temp);
				DatabaseEntry value = new DatabaseEntry();
				ieBinding.objectToEntry(ie, value);
				indexDB.put(null, key, value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void openDB(String dbEnvPath, String dbName) {
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

	public static void main(String[] args) {
		ProbIndex index = new ProbIndex();
		index.init("D:/dbpedia/clean", "D:/dbpedia index", "index");
		index.build();
		String[] classes = index.getClasses("tom");
		for (String str : classes) {
			System.out.println(str);
		}
		index.close();
	}

}
