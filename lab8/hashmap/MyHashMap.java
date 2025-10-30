package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private double loadFactor = 1.5;
    private int bucketsSize = 16;
    private int elemSize;
    private HashSet<K> keyset;
    private double resizeFactor = 2.0;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(bucketsSize);
        elemSize = 0;
        keyset = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        this();
        bucketsSize = initialSize;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this(initialSize);
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection[] res = new Collection[tableSize];
        for (int i = 0; i < tableSize; i += 1) {
            res[i] = createBucket();
        }
        return res;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        elemSize = 0;
        buckets = createTable(bucketsSize);
        keyset.clear();
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    /** Change a hashcode to an index. */
    private int changeToIndex(int hashCode) {return Math.floorMod(hashCode, bucketsSize); }
    /** Change a hashcode to an index with given base. */
    private int changeToIndex(int hashCode, int base) {return Math.floorMod(hashCode, base); }
    /** Returns the key-value Node. */
    private Node getNode(K key) {
        for (Node i: buckets[changeToIndex(key.hashCode())]) {
            if (i.key.equals(key)) {
                return i;
            }
        }
        return null;
    }
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        Node res = getNode(key);
        if (res == null) {return null; }
        return res.value;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return elemSize;
    }

    /** Resize the buckets when elemSize / buckets > loadFactor. */
    private void resize(int capacity) {
        Collection<Node>[] newBuckets = createTable(capacity);
        for (K i: keyset) {
            Node newNode = getNode(i);
            newBuckets[changeToIndex(i.hashCode(), capacity)].add(newNode);
        }
        bucketsSize = capacity;
        buckets = newBuckets;
    }
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        Node target = getNode(key);
        if (target != null) {
            target.value = value;
            return;
        }
        buckets[changeToIndex(key.hashCode())].add(createNode(key, value));
        keyset.add(key);
        elemSize += 1;
        if ((double) (elemSize / bucketsSize) > loadFactor) {resize((int) Math.round(bucketsSize * resizeFactor));}
    }

    /**Return an iterator of keys. */
    @Override
    public Iterator<K> iterator() {
        return keyset.iterator();
    }

    /** Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        return keyset;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key) {
        Node target = getNode(key);
        if (target == null) {return null; }
        V res = target.value;
        buckets[changeToIndex(key.hashCode())].remove(target);
        elemSize -= 1;
        keyset.remove(key);
        return res;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value) {
        return remove(key);
    }
}
