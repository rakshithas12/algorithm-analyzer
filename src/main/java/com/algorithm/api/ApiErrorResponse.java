package com.algorithm.api;

/** Safe JSON error returned to API clients. */
public final class ApiErrorResponse {
    private final boolean success = false;
    private final String error;

    public ApiErrorResponse(String error) { this.error = error; }
    public boolean isSuccess() { return success; }
    public String getError() { return error; }
}
