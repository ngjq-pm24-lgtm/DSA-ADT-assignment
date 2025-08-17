package ADT;

public interface QueueInterface<T> {
    void enqueue(T element);       // Add consultation appointment
    T dequeue();                   // Remove next consultation
    T peek();                      // View the front consultation
    boolean isEmpty();             // Check if queue is empty
    int size();                    // Get number of consultations
    T get(int index);              // Get consultation at specific index
}
