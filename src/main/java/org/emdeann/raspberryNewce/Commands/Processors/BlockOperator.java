package org.emdeann.raspberryNewce.Commands.Processors;

import org.bukkit.block.Block;

@FunctionalInterface
public interface BlockOperator {
    void op(Block b);
}
