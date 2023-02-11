package com.tosan.model;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@SuppressWarnings("unused")
public class DomainException extends RuntimeException {
    private final MessageSource _messageSource;
    private final String resourceKey;
    private final Object[] args;

    public DomainException(/*@PropertyKey(resourceBundle = "")*/ String resourceKey, Object... args) {
        super(resourceKey);
        this.resourceKey = resourceKey;
        this.args = args;

        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        //messageSource.setCacheMillis(500);
        _messageSource = messageSource;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    @Override
    public String getMessage() {
        return _messageSource.getMessage(resourceKey, args, Locale.getDefault());
    }

    public String getMessage(Locale locale) {
        return _messageSource.getMessage(resourceKey, args, locale);
    }
}
