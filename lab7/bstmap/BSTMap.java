package bstmap;

import java.security.PublicKey;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private BSTNode<K, V> root;
    private int size;
    public BSTMap() {
        root = null;
        size = 0;
    }
    private static class BSTNode<K extends Comparable<K>, V> {
        K key;
        V value;
        BSTNode<K, V> lchild;
        BSTNode<K, V> rchild;
        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            lchild = rchild = null;
        }
        int children() {
            int res = 2;
            if (lchild == null) {
                res -= 1;
            }
            if (rchild == null) {
                res -= 1;
            }
            return res;
        }
    }
    /** Find the node with the given key. */
    private BSTNode<K, V> findNode(K key) {
        BSTNode<K, V> p = root;
        while (p != null) {
            if (p.key.compareTo(key) == 0) {
                return p;
            } else if (p.key.compareTo(key) > 0) {
                p = p.lchild;
            } else {
                p = p.rchild;
            }
        }
        return null;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    /** Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        BSTNode<K, V> p = findNode(key);
        if (p == null) {
            return null;
        }
        return p.value;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /** Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    /** Helper of PUT using recursion. */
    private BSTNode<K, V> put(BSTNode<K, V>curr, K key, V value) {
        if (curr == null) {
            size += 1;
            return new BSTNode<>(key, value);
        }
        int cmp = curr.key.compareTo(key);
        if (cmp == 0) {
            curr.value = value;
        } else if (cmp > 0) {
            curr.lchild = put(curr.lchild, key, value);
        } else {
            curr.rchild = put(curr.rchild, key, value);
        }
        return curr;
    }

    /** Unsupported method. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /** Unsupported method. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /** Unsupported method. */
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**Unsupported method. */
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
