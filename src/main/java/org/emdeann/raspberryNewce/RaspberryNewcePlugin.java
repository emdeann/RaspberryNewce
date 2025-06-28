package org.emdeann.raspberryNewce;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;

public final class RaspberryNewcePlugin extends JavaPlugin {

    private BukkitTask serverListenerTask;
    private ServerListenerThread serverListenerThread;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        try {
            serverListenerThread = new ServerListenerThread(this, 4711);
            serverListenerTask = serverListenerThread.runTaskAsynchronously(this);
        } catch (IOException e) {
            getLogger().severe("Failed to start ListenerThread!");
        }
    }

    @Override
    public void onDisable() {
        serverListenerThread.close();
        serverListenerTask.cancel();
    }

    public void handleConnection(RemoteSession session) {
        session.runTaskTimer(this, 0, 1);
    }
}
