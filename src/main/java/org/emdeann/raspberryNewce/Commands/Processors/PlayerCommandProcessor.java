package org.emdeann.raspberryNewce.Commands.Processors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;

import java.util.Arrays;
import java.util.List;

public class PlayerCommandProcessor extends CommandProcessor {


    public PlayerCommandProcessor(RaspberryNewcePlugin plugin) {
        super(plugin);
    }

    @GameCommand(paramList = {
            CommandParameterTypes.PLAYER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER
    })
    public void setTilePos(Player player, int x, int y, int z) {
        player.teleport(new Location(player.getWorld(), x, y, z));
    }

    @GameCommand(paramList = {CommandParameterTypes.PLAYER})
    public List<Integer> getTilePos(Player player) {
        Location loc = player.getLocation();
        return Arrays.asList(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
