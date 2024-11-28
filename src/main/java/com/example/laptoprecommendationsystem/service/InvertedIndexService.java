package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvertedIndexService {

    // TrieNode definition
    private static class TrieNode {
        private final TrieNode[] children;
        private boolean isEndOfWord;
        private final Set<Long> laptopIds; // Stores unique laptop IDs

        TrieNode() {
            this.children = new TrieNode[26]; // Supports lowercase English letters
            this.isEndOfWord = false;
            this.laptopIds = new HashSet<>();
        }

        void addLaptopId(Long laptopId) {
            laptopIds.add(laptopId);
        }
    }

    private final TrieNode root;

    public InvertedIndexService() {
        this.root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie with its associated laptop ID.
     * @param word The word to insert.
     * @param laptopId The ID of the laptop where the word appears.
     */
    public void insert(String word, Long laptopId) {
        if (word == null || word.trim().isEmpty()) {
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
        currentNode.addLaptopId(laptopId);
    }

    /**
     * Searches for laptops with a given prefix.
     * @param prefix The prefix to search for.
     * @return A set of laptop IDs associated with the prefix.
     */
    public Set<Long> searchByPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptySet(); // Return empty result for null or empty prefix
        }

        TrieNode currentNode = root;

        for (char character : prefix.toLowerCase().toCharArray()) {
            if (!isValidCharacter(character)) {
                return Collections.emptySet(); // Return empty if prefix contains invalid characters
            }

            int index = character - 'a';
            if (currentNode.children[index] == null) {
                return Collections.emptySet(); // Prefix not found
            }
            currentNode = currentNode.children[index];
        }

        return collectLaptopIds(currentNode); // Collect all matching laptop IDs
    }

    /**
     * Collects all laptop IDs from the given TrieNode and its descendants.
     * @param node The current TrieNode.
     * @return A set of laptop IDs.
     */
    private Set<Long> collectLaptopIds(TrieNode node) {
        Set<Long> result = new HashSet<>(node.laptopIds);

        for (TrieNode child : node.children) {
            if (child != null) {
                result.addAll(collectLaptopIds(child));
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

    /**
     * Indexes a laptop's fields for searchability.
     * @param laptop The Laptop object to index.
     */
    public void indexLaptop(Laptop laptop) {
        if (laptop == null) return;

        Long id = laptop.getId();

        // Index fields such as brand name, product name, OS, etc.
        insert(laptop.getBrandName(), id);
        insert(laptop.getProductName(), id);
        insert(laptop.getOs(), id);
        insert(laptop.getProcessor(), id);
        insert(laptop.getGraphics(), id);
        insert(laptop.getDisplay(), id);
        insert(laptop.getMemory(), id);
        insert(laptop.getStorage(), id);
    }
}
