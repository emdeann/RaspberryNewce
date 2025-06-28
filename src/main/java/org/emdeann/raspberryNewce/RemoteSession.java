package org.emdeann.raspberryNewce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.scheduler.BukkitRunnable;
import org.emdeann.raspberryNewce.Commands.ServerCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

public class RemoteSession extends BukkitRunnable {

    private final RaspberryNewcePlugin plugin;

    private final Socket socket;

    private BufferedReader in;

    ObjectMapper jsonMapper;

    private BufferedWriter out;

    private Thread inThread;

    private Thread outThread;

    private final ArrayDeque<ServerCommand> inQueue = new ArrayDeque<>();

    private final ArrayDeque<String> outQueue = new ArrayDeque<>();

    public boolean running = true;

    public boolean pendingRemoval = false;


    private final int maxCommandsPerTick;


    public RemoteSession(RaspberryNewcePlugin plugin, Socket socket, int maxCommandsPerTick) throws IOException {
        this.socket = socket;
        this.plugin = plugin;
        this.maxCommandsPerTick = maxCommandsPerTick;
        init();
    }

    public void init() throws IOException {
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        socket.setTrafficClass(0x10);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.jsonMapper = new ObjectMapper();
        startThreads();
        plugin.getLogger().info("Opened connection to" + socket.getRemoteSocketAddress() + ".");
    }

    protected void startThreads() {
        inThread = new Thread(new InputThread());
        inThread.start();
        outThread = new Thread(new OutputThread());
        outThread.start();
    }

    @Override
    public void run() {
        if (pendingRemoval) {
            this.cancel();
            return;
        }
        int processedCount = 0;
        ServerCommand command;
        while ((command = inQueue.poll()) != null) {
            processedCount++;
            if (processedCount >= maxCommandsPerTick) {
                plugin.getLogger().warning("Over " + maxCommandsPerTick +
                        " commands were queued - deferring " + inQueue.size() + " to next tick");
                break;
            }
        }

        if (!running && inQueue.isEmpty()) {
            pendingRemoval = true;
        }
    }

    public void send(String a) {
        if (pendingRemoval) return;
        synchronized(outQueue) {
            outQueue.add(a);
        }
    }

    public void close() {
        running = false;
        pendingRemoval = true;

        //wait for threads to stop
        try {
            inThread.join(2000);
            outThread.join(2000);
        }
        catch (InterruptedException e) {
            plugin.getLogger().warning("Failed to stop in/out thread");
            plugin.getLogger().info(e.getMessage());
        }

        try {
            socket.close();
        } catch (IOException e) {
            plugin.getLogger().warning("Error while closing socket thread");
            plugin.getLogger().info(e.getMessage());
        }
        plugin.getLogger().info("Closed connection to" + socket.getRemoteSocketAddress() + ".");
    }

    public void kick(String reason) {
        try {
            out.write(reason);
            out.flush();
        } catch (IOException e) {
            plugin.getLogger().warning("Error while kicking for " + reason);
            plugin.getLogger().info(e.getMessage());
        }
        close();
    }

    /** socket listening thread */
    private class InputThread implements Runnable {
        public void run() {
            plugin.getLogger().info("Starting input thread");
            while (running) {
                try {
                    JsonNode root = jsonMapper.readTree(in);
                    if (root == null || root.isNull()) {
                        running = false;
                        break;
                    }
                    ServerCommand command = jsonMapper.readValue(root.binaryValue(), ServerCommand.class);
                    inQueue.add(command);
                } catch (JsonProcessingException e) {
                    plugin.getLogger().warning("A malformed message was sent to the server");
                } catch (IOException e) {
                    // if its running raise an error
                    if (running) {
                        plugin.getLogger().warning("Error while reading input stream");
                        plugin.getLogger().info(e.getMessage());
                        running = false;
                    }
                }
            }
            //close in buffer
            try {
                in.close();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to close in buffer");
                plugin.getLogger().info(e.getMessage());
            }
        }
    }

    private class OutputThread implements Runnable {
        public void run() {
            plugin.getLogger().info("Starting output thread!");
            while (running) {
                try {
                    String line;
                    while((line = outQueue.poll()) != null) {
                        out.write(line);
                        out.write('\n');
                    }
                    out.flush();
                    Thread.yield();
                    Thread.sleep(1L);
                } catch (IOException | InterruptedException e) {
                    // if its running raise an error
                    if (running) {
                        plugin.getLogger().warning("Error while sending output");
                        plugin.getLogger().info(e.getMessage());
                        running = false;
                    }
                }
            }
            //close out buffer
            try {
                out.close();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to close out buffer");
                plugin.getLogger().info(e.getMessage());
            }
        }
    }
}
