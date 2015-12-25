package edu.whu.clock.newgraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class EdgeTypeManager {

	private HashMap<String, Short> name2id;
	private HashMap<Short, String> id2name;
	
	public EdgeTypeManager() {
		this.name2id = new HashMap<String, Short>();
		this.id2name = new HashMap<Short, String>();
	}

	public void build(String dir) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(dir
					+ "/relationship_type.txt"));
			String line = null;
			short id = 0;
			while (br.ready()) {
				line = br.readLine();
				id++;
				name2id.put(line, id);
				id2name.put(id, line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public short getID(String name) {
		return name2id.get(name);
	}
	
	public String getName(short id) {
		return id2name.get(id);
	}
}
