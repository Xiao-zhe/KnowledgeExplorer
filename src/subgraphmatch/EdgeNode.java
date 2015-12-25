package subgraphmatch;

public class EdgeNode {
	private String value;
	private EdgeNode right;
	
	public EdgeNode(String value){
		this.value = value;
		right = null;
	}
	public EdgeNode(){
		right = null;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	public void setRight(EdgeNode right){
		this.right = right;
	}
	
	public String getValue(){
		return value;
	}
	public EdgeNode getRight(){
		return right;
	}

}
