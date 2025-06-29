package org.emdeann.raspberryNewce.Commands.Processors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;

import java.util.ArrayList;
import java.util.List;

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
        applyToCuboid(x1, y1, z1, x2, y2, z2, (b) -> b.setType(material));
    }

    @GameCommand(paramList = {
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.MATERIAL,
            CommandParameterTypes.MATERIAL
    })
    public void replaceBlocks(int x1, int y1, int z1, int x2, int y2, int z2, Material from, Material to) {
        applyToCuboid(x1, y1, z1, x2, y2, z2, (b) -> {
            if (b.getType() == from) {
                b.setType(to);
            }
        });
    }

    @GameCommand(paramList = {
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
            CommandParameterTypes.INTEGER,
    })
    public List<Material> getBlocks(int x1, int y1, int z1, int x2, int y2, int z2) {
        List<Material> blocks = new ArrayList<>();
        applyToCuboid(x1, y1, z1, x2, y2, z2, (b) -> blocks.add(b.getType()));
        return blocks;
    }

    @GameCommand(paramList = {CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER, CommandParameterTypes.INTEGER})
    public Material getBlock(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType();
    }

    private void applyToCuboid(int x1, int y1, int z1, int x2, int y2, int z2, BlockOperator operator) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    operator.op(world.getBlockAt(x, y, z));
                }
            }
        }
    }
}
