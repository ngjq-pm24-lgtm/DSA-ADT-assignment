package ADT;

public class MyList<T> implements ListInterface<T> {
    private static final int INITIAL_CAPACITY = 20;
    private T[] list;
    private int size;

    public MyList() {
        list = (T[]) new Object[INITIAL_CAPACITY];
        size = 0;
    }

    @Override
    public void add(T element) {
        ensureCapacity();
        list[size++] = element;
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) return;
        ensureCapacity();
        for (int i = size; i > index; i--) {
            list[i] = list[i - 1];
        }
        list[index] = element;
        size++;
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
}
