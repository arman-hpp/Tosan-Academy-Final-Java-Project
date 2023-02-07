package com.tosan.loan.services;

import com.tosan.exceptions.BusinessException;
import com.tosan.loan.dtos.LoanConditionsDto;
import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.interfaces.ILoanValidator;

public class DefaultLoanValidator implements ILoanValidator {
    public void validate(LoanConditionsDto loanConditionsDto, LoanDto loanDto) {
        if (loanDto.getRefundDuration() < loanConditionsDto.getMinRefundDuration())
            throw new BusinessException("the loan refund duration is less than minimum refund duration");

        if (loanDto.getRefundDuration() > loanConditionsDto.getMaxRefundDuration())
            throw new BusinessException("the loan refund duration is greater than maximum refund duration");

        if (loanDto.getAmount().compareTo(loanConditionsDto.getMinAmount()) < 0)
            throw new BusinessException("the loan amount is less than minimum amount");

        if (loanDto.getAmount().compareTo(loanConditionsDto.getMaxAmount()) > 0)
            throw new BusinessException("the loan amount is greater than maximum amount");
    }
}
