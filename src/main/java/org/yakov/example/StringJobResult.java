package org.yakov.example;

import org.yakov.jobs.Job;
import org.yakov.jobs.JobResult;

import java.util.Objects;
import java.util.Optional;

public class StringJobResult implements JobResult<Long, String> {
    private final Long id;
    private final Job<?, ?> job;
    private boolean isComplete;
    private String result;
    private Error error;

    public StringJobResult(Long id, Job<?, ?> job) {
        this.id = id;
        this.job = job;
        this.isComplete = false;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Job<?, ?> getJob() {
        return job;
    }

    @Override
    public Optional<String> getResult() {
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Error> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void populateResult(String result) {
        assertNotComplete();
        Objects.requireNonNull(result);
        assertNoResult();
        this.result = result;
        this.isComplete = true;
    }

    @Override
    public void populateError(Error error) {
        Objects.requireNonNull(error);
        assertNoResult();
        assertNoError();
        this.error = error;
        this.isComplete = true;
    }


    private void assertNotComplete() {
        if (isComplete) {
            throw new IllegalArgumentException("Cannot populate result on completed job");
        }
    }

    private void assertNoResult() {
        if (this.result != null) {
            throw new IllegalStateException("Result already exists");
        }
    }

    private void assertNoError() {
        if (this.error != null) {
            throw new IllegalStateException("Error already exists");
        }
    }
}
