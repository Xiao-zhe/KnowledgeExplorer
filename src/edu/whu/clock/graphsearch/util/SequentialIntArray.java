package edu.whu.clock.graphsearch.util;


public class SequentialIntArray {

	private int[] data;
	private int size = 0;
	
	private int default_size = 8;
	
	public SequentialIntArray() {
		this.data = new int[default_size];
	}
	
	public SequentialIntArray(int default_size) {
		this.default_size = default_size;
		this.data = new int[default_size];
	}
	
	public SequentialIntArray(int[] data) {
		this.data = data;
		this.size = data.length;
	}
	
	public SequentialIntArray(int size, int[] data) {
		this.data = data;
		this.size = size;
	}
	
	public int binarySearch(int target) {
		int low = 0;
		int mid = 0;
		int top = size - 1;
		
		while (low <= top) {
			mid = (low + top) / 2;
			if (target < data[mid]) {
				top = mid - 1;
			}
			else if (target > data[mid]) {
				low = mid + 1;
			}
			else if (target == data[mid]) {
				return mid;
			}
		}
		
		return -1;
	}
	
	public void insert(int num) {
		if (size == data.length) {
			int[] temp = data;
			data = new int[2 * size];
			for (int i = 0; i < temp.length; i++) {
				data[i] = temp[i];
			}
		}
		
		int low = 0;
		int mid = 0;
		int top = size - 1;
		
		while (low <= top) {
			mid = (low + top) / 2;
			if (num < data[mid]) {
				top = mid - 1;
			}
			else if (num > data[mid]) {
				low = mid + 1;
			}
			else if (num == data[mid]) {
				return;
			}
		}
		
		for (int i = size; i > low; i--) {
			data[i] = data[i - 1];
		}
		data[low] = num;
		
		size++;
	}
	
	public int getCapacity() {
		return size;
	}
	
	public int get(int index) {
		return data[index];
	}
	
	public int getArrayLength() {
		return data.length;
	}
	
	public int[] getArray() {
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = data[i];
		}
		return result;
	}

}