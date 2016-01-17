package subgraphmatch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeMap;

public class Fnesm {
	public static final String FNESET_FILE_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/fneset/minsupport5%_N=4/";
	public int line_amount;
	static BufferedWriter bw;

	// public Fnsm() throws IOException{
	// bw = new BufferedWriter(new
	// FileWriter(FNSET_FILE_DIR+"/fnsm_inputdata_24_AutoRacingLeague"));
	// }
	public EdgeHeadNode file_to_db(String filename)
			throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		EdgeHeadNode head = new EdgeHeadNode();
		EdgeHeadNode currentHead = new EdgeHeadNode();
		currentHead = head;
		EdgeNode currentNode = new EdgeNode();
		try {
			String line = null;
			line_amount = 0;
			line = br.readLine();
			line_amount++;
			if (line != null) {
				String[] nums = line.split(" ");
				EdgeNode nod = new EdgeNode(nums[0]);
				currentNode = nod;
				currentHead.setRight(nod);
				if (nums.length > 1)
					for (int i = 1; i < nums.length; i++) {
						EdgeNode node = new EdgeNode(nums[i]);
						currentNode.setRight(node);
						currentNode = node;
					}
				while (br.ready()) {
					line = br.readLine();
					line_amount++;
					nums = line.split(" ");
					nod = new EdgeNode(nums[0]);
					currentNode = nod;
					EdgeHeadNode headNod = new EdgeHeadNode();
					headNod.setRight(nod);
					currentHead.setDown(headNod);
					currentHead = headNod;
					if (nums.length > 1)
						for (int i = 1; i < nums.length; i++) {
							EdgeNode node = new EdgeNode(nums[i]);
							currentNode.setRight(node);
							currentNode = node;

						}
				}
				br.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return head;
	}

	public EdgeHeadNode create_standard_database(EdgeHeadNode head) {
		if (head == null) {
			System.out.println("create_standard_database参数为空！");
		}
		EdgeHeadNode fromHead = head;
		EdgeHeadNode toHead = null;
		EdgeHeadNode result = new EdgeHeadNode();
		EdgeNode from_node_current = null;
		EdgeNode to_node_current = null;
		if (head != null) {
			toHead = new EdgeHeadNode();
			result = toHead;

		}
		while (fromHead != null) {
			from_node_current = fromHead.getRight();
			fromHead = fromHead.getDown();

			to_node_current = new EdgeNode(from_node_current.getValue());
			toHead.setRight(to_node_current);

			while (from_node_current.getRight() != null) {
				from_node_current = from_node_current.getRight();
				if (to_node_current.getValue().equals(
						from_node_current.getValue())) {

					continue;

				} else {
					EdgeNode tempNode = new EdgeNode(
							from_node_current.getValue());
					to_node_current.setRight(tempNode);
					to_node_current = tempNode;

				}

			}
			if (fromHead != null) {
				EdgeHeadNode tempHead = new EdgeHeadNode();
				toHead.setDown(tempHead);
				toHead = tempHead;
			}

		}

		return result;
	}

	public EdgeHeadNode create_project_database(EdgeHeadNode db, String x) {
		EdgeHeadNode res = null;
		EdgeHeadNode currentHead = db;
		EdgeNode currentNode = null;
		EdgeNode to_node_current = null;
		EdgeHeadNode to_head_current = null;
		if (db != null) {

			while (currentHead != null) {
				currentNode = currentHead.getRight();
				currentHead = currentHead.getDown();

				while (currentNode != null && !currentNode.getValue().equals(x)) {
					currentNode = currentNode.getRight();
				}

				if (currentNode != null) {
					currentNode = currentNode.getRight();
					if (currentNode != null && res == null) {
						to_head_current = new EdgeHeadNode();
						res = to_head_current;

						to_node_current = new EdgeNode();
						to_head_current.setRight(to_node_current);

					} else if (currentNode != null && res != null) {
						to_head_current.setDown(new EdgeHeadNode());
						to_head_current = to_head_current.getDown();

						to_node_current = new EdgeNode();
						to_head_current.setRight(to_node_current);

					}

					while (currentNode != null) {
						to_node_current.setValue(currentNode.getValue());
						currentNode = currentNode.getRight();
						if (currentNode != null) {
							to_node_current.setRight(new EdgeNode());
							to_node_current = to_node_current.getRight();
						}
					}

				}

			}
		}
		return res;
	}

	public TreeMap<String, Integer> countValue(EdgeHeadNode deduplicatedDB) {
		if (deduplicatedDB == null)
			System.out.println("countValue参数为空!\n");
		TreeMap<String, Integer> count = new TreeMap<String, Integer>(
				new Comparator<String>() {

					@Override
					public int compare(String s1, String s2) {
						// TODO Auto-generated method stub
						String[] s1s = s1.split("#");
						String[] s2s = s2.split("#");
						if (Integer.valueOf(s1s[0]) > Integer.valueOf(s2s[0])) {
							return 1;
						}
						else if (Integer.valueOf(s1s[0]) < Integer.valueOf(s2s[0])) {
							return -1;
						}
						else {
							if (Integer.valueOf(s1s[1]) > Integer.valueOf(s2s[1])) {
								return 1;
							}
							else if (Integer.valueOf(s1s[1]) < Integer.valueOf(s2s[1])) {
								return -1;
							}
							else {
								if (s1s[2].equals("true") && !s2s[2].equals("true")) {
									return 1;
								}
								else if (!s1s[2].equals("true") && s2s[2].equals("true")) {
									return -1;
								}
								else {
									return 0;
								}
							}
						}}});
		EdgeNode currentNode = null;
		EdgeHeadNode headNode = null;
		headNode = deduplicatedDB;
		while (headNode != null) {

			currentNode = headNode.getRight();
			headNode = headNode.getDown();
			while (currentNode != null) {
				String key = currentNode.getValue();
				if (count.containsKey(key)) {
					int val = count.get(key);
					val++;
					count.put(key, val);
				} else
					count.put(key, 1);
				currentNode = currentNode.getRight();
			}

		}
		return count;
	}

	public void dmine4(EdgeHeadNode db, String[] prefix, int N)
			throws IOException {
		int n = N;
		n++;
		if (n > 4)
			return;
		if (db == null) {
			System.out.println("database is null!\n");
			return;
		}

		String[] pre;
		pre = new String[4];

		for (int k = 0; k < 4; k++) {
			pre[k] = prefix[k];
		}

		EdgeHeadNode res1 = create_standard_database(db);

		TreeMap<String, Integer> co = countValue(res1);
		for (String key : co.keySet()) {
			if (co.get(key) >= line_amount * 0.05) {
				for (int k = 0; k < 4; k++) {
					pre[k] = prefix[k];
				}
				int j = 0;
				if (pre[0] != null) {

					while (pre[j] != null) {
						bw.write(pre[j] + " ");
						bw.flush();
						j++;
					}
					bw.write(key + " (" + co.get(key) + ")");
					bw.newLine();
					bw.flush();
					pre[j] = key;
				} else
					pre[0] = key;

				EdgeHeadNode pdb = create_project_database(db, key);

				dmine4(pdb, pre, n);
			}
		}

	}
	
	public void dmine5(EdgeHeadNode db, String[] prefix, int N)
			throws IOException {
		int n = N;
		n++;
		if (n > 5)
			return;
		if (db == null) {
//			System.out.println("database is null!\n");
			return;
		}

		String[] pre;
		pre = new String[5];

		for (int k = 0; k < 5; k++) {
			pre[k] = prefix[k];
		}

		EdgeHeadNode res1 = create_standard_database(db);

		TreeMap<String, Integer> co = countValue(res1);
		for (String key : co.keySet()) {
			if (co.get(key) >= line_amount * 0.05) {
				for (int k = 0; k < 5; k++) {
					pre[k] = prefix[k];
				}
				int j = 0;
				if (pre[0] != null) {

					while (pre[j] != null) {
						bw.write(pre[j] + " ");
//						bw.flush();
						j++;
					}
					bw.write(key + " (" + co.get(key) + ")");
					bw.newLine();
//					bw.flush();
					pre[j] = key;
				} else
					pre[0] = key;

				EdgeHeadNode pdb = create_project_database(db, key);

				dmine5(pdb, pre, n);
			}
		}

	}

	public static void main(String[] args) throws IOException {
		Fnesm fnesm = new Fnesm();
		// HeadNode he1 =
		// fnsm.file_to_db("D:/fnsm_db/fnsm_inputdata_24_AutoRacingLeague");

		File file = new File(
				"D:/experiment data/knowledge graph explorer/dbpedia-old/fnsm_db");
		File[] files = file.listFiles();
		for (File f : files) {
			System.out.println(f.getName());
			if (f.length() != 0) {
				String[] pref;
				pref = new String[4];
				EdgeHeadNode he1 = fnesm
						.file_to_db("D:/experiment data/knowledge graph explorer/dbpedia-old/fnsm_db/"
								+ f.getName());
				// bw = new BufferedWriter(new
				// FileWriter(FNSET_FILE_DIR+f.getName()));
				// String[] filename = f.getName().split("_");
				bw = new BufferedWriter(new FileWriter(FNESET_FILE_DIR
						+ f.getName()));
				fnesm.dmine4(he1, pref, 0);
			}
			bw.close();
		}

		// int[] co = fnsm.countValue(he2);
		// int j=0;
		// for(int i : co){
		//
		// System.out.println(j+":"+i);
		// j++;
		// }
		// HeadNode he = fnsm.create_project_database(he2, (short) 69);
		// while(he != null){
		// HeadNode head = he;
		// he = he.getDown();
		// Node node = head.getRight();
		// while(node!=null){
		// System.out.print(node.getValue()+" ");
		// node = node.getRight();
		// }
		// System.out.println();
		// }
		// System.out.println(line_amount);
	}

}
