package notinuse;

public class IndexedEdge {
	private short start;
	private short end;

	public IndexedEdge(short start, short end) {
		this.start = start;
		this.end = end;
	}

	public void setStart(short start) {
		this.start = start;
	}

	public void setEnd(short end) {
		this.end = end;
	}

	public short getStart() {
		return this.start;
	}

	public short getEnd() {
		return this.end;
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
		IndexedEdge edge = (IndexedEdge) o;
		return this.getStart() == edge.getStart()
				&& this.getEnd() == edge.getEnd();

	}

	@Override
	public int hashCode() {
		return getStart();
	}
}
