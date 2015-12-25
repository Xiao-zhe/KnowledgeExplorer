package mypackage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class TestSerializable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3987082695738940962L;
	private int id;
	String name;
	 Map<Integer,String> map;
	public TestSerializable(int id,String name){
		this.id = id;
		this.name = name;
		map = new HashMap<Integer,String>();
		map.put(3, "eeeeee");
	}
	public TestSerializable() {
		// TODO Auto-generated constructor stub
	}
	public String getName(){
		return name;
	}
	public void setmap(int i,String str){
		map.put(i, str);
	}
public static void main(String args[]){
	TestSerializable ts = new TestSerializable(1,"fuzhejin");
	TestSerializable t1s = new TestSerializable(11,"fuzhe");
	try{
		
		FileOutputStream fo = new FileOutputStream("cla.txt");
	ObjectOutputStream os = new ObjectOutputStream(fo);
	os.writeObject(ts);
	t1s.setmap(5, "eeetrtttyyy");
for(int i=1;i<10000;i++){	os.writeObject(t1s);
}os.close();
	TestSerializable ts1 = new TestSerializable(2,"djjjjjj");
	FileInputStream fi = new FileInputStream("cla.txt");
	ObjectInputStream oi = new ObjectInputStream(fi);
	t1s = (TestSerializable)oi.readObject();
	for(int i=1;i<10000;i++){ts = (TestSerializable)oi.readObject();
	}	System.out.println(t1s.getName());
	System.out.println(ts.getName());
	Map map1 = new HashMap();
	map1 = ts1.map;
	System.out.println(map1.get(3));
	}catch(Exception e){
		e.printStackTrace();
	}
	
	
}
}
