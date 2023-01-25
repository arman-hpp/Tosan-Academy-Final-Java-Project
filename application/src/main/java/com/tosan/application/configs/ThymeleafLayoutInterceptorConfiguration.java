package com.tosan.application.configs;

import com.tosan.application.extensions.thymeleaf.ThymeleafLayoutInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class ThymeleafLayoutInterceptorConfiguration implements WebMvcConfigurer  {
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new ThymeleafLayoutInterceptor());
    }
}
