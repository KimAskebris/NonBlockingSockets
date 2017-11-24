package com.sockets.client.view;

import com.sockets.client.controller.Controller;
import com.sockets.client.net.OutputHandler;

import java.util.Scanner;

public class View implements Runnable {

    private Controller cont;
    private Scanner userInput;
    private boolean playing = false;

    public View() {
            userInput = new Scanner(System.in);
    }

    public void start() {
        if (playing) {
            return;
        } else {
            playing = true;
            cont = new Controller();
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        cont.connect(new ConsoleOutput());
        while (true) {
            String userMsg = userInput.nextLine();
            if (userMsg.equals("quit")) {
                cont.disconnect();
                break;
            }
            cont.sendMessage(userMsg);
        }
    }

    private class ConsoleOutput implements OutputHandler {

        @Override
        public void printToConsole(String message) {
            System.out.println(message);
        }
    }

}
