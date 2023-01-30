package com.tosan.application.configs;

import com.tosan.core_banking.interfaces.ITraceNoGeneratorService;
import com.tosan.core_banking.services.RandomTraceNoGeneratorService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ITraceNoGeneratorService traceNoGeneratorService() {
        return new RandomTraceNoGeneratorService();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
