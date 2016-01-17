package edu.whu.clock.newprobindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import edu.whu.clock.newgraph.GraphManager;
import edu.whu.clock.newprobsearch.SearchPathTyped;

public class FNSetManager {

	private HashMap<String, Integer> fnSet;
	private int[] maxCount;
	
	public FNSetManager(short classNum) {
		this.fnSet = new HashMap<String, Integer>();
		this.maxCount = new int[classNum];
	}
	
	public void loadFromFile(String dir) {
		BufferedReader br = null;
		try {
			File d = new File(dir);
			if (d.isFile()) {
				System.out.println("Error: wrong FNS-Table directory.");
				return;
			}
			for (File f : d.listFiles()) {
				br = new BufferedReader(new FileReader(f));
				int max = 0;
				String fname = f.getName();
				fname = fname.substring(0, fname.lastIndexOf("_"));
				fname = fname.substring(fname.lastIndexOf("_") + 1);
				String line = null;
				while (br.ready()) {
					line = br.readLine();
					int pos = line.lastIndexOf(" ");
					String left = line.substring(0, pos);
					String right = line.substring(pos + 2, line.length() - 1);
					int count = Integer.parseInt(right);
					if (count > max) max = count;
					fnSet.put(fname + "@" + left, count);
				}
				maxCount[Integer.parseInt(fname)] = max;
				br.close();
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean checkNodeSet(short root, SearchPathTyped[] paths) {
		String fns = root + "@";
		for (SearchPathTyped path : paths) {
			fns += path.getNode(path.nodeNum() - 2) + " ";
		}
		if (fnSet.keySet().contains(fns.substring(0, fns.length() - 1)))
			return true;
		return false;
	}
	
	/**
	 * 获取指定邻居节点集合的出现次数
	 * @param root
	 * @param paths
	 * @return 邻居节点集合的出现次数，即文件每一行最后括号中的数字。如果该邻居节点集合不是频繁集，则返回0。
	 */
	public int countNodeSet(short root, SearchPathTyped[] paths) {
		String fns = root + "@";
		for (SearchPathTyped path : paths) {
			fns += path.getNode(path.nodeNum() - 2) + " ";
		}
		Integer count = fnSet.get(fns.substring(0, fns.length() - 1));
		if (count == null) return 0;
		else return count.intValue();
	}
	
	public int getMaxCount(short classID) {
		return maxCount[classID];
	}
	
	// 用于测试
	public int test(short root, short[] nei) {
		String fns = root + "@";
		for (short path : nei) {
			fns += path + " ";
		}
		Integer count = fnSet.get(fns.substring(0, fns.length() - 1));
		if (count == null) return 0;
		else return count.intValue();
	}
	
	// 用于测试
	public static void main(String[] args) {
		FNSetManager fnSet = new FNSetManager((short)272);
		fnSet.loadFromFile(GraphManager.FNSET_FILE_DIR);
		int i = fnSet.test((short)254, new short[]{154, 154, 189});
		System.out.println(i);
		System.out.println(fnSet.getMaxCount((short)5));
	}
}
