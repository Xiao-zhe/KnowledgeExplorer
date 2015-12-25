package edu.whu.clock.database;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class ClassDB implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5504246085096097312L;
	private HashMap<String, Integer> class2id;
	private HashMap<Integer, String> id2class;
	
	public ClassDB() {
		class2id = new HashMap<String, Integer>();
		id2class = new HashMap<Integer, String>();
	  
	}
	
	public void load(String dir) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/class.txt"));
			String line = null;
			int id = 0;
			while (br.ready()) {
				line = br.readLine();
				class2id.put(line, id);
				id2class.put(id, line);
				id++;
			}
			br.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public int getClassID(String className) {
		return class2id.get(className);
	}
	
	public String getClassName(int classid) {
		return id2class.get(classid);
	}

         public static void main(String[] args) throws IOException{
        	ClassDB classDB =new ClassDB();
//        	classDB.load("D:/testing example");
        	classDB.load("D:/DBpedia/clean");
        	FileOutputStream fos = new FileOutputStream("D:/SerializedFile/ClassDB.ser");
        	ObjectOutputStream oos = new ObjectOutputStream(fos);
        	oos.writeObject(classDB);
        	oos.close();
        	return;
        	
         }
}