package bstmap;

import java.util.*;

/**Implement BSTMap with nodes threading. */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>, Iterable<K>{
    private BSTNode<K, V> root;
    private BSTNode<K, V> threadSentinel;
    private int size;
    public BSTMap() {
        root = null;
        threadSentinel = new BSTNode<>(null, null);
        size = 0;
    }
    private static class BSTNode<K extends Comparable<K>, V> {
        K key;
        V value;
        BSTNode<K, V> lchild;
        BSTNode<K, V> rchild;
        /**tag == 1 means a real child, == 0 means a thread. */
        int ltag;
        int rtag;
        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            lchild = rchild = null;
            ltag = rtag = 0;
        }
        int children() {
            int res = 2;
            if (ltag == 0) {
                res -= 1;
            }
            if (rtag == 0) {
                res -= 1;
            }
            return res;
        }
    }
    /** Find the next node by inorder and return null if it's the last node. */
    private BSTNode<K, V> findNext(BSTNode<K, V> curr) {
        if (curr.rtag == 0) {
            return curr.rchild;
        } else {
            curr = curr.rchild;
            while (true) {
                if (curr.ltag == 1) {
                    curr = curr.lchild;
                } else {
                    return curr;
                }
            }
        }
    }
    /** Find the prior node by inorder and return null if it's the first node. */
    private BSTNode<K, V> findPrior(BSTNode<K, V> curr) {
        if (curr.ltag == 0) {
            return curr.lchild;
        } else {
            curr = curr.lchild;
            while (true) {
                if (curr.rtag == 1) {
                    curr = curr.rchild;
                } else {
                    return curr;
                }
            }
        }
    }
    /** Find the node with the given key. */
    private BSTNode<K, V> findNode(K key) {
        BSTNode<K, V> p = root;
        while (true) {
            if (p.key.compareTo(key) == 0) {
                return p;
            } else if (p.key.compareTo(key) > 0) {
                if (p.ltag == 0) {
                    return null;
                }
                p = p.lchild;
            } else {
                if (p.rtag == 0) {
                    return null;
                }
                p = p.rchild;
            }
        }
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (isEmpty() || key == null) {
            return false;
        }
        return findNode(key) != null;
    }

    /** Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (isEmpty() || key == null) {
            return null;
        }
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
        threadSentinel = new BSTNode<>(null, null);
        size = 0;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }
    /** Check if it's an empty tree. */
    private boolean isEmpty() {
        return size == 0;
    }
    /** Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            return;
        }
        if (isEmpty()) {
            root = new BSTNode<>(key, value);
            threadSentinel.lchild = threadSentinel.rchild = root;
            root.lchild = root.rchild = threadSentinel;
            size += 1;
            return;
        }
        insert(key, value);
    }
    /** Insert a node to the BST.*/
    private void insert(K key, V value) {
        BSTNode<K, V> curr = root;
        while (true) {
            int cmp = curr.key.compareTo(key);
            if (cmp == 0) {
                curr.value = value;
                return;
            } else if(cmp > 0) {
                if (curr.ltag == 0) {
                    BSTNode<K, V> newNode = new BSTNode<>(key, value);
                    if (curr.lchild.rtag == 0) {
                        curr.lchild.rchild = newNode;
                    }
                    newNode.lchild = curr.lchild;
                    curr.ltag = 1;
                    curr.lchild = newNode;
                    newNode.rchild = curr;
                    size += 1;
                    return;
                } else {
                    curr = curr.lchild;
                }
            } else {
                if (curr.rtag == 0) {
                    BSTNode<K,V> newNode = new BSTNode<>(key, value);
                    if (curr.rchild.ltag == 0) {
                        curr.rchild.lchild = newNode;
                    }
                    newNode.rchild = curr.rchild;
                    curr.rtag = 1;
                    curr.rchild = newNode;
                    newNode.lchild = curr;
                    size += 1;
                    return;
                } else {
                    curr = curr.rchild;
                }
            }
        }
    }


    /** Unsupported method. */
    @Override
    public Set<K> keySet() {
        Set<K> res = new HashSet<>();
        for (K i: this) {
            res.add(i);
        }
        return res;
    }

    @Override
    public V remove(K key) {
        if (isEmpty() || key == null) {
            return null;
        }
        return removeHelper(key);
    }

    /** Remove a node. */
    private V removeHelper(K key) {
        BSTNode<K, V> target = findNode(key);
        if (target == null) {
            return null;
        }
        if (target.children() == 0) {
            return removeLeaf(target);
        } else {
            if (target.ltag == 1) {
                BSTNode<K, V> realTarget = findPrior(target);
                K k = realTarget.key;
                V v = realTarget.value;
                removeHelper(k);
                V res = target.value;
                target.key = k;
                target.value = v;
                return res;
            } else {
                BSTNode<K, V> realTarget = findNext(target);
                K k = realTarget.key;
                V v = realTarget.value;
                removeHelper(k);
                V res = target.value;
                target.key = k;
                target.value = v;
                return res;
            }
        }
    }
    /** Remove a leaf node. */
    private V removeLeaf(BSTNode<K, V> p) {
        BSTNode<K, V> pre = findPrior(p);
        BSTNode<K, V> next = findNext(p);
        V res = p.value;
        if (pre.rchild == p) {
            pre.rchild = p.rchild;
            pre.rtag = 0;
        }
        if (next.lchild == p) {
            next.lchild = p.lchild;
            next.ltag = 0;
        }
        size -= 1;
        return res;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    /**Unsupported method. */
    @Override
    public Iterator<K> iterator() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return new BSTMapIterator();
    }

    private class BSTMapIterator implements Iterator<K> {
        BSTNode<K, V> curr;
        BSTMapIterator() {
            curr = findNext(threadSentinel);
        }
        @Override
        public boolean hasNext() {
            return curr != threadSentinel;
        }
        @Override
        public K next() {
            K res = curr.key;
            curr = findNext(curr);
            return res;
        }
    }


    /** Print all elements with the key increasing. */
    public void printInOrder() {
        if (isEmpty()) {
            return;
        }
        printInOrderHelper();
    }

    /** Helper of printInOrder. */
    private void printInOrderHelper() {
        BSTNode<K, V> p = findNext(threadSentinel);
        while (p != threadSentinel) {
            printOneElement(p);
            p = findNext(p);
        }
    }
    /** Print an element's key and value. */
    private void printOneElement(BSTNode<K, V> p) {
        System.out.printf("Key: %s Value: %s\n", p.key.toString(), p.value.toString());
    }
}
