package org.ashwin.example2;

import org.ashwin.service.annotations.IdGenerator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
    @Override
    public Object generate() {
        return UUID.randomUUID().toString();
    }
}
