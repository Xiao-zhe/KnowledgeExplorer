package edu.whu.clock.graphsearch.util;


public class ExpandableIntArray {

	private int[] arr;
	private int size;
	
	public final static int INIT_CAPACITY = 128;

	public ExpandableIntArray() {
		this(ExpandableIntArray.INIT_CAPACITY);
	}
	
	public ExpandableIntArray(int capacity) {
		this.arr = new int[capacity];
		this.size = 0;
	}
	
	public void add(int value) {
		if (size == arr.length) {
			int[] temp = arr;
			arr = new int[2 * size];
			for (int i = 0; i < size; i++) {
				arr[i] = temp[i];
			}
		}
		
		arr[size] = value;
		size++;
	}
	
	public int get(int index) {
		return arr[index];
	}
	
	public int getSize() {
		return size;
	}
	
	public int[] toArray() {
		if (size == 0) {
			return null;
		}
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	
	public void set(int index,int data)
	{
		if (index == arr.length) {
			int[] temp = arr;
			arr = new int[2 * index];
			for (int i = 0; i < size; i++) {
				arr[i] = temp[i];
			}
		}
		arr[index] = data ;
	}


}
