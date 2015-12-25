package edu.whu.clock.graphsearch.util;

import java.util.Arrays;

public class MyMath {

	public final static int ARR1_CONTAIN_ARR2 = 1;
	public final static int ARR2_CONTAIN_ARR1 = -1;
	public final static int NOT_CONTAINED = 0;
	public final static int ARR1_EQUAL_ARR2 = 2;
	
	public static int compareIntArrays(int[] arr1, int[] arr2) {
		
		if (arr1.length < arr2.length) {
			int low = 0;
			for (int i = 0; i < arr1.length; i++) {
				for (int j = low; j < arr2.length; j++) {
					if (arr1[i] < arr2[j]) {
						return NOT_CONTAINED;
					}
					else if (arr1[i] == arr2[j]) {
						low = j + 1;
						break;
					}
					else {
						if (arr2.length - j <= arr1.length - i) {
							return NOT_CONTAINED;
						}
					}
				}
			}
			return ARR2_CONTAIN_ARR1;
		}
		else if (arr1.length > arr2.length) {
			int low = 0;
			for (int i = 0; i < arr2.length; i++) {
				for (int j = low; j < arr1.length; j++) {
					if (arr2[i] < arr1[j]) {
						return NOT_CONTAINED;
					}
					else if (arr2[i] == arr1[j]) {
						low = j + 1;
						break;
					}
					else {
						if (arr1.length - j <= arr2.length - i) {
							return NOT_CONTAINED;
						}
					}
				}
			}
			return ARR1_CONTAIN_ARR2;
		}
		else {
			if (Arrays.equals(arr1, arr2)) {
				return ARR2_CONTAIN_ARR1;
			}
			else {
				return NOT_CONTAINED;
			}
		}
		
//		boolean match = true;
//		int low = 0;
//		for (int i = 0; i < arr1.length; i++) {
//			if (match) {
//				match = false;
//				
//				int mid = 0;
//				int top = arr2.length - 1;
//				
//				while (low <= top) {
//					mid = (low + top) / 2;
//					if (arr1[i] < arr2[mid]) {
//						top = mid - 1;
//					}
//					else if (arr1[i] > arr2[mid]) {
//						low = mid + 1;
//					}
//					else if (arr1[i] == arr2[mid]) {
//						match = true;
//						break;
//					}
//				}
//			}
//			else {
//				break;
//			}
//			if (match) {
//				nums.add(new Integer(i));
//			}
//		}
	}
	
	public static int compareIntArrays2(int[] arr1, int[] arr2) {
		
		if (arr1.length < arr2.length) {
			int low = 0;
			for (int i = 0; i < arr1.length; i++) {
				for (int j = low; j < arr2.length; j++) {
					if (arr1[i] < arr2[j]) {
						return NOT_CONTAINED;
					}
					else if (arr1[i] == arr2[j]) {
						low = j + 1;
						break;
					}
					else {
						if (arr2.length - j <= arr1.length - i) {
							return NOT_CONTAINED;
						}
					}
				}
			}
			return ARR2_CONTAIN_ARR1;
		}
		else if (arr1.length > arr2.length) {
			int low = 0;
			for (int i = 0; i < arr2.length; i++) {
				for (int j = low; j < arr1.length; j++) {
					if (arr2[i] < arr1[j]) {
						return NOT_CONTAINED;
					}
					else if (arr2[i] == arr1[j]) {
						low = j + 1;
						break;
					}
					else {
						if (arr1.length - j <= arr2.length - i) {
							return NOT_CONTAINED;
						}
					}
				}
			}
			return ARR1_CONTAIN_ARR2;
		}
		else {
			if (Arrays.equals(arr1, arr2)) {
				return ARR1_EQUAL_ARR2;
			}
			else {
				return NOT_CONTAINED;
			}
		}
	}
	
}
