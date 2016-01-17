package edu.whu.clock.newgraph;

import java.io.Serializable;

public class EntityGraphEdgeTyped implements Serializable,
		Comparable<EntityGraphEdgeTyped> {

	private static final long serialVersionUID = -1554918086776064726L;
	private int end;
	private short type;
	private boolean out;

	public EntityGraphEdgeTyped(int end, short type, boolean out) {
		this.end = end;
		this.type = type;
		this.out = out;
	}

	public int getEnd() {
		return end;
	}

	public short getType() {
		return type;
	}

	public boolean isOut() {
		return out;
	}

	public int compareTo(EntityGraphEdgeTyped other) {
		if (end > other.getEnd()) {
			return 1;
		}
		else if (end < other.getEnd()) {
			return -1;
		}
		else {
			if (type > other.getType()) {
				return 1;
			}
			else if (type < other.getType()) {
				return -1;
			}
			else {
				if (out && !other.isOut()) {
					return 1;
				}
				else if (!out && other.isOut()) {
					return -1;
				}
				else {
					return 0;
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityGraphEdgeTyped) {
			EntityGraphEdgeTyped edge = (EntityGraphEdgeTyped) obj;
			if (this.end == edge.getEnd() && this.type == edge.getType()
					&& this.isOut() == edge.isOut()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + type;
		result = prime * result + (out ? 1 : 0);
		return result;
	}
	
}
