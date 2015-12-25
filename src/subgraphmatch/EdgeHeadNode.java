package subgraphmatch;

public class EdgeHeadNode {
	private EdgeNode right;
	private EdgeHeadNode down;

	public EdgeHeadNode() {
		right = null;
		down = null;
	}

	public void setRight(EdgeNode right) {
        this.right = right;
	}

	public void setDown(EdgeHeadNode down) {
		this.down = down;
	}

	public EdgeNode getRight() {
		return right;
	}

	public EdgeHeadNode getDown() {
		return down;
	}

}

