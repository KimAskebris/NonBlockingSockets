package com.sockets.server.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Word {

    private String currentWord;
    private final String FILE_PATH_BASE = "C:\\JavaProjects\\NonBlockingSockets\\games\\";
    private final String[] WORD_TYPES = {"countries","capitals"};
    private final String EXTENSION = ".txt";
    private ArrayList<String> words;

    public Word() {
        getAllWords(FILE_PATH_BASE + WORD_TYPES[1] + EXTENSION);
        currentWord = newWord();
    }

    public String getWord() {
        return currentWord;
    }

    /*
    * Generates a new word from the text file,
    * sets it to the current word, and returns it.
     */
    public final String newWord() {
        Random randomGenerator = new Random();
        String randomWord = words.get(randomGenerator.nextInt(words.size()));
        currentWord = randomWord;
        return randomWord;
    }

    public String dash(String word) {
        String regexAllCharacters = ".";
        String dash = "_";
        String dashedWord = word.replaceAll(regexAllCharacters, dash);
        return dashedWord;
    }

    private void getAllWords(String filePath) {
        words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        }
    }

}