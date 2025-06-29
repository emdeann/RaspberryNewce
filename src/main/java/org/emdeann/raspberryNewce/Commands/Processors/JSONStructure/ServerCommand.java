package org.emdeann.raspberryNewce.Commands.Processors.JSONStructure;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ServerCommand {
    @JsonProperty("domain")
    public String domain;

    @JsonProperty("command")
    public String command;

    @JsonProperty("args")
    public List<String> args;
}
