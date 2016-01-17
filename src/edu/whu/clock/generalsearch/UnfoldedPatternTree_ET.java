package edu.whu.clock.generalsearch;

import edu.whu.clock.newgraph.SummaryGraphTyped;

public class UnfoldedPatternTree_ET {

	private short root;
	private SearchPath_ET[] paths;
	private double score;
	
	public UnfoldedPatternTree_ET(short root, SearchPath_ET[] paths, double score) {
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

	public SearchPath_ET[] getPaths() {
		return paths;
	}
	
	public SearchPath_ET getPath(int index) {
		return paths[index];
	}
	
	public int numOfPaths() {
		return paths.length;
	}
	
	public String getString(SummaryGraphTyped graph) {
		String str = getScore() + " £º {" + getRoot() + "."
				+ graph.classManager.getClassName(getRoot())
				+ " --- ";
		str += "\r\n";
		for (SearchPath_ET path : paths) {
			str += "[";
			for (int i = 0; i < path.nodeNum() - 1; i++) {
				str += path.getNode(i)
						+ "."
						+ graph.classManager.getClassName(path.getNode(i));
				if (path.isOut(i)) {
					str += " -- " + path.getType(i) + "." + graph.etypeManager.getName(path.getType(i)) + "--> ";
				}
				else {
					str += " <--" + path.getType(i) + "." + graph.etypeManager.getName(path.getType(i))  + "-- ";
				}
			}
			str += path.getNode(path.nodeNum() - 1) + "."
					+ graph.classManager.getClassName(path.getNode(path.nodeNum() - 1)) + "]";
			str += "\r\n";
		}
		return str;
	}

}
