package com.tosan.core_banking.services;

import com.tosan.core_banking.interfaces.ITraceNoGeneratorService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RandomTraceNoGeneratorService implements ITraceNoGeneratorService {
    @Override
    public String Generate() {
        return UUID.randomUUID().toString();
    }
}
