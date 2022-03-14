/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.commands;

import de.chojo.jdautil.pagination.bag.ListPageBag;
import de.chojo.jdautil.util.MentionUtil;
import de.chojo.shinra.configuration.elements.messages.TimedRoleMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoleMessagePage extends ListPageBag<TimedRoleMessage> {

    public RoleMessagePage(List<TimedRoleMessage> messages) {
        super(messages);
    }

    @Override
    public CompletableFuture<MessageEmbed> buildPage() {
        return CompletableFuture.supplyAsync(() -> new EmbedBuilder()
                .setTitle(current() + " | ")
                .setDescription(currentElement().message())
                .addField("Role", MentionUtil.role(currentElement().roleId()), false)
                .addField("Duration", currentElement().prettyDelay(), false)
                .build());
    }
}
