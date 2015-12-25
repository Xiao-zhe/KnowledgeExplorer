package subgraphmatch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Fnsm {
	public static final String FNSET_FILE_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/fnset/minsupport5%_N=6/";
	public int line_amount;
	static BufferedWriter bw;
//	public Fnsm() throws IOException{
//		 bw = new BufferedWriter(new FileWriter(FNSET_FILE_DIR+"/fnsm_inputdata_24_AutoRacingLeague"));
//	}
	public HeadNode file_to_db(String filename) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		HeadNode head = new HeadNode();
		HeadNode currentHead = new HeadNode();
		currentHead = head;
		Node currentNode = new Node();
		try {
			String line = null;
			line_amount = 0;
			line = br.readLine();
			line_amount++;
			if(line!=null){
			String[] nums = line.split(" ");
			Node nod = new Node(Short.valueOf(nums[0]));
			currentNode = nod;
			currentHead.setRight(nod);
			if (nums.length > 1)
				for (int i = 1; i < nums.length; i++) {
					short num = Short.valueOf(nums[i]);
					Node node = new Node(num);
					currentNode.setRight(node);
					currentNode = node;
				}
			while (br.ready()) {
				line = br.readLine();
				line_amount++;
				nums = line.split(" ");
				nod = new Node(Short.valueOf(nums[0]));
				currentNode = nod;
				HeadNode headNod = new HeadNode();
				headNod.setRight(nod);
				currentHead.setDown(headNod);
				currentHead = headNod;
				if (nums.length > 1)
					for (int i = 1; i < nums.length; i++) {
						short num = Short.valueOf(nums[i]);
						Node node = new Node(num);
						currentNode.setRight(node);
						currentNode = node;

					}
			}
			br.close();
		} 
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return head;
	}
	
	
	public HeadNode create_standard_database(HeadNode head){
		if(head==null){
			System.out.println("create_standard_database参数为空！");
		}
		HeadNode fromHead = head;
		HeadNode toHead = null;
		HeadNode result = new HeadNode();
		Node from_node_current = null;
		Node to_node_current = null;
		if(head!=null){
	        toHead = new HeadNode();
			result = toHead;
			
		}
		while(fromHead!=null){
			from_node_current = fromHead.getRight();
			fromHead = fromHead.getDown();
			
			to_node_current = new Node(from_node_current.getValue());
			toHead.setRight(to_node_current);
			
			while(from_node_current.getRight()!=null){
				from_node_current = from_node_current.getRight();
				if (to_node_current.getValue() == from_node_current.getValue())
				{

					continue;

				}
				else
				{
					Node tempNode = new Node(from_node_current.getValue());
					to_node_current.setRight(tempNode);
					to_node_current = tempNode;


				}

			}
			if(fromHead!=null){
				HeadNode tempHead = new HeadNode();
				toHead.setDown(tempHead);
				toHead = tempHead;
			}
			
		}
	
		return result;
	}
	
	
	public HeadNode create_project_database(HeadNode db, short x) {
		HeadNode res = null;
		HeadNode currentHead = db;
		Node currentNode = null;
		Node to_node_current = null;
		HeadNode to_head_current = null;
		if (db!=null) {

			while (currentHead!=null) {
				currentNode = currentHead.getRight();
				currentHead = currentHead.getDown();

				while (currentNode!=null && currentNode.getValue() != x) {
					currentNode = currentNode.getRight();
				}

				if (currentNode!=null) {
					currentNode = currentNode.getRight();
					if (currentNode!=null && res == null) {
						to_head_current = new HeadNode();
						res = to_head_current;

						to_node_current = new Node();
						to_head_current.setRight(to_node_current);
			

					} else if (currentNode!=null && res!=null) {
						to_head_current.setDown(new HeadNode());
						to_head_current = to_head_current.getDown();

						to_node_current = new Node();
						to_head_current.setRight(to_node_current);

					}

					while (currentNode!=null) {
						to_node_current.setValue(currentNode.getValue());
						currentNode = currentNode.getRight();
						if (currentNode!=null) {
							to_node_current.setRight(new Node());
							to_node_current = to_node_current.getRight();
						}
					}

				}

			}
		}
		return res;
	}
	
	

	
	public int[] countValue(HeadNode deduplicatedDB) {
		if (deduplicatedDB == null)
			System.out.println("countValue参数为空!\n");
		int[] count;
		count = new int[272];
		Node currentNode = null;
		HeadNode headNode = null;
		headNode = deduplicatedDB;
		while (headNode!=null) {

			currentNode = headNode.getRight();
			headNode = headNode.getDown();
			while (currentNode!=null) {
				count[currentNode.getValue()]++;
				currentNode = currentNode.getRight();
			}

		}
		return count;
	}
	
	
	public void dmine6(HeadNode db, int[] prefix, int N) throws IOException {
		int n = N;
		n++;
		if (n > 6)
			return;
		if (db == null) {
			System.out.println("database is null!\n");
			return;
		}

		int[] pre;
		pre = new int[6];

		for (int k = 0; k < 6; k++) {
			pre[k] = prefix[k];
		}

		HeadNode res1 = create_standard_database(db);
        
		int[] co = countValue(res1);
		for (int i = 1; i < 272; i++) {
			if (co[i] >= line_amount*0.05) {
				for (int k = 0; k < 6; k++) {
					pre[k] = prefix[k];
				}
				int j = 0;
				if (pre[0] > 0) {

					while (pre[j]!=0) {
						bw.write(String.valueOf(pre[j])+" ");
//						bw.flush();
						j++;
					}
					bw.write(i+" ("+co[i]+")");
					bw.newLine();
//					bw.flush();
					pre[j] = i;
				} else
					pre[0] = i;

				HeadNode pdb = create_project_database(db, (short)i);

				dmine6(pdb, pre, n);
			}
		}
           
	}
	public static void main(String[] args) throws IOException{
		Fnsm fnsm = new Fnsm();
//		HeadNode he1 = fnsm.file_to_db("D:/fnsm_db/fnsm_inputdata_24_AutoRacingLeague");
		
		
		 File file = new File("D:/fnsm_db");
		 File[] files = file.listFiles();
		 for(File f:files){
			 System.out.println(f.getName());
			 if(f.length()!=0){
			 int[] pref;
		     pref = new int[10];
			 HeadNode he1 = fnsm.file_to_db("D:/fnsm_db/"+f.getName());
//			 bw = new BufferedWriter(new FileWriter(FNSET_FILE_DIR+f.getName()));
			 String[] filename = f.getName().split("_");
			 bw = new BufferedWriter(new FileWriter(FNSET_FILE_DIR+filename[2]));
			 fnsm.dmine6(he1,pref,0);
		 }
			 bw.close();
		 }
		
		
//		int[] co = fnsm.countValue(he2);
//		int j=0;
//		for(int i : co){
//			
//			System.out.println(j+":"+i);
//			j++;
//		}
//		HeadNode he = fnsm.create_project_database(he2, (short) 69);
//		while(he != null){
//			HeadNode head = he;
//			he = he.getDown();
//			Node node = head.getRight();
//			while(node!=null){
//		System.out.print(node.getValue()+" ");
//		node = node.getRight();
//		}
//			System.out.println();
//		}
//		System.out.println(line_amount);
	}

}
