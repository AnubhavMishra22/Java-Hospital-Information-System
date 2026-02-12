package com.hospital.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Hospital Management System Server
 * Handles client connections for messaging and file transfer
 */
public class HospitalServer {

    private static final int PORT = 8888;
    private static Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet();
    private static Map<Integer, ClientHandler> userClientMap = new ConcurrentHashMap<>();
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Hospital Management System Server");
        System.out.println("===========================================");
        System.out.println("Server starting on port " + PORT + "...");

        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\nShutdown signal received. Closing all connections...");
            running = false;
            shutdownServer();
            System.out.println("Server shutdown complete.");
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started successfully!");
            System.out.println("Waiting for client connections...\n");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandlers.add(clientHandler);

                    Thread thread = new Thread(clientHandler);
                    thread.setDaemon(true); // Allow JVM to exit when main thread ends
                    thread.start();
                } catch (SocketException e) {
                    if (!running) {
                        break; // Server is shutting down
                    }
                    throw e;
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Broadcast message to specific user
     */
    public static void sendMessageToUser(int userId, String message) {
        ClientHandler handler = userClientMap.get(userId);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    /**
     * Broadcast to all connected clients
     */
    public static void broadcastMessage(String message, ClientHandler excludeHandler) {
        for (ClientHandler handler : clientHandlers) {
            if (handler != excludeHandler) {
                handler.sendMessage(message);
            }
        }
    }

    /**
     * Remove client handler
     */
    public static void removeClient(ClientHandler handler) {
        clientHandlers.remove(handler);
        if (handler.getUserId() != -1) {
            userClientMap.remove(handler.getUserId());
        }
    }

    /**
     * Register user with client handler
     */
    public static void registerUser(int userId, ClientHandler handler) {
        userClientMap.put(userId, handler);
        System.out.println("User ID " + userId + " registered with socket connection");
    }

    /**
     * Shutdown server and close all connections
     */
    private static void shutdownServer() {
        // Close all client connections
        for (ClientHandler handler : clientHandlers) {
            try {
                handler.close();
            } catch (Exception e) {
                // Ignore errors during shutdown
            }
        }
        clientHandlers.clear();
        userClientMap.clear();
    }

    /**
     * Client Handler Inner Class
     */
    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int userId = -1;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    processMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        /**
         * Process incoming messages
         */
        private void processMessage(String message) {
            String[] parts = message.split("\\|", 3);
            if (parts.length < 2) return;

            String command = parts[0];

            switch (command) {
                case "REGISTER":
                    if (parts.length >= 2) {
                        userId = Integer.parseInt(parts[1]);
                        HospitalServer.registerUser(userId, this);
                        sendMessage("REGISTERED|SUCCESS");
                    }
                    break;

                case "MESSAGE":
                    if (parts.length >= 3) {
                        String[] msgParts = parts[1].split(",");
                        int receiverId = Integer.parseInt(msgParts[0]);
                        String msgContent = parts[2];
                        HospitalServer.sendMessageToUser(receiverId, "MESSAGE|" + userId + "|" + msgContent);
                        sendMessage("MESSAGE_SENT|SUCCESS");
                    }
                    break;

                case "FILE":
                    if (parts.length >= 3) {
                        String[] fileParts = parts[1].split(",");
                        int receiverId = Integer.parseInt(fileParts[0]);
                        String fileName = fileParts[1];
                        String fileData = parts[2];
                        HospitalServer.sendMessageToUser(receiverId,
                            "FILE|" + userId + "," + fileName + "|" + fileData);
                        sendMessage("FILE_SENT|SUCCESS");
                    }
                    break;

                case "PING":
                    sendMessage("PONG");
                    break;

                default:
                    sendMessage("ERROR|Unknown command");
            }
        }

        /**
         * Send message to this client
         */
        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        /**
         * Get user ID
         */
        public int getUserId() {
            return userId;
        }

        /**
         * Cleanup resources
         */
        private void cleanup() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
                HospitalServer.removeClient(this);
                System.out.println("Client disconnected" + (userId != -1 ? " (User ID: " + userId + ")" : ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Force close this client handler
         */
        public void close() {
            cleanup();
        }
    }
}
