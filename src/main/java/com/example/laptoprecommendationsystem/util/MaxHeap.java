package com.example.laptoprecommendationsystem.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MaxHeap<T> {

    private final List<T> heap;
    private final Comparator<T> comparator;

    public MaxHeap(Comparator<T> comparator) {
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    public void add(T element) {
        heap.add(element);
        heapifyUp(heap.size() - 1);
    }

    public T remove() {
        if (heap.isEmpty()) {
            return null;
        }
        T root = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        heapifyDown(0);
        return root;
    }

    private void heapifyUp(int index) {
        while (index > 0 && comparator.compare(heap.get(index), heap.get(parent(index))) > 0) {
            swap(index, parent(index));
            index = parent(index);
        }
    }

    private void heapifyDown(int index) {
        int left = leftChild(index);
        int right = rightChild(index);
        int largest = index;

        if (left < heap.size() && comparator.compare(heap.get(left), heap.get(largest)) > 0) {
            largest = left;
        }
        if (right < heap.size() && comparator.compare(heap.get(right), heap.get(largest)) > 0) {
            largest = right;
        }
        if (largest != index) {
            swap(index, largest);
            heapifyDown(largest);
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    private int leftChild(int index) {
        return 2 * index + 1;
    }

    private int rightChild(int index) {
        return 2 * index + 2;
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    public int size() {
        return heap.size();
    }
}
