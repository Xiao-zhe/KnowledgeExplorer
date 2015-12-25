package subgraphmatch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import edu.whu.clock.newgraph.ClassManager;

public class InstanceDB implements Serializable{

	private static final long serialVersionUID = 391515429947213876L;
	private HashMap<String, Integer> instance2id;  
	private HashMap<Integer, String> id2instance;
	public InstanceDB() {
		instance2id = new HashMap<String, Integer>();
		id2instance = new HashMap<Integer, String>();
	}
	
	public void load(String dir)  {
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(dir  /////////////////////////////////////////////////
					+ "/instance_class_mapping.txt"));
			String[] elements = new String[2];
			String line = null;
			int id = 0;
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ");///////////////////////////////////////////////////////////////////////////////
				
				instance2id.put(elements[0], id);
			
				id2instance.put(id, elements[0]);
				id++;
			}
			
			br.close();
		}
		catch (IOException ex) {
			ex.printStackTrace(); 
		}
		//System.out.println(instance2id.size());
	}
	
	public int getInstanceID(String instanceName) {
		return instance2id.get(instanceName);
	}
	/**
	 * shirnnirninirninirnirnirnirni
	 * @param instanceid
	 * @return
	 */
	public String getInstanceName(Integer instanceid) {
		return id2instance.get(instanceid);
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		InstanceDB instanceDB = new InstanceDB();
//		instanceDB.load("D:/testing example");
		instanceDB.load("D:/dbpedia/clean");
		FileOutputStream fos = new FileOutputStream("D:/SerializedFile/InstanceDB.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(instanceDB);
		oos.close();
		FileInputStream fis= new FileInputStream("D:/SerializedFile/InstanceDB.ser");
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	InstanceDB ins = new InstanceDB();
    	ins = (InstanceDB) ois.readObject();
    	System.out.println(ins.getInstanceID("Autism"));
    	
    	
    	ClassManager CM = new ClassManager();
		CM.load("D:/dbpedia/clean");
    	FileInputStream fi = new FileInputStream("D:/SerializedFile/ClassManager.ser");
    	ObjectInputStream oi = new ObjectInputStream(fi);
    	
//    		ClassManager CM1 = new ClassManager();
			ClassManager CM1 = (ClassManager) oi.readObject();
		 
		
//    	int N = CM.getClassNum();
//    	System.out.println(N);
//    	for(String str:CM1.getClasses()){
//    		
//    		System.out.print(str);
//    		
//    	}
//			System.out.println(CM1.id2class[0]);
    	     System.out.println(CM1.getInstanceNum((short)2));
	}
}
         

