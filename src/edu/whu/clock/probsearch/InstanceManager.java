package edu.whu.clock.probsearch;
//create the public HashMap<String, Integer> instance2class,根据实例得到该实例所属的class

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import subgraphmatch.InstanceDB;

public class InstanceManager implements Serializable{
	
	public HashMap<String, Integer> instance2class = new HashMap<String, Integer>();
    public HashMap<Integer,Integer> instanceid2classid =new HashMap<Integer,Integer>();
	
	public void load(String dir, ClassManager classManager) {
		String line = null;
		String ASplit[] = new String[2];
		InstanceDB instanceDB = new InstanceDB();
		instanceDB.load("D:/testing example");
//		HashMap<Integer, ArrayList<String>> class2instance = new HashMap<Integer, ArrayList<String>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir + "/instance_class_mapping.txt"));
			while (br.ready()) {
				line = br.readLine();
				ASplit = line.split(" ", 2);
				int instanceid = instanceDB.getInstanceID(ASplit[0]);
				int class_id = classManager.getClassID(ASplit[1]);
				instance2class.put(ASplit[0], class_id);
				instanceid2classid.put(instanceid, class_id);

//				if (class2instance.get(class_id) == null) {
//					ArrayList<String> instance_team = new ArrayList<String>();
//					instance_team.add(ASplit[0]);
//					class2instance.put(class_id, instance_team);
//				} else {
//					ArrayList<String> instance_team = class2instance
//							.get(class_id);
//					instance_team.add(ASplit[0]);
//					class2instance.put(class_id, instance_team);
//				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	public int getInsclassid(int ins){
		return instanceid2classid.get(ins);
	}
	public static void main(String args[]) throws IOException{
		InstanceManager instanceManager = new InstanceManager();
		ClassManager classManager = new ClassManager();
		classManager.load("D:/testing example");
		instanceManager.load("D:/testing example", classManager);
		int a = instanceManager.getInsclassid(4);
		System.out.println(a);
		FileOutputStream fos = new FileOutputStream("D:/SerializedFile/InstanceManager.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(instanceManager);
		oos.close();
	}
}
