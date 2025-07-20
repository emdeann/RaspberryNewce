package org.emdeann.raspberryNewce.Commands.Processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
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
import java.util.logging.Level;

public class CommandProcessor {
    private final Map<String, Method> commands;
    private final Map<String, CommandParameterTypes[]> parameters;
    protected RaspberryNewcePlugin plugin;
    private final ObjectMapper jsonMapper;

    public CommandProcessor(RaspberryNewcePlugin plugin) {
        this.plugin = plugin;
        commands = new HashMap<>();
        parameters = new HashMap<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(GameCommand.class)) {
                commands.put(method.getName(), method);
                parameters.put(method.getName(), method.getAnnotation(GameCommand.class).paramList());
            }
        }
        jsonMapper = new ObjectMapper();
    }

    public void runCommand(ServerCommand command, RemoteSession session) {
        try {
            Method method = commands.get(command.command);
            CommandParameterTypes[] expectedTypes = parameters.get(command.command);
            if (method == null) {
                plugin.getLogger().warning("Unknown command: " + command.command);
                return;
            }

            Object[] parsedParams = validateAndParseParameters(command.args, expectedTypes);
            if (parsedParams == null) {
                plugin.getLogger().log(Level.WARNING, "Invalid parameters for command: " + command.command);
                return;
            }

            Object ret = method.invoke(this, parsedParams);
            if (ret != null) {
                session.send(jsonMapper.writeValueAsString(ret));
            }

        } catch (IllegalAccessException | InvocationTargetException | JsonProcessingException e) {
            plugin.getLogger().log(Level.SEVERE, "Error executing command: " + command.command, e);
        }
    }

    protected Object[] validateAndParseParameters(List<String> params, CommandParameterTypes... types) {
        if (params.size() != types.length) {
            return null;
        }

        Object[] parsedParams = new Object[params.size()];

        for (int i = 0; i < params.size(); i++) {
            try {
                switch (types[i]) {
                    case INTEGER -> parsedParams[i] = Integer.parseInt(params.get(i));
                    case DOUBLE -> parsedParams[i] = Double.parseDouble(params.get(i));
                    case MATERIAL -> parsedParams[i] = Material.valueOf(params.get(i).toUpperCase());
                    case BOOLEAN -> {
                        String boolStr = params.get(i).toLowerCase();
                        if (!boolStr.equals("true") && !boolStr.equals("false")) {
                            return null;
                        }
                        parsedParams[i] = Boolean.parseBoolean(boolStr);
                    }
                    case STRING -> parsedParams[i] = params.get(i);
                    case PLAYER -> {
                        parsedParams[i] = plugin.getServer().getPlayer(params.get(i));
                        if (parsedParams[i] == null) {
                            throw new NullPointerException("Player parameter was null");
                        }
                    }
                    default -> {
                        return null;
                    }
                }
            } catch (NumberFormatException | NullPointerException e) {
                return null;
            }
        }

        return parsedParams;
    }
}
