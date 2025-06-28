package org.emdeann.raspberryNewce;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerListenerThread extends BukkitRunnable {

    private final ServerSocket serverSocket;
    private final RaspberryNewcePlugin plugin;

    public ServerListenerThread(RaspberryNewcePlugin plugin, int port) throws IOException {
        this.plugin = plugin;
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.setReuseAddress(true);
    }

    @Override
    public void run() {
        while (!this.isCancelled()) {
            try {
                Socket newConnection = serverSocket.accept();
                if (this.isCancelled()) return;
                plugin.handleConnection(new RemoteSession(plugin, newConnection, 9000));
            } catch (SocketException ignored) {
                // Socket closed
                break;
            }
            catch (IOException e) {
                plugin.getLogger().warning("Error creating new connection");
                plugin.getLogger().info(e.getMessage());
            }
        }
    }

    public void close() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            plugin.getLogger().warning("Error closing server socket");
            plugin.getLogger().info(e.getMessage());
        }
    }
}
