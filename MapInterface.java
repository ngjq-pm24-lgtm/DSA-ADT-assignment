package ADT;

public interface MapInterface<K, V> {
    public void put(K key, V value);         // Add or update a consultation (e.g., by ID)
    public V get(K key);                     // Retrieve consultation by ID
    public V remove(K key);                  // Remove consultation by ID
    public boolean containsKey(K key);       // Check if consultation ID exists
    public boolean isEmpty();                // Is map empty?
    public int size();                       // Total consultations
}

