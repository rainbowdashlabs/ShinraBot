/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.commands;

import de.chojo.jdautil.pagination.bag.ListPageBag;
import de.chojo.shinra.configuration.elements.messages.TimedMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TimedMessagePage extends ListPageBag<TimedMessage> {

    public TimedMessagePage(List<TimedMessage> messages) {
        super(messages);
    }

    @Override
    public CompletableFuture<MessageEmbed> buildPage() {
        return CompletableFuture.supplyAsync(() -> new EmbedBuilder()
                .setTitle("#" + current())
                .setDescription(currentElement().message())
                .addField("Duration", currentElement().prettyDelay(), true)
                .build());
    }
}
