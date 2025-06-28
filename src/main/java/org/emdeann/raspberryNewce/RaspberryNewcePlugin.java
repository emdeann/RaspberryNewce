package org.emdeann.raspberryNewce;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.emdeann.raspberryNewce.Commands.CommandDomain;
import org.emdeann.raspberryNewce.Commands.Processors.CommandProcessor;
import org.emdeann.raspberryNewce.Commands.Processors.WorldCommandProcessor;
import org.emdeann.raspberryNewce.Commands.ServerCommand;

import java.io.IOException;
import java.util.Map;

public final class RaspberryNewcePlugin extends JavaPlugin {

    private BukkitTask serverListenerTask;
    private ServerListenerThread serverListenerThread;
    private Map<CommandDomain, CommandProcessor> commandProcessors;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        try {
            serverListenerThread = new ServerListenerThread(this, 4711);
            serverListenerTask = serverListenerThread.runTaskAsynchronously(this);
        } catch (IOException e) {
            getLogger().severe("Failed to start ListenerThread!");
        }
        commandProcessors = Map.ofEntries(
                Map.entry(CommandDomain.WORLD, new WorldCommandProcessor(this))
        );
    }

    @Override
    public void onDisable() {
        serverListenerThread.close();
        serverListenerTask.cancel();
    }

    public void handleConnection(RemoteSession session) {
        session.runTaskTimer(this, 0, 1);
    }

    public void handleCommand(ServerCommand command) {
        commandProcessors.get(CommandDomain.valueOf(command.domain.toUpperCase())).runCommand(command);
    }
}
