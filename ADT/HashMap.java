package adt;

import enums.TimeSlot;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

//source: https://github.com/nishantroy/Data-Structures-and-Algorithms/blob/master/HashMap/HashMap.java
//        https://github.com/nishantroy/Data-Structures-and-Algorithms/blob/master/HashMap/MapEntry.java


public class HashMap<K, V> implements MapInterface<K, V>, Serializable {

    private static final long serialVersionUID = 1L;
    private MapEntry<K, V>[] table;
    private int size;

    public HashMap() {
        this(STARTING_SIZE);
    }


    public HashMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = STARTING_SIZE;
        }
        
        table = new MapEntry[initialCapacity];
        size = 0;
    }
    

    @Override
    public K findLargestKey() {
        if (size == 0) return null;
        
        K largestKey = null;
        for (MapEntry<K, V> entry : table) {
            if (entry != null && !entry.isRemoved()) {
                if (largestKey == null || ((Comparable<K>) entry.getKey()).compareTo(largestKey) > 0) {
                    largestKey = entry.getKey();
                }
            }
        }
        return largestKey;
    }


    @Override
    public boolean add(K key, V value) {
        if (key == null || value == null) return false;

        // Resize if load factor exceeded
        double loadFactor = (double) (size + 1) / table.length;
        if (loadFactor > MAX_LOAD_FACTOR) {
            resizeBackingTable(table.length * 2 + 1);
        }
        
        int rawHash = key.hashCode();
        int baseHash = Math.abs(rawHash) % table.length;
        
        int indexWithRemovedItem = -1;

        for (int i = 0; i < table.length; i++) {
            int index = (baseHash + i) % table.length;
            MapEntry<K, V> current = table[index];

            if (current == null) {  //if table[index] still vacant, then can insert here
                if (indexWithRemovedItem != -1) { //but if there exists another occupied index that isRemoved == true
                    table[indexWithRemovedItem] = new MapEntry<>(key, value); //then will insert at that occupied index 
                } else {  //otherwise, will insert at table[index]
                    table[index] = new MapEntry<>(key, value);
                }
                size++;
                return true;
            }

            if (current.isRemoved()) {  //if found a index that have removed item (isRemoved == true)
                if (indexWithRemovedItem == -1) {  // if this is the first time of finding a index that have removed item (isRemoved == true)
                    indexWithRemovedItem = index;  // save this index, so that later can add here if can't find empty index
                }
            } else if (current.getKey().equals(key)) {
                // Key already exists — update value
                current.setValue(value);
                return true;
            }
        }

        // if reach here, it means didnt manage to find an empty index
        if (indexWithRemovedItem != -1) { //so, if we found a index that have removed item (isRemoved == true) earlier
            table[indexWithRemovedItem] = new MapEntry<>(key, value); //then we add there
            size++;
            return true;
        }
        
        return false;
    }

    @Override
    public V get(K key) {
        if (key == null || size == 0) {
            return null;
        }

        int rawHash = key.hashCode();
        int baseHash = Math.abs(rawHash) % table.length;

        for (int i = 0; i < table.length; i++) {
            int index = (baseHash + i) % table.length;
            MapEntry<K, V> current = table[index];
            
            // if the key does not correspond to any entry
            if(current == null) return null;

            // if the key corresponds to an entry, and that entry is not removed
            if (!current.isRemoved() && current.getKey().equals(key)) {
                return current.getValue();
            }
        }

        return null;
    }


    @Override
    public V remove(K key) {
        if (key == null || size == 0) return null;
        
        int rawHash = key.hashCode();
        int baseHash = Math.abs(rawHash) % table.length;
        
        MapEntry<K, V> current = table[baseHash];

        int i = 0;
        if (current == null) {
            return null;
        }

        int mapSize = 0;

        //loop through the hashmap and look for entry with matching key
        //return old value, and update map entry isRemoved field to true
        while (mapSize < table.length) {

            if (current.getKey().equals(key)) {

                if (!current.isRemoved()) {

                    V removed = current.getValue();
                    current.setIsRemoved(true);
                    size--;

                    return removed;

                } else {
                    return null;
                }
            }

            i++;

            rawHash = key.hashCode();
            baseHash = Math.abs(rawHash) % table.length;

            current = table[baseHash];

            mapSize++;

            if (current == null) {
                return null;
            }

        }

        if (mapSize == table.length) {
            return null;
        }

        return null;
    }

    


    @Override
    public boolean contains(K key) {
        if (key == null || size == 0) return false;
        
        int i = 0;
        int rawHash = key.hashCode();
        int baseHash = Math.abs(rawHash) % table.length;

        while (true) {
            int probeIndex = (baseHash + i) % table.length;
            MapEntry<K, V> entry = table[probeIndex];

            if (entry == null) {
                return false; // Stop if empty slot reached
            } else if (!entry.isRemoved() && entry.getKey().equals(key)) {
                return true;
            }

            i++;
            if (i >= table.length) {
                return false; // Full table scanned
            }
        }
    }
    
    @Override
    public MapEntry<K, V>[] sort(Comparator<K> comparator) {
        
        if(comparator == null || size == 0) return null;
        
        int count = 0;
        for (MapEntry<K, V> entry : table) {
            if (entry != null && !entry.isRemoved()) {
                count++;
            }
        }

        MapEntry<K, V>[] entries = new MapEntry[count];
        int index = 0;
        for (MapEntry<K, V> entry : table) {
            if (entry != null && !entry.isRemoved()) {
                entries[index++] = entry;
            }
        }

        for (int i = 1; i < entries.length; i++) {
            MapEntry<K, V> keyEntry = entries[i];
            int j = i - 1;

            while (j >= 0 && comparator.compare(entries[j].getKey(), keyEntry.getKey()) > 0) {
                entries[j + 1] = entries[j];
                j--;
            }
            entries[j + 1] = keyEntry;
        }

        return entries;
    }
    

    @Override
    public void clear() {
        size = 0;
        table = new MapEntry[STARTING_SIZE];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean resizeBackingTable(int length) {
        if (length <= size) {
            return false;
        }

        // create new bigger table
        MapEntry<K, V>[] newTable = new MapEntry[length];

        // rehash all *active* entries into new table
        for (int i = 0; i < table.length; i++) {
            MapEntry<K, V> entry = table[i];
            if (entry != null && !entry.isRemoved()) {
                int rawHash = entry.getKey().hashCode();
                int newIndex = Math.abs(rawHash) % newTable.length;

                // handle collisions the same way as in add()
                while (newTable[newIndex] != null) {
                    newIndex = (newIndex + 1) % length;
                }
                newTable[newIndex] = entry;
            }
        }

        table = newTable;
        return true;
    }


    @Override
    public MapEntry<K, V>[] getTable() {
        return table;
    }

}