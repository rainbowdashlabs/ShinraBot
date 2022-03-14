/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.configuration.elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.chojo.shinra.configuration.elements.messages.TimedMessage;
import de.chojo.shinra.configuration.elements.messages.TimedRoleMessage;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AutoMessages {
    int currentId = 0;

    private List<TimedRoleMessage> roleMessages = new ArrayList<>();
    private List<TimedMessage> timedMessages = new ArrayList<>();

    public AutoMessages() {
    }

    @JsonCreator
    public AutoMessages(@JsonProperty("currentId") int currentId, @JsonProperty("roleMessages") List<TimedRoleMessage> roleMessages, @JsonProperty("timedMessages") List<TimedMessage> timedMessages) {
        this.currentId = currentId;
        this.roleMessages = roleMessages;
        this.timedMessages = timedMessages;
    }

    public void registerRoleMessage(TimedRoleMessage message) {
        message.id(currentId++);
        roleMessages.add(message);
    }

    public void registerTimedMessage(TimedMessage message) {
        message.id(currentId++);
        timedMessages.add(message);
    }

    public List<TimedMessage> timedMessages() {
        return timedMessages;
    }

    public List<TimedRoleMessage> roleMessages(Role role) {
        return roleMessages.stream().filter(roleMessage -> roleMessage.roleId() == role.getIdLong()).collect(Collectors.toList());
    }

    public Optional<TimedMessage> byId(int eventId) {
        return timedMessageById(eventId).or(() -> roleMessageById(eventId));
    }

    public Optional<TimedRoleMessage> roleMessageById(int eventId) {
        return roleMessages.stream().filter(event -> event.id() == eventId).findFirst();
    }

    public Optional<TimedMessage> timedMessageById(int eventId) {
        return timedMessages.stream().filter(event -> event.id() == eventId).findFirst();
    }

    public List<TimedRoleMessage> roleMessages() {
        return roleMessages;
    }
}
