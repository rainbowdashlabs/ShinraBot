CREATE TABLE events
(
    id         INTEGER
        CONSTRAINT events_pk
            PRIMARY KEY AUTOINCREMENT,
    message_id INTEGER,
    send_after INTEGER,
    user_id    INTEGER
);
