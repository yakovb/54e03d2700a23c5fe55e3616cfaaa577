package org.yakov.example;

import org.yakov.jobs.Job;

import java.util.UUID;

public class StringJob implements Job<UUID, String> {
    private final UUID id;
    private final String payload;

    public StringJob(UUID id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    public UUID getId() {
        return id;
    }
}
