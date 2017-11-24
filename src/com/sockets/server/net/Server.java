package com.sockets.server.net;

import com.sockets.server.controller.Controller;
import com.sockets.server.model.Game;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private static final String LOCAL_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 12000;
    private final int MAX_MESSAGE_SIZE = 1024;
    private ServerSocketChannel server;
    private Selector selector;
    private final ByteBuffer buffer;
    private Controller cont;
    private String responseMessage;

    public Server() {
        cont = new Controller();
        buffer = ByteBuffer.allocate(MAX_MESSAGE_SIZE);
    }

    public void runServer() {
        initSelector();
        initServer();
        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                System.err.println("Issue with selector in runServer() " + e.getMessage());
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    recvMessageFromClient(key);
                } else if (key.isWritable()) {
                    sendMessageToClient(key);
                }
            }
        }
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel client = serverSocketChannel.accept();
            print("Connected to " + client.getLocalAddress());
            client.configureBlocking(false);
            SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
            clientKey.attach(cont.initGame());
            responseMessage = cont.startGame((Game) clientKey.attachment());
            selector.wakeup();
        } catch (IOException ioe) {
            System.err.println("Exception thrown in accept() " + ioe.getMessage());
        }
    }

    private void sendMessageToClient(SelectionKey key) {
        buffer.clear();
        SocketChannel client = (SocketChannel) key.channel();
        buffer.put(responseMessage.getBytes());
        buffer.flip();
        try {
            while (buffer.hasRemaining()) {
                client.write(buffer);
            }
        } catch (IOException ioe) {
            System.err.println("Buffer issue in sendMessageToClient " + ioe.getMessage());
        }
        client.keyFor(selector).interestOps(SelectionKey.OP_READ);
        buffer.clear();
    }

    private void recvMessageFromClient(SelectionKey key) {
        buffer.clear();
        SocketChannel client = (SocketChannel) key.channel();
        try {
            client.read(buffer);
            String clientMessage = bytebufferToString();
            responseMessage = cont.progress(clientMessage.toLowerCase(), (Game) key.attachment());
            client.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        } catch (IOException ioe) {
            try {
                System.err.println("Client is disconnected " + client.getLocalAddress());
                client.close();
            } catch (IOException e) {
                System.err.println("Could not close the connection " + e.getMessage());
            }
        }
        buffer.clear();
    }

    private void initSelector() {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.err.println("Could not initSelector " + e.getMessage());
        }
    }

    private void initServer() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(LOCAL_HOST, DEFAULT_PORT));
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.err.println("Could not init server " + e.getMessage());
        }
        print("Server running...");
    }

    private String bytebufferToString() {
        buffer.flip();
        byte [] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes);
    }

    private void print(String msg) {
        System.out.println(msg);
    }

}
