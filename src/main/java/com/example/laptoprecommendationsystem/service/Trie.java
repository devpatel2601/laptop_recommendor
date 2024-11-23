package com.example.laptoprecommendationsystem.service;

import java.util.ArrayList;
import java.util.List;

public class Trie {

    // Trie Node definition
    private static class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;

        TrieNode() {
            children = new TrieNode[26]; // Supports lowercase English letters
            isEndOfWord = false;
        }
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Method to insert a word into the Trie
    public void insert(String word) {
        TrieNode currentNode = root;

        for (char character : word.toCharArray()) {
            // Ensure the character is a valid lowercase English letter
            if (character < 'a' || character > 'z') {
                System.err.println("Skipping invalid character: " + character);
                continue; // Skip invalid characters
            }

            int index = character - 'a';
            if (currentNode.children[index] == null) {
                currentNode.children[index] = new TrieNode();
            }
            currentNode = currentNode.children[index];
        }

        currentNode.isEndOfWord = true; // Mark the end of the word
    }

    // Helper method for collecting all words from the Trie
    private void collectWords(TrieNode node, String prefix, List<String> words) {
        if (node == null) {
            return;
        }
        if (node.isEndOfWord) {
            words.add(prefix);
        }
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                char nextChar = (char) (i + 'a');
                collectWords(node.children[i], prefix + nextChar, words);
            }
        }
    }

    // Method to search for words with a specific prefix
    public List<String> searchByPrefix(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                return new ArrayList<>(); // No words with this prefix
            }
            current = current.children[index];
        }
        List<String> words = new ArrayList<>();
        collectWords(current, prefix, words);
        return words;
    }
    public List<String> getAllWords() {
        List<String> words = new ArrayList<>();
        collectWords(root, "", words); // Use the existing collectWords method
        return words;
    }
}