/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.configuration.elements;

import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class General {
    private String token = "";
    private List<Long> botOwnerRoles = new ArrayList<>();

    public String token() {
        return token;
    }

    public List<Long> botOwnerRoles() {
        return botOwnerRoles;
    }

    public boolean isOwner(@Nullable Member member){
        if(member == null) return false;
        return member.getRoles().stream().anyMatch(role -> botOwnerRoles.contains(role.getIdLong()));
    }
}
