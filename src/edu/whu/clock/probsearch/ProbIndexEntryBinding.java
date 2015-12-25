package edu.whu.clock.probsearch;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class ProbIndexEntryBinding extends TupleBinding<ProbIndexEntry> {
	@Override
	public ProbIndexEntry entryToObject(TupleInput in) {
		int len = in.readInt();
		int[] c = new int[len];
		double[] p = new double[len];
		for (int i = 0; i < len; i++) {
			c[i] = in.readInt();
		}
		for (int i = 0; i < len; i++) {
			p[i] = in.readDouble();
		}
		return new ProbIndexEntry(c, p);
	}

	@Override
	public void objectToEntry(ProbIndexEntry ie, TupleOutput out) {
		int[] c = ie.getClassIDList();
		double[] p = ie.getProbList();
		out.writeInt(c.length);
		for (int i : c) {
			out.writeInt(i);
		}
		for (double i : p) {
			out.writeDouble(i);
		}
	}

}