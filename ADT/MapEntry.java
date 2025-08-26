package ADT;

import java.io.Serializable;

public class MapEntry<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isRemoved;
    private K key;
    private V value;

    public MapEntry(K k, V v) {
        key = k;
        value = v;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
    
    @Override
    public String toString(){
        return key + ": " + value;
    }

}
