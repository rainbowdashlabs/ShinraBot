/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.data;

import de.chojo.shinra.configuration.elements.messages.TimedMessage;
import de.chojo.shinra.data.wrapper.EventEntry;
import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.chojo.sqlutil.wrapper.QueryBuilderConfig;
import net.dv8tion.jda.api.entities.Member;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EventData extends QueryFactoryHolder {
    /**
     * Create a new QueryFactoryholder
     *
     * @param dataSource datasource
     */
    public EventData(DataSource dataSource) {
        super(dataSource, QueryBuilderConfig.builder().build());
    }

    public void addEvent(TimedMessage message, Member member) {
        builder()
                .query("INSERT INTO events(message_id, send_after, user_id) VALUES(?,?,?)")
                .paramsBuilder(stmt -> stmt.setInt(message.id())
                        .setLong(LocalDateTime.now().plus(message.delay().toSeconds(), ChronoUnit.SECONDS).toEpochSecond(ZoneOffset.UTC))
                        .setLong(member.getIdLong()))
                .insert()
                .execute();
    }

    public List<EventEntry> getExpiredEvents() {
        return builder(EventEntry.class)
                .query("SELECT id, message_id, user_id FROM events WHERE send_after < ?")
                .paramsBuilder(paramBuilder -> paramBuilder.setLong(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
                .readRow(r -> new EventEntry(r.getInt("id"), r.getInt("message_id"), r.getLong("user_id")))
                .allSync();
    }

    public void deleteEvent(EventEntry eventEntry) {
        builder()
                .query("DELETE FROM events WHERE id = ?")
                .paramsBuilder(paramBuilder -> paramBuilder.setInt(eventEntry.id()))
                .delete()
                .execute();
    }
}
