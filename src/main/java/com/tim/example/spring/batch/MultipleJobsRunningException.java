package com.tim.example.spring.batch;

public class MultipleJobsRunningException extends Exception {

    private String message;

    /**
     * @param message
     */
    public MultipleJobsRunningException(final String message) {
        super();
        this.message = message;
    }

    /**
     * @return the message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

}
