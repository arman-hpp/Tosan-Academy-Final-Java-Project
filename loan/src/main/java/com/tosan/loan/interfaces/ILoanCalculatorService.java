package com.tosan.loan.interfaces;

import com.tosan.loan.dtos.*;

public interface ILoanCalculatorService {
    LoanPaymentInfoDto calculate(LoanDto loan);
}
