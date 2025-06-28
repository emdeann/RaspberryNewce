package org.emdeann.raspberryNewce.Commands;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ServerCommand {
    @JsonProperty("domain")
    private String domain;

    @JsonProperty("command")
    private String command;

    @JsonProperty("args")
    private List<String> args;
}
