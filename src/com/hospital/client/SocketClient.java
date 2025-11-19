package com.hospital.client;

import java.io.*;
import java.net.*;

/**
 * Socket Client for Hospital Management System
 * Handles communication with server for messaging and file transfer
 */
public class SocketClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageListener messageListener;
    private boolean connected = false;
    private int userId;

    /**
     * Connect to server
     */
    public boolean connect(int userId) {
        this.userId = userId;
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            // Register with server
            sendMessage("REGISTER|" + userId);

            // Start listening for messages
            startMessageListener();

            System.out.println("Connected to server successfully!");
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    /**
     * Send message to another user
     */
    public void sendMessageToUser(int receiverId, String message) {
        if (connected && out != null) {
            out.println("MESSAGE|" + receiverId + "|" + message);
        }
    }

    /**
     * Send file to another user
     */
    public void sendFileToUser(int receiverId, String fileName, String fileData) {
        if (connected && out != null) {
            out.println("FILE|" + receiverId + "," + fileName + "|" + fileData);
        }
    }

    /**
     * Send raw message to server
     */
    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    /**
     * Start listening for incoming messages
     */
    private void startMessageListener() {
        Thread listenerThread = new Thread(() -> {
            try {
                String message;
                while (connected && (message = in.readLine()) != null) {
                    processIncomingMessage(message);
                }
            } catch (IOException e) {
                if (connected) {
                    System.err.println("Connection lost: " + e.getMessage());
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Process incoming messages from server
     */
    private void processIncomingMessage(String message) {
        System.out.println("Received from server: " + message);

        if (messageListener != null) {
            String[] parts = message.split("\\|", 3);
            if (parts.length >= 1) {
                String command = parts[0];

                switch (command) {
                    case "MESSAGE":
                        if (parts.length >= 3) {
                            int senderId = Integer.parseInt(parts[1]);
                            String msgContent = parts[2];
                            messageListener.onMessageReceived(senderId, msgContent);
                        }
                        break;

                    case "FILE":
                        if (parts.length >= 3) {
                            String[] fileParts = parts[1].split(",");
                            int senderId = Integer.parseInt(fileParts[0]);
                            String fileName = fileParts[1];
                            String fileData = parts[2];
                            messageListener.onFileReceived(senderId, fileName, fileData);
                        }
                        break;

                    case "REGISTERED":
                        messageListener.onRegistered();
                        break;

                    case "MESSAGE_SENT":
                        messageListener.onMessageSent();
                        break;
                }
            }
        }
    }

    /**
     * Set message listener
     */
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    /**
     * Disconnect from server
     */
    public void disconnect() {
        connected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if connected
     */
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    /**
     * Message Listener Interface
     */
    public interface MessageListener {
        void onMessageReceived(int senderId, String message);
        void onFileReceived(int senderId, String fileName, String fileData);
        void onRegistered();
        void onMessageSent();
    }
}
