/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.listener;

import de.chojo.shinra.configuration.Configuration;
import de.chojo.shinra.configuration.elements.messages.TimedRoleMessage;
import de.chojo.shinra.data.EventData;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class StateListener extends ListenerAdapter {
    Configuration configuration;
    EventData eventData;

    public StateListener(Configuration configuration, EventData eventData) {
        this.configuration = configuration;
        this.eventData = eventData;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        for (var message : configuration.autoMessages().timedMessages()) {
            eventData.addEvent(message, event.getMember());
        }
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        for (Role role : event.getRoles()) {
            for (TimedRoleMessage message : configuration.autoMessages().roleMessages(role)) {
                eventData.addEvent(message, event.getMember());
            }
        }
    }
}
