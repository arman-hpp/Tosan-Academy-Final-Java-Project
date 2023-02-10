package com.tosan.loan.services;

import com.tosan.loan.dtos.LoanConditionsDto;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.interfaces.ILoanValidator;
import com.tosan.model.DomainException;

public class DefaultLoanValidator implements ILoanValidator {
    public void validate(LoanConditionsDto loanConditionsDto, LoanDto loanDto) {
        if (loanDto.getRefundDuration() < loanConditionsDto.getMinRefundDuration())
            throw new DomainException("error.loan.conditions.minRefundDuration.invalid");

        if (loanDto.getRefundDuration() > loanConditionsDto.getMaxRefundDuration())
            throw new DomainException("error.loan.conditions.maxRefundDuration.invalid");

        if (loanDto.getAmount().compareTo(loanConditionsDto.getMinAmount()) < 0)
            throw new DomainException("error.loan.conditions.minAmount.invalid");

        if (loanDto.getAmount().compareTo(loanConditionsDto.getMaxAmount()) > 0)
            throw new DomainException("error.loan.conditions.maxAmount.invalid");
    }
}
