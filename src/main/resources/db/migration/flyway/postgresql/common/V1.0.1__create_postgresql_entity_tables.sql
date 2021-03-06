    SET search_path = spring_batch_demo;
    SET SCHEMA 'spring_batch_demo';

    DROP TABLE IF EXISTS TAS_BETC;
    -- Drop the TASBETC Sequence if it exists
    DROP SEQUENCE IF EXISTS TAS_BETC_SEQ;
    DROP TABLE IF EXISTS FILE_UPLOAD_JOB_HEADER;
    -- Drop the JOBHEADER Sequence if it exists
    DROP SEQUENCE IF EXISTS FILE_UPLOAD_JOB_HEADER_SEQ;

    CREATE TABLE FILE_UPLOAD_JOB_HEADER (
        FILE_UPLOAD_JOB_HEADER_ID BIGINT NOT NULL,
        VERSION_ID BIGINT DEFAULT 0 NOT NULL,
        FK_JOB_EXECUTION_ID BIGINT,
        READ_COUNT BIGINT,
        EXIT_CODE VARCHAR(2500),
        STATUS VARCHAR(10),
        FILE_NAME VARCHAR(250),
        FILE_UPLOAD_DATETIME TIMESTAMP,
        PRIMARY KEY (FILE_UPLOAD_JOB_HEADER_ID)
    );

    GRANT INSERT, SELECT, UPDATE, DELETE
    ON TABLE FILE_UPLOAD_JOB_HEADER
    TO "SPRINGBATCHAPPROLE";

    CREATE TABLE TAS_BETC (
        ID_NUMBER BIGINT NOT NULL,
        VERSION_ID BIGINT DEFAULT 0 NOT NULL,
        CMPNT_TAS_SP VARCHAR(25),
        CMPNT_TAS_ATA VARCHAR(25),
        CMPNT_TAS_AID VARCHAR(25),
        CMPNT_TAS_BPOA VARCHAR(25),
        CMPNT_TAS_EPOA VARCHAR(25),
        CMPNT_TAS_A VARCHAR(25),
        CMPNT_TAS_MAIN VARCHAR(25),
        CMPNT_TAS_SUB VARCHAR(25),
        ADMIN_BUR VARCHAR(250),
        GWA_TAS VARCHAR(35),
        GWA_TAS_NAME VARCHAR(250),
        AGENCY_NAME VARCHAR(250),
        BETC VARCHAR(35),
        BETC_NAME VARCHAR(250),
        EFF_DATE VARCHAR(35),
        SUSPEND_DATE VARCHAR(35),
        CREDIT VARCHAR(1),
        ADJ_BETC VARCHAR(35),
        STAR_TAS VARCHAR(35),
        STAR_DEPT_REG VARCHAR(35),
        STAR_DEPT_XFR VARCHAR(35),
        STAR_MAIN_ACCT VARCHAR(35),
        TXN_TYPE VARCHAR(35),
        ACCT_TYPE VARCHAR(35),
        ACCT_TYPE_DESCRIPTION VARCHAR(250),
        FUND_TYPE VARCHAR(35),
        FUND_TYPE_DESCRIPTION VARCHAR(250),
        PROCESS_DATETIME TIMESTAMP,
        FK_FILE_UPLOAD_JOB_HEADER_ID BIGINT NOT NULL,
        PRIMARY KEY (ID_NUMBER)
    );

    ALTER TABLE TAS_BETC
    ADD CONSTRAINT FK_JOB_HEADER_TAS_BETC_CONSTRAINT
    FOREIGN KEY (FK_FILE_UPLOAD_JOB_HEADER_ID)
    REFERENCES FILE_UPLOAD_JOB_HEADER (FILE_UPLOAD_JOB_HEADER_ID) ON DELETE CASCADE;

    GRANT INSERT, SELECT, UPDATE, DELETE
    ON TABLE TAS_BETC
    TO "SPRINGBATCHAPPROLE";

    CREATE TABLE PROCESSING_ERROR (
        PROCESSING_ERROR_ID BIGINT NOT NULL,
        VERSION_ID BIGINT DEFAULT 0 NOT NULL,
        FK_FILE_UPLOAD_JOB_HEADER_ID BIGINT NOT NULL,
        ERROR_DESCRIPTION VARCHAR(2500),
        STEP_TYPE_ERROR VARCHAR(30),
        PRIMARY KEY (PROCESSING_ERROR_ID)
    );

    ALTER TABLE PROCESSING_ERROR
    ADD CONSTRAINT FK_JOB_HEADER_PROCESSING_ERROR_CONSTRAINT
    FOREIGN KEY (FK_FILE_UPLOAD_JOB_HEADER_ID)
    REFERENCES FILE_UPLOAD_JOB_HEADER (FILE_UPLOAD_JOB_HEADER_ID) ON DELETE CASCADE;

    GRANT INSERT, SELECT, UPDATE, DELETE
    ON TABLE PROCESSING_ERROR
    TO "SPRINGBATCHAPPROLE";

    -- Create the TASBETC sequence
    CREATE SEQUENCE TAS_BETC_SEQ AS BIGINT START 20 INCREMENT 10 MINVALUE 20 OWNED BY TAS_BETC.ID_NUMBER;
    -- Grants TASBETC sequence
    GRANT USAGE
    ON SEQUENCE TAS_BETC_SEQ
    TO "SPRINGBATCHAPPROLE";
    -- Create JOBHEADER Sequence

    CREATE SEQUENCE FILE_UPLOAD_JOB_HEADER_SEQ AS BIGINT START 20 INCREMENT 10 MINVALUE 20 OWNED BY FILE_UPLOAD_JOB_HEADER.FILE_UPLOAD_JOB_HEADER_ID;
    -- Grants FILE_UPLOAD_JOB_HEADER sequence
    GRANT USAGE
    ON SEQUENCE FILE_UPLOAD_JOB_HEADER_SEQ
    TO "SPRINGBATCHAPPROLE";

    -- Create PROCESSING_ERROR Sequence
    CREATE SEQUENCE PROCESSING_ERROR_SEQ AS BIGINT START 20 INCREMENT 10 MINVALUE 20 OWNED BY PROCESSING_ERROR.PROCESSING_ERROR_ID;
    -- Grants PROCESSING_ERROR sequence
    GRANT USAGE
    ON SEQUENCE PROCESSING_ERROR_SEQ
    TO "SPRINGBATCHAPPROLE";