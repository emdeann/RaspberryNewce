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
        if (!verifyParametersAre(params,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.MATERIAL)) {
            return;
        }
        setBlock(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)), Material.valueOf(params.get(3).toUpperCase()));
    }

    private void setBlock(int x, int y, int z, Material material) {
        world.getBlockAt(x, y, z).setType(material);
    }

    @GameCommand
    public void setBlocks(List<String> params) {
        if (!verifyParametersAre(params,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.INTEGER,
                CommandParameterTypes.MATERIAL)) {
            return;
        }
        setBlocks(
                Integer.parseInt(params.get(0)),
                Integer.parseInt(params.get(1)),
                Integer.parseInt(params.get(2)),
                Integer.parseInt(params.get(3)),
                Integer.parseInt(params.get(4)),
                Integer.parseInt(params.get(5)),
                Material.valueOf(params.get(6).toUpperCase())
        );
    }

    private void setBlocks(int x1, int y1, int z1, int x2, int y2, int z2, Material material) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    world.getBlockAt(x, y, z).setType(material);
                }
            }
        }
    }
}
