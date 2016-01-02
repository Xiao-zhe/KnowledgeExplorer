package edu.whu.clock.newprobindex;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class PKIndexTypedEntryBinding extends TupleBinding<PKIndexTypedEntry> {

	public void objectToEntry(PKIndexTypedEntry pk, TupleOutput out) {
		int length = pk.length();
		out.writeInt(length);
		for (int i = 0; i < length; i++) {
			
			out.writeShort(pk.getEdge(i).getStart());
			out.writeShort(pk.getEdge(i).getEnd());
			out.writeShort(pk.getEdge(i).getType());
			out.writeBoolean(pk.getEdge(i).isOut());
			out.writeDouble(pk.getEdge(i).getProb());
		}
	}

	public PKIndexTypedEntry entryToObject(TupleInput in) {
		int length = in.readInt();
		IndexedEdgeTyped[] edgeList = new IndexedEdgeTyped[length];
		short start;
		short end;
		short type;
		boolean out;
		double prob;
		for (int i = 0; i < length; i++) {
			start = in.readShort();
			end = in.readShort();
			type = in.readShort();
			out = in.readBoolean();
			prob = in.readDouble();
			edgeList[i] = new IndexedEdgeTyped(start, end, type, out, prob);
		}
		PKIndexTypedEntry pk = new PKIndexTypedEntry(edgeList);
		return pk;
	}

}
