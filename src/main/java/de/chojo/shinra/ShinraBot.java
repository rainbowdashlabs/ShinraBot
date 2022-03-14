/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra;

import de.chojo.jdautil.command.dispatching.CommandHub;
import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.localization.util.Language;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.shinra.commands.RoleMessage;
import de.chojo.shinra.commands.TimedMessage;
import de.chojo.shinra.configuration.Configuration;
import de.chojo.shinra.data.EventData;
import de.chojo.shinra.data.SqLiteData;
import de.chojo.shinra.listener.StateListener;
import de.chojo.shinra.worker.EventWorker;
import de.chojo.sqlutil.logging.LoggerAdapter;
import de.chojo.sqlutil.updater.SqlType;
import de.chojo.sqlutil.updater.SqlUpdater;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

public class ShinraBot {
    private static final ShinraBot instance;
    private static final Logger log = getLogger(ShinraBot.class);
    private Configuration configuration;
    private ShardManager shardManager;

    static {
        instance = new ShinraBot();
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public static void main(String[] args) {
        try {
            instance.start();
        } catch (Exception e) {
            log.error("Bot startup failed", e);
        }
    }

    public void start() throws LoginException, IOException, SQLException {
        var dataSource = SqLiteData.createSqLiteDataSource(Path.of("data.db"));

        configuration = Configuration.create();

        SqlUpdater.builder(dataSource, SqlType.SQLITE)
                .withLogger(LoggerAdapter.wrap(log))
                .execute();

        var eventData = new EventData(dataSource);

        shardManager = DefaultShardManagerBuilder.createDefault(configuration.general().token())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .enableCache(CacheFlag.MEMBER_OVERRIDES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
        var evenWorker = EventWorker.create(eventData, shardManager, configuration, Executors.newSingleThreadScheduledExecutor());

        shardManager.addEventListener(new StateListener(configuration, eventData, evenWorker));

        for (var guild : shardManager.getGuilds()) {
            guild.loadMembers();
        }

        var pageService = PageService.builder(shardManager)
                .build();


        var localizer = Localizer.builder(Language.ENGLISH).build();

        CommandHub.builder(shardManager)
                .withPermissionCheck((event, simpleCommand) -> false)
                .useGuildCommands()
                .withCommands(new RoleMessage(pageService, configuration), new TimedMessage(pageService, configuration))
                .withConversationSystem()
                .withLocalizer(localizer)
                .withManagerRole(guild -> configuration.general().botOwnerRoles())
                .build();
    }
}
