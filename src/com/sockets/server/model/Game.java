package com.sockets.server.model;

import java.util.Random;

public class Game {

    private int score;
    private int remainingGuesses;
    private String correctWord;
    private final Word wordHandler;
    private StringBuilder currentProgress;
    private boolean gameOver;
    private int player;

    public Game() {
        score = 0;
        wordHandler = new Word();
        correctWord = wordHandler.getWord().toLowerCase();
        currentProgress = new StringBuilder(wordHandler.dash(correctWord));
        remainingGuesses = correctWord.length();
        gameOver = false;
        player = new Random().nextInt(10000);
    }

    private void incrementScore() {
        score++;
    }

    private void decrementScore() {
        score--;
    }

    public int getScore() {
        return score;
    }

    public void decrementGuesses() {
        remainingGuesses--;
        if (remainingGuesses < 1) {
            decrementScore();
            gameOver = true;
        }
    }

    public boolean guess(String guess) {
        // Word guess
        if (guess.length() > 1) {
            if (guess.equals(correctWord)) {
                return correctWordGuess();
            } else {
                return incorrectGuess();
            }
            // Letter guess
        } else if (guess.length() == 1) {
            char letter = guess.charAt(0);
            if (correctWord.contains(guess)) {
                for (int i = 0; i < correctWord.length(); i++) {
                    if (correctWord.charAt(i) == letter) {
                        currentProgress.setCharAt(i, letter);
                    }
                }
                if (!(currentProgress.toString().contains("_"))) {
                    return correctWordGuess();
                }
            } else {
                return incorrectGuess();
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean correctWordGuess() {
        incrementScore();
        gameOver = true;
        return true;
    }

    private boolean incorrectGuess() {
        decrementGuesses();
        return false;
    }

    public String getProgress() {
        return currentProgress.toString().toUpperCase();
    }

    public String getCorrectWord() {
        return correctWord.toUpperCase();
    }

    public void newGame() {
        correctWord = wordHandler.newWord().toLowerCase();
        currentProgress = new StringBuilder(wordHandler.dash(correctWord));
        gameOver = false;
        remainingGuesses = correctWord.length();
        System.out.println("Spelare: " + player + " Land: " + getCorrectWord());
    }

    public boolean gameOver() {
        return gameOver;
    }

    public int getRemainingGuesses() {
        return remainingGuesses;
    }

}
