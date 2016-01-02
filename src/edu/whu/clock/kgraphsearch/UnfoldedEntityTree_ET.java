package edu.whu.clock.kgraphsearch;

import edu.whu.clock.newgraph.EntityGraphTyped;

public class UnfoldedEntityTree_ET {

	private int root;
	private SimpleSearchPath_KG_ET[] paths;
	private double score;
	
	public UnfoldedEntityTree_ET(int root, SimpleSearchPath_KG_ET[] paths, double score) {
		this.root = root;
		this.paths = paths;
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public int getRoot() {
		return root;
	}

	public SimpleSearchPath_KG_ET[] getPaths() {
		return paths;
	}
	
	public SimpleSearchPath_KG_ET getPath(int index) {
		return paths[index];
	}
	
	public int numOfPaths() {
		return paths.length;
	}
	
	public String getString(EntityGraphTyped graph) {
		String str = getScore() + " £º {" + graph.getInstanceManager().getClassID(getRoot()) + "." + getRoot() + "."
				+ graph.getInstanceManager().getInstanceName(getRoot())
				+ " --- ";
		str += "\r\n";
		for (SimpleSearchPath_KG_ET path : paths) {
			str += "[";
			for (int i = 0; i < path.nodeNum() - 1; i++) {
				str +=  graph.getInstanceManager().getClassID(path.getNode(i)) + "." + path.getNode(i)
						+ "."
						+ graph.getInstanceManager().getInstanceName(path.getNode(i));
				if (path.isOut(i)) {
					str += " -- " + path.getType(i) + "." + graph.getEdgeTypeManager().getName(path.getType(i)) + "--> ";
				}
				else {
					str += " <--" + path.getType(i) + "." + graph.getEdgeTypeManager().getName(path.getType(i))  + "-- ";
				}
			}
			str += graph.getInstanceManager().getClassID(path.getNode(path.nodeNum() - 1)) + "." + path.getNode(path.nodeNum() - 1) + "."
					+ graph.getInstanceManager().getInstanceName(path.getNode(path.nodeNum() - 1)) + "]";
			str += "\r\n";
		}
		return str;
	}

}
