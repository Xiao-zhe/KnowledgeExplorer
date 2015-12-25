package edu.whu.clock.probsearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClassManager {

	private HashMap<String, Integer> class2id;
	private String[] id2class;
	private int[] instanceNum;
	
	public ClassManager() {
		class2id = new HashMap<String, Integer>();
	}
	
	public void load(String dir) {
		ArrayList<String> classes = new ArrayList<String>();
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		try {
//			BufferedReader br = new BufferedReader(new FileReader(dir+ "/class.txt"));
			BufferedReader br = new BufferedReader(new FileReader(dir+ "/class_1.txt"));
			String str;
			String[] ASplit;
			int id = 0;
			while (br.ready()) {
				str = br.readLine();
				ASplit = str.split(" ", 2);
				
				class2id.put(ASplit[0], id);
				classes.add(ASplit[0]);
				numbers.add(Integer.parseInt(ASplit[1], 10));
				id++;
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		id2class = new String[classes.size()];
		for (int i = 0; i < classes.size(); i++) {
			id2class[i] = classes.get(i);
		}
		instanceNum = new int[numbers.size()];
		for (int i = 0; i < numbers.size(); i++) {
			instanceNum[i] = numbers.get(i);
		}
	}
	
	public int getClassID(String className) {
		return class2id.get(className);
	}
	
	public String getClassName(int classid) {
		return id2class[classid-1];
	}
	
	public int getInstanceNum(int classID) {
		return instanceNum[classID-1];
	}

//	public static void main(String[] args) throws IOException {
//		ClassManager NM = new ClassManager("D:/dbpedia/clean/class2num.txt");
//		for (int i : NM.class2num.keySet()) {
//			System.out.println(i);
//			System.out.println(NM.class2num.get(i));
//		}
//	}
}
