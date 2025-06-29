package org.emdeann.raspberryNewce.Commands.Processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.bukkit.World;
import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.JSONStructure.ServerCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;
import org.emdeann.raspberryNewce.RemoteSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandProcessor {
    private final Map<String, Method> commands;
    protected RaspberryNewcePlugin plugin;
    protected final World world;
    private final ObjectMapper jsonMapper;

    public CommandProcessor(RaspberryNewcePlugin plugin) {
        this.plugin = plugin;
        commands = new HashMap<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(GameCommand.class)) {
                commands.put(method.getName(), method);
            }
        }
        this.world = plugin.getServer().getWorlds().getFirst();
        jsonMapper = new ObjectMapper();
    }

    public void runCommand(ServerCommand command, RemoteSession session) {
        try {
            Object ret = commands.get(command.command).invoke(this, command.args);
            session.send(jsonMapper.writeValueAsString(ret));
        } catch (IllegalAccessException | InvocationTargetException | JsonProcessingException ignored) {

        }
    }

    protected boolean verifyParametersAre(List<String> params, CommandParameterTypes... types) {
        if (params.size() != types.length) {
            return false;
        }
        for (int i = 0; i < params.size(); i++) {
            try {
                switch (types[i]) {
                    case INTEGER -> Integer.parseInt(params.get(i));
                    case DOUBLE -> Double.parseDouble(params.get(i));
                    case MATERIAL -> Material.valueOf(params.get(i).toUpperCase());
                    case BOOLEAN -> {
                        if (!params.get(i).equalsIgnoreCase("true") && !params.get(i).equalsIgnoreCase("false"))
                            return false;
                    }
                    case STRING -> {}
                    default -> {
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
