package edu.whu.clock.graphsearch.util;

import java.util.ArrayList;


public class FibonacciHeapPQ<E extends Comparable<? super E>> {

	private class FibHeapNode<E> {
		E element;
		int id;
		FibHeapNode<E> left;
	    FibHeapNode<E> right;
	    FibHeapNode<E> parent;
	    FibHeapNode<E> child;
	    int degree;
	    boolean mark;
	    
	    FibHeapNode(E element, int id) {
	    	this.element = element;
	    	this.id = id;
	    	degree = 0;
	    	mark = false;
	    }
	}
	
	private int size;
	private FibHeapNode<E> minNode;
	private ArrayList<FibHeapNode<E>> fhnIndex;
	private int fhnIndexID;
	
	public FibonacciHeapPQ() {
		size = 0;
		minNode = null;
		fhnIndex = new ArrayList<FibHeapNode<E>>();
	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public void decreaseKey(int indexID, E k) throws UnderflowException {
		FibHeapNode<E> x = getNode(indexID);
		
		if (x.element.compareTo(k) < 0) {
            throw new UnderflowException("new key is greater than current key");
        }

		//decrease the priority of x
        x.element = k;

        FibHeapNode<E> y = x.parent;

        //compare x with its parent
        if ((y != null) && (x.element.compareTo(y.element) < 0)) {
        	//cut x from its parent and set it as a root node
            cut(x, y);
            cascadingCut(y);
        }

        //compare the priority of x and minNode and set the lower one as the minNode 
        if (x.element.compareTo(minNode.element) < 0) {
            minNode = x;
        }
	}

	private void cut(FibHeapNode<E> x, FibHeapNode<E> y) {
		//if x is not the only child, set its right brother as y's direct child
        if (x.right == x) {
            y.child = null;
        }
        else {
            y.child = x.right;
        }

        x.left.right = x.right;
        x.right.left = x.left;
        y.degree--;

        //set x as a root node
        minNode.right.left = x;
        x.right = minNode.right;
        minNode.right = x;
        x.left = minNode;
        x.parent = null;
        x.mark = false;
    }

	private void cascadingCut(FibHeapNode<E> y) {
        FibHeapNode<E> z = y.parent;

        if (z != null) {
            if (y.mark == false) {
                y.mark = true;
            }
            else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

	public E dequeueMin() throws UnderflowException {
		FibHeapNode<E> min = deleteMin();

        if (min == null) {
            throw new UnderflowException("Empty FibonacciHeap");
        }
        else {
            return min.element;
        }
	}
	
	private FibHeapNode<E> deleteMin() {
		FibHeapNode<E> t;
        FibHeapNode<E> w;
        FibHeapNode<E> min = minNode;

        if (min != null) {
            if (min.child != null) {
                w = min.child;
                t = w;

                do {
                    t.parent = null;
                    t = t.right;
                } while (t != w);

                minNode.left.right = w.right;
                w.right.left = minNode.left;
                minNode.left = w;
                w.right = minNode;
            }

            min.left.right = min.right;
            min.right.left = min.left;

            if (min == min.right) {
                minNode = null;
            }
            else {
                minNode = min.right;
                consolidate();
            }

            size--;

            int index = min.id;
            fhnIndex.set(index, null);
        }

        return min;
	}
	
	private void consolidate() {
        if (isEmpty()) {
            return;
        }

        int k = (int) Math.floor(Math.sqrt((double) size)) + 2; //number of new root nodes
        FibHeapNode<E>[] a = new FibHeapNode[k];                //array of new root nodes

        for (int i = 0; i < k; i++)
            a[i] = null;

        FibHeapNode<E> stop = minNode;
        FibHeapNode<E> check = stop;
        FibHeapNode<E> x;
        FibHeapNode<E> y;
        FibHeapNode<E> temp;
        int d;

        do {
            x = check;
            d = x.degree;

            //merge x and the root node having the same degree with it
            if (a[d] != x) {
                while (a[d] != null) {
                    y = a[d];

                    //make sure the root node with low priority as parent
                    if (y.element.compareTo(x.element) < 0) {
                        temp = x;
                        x = y;
                        y = temp;
                    }

                    //merge
                    link(y, x);

                    stop = x;
                    check = x;
                    a[d] = null;
                    d++;
                }

                //put the merged result there
                a[d] = x;
            }

            //prepare to check the right root node of x
            check = check.right;
        } while (check != stop);

        minNode = stop;
        check = stop;

        //check the remaining root nodes and find the minimum
        do {
            if (check.element.compareTo(minNode.element) < 0) {
                minNode = check;
            }

            check = check.right;
        } while (check != stop);
    }
	
	private void link(FibHeapNode<E> y, FibHeapNode<E> x) {
		//connect the root nodes on both sides of y
        y.left.right = y.right;
        y.right.left = y.left;

        //set y as a child of x
        if (x.child == null) {
        	//if x has no child, insert y as x's direct child
            y.right = y;
            y.left = y;
            x.child = y;
        }
        else {
        	//if x has at least one child, insert y as the right brother of x's direct child
            y.right = x.child.right;
            y.left = x.child;
            x.child.right.left = y;
            x.child.right = y;
        }

        //set x as the parent of y 
        y.parent = x;
        x.degree++;
        y.mark = false;
    }

	public int enqueue(E e) {
		if (isEmpty()) {
			fhnIndex.clear();
			fhnIndexID = -1;
		}
		
		fhnIndexID++;
		
		FibHeapNode<E> fhn = new FibHeapNode<E>(e, fhnIndexID);
		fhnIndex.add(fhnIndexID, fhn);
		insert(fhn);
		
		return fhnIndexID;
	}
	
	private void insert(FibHeapNode<E> fhn) {
		fhn.left = fhn;
        fhn.right = fhn;
        
        if (!isEmpty()) {
            minNode.right.left = fhn;
            fhn.right = minNode.right;
            minNode.right = fhn;
            fhn.left = minNode;
        }
        
        if (isEmpty() || (fhn.element.compareTo(minNode.element) < 0)) {
            minNode = fhn;
        }
        
        size++;
	}

	public E findMin() throws UnderflowException {
		if (isEmpty()) {
            throw new UnderflowException("Empty FibonacciHeap");
        }
		else {
            return minNode.element;
        }
	}

	public boolean isEmpty() {
		return (minNode == null);
	}

	public FibHeapNode<E> getNode(int index) {
        if (fhnIndex.get(index) != null) {
            return (FibHeapNode<E>) fhnIndex.get(index);
        }

        return null;
    }

}
