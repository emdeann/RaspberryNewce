package org.emdeann.raspberryNewce;

import java.io.*;
import java.net.*;

public class ServerListenerThread implements Runnable {

    public ServerSocket serverSocket;

    public SocketAddress bindAddress;

    public boolean running = true;

    private final RaspberryNewcePlugin plugin;

    public ServerListenerThread(RaspberryNewcePlugin plugin, SocketAddress bindAddress) throws IOException {
        this.plugin = plugin;
        this.bindAddress = bindAddress;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(bindAddress);
    }

    public void run() {
        while (running) {
            try {
                Socket newConnection = serverSocket.accept();
                if (!running) return;

                plugin.handleConnection(new RemoteSession(plugin, newConnection, 9000));
            } catch (IOException e) {
                // if the server thread is still running raise an error
                if (running) {
                    plugin.getLogger().warning("Error creating new connection");
                    plugin.getLogger().info(e.getMessage());
                }
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            plugin.getLogger().warning("Error closing server socket");
            plugin.getLogger().info(e.getMessage());
        }
    }
}
