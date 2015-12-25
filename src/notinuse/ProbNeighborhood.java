package notinuse;

public class ProbNeighborhood {
	private int[] vertices;
	private double[] probabilities;

	public ProbNeighborhood(int[] vertices, double[] probabilities) {
		this.vertices = vertices;
		this.probabilities = probabilities;
	}

	public ProbNeighborhood() {
		// TODO Auto-generated constructor stub
	}
	
	public int size() {
		return vertices.length;
	}

	public void setVertices(int[] vertices) {
		this.vertices = vertices;
	}

	public void setProbabilities(double[] probabilities) {
		this.probabilities = probabilities;
	}

	public int getVertex(int i) {
		return vertices[i];
	}

	public double getProbability(int i) {
		return probabilities[i];
	}

}
