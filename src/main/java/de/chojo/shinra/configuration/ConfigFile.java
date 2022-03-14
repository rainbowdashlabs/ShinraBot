/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.configuration;

import de.chojo.shinra.configuration.elements.AutoMessages;
import de.chojo.shinra.configuration.elements.General;

@SuppressWarnings("FieldMayBeFinal")
public class ConfigFile {
    private General general = new General();
    private AutoMessages autoMessages = new AutoMessages();

    public General general() {
        return general;
    }

    public AutoMessages autoMessages() {
        return autoMessages;
    }
}
