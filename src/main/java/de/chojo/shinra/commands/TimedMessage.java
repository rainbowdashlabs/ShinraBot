/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.commands;

import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.conversation.builder.ConversationBuilder;
import de.chojo.jdautil.conversation.elements.Result;
import de.chojo.jdautil.conversation.elements.Step;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.wrapper.SlashCommandContext;
import de.chojo.shinra.configuration.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TimedMessage extends SimpleCommand {
    private final PageService pageService;
    private final Configuration configuration;

    public TimedMessage(PageService pageService, Configuration configuration) {
        super("timedmessage", null, "Add timed messages", subCommandBuilder()
                        .add("add", "Add a timed message", argsBuilder()
                                .add(OptionType.INTEGER, "minutes", "minutes")
                                .add(OptionType.INTEGER, "hours", "hours")
                                .add(OptionType.INTEGER, "days", "days")
                                .build())
                        .add("remove", "Remove a timed message", argsBuilder()
                                .add(OptionType.INTEGER, "id", "id", a -> a.asRequired()).build())
                        .add("edit", "Edit a timed message", argsBuilder()
                                .add(OptionType.INTEGER, "id", "id", a -> a.asRequired()).build())
                        .add("list", "List a timed message")
                        .add("show", "Show a timed message", argsBuilder()
                                .add(OptionType.INTEGER, "id", "id").build())
                        .build(),
                Permission.UNKNOWN);
        this.pageService = pageService;
        this.configuration = configuration;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, SlashCommandContext context) {
        var label = event.getSubcommandName();
        if ("add".equals(label)) {
            context.conversationService().startDialog(event.getUser(), event.getTextChannel(),
                    ConversationBuilder.builder(Step.message("Please enter the text", conversationContext -> {
                        var duration = Duration.ofDays(event.getOption("days", 0, OptionMapping::getAsInt));
                        duration = duration.plus(event.getOption("hours", 0, OptionMapping::getAsInt), ChronoUnit.HOURS);
                        duration = duration.plus(event.getOption("minutes", 0, OptionMapping::getAsInt), ChronoUnit.MINUTES);
                        var message = new de.chojo.shinra.configuration.elements.messages.TimedMessage(duration, conversationContext.getContentRaw());
                        configuration.autoMessages().registerTimedMessage(message);
                        conversationContext.getContentRaw();
                        configuration.saveConfig();
                        return Result.finish();
                    }).build()).build());
            return;
        }

        if ("remove".equals(label)) {
            var message = configuration.autoMessages().timedMessageById(event.getOption("id", 0, OptionMapping::getAsInt));
            if (message.isEmpty()) {
                event.reply("Unkown message id").setEphemeral(true).queue();
                return;
            }
            configuration.autoMessages().timedMessages().removeIf(mes -> mes.id() == message.get().id());
            event.reply("Removed.").queue();
            configuration.saveConfig();
            return;
        }

        if ("edit".equals(label)) {
            var message = configuration.autoMessages().timedMessageById(event.getOption("id", 0, OptionMapping::getAsInt));
            if (message.isEmpty()) {
                event.reply("Unkown message id").setEphemeral(true).queue();
                return;
            }

            context.conversationService().startDialog(event.getUser(), event.getTextChannel(),
                    ConversationBuilder.builder(Step.message("Please enter the text", conversationContext -> {
                        message.get().message(conversationContext.getContentRaw());
                        configuration.saveConfig();
                        return Result.finish();
                    }).build()).build());
            return;
        }

        if ("list".equals(label)) {
            pageService.registerPage(event, new TimedMessagePage(configuration.autoMessages().timedMessages()));
            return;
        }

        if ("show".equals(label)) {
            var page = new TimedMessagePage(configuration.autoMessages().timedMessages());
            if (event.getOption("id") != null) {
                var message = configuration.autoMessages().timedMessageById(event.getOption("id").getAsInt());
                message.ifPresent(m -> page.current(configuration.autoMessages().timedMessages().indexOf(m)));
            }
            pageService.registerPage(event, page);
        }
    }
}
