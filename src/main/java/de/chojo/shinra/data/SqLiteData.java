/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.shinra.data;

import org.slf4j.Logger;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.slf4j.LoggerFactory.getLogger;

public class SqLiteData {
    private static final Logger log = getLogger(SqLiteData.class);

    public static DataSource createSqLiteDataSource(Path path) {
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            log.info("Found database");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to init Database", e);
        }
        var sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:" + path.toString());

        return sqLiteDataSource;
    }
}
