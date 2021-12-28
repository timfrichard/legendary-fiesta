package com.tim.example.spring.batch.items;

public class Constants {

    /**
     * Name of the job
     */
    public static final String JOB_NAME = "jobFileUploadProcessing";

    public static final String PARAMETERS_JOB_HEADER_ID = "jobHeaderId";

    public static final String PARAMETERS_JOB_START_VALUE = "jobStartValue";

    public static final String PARAMETERS_TAS_BETC_FILE_NAME = "tastbetFile";

    public static final String STEP_FILE_UPLOAD = "stepFileUpload";

    private Constants() {
        // no op
    }
}
