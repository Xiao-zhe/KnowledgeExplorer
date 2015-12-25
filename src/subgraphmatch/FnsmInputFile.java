package subgraphmatch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import notinuse.EntityGraph;
import edu.whu.clock.database.ClassDB;
import edu.whu.clock.newgraph.InstanceManager;

//创建frequent neighbor set mining算法的输入文件
public class FnsmInputFile {
	public void generateFile(String str) throws ClassNotFoundException, IOException{
		long start  = System.currentTimeMillis()/1000;
		FileInputStream fis = new FileInputStream("D:/SerializedFile/InstanceManager.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		InstanceManager instanceManager = (InstanceManager) ois.readObject();
		ois.close();
		
		ClassInstanceIndex claInstance = new ClassInstanceIndex();
//		claInstance.init("D:/testing example", "D:/entityindex", "index");
		claInstance.init("D:/dbpedia/clean", "D:/classInstanceIndex", "index");
		claInstance.build();
		
//		FileInputStream fis1 = new FileInputStream("E:/fu/SerializedFile/EntityGraph.ser");
//		ObjectInputStream ois1 = new ObjectInputStream(fis1);
//		EntityGraph entity= (EntityGraph) ois1.readObject();
//		ois1.close();
		
		
		FileInputStream fis2 = new FileInputStream("D:/SerializedFile/InstanceDB.ser");
		ObjectInputStream ois2 = new ObjectInputStream(fis2);
		InstanceDB instanceDB = (InstanceDB) ois2.readObject();
		ois2.close();
		
//		EntityGraph entity = new EntityGraph(instanceDB);
//		entity.loadUndirectedGraph("D:/dbpedia/clean");
		
		FileInputStream fistr= new FileInputStream("D:/SerializedFile/EntityGraph.ser");
    	ObjectInputStream oistr = new ObjectInputStream(fistr);
    	EntityGraph entity =EntityGraph.readObject(oistr);
    	oistr.close();
    	 
		FileInputStream fis3 = new FileInputStream("D:/SerializedFile/ClassDB.ser");
		ObjectInputStream ois3 = new ObjectInputStream(fis3);
		ClassDB classDB = (ClassDB) ois3.readObject();
		ois3.close();
		
		try {
			String cla = null;
			int instanceID[] =null;
			int[] neighbor = null;
			int[] neighbor2cla;
			BufferedReader br =new BufferedReader(new FileReader(str+"/class.txt"));
				while(br.ready()){
					String fileName =null;
				    cla = br.readLine();
				    fileName = "fnsm_inputdata_"+classDB.getClassID(cla)+"_"+cla;
//				    FileWriter fw = new FileWriter("E:/fu/fnsm_inputdata/"+fileName+"0");
//				    BufferedWriter bw = new BufferedWriter(fw);
//				    BufferedReader br1 = new BufferedReader(new FileReader("E:/fu/fnsm_inputdata/"+fileName+"0"));
				    BufferedWriter bw1 = new BufferedWriter(new FileWriter("D:/fnsm_db/"+fileName));
				    instanceID = claInstance.getCinstID(cla);
				    
				    int min=10000,max=0,temp;
				    double average =0;
				    int num = instanceID.length;
				    for(int i=0;i<instanceID.length;i++){
				    	String s =null;
				    	
				    	neighbor = entity.getNeighbors(instanceID[i]);
				    	if(neighbor==null){
				    		num--;
				    		continue;
				    	}
				    	temp = neighbor.length;
				    	if(temp<min) min = temp;
				    	if(temp>max) max = temp;
				    	average +=temp;
				    	neighbor2cla = new int[neighbor.length];
				    	for(int j=0;j<neighbor.length;j++){
				    		neighbor2cla[j] = instanceManager.getClassID(neighbor[j]);
				    	}
				    	Arrays.sort(neighbor2cla);
				    	String neiString = "";
				    	for(int b=0;b<neighbor2cla.length;b++){
				    		if(b==neighbor.length-1){
				    			neiString += neighbor2cla[b];
				    		}
				    		else
//				    		neiString += neighbor2cla[b]+",";
				    			neiString += neighbor2cla[b]+" ";
				    	}
//				    	s = instanceID[i]+" "+instanceDB.getInstanceName(instanceID[i])+" "+neiString;
//				    	bw.write(s);
//				    	bw.newLine();
				    	bw1.write(neiString+" ");
				    	//bw1.newLine();
				    	if(i+1<instanceID.length)
				        bw1.write('\n');
				    	
				    	
				    }
				    
//				    bw.close();
//				    fw.close();
				    
//				    average= average/num;
//				    String inf = "//"+"min:"+min+"   max:"+max+"    average:"+average;
				    
//				    bw1.write(inf);
//				    bw1.newLine();
//				    while(br1.ready()){
//				    	String ts = br1.readLine();
//				    	bw1.write(ts);
//				    	bw1.newLine();
//				    }
//				     br1.close();
				
				     bw1.close();
				
//				 File file = new File("E:/fu/fnsm_inputdata/"+fileName+"0");
//				 file.delete();
				}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis()/1000;
		System.out.println("generateFile运行时间："+(end-start)+"秒");
	}
	public static void main(String args[]) throws ClassNotFoundException, IOException{
		FnsmInputFile ff = new FnsmInputFile();
//		ff.generateFile("D:/testing example");
		ff.generateFile("D:/dbpedia/clean");
	}

}
