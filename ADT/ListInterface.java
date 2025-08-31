package ADT;

import java.util.Comparator;
import java.util.Iterator;


public interface ListInterface<T> {

   boolean add(T newEntry);
   boolean add(int newPosition, T newEntry);
   T remove(int givenPosition);
   T remove(T item);
   void clear();
   boolean replace(int givenPosition, T newEntry);
   T get(int givenPosition);
   boolean contains(T anEntry);
   int size();
   boolean isEmpty();
   boolean isFull();
   void sort(Comparator<T> comparator);
   ListInterface<T> deepCopy();
   Iterator<T> getIterator();
}




