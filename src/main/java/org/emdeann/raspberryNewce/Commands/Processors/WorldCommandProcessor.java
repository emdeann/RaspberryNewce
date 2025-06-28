package org.emdeann.raspberryNewce.Commands.Processors;

import org.bukkit.Material;
import org.bukkit.World;
import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;

import java.util.List;

public class WorldCommandProcessor extends CommandProcessor {

    private final World world;

    public WorldCommandProcessor(RaspberryNewcePlugin plugin) {
        super(plugin);
        world = plugin.getServer().getWorlds().getFirst();
    }

    @GameCommand
    public void setBlock(List<String> params) {
        if (!verifyParametersAre(params, CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER)) {
            return;
        }
        setBlock(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)));
    }

    private void setBlock(int x, int y, int z) {
        world.getBlockAt(x, y, z).setType(Material.DIAMOND_BLOCK);
    }
}
