package com.sockets.server.model;

public class GameHandler {

    private String clientGuess;
    private Game game;

    public GameHandler() {

    }

    public Game initGame() {
        return new Game();
    }

    public String progress(String clientGuess, Game game) {
        this.clientGuess = clientGuess;
        this.game = game;
        return guess();
    }

    private String guess() {
        if (game.guess(clientGuess)) {
            if (game.gameOver()) {
                return wonGame(true);
            } else {
                return letterGuess(true);
            }
        } else {
            if (game.gameOver()) {
                return wonGame(false);
            }
            return letterGuess(false);
        }
    }

    private String letterGuess(boolean correctLetterGuess) {
        String guessStatus = correctLetterGuess ? "Right!" : "Wrong!";
        String response = guessStatus + "\n" +
                "Guesses left: " + game.getRemainingGuesses() + "\n"
                + game.getProgress() + "\n";
        return response;
    }

    private String wonGame(boolean won) {
        String wonOrLost = won ? "Right!\nYou won.\n" : "Wrong!\nYou lost.\n";
        String gameOverMessage = wonOrLost
                + "The correct word was: " + game.getCorrectWord() + "\n";
        game.newGame();
        return gameOverMessage + printableState(game);
    }

    public String introduction() {
        return "--------- WELCOME TO HANGMAN! ---------\n"
                + "------- Guess on world capitals -------\n";
    }

    public String printableState(Game game) {
        String state = "Score: " + game.getScore()
                + "\nGuesses: " + game.getRemainingGuesses()
                + "\nProgress: " + game.getProgress() + "\n";
        return state;
    }

}


