package com.sockets.server.start;

import com.sockets.server.net.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException{
        new Server().runServer();
    }

}
