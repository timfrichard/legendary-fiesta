package com.tim.example.spring.batch.model.entities;

public enum StepTypeError {

    /* NOTE: Using this Enum to give an idea of what part in the process the error occurred. */
    FILEUPLOADREADER("Flat File Reader Error"),
    FILEUPLOADPROCESSOR("Input Object Validation Error"),
    FILEUPLOADWRITER("Data Writer Persist Error");

    private final String stepTypeErrorDescription;

    StepTypeError(final String stepTypeErrorDescription) {
        this.stepTypeErrorDescription = stepTypeErrorDescription;
    }
}
