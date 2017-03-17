DROP TABLE IF EXISTS "task";

CREATE TABLE "task" (
  "id"        SERIAL PRIMARY KEY,
  "priority"  SMALLINT                 NOT NULL DEFAULT 10,
  "type"      VARCHAR(64)              NOT NULL,
  "status"    CHAR(1)                  NOT NULL DEFAULT 'n',
  "posted_on" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  "queued_on" TIMESTAMP WITH TIME ZONE          DEFAULT NULL,
  "status_on" TIMESTAMP WITH TIME ZONE          DEFAULT NULL,
  "context"   TEXT
);