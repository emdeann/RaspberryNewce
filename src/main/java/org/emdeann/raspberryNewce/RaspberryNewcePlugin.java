package org.emdeann.raspberryNewce;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class RaspberryNewcePlugin extends JavaPlugin {

    private ServerListenerThread serverListenerThread;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        try {
            serverListenerThread = new ServerListenerThread(this, new InetSocketAddress(4711));
            new Thread(serverListenerThread).start();
        } catch (IOException e) {
            getLogger().severe("Failed to start ListenerThread!");
        }
        this.getServer().broadcast(Component.text("hello!"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void handleConnection(RemoteSession session) {
        session.runTaskTimer(this, 0, 1);
    }
}
