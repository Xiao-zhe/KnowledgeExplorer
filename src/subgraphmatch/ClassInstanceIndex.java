
//class°üº¬ÄÄÐ©instance
package subgraphmatch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
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

public class ClassInstanceIndex {

	private InstanceDB instanceDB;
	private Environment env;
	private Database indexDB;

	private String dir;
	private SequentialIntArrayBinding siaBinding;

	public ClassInstanceIndex() {
		instanceDB = new InstanceDB();
		siaBinding = new SequentialIntArrayBinding();
	}

	public int[] getCinstID(String keyword) {  //get the IDs of instance from class-instance index 
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
	
	
	public String[] getCinstName(String keyword) {//get the names of instance from class-instance index 
		int[] temp = getCinstID(keyword);
		String[] result = new String[temp.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = instanceDB.getInstanceName(temp[i]);
		}
		return result;
	}
	
	

	public void init(String dir, String dbEnvPath, String dbName) {
		this.dir = dir;
		instanceDB.load(dir);
		openDB(dbEnvPath, dbName);
	}

	public void build() {
		HashMap<String, SequentialIntArray> map = new HashMap<String, SequentialIntArray>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/instance_class_mapping.txt"));
			String line = null;
			String[] elements = null;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ", 2);
					int instanceID = instanceDB.getInstanceID(elements[0]);
					String term = elements[1];
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
		ClassInstanceIndex Cindex = new ClassInstanceIndex();
//		Cindex.init("D:/testing example", "D:/classInstanceIndex", "index");
		Cindex.init("D:/dbpedia/clean", "D:/classInstanceIndex", "index");
		Cindex.build();

//		String[] instances = Cindex.getCinstName("TelevisionShow");
//		String[] instances = Cindex.getCinstName("Actor");
//		
//		for (String str : instances) {
//			System.out.println(str);
//		}
          int[] instances = Cindex.getCinstID("Disease");
		
		for (int str : instances) {
			System.out.println(str);
		}
		
		Cindex.close();
	}

}


