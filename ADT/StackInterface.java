package ADT;

public interface StackInterface<T> {
    public void push(T element);        // Save previous state before cancel
    public T pop();                     // Undo last cancellation
    public T peek();                    // See last cancelled consultation
    public boolean isEmpty();           // Check if undo stack is empty
    public int size();                  // Get number of undo-able actions
}
