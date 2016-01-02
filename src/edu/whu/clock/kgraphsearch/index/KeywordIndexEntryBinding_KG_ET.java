package edu.whu.clock.kgraphsearch.index;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class KeywordIndexEntryBinding_KG_ET extends TupleBinding<KeywordIndexEntry_KG_ET> {

	public void objectToEntry(KeywordIndexEntry_KG_ET pk, TupleOutput out) {
		int length = pk.length();
		out.writeInt(length);
		for (int i = 0; i < length; i++) {
			out.writeInt(pk.getEdge(i).getStart());
			out.writeInt(pk.getEdge(i).getEnd());
			out.writeShort(pk.getEdge(i).getType());
			out.writeBoolean(pk.getEdge(i).isOut());
		}
	}

	public KeywordIndexEntry_KG_ET entryToObject(TupleInput in) {
		int length = in.readInt();
		IndexedEdge_KG_ET[] edgeList = new IndexedEdge_KG_ET[length];
		int start;
		int end;
		short type;
		boolean out;
		for (int i = 0; i < length; i++) {
			start = in.readInt();
			end = in.readInt();
			type = in.readShort();
			out = in.readBoolean();
			edgeList[i] = new IndexedEdge_KG_ET(start, end, type, out);
		}
		KeywordIndexEntry_KG_ET pk = new KeywordIndexEntry_KG_ET(edgeList);
		return pk;
	}


}
