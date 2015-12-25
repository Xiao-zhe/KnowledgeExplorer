package edu.whu.clock.graphsearch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import edu.whu.clock.database.ClassDB;


public class AnswerTree implements Serializable {

	private static final long serialVersionUID = 8635550755647505024L;
	private int root;
	private String sroot;
	private int[] matched_vertexes;
	private int[][] paths;
	private String[][] spaths;
	private int score;
	private HashSet<Integer> allVertices;
	private HashSet<String> allEdges;

	public AnswerTree(int root, int[] matched_vertexes, int[][] paths, int score) {
		this.root = root;
		this.matched_vertexes = matched_vertexes;
		this.paths = paths;
		this.score = score;
		this.allVertices = new HashSet<Integer>();
		this.allEdges = new HashSet<String>();
		genAll();
	}
	
	private void genAll() {
		allVertices.add(root);
		for (int i = 0; i < paths.length; i++) {
			if (paths[i] != null && paths[i].length > 0) {
				allEdges.add(genEdge(root, paths[i][0]));
				for (int j = 0; j < paths[i].length - 1; j++) {
					allVertices.add(paths[i][j]);
					allEdges.add(genEdge(paths[i][j], paths[i][j+1]));
				}
				allVertices.add(paths[i][paths[i].length - 1]);
			}
		}
	}
	
	private String genEdge(int x, int y) {
		if (x < y) {
			return x + "-" + y;
		}
		else {
			return y + "-" + x;
		}
	}

	public int compare(AnswerTree at) {
		if(!(allEdges.isEmpty()) && !(at.getAllEdges().isEmpty())){
			boolean ARR1_CONTAIN_ARR2 = allEdges.containsAll(at.getAllEdges());
			boolean ARR2_CONTAIN_ARR1 = at.getAllEdges().containsAll(allEdges);
			if(ARR1_CONTAIN_ARR2 && ARR2_CONTAIN_ARR1)
				return 2;
			else if(ARR1_CONTAIN_ARR2)
				return 1;
			else if(ARR2_CONTAIN_ARR1)
				return -1;
			return 0;
		}
		else if(allEdges.isEmpty() && !(at.getAllEdges().isEmpty())){
			if (at.getAllVertices().contains(root)) {
				return -1;
			}
			else {
				return 0;
			}
		}
		else if(!(allEdges.isEmpty()) && at.getAllEdges().isEmpty()){
			if (allVertices.contains(at.getRoot())) {
				return 1;
			}
			else {
				return 0;
			}
		}
		return 0;
	}

	public double getScore() {
		return score;
	}

	public int getRoot() {
		return root;
	}
	
	public HashSet<Integer> getAllVertices() {
		return allVertices;
	}

	public HashSet<String> getAllEdges() {
		return allEdges;
	}

	public int getLeafNumber() {
		return paths.length;
	}

	public int[] getPath(int index) {
		return paths[index];
	}

	public int[] getLeaves() {
		return matched_vertexes;
	}
	
	public void transform(ClassDB classDB) {
		sroot = classDB.getClassName(root);
		spaths = new String[paths.length][];
		for (int i = 0; i < spaths.length; i++) {
			if (paths[i] != null) {
				spaths[i] = new String[paths[i].length];
				for (int j = 0; j < spaths[i].length; j++) {
					spaths[i][j] = classDB.getClassName(paths[i][j]);
				}
			}
		}
	}
	
	public String toString() {
		String str = "score:" + score + "  root(" + sroot + ") -- paths{";
		for (String[] path : spaths) {
			if (path == null) {
				str += "(), ";
			}
			else {
				str += "(";
				for (String i : path) {
					str += i + ", ";
				}
				str += "), ";
			}
		}
		return str;
	}
	
	public int[][] getTreeArray(){
		int maxL=0;
		for (int j = 0; j < getLeafNumber(); j++) {
			int[] path = getPath(j);
			if (path != null) {
				if(maxL<path.length){
					maxL = path.length;
				}
			}
		}
		
		int[][] v = new int[paths.length][maxL+1];//include root
		for(int i=0;i<paths.length;i++){
			v[i][0] = root;
		}
		
		if(maxL != 0){
			for (int i = 0; i < getLeafNumber(); i++) {
				int[] path = getPath(i);
				if (path != null) {
					for(int j=0;j<path.length;j++){
							v[i][j+1] = path[j];
					}
				}
			}
		}
		
		return v;
	}
	
