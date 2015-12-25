package notinuse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;

import edu.whu.clock.database.ClassDB;
import edu.whu.clock.graphsearch.GraphInMemory;
import edu.whu.clock.newgraph.ClassManager;
import subgraphmatch.ClassInstanceIndex;
import subgraphmatch.InstanceDB;
//import subgraphmatch.InstanceDB;


public class Twohop implements Serializable{
	private static final long serialVersionUID = -456882277558716259L;
	private ArrayList<ArrayList<BitSet>> claBitset;
	
	public Twohop(){
		claBitset = new ArrayList<ArrayList<BitSet>>(); 
	}
	
	public ArrayList<ArrayList<BitSet>> getBitSet(){
		return this.claBitset;
	}

    public void countNum() throws IOException, ClassNotFoundException {
    	int N;  //某一class的相邻节点的数目
    	ClassManager CM = new ClassManager();
    	FileInputStream fs = new FileInputStream("D:/SerializedFile/ClassManager.ser");
    	ObjectInputStream os = new ObjectInputStream(fs);
		CM = (ClassManager) os.readObject();
		os.close();
		fs.close();
		
		
		ClassDB classDB = new ClassDB();
		GraphInMemory classGraph = new GraphInMemory(classDB);
    	FileInputStream fs2 = new FileInputStream("D:/SerializedFile/GraphInMemory.ser");
    	ObjectInputStream os2 = new ObjectInputStream(fs2);
		classGraph = (GraphInMemory) os2.readObject();
		os2.close();
		fs2.close();
				
        FileInputStream fis= new FileInputStream("D:/SerializedFile/EntityGraph.ser");
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	EntityGraph eg = new EntityGraph();
    	eg =  (EntityGraph) ois.readObject();
    	ois.close();
        fis.close();
        
        InstanceDB instanceDB = new InstanceDB();
        FileInputStream fis1 = new FileInputStream("D:/SerializedFile/InstanceDB.ser");
        ObjectInputStream ois1 = new ObjectInputStream(fis1);
        instanceDB = (InstanceDB) ois1.readObject();
        ois1.close();
        fis1.close();
        
        ClassInstanceIndex cii= new ClassInstanceIndex();
//		cii.init("D:/testing example", "D:/entityindex", "index");
        cii.init("D:/dbpedia/clean", "D:/classInstanceIndex", "index");
		cii.build();
                
        for(short i = 0; i < CM.getClassNum(); i++){
        	String cla = CM.getClassName(i);
        	System.out.println(cla+":");//test
        	String Cinst[] = cii.getCinstName(cla);
        	String Cneighboors[] = classGraph.getNeighborNames(cla);
        	ArrayList<BitSet> array = new ArrayList<BitSet>();
        	N = Cneighboors.length;        
        	
        	for(String ins:Cinst){
        	System.out.print(ins+":");//test
            int a = 0;      	
            BitSet bs = new BitSet(N);
        	      for(String claNei:Cneighboors){	 
        		      String instances[] = cii.getCinstName(claNei);
        		       for(String inst:instances){
        		          if(eg.haveEdge(instanceDB.getInstanceID(ins), instanceDB.getInstanceID(inst))){
        		          bs.set(a);
//        		          a++;
        	              break;
       		                   }
        		          
        	}
        		
        	a++;}
        	      System.out.println(bs.toString());//test
        	array.add(bs);
         }
    	
    	claBitset.add(array);
    }
        cii.close();
  
}

    public static void main(String[] args) throws ClassNotFoundException, IOException{
    	Twohop th = new Twohop();
 		th.countNum();
    	FileOutputStream fos = new FileOutputStream("D:/SerializedFile/Twohop.ser");
  		ObjectOutputStream oos = new ObjectOutputStream(fos);
    	oos.writeObject(th);
//    			ArrayList<BitSet> arr = new ArrayList<BitSet>();
//    			arr = res.get(0);
//    			BitSet bitset = new BitSet();
//    			bitset = arr.get(2);    			
//    			System.out.println(bitset.toString());
    	System.exit(0);
    }

}
