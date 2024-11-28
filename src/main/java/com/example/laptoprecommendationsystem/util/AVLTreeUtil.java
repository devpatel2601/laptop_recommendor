package com.example.laptoprecommendationsystem.util;

import java.util.*;

public class AVLTreeUtil {
    // Node class for AVL tree
    static class Node {
        String word;
        int frequency;
        int height;
        Node left, right;

        Node(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
            this.height = 1;
        }
    }

    // AVL tree implementation

        private Node root;

        public void insert(String word, int frequency) {
            root = insert(root, word, frequency);
        }

        private Node insert(Node node, String word, int frequency) {
            if (node == null) {
                return new Node(word, frequency);
            }

            if (word.equals(node.word)) {
                node.frequency += frequency;
                return node;
            }

            if (word.compareTo(node.word) < 0) {
                node.left = insert(node.left, word, frequency);
            } else {
                node.right = insert(node.right, word, frequency);
            }

            node.height = 1 + Math.max(height(node.left), height(node.right));
            return balance(node);
        }

        public List<String> getTopCompletions(String prefix, int k) {
            PriorityQueue<Node> minHeap = new PriorityQueue<>(Comparator.comparingInt(n -> n.frequency));
            collectWordsWithPrefix(root, prefix, minHeap, k);

            List<String> result = new ArrayList<>();
            while (!minHeap.isEmpty()) {
                Node node = minHeap.poll();
                result.add(node.word + ": " + node.frequency);
            }
            Collections.reverse(result);
            return result;
        }

        private void collectWordsWithPrefix(Node node, String prefix, PriorityQueue<Node> minHeap, int k) {
            if (node == null) return;

            if (node.word.startsWith(prefix)) {
                if (minHeap.size() < k) {
                    minHeap.offer(node);
                } else if (node.frequency > minHeap.peek().frequency) {
                    minHeap.poll();
                    minHeap.offer(node);
                }
            }

            collectWordsWithPrefix(node.left, prefix, minHeap, k);
            collectWordsWithPrefix(node.right, prefix, minHeap, k);
        }

        private int height(Node node) {
            return node == null ? 0 : node.height;
        }

        private int getBalance(Node node) {
            return node == null ? 0 : height(node.left) - height(node.right);
        }

        private Node rotateRight(Node y) {
            Node x = y.left;
            Node T2 = x.right;
            x.right = y;
            y.left = T2;
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            return x;
        }

        private Node rotateLeft(Node x) {
            Node y = x.right;
            Node T2 = y.left;
            y.left = x;
            x.right = T2;
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            return y;
        }

        private Node balance(Node node) {
            int balanceFactor = getBalance(node);
            if (balanceFactor > 1 && getBalance(node.left) >= 0) return rotateRight(node);
            if (balanceFactor > 1 && getBalance(node.left) < 0) {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
            if (balanceFactor < -1 && getBalance(node.right) <= 0) return rotateLeft(node);
            if (balanceFactor < -1 && getBalance(node.right) > 0) {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
            return node;
        }
    }
