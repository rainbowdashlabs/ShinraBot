/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.configuration.elements.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.Member;

import java.time.Duration;
import java.util.StringJoiner;

public class TimedMessage {
    private int id = -1;
    private Duration delay;
    private String message;

    @JsonCreator
    public TimedMessage(@JsonProperty("delay") Duration delay, @JsonProperty("message") String message) {
        this.delay = delay;
        this.message = message;
    }

    public int id() {
        return id;
    }

    public Duration delay() {
        return delay;
    }

    public String message() {
        return message;
    }

    public void id(int id) {
        this.id = id;
    }

    public boolean isApplicable(Member member){
        return true;
    }

    public void message(String message) {
        this.message = message;
    }

    public String prettyDelay() {
        var builder = new StringJoiner(" ");

        if (delay.toDays() > 0) {
            builder.add(delay.toDaysPart() + " Days");
        }

        if (delay.toHours() > 0) {
            builder.add(delay.toHoursPart() + " Hours");
        }

        builder.add(delay.toMinutesPart() + " Minutes");
        return builder.toString();
    }

}
