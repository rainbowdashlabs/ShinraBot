/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.configuration.elements;

import java.util.ArrayList;
import java.util.List;

public class General {
    private String token = "";
    private List<Long> botOwner = new ArrayList<>();

    public String token() {
        return token;
    }

    public List<Long> botOwner() {
        return botOwner;
    }
}
