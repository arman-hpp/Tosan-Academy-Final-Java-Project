package com.tosan.core_banking.services;

import com.tosan.core_banking.interfaces.ITraceNoGenerator;

import java.util.UUID;

public class RandomTraceNoGenerator implements ITraceNoGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
