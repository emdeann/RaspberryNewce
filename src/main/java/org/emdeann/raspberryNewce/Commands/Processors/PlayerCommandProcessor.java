package org.emdeann.raspberryNewce.Commands.Processors;

import org.bukkit.Location;
import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;

public class PlayerCommandProcessor extends CommandProcessor {

    public PlayerCommandProcessor(RaspberryNewcePlugin plugin) {
        super(plugin);
    }

    @GameCommand(paramList = {CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER})
    public void setTilePos(int x, int y, int z) {
        world.getPlayers().getFirst().teleport(new Location(world, x, y, z));
    }
}
