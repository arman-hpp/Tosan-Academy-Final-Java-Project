package com.tosan.application.extensions.springframework;

public class RequestParamsBuilder {
    public static String build(Object... params) {
        var result = new StringBuilder();
        result.append("?");
        for (int i = 0; i < params.length; i = i + 2) {
            result.append(params[i].toString()).append("=").append(params[i + 1].toString());
            if(i != params.length - 2)
                result.append("&");
        }

        return result.toString();
    }
}
