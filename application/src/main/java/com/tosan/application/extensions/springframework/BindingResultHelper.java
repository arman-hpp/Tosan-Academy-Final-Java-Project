package com.tosan.application.extensions.springframework;

import com.tosan.exceptions.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public final class BindingResultHelper {
    public static void addGlobalError(BindingResult bindingResult, String errorMessage) {
        var error = new ObjectError("globalError", errorMessage);
        bindingResult.addError(error);
    }

    public static void addGlobalError(BindingResult bindingResult, Exception exception) {
        addGlobalError(bindingResult, getGlobalError(exception, false));
    }

    public static String getGlobalError(String referUrl, Exception exception) {
        var errorMessage = getGlobalError(exception, true);
        return createUrlError(referUrl, errorMessage);
    }

    private static String getGlobalError(Exception exception, Boolean encoded) {
        String errorMessage;
        if (exception instanceof BusinessException) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = "unhandled error occurred";
        }

        if(encoded) {
            return Encode(errorMessage);
        } else {
            return errorMessage;
        }
    }

    public static String getInputValidationError(String returnUrl) {
        var errorMessage = getInputValidationError(true);
        return createUrlError(returnUrl, errorMessage);
    }

    private static String getInputValidationError(Boolean encoded) {
        var errorMessage = "Invalid input parameters";
        if(encoded){
            return Encode(errorMessage);
        } else {
            return errorMessage;
        }
    }

    public static String createUrlError(String referUrl, String errorMessage) {
        return referUrl + "?error=" + errorMessage;
    }

    private static String Encode(String str) {
        return str.replace(' ', '+');
    }
}
