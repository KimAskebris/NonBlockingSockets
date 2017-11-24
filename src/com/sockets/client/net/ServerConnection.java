package com.sockets.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerConnection implements Runnable {

    private final String LOCAL_HOST = "127.0.0.1";
    private final int PORT = 12000;
    private final int MAX_MESSAGE_SIZE = 1024;
    private SocketChannel server;
    private InetSocketAddress serverAddress;
    private OutputHandler outputHandler;
    private Selector selector;
    private boolean connected = false;
    private boolean timeToSendMessage = false;
    private final Queue<String> messagesToServer = new LinkedList<>();
    private ByteBuffer buffer;

    public ServerConnection() {
        buffer = ByteBuffer.allocate(MAX_MESSAGE_SIZE);
    }

    @Override
    public void run() {
        initConnect();
        initSelector();

        while (connected) {
            if (timeToSendMessage) {
                server.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                timeToSendMessage = false;
            }
            try {
                selector.select();
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isReadable()) {
                        recvMessageFromServer(key);
                    } else if (key.isWritable()) {
                        sendMessageToServer(key);
                    }
                }
            } catch (IOException e) {
                System.err.println("Could not selector.select in server connection thread " + e.getMessage());
            }
        }
    }

    public void connect(OutputHandler outputHandler) {
        serverAddress = new InetSocketAddress(LOCAL_HOST, PORT);
        new Thread(this).start();
        this.outputHandler = outputHandler;
    }

    private void initSelector() {
        try {
            selector = Selector.open();
            server.register(selector, SelectionKey.OP_READ);
        } catch (IOException ioe) {
            System.err.println("Could not init selector " + ioe.getMessage());
        }
    }

    private void initConnect() {
        try {
            server = SocketChannel.open(serverAddress);
            server.configureBlocking(false);
            connected = true;
        } catch (IOException ioe) {
            System.err.println("Could not init connect " + ioe.getMessage());
        }
    }

    public void disconnect() {
        try {
            connected = false;
            server.close();
        } catch (IOException ioe) {
            System.err.println("Could not disconnect " + ioe.getMessage());
        }
    }

    private void recvMessageFromServer(SelectionKey key) {
        buffer.clear();
        SocketChannel server = (SocketChannel) key.channel();
        try {
            server.read(buffer);
            String msg = bytebufferToString();
            outputHandler.printToConsole(msg);
            buffer.clear();
        } catch (IOException ioe) {
            System.err.println("Error in recvMessageFromServer " + ioe.getMessage());
            System.exit(1);
        }
        buffer.clear();
    }

    public void addMessageToSend(String message) {
        synchronized (messagesToServer) {
            messagesToServer.add(message);
        }
        timeToSendMessage = true;
        selector.wakeup();
    }

    private void sendMessageToServer(SelectionKey key) {
        buffer.clear();
        String msg;
        synchronized (messagesToServer) {
            while ((msg = messagesToServer.peek()) != null) {
                buffer.put(msg.getBytes());
                messagesToServer.remove();
            }
        }
        buffer.flip();
        try {
            while (buffer.hasRemaining()) {
                server.write(buffer);
            }
        } catch (IOException ioe) {
            System.err.println("IOException " + ioe.getMessage());
        }
        buffer.clear();
        key.interestOps(SelectionKey.OP_READ);
    }

    private String bytebufferToString() {
        buffer.flip();
        byte [] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes);
    }
}
