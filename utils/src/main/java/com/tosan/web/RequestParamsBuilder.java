package com.tosan.web;

public final class RequestParamsBuilder {
    private final StringBuilder queryString;
    private Boolean isFirstParam = true;

    public RequestParamsBuilder(String baseUrl) {
        queryString = new StringBuilder(baseUrl);
    }

    public RequestParamsBuilder Add(String paramName, Object paramValue) {
        if(isFirstParam) {
            queryString.append("?");
            isFirstParam = false;
        } else {
            queryString.append("&");
        }

        queryString.append(paramName).append("=").append(paramValue);

        return this;
    }

    @Override
    public String toString() {
        return queryString.toString();
    }
}
