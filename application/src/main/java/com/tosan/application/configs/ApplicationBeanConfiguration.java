package com.tosan.application.configs;

import com.tosan.core_banking.interfaces.*;
import com.tosan.core_banking.services.*;
import com.tosan.loan.interfaces.*;
import com.tosan.loan.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ITraceNoGeneratorService traceNoGeneratorService() {
        return new RandomTraceNoGeneratorService();
    }

    @Bean
    public ILoanCalculatorService loanCalculatorService() {
        return new AmortizationCalculatorService();
    }
}
