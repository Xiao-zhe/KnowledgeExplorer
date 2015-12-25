package edu.whu.clock.probsearch;

import java.io.IOException;
import java.util.HashMap;

public class ProbIndexEntry {

	private int[] classIDList;
	private double[] probList;

	public ProbIndexEntry(int[] classIDList) {
		this.classIDList = classIDList;
	}

	public ProbIndexEntry(int[] classIDList, double[] probList) {
		this.classIDList = classIDList;
		this.probList = probList;
	}

	public void computeProb(ClassManager cm, HashMap<Integer, Integer> counter) throws IOException {
		probList = new double[classIDList.length];
		for (int i = 0; i < classIDList.length; i++) {
			probList[i] = (double) counter.get(classIDList[i]) / (double) cm.getInstanceNum(classIDList[i]);
		}
//		Iterator<Integer> it = counter.keySet().iterator();
//		int i = 0;
//		while (it.hasNext()) {
//			int classid = it.next();
//			probList[i] = (double) counter.get(classid) / (double) cm.getInstanceNum(classid);
//			i++;
//		}
	}

	public int[] getClassIDList() {
		return classIDList;
	}
	
	public int getClassID(int index) {
		return classIDList[index];
	}

	public double[] getProbList() {
		return probList;
	}
	
	public double getProb(int index) {
		return probList[index];
	}

	public int getLen() {
		return classIDList.length;
	}
}
