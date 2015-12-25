package edu.whu.clock.graphsearch.util;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class SequentialIntArrayBinding extends TupleBinding<SequentialIntArray>{

	@Override
	public SequentialIntArray entryToObject(TupleInput in) {
		// TODO Auto-generated method stub
		int len = in.readInt();
		int[] vids = new int[len];
		for(int i=0;i<len;i++){
			vids[i]=in.readInt();
		}
		
		return new SequentialIntArray(vids);
	}

	@Override
	public void objectToEntry(SequentialIntArray ei, TupleOutput out) {
		// TODO Auto-generated method stub
		
		int[] vids = ei.getArray();
		out.writeInt(vids.length);
		for(int i=0;i<vids.length;i++){
			out.writeInt(vids[i]);
		}
	}

}