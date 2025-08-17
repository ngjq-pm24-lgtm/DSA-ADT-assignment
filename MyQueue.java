package ADT;

public class MyQueue<T> implements QueueInterface<T> {
    private T[] elements;
    private int front, rear, size;
    private static final int DEFAULT_CAPACITY = 50;

    public MyQueue() {
        elements = (T[]) new Object[DEFAULT_CAPACITY];
        front = 0;
        rear = -1;
        size = 0;
    }

    @Override
    public void enqueue(T element) {
        if (size == elements.length)
            throw new IllegalStateException("Queue is full.");
        rear = (rear + 1) % elements.length;
        elements[rear] = element;
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) return null;
        T removed = elements[front];
        front = (front + 1) % elements.length;
        size--;
        return removed;
    }

    @Override
    public T peek() {
        return isEmpty() ? null : elements[front];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) return null;
        return elements[(front + index) % elements.length];
    }
}
