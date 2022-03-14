/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.worker;

import de.chojo.shinra.configuration.Configuration;
import de.chojo.shinra.data.EventData;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventWorker implements Runnable {
    private EventData data;
    private ShardManager shardManager;
    private Configuration configuration;

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
        for (var event : data.getExpiredEvents()) {
            var timedMessage = configuration.autoMessages().byId(event.eventId());
            if (timedMessage.isEmpty()) continue;

            shardManager.retrieveUserById(event.userId()).queue(user -> {
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage(timedMessage.get().message()).queue(RestAction.getDefaultSuccess(), err -> ErrorResponseException.ignore(ErrorResponse.CANNOT_SEND_TO_USER));
                });
            }, err -> ErrorResponseException.ignore(ErrorResponse.UNKNOWN_USER));
            data.deleteEvent(event);
        }
    }
}
