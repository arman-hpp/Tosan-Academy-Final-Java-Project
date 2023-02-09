package com.tosan.application.extensions.springframework;

public class RequestParamsBuilder {
    private final StringBuilder q;
    private Boolean First = true;

    public RequestParamsBuilder(String baseUrl) {
        q = new StringBuilder(baseUrl);
    }

    public RequestParamsBuilder Add(String paramName, Object paramValue) {
        if(First) {
            q.append("?");
            First = false;
        } else {
            q.append("&");
        }

        q.append(paramName).append("=").append(paramValue);

        return this;
    }

    @Override
    public String toString() {
        return q.toString();
    }
}
