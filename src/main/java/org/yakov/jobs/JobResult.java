package org.yakov.jobs;

import java.util.Optional;

public interface JobResult<I, R> {
    I getId();

    Job<?, ?> getJob();

    void populateResult(R result);

    void populateError(Error error);

    Optional<R> getResult();

    Optional<Error> getError();

    boolean isComplete();

    enum Error {
        PROCESSOR_SHUTDOWN,
        MALFORMED_JOB,
        SUBMIT_FAILED,
        INTERNAL_ERROR,
    }
}