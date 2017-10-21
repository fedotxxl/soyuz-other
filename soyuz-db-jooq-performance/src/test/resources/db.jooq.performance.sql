CREATE TABLE sql_log
(
    id CHAR(32) PRIMARY KEY NOT NULL,
    query VARCHAR(4096),
    invocations_count INT DEFAULT 0 NOT NULL,
    total_duration_in_millis BIGINT DEFAULT 0 NOT NULL,
    last_updated_at DATETIME DEFAULT getdate() NOT NULL
);