package com.tosan.loan.services;

import com.tosan.loan.interfaces.IInstallmentService;
import com.tosan.repository.InstallmentRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class InstallmentService implements IInstallmentService {
    private final InstallmentRepository _installmentRepository;
    private final ModelMapper _modelMapper;

    public InstallmentService(InstallmentRepository installmentRepository, ModelMapper modelMapper) {
        _installmentRepository = installmentRepository;
        _modelMapper = modelMapper;
    }

    public void loadInstallments() {

    }

    public void payInstallments() {

    }

    public void addInstallments() {

    }
}