	public int[][] change(int[][] v){
		int maxL =0;
//		int[][] v ={
//				{1,3,5,9},
//				{1,4,8,0},
//				{1,3,6,10},
//				{1,2,0,0},
//				{1,3,7,0},
//				{1,3,6,11}
//		};
		
//		int[][] v={
//				{1,2,3,0},
//				{1,4,5,7},
//				{1,4,8,0},				
//				{1,4,5,6},
//				{1,4,9,0}
//		};
	
//		System.out.println(v.length);
		
		for(int i=0;i<v.length;i++){
			if(maxL <v[i].length){
				maxL = v[i].length;
			}
		}
		
		int[][] vv = new int[v.length][maxL];
		int vv_rows=0;

		int vv_k=0;
		
		for(int i=0;i<maxL;i++){//v_column
			if(vv_k>vv_rows){
				vv_rows = vv_k;
			}
			vv_k=0;
			
			for(int j =0;j<v.length;j++){//v_row
				int v_new = v[j][i];
				int v_new_prev;
				
				if(v_new == 0) continue;
								
				int m=0;
				for(;m<vv_k;m++){
					if(vv[m][maxL-i-1] == v_new){
						break;
					}
				}
				if(m>=vv_k){
					if(i == 0){
						vv[vv_k][maxL-i-1]=v_new;						
					}else{//v_column is the next column
						v_new_prev = v[j][i-1];
						for(int h=0;h<v.length;h++){							
							if(vv[h][maxL-i] == v_new_prev){
								if(vv[h][maxL-i-1] == 0){
									vv[h][maxL-i-1]=v_new;
								}else{//需要挪动
									for(int d=vv_rows-1;d>h;d--){
										for(int s=maxL-i-1;s<=maxL-1;s++){
											vv[d+1][s]= vv[d][s];
											vv[d][s] = 0;
										}
									}
									vv[h+1][maxL-i-1]=v_new;
									vv_rows+=1;
								}
								break;
							}//end if						
						}//end for
					}
					vv_k++;
				}else{
					continue;
				}
			}
		}//end the first for loop
		
		int[][] res = new int[2*(v.length)-1][2*maxL-1];
		
		int xx=-2;//以后该处画横线
		int xy=-3;//以后该处画"┚"
		int yy=-4;//以后该处画竖线
		
		//copy the data to new array
		for(int i=0;i<vv.length;i++){//row
			boolean flag = false;
			for(int j=0;j<maxL;j++){//column
				int newV = vv[i][j];
				if(newV == 0){
					if(j == 0){//第一列为0，其余为0则将该处为
						flag = true;
					}
				}else{//该数不为0
					if(flag){//将该数填到尾部
						res[2*i][0] = newV;
						//从1到2*j都置为横线
						for(int k=1;k<=2*j;k++){
							res[2*i][k] = xx;
						}
						flag=false;
					}else{
						res[2*i][2*j] = newV;
						//将左边的置为横线
						if((2*j-1)>=0){
							res[2*i][2*j-1] = xx;
						}
					}
					
					//判断后继数是否为0
					if((j+1)< maxL){//确保不是最后一个数
						if(vv[i][j+1] == 0){//本数为newV是该行的头节点
							res[2*i][2*j+1] = xy;//画折线"┚"
							//将该列从上一个非0行开始画竖线
							int m=2*i-1;
							while(m>=0 && (res[m][2*j+1]==0)){
								res[m][2*j+1] = yy;
								m--;
							}
							break;
						}
					}
				}
			}
		}
		
		return res;
	
//		print
//		for(int x=0;x<v.length;x++){
//			for(int y=0;y<maxL;y++){
//				System.out.print(vv[x][y]+" ");
//			}
//			System.out.println("");
//		}	
		
	}

	public HashMap<Integer,Integer> getNextForEdge(int[][] v){
		HashMap<Integer,Integer> edge = new HashMap<Integer,Integer>();
		for(int i=0;i<v.length;i++){
			for(int j=0;j<v[i].length;j++){
				if((j+1)<v[i].length && v[i][j+1]!=0){
					edge.put(v[i][j+1],v[i][j]);					
				}
			}
		}	
		return edge;
	}
}
