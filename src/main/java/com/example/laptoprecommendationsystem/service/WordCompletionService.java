package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.util.AVLTreeUtil;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WordCompletionService {


    private final AVLTreeUtil avlTree = new AVLTreeUtil();

    public void buildVocabulary(Map<String, Integer> vocabulary) {
        for (Map.Entry<String, Integer> entry : vocabulary.entrySet()) {
            avlTree.insert(entry.getKey(), entry.getValue());
        }
    }

    public List<String> getSuggestions(String prefix, int topK) {
        return avlTree.getTopCompletions(prefix, topK);
    }
}
