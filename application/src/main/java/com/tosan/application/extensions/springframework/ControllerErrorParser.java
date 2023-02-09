package com.tosan.application.extensions.springframework;

import com.tosan.exceptions.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public final class ControllerErrorParser {
    public static String getError(Exception exception, Boolean encode) {
        String errorMessage;
        if (exception instanceof BusinessException) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = "unhandled error occurred";
        }

        if(encode) {
            return Encode(errorMessage);
        } else {
            return errorMessage;
        }
    }

    public static String getError(Exception exception) {
        return getError(exception, true);
    }

    public static String getError(BindingResult bindingResult) {
        return "Invalid+input+parameters";
    }

    public static String getInvalidArgumentError() {
        return "Invalid+input+parameters";
    }

    public static String getIllegalAccessError() {
        return "illegal+access";
    }


    public static void setError(BindingResult bindingResult, Exception ex) {
        var error = new ObjectError("globalError", getError(ex, false));
        bindingResult.addError(error);
    }

    public static void setPasswordMisMatchedError(BindingResult bindingResult) {
        var error = new ObjectError("globalError", "the password not matched");
        bindingResult.addError(error);
    }

    private static String Encode(String str) {
        return str.replace(' ', '+');
    }
}
