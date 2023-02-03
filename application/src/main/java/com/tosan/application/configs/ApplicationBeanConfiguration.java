package com.tosan.application.configs;

import com.tosan.core_banking.interfaces.*;
import com.tosan.core_banking.services.*;
import com.tosan.loan.interfaces.*;
import com.tosan.loan.services.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.*;

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
}
