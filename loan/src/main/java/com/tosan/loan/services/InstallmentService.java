package com.tosan.loan.services;

import com.tosan.loan.dtos.InstallmentDto;
import com.tosan.repository.InstallmentRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InstallmentService {
    private final InstallmentRepository _installmentRepository;
    private final ModelMapper _modelMapper;

    public InstallmentService(InstallmentRepository installmentRepository, ModelMapper modelMapper) {
        _installmentRepository = installmentRepository;
        _modelMapper = modelMapper;
    }

    public List<InstallmentDto> loadInstallments(Long loanId) {
        var installments = _installmentRepository.findByLoanIdOrderByInstallmentNo(loanId);
        var installmentDtoList = new ArrayList<InstallmentDto>();
        for(var installment : installments) {
            installmentDtoList.add(_modelMapper.map(installment, InstallmentDto.class));
        }

        return installmentDtoList;
    }

    public List<InstallmentDto> loadPaidInstallments(Long loanId) {
        var installments = _installmentRepository.findByLoanIdAndPaidTrueOrderByInstallmentNo(loanId);
        var installmentDtoList = new ArrayList<InstallmentDto>();
        for(var installment : installments) {
            installmentDtoList.add(_modelMapper.map(installment, InstallmentDto.class));
        }

        return installmentDtoList;
    }

    public List<InstallmentDto> loadNotPaidInstallments(Long loanId) {
        var installments = _installmentRepository.findByLoanIdAndPaidFalseOrderByInstallmentNo(loanId);
        var installmentDtoList = new ArrayList<InstallmentDto>();
        for(var installment : installments) {
            installmentDtoList.add(_modelMapper.map(installment, InstallmentDto.class));
        }

        return installmentDtoList;
    }
}