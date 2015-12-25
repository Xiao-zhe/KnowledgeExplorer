package edu.whu.clock.newprobindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import edu.whu.clock.newprobsearch.SearchPathTyped;

public class FNSetManager {

	private HashSet<String> fnSet;
	
	public void loadFromFile(String dir) {
		fnSet = new HashSet<String>();
		BufferedReader br = null;
		try {
			File d = new File(dir);
			if (d.isFile()) {
				System.out.println("Error: wrong FNS-Table directory.");
				return;
			}
			for (File f : d.listFiles()) {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				while (br.ready()) {
					line = br.readLine();
					String fname = f.getName();
					fname = fname.substring(0, fname.lastIndexOf("_"));
					fname = fname.substring(fname.lastIndexOf("_") + 1);
					fnSet.add(fname + "@" + line.substring(0, line.lastIndexOf(" ")));
				}
				br.close();
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
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
	
	public boolean check(short root, SearchPathTyped[] paths) {
		String fns = root + "@";
		for (SearchPathTyped path : paths) {
			fns += path.getNode(path.nodeNum() - 2) + " ";
		}
		if (fnSet.contains(fns.substring(0, fns.length() - 1)))
			return true;
		return false;
	}
}
