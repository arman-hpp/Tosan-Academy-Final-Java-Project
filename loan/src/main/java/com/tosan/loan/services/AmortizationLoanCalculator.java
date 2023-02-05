package com.tosan.loan.services;

import com.tosan.loan.dtos.*;
import com.tosan.loan.interfaces.ILoanCalculator;

import java.math.*;
import java.time.*;
import java.util.*;

// adapted from https://github.com/ArtyomPanfutov/loan-amortization-calculator
public class AmortizationLoanCalculator implements ILoanCalculator {
    public LoanPaymentInfoDto calculate(LoanDto loanDto) {
        var overPaidInterestAmount = BigDecimal.ZERO;
        var loanBalance = loanDto.getAmount();
        var term = loanDto.getRefundDuration();
        var monthlyInterestRate = getMonthlyInterestRate(loanDto.getInterestRate());
        var monthlyPaymentAmount = getMonthlyPaymentAmount(loanBalance, monthlyInterestRate, term);
        var paymentDate = loanDto.getFirstPaymentDate();

        // Calculate amortization schedule
        var payments = new ArrayList<InstallmentDto>();
        for (var i = 0; i < term; i++) {
            var additionalPaymentAmount = BigDecimal.ZERO;
            var interestAmount = calculateInterestAmount(loanDto, loanBalance, monthlyInterestRate, paymentDate);

            // If something gets negative for some reason (because of early payments)
            // we stop calculating and correct the amount in the last payment
            if (interestAmount.compareTo(BigDecimal.ZERO) < 0 || loanBalance.compareTo(BigDecimal.ZERO) < 0) {
                var lastPaymentNumber = i - 1;

                if (lastPaymentNumber >= 0) {
                    var lastPayment = payments.get(lastPaymentNumber);
                    payments.set(lastPaymentNumber, new InstallmentDto(lastPayment.getInstallmentNo(),
                            paymentDate, lastPayment.getInterestAmount(), lastPayment.getLoanBalanceAmount(),
                            lastPayment.getLoanBalanceAmount(), lastPayment.getLoanBalanceAmount(), loanDto.getCurrency()));
                }

                break;
            }

            overPaidInterestAmount = overPaidInterestAmount.add(interestAmount);

            var principalAmount = (i + 1 == loanDto.getRefundDuration())
                    ? loanBalance
                    : (monthlyPaymentAmount.subtract(interestAmount))
                        .add(additionalPaymentAmount)
                        .setScale(2, RoundingMode.HALF_UP);

            var paymentAmount = interestAmount.add(principalAmount);

            payments.add(new InstallmentDto(i, paymentDate, interestAmount, principalAmount,
                    paymentAmount, loanBalance, loanDto.getCurrency()));

            loanBalance = loanBalance.subtract(principalAmount);

            if (loanDto.getFirstPaymentDate() != null && paymentDate != null) {
                paymentDate = getNextMonthPaymentDate(loanDto.getFirstPaymentDate(), paymentDate);
            }
        }

        return new LoanPaymentInfoDto(monthlyPaymentAmount, overPaidInterestAmount, Collections.unmodifiableList(payments));
    }

    private BigDecimal getMonthlyInterestRate(BigDecimal rate) {
        return rate
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 15, RoundingMode.HALF_UP);
    }

    private LocalDate getNextMonthPaymentDate(LocalDate firstPaymentDate, LocalDate paymentDate) {
        var nextMonth = paymentDate.plusMonths(1);

        try {
            paymentDate = nextMonth.withDayOfMonth(firstPaymentDate.getDayOfMonth());
        } catch (DateTimeException e) {
            // Cannot construct next payment date with the requested day of month.
            // The last month day will be used instead.
            paymentDate = nextMonth.withDayOfMonth(nextMonth.lengthOfMonth());
        }

        return paymentDate;
    }

    private BigDecimal getMonthlyPaymentAmount(BigDecimal amount, BigDecimal rate, Integer term) {
        return getInterestAmountByBalanceAndMonthlyInterestRate(
                amount, (rate
                        .multiply(BigDecimal.ONE.add(rate).pow(term)))
                        .divide((BigDecimal.ONE.add(rate).pow(term).subtract(BigDecimal.ONE)), 15, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal getInterestAmountByBalanceRateAndDays(BigDecimal currentLoanBalance, BigDecimal annualInterestRate, int daysInMonth, int daysInYear) {
        return currentLoanBalance
                .multiply((annualInterestRate.multiply(BigDecimal.valueOf(daysInMonth)))
                        .divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(daysInYear)), 15, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getInterestAmountByBalanceAndMonthlyInterestRate(BigDecimal currentLoanBalance, BigDecimal monthlyInterestRate) {
        return currentLoanBalance
                .multiply(monthlyInterestRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateInterestAmount(LoanDto loanDto, BigDecimal currentLoanBalance, BigDecimal monthlyInterestRate, LocalDate paymentDate) {
        return paymentDate == null
                ? getInterestAmountByBalanceAndMonthlyInterestRate(currentLoanBalance, monthlyInterestRate)
                : getInterestAmountByBalanceRateAndDays(
                currentLoanBalance, loanDto.getInterestRate(),
                paymentDate.minusMonths(1).lengthOfMonth(),
                paymentDate.minusMonths(1).lengthOfYear()
        );
    }
}
