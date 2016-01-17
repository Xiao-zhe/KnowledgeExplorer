package edu.whu.clock.newgraph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 */
public class InstanceManager implements Serializable {

	private static final long serialVersionUID = 4677181773279474021L;
	private HashMap<String, Integer> instance2id;
	private String[] id2instance;
	private short[] instance2class;
	private int[][] class2instance;

	public void load(String dir, ClassManager classManager) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir + "/instance_class_mapping.txt"));
			String[] elements = new String[2];
			String line = null;
			int id = 0;
			instance2id = new HashMap<String, Integer>();
			ArrayList<String> instanceList = new ArrayList<String>();
			ArrayList<Short> classList = new ArrayList<Short>();
			HashMap<Short, ArrayList<Integer>> classinstanceList = new HashMap<Short, ArrayList<Integer>>();
			while (br.ready()) {
				line = br.readLine();
				elements = line.split(" ");
				instance2id.put(elements[0], id);
				instanceList.add(elements[0]);
				short classID = classManager.getClassID(elements[1]);
				classList.add(classID);
				if (classinstanceList.containsKey(classID)) {
					classinstanceList.get(classID).add(id);
				}
				else {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(id);
					classinstanceList.put(classID, list);
				}
				id++;
			}
			System.out.println("Num of entities: " + id);
			id2instance = instanceList.toArray(new String[instanceList.size()]);
			instance2class = new short[classList.size()];
			for (int i = 0; i < classList.size(); i++) {
				instance2class[i] = classList.get(i);
			}
			class2instance = new int[classManager.getClassNum()][];
			for (short i = 0; i < classManager.getClassNum(); i++) {
				if (!classinstanceList.containsKey(i)) {
					continue;
				}
				ArrayList<Integer> list = classinstanceList.get(i);
				class2instance[i] = new int[list.size()];
				for (int j = 0; j < list.size(); j++) {
					class2instance[i][j] = list.get(j);
				}
//				Arrays.sort(class2instance[i]);
			}
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public int getInstanceID(String instanceName) {
		return instance2id.get(instanceName);
	}

	public String getInstanceName(int instanceid) {
		return id2instance[instanceid];
	}
	
	public short getClassID(String instanceName) {
		return instance2class[instance2id.get(instanceName)];
	}
	
	public short getClassID(int instanceID) {
		return instance2class[instanceID];
	}
	public int getInstanceNum(){
		return id2instance.length;
	}
	
	public int[] getInstanceSet(short classID) {
		return class2instance[classID];
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(
				"D:/SerializedFile/ClassManager.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		ClassManager cla = (ClassManager) ois.readObject();
		ois.close();
		// instanceManager.load("D:/testing example", cla);
		InstanceManager instanceManager = new InstanceManager();
		instanceManager.load("D:/dbpedia/clean", cla);
		FileOutputStream fos = new FileOutputStream(
				"D:/SerializedFile/InstanceManager.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(instanceManager);
		oos.close();

	}
}
