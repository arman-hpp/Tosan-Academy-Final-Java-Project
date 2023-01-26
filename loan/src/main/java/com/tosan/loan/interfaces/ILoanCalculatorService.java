package com.tosan.loan.interfaces;

import com.tosan.loan.dtos.*;

public interface ILoanCalculatorService {
    LoanAmortizationDto calculate(LoanDto loan);
}
