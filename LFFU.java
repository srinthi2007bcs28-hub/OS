import java.util.HashMap;
import java.util.Map;

class LFUCache {
    // Node structure storing key, value, and its current access frequency
    private class Node {
        int key, value, freq;
        Node prev, next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1; // New nodes start with a frequency of 1
        }
    }

    // A custom Doubly Linked List to handle node ordering for a specific frequency
    private class DoublyLinkedList {
        Node head, tail;
        int size;

        DoublyLinkedList() {
            head = new Node(-1, -1);
            tail = new Node(-1, -1);
            head.next = tail;
            tail.prev = head;
            size = 0;
        }

        void insertAtHead(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            size++;
        }

        void removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        Node removeTail() {
            if (size == 0) return null;
            Node lruNode = tail.prev;
            removeNode(lruNode);
            return lruNode;
        }
    }

    private final int capacity;
    private int curSize;
    private int minFrequency;
    private final Map<Integer, Node> cacheMap;
    private final Map<Integer, DoublyLinkedList> freqMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.curSize = 0;
        this.minFrequency = 0;
        this.cacheMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    // Helper: Increments a node's frequency and shifts it to the appropriate list
    private void updateFrequency(Node node) {
        int oldFreq = node.freq;
        DoublyLinkedList oldList = freqMap.get(oldFreq);
        oldList.removeNode(node);

        // If the current minFrequency list becomes empty, increment global minFrequency
        if (oldFreq == minFrequency && oldList.size == 0) {
            minFrequency++;
        }

        node.freq++;
        // Move node to the new frequency list
        freqMap.computeIfAbsent(node.freq, k -> new DoublyLinkedList()).insertAtHead(node);
    }

    public int get(int key) {
        if (!cacheMap.containsKey(key)) {
            return -1;
        }
        Node node = cacheMap.get(key);
        updateFrequency(node); // Step up its frequency count
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        // Case 1: Key already exists -> Update value and update frequency
        if (cacheMap.containsKey(key)) {
            Node node = cacheMap.get(key);
            node.value = value;
            updateFrequency(node);
            return;
        }

        // Case 2: Cache is full -> Evict the LFU (and LRU tie-breaker) node
        if (curSize >= capacity) {
            DoublyLinkedList minFreqList = freqMap.get(minFrequency);
            Node deadNode = minFreqList.removeTail(); // Removes the oldest node of minimum frequency
            cacheMap.remove(deadNode.key);
            curSize--;
        }

        // Case 3: New key insertion
        Node newNode = new Node(key, value);
        cacheMap.put(key, newNode);
        
        // New nodes always enter at frequency 1
        minFrequency = 1;
        freqMap.computeIfAbsent(1, k -> new DoublyLinkedList()).insertAtHead(newNode);
        curSize++;
    }
}
