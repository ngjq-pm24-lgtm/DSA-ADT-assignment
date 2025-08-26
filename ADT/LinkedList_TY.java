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

    public boolean addLast(T newEntry) {
        if (newEntry == null) return false;

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