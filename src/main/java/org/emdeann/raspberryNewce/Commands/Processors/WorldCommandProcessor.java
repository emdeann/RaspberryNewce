package org.emdeann.raspberryNewce.Commands.Processors;

import org.bukkit.Material;
import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;

public class WorldCommandProcessor extends CommandProcessor {

    public WorldCommandProcessor(RaspberryNewcePlugin plugin) {
        super(plugin);
    }

    @GameCommand(paramList = {CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER, CommandParameterTypes.MATERIAL})
    public void setBlock(int x, int y, int z, Material material) {
        world.getBlockAt(x, y, z).setType(material);
    }

    @GameCommand(paramList = {
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.MATERIAL
    })
    public void setBlocks(int x1, int y1, int z1, int x2, int y2, int z2, Material material) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    world.getBlockAt(x, y, z).setType(material);
                }
            }
        }
    }

    @GameCommand(paramList = {CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER})
    public Material getBlock(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType();
    }
}
