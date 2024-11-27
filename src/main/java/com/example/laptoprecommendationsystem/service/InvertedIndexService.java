package com.example.laptoprecommendationsystem.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvertedIndexService {

    // TrieNode definition
    private static class TrieNode {
        private final TrieNode[] children;
        private boolean isEndOfWord;
        private final Set<Integer> laptopIndexes;  // Stores unique laptop indexes

        TrieNode() {
            this.children = new TrieNode[26]; // Supports lowercase English letters
            this.isEndOfWord = false;
            this.laptopIndexes = new HashSet<>();
        }

        // Adds an index to the current node
        void addLaptopIndex(int index) {
            laptopIndexes.add(index);
        }
    }

    private final TrieNode root;

    public InvertedIndexService() {
        this.root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie with its associated laptop index.
     * @param word The word to insert.
     * @param laptopIndex The index of the laptop where the word appears.
     */
    public void insert(String word, int laptopIndex) {
        if (word == null || word.isEmpty()) {
            return; // Skip empty or null words
        }

        TrieNode currentNode = root;

        for (char character : word.toLowerCase().toCharArray()) {
            if (!isValidCharacter(character)) {
                continue; // Skip invalid characters
            }

            int index = character - 'a';
            if (currentNode.children[index] == null) {
                currentNode.children[index] = new TrieNode();
            }
            currentNode = currentNode.children[index];
        }

        currentNode.isEndOfWord = true;
        currentNode.addLaptopIndex(laptopIndex);
    }

    /**
     * Searches for words with a given prefix.
     * @param prefix The prefix to search for.
     * @return A list of laptop indexes associated with the prefix.
     */
    public List<Integer> searchByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList(); // Return empty result for null or empty prefix
        }

        TrieNode currentNode = root;

        for (char character : prefix.toLowerCase().toCharArray()) {
            if (!isValidCharacter(character)) {
                return Collections.emptyList(); // Return empty if prefix contains invalid characters
            }

            int index = character - 'a';
            if (currentNode.children[index] == null) {
                return Collections.emptyList(); // Prefix not found
            }
            currentNode = currentNode.children[index];
        }

        return new ArrayList<>(collectLaptopIndexes(currentNode)); // Collect all matching laptop indexes
    }

    /**
     * Collects all laptop indexes from the given TrieNode and its descendants.
     * @param node The current TrieNode.
     * @return A set of laptop indexes.
     */
    private Set<Integer> collectLaptopIndexes(TrieNode node) {
        Set<Integer> result = new HashSet<>(node.laptopIndexes);

        for (TrieNode child : node.children) {
            if (child != null) {
                result.addAll(collectLaptopIndexes(child));
            }
        }

        return result;
    }

    /**
     * Checks if a character is a valid lowercase English letter.
     * @param character The character to validate.
     * @return True if valid, false otherwise.
     */
    private boolean isValidCharacter(char character) {
        return character >= 'a' && character <= 'z';
    }
}
