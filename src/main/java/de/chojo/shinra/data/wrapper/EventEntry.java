/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.data.wrapper;

public class EventEntry {
    int id;
    int eventId;
    long userId;

    public EventEntry(int id, int eventId, long userId) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
    }

    public int id() {
        return id;
    }

    public int eventId() {
        return eventId;
    }

    public long userId() {
        return userId;
    }
}
