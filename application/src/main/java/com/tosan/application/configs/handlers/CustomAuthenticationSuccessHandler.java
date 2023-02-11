package com.tosan.application.configs.handlers;

import com.tosan.core_banking.services.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthenticationService _authenticationService;

    public CustomAuthenticationSuccessHandler(AuthenticationService authenticationService) {
        _authenticationService = authenticationService;
    }

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) throws IOException, ServletException {

        _authenticationService.logSuccessfulLogin(request.getParameter("username"));

        var redirectURL = "/home/index";
        super.setDefaultTargetUrl(redirectURL);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
