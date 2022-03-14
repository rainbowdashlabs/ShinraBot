/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.listener;

import de.chojo.shinra.configuration.Configuration;
import de.chojo.shinra.configuration.elements.messages.TimedMessage;
import de.chojo.shinra.data.EventData;
import de.chojo.shinra.worker.EventWorker;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class StateListener extends ListenerAdapter {
    private final Configuration configuration;
    private final EventData eventData;
    private final EventWorker eventWorker;

    public StateListener(Configuration configuration, EventData eventData, EventWorker eventWorker) {
        this.configuration = configuration;
        this.eventData = eventData;
        this.eventWorker = eventWorker;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        for (var message : configuration.autoMessages().timedMessages()) {
            registerEvent(message, event.getMember());
        }
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        for (var role : event.getRoles()) {
            for (var message : configuration.autoMessages().roleMessages(role)) {
                registerEvent(message, event.getMember());
            }
        }
    }

    private void registerEvent(TimedMessage message, Member member) {
        if (message.delay().isZero()) {
            eventWorker.sendEvent(member.getUser().getIdLong(), message);
        } else {
            eventData.addEvent(message, member);
        }
    }
}
