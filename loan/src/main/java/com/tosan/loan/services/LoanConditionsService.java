package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.*;
import com.tosan.loan.interfaces.ILoanConditionsService;
import com.tosan.model.LoanCondition;
import com.tosan.repository.LoanConditionsRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LoanConditionsService implements ILoanConditionsService {
    private final LoanConditionsRepository _loanConditionsRepository;
    private final ModelMapper _modelMapper;

    public LoanConditionsService(LoanConditionsRepository loanConditionsRepository,
                                 ModelMapper modelMapper) {
        _loanConditionsRepository = loanConditionsRepository;
        _modelMapper = modelMapper;
    }

    public LoanConditionsDto loadLoanConditions() {
        var loanConfigs = _loanConditionsRepository
                .findTop1ByExpireDateIsNullOrderByStartDateDesc().orElse(null);
        if(loanConfigs == null)
            throw new BusinessException("can not find the active loan configuration");

        return _modelMapper.map(loanConfigs, LoanConditionsDto.class);
    }

    @Transactional
    public void editLoanConditions(LoanConditionsDto inputDto) {
        var currentLoanConfigs = _loanConditionsRepository
                .findTop1ByExpireDateIsNullOrderByStartDateDesc().orElse(null);
        if(currentLoanConfigs == null)
            throw new BusinessException("can not find the active loan configuration");

        currentLoanConfigs.setExpireDate(LocalDateTime.now());

        var newLoanConfigs = _modelMapper.map(inputDto, LoanCondition.class);
        newLoanConfigs.setStartDate(LocalDateTime.now());
        newLoanConfigs.setExpireDate(null);

        _loanConditionsRepository.save(newLoanConfigs);
        _loanConditionsRepository.save(currentLoanConfigs);
    }
}
