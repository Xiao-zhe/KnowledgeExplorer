package notinuse;

import java.io.Serializable;

public class EntityGraphEdge implements Serializable{
	
	private static final long serialVersionUID = 8835371397064347597L;
	private int end;
	//private Integer type;
	private EntityGraphEdge nextEdge;
	
//	public GraphEdge(String end, GraphEdge nextEdge) {
//		this.end = end;
//		this.types = new HashSet<String>();
//		this.nextEdge = nextEdge;
//	}
	
	public EntityGraphEdge(int end, /*Integer type,*/ EntityGraphEdge nextEdge) {
		this.end = end;
	//	this.type = type;
		this.nextEdge = nextEdge;
		
		
	}
	
	

	public int getEnd() {
		return end;
	}
	
	
//	public Integer getType() {
//		return type;
//  }
	
	
	public EntityGraphEdge getNextEdge() {
		return nextEdge;
	}
	

	public void setNextEdge(EntityGraphEdge nextEdge) {
		this.nextEdge = nextEdge;
	}
	


	
}



