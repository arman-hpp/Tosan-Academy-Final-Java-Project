package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.*;
import com.tosan.loan.interfaces.ILoanConditionsValidatorService;
import com.tosan.model.LoanCondition;
import com.tosan.repository.LoanConditionsRepository;

import org.springframework.stereotype.Service;

@Service
public class LoanConditionsValidatorService implements ILoanConditionsValidatorService {
    private final LoanConditionsRepository _loanConditionsRepository;

    public LoanConditionsValidatorService(LoanConditionsRepository loanConditionsRepository) {
        _loanConditionsRepository = loanConditionsRepository;
    }

    public void validate(LoanDto loanDto) {
        var loanConditions = _loanConditionsRepository
                .findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(loanDto.getCurrency()).orElse(null);
        if(loanConditions == null)
            throw new BusinessException("can not find the active loan conditions");

        if (loanDto.getRefundDuration() < loanConditions.getMinRefundDuration())
            throw new BusinessException("the loan refund duration is less than minimum refund duration");

        if (loanDto.getRefundDuration() > loanConditions.getMaxRefundDuration())
            throw new BusinessException("the loan refund duration is greater than maximum refund duration");

        if (loanDto.getAmount().compareTo(loanConditions.getMinAmount()) < 0)
            throw new BusinessException("the loan amount is less than minimum amount");

        if (loanDto.getAmount().compareTo(loanConditions.getMaxAmount()) > 0)
            throw new BusinessException("the loan amount is greater than minimum amount");
    }


    public void validate(LoanCondition loanConditions, LoanDto loanDto) {

        if (loanDto.getRefundDuration() < loanConditions.getMinRefundDuration())
            throw new BusinessException("the loan refund duration is less than minimum refund duration");

        if (loanDto.getRefundDuration() > loanConditions.getMaxRefundDuration())
            throw new BusinessException("the loan refund duration is greater than maximum refund duration");

        if (loanDto.getAmount().compareTo(loanConditions.getMinAmount()) < 0)
            throw new BusinessException("the loan amount is less than minimum amount");

        if (loanDto.getAmount().compareTo(loanConditions.getMaxAmount()) > 0)
            throw new BusinessException("the loan amount is greater than minimum amount");
    }
}
