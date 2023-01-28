package com.tosan.loan.interfaces;

import com.tosan.loan.dtos.*;

public interface ILoanCalculatorService {
    InstallmentsBookDto calculate(LoanDto loan);
}
