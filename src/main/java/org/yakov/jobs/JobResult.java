package org.yakov.jobs;

import java.util.Optional;

/**
 * The primitive representing a job result. This can be in three possible states:
 * 1. The job has not yet been processed.
 * 2. The job is processed successfully and has a result.
 * 3. The job is processed unsuccessfully and has an error.
 *
 * @param <J> type of the job result's identifier.
 * @param <R> ype of the job result's payload.
 */
public interface JobResult<J, R> {
    J getId();

    Job<?, ?> getJob();

    /**
     * Set the result after successful processing.
     * Should only be set on incomplete jobs.
     *
     * @param result the result of processing.
     */
    void populateResult(R result);

    /**
     * Set the error after unsuccessful processing.
     * Should only be set on incomplete jobs.
     *
     * @param error the error that caused the failure.
     */
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