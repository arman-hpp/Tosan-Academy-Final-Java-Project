package com.tosan.loan.interfaces;

import com.tosan.loan.dtos.LoanDto;
import com.tosan.loan.dtos.LoanPaymentInfoDto;

public interface ILoanCalculator {
    LoanPaymentInfoDto calculate(LoanDto loan);
}
