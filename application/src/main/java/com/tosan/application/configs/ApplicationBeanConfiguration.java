package com.tosan.application.configs;

import com.tosan.application.extensions.exporters.ExporterFactory;
import com.tosan.application.extensions.exporters.IExporterFactory;
import com.tosan.core_banking.interfaces.ITraceNoGenerator;
import com.tosan.core_banking.services.RandomTraceNoGenerator;
import com.tosan.loan.interfaces.ILoanCalculator;
import com.tosan.loan.interfaces.ILoanValidator;
import com.tosan.loan.services.AmortizationLoanCalculator;
import com.tosan.loan.services.DefaultLoanValidator;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }

    @Bean
    public ITraceNoGenerator traceNoGenerator() {
        return new RandomTraceNoGenerator();
    }

    @Bean
    public ILoanCalculator loanCalculator() {
        return new AmortizationLoanCalculator();
    }

    @Bean
    public ILoanValidator loanValidator() {
        return new DefaultLoanValidator();
    }

    @Bean
    public IExporterFactory exporterFactory() {
        return new ExporterFactory();
    }
}
