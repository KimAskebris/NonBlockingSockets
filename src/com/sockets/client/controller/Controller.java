package com.sockets.client.controller;

import com.sockets.client.net.OutputHandler;
import com.sockets.client.net.ServerConnection;

public class Controller {

    private ServerConnection serverConnection;

    public Controller() {

    }

    public void connect(OutputHandler outputHandler) {
        serverConnection = new ServerConnection();
        serverConnection.connect(outputHandler);
    }

    public void disconnect() {
        serverConnection.disconnect();
    }

    public void sendMessage(String msg) {
        serverConnection.addMessageToSend(msg);
    }

}
