


package notinuse;

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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.whu.clock.newgraph.InstanceManager;
import subgraphmatch.InstanceDB;


public class EntityGraph implements Serializable{
	
	private static final long serialVersionUID = 5667400953923286368L;
	private HashMap<Integer, EntityGraphEdge> rowEdgeHead;  
	//private HashMap<String, Integer> type2id;  //鏉堥�涚瑐type娑撳孩鏆熺�涙顕惔锟�
	//private HashMap<Integer, String> id2type;
	private InstanceManager instanceManager;
	private int edgeNum;
	
	public void setRowEdgeHead(HashMap row){
		this.rowEdgeHead = row;
	}
	public void setInstanceDB(InstanceManager ins){
		this.instanceManager = ins;
	}
	public void setEdgeNum(int num){
		this.edgeNum = num;
	}
	public EntityGraph(){
		
	}
	public EntityGraph(InstanceManager instanceDB) {
		this.instanceManager = instanceDB;
		this.rowEdgeHead = new HashMap<Integer, EntityGraphEdge>();
	//	this.type2id = new HashMap<String, Integer>();
	//	this.id2type = new HashMap<Integer, String>();
	}
	
	public int getEdgeNum() {
		return edgeNum;
	}
	
	public int getVertexNum() {
		return rowEdgeHead.size();   //???????????????
	}
	public void load(String dir) {
		try {
			/*BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/clean/relationship_type.txt"));
			String line = null;
			int id = 0;
			while (br.ready()) {
				line = br.readLine();
				id++;
				type2id.put(line, id);
				id2type.put(id, line);
			}
			br.close();*/
			
			BufferedReader br1 = new BufferedReader(new FileReader(dir
					+ "/instance_relationship_mapping.txt"));
			String[] ele = new String[3];
			String line = null;
			while (br1.ready()) {
				line = br1.readLine();
				ele = line.split(" ");
				addEdge(instanceManager.getInstanceID(ele[0]), instanceManager.getInstanceID(ele[2]));
			
			
		}
			br1.close();
			}catch (Exception ex) { 
			ex.printStackTrace();
		}
		System.out.println("Num of vertices: " + getVertexNum());
		System.out.println("Num of edges:    " + getEdgeNum());
	

}
	public void loadUndirectedGraph(String dir) {
		try {
			/*BufferedReader br = new BufferedReader(new FileReader(dir
					+ "/clean/relationship_type.txt"));
			String line = null;
			int id = 0;
			while (br.ready()) {
				line = br.readLine();
				id++;
				type2id.put(line, id);
				id2type.put(id, line);
			}
			br.close();*/
			
			BufferedReader br1 = new BufferedReader(new FileReader(dir
					+ "/instance_relationship_mapping.txt"));
			String[] ele = new String[3];
			String line = null;
			while (br1.ready()) {
				line = br1.readLine();
				ele = line.split(" ");
				addEdge(instanceManager.getInstanceID(ele[0]), instanceManager.getInstanceID(ele[2]));
				addEdge(instanceManager.getInstanceID(ele[2]), instanceManager.getInstanceID(ele[0]));
			
			
		}
			br1.close();
			}catch (Exception ex) { 
			ex.printStackTrace();
		}
		System.out.println("Num of vertices: " + getVertexNum());
		System.out.println("Num of edges:    " + getEdgeNum());
	

}
	
	public void addEdge(int start, int end) {   //閺嶈宓乻tart閹垫儳鍩岀挧宄邦潗妞ゅ墎鍋ｉ敍灞芥倵閺嶈宓乪nd閻ㄥ嫬銇囩亸蹇旀箒鎼村繑褰冮崗銉╂懠鐞涳拷
		EntityGraphEdge head = rowEdgeHead.get(start);
	//	int t = type2id.get(type);
		if (head != null) {
			EntityGraphEdge ahead = head;
			EntityGraphEdge current = ahead.getNextEdge();

			while (current != null && end > current.getEnd()) {
				ahead = current;
				current = current.getNextEdge();
			}

			if (current == null) {          //闁秴宸婚懛鍐茬啲闁劍褰冮崗锟�
				ahead.setNextEdge(new EntityGraphEdge(end, null));
				
			}
			else {
			    if (end == current.getEnd()) {  
					//current.addType(t);
					edgeNum--;
				}
				else {
					ahead.setNextEdge(new EntityGraphEdge(end,current));
				}
			}
		}
		else {
//			head = new EntityGraphEdge(end, null);
			head = new EntityGraphEdge(0, new EntityGraphEdge(end, null));
			rowEdgeHead.put(start, head);
		}
		edgeNum++;
	}
	
	public int[] getNeighbors(int source) {  //get sourc 健康数据库等级
		EntityGraphEdge head = rowEdgeHead.get(source);
		if (head == null) {
			return null;
		}
		EntityGraphEdge re = head.getNextEdge();

		ArrayList<Integer> temp = new ArrayList<Integer>();

		while (re != null) {
			temp.add(re.getEnd());
			re = re.getNextEdge();
		}
		
		   
		
		int[] result = new int[temp.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = temp.get(i);
		}
		
	//	System.out.println(temp.size());

		return result;
	}

	
	//haveEdge checks that edge(ins1,ins2) in entityGraph or not
	public boolean haveEdge(int ins1,int ins2){
		EntityGraphEdge head1 = rowEdgeHead.get(ins1);
		EntityGraphEdge re1 = head1.getNextEdge();
		while (re1 != null) {
			if(ins2==re1.getEnd()){
				return true;
			}
			
			else{
			re1 = re1.getNextEdge();
			}
			
		}
		
		EntityGraphEdge head2 = rowEdgeHead.get(ins1);
		EntityGraphEdge re2 = head2.getNextEdge();
		while (re2 != null) {
			if(ins1==re2.getEnd())
				return true;
			else
			re2 = re2.getNextEdge();
		}
	return false;
		
	}
	
	
	public int[] getNeighbors(String source) {  
		return getNeighbors(instanceManager.getInstanceID(source));
		
	}
	
