package edu.whu.clock.kgraphsearch.index;

public class IndexedEdge_KG_ET implements Comparable<IndexedEdge_KG_ET> {

	private int start;
	private int end;
	private short type;
	private boolean out;

	public IndexedEdge_KG_ET(int start, int end, short type, boolean out) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.out = out;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public boolean isOut() {
		return out;
	}

	public void setOut(boolean out) {
		this.out = out;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		IndexedEdge_KG_ET edge = (IndexedEdge_KG_ET) o;
		return this.getStart() == edge.getStart()
				&& this.getEnd() == edge.getEnd()
				&& this.getType() == edge.getType()
				&& this.isOut() == edge.isOut();

	}

	@Override
	public int compareTo(IndexedEdge_KG_ET other) {
		if (start > other.getStart()) {
			return 1;
		}
		else if (start < other.getStart()) {
			return -1;
		}
		else {
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
	}
}
