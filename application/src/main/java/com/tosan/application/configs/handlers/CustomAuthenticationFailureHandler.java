package com.tosan.application.configs.handlers;

import com.tosan.core_banking.services.AuthenticationService;
import com.tosan.web.RequestParamsBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final AuthenticationService _authenticationService;

    public CustomAuthenticationFailureHandler(AuthenticationService authenticationService) {
        _authenticationService = authenticationService;
    }

    @Override
    public void onAuthenticationFailure(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception) throws IOException, ServletException {

        _authenticationService.logFailedLogin(request.getParameter("username"));

        var error = "error.public.unexpected";
        if (exception instanceof UsernameNotFoundException) {
            error = "error.auth.notFound";
        } else if (exception instanceof DisabledException) {
            error = "error.auth.disabled";
        } else if (exception instanceof BadCredentialsException) {
            error = "error.auth.credentials.invalid";
        } else if (exception instanceof SessionAuthenticationException) {
            error = "error.auth.session";
        } else if (exception instanceof LockedException) {
            error = "error.auth.locked";
        } else if (exception instanceof AccountExpiredException) {
            error = "error.auth.expired";
        } else if (exception instanceof CredentialsExpiredException) {
            error = "error.auth.credentials.expired";
        }

        var failureUrl = new RequestParamsBuilder("/auth/login")
                .Add("username", request.getParameter("username"))
                .Add("error", error)
                .toString();

        super.setDefaultFailureUrl(failureUrl);
        super.onAuthenticationFailure(request, response, exception);
    }
}
