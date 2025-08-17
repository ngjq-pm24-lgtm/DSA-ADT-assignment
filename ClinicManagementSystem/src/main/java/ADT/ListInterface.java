package ADT;

public interface ListInterface<T> {
    void add(T element);                        // Add at end
    void add(int index, T element);             // Add at position
    T get(int index);                           // Get element at index
    T remove(int index);                        // Remove at index
    boolean isEmpty();                          // Check if list is empty
    int size();                                 // Number of elements
}
