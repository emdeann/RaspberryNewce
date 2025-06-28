package org.emdeann.raspberryNewce.Commands.Processors;

import org.emdeann.raspberryNewce.Commands.CommandParameterTypes;
import org.emdeann.raspberryNewce.Commands.GameCommand;
import org.emdeann.raspberryNewce.Commands.ServerCommand;
import org.emdeann.raspberryNewce.RaspberryNewcePlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandProcessor {
    private final Map<String, Method> commands;
    protected RaspberryNewcePlugin plugin;

    public CommandProcessor(RaspberryNewcePlugin plugin) {
        this.plugin = plugin;
        commands = new HashMap<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(GameCommand.class)) {
                commands.put(method.getName(), method);
            }
        }
    }

    public void runCommand(ServerCommand command) {
        try {
            commands.get(command.command).invoke(this, command.args);
        } catch (IllegalAccessException | InvocationTargetException ignored) {

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
