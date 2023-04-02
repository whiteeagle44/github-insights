package io.eagle44.githubinsights.errors;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class ErrorResponse {
    private Date timeStamp;
    private int status;
    private String error;

    public ErrorResponse(HttpStatus httpStatus, String error) {
        this.timeStamp = new Date();
        this.status = httpStatus.value();
        this.error = error;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
