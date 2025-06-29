package org.emdeann.raspberryNewce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.scheduler.BukkitRunnable;
import org.emdeann.raspberryNewce.JSONStructure.ServerCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemoteSession extends BukkitRunnable {

    private final RaspberryNewcePlugin plugin;
    private final Socket socket;
    private BufferedReader in;
    ObjectMapper jsonMapper;
    private BufferedWriter out;
    private Thread inThread;
    private final ArrayDeque<ServerCommand> inQueue;
    private final ExecutorService outputPool;
    public boolean running = true;
    public boolean pendingRemoval = false;


    private final int maxCommandsPerTick;


    public RemoteSession(RaspberryNewcePlugin plugin, Socket socket, int maxCommandsPerTick) throws IOException {
        this.socket = socket;
        this.plugin = plugin;
        this.maxCommandsPerTick = maxCommandsPerTick;
        this.inQueue = new ArrayDeque<>();
        this.outputPool = Executors.newFixedThreadPool(10);
        init();
    }

    public void init() throws IOException {
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        socket.setTrafficClass(0x10);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.jsonMapper = new ObjectMapper();

        inThread = new Thread(new InputThread());
        inThread.start();

        plugin.getLogger().info("Opened connection to" + socket.getRemoteSocketAddress() + ".");
    }

    @Override
    public void run() {
        if (pendingRemoval) {
            this.close();
            return;
        }
        int processedCount = 0;
        ServerCommand command;
        while ((command = inQueue.poll()) != null) {
            plugin.handleCommand(command, this);
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
        outputPool.submit(new OutputThread(a));
    }

    public void close() {
        running = false;
        pendingRemoval = true;

        //wait for threads to stop
        try {
            outputPool.shutdown();
            if (!outputPool.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new InterruptedException("Output pool failed to close in time");
            }
            in.close();
            inThread.join(500);
        }
        catch (InterruptedException | IOException e) {
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
        this.cancel();
    }

    /** socket listening thread */
    private class InputThread implements Runnable {
        public void run() {
            plugin.getLogger().info("Starting input thread");
            while (running) {
                try {
                    MappingIterator<ServerCommand> generator = jsonMapper.readerFor(ServerCommand.class).readValues(in);
                    while (generator.hasNextValue()) {
                        ServerCommand command = generator.nextValue();
                        plugin.getLogger().info(command.domain);
                        inQueue.add(command);
                    }
                } catch (JsonProcessingException | IllegalArgumentException e) {
                    plugin.getLogger().warning("A malformed message was sent to the server");
                } catch (IOException e) {
                    if (running) {
                        plugin.getLogger().info("Input stream closed");
                        running = false;
                    }
                }
            }
        }
    }

    private class OutputThread implements Runnable {
        private final String message;

        public OutputThread(String message) {
            this.message = message;
        }

        public void run() {
            plugin.getLogger().info("Starting output thread!");
            try {
                plugin.getLogger().info(message);
                out.write(message);
                out.write('\n');
                out.flush();
            } catch (IOException e) {
                plugin.getLogger().warning("Error while sending output");
                plugin.getLogger().info(e.getMessage());
                running = false;
            }
        }
    }
}
