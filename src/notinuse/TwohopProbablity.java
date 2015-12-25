package notinuse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

import subgraphmatch.ClassInstanceIndex;
import edu.whu.clock.database.ClassDB;
import edu.whu.clock.graphsearch.GraphInMemory;

public class TwohopProbablity implements Serializable{
	
	private HashMap<String, Double> twohopPro; 
    private Twohop twohop;
    
    public TwohopProbablity(){
    	this.twohopPro = new HashMap<String, Double>();
    	this.twohop = new Twohop();
    }
    
    
	
    public void generatePro() throws IOException, ClassNotFoundException{
    	ClassDB classDB = new ClassDB();
    	classDB.load("D:/dbpedia/");
    	FileInputStream fis = new FileInputStream("D:/SerializedFile/GraphInMemory.ser");
    	ObjectInputStream ois = new ObjectInputStream(fis);
//    	GraphInMemory classGraph = new GraphInMemory(classDB);
    	GraphInMemory classGraph = (GraphInMemory) ois.readObject();
    	ois.close();
//    	System.out.println(classGraph.getClassNum());
//    	System.out.println(classGraph.getRowEdgeHead());
//    	System.out.println(classGraph.gettype2id());
    	FileInputStream fis1 = new FileInputStream("D:/SerializedFile/Twohop.ser");
    	ObjectInputStream ois1 = new ObjectInputStream(fis1);
//    	Twohop twohop = new Twohop();
    	twohop = (Twohop) ois1.readObject();
    	ArrayList<ArrayList<BitSet>> bitset = twohop.getBitSet();
        ois1.close();
        
        
    
    	ClassInstanceIndex Cindex = new ClassInstanceIndex();
		Cindex.init("D:/testing example", "D:/classInstanceIndex", "index");
		Cindex.build();
    	for(int h=0;h<classGraph.getClassNum();h++){
    		String A = String.valueOf(h);
    		int nei[] = classGraph.getNeighbors(h);
    		for(int i=0;i<nei.length;i++){
    			String B = String.valueOf(nei[i]);
    			ArrayList<BitSet> arrbitset = bitset.get(nei[i]);
    			int neinei[] =classGraph.getNeighbors(nei[i]);
    			
    			//为了能根据classid得到neighbor数组的下标
    			HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
    			for(int i1 =0;i1<neinei.length;i1++){
    				map.put(neinei[i1], i1);
    			}
    			int a =(Integer) map.get(h);
    			int m=0,n=0;
    			double pro;
    			for(int j=0;j<neinei.length;j++){
    				if(h==neinei[j]){
    					continue;
    				}
    				m =0;n=0;
    				String C = String.valueOf(neinei[j]);
    				System.out.println(classDB.getClassName(nei[i]));
    				int instance[] = Cindex.getCinstID(classDB.getClassName(nei[i]));
    				for(int k =0;k<instance.length;k++){
    					if(arrbitset.get(k).get(a)==true){
    						n++;	
    					}
    					if(arrbitset.get(k).get(a)&&arrbitset.get(k).get(j)){
    						m++;
    					}
    				}
    				pro = (double)m/n;
    				String key = A+"#"+B+"#"+C;
    				
    				twohopPro.put(key, pro);
    				System.out.println(key+":"+pro);
    			}
    		}
    	}
    }
    
    
    public double getPro(String key){
    	return twohopPro.get(key);
    }
    
    public double getProbablity(int a,int b,int c){
    	if(a==c){
    		System.out.println("参数错误，");
    		return -1;
    	}
    	String key = a+"#"+b+"#"+c;
    	return getPro(key);
    }
    public static void main(String args[]) throws ClassNotFoundException, IOException{
    	TwohopProbablity hopPro = new TwohopProbablity();
    	hopPro.generatePro();
    	System.out.print(hopPro.getProbablity(4, 1, 2));
    	
    }
}
