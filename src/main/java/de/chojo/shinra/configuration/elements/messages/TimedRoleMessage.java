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

public class TimedRoleMessage extends TimedMessage {
    long roleId;

    @JsonCreator
    public TimedRoleMessage(@JsonProperty("delay") Duration delay, @JsonProperty("message") String message, @JsonProperty("roleId") long roleId) {
        super(delay, message);
        this.roleId = roleId;
    }

    public long roleId() {
        return roleId;
    }

    public void roleId(long roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean isApplicable(Member member) {
        return member.getRoles().stream().anyMatch(r -> r.getIdLong() == roleId);
    }
}
