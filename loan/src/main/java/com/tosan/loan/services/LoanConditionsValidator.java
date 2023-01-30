package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.*;
import com.tosan.loan.interfaces.ILoanConditionsValidator;
import com.tosan.model.LoanCondition;
import com.tosan.repository.LoanConfigurationRepository;

import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class LoanConditionsValidator implements ILoanConditionsValidator {
    private final LoanConfigurationRepository _loanConfigurationRepository;
    private final ModelMapper _modelMapper;

    public LoanConditionsValidator(LoanConfigurationRepository loanConfigurationRepository, ModelMapper modelMapper) {
        _loanConfigurationRepository = loanConfigurationRepository;
        _modelMapper = modelMapper;
    }

    public void validate(LoanDto inputDto) {
        var loanConfigs = loadLoanConfigs();

        if(inputDto.getRefundDuration() < loanConfigs.getMinRefundDuration())
            throw new BusinessException("the loan refund duration is less than minimum refund duration");

        if(inputDto.getRefundDuration() > loanConfigs.getMaxRefundDuration())
            throw new BusinessException("the loan refund duration is greater than maximum refund duration");

        if(inputDto.getAmount().compareTo(loanConfigs.getMinAmount()) < 0)
            throw new BusinessException("the loan amount is less than minimum amount");

        if(inputDto.getAmount().compareTo(loanConfigs.getMaxAmount()) > 0)
            throw new BusinessException("the loan amount is greater than minimum amount");
    }

    public LoanConfigurationDto loadLoanConfigs() {
        var loanConfigs = _loanConfigurationRepository
                .findTop1ByExpireDateIsNullOrderByStartDateDesc().orElse(null);
        if(loanConfigs == null)
            throw new BusinessException("can not find the active loan configuration");

        return _modelMapper.map(loanConfigs, LoanConfigurationDto.class);
    }

    @Transactional
    public void editLoanConfigs(LoanConfigurationDto inputDto) {
        var currentLoanConfigs = _loanConfigurationRepository
                .findTop1ByExpireDateIsNullOrderByStartDateDesc().orElse(null);
        if(currentLoanConfigs == null)
            throw new BusinessException("can not find the active loan configuration");

        currentLoanConfigs.setExpireDate(LocalDateTime.now());

        var newLoanConfigs = _modelMapper.map(inputDto, LoanCondition.class);
        newLoanConfigs.setStartDate(LocalDateTime.now());
        newLoanConfigs.setExpireDate(null);

        _loanConfigurationRepository.save(newLoanConfigs);
        _loanConfigurationRepository.save(currentLoanConfigs);
    }
}
