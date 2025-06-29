package org.emdeann.raspberryNewce.Commands.Processors;

import org.bukkit.Location;
import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;

import java.util.List;

public class PlayerCommandProcessor extends CommandProcessor {

    public PlayerCommandProcessor(RaspberryNewcePlugin plugin) {
        super(plugin);
    }

    @GameCommand
    public void setTilePos(List<String> params) {
        if (!verifyParametersAre(params,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER)) {
            return;
        }
        setTilePos(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)));
    }

    public void setTilePos(int x, int y, int z) {
        world.getPlayers().getFirst().teleport(new Location(world, x, y, z));
    }
}