	public String[] getNeighborNames(String source) {
		int[] temp = getNeighbors(source);
		if(temp==null)
			return null;
		String[] result = new String[temp.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = instanceManager.getInstanceName(temp[i]);
		}
		
		return result;
	}
	
	
//   private void writeObject(ObjectOutputStream s)
//            throws IOException {
//        s.writeObject(instanceDB);
//        s.writeInt(edgeNum);
//        Iterator<Entry<Integer, EntityGraphEdge>> it = rowEdgeHead.entrySet().iterator();
//        int size = rowEdgeHead.entrySet().size();
//        	size = 2*size;
//        	s.writeInt(size);
//        while(it.hasNext()){
//        	Map.Entry<Integer, EntityGraphEdge> entry = (Entry<Integer, EntityGraphEdge>) it.next();
//        	Integer i = entry.getKey();
//        	EntityGraphEdge edge = entry.getValue();
//        	s.writeObject(i);
//        	s.writeObject(edge);
//        }
//	}
//	
//   private EntityGraph readObject(ObjectInputStream in)
//        throws IOException, ClassNotFoundException{
//        	
//        	InstanceDB ins = (InstanceDB) in.readObject();
//        	int en = in.readInt();
//        	HashMap<Integer, EntityGraphEdge> map1= new HashMap<Integer, EntityGraphEdge>();
//        	int size = in.readInt();
//        	for(int i=size/2;i>0;i--){
//        		Integer key = (Integer) in.readObject();
//            	EntityGraphEdge edge = (EntityGraphEdge) in.readObject();
//            	map1.put(key, edge);
//        	}
//        	EntityGraph eg = new EntityGraph();
//        	eg.setEdgeNum(en);
//        	eg.setInstanceDB(ins);
//        	eg.setRowEdgeHead(map1);
//        	return eg;
//        }
	
	private void writeObject(ObjectOutputStream s)
            throws IOException {
        s.writeObject(instanceManager);//////111111111
        s.writeInt(edgeNum);///////////2222222222
        Iterator<Entry<Integer, EntityGraphEdge>> it = rowEdgeHead.entrySet().iterator();
        int size = rowEdgeHead.entrySet().size();
        s.writeInt(size);//////////////333333333333
        while(it.hasNext()){
        	Map.Entry<Integer, EntityGraphEdge> entry = (Entry<Integer, EntityGraphEdge>) it.next();
        	Integer i = entry.getKey();
        	s.writeObject(i);//////////
        	EntityGraphEdge edge = entry.getValue();
        	int num = 0;
        	EntityGraphEdge temp =edge; 
        	while(temp!=null){
        		temp= temp.getNextEdge();
        		num++;
        	}
        	s.writeInt(num);///////////
        	s.writeInt(edge.getEnd());
        	while(edge.getNextEdge()!=null){
        		edge = edge.getNextEdge();
        		s.writeInt(edge.getEnd());//////
        	}
        }
	}
	
   public static EntityGraph readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException{
        	
	   InstanceManager ins = (InstanceManager) in.readObject();//111111
        	int en = in.readInt();///22222222       	
        	int size = in.readInt();//3333333333
        	HashMap<Integer, EntityGraphEdge> map1= new HashMap<Integer, EntityGraphEdge>();
        	for(int i=size;i>0;i--){
        		Integer key = (Integer) in.readObject();
        		int num = in.readInt();
        		EntityGraphEdge headedge = new EntityGraphEdge(in.readInt(),null);
        		EntityGraphEdge edge = headedge; 
        		for(int k=num-1;k>0;k--){
        			EntityGraphEdge ed = new EntityGraphEdge(in.readInt(),null);
        			edge.setNextEdge(ed);
        			edge = edge.getNextEdge();
        		}
            	
            	map1.put(key, headedge);
        	}
        	EntityGraph eg = new EntityGraph();
        	eg.setEdgeNum(en);
        	eg.setInstanceDB(ins);
        	eg.setRowEdgeHead(map1);
        	return eg;
        }
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		InstanceManager instanceDB = new InstanceManager();
//		instanceDB.load("D:/dbpedia/clean");/////////////////////////////////////////////////////////////////////////////////
//		instanceDB.load("D:/testing example");
		EntityGraph graph = new EntityGraph(instanceDB);
		graph.loadUndirectedGraph("D:/dbpedia/clean");
//		graph.loadUndirectedGraph("D:/testing example");
		FileOutputStream fos = new FileOutputStream("D:/SerializedFile/EntityGraph.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
//		oos.writeObject(graph);
		graph.writeObject(oos);
		oos.close();
//		String[] set = graph.getNeighborNames("Jacques_Dixmier");
//		String[] set = graph.getNeighborNames("A1");
//		for (String str : set) {
//			System.out.println(str);
//		}
//		System.exit(0);
		FileInputStream fis= new FileInputStream("D:/SerializedFile/EntityGraph.ser");
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	EntityGraph eg = (EntityGraph) EntityGraph.readObject(ois);
           
 
    	String[] Str =eg.getNeighborNames("Autism");
    	if(Str==null)
    		System.out.println("no neighboors");
    	else{
    	for(String i: Str){
    	System.out.println(i);
    	}}
    	System.out.println(eg.haveEdge(instanceDB.getInstanceID("Animal_Farm"), instanceDB.getInstanceID("George_Orwell")));
    	
	}
}

