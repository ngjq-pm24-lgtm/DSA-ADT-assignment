package ADT;

import java.util.Comparator;
import java.util.Iterator;

public class MyList<T> implements ListInterface<T> {
    private static final int INITIAL_CAPACITY = 20;
    private T[] list;
    private int size;

    public MyList() {
        list = (T[]) new Object[INITIAL_CAPACITY];
        size = 0;
    }

    @Override
    public boolean add(T element) {
        if(element == null) return false;
        
        ensureCapacity();
        list[size++] = element;
        return true;
    }

    @Override
    public boolean add(int index, T element) {
        if (index < 0 || index > size) return false;
        ensureCapacity();
        for (int i = size; i > index; i--) {
            list[i] = list[i - 1];
        }
        list[index] = element;
        size++;
        return true;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) return null;
        return list[index];
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) return null;
        T removed = list[index];
        for (int i = index; i < size - 1; i++) {
            list[i] = list[i + 1];
        }
        list[--size] = null;
        return removed;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    private void ensureCapacity() {
        if (size >= list.length) {
            T[] newList = (T[]) new Object[list.length * 2];
            System.arraycopy(list, 0, newList, 0, size);
            list = newList;
        }
    }

    @Override
    public void sort(Comparator<T> comparator) {
        if (size <= 1 || comparator == null) {
            return;
        }

        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < size - 1; i++) {
                if (comparator.compare(list[i], list[i + 1]) > 0) {
                    // Swap elements
                    T temp = list[i];
                    list[i] = list[i + 1];
                    list[i + 1] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean replace(int givenPosition, T newEntry) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean contains(T anEntry) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public T remove(T item) {
        if (item == null || isEmpty()) {
            return null;
        }

        for (int i = 0; i < size; i++) {
            if (list[i].equals(item)) {
                T removed = list[i];
                for (int j = i; j < size - 1; j++) {
                    list[j] = list[j + 1];
                }
                list[--size] = null;
                return removed;
            }
        }
        return null;
    }


    @Override
    public Iterator<T> getIterator() {
        return new MyListIterator();
    }
    
    @Override
    public void debugPrintAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void printDebugIdentity() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public ListInterface<T> deepCopy() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private class MyListIterator implements Iterator<T> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return list[currentIndex++];
        }
    }
}
    
    

