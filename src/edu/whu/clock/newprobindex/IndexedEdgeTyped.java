package edu.whu.clock.newprobindex;


public class IndexedEdgeTyped implements Comparable<IndexedEdgeTyped> {
	private short start;
	private short end;
	private short type;
	private boolean out;
	private double prob;

	public IndexedEdgeTyped(short start, short end, short type, boolean out,
			double prob) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.out = out;
		this.prob = prob;
	}
	
	public IndexedEdgeTyped(short start, short end, short type, boolean out) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.out = out;
		this.prob = 0.0d;
	}

	public short getStart() {
		
		return start;
	}

	public void setStart(short start) {
		this.start = start;
	}

	public short getEnd() {
		return end;
	}

	public void setEnd(short end) {
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

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
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
		IndexedEdgeTyped edge = (IndexedEdgeTyped) o;
		return this.getStart() == edge.getStart()
				&& this.getEnd() == edge.getEnd()
				&& this.getType() == edge.getType()
				&& this.isOut() == edge.isOut();

	}

	@Override
	public int compareTo(IndexedEdgeTyped other) {
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
