package notinuse;



public class RawProbAnswerTree {
	
	private short root;
	private SearchPath[] paths;
	private double score;
	
	public RawProbAnswerTree(short root, SearchPath[] paths, double score) {
		super();
		this.root = root;
		this.paths = paths;
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public short getRoot() {
		return root;
	}

	public SearchPath[] getPaths() {
		return paths;
	}

}
