package com.eis.ileadbyexample.Models;

public class ErrorBooleanResponce {
    private boolean error;

    public ErrorBooleanResponce(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
