/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.worker;

import de.chojo.shinra.configuration.Configuration;
import de.chojo.shinra.configuration.elements.messages.TimedMessage;
import de.chojo.shinra.configuration.elements.messages.TimedRoleMessage;
import de.chojo.shinra.data.EventData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventWorker implements Runnable {
    private final EventData data;
    private final ShardManager shardManager;
    private final Configuration configuration;

    public EventWorker(EventData data, ShardManager shardManager, Configuration configuration) {
        this.data = data;
        this.shardManager = shardManager;
        this.configuration = configuration;
    }

    public static EventWorker create(EventData data, ShardManager shardManager, Configuration configuration, ScheduledExecutorService executorService) {
        var worker = new EventWorker(data, shardManager, configuration);
        executorService.scheduleAtFixedRate(worker, 0, 1, TimeUnit.MINUTES);
        return worker;
    }

    @Override
    public void run() {
        data.getExpiredEvents().thenAccept(events -> {
            for (var event : events) {
                var timedMessage = configuration.autoMessages().byId(event.eventId());
                if (timedMessage.isEmpty()) continue;
                sendEvent(event.userId(), timedMessage.get());
                data.deleteEvent(event);
            }
        });
    }

    public void sendEvent(long userId, TimedMessage message) {
        shardManager.retrieveUserById(userId).queue(user -> {
            if (!(message instanceof TimedRoleMessage roleMessage)) {
                sendMessage(user, message);
                return;
            }
            var mutual = shardManager.getMutualGuilds(user);
            if (mutual.isEmpty()) return;
            mutual.get(0).retrieveMember(user).queue(mem -> {
                if (mem.getRoles().stream().anyMatch(r -> roleMessage.roleId() == r.getIdLong())) {
                    sendMessage(user, message);
                }
            });
        }, err -> ErrorResponseException.ignore(ErrorResponse.UNKNOWN_USER));
    }

    private void sendMessage(User user, TimedMessage message) {
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage(message.message())
                    .queue(RestAction.getDefaultSuccess(),
                            err -> ErrorResponseException.ignore(ErrorResponse.CANNOT_SEND_TO_USER));
        });
    }
}
