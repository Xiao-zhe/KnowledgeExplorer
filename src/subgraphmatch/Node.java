package subgraphmatch;

public class Node {
	private short value;
	private Node right;
	
	public Node(short value){
		this.value = value;
		right = null;
	}
	public Node(){
		right = null;
	}
	
	public void setValue(short value){
		this.value = value;
	}
	public void setRight(Node right){
		this.right = right;
	}
	
	public short getValue(){
		return value;
	}
	public Node getRight(){
		return right;
	}

}
