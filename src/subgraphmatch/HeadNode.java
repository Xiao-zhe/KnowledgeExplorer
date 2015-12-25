package subgraphmatch;

public class HeadNode {
	private Node right;
	private HeadNode down;

	public HeadNode() {
		right = null;
		down = null;
	}

	public void setRight(Node right) {
        this.right = right;
	}

	public void setDown(HeadNode down) {
		this.down = down;
	}

	public Node getRight() {
		return right;
	}

	public HeadNode getDown() {
		return down;
	}

}

