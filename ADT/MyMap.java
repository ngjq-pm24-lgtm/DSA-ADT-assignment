package ADT;
public class MyMap<K, V> implements MapInterface<K, V>, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    
    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private final Entry<K, V> DELETED = new Entry<>(null, null);

    /**
     * Constructs an empty MyMap with the default initial capacity (16).
     */
    @SuppressWarnings("unchecked")
    public MyMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.table = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
    }

    @Override
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value cannot be null");
        }

        // Resize if needed
        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }

        int index = getIndex(key);
        int startIndex = index;
        int firstDeleted = -1;

        // Linear probing to find the key or an empty slot
        do {
            if (table[index] == null) {
                // Found an empty slot
                if (firstDeleted != -1) {
                    // Place in the first deleted slot we found
                    index = firstDeleted;
                }
                table[index] = new Entry<>(key, value);
                size++;
                return;
            } else if (table[index] == DELETED) {
                // Remember the first deleted slot
                if (firstDeleted == -1) {
                    firstDeleted = index;
                }
            } else if (table[index].key.equals(key)) {
                // Key exists, update value
                table[index].value = value;
                return;
            }
            index = (index + 1) % capacity;
        } while (index != startIndex);

        // If we get here, the table is full (shouldn't happen because of resize)
        throw new IllegalStateException("Hash table is full");
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if no mapping exists
     */
    @Override
    public V get(K key) {
        if (key == null) return null;
        
        int index = getIndex(key);
        int startIndex = index;
        
        do {
            if (table[index] == null) {
                return null;
            }
            
            if (table[index] != DELETED && table[index].key.equals(key)) {
                return table[index].value;
            }
            
            index = (index + 1) % capacity;
        } while (index != startIndex);
        
        return null;
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    @Override
    public V remove(K key) {
        if (key == null) return null;
        
        int index = getIndex(key);
        int startIndex = index;
        
        do {
            if (table[index] == null) {
                return null;
            }
            
            if (table[index] != DELETED && table[index].key.equals(key)) {
                V oldValue = table[index].value;
                table[index] = DELETED;
                size--;
                return oldValue;
            }
            
            index = (index + 1) % capacity;
        } while (index != startIndex);
        
        return null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(K key) {
        if (key == null) return false;
        
        int index = getIndex(key);
        int startIndex = index;
        
        do {
            if (table[index] == null) {
                return false;
            }
            
            if (table[index] != DELETED && table[index].key.equals(key)) {
                return true;
            }
            
            index = (index + 1) % capacity;
        } while (index != startIndex);
        
        return false;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return true if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return size;
    }

    // Helper method to calculate index for a key
    private int getIndex(K key) {
        return Math.abs(key.hashCode() % capacity);
    }

    // Resize the hash table when it gets too full
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        capacity *= 2;
        table = (Entry<K, V>[]) new Entry[capacity];
        size = 0;
        
        // Rehash all entries
        for (Entry<K, V> entry : oldTable) {
            if (entry != null && entry != DELETED) {
                put(entry.key, entry.value);
            }
        }
    }

    // Inner class to represent key-value pairs
    private static class Entry<K, V> implements java.io.Serializable {
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // For testing purposes
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.key).append("=").append(entry.value);
                first = false;
            }
        }
        
        sb.append("}");
        return sb.toString();
    }
}
