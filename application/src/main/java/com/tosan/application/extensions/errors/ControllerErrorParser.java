package com.tosan.application.extensions.errors;

import com.tosan.model.DomainException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public final class ControllerErrorParser {
    public static String getError(Exception exception) {
        String errorMessage;
        if(exception instanceof DomainException) {
            errorMessage = ((DomainException) exception).getResourceKey();
        }
        else {
            errorMessage = ControllerDefaultErrors.UnhandledError.getDisplayMessage();
        }

        return errorMessage;
    }

    public static String getError(BindingResult bindingResult) {
        if(bindingResult == null) {
            return ControllerDefaultErrors.UnhandledError.getDisplayMessage();
        }

        return getError(ControllerDefaultErrors.InvalidInputParameters);
    }

    public static String getError(ControllerDefaultErrors defaultErrors) {
        return defaultErrors.getDisplayMessage();
    }

    public static void setError(BindingResult bindingResult, Exception ex) {
        var error = new ObjectError("globalError", getError(ex));
        bindingResult.addError(error);
    }
}