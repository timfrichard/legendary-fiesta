    SET search_path = spring_batch_demo;
    SET SCHEMA 'spring_batch_demo';

    -- Drop table if exists
    DROP TABLE IF EXISTS SHEDLOCK;

    -- Create ShedLock Tables
    CREATE TABLE SHEDLOCK(
        NAME VARCHAR(64) NOT NULL,
        LOCK_UNTIL TIMESTAMP NOT NULL,
        LOCKED_AT TIMESTAMP NOT NULL,
        LOCKED_BY VARCHAR(255) NOT NULL,
        PRIMARY KEY (NAME)
    );

    GRANT INSERT, SELECT, UPDATE, DELETE
    ON TABLE SHEDLOCK
    TO "SPRINGBATCHAPPROLE";