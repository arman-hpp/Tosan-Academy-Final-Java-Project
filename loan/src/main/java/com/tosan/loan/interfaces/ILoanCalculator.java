package com.tosan.loan.interfaces;

import com.tosan.loan.dtos.*;

public interface ILoanCalculator {
    LoanPaymentInfoDto calculate(LoanDto loan);
}
