package com.tosan.application.controllers;

import com.tosan.utils.ConvertorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExceptionController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            var statusCode = ConvertorUtils.tryParseInt(status.toString(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "/errors/error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "/errors/error-500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "/errors/error-403";
            }
        }

        return "/errors/error-500";
    }
}