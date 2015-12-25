package edu.whu.clock.newgraph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ClassManager {

	private HashMap<String, Short> class2id;
	private String[] id2class;
	private int[] instanceNum;// 根据classid得到该class实例的数量

	public void load(String dir) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/class_1.txt"));
			String line;
			String[] ASplit;
			short id = 0;
			class2id = new HashMap<String, Short>();
			ArrayList<String> classes = new ArrayList<String>();
			ArrayList<Integer> numbers = new ArrayList<Integer>();
			while (br.ready()) {
				line = br.readLine();
				ASplit = line.split(" ", 2);
				classes.add(ASplit[0]);
				class2id.put(ASplit[0], id);
				numbers.add(Integer.parseInt(ASplit[1]));
				id++;
			}
			id2class = new String[classes.size()];
			for (int i = 0; i < classes.size(); i++) {
				id2class[i] = classes.get(i);
			}
			instanceNum = new int[numbers.size()];
			for (int i = 0; i < numbers.size(); i++) {
				instanceNum[i] = numbers.get(i);
			}

			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public short getClassNum() {
		return (short) id2class.length;
	}

	public short getClassID(String className) {
		return class2id.get(className);
	}

	public String getClassName(short classID) {
		return id2class[classID];
	}

	public int getInstanceNum(short classID) {
		return instanceNum[classID];
	}

	public int getInstanceNum(String className) {
		return instanceNum[getClassID(className)];
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		ClassManager CM = new ClassManager();
		// CM.load("D:/testing example");
		CM.load("D:/dbpedia/clean");
		FileOutputStream fs = new FileOutputStream(
				"D:/SerializedFile/ClassManager.ser");
		ObjectOutputStream os = new ObjectOutputStream(fs);
		os.writeObject(CM);
		os.close();
		// for (String i : id2class) {
		// System.out.println(i);
		//
		// }
		// System.out.println("Actor :" + CM.getInstanceNum("Actor"));

		FileInputStream fis = new FileInputStream(
				"D:/SerializedFile/ClassManager.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);

		// ClassManager CM1 = new ClassManager();
		ClassManager CM1 = (ClassManager) ois.readObject();

		// int N = CM.getClassNum();
		// System.out.println(N);
		// for(String str:CM1.getClasses()){
		//
		// System.out.print(str);
		//
		// }
		// System.out.println(CM1.id2class[0]);
		System.out.println(CM1.getInstanceNum((short) 2));
		ois.close();
	}
}
