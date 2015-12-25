package notinuse;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class PKIndexEntryBinding  extends TupleBinding<PKIndexEntry>{
	
	public void objectToEntry(PKIndexEntry pk,TupleOutput out){
		IndexedEdge[] edgeList = pk.getEdgeList();
		double[] proList = pk.getProbList();
		int edgeLen = edgeList.length;
		
		out.writeInt(edgeLen);
		for(int i=0;i<edgeLen;i++){
			out.writeShort(edgeList[i].getStart());
			out.writeShort(edgeList[i].getEnd());
		}
		for(int j=0;j<edgeLen;j++){
			out.writeDouble(proList[j]);
		}
	}
	public PKIndexEntry entryToObject(TupleInput in){
		int len = in.readInt();
		IndexedEdge[] edgeList = new IndexedEdge[len];
		for(int a = 0;a<len;a++){
			edgeList[a] = new IndexedEdge((short)0,(short)0);
		}
		double[] proList = new double[len];
		for(int i=0;i<len;i++){
			edgeList[i].setStart(in.readShort());
			edgeList[i].setEnd(in.readShort());
		}
		for(int j=0;j<len;j++){
			proList[j] = in.readDouble();
		}
		PKIndexEntry pk = new PKIndexEntry(edgeList,proList);
		return pk;
	}

}
