package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.*;
import com.tosan.loan.interfaces.ILoanConditionsService;
import com.tosan.model.*;
import com.tosan.repository.LoanConditionsRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanConditionsService implements ILoanConditionsService {
    private final LoanConditionsRepository _loanConditionsRepository;
    private final ModelMapper _modelMapper;

    public LoanConditionsService(LoanConditionsRepository loanConditionsRepository,
                                 ModelMapper modelMapper) {
        _loanConditionsRepository = loanConditionsRepository;
        _modelMapper = modelMapper;
    }

    public List<LoanConditionsDto> loadLoanConditions() {
        var loanConditions = _loanConditionsRepository.findAll();
        var outputDto = new ArrayList<LoanConditionsDto>();
        for(var loanCondition : loanConditions) {
            outputDto.add(_modelMapper.map(loanCondition, LoanConditionsDto.class));
        }

        return outputDto;
    }

    public LoanConditionsDto loadLoanCondition(Currencies currency) {
        var loanConditions = _loanConditionsRepository
                .findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(currency).orElse(null);
        if(loanConditions == null)
            throw new BusinessException("can not find the active loan configuration");

        return _modelMapper.map(loanConditions, LoanConditionsDto.class);
    }

    @Transactional
    public void editLoanConditions(LoanConditionsDto inputDto) {
        var currentLoanConfigs = _loanConditionsRepository
                .findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(inputDto.getCurrency()).orElse(null);
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
