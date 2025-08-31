//TY

package ADT;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;


public class LinkedList<T> implements ListInterface<T>, Serializable {

    private static final long serialVersionUID = 1L;
    private Node firstNode;
    private int numberOfEntries;

    public LinkedList() {
      clear();
    }

    @Override
    public final void clear() {
      firstNode = null;
      numberOfEntries = 0;
    }
  
    @Override
    public boolean add(T newEntry) {
        if(newEntry == null) return false;
        
        Node newNode = new Node(newEntry);

        if (isEmpty()) {
            firstNode = newNode;
        } else {
            Node currentNode = firstNode;
            while (currentNode.next != null) {
                currentNode = currentNode.next;
            }
            currentNode.next = newNode;
        }

        numberOfEntries++;
        return true;
    }


    @Override
    public boolean add(int newPosition, T newEntry) {
        if(newEntry == null) return false;

        boolean isSuccessful = true;

        if ((newPosition >= 1) && (newPosition <= numberOfEntries + 1)) {
            Node newNode = new Node(newEntry);

            if (isEmpty() || (newPosition == 1)) {
              newNode.next = firstNode;
              firstNode = newNode;
            } else {								
              Node nodeBefore = firstNode;
              for (int i = 1; i < newPosition - 1; ++i) {
                nodeBefore = nodeBefore.next;		
              }

              newNode.next = nodeBefore.next;	
              nodeBefore.next = newNode;		
            }

            numberOfEntries++;
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    @Override
    public T remove(int givenPosition) {
        if(isEmpty()) return null;
        
        T result = null;

        if ((givenPosition >= 1) && (givenPosition <= numberOfEntries)) {
            if (givenPosition == 1) {      
              result = firstNode.data;     
              firstNode = firstNode.next;
            } else {                         
              Node nodeBefore = firstNode;
              for (int i = 1; i < givenPosition - 1; ++i) {
                nodeBefore = nodeBefore.next;		
              }
              result = nodeBefore.next.data;  
              nodeBefore.next = nodeBefore.next.next;	
            } 																// one to be deleted (to disconnect node from chain)

            numberOfEntries--;
        }

        return result; 
    }
  

    @Override
    public T remove(T item) {
        
        if (firstNode == null || item == null || isEmpty()) {
            return null;
        }
        
        T removedItem;

        if (firstNode.data.equals(item)) {
            removedItem = firstNode.data;
            firstNode = firstNode.next;
            numberOfEntries--;
            return removedItem;
        }

        Node current = firstNode;
        while (current.next != null) {
            if (current.next.data.equals(item)) {
                removedItem = current.next.data;
                current.next = current.next.next;
                numberOfEntries--;
                return removedItem;
            }
            current = current.next;
        }

        return null;
    }
    
    @Override
    public void sort(Comparator<T> comparator) {
        if (firstNode == null || firstNode.next == null || comparator == null) {
            return;
        }

        boolean swapped;
        do {
            swapped = false;
            Node current = firstNode;
            Node nextNode = firstNode.next;

            while (nextNode != null) {
                if (comparator.compare(current.data, nextNode.data) > 0) {

                    T temp = current.data;
                    current.data = nextNode.data;
                    nextNode.data = temp;
                    swapped = true;
                }
                current = nextNode;
                nextNode = nextNode.next;
            }
        } while (swapped);
    }


    @Override
    public boolean replace(int givenPosition, T newEntry) {
        if(newEntry == null) return false;
        
        boolean isSuccessful = true;

        if ((givenPosition >= 1) && (givenPosition <= numberOfEntries)) {
            Node currentNode = firstNode;
            for (int i = 0; i < givenPosition - 1; ++i) {
              currentNode = currentNode.next;		// advance currentNode to next node
            }
            currentNode.data = newEntry;	// currentNode is pointing to the node at givenPosition
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    @Override
    public T get(int givenPosition) {
        if(isEmpty()) return null;
        
        T result = null;

        if ((givenPosition >= 1) && (givenPosition <= numberOfEntries)) {
            Node currentNode = firstNode;
            for (int i = 0; i < givenPosition - 1; ++i) {
                currentNode = currentNode.next;		// advance currentNode to next node
            }
            result = currentNode.data;	// currentNode is pointing to the node at givenPosition
        }

        return result;
    }

  @Override
    public boolean contains(T anEntry) {
        if(anEntry == null || isEmpty()) return false;

        boolean found = false;
        Node currentNode = firstNode;

        while (!found && (currentNode != null)) {
            if (anEntry.equals(currentNode.data)) {
                found = true;
            } else {
                currentNode = currentNode.next;
            }
        }
        return found;
    }

    @Override
    public int size() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public String toString() {
        String outputStr = "";
        Node currentNode = firstNode;
        while (currentNode != null) {
          outputStr += currentNode.data + "\n";
          currentNode = currentNode.next;
        }
        return outputStr;
    }
    
    // return a new linked list that contain common elements in both lists
    public LinkedList<T> intersection(LinkedList<T> otherList) {
        LinkedList<T> result = new LinkedList<>();

        if (otherList == null || otherList.isEmpty() || this.isEmpty()) {
            return result;
        }

        Node current = this.firstNode;
        while (current != null) {
            if (otherList.contains(current.data) && !result.contains(current.data)) {
                result.add(current.data);
            }
            current = current.next;
        }

        return result;
    }

    // return a new LinkedList containing all unique elements from both list
    public LinkedList<T> union(LinkedList<T> otherList) {
        LinkedList<T> result = new LinkedList<>();

        // Add elements from this list
        Node current = this.firstNode;
        while (current != null) {
            if (!result.contains(current.data)) {
                result.add(current.data);
            }
            current = current.next;
        }

        // Add elements from the other list
        if (otherList != null) {
            current = otherList.firstNode;
            while (current != null) {
                if (!result.contains(current.data)) {
                    result.add(current.data);
                }
                current = current.next;
            }
        }

        return result;
    }

  
    
    //return new linkedlist with same reference to inner objects as old linkedlist
    @Override
    public ListInterface<T> deepCopy() {
        ListInterface<T> copy = new LinkedList<>();

        if (this.isEmpty()) {
            return copy;
        }

        Node currentNode = this.firstNode;
        while (currentNode != null) {
            copy.add(currentNode.data);
            currentNode = currentNode.next;
        }

        return copy;
    }
    
    public void merge(LinkedList<T> other) {
        if (other == null || other.isEmpty()) {
            return;
        }

        if (this.isEmpty()) {
            this.firstNode = other.firstNode;
        } else {
            Node current = this.firstNode;
            while (current.next != null) {
                current = current.next;
            }
            current.next = other.firstNode;
        }

        this.numberOfEntries += other.numberOfEntries;
        other.clear(); // optional: clear the merged list
    }

    public void reverse() {
        Node previous = null;
        Node current = firstNode;
        Node next = null;
        while (current != null) {
            next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }
        firstNode = previous;
    }

    public void removeDuplicates() {
        Node current = firstNode;
        while (current != null) {
            Node runner = current;
            while (runner.next != null) {
                if (runner.next.data.equals(current.data)) {
                    runner.next = runner.next.next;
                } else {
                    runner = runner.next;
                }
            }
            current = current.next;
        }
    }


  
    @Override
    public Iterator<T> getIterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {

        private Node currentNode;

        public LinkedListIterator() {
            currentNode = firstNode;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T data = currentNode.data;
            currentNode = currentNode.next;
            return data;
        }
    }


  private class Node implements Serializable {

    private T data;
    private Node next;

    private Node(T data) {
      this.data = data;
      this.next = null;
    }

    private Node(T data, Node next) {
      this.data = data;
      this.next = next;
    }
  }

}