package edu.whu.clock.kgraphsearch;

import java.util.ArrayList;
import java.util.TreeSet;

import edu.whu.clock.newgraph.EntityGraphEdgeTyped;

public class ResultEntityTree_KG_ET {

	private ResultEntityNode_KG_ET root;
	
	public ResultEntityTree_KG_ET(SimpleSearchPath_KG_ET[] paths) {
		int depth = getDepth(paths);
		if (depth < 1) {
			System.out.println("Error: the depth of a result tree is " + depth);
			return;
		}
		ResultEntityNode_KG_ET[][] temp = new ResultEntityNode_KG_ET[depth][paths.length];
		
		for (int i = 1; i <= depth; i++) {
			TreeSet<EntityGraphEdgeTyped> set = new TreeSet<EntityGraphEdgeTyped>();
			for (int j = 0; j < paths.length; j++) {
				if (paths[j].getLength() < i) continue;
				int end = paths[j].getNode(paths[j].getLength() - i);
				short type = paths[j].getType(paths[j].getLength() - i - 1);
				boolean out = paths[j].isOut(paths[j].getLength() - i - 1);
				set.add(new EntityGraphEdgeTyped(end, type, out));
			}
		}
	}
	
	private void iterate(EntityGraphEdgeTyped[] edges) {
		int[] arr = new int[edges.length];
		ArrayList<EntityGraphEdgeTyped> list = new ArrayList<EntityGraphEdgeTyped>(edges.length);
		for (int i = 0; i < edges.length; i++) {
			int index = list.indexOf(edges[i]);
			if (index == -1) {
				list.add(edges[i]);
				arr[i] = list.size() - 1;
			} else {
				arr[i] = index;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			ArrayList<EntityGraphEdgeTyped> newList = new ArrayList<EntityGraphEdgeTyped>();
			for (int j = 0; j < arr.length; j++) {
				if (arr[j] == i) {
					// 创建一个新边，加入到newList
				}
			}
		}
	}
	
	private int getDepth(SimpleSearchPath_KG_ET[] paths) {
		int depth = 0;
		for (SimpleSearchPath_KG_ET path : paths) {
			int length = path.getLength();
			if (length > depth) depth = length;
		}
		return depth;
	}
}
