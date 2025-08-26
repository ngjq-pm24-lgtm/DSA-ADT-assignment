package ADT;

//source: https://github.com/nishantroy/Data-Structures-and-Algorithms/blob/master/HashMap/HashMapInterface.java

import java.util.Comparator;


public interface MapInterface_TY<K, V> {
    int STARTING_SIZE = 14;
    double MAX_LOAD_FACTOR = 0.67;

    boolean add(K key, V value);
    V remove(K key);
    V get(K key);
    boolean contains(K key);
    void clear();
    int size();
    boolean resizeBackingTable(int length);
    MapEntry<K, V>[] getTable();
    K findLargestKey();
    MapEntry<K, V>[] sort(Comparator<K> comparator);

}