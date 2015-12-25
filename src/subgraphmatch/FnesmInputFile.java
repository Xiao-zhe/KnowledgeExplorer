package subgraphmatch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.whu.clock.newgraph.EntityGraphEdgeTyped;
import edu.whu.clock.newgraph.GraphManager;

public class FnesmInputFile {
	
	public static String  DIR_fnesm = "D:/experiment data/knowledge graph explorer/dbpedia-old/fnsm_db/";
	
	public void generateFile(){
		GraphManager graphManager = new GraphManager();
		graphManager.genClassManager();
		graphManager.genInstanceManager();
		graphManager.genEntityGraphTyped();
		String item = null;
		for(int i=0;i<272;i++){
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter(DIR_fnesm+String.valueOf(i)));
				for(int ins:graphManager.instanceManager.getInstanceSet((short) i))	{
					TreeMap<String,Integer> count = new TreeMap<String,Integer>(
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
					for(EntityGraphEdgeTyped nei:graphManager.entityGraphTyped.getNeighbors(ins)){
						short classid = graphManager.instanceManager.getClassID(nei.getEnd());
						item = String.valueOf(classid)+"#"+String.valueOf(nei.getType())+"#"+String.valueOf(nei.isOut());
						if (count.containsKey(item)) {
							int val = count.get(item);
							val++;
							count.put(item, val);
						} else
							count.put(item, 1);
					}
					for(String str:count.keySet()){
						for(int n=0;n<count.get(str);n++){
						br.write(str);
						br.write(" ");
						}
					}
					if(graphManager.entityGraphTyped.getNeighbors(ins).length!=0)
					br.newLine();
				}
				
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args){
		FnesmInputFile f = new FnesmInputFile();
		f.generateFile();
	}

}
