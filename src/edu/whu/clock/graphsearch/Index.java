//包含关键词的类

package edu.whu.clock.graphsearch;

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

import edu.whu.clock.database.ClassDB;
import edu.whu.clock.graphsearch.util.SequentialIntArray;
import edu.whu.clock.graphsearch.util.SequentialIntArrayBinding;

public class Index {

	private ClassDB classDB;
	private Environment env;
	private Database indexDB;

	private String dir;
	private SequentialIntArrayBinding siaBinding;

	public Index() {
		classDB = new ClassDB();
		siaBinding = new SequentialIntArrayBinding();
	}

	public int[] get(String keyword) {
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
	
	public String[] getClasses(String keyword) {
		int[] temp = get(keyword);
		String[] result = new String[temp.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = classDB.getClassName(temp[i]);
		}
		return result;
	}

	public void init(String dir, String dbEnvPath, String dbName) {
		this.dir = dir;
		classDB.load(dir);
		openDB(dbEnvPath, dbName);
	}

	public void build() {
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
					int classID = classDB.getClassID(elements[0]);
					String[] terms = elements[3].substring(1,
							elements[3].lastIndexOf("\"")).split(" ");
					for (String term : terms) {
						term = term.toLowerCase();
						SequentialIntArray si = null;
						if (term.length() > 0) {
							if (map.containsKey(term)) {
								si = map.get(term);
							} else {
								si = new SequentialIntArray();
							}
							si.insert(classID);
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
		Index index = new Index();
		index.init("D:/dbpedia/clean", "D:/dbpedia index", "index");
//		index.init("D:/testing example", "D:/index", "index");
		index.build();
		String[] classes = index.getClasses("tom");
		for (String str : classes) {
			System.out.println(str);
		}
		index.close();
	}

}
