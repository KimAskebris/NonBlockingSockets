package com.sockets.server.controller;

import com.sockets.server.model.GameHandler;
import com.sockets.server.model.Game;

public class Controller {

    private GameHandler gameHandler;

    public Controller() {
        gameHandler = new GameHandler();
    }

    public String startGame(Game game) {
        return gameHandler.introduction() + gameHandler.printableState(game);
    }

    public String progress(String clientMessage, Game game){
        return gameHandler.progress(clientMessage, game);
    }

    public Game initGame() {
        return gameHandler.initGame();
    }

}
