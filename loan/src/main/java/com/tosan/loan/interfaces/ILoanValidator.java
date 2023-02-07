package com.tosan.loan.interfaces;

import com.tosan.loan.dtos.LoanConditionsDto;
import com.tosan.loan.dtos.LoanDto;

public interface ILoanValidator {
    void validate(LoanConditionsDto loanConditionsDto, LoanDto loanDto);
}
